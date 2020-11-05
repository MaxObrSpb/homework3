package ru.digitalhabbits.homework3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.*;
import ru.digitalhabbits.homework3.web.ConflictException;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl
        implements DepartmentService {
    private final DepartmentDao departmentDao;

    private final PersonService personService;

    @Nonnull
    @Transactional
    @Override
    public List<DepartmentShortResponse> findAllDepartments() {
        List<DepartmentShortResponse> departmentsShortResponse =
                departmentDao.findAll().stream().map(mapDepartmentToDepartmentShortResponse).collect(Collectors.toList());
        return departmentsShortResponse;
    }

    @Nonnull
    @Transactional
    @Override
    public DepartmentResponse getDepartment(@Nonnull Integer id) {
        Department department = departmentDao.findById(id);
        if (department != null) {
            List<Person> people = department.getPeople();
            List<PersonInfo> peopleInfo = people.stream().map(mapPersonToPersonInfo).collect(Collectors.toList());
            DepartmentResponse departmentResponse = new DepartmentResponse();
            departmentResponse.setId(id);
            departmentResponse.setName(department.getName());
            departmentResponse.setClosed(department.isClosed());
            departmentResponse.setPersons(peopleInfo);
            return departmentResponse;
        } else {
            throw new EntityNotFoundException("Department with id '" + id + "' is not found");
        }

    }

    @Nonnull
    @Transactional
    @Override
    public Integer createDepartment(@Nonnull DepartmentRequest request) {
        String name = request.getName();
        Department department = new Department();
        department.setName(name);
        try {
            department = departmentDao.create(department);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return department.getId();
    }

    @Nonnull
    @Transactional
    @Override
    public DepartmentResponse updateDepartment(@Nonnull Integer id, @Nonnull DepartmentRequest request) {
        Department department = departmentDao.findById(id);
        DepartmentResponse departmentResponse = new DepartmentResponse();
        if (department != null) {
            department.setName(request.getName());
            if (department.getPeople() == null) {
                department.setPeople(new ArrayList<>());
            }
            Department updatedDepartment = departmentDao.update(department);

            if (updatedDepartment != null) {
                List<Person> people = updatedDepartment.getPeople();
                List<PersonInfo> peopleInfo = new ArrayList<>();
                if (people != null)
                    peopleInfo = people.stream().map(mapPersonToPersonInfo).collect(Collectors.toList());
                departmentResponse.setId(id);
                departmentResponse.setName(updatedDepartment.getName());
                departmentResponse.setClosed(updatedDepartment.isClosed());
                departmentResponse.setPersons(peopleInfo);
            }
        } else {
            throw new EntityNotFoundException("Department with id '" + id + "' is not found");
        }
        return departmentResponse;
    }

    @Transactional
    @Override
    public void deleteDepartment(@Nonnull Integer id) {
        Department department = departmentDao.findById(id);
        if (department != null) {
            detachPeopleFromDepartment(department);
            departmentDao.delete(id);
        }
    }

    @Transactional
    @Override
    public void addPersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
        PersonResponse personResponse = personService.getPerson(personId);
        Department department = departmentDao.findById(departmentId);
        if (department == null) {
            throw new EntityNotFoundException("Department with id '" + departmentId + "' is not found");
        } else if (department.isClosed()) {
            throw new ConflictException("Department with id '" + departmentId + "' is closed");
        } else if (personResponse.getDepartment() == null || personResponse.getDepartment().getId() == null) {

            List<Person> persons = department.getPeople();
            Person person = new Person();
            person.setLastName(personResponse.getFullName().trim().split(" ")[0]);
            person.setFirstName(personResponse.getFullName().trim().split(" ")[1]);
            if (personResponse.getFullName().trim().split(" ").length > 2) {
                person.setMiddleName(personResponse.getFullName().trim().split(" ")[2]);
            }
            person.setAge(personResponse.getAge());
            person.setId(personId);
            person.setDepartment(department);
            persons.add(person);
            departmentDao.update(department);
        }
    }

    @Transactional
    @Override
    public void removePersonToDepartment(@Nonnull Integer departmentId, @Nonnull Integer personId) {
        Department department = departmentDao.findById(departmentId);
        if (department == null) {
            throw new EntityNotFoundException("Department with id '" + departmentId + "' is not found");
        } else {
            try {
                PersonResponse personResponse = personService.getPerson(personId);
                List<Person> people = department.getPeople();
                for (int i = 0; i < people.size(); i++) {
                    if (people.get(i).getId().equals(personResponse.getId())) {
                        detachPersonFromDepartment(people, i);
                        break;
                    }
                }
            } catch (EntityNotFoundException e){
            }
        }
    }

    @Transactional
    @Override
    public void closeDepartment(@Nonnull Integer id) {
        Department department = departmentDao.findById(id);
        if (department == null) {
            throw new EntityNotFoundException("Department with id '" + id + "' is not found");
        } else {
            detachPeopleFromDepartment(department);
            department.setClosed(true);
            departmentDao.update(department);
        }
    }

    private void detachPeopleFromDepartment(Department department) {
        List<Person> people = department.getPeople();
        for (int i = 0; i < people.size(); i++) {
            detachPersonFromDepartment(people, i);
        }
    }

    private void detachPersonFromDepartment(List<Person> people, int i) {
        Person person = people.get(i);
        PersonRequest personRequest = new PersonRequest();
        personRequest.setLastName(person.getLastName());
        personRequest.setFirstName(person.getFirstName());
        if (person.getMiddleName() != null) {
            personRequest.setMiddleName(person.getMiddleName());
        }
        personRequest.setAge(person.getAge());
        personService.removeFromDepartment(person.getId(), personRequest);
    }

    private final Function<Department, DepartmentShortResponse> mapDepartmentToDepartmentShortResponse = department -> {
        DepartmentShortResponse departmentShortResponse = new DepartmentShortResponse();
        departmentShortResponse.setId(department.getId());
        departmentShortResponse.setName(department.getName());
        return departmentShortResponse;
    };

    private final Function<Person, PersonInfo> mapPersonToPersonInfo = person -> {
        PersonInfo info = new PersonInfo();
        info.setId(person.getId());
        StringBuilder fullName = new StringBuilder(person.getLastName())
                .append(" ")
                .append(person.getFirstName())
                .append(hasMiddleName(person) ? " " : "")
                .append(hasMiddleName(person) ? person.getMiddleName() : "");
        info.setFullName(fullName.toString());
        return info;
    };

    private boolean hasMiddleName(Person person) {
        if (person.getMiddleName() != null) {
            return true;
        } else
            return false;
    }
}

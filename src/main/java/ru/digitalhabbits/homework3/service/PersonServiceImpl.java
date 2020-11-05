package ru.digitalhabbits.homework3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.dao.PersonDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.DepartmentInfo;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PersonServiceImpl
        implements PersonService {
    private final PersonDao personDao;

    @Autowired
    public PersonServiceImpl(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Nonnull
    @Override
    @Transactional
    public List<PersonResponse> findAllPersons() {
        List<Person> people = personDao.findAll();
        List<PersonResponse> response = new ArrayList<>();
        for (Person person : people) {
            PersonResponse personResponse = new PersonResponse();
            fillPersonResponse(personResponse, person, person.getDepartment());

            response.add(personResponse);
        }
        return response;
    }

    @Nonnull
    @Override
    @Transactional
    public PersonResponse getPerson(@Nonnull Integer id) {
        Person person = personDao.findById(id);
        PersonResponse personResponse = new PersonResponse();
        if (person != null) {
            personResponse.setId(person.getId());
            personResponse.setFullName(new StringBuilder(person.getLastName()).append(" ").
                    append(person.getFirstName()).
                    append(" ").
                    append(person.getMiddleName()!= null ? person.getMiddleName() : "").toString());
            personResponse.setAge(person.getAge());

            DepartmentInfo departmentInfo = new DepartmentInfo();
            if (person.getDepartment() != null) {
                departmentInfo.setId(person.getDepartment().getId());
                departmentInfo.setName(person.getDepartment().getName());
            }
            personResponse.setDepartment(departmentInfo);

        } else {
            throw new EntityNotFoundException("Person with id '" + id + "' is not found");
        }
        return personResponse;
    }

    @Nonnull
    @Override
    @Transactional
    public Integer createPerson(@Nonnull PersonRequest request) {
        String firstName = request.getFirstName();
        String middleName = request.getMiddleName();
        String lastName = request.getLastName();
        Integer age = request.getAge();

        Person person = new Person();
        person.setFirstName(firstName);
        person.setMiddleName(middleName);
        person.setLastName(lastName);
        person.setAge(age);
        try {
            person = personDao.create(person);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        return person.getId();
    }

    @Nonnull
    @Override
    @Transactional
    public PersonResponse updatePerson(@Nonnull Integer id, @Nonnull PersonRequest request) {
        Person person = personDao.findById(id);
        PersonResponse personResponse = new PersonResponse();
        if (person != null) {
            String firstName = request.getFirstName();
            String middleName = request.getMiddleName();
            String lastName = request.getLastName();
            Integer age = request.getAge();
            person.setFirstName(firstName);
            person.setMiddleName(middleName);
            person.setLastName(lastName);
            person.setAge(age);

            Person updatedPerson = personDao.update(person);
            fillPersonResponse(personResponse, updatedPerson, person.getDepartment());
        } else {
            throw new EntityNotFoundException("Person with id '" + id + "' is not found");
        }
        return personResponse;
    }

    @Override
    @Transactional
    public void deletePerson(@Nonnull Integer id) {
        Person person = personDao.findById(id);
        if (person != null) {
            personDao.removeFromDepartment(id);
            personDao.delete(id);
        }
    }

    @Nonnull
    @Override
    @Transactional
    public void removeFromDepartment(@Nonnull Integer id, @Nonnull PersonRequest request) {
        Person person = personDao.findById(id);
        if (person != null) {
            personDao.removeFromDepartment(id);
        } else {
            throw new EntityNotFoundException("Person with id '" + id + "' is not found");
        }
    }

    private void fillPersonResponse(PersonResponse personResponse, Person person,  Department department) {
        personResponse.setId(person.getId());
        personResponse.setFullName(new StringBuilder(person.getLastName()).append(" ").
                append(person.getFirstName()).
                append(" ").
                append(person.getMiddleName()!= null ? person.getMiddleName() : "").toString());
        personResponse.setAge(person.getAge());

        if (department != null) {
            DepartmentInfo departmentInfo = new DepartmentInfo();
            departmentInfo.setId(department.getId());
            departmentInfo.setName(department.getName());
            personResponse.setDepartment(departmentInfo);
        }
    }
}

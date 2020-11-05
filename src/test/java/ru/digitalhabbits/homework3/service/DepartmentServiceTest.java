package ru.digitalhabbits.homework3.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.digitalhabbits.homework3.dao.DepartmentDao;
import ru.digitalhabbits.homework3.domain.Department;
import ru.digitalhabbits.homework3.model.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomUtils.nextBoolean;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DepartmentServiceImpl.class)
class DepartmentServiceTest {
    private static final int COUNT = 2;

    @MockBean
    private DepartmentDao departmentDao;

    @MockBean
    private PersonService personService;

    @Autowired
    private DepartmentService departmentService;

    @BeforeEach
    void init() {
        this.departmentDao = mock(DepartmentDao.class);
        this.departmentService = new DepartmentServiceImpl(departmentDao, personService);
    }
    
    @Test
    void findAllDepartments() {
        when(departmentDao.findAll())
                .thenReturn(range(0, COUNT).mapToObj(i -> buildDepartment()).collect(toList()));
        final List<DepartmentShortResponse> people = departmentService.findAllDepartments();
        Assertions.assertEquals(COUNT, people.size());
    }

    @Test
    void getDepartment() {
        when(departmentDao.findById(1))
                .thenReturn(buildDepartment());
        final DepartmentResponse departmentResponse = departmentService.getDepartment(1);
        Assertions.assertNotEquals(null, departmentResponse);
    }

    @Test
    void createDepartment() {
        final DepartmentRequest request = buildCreateDepartmentRequest();
        final Department department = new Department()
                .setId(nextInt())
                .setName(randomAlphabetic(7))
                .setClosed(nextBoolean())
                .setPeople(new ArrayList<>());

        when(departmentDao.create(any(Department.class))).thenReturn(department);

        Integer id = departmentService.createDepartment(request);
        Assertions.assertNotEquals(null, id);
    }

    @Test
    void updateDepartment() {
        int id = nextInt(10, 50);
        final DepartmentRequest request = buildCreateDepartmentRequest();
        final Department department = new Department()
                .setId(id)
                .setName(request.getName())
                .setPeople(new ArrayList<>());
        when(departmentDao.findById(id)).thenReturn(buildDepartment().setId(id).setClosed(false));
        when(departmentDao.update(department)).thenReturn(department);

        DepartmentResponse departmentResponse = departmentService.updateDepartment(department.getId(), request);
        Assertions.assertEquals(department.getId(), departmentResponse.getId());
    }

    @Test
    void deleteDepartment() {
        int id = nextInt(10, 50);
        final DepartmentService mockedDepartmentService = mock(DepartmentService.class);
        final Department department = buildDepartment().setId(id);
        when(departmentDao.findById(any(Integer.class))).thenReturn(department);
        when(departmentDao.delete(any(Integer.class))).thenReturn(department);
        departmentService.deleteDepartment(department.getId());
        mockedDepartmentService.deleteDepartment(department.getId());
        verify(departmentDao, times(1)).delete(department.getId());
        verify(mockedDepartmentService, times(1)).deleteDepartment(department.getId());
    }

    @Test
    void addPersonToDepartment() {
        int departmentId = nextInt(10, 50);
        int personId = nextInt(10, 50);
        final Department department = new Department()
                .setId(departmentId)
                .setName(randomAlphabetic(7))
                .setClosed(false)
                .setPeople(new ArrayList<>());
        PersonResponse personResponse = new PersonResponse().setId(personId).setFullName(randomAlphabetic(7) + " " + randomAlphabetic(7) + " " + randomAlphabetic(7)).setAge(nextInt(10, 50));
        when(personService.getPerson(any(Integer.class))).thenReturn(personResponse);
        when(departmentDao.findById(any(Integer.class))).thenReturn(department);
        when(departmentDao.update(any(Department.class))).thenReturn(department);
        departmentService.addPersonToDepartment(department.getId(), personResponse.getId());
        verify(personService, times(1)).getPerson(personResponse.getId());
        verify(departmentDao, times(1)).findById(department.getId());
        verify(departmentDao, times(1)).update(department);
    }

    @Test
    void removePersonToDepartment() {
        int departmentId = nextInt(10, 50);
        int personId = nextInt(10, 50);
        final Department department = new Department()
                .setId(departmentId)
                .setName(randomAlphabetic(7))
                .setClosed(false)
                .setPeople(new ArrayList<>());
        PersonResponse personResponse = new PersonResponse().setId(personId).setFullName(randomAlphabetic(7) + " " + randomAlphabetic(7) + " " + randomAlphabetic(7)).setAge(nextInt(10, 50));
        when(departmentDao.findById(any(Integer.class))).thenReturn(department);
        when(personService.getPerson(any(Integer.class))).thenReturn(personResponse);
        departmentService.removePersonToDepartment(department.getId(), personResponse.getId());
        verify(departmentDao, times(1)).findById(department.getId());
        verify(personService, times(1)).getPerson(personResponse.getId());
    }

    @Test
    void closeDepartment() {
        int departmentId = nextInt(10, 50);
        final Department department = new Department()
                .setId(departmentId)
                .setName(randomAlphabetic(7))
                .setClosed(false)
                .setPeople(new ArrayList<>());
        when(departmentDao.findById(any(Integer.class))).thenReturn(department);
        when(departmentDao.update(any(Department.class))).thenReturn(department);
        departmentService.closeDepartment(department.getId());
        verify(departmentDao, times(1)).findById(department.getId());
        verify(departmentDao, times(1)).update(department);
    }

    private Department buildDepartment() {
        return new Department()
                .setName(randomAlphabetic(7))
                .setClosed(nextBoolean())
                .setPeople(new ArrayList<>());
    }

    public static DepartmentRequest buildCreateDepartmentRequest() {
        return new DepartmentRequest()
                .setName(randomAlphabetic(7));
    }
}
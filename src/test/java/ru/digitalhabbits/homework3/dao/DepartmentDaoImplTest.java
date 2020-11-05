package ru.digitalhabbits.homework3.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.digitalhabbits.homework3.domain.Department;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class DepartmentDaoImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentDao departmentDao;

    private final int[] ids = new int[2];
    private static List<Department> departments;

    @BeforeEach
    void init() {
        departments = generateDepartments(2);
        Department department1 = entityManager.persist(departments.get(0));
        Department department2 = entityManager.persist(departments.get(1));
        entityManager.flush();

        ids[0] = department1.getId();
        ids[1] = department2.getId();
    }

    private List<Department> generateDepartments(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Department()
                        .setName(randomAlphabetic(20)))
                .collect(Collectors.toList());
    }

    @Test
    void findById() {
        final Department department1 = departmentDao.findById(ids[0]);
        final Department department2 = departmentDao.findById(ids[1]);

        Assertions.assertEquals(department1, departments.get(0));
        Assertions.assertEquals(department2, departments.get(1));
    }

    @Test
    void findAll() {
        final List<Department> departmentsList = departmentDao.findAll();
        Assertions.assertEquals(2, departmentsList.size());
    }

    @Test
    void update() {
        final Department originalDepartment = entityManager.find(Department.class, ids[0]);
        final String name = originalDepartment.getName();

        final Department department = new Department();
        department.setId(ids[0]);
        department.setName("New Name");
        department.setClosed(originalDepartment.isClosed());
        Department updatedDepartment = departmentDao.update(department);

        Assertions.assertNotEquals(null, updatedDepartment);
        Assertions.assertEquals(originalDepartment.getId(), updatedDepartment.getId());
        Assertions.assertNotEquals(name, updatedDepartment.getName());
    }

    @Test
    void delete() {
        final Department deletedPerson = departmentDao.delete(ids[0]);
        Assertions.assertNotEquals(null, deletedPerson);
    }
}
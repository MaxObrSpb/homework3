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
import ru.digitalhabbits.homework3.domain.Person;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ExtendWith(SpringExtension.class)
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class PersonDaoTest {

    @Autowired
    private TestEntityManager entityManager;

    private final int[] ids = new int[2];
    private static List<Person> people;

    @Autowired
    private PersonDao personDao;

    @BeforeEach
    void init() {
        people = generatePeople(2);
        Person person1 = entityManager.persist(people.get(0));
        Person person2 = entityManager.persist(people.get(1));
        entityManager.flush();

        ids[0] = person1.getId();
        ids[1] = person2.getId();
    }

    @Test
    void findById() {
        final Person person1 = personDao.findById(ids[0]);
        final Person person2 = personDao.findById(ids[1]);

        Assertions.assertEquals(person1, people.get(0));
        Assertions.assertEquals(person2, people.get(1));
    }

    @Test
    void findAll() {
        final List<Person> personList = personDao.findAll();
        Assertions.assertEquals(2, personList.size());
    }

    @Test
    void update() {
        final Person originalPerson = entityManager.find(Person.class, ids[0]);
        final String lastName = originalPerson.getLastName();

        final Person person = new Person();
        person.setId(ids[0]);
        person.setFirstName(originalPerson.getFirstName());
        person.setLastName("AbcdefghijkZ");
        person.setAge(originalPerson.getAge());
        Person updatedPerson = personDao.update(person);

        Assertions.assertNotEquals(null, updatedPerson);
        Assertions.assertEquals(originalPerson.getId(), updatedPerson.getId());
        Assertions.assertNotEquals(lastName, updatedPerson.getLastName());
    }

    @Test
    void delete() {
        final Person deletedPerson = personDao.delete(ids[0]);
        Assertions.assertNotEquals(null, deletedPerson);
    }

    @Test
    void create() {
        Person createdPerson = personDao.create(buildPerson());
        Assertions.assertNotEquals(null, createdPerson);
    }

    private List<Person> generatePeople(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Person()
                        .setFirstName(randomAlphabetic(7))
                        .setMiddleName(randomAlphabetic(7))
                        .setLastName(randomAlphabetic(7))
                        .setAge(nextInt(10, 50)))
                .collect(Collectors.toList());
    }

    private Person buildPerson() {
        return new Person()
                .setFirstName(randomAlphabetic(7))
                .setMiddleName(randomAlphabetic(7))
                .setLastName(randomAlphabetic(7))
                .setAge(nextInt(10, 50));
    }
}
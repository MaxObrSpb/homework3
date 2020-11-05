package ru.digitalhabbits.homework3.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.digitalhabbits.homework3.dao.PersonDao;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import ru.digitalhabbits.homework3.domain.Person;
import ru.digitalhabbits.homework3.model.PersonRequest;
import ru.digitalhabbits.homework3.model.PersonResponse;

import java.util.List;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PersonServiceImpl.class)
class PersonServiceTest {
    private static final int COUNT = 2;

    @MockBean
    private PersonDao personDao;

    @Autowired
    private PersonService personService;

    @BeforeEach
    void init() {
        this.personDao = mock(PersonDao.class);
        this.personService = new PersonServiceImpl(personDao);
    }

    @Test
    void findAllPersons() {
        when(personDao.findAll())
                .thenReturn(range(0, COUNT).mapToObj(i -> buildPerson()).collect(toList()));
        final List<PersonResponse> people = personService.findAllPersons();
        Assertions.assertEquals(COUNT, people.size());
    }

    @Test
    void getPerson() {
        when(personDao.findById(1))
                .thenReturn(buildPerson());
        final PersonResponse personResponse = personService.getPerson(1);
        Assertions.assertNotEquals(null, personResponse);
    }

    @Test
    void createPerson() {
        final PersonRequest request = buildCreatePersonRequest();
        final Person person = new Person()
                .setId(nextInt())
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setLastName(request.getLastName())
                .setAge(request.getAge());

        when(personDao.create(any(Person.class))).thenReturn(person);

        Integer id = personService.createPerson(request);
        Assertions.assertNotEquals(null, id);
    }

    @Test
    void updatePerson() {
        int id = nextInt(10, 50);
        final PersonRequest request = buildCreatePersonRequest();
        final Person person = new Person()
                .setId(id)
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setLastName(request.getLastName())
                .setAge(request.getAge());
        when(personDao.findById(id)).thenReturn(buildPerson().setId(id));
        when(personDao.update(person)).thenReturn(person.setLastName(person.getLastName()));

        PersonResponse personResponse = personService.updatePerson(person.getId(), request);
        Assertions.assertEquals(person.getId(), personResponse.getId());
    }

    @Test
    void deletePerson() {
        int id = nextInt(10, 50);
        when(personDao.findById(id)).thenReturn(buildPerson().setId(id));
        when(personDao.delete(id)).thenReturn(new Person());
        personService.deletePerson(id);
        verify(personDao, times(1)).findById(id);
        verify(personDao, times(1)).delete(id);
    }

    private Person buildPerson() {
        return new Person()
                .setFirstName(randomAlphabetic(7))
                .setMiddleName(randomAlphabetic(7))
                .setLastName(randomAlphabetic(7))
                .setAge(nextInt(10, 50));
    }

    public static PersonRequest buildCreatePersonRequest() {
        return new PersonRequest()
                .setAge(nextInt(10, 50))
                .setFirstName(randomAlphabetic(7))
                .setMiddleName(randomAlphabetic(7))
                .setLastName(randomAlphabetic(7));
    }
}
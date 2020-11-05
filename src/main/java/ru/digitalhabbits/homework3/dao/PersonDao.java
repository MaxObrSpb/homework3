package ru.digitalhabbits.homework3.dao;

import org.springframework.data.repository.NoRepositoryBean;
import ru.digitalhabbits.homework3.domain.Person;

@NoRepositoryBean
public interface PersonDao
        extends CrudOperations<Person, Integer> {

    void removeFromDepartment(Integer id);
}
package ru.digitalhabbits.homework3.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.digitalhabbits.homework3.domain.Person;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class PersonDaoImpl
        implements PersonDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PersonDaoImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Person findById(@Nonnull Integer id) {
        return entityManager.find(Person.class, id);
    }

    @Override
    public List<Person> findAll() {
        Query query = entityManager.createNamedQuery("Person.findPeople");
        return query.getResultList();
    }

    @Override
    public Person create(Person entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public Person update(Person entity) {
        return entityManager.merge(entity);
    }

    @Override
    public Person delete(Integer integer) {
        Person person = findById(integer);
        entityManager.remove(person);
        return person;
    }


    @Override
    public void removeFromDepartment(Integer id) {
        Query query = entityManager.createNamedQuery("Person.removeDepartmentId");
        query.setParameter("pId", id);
        query.executeUpdate();
        entityManager.flush();
    }
}

package ru.digitalhabbits.homework3.dao;

import org.springframework.stereotype.Repository;
import ru.digitalhabbits.homework3.domain.Department;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DepartmentDaoImpl
        implements DepartmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Department findById(@Nonnull Integer integer) {
        Department department = entityManager.find(Department.class, integer);
        return department;
    }

    @Override
    public List<Department> findAll() {
        Query query = entityManager.createNamedQuery("Department.findDepartments");
        return query.getResultList();
    }

    @Override
    public Department create(Department entity) {
        entityManager.persist(entity);
        entityManager.flush();
        return entity;
    }

    @Override
    public Department update(Department entity) {
        return entityManager.merge(entity);
    }


    @Override
    public Department delete(Integer integer) {
        Department department = findById(integer);
        entityManager.remove(department);
        return department;
    }
}

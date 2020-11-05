package ru.digitalhabbits.homework3.domain;

import lombok.Data;
import lombok.experimental.Accessors;


import javax.persistence.*;
import java.util.List;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "department")

@NamedQueries({
        @NamedQuery(name = "Department.findDepartments",
                query = "SELECT d FROM Department d ")
})
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "department_id")
    private Integer id;

    @Column(nullable = false, length = 80, unique = true)
    private String name;

    @Column(nullable = false, columnDefinition = "BOOL NOT NULL DEFAULT FALSE")
    private boolean closed;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.LAZY,
            mappedBy = "department")
//    @OnDelete(action = OnDeleteAction.NO_ACTION)
//    @OneToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Person> people;
}
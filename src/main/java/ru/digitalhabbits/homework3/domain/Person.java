package ru.digitalhabbits.homework3.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "person")

@NamedQueries({
        @NamedQuery(name = "Person.findPeople",
                query = "SELECT p FROM Person p "),
        @NamedQuery(name = "Person.removeDepartmentId",
                query = "UPDATE Person p set p.department = null where p.id=:pId")
})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "person_id")
    private Integer id;

    @Column(nullable = false, length = 80)
    private String firstName;

    @Column(length = 80)
    private String middleName;

    @Column(nullable = false, length = 80)
    private String lastName;

    @Column
    private Integer age;


    @ManyToOne//(cascade = {CascadeType.MERGE},  fetch = FetchType.LAZY)
//    @ManyToOne(cascade = {CascadeType.REFRESH},  fetch = FetchType.EAGER) // точно нет

//    @ManyToOne(cascade = {CascadeType.ALL},  fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;
}

package ru.digitalhabbits.homework3.web;


import javax.persistence.PersistenceException;


public class ConflictException extends PersistenceException {

    public ConflictException(String message) {
        super(message);
    }

}

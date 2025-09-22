package org.example.bankservice.validator;

public interface Validator<T> {
    void validate(T dto);
}

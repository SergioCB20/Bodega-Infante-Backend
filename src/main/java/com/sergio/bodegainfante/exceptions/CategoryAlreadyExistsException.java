package com.sergio.bodegainfante.exceptions;

public class CategoryAlreadyExistsException extends  RuntimeException{
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}

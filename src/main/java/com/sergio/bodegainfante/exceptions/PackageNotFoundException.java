package com.sergio.bodegainfante.exceptions;

public class PackageNotFoundException extends RuntimeException {
    public PackageNotFoundException(String message) {
        super(message);
    }
}

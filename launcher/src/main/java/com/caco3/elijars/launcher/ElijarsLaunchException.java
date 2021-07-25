package com.caco3.elijars.launcher;

public class ElijarsLaunchException extends RuntimeException {
    public ElijarsLaunchException(String message) {
        super(message);
    }

    public ElijarsLaunchException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.caco3.elijars.utils;

import java.io.IOException;

/**
 * Functions that performs an io action.
 * <p>
 * That is it takes {@code T} as a parameter, returns {@code R} and can throw {@link IOException}
 *
 * @param <T> parameter type
 * @param <R> result type
 */
@FunctionalInterface
public interface IoFunction<T, R> {
    R apply(T t) throws IOException;
}

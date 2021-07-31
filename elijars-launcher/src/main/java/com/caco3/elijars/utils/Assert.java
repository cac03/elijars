package com.caco3.elijars.utils;

import java.util.function.Supplier;

public abstract class Assert {
    private Assert() {
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, Supplier<String> messageSupplier) {
        if (object == null) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    public static void state(boolean state, Supplier<String> messageSupplier) {
        if (!state) {
            throw new IllegalStateException(nullSafeGet(messageSupplier));
        }
    }

    public static void state(boolean state, String message) {
        if (!state) {
            throw new IllegalStateException(message);
        }
    }

    private static <T> T nullSafeGet(Supplier<? extends T> supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }
}

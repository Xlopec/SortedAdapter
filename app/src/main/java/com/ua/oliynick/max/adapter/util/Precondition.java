package com.ua.oliynick.max.adapter.util;

import java.util.Locale;

/**
 * Created by Максим on 2/14/2017.
 */
public final class Precondition {

    private Precondition() {
        throw new IllegalStateException("shouldn't be called");
    }

    public static void checkArgument(boolean condition, String message, Object... format) {
        if (!condition)
            throw new IllegalArgumentException(String.format(Locale.ENGLISH, message, format));
    }

    public static void checkArgument(boolean condition, String message) {
        if (!condition)
            throw new IllegalArgumentException(message);
    }

    public static void checkArgument(boolean condition) {
        if (!condition)
            throw new IllegalArgumentException();
    }

    public static void checkArguments(boolean... conditions) {
        Precondition.isNotNull(conditions);
        for (int i = 0; i < conditions.length; ++i) {
            if (!conditions[i])
                throw new IllegalArgumentException(String.format(Locale.ENGLISH,
                        "%d-th argument check failed", i + 1));
        }
    }

    public static <T> void isNull(T t, String message, Object... format) {
        if (t != null)
            throw new NullPointerException(String.format(Locale.ENGLISH, message, format));
    }

    public static <T> void isNull(T t, String message) {
        if (t != null)
            throw new IllegalArgumentException(message);
    }

    public static <T> void isNull(T t) {
        if (t != null)
            throw new NullPointerException();
    }

    public static <T> T isNotNull(T t, String message, Object... format) {
        if (t == null)
            throw new NullPointerException(String.format(Locale.ENGLISH, message, format));

        return t;
    }

    public static <T> T isNotNull(T t, String message) {
        if (t == null)
            throw new NullPointerException(message);

        return t;
    }

    public static void isNotNullAll(String message, Object... t) {
        Precondition.isNotNull(t);
        for (int i = 0; i < t.length; ++i) {
            if (t[i] == null)
                throw new NullPointerException(String.format(Locale.ENGLISH,
                        "%d-th argument was null, message - %s", i + 1, message));
        }
    }

    public static void isNotNullAll(Object... t) {
        Precondition.isNotNull(t);
        for (int i = 0; i < t.length; ++i) {
            if (t[i] == null)
                throw new NullPointerException(String.format(Locale.ENGLISH,
                        "%d-th argument was null", i + 1));
        }
    }

    public static <T> T isNotNull(T t) {
        if (t == null)
            throw new NullPointerException();

        return t;
    }

}

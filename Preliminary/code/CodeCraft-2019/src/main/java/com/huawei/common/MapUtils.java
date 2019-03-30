package com.huawei.common;

import java.util.Map;

public class MapUtils {
    private MapUtils() {
    }

    public interface ExceptionThrower<E extends Exception> {
        void throwException() throws E;
    }

    public static <K, V, E extends Exception> V getOrThrow(Map<K, V> map, K key, ExceptionThrower<E> exceptionThrower) throws E {
        if (map.containsKey(key))
            return map.get(key);
        else {
            exceptionThrower.throwException();
            throw new AssertionError();
        }
    }

    public static <K, V> V getOrThrow(Map<K, V> map, K key) throws AssertionError {
        if (map.containsKey(key))
            return map.get(key);
        else
            throw new AssertionError();
    }
}

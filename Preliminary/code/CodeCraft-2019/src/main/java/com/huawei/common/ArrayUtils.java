package com.huawei.common;

import java.util.function.Predicate;

public class ArrayUtils {
    private ArrayUtils() {
    }

    public static <T> int indexOf(T[] array, Predicate<? super T> predicate) {
        for (int i = 0; i < array.length; i++)
            if (predicate.test(array[i]))
                return i;

        return -1;
    }
}

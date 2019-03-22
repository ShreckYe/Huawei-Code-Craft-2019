package com.huawei.util;

public final class DataUtils {
    private DataUtils() {
    }

    public static String tupleToString(int[] tuple) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        if (tuple.length == 0)
            throw new IllegalArgumentException("tuple has no elements");

        stringBuilder.append(tuple[0]);
        for (int i = 1; i < tuple.length; i++)
            stringBuilder.append(", ").append(tuple[i]);

        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

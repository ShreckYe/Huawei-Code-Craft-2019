package com.huawei.common;

public class MathUtils {
    private MathUtils() {
    }

    public static int mod(int x, int y) {
        int r = x % y;
        return r >= 0 ? r : r + y;
    }
}

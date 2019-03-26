package com.huawei.common;

public class MathUtils {
    private MathUtils() {
    }

    public static int mod(int x, int y) {
        int r = x % y;
        return r >= 0 ? r : r + y;
    }

    public static int ceilDivBy2(int x) {
        return (x + 1) / 2;
    }
}

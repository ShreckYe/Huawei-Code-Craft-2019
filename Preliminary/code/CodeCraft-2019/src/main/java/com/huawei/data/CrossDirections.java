package com.huawei.data;

import com.huawei.common.MathUtils;

public class CrossDirections {
    private CrossDirections() {
    }

    public final static int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

    public static int reverse(int direction) {
        return MathUtils.mod(direction + 2, 4);
    }
}

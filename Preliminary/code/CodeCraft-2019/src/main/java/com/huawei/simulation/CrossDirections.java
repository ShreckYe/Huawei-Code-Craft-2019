package com.huawei.simulation;

import com.huawei.common.MathUtils;

public class CrossDirections {
    private CrossDirections() {
    }

    public final static int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

    public static int reverse(int direction) {
        return MathUtils.mod(direction + 2, 4);
    }

    public static int getDirectionOut(int directionIn, CrossTurn crossTurn) {
        return MathUtils.mod(directionIn + crossTurn.directionOffset, 4);
    }

    public static int getDirectionIn(int directionOut, CrossTurn crossTurn) {
        return MathUtils.mod(directionOut - crossTurn.directionOffset, 4);
    }
}

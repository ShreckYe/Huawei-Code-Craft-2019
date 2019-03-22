package com.huawei.simulation;

import com.huawei.common.MathUtils;

public class SimulationUtils {
    public static int getDirectionOut(int directionIn, CrossTurn crossTurn) {
        return MathUtils.mod(directionIn + crossTurn.directionOffset, 4);
    }

    public static int getDirectionIn(int directionOut, CrossTurn crossTurn) {
        return MathUtils.mod(directionOut - crossTurn.directionOffset, 4);
    }
}

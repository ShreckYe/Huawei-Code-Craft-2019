package com.huawei.simulation;

import java.util.Arrays;

// A path determined by its turns at each cross
public class TurnPath {
    // First direction choice at the cross garage
    int firstDirection;
    // The i-th element indicates the getDirectionOut to make from the i-th road to the (i+1)-th road
    CrossTurn[] crossTurns;

    TurnPath(int firstDirection, CrossTurn[] crossTurns) {
        this.firstDirection = firstDirection;
        this.crossTurns = crossTurns;
    }

    public int getFirstDirection() {
        return firstDirection;
    }

    public CrossTurn[] getCrossTurns() {
        return crossTurns;
    }

    @Override
    public String toString() {
        return "TurnPath{" +
                "firstDirection=" + firstDirection +
                ", crossTurns=" + Arrays.toString(crossTurns) +
                '}';
    }
}

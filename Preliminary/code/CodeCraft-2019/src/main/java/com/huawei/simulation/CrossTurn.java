package com.huawei.simulation;

import com.huawei.common.MathUtils;

import java.util.Arrays;
import java.util.stream.Stream;

public enum CrossTurn {
    // Ordered by priority
    STRAIGHT(2, 0),
    LEFT(1, 1),
    RIGHT(3, 2);
    private final static CrossTurn[] INDEXED_BY_DIRECTION_OFFSET = new CrossTurn[]{null, LEFT, STRAIGHT, RIGHT};

    // The value represents the direction change in clockwise order
    int directionOffset,
    // Cross turn priority
    priority;

    CrossTurn(int directionOffset, int priority) {
        this.directionOffset = directionOffset;
        this.priority = priority;
    }

    public static CrossTurn getWithDirectionOffset(int directionOffset) {
        return INDEXED_BY_DIRECTION_OFFSET[MathUtils.mod(directionOffset, 4)];
    }

    boolean higherThan(CrossTurn other) {
        return priority < other.priority;
    }

    // This method should be package level
    Stream<CrossTurn> getAllWithHigherPriority() {
        return Arrays.stream(values()).limit(priority);
    }
}
package com.huawei.simulation;

import java.util.Arrays;

public class PrintCrossTurnEnumValues {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(CrossTurn.values()));

        System.out.println(CrossTurn.STRAIGHT.getAllWithHigherPriority());
        System.out.println(CrossTurn.LEFT.getAllWithHigherPriority());
        System.out.println(CrossTurn.RIGHT.getAllWithHigherPriority());
    }
}

package com.huawei.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Answer {

    int carId, actualStartTime;

    List<Integer> route;

    public Answer(int carId, int actualStartTime, List<Integer> roads) {
        this.carId = carId;
        this.actualStartTime = actualStartTime;
        this.route = roads;
    }

    public Answer(int carId, int actualStartTime, int[] tuple) {
        this(carId, actualStartTime, Arrays.stream(tuple).boxed().collect(Collectors.toList()));
    }

    public Answer(int[] tuple) {
        this(tuple[0], tuple[1], Arrays.copyOfRange(tuple, 2, tuple.length - 1));
    }


    @Override
    public String toString() {
        return "Answer{" +
                "carId=" + carId +
                ", actualStartTime=" + actualStartTime +
                ", route=" + route +
                '}';
    }
}

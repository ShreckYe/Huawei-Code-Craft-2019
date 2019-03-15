package com.huawei.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Answer {

    int carId, startTime;

    List<Integer> route;

    public Answer(int carId, int startTime, List<Integer> roads) {
        this.carId = carId;
        this.startTime = startTime;
        this.route = roads;
    }

    public Answer(int carId, int startTime, int[] tuple) {
        this(carId, startTime, Arrays.stream(tuple).boxed().collect(Collectors.toList()));
    }

    public static Answer fromTuple(int[] tuple) {
        return new Answer(tuple[0], tuple[1], Arrays.copyOfRange(tuple, 2, tuple.length - 1));
    }

    public String toTuple() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(carId).append(", ").append(startTime);
        for (int roadId : route)
            stringBuilder.append(", ").append(roadId);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return "Answer{" +
                "carId=" + carId +
                ", startTime=" + startTime +
                ", route=" + route +
                '}';
    }
}

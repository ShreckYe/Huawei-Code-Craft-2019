package com.huawei.data;

import java.util.Arrays;

public class Answer {
    int carId, startTime;
    Path path;

    public Answer(int carId, int startTime, Path path) {
        this.carId = carId;
        this.startTime = startTime;
        this.path = path;
    }

    public static Answer fromTuple(int[] tuple) {
        return new Answer(tuple[0], tuple[1], new Path(Arrays.stream(tuple).skip(2).toArray()));
    }

    public int getCarId() {
        return carId;
    }

    public int getStartTime() {
        return startTime;
    }

    public Path getPath() {
        return path;
    }

    public int[] toTuple() {
        int[] roadIds = path.getRoadIds();
        int numberOfRoadIds = roadIds.length;
        int[] tuple = new int[2 + numberOfRoadIds];

        tuple[0] = carId;
        tuple[1] = startTime;

        for (int i = 0; i < numberOfRoadIds; i++)
            tuple[2 + i] = roadIds[i];

        return tuple;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer = (Answer) o;

        if (carId != answer.carId) return false;
        if (startTime != answer.startTime) return false;
        return path.equals(answer.path);
    }

    @Override
    public int hashCode() {
        int result = carId;
        result = 31 * result + startTime;
        result = 31 * result + path.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "carId=" + carId +
                ", startTime=" + startTime +
                ", path=" + path +
                '}';
    }
}

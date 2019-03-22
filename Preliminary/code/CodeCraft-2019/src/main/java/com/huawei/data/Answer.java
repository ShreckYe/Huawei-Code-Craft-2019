package com.huawei.data;

public class Answer {
    int carId, startTime;
    Path path;

    public Answer(int carId, int startTime, Path path) {
        this.carId = carId;
        this.startTime = startTime;
        this.path = path;
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
    public String toString() {
        return "Answer{" +
                "carId=" + carId +
                ", startTime=" + startTime +
                ", path=" + path +
                '}';
    }
}

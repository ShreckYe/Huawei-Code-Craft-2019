package com.huawei.simulation;

import com.huawei.common.Pair;
import com.huawei.data.Location;
import com.huawei.data.Path;

import java.util.List;

public class CarSimulationResult {
    private int carId;
    private int startTime,
            arriveTime;
    private TurnPath turnPath;
    private Path path;
    private List<Pair<Integer, Location>> timeLocations;

    public CarSimulationResult(int carId, int startTime, int arriveTime, TurnPath turnPath, Path path, List<Pair<Integer, Location>> timeLocations) {
        this.carId = carId;
        this.startTime = startTime;
        this.arriveTime = arriveTime;
        this.turnPath = turnPath;
        this.path = path;
        this.timeLocations = timeLocations;
    }

    public int getCarId() {
        return carId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getArriveTime() {
        return arriveTime;
    }

    public TurnPath getTurnPath() {
        return turnPath;
    }

    public Path getPath() {
        return path;
    }

    public List<Pair<Integer, Location>> getTimeLocations() {
        return timeLocations;
    }

    @Override
    public String toString() {
        return "CarSimulationResult{" +
                "carId=" + carId +
                ", startTime=" + startTime +
                ", turnPath=" + turnPath +
                ", path=" + path +
                ", timeLocations=" + timeLocations +
                '}';
    }
}

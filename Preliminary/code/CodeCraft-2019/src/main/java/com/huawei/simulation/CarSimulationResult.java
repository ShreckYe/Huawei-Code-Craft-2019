package com.huawei.simulation;

import com.huawei.common.Pair;
import com.huawei.data.Location;
import com.huawei.data.Path;

import java.util.List;

public class CarSimulationResult {
    private int carId;
    private int startTime;
    private Path path;
    private List<Pair<Integer, Location>> timeLocations;

    CarSimulationResult(int carId, int startTime, Path path, List<Pair<Integer, Location>> timeLocations) {
        this.carId = carId;
        this.startTime = startTime;
        this.path = path;
        this.timeLocations = timeLocations;
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

    public List<Pair<Integer, Location>> getTimeLocations() {
        return timeLocations;
    }
}

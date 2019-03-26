package com.huawei.simulation;

import com.huawei.data.Car;

public class CarStartTimeTurnPathSingleSolution {
    Car car;
    public int startTime;
    public TurnPath turnPath;

    public CarStartTimeTurnPathSingleSolution(Car car, int startTime, TurnPath turnPath) {
        this.car = car;
        this.startTime = startTime;
        this.turnPath = turnPath;
    }

    public Car getCar() {
        return car;
    }
}

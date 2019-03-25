package com.huawei.simulation;

import com.huawei.data.Car;

class SimulationGarageCar {
    // Unknown start time
    int id, from, to, speed, startTime;
    TurnPath path;

    SimulationGarageCar(int id, int from, int to, int speed, int startTime, TurnPath path) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.speed = speed;
        this.startTime = startTime;
        this.path = path;
    }

    SimulationGarageCar(Car car, int startTime, TurnPath path) {
        this(car.getId(), car.getFrom(), car.getTo(), car.getSpeed(), startTime, path);
    }
}

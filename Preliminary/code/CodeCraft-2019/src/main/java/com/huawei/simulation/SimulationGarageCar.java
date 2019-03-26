package com.huawei.simulation;

import com.huawei.data.Car;

class SimulationGarageCar {
    // Unknown start time
    final int id, from, to, speed, planTime;
    TurnPath path;

    int startTime;

    public SimulationGarageCar(int id, int from, int to, int speed, int planTime, TurnPath path, int startTime) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.speed = speed;
        this.planTime = planTime;
        this.path = path;
        this.startTime = startTime;
    }

    SimulationGarageCar(Car car, int startTime, TurnPath path) {
        this(car.getId(), car.getFrom(), car.getTo(), car.getSpeed(), car.getPlanTime(), path, startTime);
    }
}

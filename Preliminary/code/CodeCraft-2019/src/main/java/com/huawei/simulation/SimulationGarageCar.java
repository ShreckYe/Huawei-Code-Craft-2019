package com.huawei.simulation;

import com.huawei.data.Car;

class SimulationGarageCar {
    // Unknown start time
    int id, from, to, speed, planTime;
    PathCrossTurns path;

    SimulationGarageCar(int id, int from, int to, int speed, int planTime, PathCrossTurns path) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.speed = speed;
        this.planTime = planTime;
        this.path = path;
    }

    SimulationGarageCar(Car car, PathCrossTurns path) {
        this(car.getId(), car.getFrom(), car.getTo(), car.getSpeed(), car.getPlanTime(), path);
    }
}

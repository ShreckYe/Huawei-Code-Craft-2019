package com.huawei.simulation;

class SimulationRoadCar {
    int carId,
            speed,
            position;
    PathCrossTurns path;
    int currentPathIndex;

    int startTime;

    // Indicates whether the car is waiting to be scheduled
    boolean waiting;

    SimulationRoadCar(int carId, int speed, int position, PathCrossTurns path, int currentPathIndex, int startTime, boolean waiting) {
        this.carId = carId;
        this.speed = speed;
        this.position = position;
        this.path = path;
        this.currentPathIndex = currentPathIndex;
        this.startTime = startTime;
        this.waiting = waiting;
    }


    SimulationRoadCar(int carId, int speed, PathCrossTurns path, int startTime) {
        this(carId, speed, -1, path, 0, startTime, false);
    }

    /*private void scheduleTo(int newPosition) {
        waiting = false;
        position = newPosition;
    }*/

    CrossTurn getCurrentTurn() {
        return currentPathIndex == path.crossTurns.length ? null : path.crossTurns[currentPathIndex];
    }
}

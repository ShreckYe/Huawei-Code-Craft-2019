package com.huawei.simulation;

class SimulationRoadCar {
    final int carId,
            speed;
    int position;
    final static int UNINITILIAZED_POSITION = Integer.MAX_VALUE;
    final PathCrossTurns path;
    int currentPathIndex;
    final static int INITIAL_PATH_INDEX = -1;

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
        this(carId, speed, UNINITILIAZED_POSITION, path, INITIAL_PATH_INDEX, startTime, false);
    }

    /*private void scheduleTo(int newPosition) {
        waiting = false;
        position = newPosition;
    }*/

    CrossTurn getCurrentTurn() {
        return currentPathIndex == path.crossTurns.length ? null : path.crossTurns[currentPathIndex];
    }

    @Override
    public String toString() {
        return "SimulationRoadCar{" +
                "carId=" + carId +
                ", speed=" + speed +
                ", position=" + position +
                ", path=" + path +
                ", currentPathIndex=" + currentPathIndex +
                ", startTime=" + startTime +
                ", waiting=" + waiting +
                '}';
    }
}

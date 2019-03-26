package com.huawei.simulation;

import java.util.NoSuchElementException;

class SimulationRoadCar {
    final int carId,
            speed;
    int position;
    final static int UNINITILIAZED_POSITION = Integer.MAX_VALUE;
    final TurnPath turnPath;
    int currentPathIndex;
    final static int INITIAL_PATH_INDEX = -1;

    int startTime;

    // Indicates whether the car is waiting to be scheduled
    boolean waiting;

    SimulationRoadCar(int carId, int speed, int position, TurnPath turnPath, int currentPathIndex, int startTime, boolean waiting) {
        this.carId = carId;
        this.speed = speed;
        this.position = position;
        this.turnPath = turnPath;
        this.currentPathIndex = currentPathIndex;
        this.startTime = startTime;
        this.waiting = waiting;
    }

    SimulationRoadCar(int carId, int speed, TurnPath turnPath, int startTime) {
        this(carId, speed, UNINITILIAZED_POSITION, turnPath, INITIAL_PATH_INDEX, startTime, false);
    }

    /*private void scheduleTo(int newPosition) {
        waiting = false;
        position = newPosition;
    }*/

    CrossTurn getCurrentTurn() throws NoSuchElementException {
        try {
            return currentPathIndex == turnPath.crossTurns.length ? CrossTurn.STRAIGHT : turnPath.crossTurns[currentPathIndex];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new NoSuchElementException();
        }
    }

    boolean isArriving() {
        return currentPathIndex == turnPath.crossTurns.length;
    }

    @Override
    public String toString() {
        return "SimulationRoadCar{" +
                "carId=" + carId +
                ", speed=" + speed +
                ", position=" + position +
                ", turnPath=" + turnPath +
                ", currentPathIndex=" + currentPathIndex +
                ", startTime=" + startTime +
                ", waiting=" + waiting +
                '}';
    }
}

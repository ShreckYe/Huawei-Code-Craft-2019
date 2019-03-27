package com.huawei.simulation;

import java.util.NoSuchElementException;

class SimulationRoadCar {
    final int carId,
            speed,
            planTime;
    private int position;
    final static int UNINITILIAZED_POSITION = Integer.MAX_VALUE;
    final TurnPath turnPath;
    int currentPathIndex;
    final static int INITIAL_PATH_INDEX = -1;

    int startTime;

    // Indicates whether the car is waiting to be scheduled
    private boolean waiting;

    public SimulationRoadCar(int carId, int speed, int planTime, int position, TurnPath turnPath, int currentPathIndex, int startTime, boolean waiting) {
        this.carId = carId;
        this.speed = speed;
        this.planTime = planTime;
        this.position = position;
        this.turnPath = turnPath;
        this.currentPathIndex = currentPathIndex;
        this.startTime = startTime;
        this.waiting = waiting;
    }

    SimulationRoadCar(int carId, int speed, int planTime, TurnPath turnPath, int startTime) {
        this(carId, speed, planTime, UNINITILIAZED_POSITION, turnPath, INITIAL_PATH_INDEX, startTime, true);
    }

    public int getPosition() {
        return position;
    }

    public boolean isWaiting() {
        return waiting;
    }

    void setWaitingWhenSchedulingRoad() {
        if (waiting) throw new IllegalStateException("Already waiting");
        waiting = true;
    }

    void scheduleToPositionWhenSchedulingRoad(int newPosition) {
        if (waiting) throw new IllegalStateException("Already waiting");
        position = newPosition;
        // Already assigned to this value: waiting = false;
    }

    void scheduleToPosition(int newPosition) {
        if (!waiting) throw new IllegalStateException("Already scheduled");
        position = newPosition;
        waiting = false;
    }

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

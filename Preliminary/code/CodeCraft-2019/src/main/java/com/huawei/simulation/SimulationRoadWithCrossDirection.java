package com.huawei.simulation;

class SimulationRoadWithCrossDirection {
    SimulationRoad road;
    // See CrossDirections
    // Can be either direction in (which is opposite to actual driving direction) or direction out
    int direction;

    SimulationRoadWithCrossDirection(SimulationRoad road, int direction) {
        this.road = road;
        this.direction = direction;
    }
}

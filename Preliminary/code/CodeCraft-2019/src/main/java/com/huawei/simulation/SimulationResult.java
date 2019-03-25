package com.huawei.simulation;

import java.util.List;

public class SimulationResult {
    public final static int STATUS_SUCCESS = 0, STATUS_SUCCESS_AEAP = 1, STATUS_DEADLOCK = 2, STATUS_CANT_START_ON_TIME = 3;
    private int statusCode;
    // Indicates the time to schedule all cars when successful, or the time before it fails otherwise
    private int systemScheduleTime,
            totalTravelTime;
    private List<CarSimulationResult> carSimulationResults;

    private SimulationResult(int statusCode, int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carSimulationResults) {
        this.statusCode = statusCode;
        this.systemScheduleTime = systemScheduleTime;
        this.totalTravelTime = totalTravelTime;
        this.carSimulationResults = carSimulationResults;
    }

    static SimulationResult newSuccessInstance(int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new SimulationResult(STATUS_SUCCESS, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    static SimulationResult newSuccessInstance(boolean cantStartOnTime, int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new SimulationResult(cantStartOnTime ? STATUS_SUCCESS_AEAP : STATUS_SUCCESS, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    static SimulationResult newDeadlockInstance(int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new SimulationResult(STATUS_DEADLOCK, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    static SimulationResult newCantStartOnTimeInstance(int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new SimulationResult(STATUS_CANT_START_ON_TIME, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    public int getStatusCode() {
        return statusCode;
    }

    /*public boolean isSuccessful() {
        return statusCode == STATUS_SUCCESS;
    }*/

    public int getSystemScheduleTime() {
        return systemScheduleTime;
    }

    public int getTotalTravelTime() {
        return totalTravelTime;
    }

    public List<CarSimulationResult> getCarSimulationResults() {
        return carSimulationResults;
    }

    @Override
    public String toString() {
        return "SimulationResult{" +
                "statusCode=" + statusCode +
                ", systemScheduleTime=" + systemScheduleTime +
                ", totalTravelTime=" + totalTravelTime +
                ", carSimulationResults=" + carSimulationResults +
                '}';
    }
}

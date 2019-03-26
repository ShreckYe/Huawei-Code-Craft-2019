package com.huawei.simulation;

import java.util.List;

public class FullSimulationResult {
    public final static int STATUS_SUCCESS = 0, STATUS_SUCCESS_AEAP = 1, STATUS_DEADLOCK = 2, STATUS_CANT_START_ON_TIME = 3;
    private int statusCode;
    // Indicates the time to schedule all cars when successful, or the time before it fails otherwise
    private int systemScheduleTime,
            totalTravelTime;
    private List<CarSimulationResult> carSimulationResults;

    private FullSimulationResult(int statusCode, int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carSimulationResults) {
        this.statusCode = statusCode;
        this.systemScheduleTime = systemScheduleTime;
        this.totalTravelTime = totalTravelTime;
        this.carSimulationResults = carSimulationResults;
    }

    static FullSimulationResult newSuccessInstance(int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new FullSimulationResult(STATUS_SUCCESS, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    static FullSimulationResult newSuccessInstance(boolean cantStartOnTime, int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new FullSimulationResult(cantStartOnTime ? STATUS_SUCCESS_AEAP : STATUS_SUCCESS, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    static FullSimulationResult newDeadlockInstance(int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new FullSimulationResult(STATUS_DEADLOCK, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    static FullSimulationResult newCantStartOnTimeInstance(int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new FullSimulationResult(STATUS_CANT_START_ON_TIME, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isSuccessfulAeap() {
        return statusCode == STATUS_SUCCESS || statusCode == STATUS_SUCCESS_AEAP;
    }

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
        return "FullSimulationResult{" +
                "statusCode=" + statusCode +
                ", systemScheduleTime=" + systemScheduleTime +
                ", totalTravelTime=" + totalTravelTime +
                ", carSimulationResults=" + carSimulationResults +
                '}';
    }
}

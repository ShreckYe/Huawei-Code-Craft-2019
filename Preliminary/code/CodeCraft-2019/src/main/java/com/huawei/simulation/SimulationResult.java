package com.huawei.simulation;

import java.util.List;

public class SimulationResult {
    public final static int STATUS_SUCCESS = 0, STATUS_DEADLOCK = 1;
    private int statusCode;
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

    static SimulationResult newDeadlockInstance(int systemScheduleTime, int totalTravelTime, List<CarSimulationResult> carPathSimulations) {
        return new SimulationResult(STATUS_DEADLOCK, systemScheduleTime, totalTravelTime, carPathSimulations);
    }

    public int getStatusCode() {
        return statusCode;
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
        return "SimulationResult{" +
                "statusCode=" + statusCode +
                ", systemScheduleTime=" + systemScheduleTime +
                ", totalTravelTime=" + totalTravelTime +
                ", carSimulationResults=" + carSimulationResults +
                '}';
    }
}

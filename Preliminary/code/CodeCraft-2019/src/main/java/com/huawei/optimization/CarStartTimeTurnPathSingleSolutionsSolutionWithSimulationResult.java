package com.huawei.optimization;

import com.huawei.simulation.CarStartTimeTurnPathSingleSolution;

import java.util.List;

class CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult {
    List<CarStartTimeTurnPathSingleSolution> singleSolutions;
    int minStartTime,
            systemScheduleTime;

    CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult(List<CarStartTimeTurnPathSingleSolution> singleSolutions, int minStartTime, int systemScheduleTime) {
        this.singleSolutions = singleSolutions;
        this.minStartTime = minStartTime;
        this.systemScheduleTime = systemScheduleTime;
    }

    /*public void mergeSolutionsWith(CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult other) {
        singleSolutions.addAll(other.singleSolutions);
    }*/

    public int getSystemScheduleRunningTime() {
        return systemScheduleTime - minStartTime;
    }

    public void shiftStartTimesBy(int incrementTime) {
        for (CarStartTimeTurnPathSingleSolution solution : singleSolutions)
            solution.startTime += incrementTime;
    }
}

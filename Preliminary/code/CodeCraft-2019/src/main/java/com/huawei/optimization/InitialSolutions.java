package com.huawei.optimization;


import com.huawei.common.IntObjPair;
import com.huawei.common.Pair;
import com.huawei.data.Car;
import com.huawei.data.Path;
import com.huawei.simulation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.huawei.simulation.SimulationResult.*;

public class InitialSolutions {
    public static List<Pair<Car, IntObjPair<TurnPath>>> determineSuccessfulStartTimesWithPath(TrafficSimulationGraph simulationGraph, List<Pair<Car, IdealPathResult>> carIdealPathResults) {
        List<Pair<Car, Path>> carPaths = carIdealPathResults.stream()
                .sorted(Comparator.comparingDouble(carIdealPathResult -> -carIdealPathResult.getSecond().getIdealArriveTime()))
                .map(carIdealPathResult -> new Pair<>(carIdealPathResult.getFirst(), carIdealPathResult.getSecond().getPath()))
                .collect(Collectors.toList());
        List<Pair<Car, IntObjPair<TurnPath>>> carStartTimePaths = SimulationDataUtils.carPathsToCarStartTimePaths(
                simulationGraph.convertCarPathToCarTurnPath(carPaths));


        int size = carStartTimePaths.size();
        SimulationResult simulationResult = null;
        int inc = 1, lastSuccessfulScheduleTime = -1;
        for (int i = 0; i < size; ) {
            List<Pair<Car, IntObjPair<TurnPath>>> simulationCarStartTimePaths = carStartTimePaths.subList(0, i + 1);
            simulationResult = simulationGraph.simulateAeap(simulationCarStartTimePaths);
            simulationGraph.clearSimulation();

            switch (simulationResult.getStatusCode()) {
                case STATUS_SUCCESS_AEAP:
                    System.out.println("Success AEAP: " + i);
                    inc = 1;
                    lastSuccessfulScheduleTime = simulationResult.getSystemScheduleTime();
                    i++;
                    break;
                case STATUS_SUCCESS:
                    System.out.println("Success: " + i);
                    inc = 1;
                    lastSuccessfulScheduleTime = simulationResult.getSystemScheduleTime();
                    i++;
                    break;

                case STATUS_DEADLOCK:
                    System.out.println("Deadlock: " + i);
                    IntObjPair<TurnPath> startTimeTurnPath = carStartTimePaths.get(i).getSecond();
                    int startTime = startTimeTurnPath.getFirst();
                    if (startTime >= lastSuccessfulScheduleTime)
                        throw new AssertionError();
                    startTime += inc;
                    startTimeTurnPath.setFirst(startTime);
                    if (startTime > lastSuccessfulScheduleTime) {
                        startTime = lastSuccessfulScheduleTime;
                    }
                    inc *= 2;
                    System.out.println(inc + " " + startTime + " " + lastSuccessfulScheduleTime);

                    break;
                default:
                    throw new AssertionError();
            }
        }

        Map<Integer, Integer> actualStartTimes = simulationResult.getCarSimulationResults().stream().collect(Collectors.toMap(CarSimulationResult::getCarId, CarSimulationResult::getStartTime));
        for (Pair<Car, IntObjPair<TurnPath>> carStartTimePath : carStartTimePaths)
            carStartTimePath.getSecond().setFirst(actualStartTimes.get(carStartTimePath.getFirst().getId()));

        return carStartTimePaths;
    }
}

package com.huawei.optimization;


import com.huawei.common.ListUtils;
import com.huawei.common.MathUtils;
import com.huawei.common.Pair;
import com.huawei.data.Car;
import com.huawei.data.Path;
import com.huawei.simulation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.huawei.simulation.FullSimulationResult.STATUS_DEADLOCK;

public class InitialSolutions {
    private static void setStartTimesToActual(List<CarStartTimeTurnPathSingleSolution> singleSolutions, FullSimulationResult fullSimulationResult) {
        Map<Integer, Integer> actualStartTimes = fullSimulationResult.getCarSimulationResults().stream().collect(Collectors.toMap(CarSimulationResult::getCarId, CarSimulationResult::getStartTime));
        for (CarStartTimeTurnPathSingleSolution singleSolution : singleSolutions)
            singleSolution.startTime = actualStartTimes.get(actualStartTimes.get(singleSolution.getCar().getId()));
    }

    private static List<CarStartTimeTurnPathSingleSolution> getSingleSolutionsOrderedDescentByIdealArriveTime
            (TrafficSimulationGraph simulationGraph, List<Pair<Car, IdealPathResult>> carIdealPathResults) {
        List<Pair<Car, Path>> carPaths = carIdealPathResults.stream()
                .sorted(Comparator.comparingDouble(carIdealPathResult -> -carIdealPathResult.getSecond().getIdealArriveTime()))
                .map(carIdealPathResult -> new Pair<>(carIdealPathResult.getFirst(), carIdealPathResult.getSecond().getPath()))
                .collect(Collectors.toList());
        return SimulationDataUtils.carPathsToSingleSolutions(
                simulationGraph.convertCarPathToCarTurnPath(carPaths));
    }

    private static List<CarStartTimeTurnPathSingleSolution> getSingleSolutionsOrderedByPlanTime
            (TrafficSimulationGraph simulationGraph, List<Pair<Car, IdealPathResult>> carIdealPathResults) {
        List<Pair<Car, Path>> carPaths = carIdealPathResults.stream()
                .sorted(Comparator.comparingDouble(carIdealPathResult -> carIdealPathResult.getFirst().getPlanTime()))
                .map(carIdealPathResult -> new Pair<>(carIdealPathResult.getFirst(), carIdealPathResult.getSecond().getPath()))
                .collect(Collectors.toList());
        return SimulationDataUtils.carPathsToSingleSolutions(
                simulationGraph.convertCarPathToCarTurnPath(carPaths));
    }

    public static List<CarStartTimeTurnPathSingleSolution> determineSuccessfulStartTimesOneByOne
            (TrafficSimulationGraph simulationGraph, List<Pair<Car, IdealPathResult>> carIdealPathResults) {
        List<CarStartTimeTurnPathSingleSolution> singleSolutions = getSingleSolutionsOrderedDescentByIdealArriveTime(simulationGraph, carIdealPathResults);

        int size = singleSolutions.size();
        List<CarStartTimeTurnPathSingleSolution> successfulSingleSolutions = new ArrayList<>();
        int lastSuccessfulSystemScheduleTime = 0;
        for (int i = 0; i < size; ) {
            CarStartTimeTurnPathSingleSolution singleSolution = singleSolutions.get(i);
            successfulSingleSolutions.add(singleSolution);
            FullSimulationResult fullSimulationResult = simulationGraph.simulateAeap(successfulSingleSolutions);

            if (fullSimulationResult.isSuccessfulAeap()) {
                System.out.println("Success: " + i + ", time: " + lastSuccessfulSystemScheduleTime);
                lastSuccessfulSystemScheduleTime = fullSimulationResult.getSystemScheduleTime();
                i++;
            } else if (fullSimulationResult.getStatusCode() == STATUS_DEADLOCK) {
                System.out.println("Deadlock: " + i);

                // Try to find a relatively small offset using binary search
                int low = singleSolution.startTime,
                        high = lastSuccessfulSystemScheduleTime;
                while (low < high) {
                    int mid = (low + high) >> 1;

                    singleSolution.startTime = mid;
                    FullSimulationResult bsFullSimulationResult = simulationGraph.simulateAeap(successfulSingleSolutions);

                    if (bsFullSimulationResult.isSuccessfulAeap()) {
                        // high always successful
                        high = mid;
                        lastSuccessfulSystemScheduleTime = fullSimulationResult.getSystemScheduleTime();
                    } else
                        // low possibly successful
                        low = mid + 1;
                }

                singleSolution.startTime = high;
            } else
                throw new AssertionError();
        }

        // TODO: setStartTimesToActual();

        return successfulSingleSolutions;
    }

    public static List<CarStartTimeTurnPathSingleSolution> determineSuccessfulStartTimesByDivideAndMerge
            (TrafficSimulationGraph simulationGraph, List<Pair<Car, IdealPathResult>> carIdealPathResults) {
        List<CarStartTimeTurnPathSingleSolution> singleSolutions = getSingleSolutionsOrderedDescentByIdealArriveTime(simulationGraph, carIdealPathResults);

        return divideAndMerge(simulationGraph, singleSolutions).singleSolutions;
    }

    private static CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult divideAndMerge(TrafficSimulationGraph simulationGraph, List<CarStartTimeTurnPathSingleSolution> singleSolutions) {
        int size = singleSolutions.size();

        if (size == 0)
            return new CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult(singleSolutions, 0, 0);
        else if (size == 1) {
            FullSimulationResult fullSimulationResult = simulationGraph.simulateAeap(singleSolutions);
            if (!fullSimulationResult.isSuccessfulAeap())
                throw new AssertionError();
            return new CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult(singleSolutions, singleSolutions.get(0).startTime, fullSimulationResult.getSystemScheduleTime());
        }

        int halfSize = MathUtils.ceilDivBy2(size);
        CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult firstHalf = divideAndMerge(simulationGraph, singleSolutions.subList(0, halfSize)),
                secondHalf = divideAndMerge(simulationGraph, singleSolutions.subList(halfSize, size));

        return mergeCarPathsAndDetermineStartTimes(simulationGraph, firstHalf, secondHalf);
    }

    private static CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult
    mergeCarPathsAndDetermineStartTimes(TrafficSimulationGraph simulationGraph,
                                        CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult earlierSolution,
                                        CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult laterSolution) {
        List<CarStartTimeTurnPathSingleSolution> singleSolutions = ListUtils.concatLists(earlierSolution.singleSolutions, laterSolution.singleSolutions);

        // We may be lucky to directly merge two without shifting
        FullSimulationResult directFullSimulationResult = simulationGraph.simulateAeap(singleSolutions);
        if (directFullSimulationResult.isSuccessfulAeap())
            return new CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult(singleSolutions,
                    Math.min(earlierSolution.minStartTime, laterSolution.minStartTime),
                    directFullSimulationResult.getSystemScheduleTime());

        // Try to find a relatively small offset using binary search
        int low = 0,
                high = Math.max(earlierSolution.systemScheduleTime - laterSolution.minStartTime, 0);
        int lastSuccessfulSystemScheduleTime = -1;
        while (low < high) {
            int mid = (low + high) >> 1;

            laterSolution.shiftStartTimesBy(mid);
            FullSimulationResult fullSimulationResult = simulationGraph.simulateAeap(singleSolutions);
            laterSolution.shiftStartTimesBy(-mid);

            if (fullSimulationResult.isSuccessfulAeap()) {
                // high always successful
                high = mid;
                lastSuccessfulSystemScheduleTime = fullSimulationResult.getSystemScheduleTime();
            } else if (fullSimulationResult.getStatusCode() == STATUS_DEADLOCK)
                // low possibly successful
                low = mid + 1;
            else
                throw new AssertionError();
        }
        if (high != low) throw new AssertionError();
        laterSolution.shiftStartTimesBy(high);

        CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult solution = new CarStartTimeTurnPathSingleSolutionsSolutionWithSimulationResult(singleSolutions,
                Math.min(earlierSolution.minStartTime, laterSolution.minStartTime + high),
                lastSuccessfulSystemScheduleTime != -1 ? lastSuccessfulSystemScheduleTime :
                        earlierSolution.systemScheduleTime + laterSolution.getSystemScheduleRunningTime()
        );

        System.out.println("Size: " + singleSolutions.size() + ", schedule time: " + solution.systemScheduleTime);
        return solution;
    }

    public static List<CarStartTimeTurnPathSingleSolution> determineSuccessfulStartTimesByRunningOneAtEachTime
            (TrafficSimulationGraph simulationGraph, List<Pair<Car, IdealPathResult>> carIdealPathResults) {
        List<CarStartTimeTurnPathSingleSolution> singleSolutions = getSingleSolutionsOrderedByPlanTime(simulationGraph, carIdealPathResults);

        if (carIdealPathResults.size() == 0) return Collections.emptyList();
        int lastSystemScheduleTime = simulationGraph.simulateAeap(ListUtils.wrapSingleElement(singleSolutions.get(0))).getSystemScheduleTime();
        for (CarStartTimeTurnPathSingleSolution singleSolution : singleSolutions.stream().skip(1).collect(Collectors.toList())) {
            singleSolution.startTime = Math.max(singleSolution.startTime, lastSystemScheduleTime);
            lastSystemScheduleTime = simulationGraph.simulateAeap(ListUtils.wrapSingleElement(singleSolution)).getSystemScheduleTime();
        }

        return singleSolutions;
    }
}

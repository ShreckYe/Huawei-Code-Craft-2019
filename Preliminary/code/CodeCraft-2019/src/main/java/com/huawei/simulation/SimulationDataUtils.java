package com.huawei.simulation;

import com.huawei.common.Pair;
import com.huawei.data.Car;

import java.util.List;
import java.util.stream.Collectors;

public class SimulationDataUtils {
    public static List<CarStartTimeTurnPathSingleSolution> carPathsToSingleSolutions(List<Pair<Car, TurnPath>> carPaths) {
        return carPaths.stream()
                .map(carPath -> new CarStartTimeTurnPathSingleSolution(carPath.getFirst(), carPath.getFirst().getPlanTime(), carPath.getSecond()))
                .collect(Collectors.toList());
    }
}

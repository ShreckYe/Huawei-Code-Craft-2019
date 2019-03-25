package com.huawei.simulation;

import com.huawei.common.IntObjPair;
import com.huawei.common.MathUtils;
import com.huawei.common.Pair;
import com.huawei.data.Car;

import java.util.List;
import java.util.stream.Collectors;

public class SimulationDataUtils {
    public static List<Pair<Car, IntObjPair<TurnPath>>> carPathsToCarStartTimePaths(List<Pair<Car, TurnPath>> carPaths) {
        return carPaths.stream()
                .map(carPath -> new Pair<>(carPath.getFirst(), new IntObjPair<>(carPath.getFirst().getPlanTime(), carPath.getSecond())))
                .collect(Collectors.toList());
    }
}

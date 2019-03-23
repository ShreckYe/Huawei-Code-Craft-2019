package com.huawei.simulation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class SimulationCross {
    int crossId;
    // 4 roads in clockwise order from north to west
    SimulationRoad[] roadsIn, roadsOut;
    // Not null roads sorted by road ID
    List<SimulationRoadWithCrossDirection> roadsInSortedById, roadsOutSortedById;

    SimulationCross(int crossId, SimulationRoad[] roadsIn, SimulationRoad[] roadsOut) {
        this.crossId = crossId;
        if (roadsIn == null || roadsIn.length != 4)
            throw new IllegalArgumentException();
        this.roadsIn = roadsIn;
        roadsInSortedById = IntStream.range(0, 4)
                .filter(direction -> roadsIn[direction] != null)
                .mapToObj(direction -> new SimulationRoadWithCrossDirection(roadsIn[direction], direction))
                .sorted(Comparator.comparingInt(road -> road.road.directedRoadId.getRoadId()))
                .collect(Collectors.toList());
        if (roadsOut == null || roadsOut.length != 4)
            throw new IllegalArgumentException();
        this.roadsOut = roadsOut;
        roadsOutSortedById = IntStream.range(0, 4)
                .filter(direction -> roadsOut[direction] != null)
                .mapToObj(direction -> new SimulationRoadWithCrossDirection(roadsOut[direction], direction))
                .sorted(Comparator.comparingInt(road -> road.road.directedRoadId.getRoadId()))
                .collect(Collectors.toList());
    }
}

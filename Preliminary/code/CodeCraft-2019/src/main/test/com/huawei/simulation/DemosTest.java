package com.huawei.simulation;

import com.huawei.data.Cross;
import com.huawei.data.Road;
import com.huawei.graph.DirectedRoadId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

class DemosTest {
    int DEFAULT_LENGTH = 100;

    @Test
    void testThroughCross() {
        ArrayList<ThroughCrossTestCase> testCases = new ArrayList<>();
        testCases.add(new ThroughCrossTestCase(1, 5, 4, 4, 2, 4, 4, 2));
        testCases.add(new ThroughCrossTestCase(2, 5, 4, 4, 2, 5, 5, 3));
        testCases.add(new ThroughCrossTestCase(3, 5, 4, 4, 2, 3, 3, 1));
        testCases.add(new ThroughCrossTestCase(4, 5, 4, 4, 2, 1, 1, 0));
        testCases.add(new ThroughCrossTestCase(5, 5, 4, 4, 2, 2, 2, 0));
        testCases.add(new ThroughCrossTestCase(6, 5, 2, 2, 1, 5, 5, 4));
        testCases.add(new ThroughCrossTestCase(7, 5, 2, 2, 1, 3, 3, 2));

        for (ThroughCrossTestCase testCase : testCases) {
            ArrayList<Road> roads = new ArrayList<>();
            roads.add(new Road(1, DEFAULT_LENGTH, testCase.r1, 1, 1, 2, false));
            roads.add(new Road(2, DEFAULT_LENGTH, testCase.r2, 1, 2, 3, false));
            ArrayList<Cross> crosses = new ArrayList<>();
            crosses.add(new Cross(1, -1, 1, -1, -1));
            crosses.add(new Cross(2, -1, 2, -1, 1));
            crosses.add(new Cross(3, -1, -1, -1, 2));
            TrafficSimulationGraph simulationGraph = new TrafficSimulationGraph(roads, crosses);

            SimulationRoadCar car = new SimulationRoadCar(1,
                    testCase.v,
                    -1,
                    DEFAULT_LENGTH - testCase.s1,
                    new TurnPath(-1, new CrossTurn[]{CrossTurn.STRAIGHT}),
                    0,
                    -1,
                    false);
            ArrayDeque<SimulationRoadCar> road0Channel = simulationGraph.roads.get(new DirectedRoadId(1, false)).channels.get(0);
            road0Channel.addFirst(car);
            HashMap<Integer, SimulationRoadCar> roadCars = new HashMap<>();
            Assertions.assertNull(simulationGraph.simulateOneTimePeriod(new TrafficSimulationGraph.SimulationStats(
                    false, new LinkedList<>(), roadCars, new ArrayList<>(), -1, false, -1)));
            if (road0Channel.contains(car)) {
                Assertions.assertEquals(DEFAULT_LENGTH - 1, car.getPosition());
                Assertions.assertEquals(0, testCase.s2);
            } else
                Assertions.assertEquals(testCase.s2, car.getPosition(), "Test case: " + testCase.index);
        }
    }
}
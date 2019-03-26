package com.huawei.test;

import com.huawei.data.*;
import com.huawei.simulation.TrafficSimulationGraph;
import com.huawei.simulation.TurnPath;
import com.huawei.util.DataIoUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CheckPathConversion {
    public static void main(String[] args) throws IOException {
        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];

        List<Car> cars = DataIoUtils.readCars(carPath);
        List<Cross> crosses = DataIoUtils.readCrosses(crossPath);
        List<Road> roads = DataIoUtils.readRoads(roadPath);
        List<Answer> answers = DataIoUtils.readAnswers(answerPath);

        Map<Integer, Integer> carFroms = cars.stream().collect(Collectors.toMap(Car::getId, Car::getFrom));

        TrafficSimulationGraph simulationGraph = new TrafficSimulationGraph(roads, crosses);
        for (Answer answer : answers) {
            Path path = answer.getPath();
            int from = carFroms.get(answer.getCarId());
            TurnPath turnPath = simulationGraph.convertPathToTurnPath(from, path);
            if (path.equals(simulationGraph.convertTurnPathToPath(from, turnPath)))
                System.out.println("Path and turn path conversion test succeeds: " + answer + ", " + turnPath);
            else
                throw new AssertionError("Path and turn path conversion test fails: " + answer + ", " + turnPath);
        }
    }
}

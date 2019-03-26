package com.huawei.test;

import com.huawei.data.Answer;
import com.huawei.data.Car;
import com.huawei.data.Cross;
import com.huawei.data.Road;
import com.huawei.simulation.CarStartTimeTurnPathSingleSolution;
import com.huawei.simulation.FullSimulationResult;
import com.huawei.simulation.TrafficSimulationGraph;
import com.huawei.util.DataIoUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SimulateAnswer {
    public static void main(String[] args) throws IOException {

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];

        List<Car> cars = DataIoUtils.readCars(carPath);
        List<Road> roads = DataIoUtils.readRoads(roadPath);
        List<Cross> crosses = DataIoUtils.readCrosses(crossPath);
        List<Answer> answers = DataIoUtils.readAnswers(answerPath);

        TrafficSimulationGraph simulationGraph = new TrafficSimulationGraph(roads, crosses);
        Map<Integer, Car> carMap = cars.stream().collect(Collectors.toMap(Car::getId, Function.identity()));
        List<CarStartTimeTurnPathSingleSolution> singleSolutions = answers.stream().map(answer -> {
            Car car = carMap.get(answer.getCarId());
            return new CarStartTimeTurnPathSingleSolution(car, answer.getStartTime(),
                    simulationGraph.convertPathToTurnPath(car.getFrom(), answer.getPath()));
        }).collect(Collectors.toList());

        for (CarStartTimeTurnPathSingleSolution singleSolution : singleSolutions)
            System.out.println("startTime - planTime: " + (singleSolution.startTime - singleSolution.getCar().getPlanTime()));

        FullSimulationResult fullSimulationResult = simulationGraph.simulateAeap(singleSolutions);
        System.out.println("Status code: " + fullSimulationResult.getStatusCode() + " , system schedule time: " + fullSimulationResult.getSystemScheduleTime());
    }
}

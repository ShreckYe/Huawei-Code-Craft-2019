package com.huawei;

import com.huawei.data.Answer;
import com.huawei.data.Car;
import com.huawei.data.Cross;
import com.huawei.data.Road;
import com.huawei.simulation.CarStartTimeTurnPathSingleSolution;
import com.huawei.simulation.FullSimulationResult;
import com.huawei.simulation.TrafficSimulationGraph;
import com.huawei.util.DataIoUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SimulateSinglePath {
    public static void main(String[] args) throws IOException {
        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];

        List<Car> cars = DataIoUtils.readCars(carPath);
        List<Road> roads = DataIoUtils.readRoads(roadPath);
        List<Cross> crosses = DataIoUtils.readCrosses(crossPath);
        List<Answer> answers = DataIoUtils.readAnswers(answerPath);

        System.out.println("Enter car ID: ");
        int carId = new Scanner(System.in).nextInt();
        Car car = cars.stream().filter(c -> c.getId() == carId).findFirst().get();
        Answer answer = answers.stream().filter(a -> a.getCarId() == carId).findFirst().get();

        TrafficSimulationGraph trafficSimulationGraph = new TrafficSimulationGraph(roads, crosses);
        CarStartTimeTurnPathSingleSolution singleSolution = new CarStartTimeTurnPathSingleSolution(car, answer.getStartTime(),
                trafficSimulationGraph.convertPathToTurnPath(car.getFrom(), answer.getPath()));
        ArrayList<CarStartTimeTurnPathSingleSolution> singleSolutions = new ArrayList<>(1);
        singleSolutions.add(singleSolution);
        FullSimulationResult fullSimulationResult = trafficSimulationGraph.simulateAeap(singleSolutions);

        System.out.println("Status code: " + fullSimulationResult.getStatusCode() +
                ", system schedule time: " + fullSimulationResult.getSystemScheduleTime() +
                ", total travel time: " + fullSimulationResult.getTotalTravelTime());
    }
}

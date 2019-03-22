package com.huawei;

import com.huawei.common.Pair;
import com.huawei.data.*;
import com.huawei.graph.CarRoadGraph;
import com.huawei.graph.DirectedRoad;
import com.huawei.simulation.CarRoadSimulationGraph;
import com.huawei.simulation.CarSimulationResult;
import com.huawei.simulation.PathCrossTurns;
import com.huawei.simulation.SimulationResult;
import com.huawei.util.DataIoUtils;
import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        if (args.length != 4) {
            logger.error("please input args: inputFilePath, resultFilePath");
            return;
        }

        logger.info("Start...");

        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String answerPath = args[3];
        logger.info("carPath = " + carPath + " roadPath = " + roadPath + " crossPath = " + crossPath + " and answerPath = " + answerPath);

        // TODO:read input files
        logger.info("start read input files");

        List<Car> cars = DataIoUtils.readCars(carPath);
        List<Cross> crosses = DataIoUtils.readCrosses(crossPath);
        List<Road> roads = DataIoUtils.readRoads(roadPath);

        // TODO: calc
        CarRoadSimulationGraph simulationGraph = new CarRoadSimulationGraph(roads, crosses);

        List<Pair<Car, Path>> carPathPairs = new ArrayList<>(cars.size());
        for (Car car : cars) {
            CarRoadGraph carRoadGraph = new CarRoadGraph(crosses, roads, car);
            GraphPath<Integer, DirectedRoad> shortestPath = carRoadGraph.dijkstraShortestPath(car.getFrom(), car.getTo());
            Path path = new Path(shortestPath.getEdgeList().stream().mapToInt(DirectedRoad::getId).toArray());

            carPathPairs.add(new Pair<>(car, path));
        }
        List<Pair<Car, PathCrossTurns>> carPathCrossTurns = simulationGraph.convertCarPathListToCarTurnsList(carPathPairs);
        SimulationResult simulationResult = simulationGraph.simulateAeap(carPathCrossTurns);
        switch (simulationResult.getStatusCode()) {
            case SimulationResult.STATUS_SUCCESS:
                logger.info("AEAP simulation success");
                break;
            case SimulationResult.STATUS_DEADLOCK:
                logger.info("AEAP simulation deadlock");
                System.out.println(simulationResult);
                return;
            default:
                throw new AssertionError();
        }


        Map<Integer, Integer> startTimes = simulationResult.getCarSimulationResults().stream()
                .collect(Collectors.toMap(CarSimulationResult::getCarId, CarSimulationResult::getStartTime));
        List<Answer> answers = carPathPairs.stream().map(carPathPair -> {
            int carId = carPathPair.getFirst().getId();
            return new Answer(carId, startTimes.get(carId), carPathPair.getSecond());
        }).collect(Collectors.toList());


        // TODO: write answer.txt
        logger.info("Start write output file");
        DataIoUtils.writeAnswers(answers, answerPath);
        logger.info("End...");
    }
}
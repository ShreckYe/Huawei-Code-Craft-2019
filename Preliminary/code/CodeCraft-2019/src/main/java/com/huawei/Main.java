package com.huawei;

import com.huawei.common.Pair;
import com.huawei.data.*;
import com.huawei.graph.CarRoadGraph;
import com.huawei.graph.DirectedRoadId;
import com.huawei.optimization.InitialSolutions;
import com.huawei.simulation.CarStartTimeTurnPathSingleSolution;
import com.huawei.simulation.IdealPathResult;
import com.huawei.simulation.TrafficSimulationGraph;
import com.huawei.util.DataIoUtils;
import org.apache.log4j.Logger;
import org.jgrapht.GraphPath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        List<Road> roads = DataIoUtils.readRoads(roadPath);
        List<Cross> crosses = DataIoUtils.readCrosses(crossPath);

        // TODO: calc
        TrafficSimulationGraph simulationGraph = new TrafficSimulationGraph(roads, crosses);

        List<Pair<Car, IdealPathResult>> carIdealPathResults = new ArrayList<>(cars.size());
        for (Car car : cars) {
            CarRoadGraph carRoadGraph = new CarRoadGraph(crosses, roads, car);
            GraphPath<Integer, DirectedRoadId> shortestPath = carRoadGraph.dijkstraShortestPath(car.getFrom(), car.getTo());
            Path path = new Path(shortestPath.getEdgeList().stream().mapToInt(DirectedRoadId::getRoadId).toArray());

            carIdealPathResults.add(new Pair<>(car, new IdealPathResult(car.getPlanTime() + shortestPath.getWeight(), path)));
        }
        /*List<Pair<Car, TurnPath>> carPathCrossTurns = simulationGraph.convertCarPathToCarTurnPath(carPathPairs);
        FullSimulationResult simulationResult = simulationGraph.simulateAeapWithPlanTimes(carPathCrossTurns);
        switch (simulationResult.getStatusCode()) {
            case FullSimulationResult.STATUS_SUCCESS:
                logger.info("AEAP simulation success");
                break;
            case FullSimulationResult.STATUS_DEADLOCK:
                logger.info("AEAP simulation deadlock at: " + simulationResult.getSystemScheduleTime());
                logger.info(simulationResult);
                throw new RuntimeException("Deadlock TODO");
            default:
                throw new AssertionError();
        }

        Map<Integer, Integer> startTimes = simulationResult.getCarSimulationResults().stream()
                .collect(Collectors.toMap(CarSimulationResult::getCarId, CarSimulationResult::getStartTime));*/
        List<CarStartTimeTurnPathSingleSolution> carStartTimeTurnPaths = InitialSolutions.determineSuccessfulStartTimesByDivideAndMerge(simulationGraph, carIdealPathResults);

        System.out.println(simulationGraph.simulateAeap(carStartTimeTurnPaths).getSystemScheduleTime());

        List<Answer> answers = carStartTimeTurnPaths.stream().map(carStartTimeTurnPath -> {
            Car car = carStartTimeTurnPath.getCar();
            return new Answer(car.getId(),
                    carStartTimeTurnPath.startTime,
                    simulationGraph.convertTurnPathToPath(car.getFrom(), carStartTimeTurnPath.turnPath));
        }).collect(Collectors.toList());


        // TODO: write answer.txt
        logger.info("Start write output file");
        DataIoUtils.writeAnswers(answers, answerPath);
        logger.info("End...");
    }
}
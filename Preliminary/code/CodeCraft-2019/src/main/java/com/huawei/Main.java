package com.huawei;

import com.huawei.common.IntObjPair;
import com.huawei.common.Pair;
import com.huawei.data.*;
import com.huawei.graph.CarRoadGraph;
import com.huawei.graph.DirectedRoadId;
import com.huawei.optimization.InitialSolutions;
import com.huawei.simulation.IdealPathResult;
import com.huawei.simulation.TrafficSimulationGraph;
import com.huawei.simulation.TurnPath;
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
        List<Cross> crosses = DataIoUtils.readCrosses(crossPath);
        List<Road> roads = DataIoUtils.readRoads(roadPath);

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
        SimulationResult simulationResult = simulationGraph.simulateAeapWithPlanTimes(carPathCrossTurns);
        switch (simulationResult.getStatusCode()) {
            case SimulationResult.STATUS_SUCCESS:
                logger.info("AEAP simulation success");
                break;
            case SimulationResult.STATUS_DEADLOCK:
                logger.info("AEAP simulation deadlock at: " + simulationResult.getSystemScheduleTime());
                logger.info(simulationResult);
                throw new RuntimeException("Deadlock TODO");
            default:
                throw new AssertionError();
        }

        Map<Integer, Integer> startTimes = simulationResult.getCarSimulationResults().stream()
                .collect(Collectors.toMap(CarSimulationResult::getCarId, CarSimulationResult::getStartTime));*/
        List<Pair<Car, IntObjPair<TurnPath>>> carStartTimeTurnPaths = InitialSolutions.determineSuccessfulStartTimesWithPath(simulationGraph, carIdealPathResults);

        List<Answer> answers = carStartTimeTurnPaths.stream().map(carStartTimeTurnPath -> {
            Car car = carStartTimeTurnPath.getFirst();
            return new Answer(car.getId(),
                    carStartTimeTurnPath.getSecond().getFirst(),
                    simulationGraph.convertTurnPathToPath(car.getFrom(), carStartTimeTurnPath.getSecond().getSecond()));
        }).collect(Collectors.toList());


        // TODO: write answer.txt
        logger.info("Start write output file");
        DataIoUtils.writeAnswers(answers, answerPath);
        logger.info("End...");
    }
}
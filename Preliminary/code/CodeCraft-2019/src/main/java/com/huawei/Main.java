package com.huawei;

import com.huawei.data.Answer;
import com.huawei.data.Car;
import com.huawei.data.Cross;
import com.huawei.data.Road;
import com.huawei.graph.CarRoadGraph;
import com.huawei.graph.DirectedRoad;
import com.huawei.util.DataUtils;
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

        List<Car> cars = DataUtils.readCars(carPath);
        List<Cross> crosses = DataUtils.readCrosses(crossPath);
        List<Road> roads = DataUtils.readRoads(roadPath);

        // TODO: calc
        List<Answer> answers = new ArrayList<>(cars.size());
        for (Car car : cars) {
            CarRoadGraph carRoadGraph = new CarRoadGraph(crosses, roads, car);
            GraphPath<Integer, DirectedRoad> shortestPath = carRoadGraph.dijkstraShortestPath(car.getFrom(), car.getTo());

            answers.add(new Answer(car.getId(), car.getFrom(), shortestPath.getEdgeList().stream().map(DirectedRoad::getId).collect(Collectors.toList())));
        }

        // TODO: write answer.txt
        logger.info("Start write output file");
        DataUtils.writeAnswers(answers, answerPath);
        logger.info("End...");
    }
}
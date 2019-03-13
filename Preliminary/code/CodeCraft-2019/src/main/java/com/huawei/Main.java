package com.huawei;

import com.huawei.data.Answer;
import com.huawei.data.Car;
import com.huawei.data.Cross;
import com.huawei.data.Road;
import com.huawei.util.DataUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

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
        for (int i = 0; i < 5; i++) {
            logger.info(cars.get(i));
        }
        List<Cross> crosses = DataUtils.readCrosses(crossPath);
        for (int i = 0; i < 5; i++) {
            logger.info(crosses.get(i));
        }
        List<Road> roads = DataUtils.readRoads(roadPath);
        for (int i = 0; i < 5; i++) {
            logger.info(roads.get(i));
        }
        List<Answer> answers = DataUtils.readAnswers(answerPath);
        for (int i = 0; i < 5; i++) {
            logger.info(answers.get(i));
        }

        // TODO: calc

        // TODO: write answer.txt
        logger.info("Start write output file");

        logger.info("End...");
    }
}
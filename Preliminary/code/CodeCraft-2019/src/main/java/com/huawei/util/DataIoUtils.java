package com.huawei.util;

import com.huawei.data.Answer;
import com.huawei.data.Car;
import com.huawei.data.Cross;
import com.huawei.data.Road;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class DataIoUtils {
    //private static final Logger logger = Logger.getLogger(DataIoUtils.class);

    private DataIoUtils() {
        throw new AssertionError();
    }

    interface TupleParser<T> {
        T parse(int[] tuple);
    }

    private static <T> List<T> readTuples(String path, TupleParser<T> tupleParser) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {

            ArrayList<T> list = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0) == '#') continue;
                if (line.charAt(0) != '(' || line.charAt(line.length() - 1) != ')')
                    throw new MalformedTupleException(line);

                String[] tupleComponents = line.substring(1, line.length() - 1).split(",");
                int[] tuple = new int[tupleComponents.length];
                for (int i = 0; i < tupleComponents.length; i++) {
                    tuple[i] = Integer.parseInt(tupleComponents[i].trim());
                }

                list.add(tupleParser.parse(tuple));
            }

            return list;
        }
    }

    public static List<Car> readCars(String carPath) throws IOException {
        try {
            return readTuples(carPath, Car::new);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<Cross> readCrosses(String crossPath) throws IOException {
        try {
            return readTuples(crossPath, Cross::new);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<Road> readRoads(String roadPath) throws IOException {
        try {
            return readTuples(roadPath, Road::new);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /*public static List<Answer> readAnswers(String answerPath) throws IOException {
        try {
            return readTuples(answerPath, Answer::fromTuple);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        }
    }*/

    public static void writeAnswers(List<Answer> answers, String answerPath) throws IOException {
        try (FileWriter writer = new FileWriter(answerPath)) {
            for (Answer answer : answers) {
                writer.write(DataUtils.tupleToString(answer.toTuple()));
                writer.write('\n');
            }

        }
    }
}

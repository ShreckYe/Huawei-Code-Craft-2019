package com.huawei.util;

import com.huawei.data.Car;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DataUtils {
    private DataUtils() {
        throw new AssertionError();
    }

    interface TupleParser<T> {
        T parse(String[] tupleElements);
    }

    private static <T> List<T> readTuples(String path, TupleParser<T> tupleParser) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

        ArrayList<T> list = new ArrayList<>();
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            if (line.charAt(0) == '#') break;
            if (line.charAt(0) != '(' || line.charAt(line.length() - 1) != ')') throw new MalformedTupleException();
            String[] tupleElements = line.substring(1, line.length() - 1).split(",");

            list.add(tupleParser.parse(tupleElements));
        }

        return list;
    }

    public static List<Car> readCars(String carPath) throws IOException {
        try {
            return readTuples(carPath, tupleElements -> new Car(Integer.parseInt(tupleElements[0]), Integer.parseInt(tupleElements[1]),
                    Integer.parseInt(tupleElements[2]), Integer.parseInt(tupleElements[3]), Integer.parseInt(tupleElements[4])));
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

package com.huawei.simulation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.stream.Collectors;

class SimulationVisualizationUtils {
    private SimulationVisualizationUtils() {
    }

    static void printRoad(SimulationRoad road) {
        String fromIdString = String.valueOf(road.from.crossId),
                toIdString = String.valueOf(road.to.crossId),
                roadIdString = String.valueOf(road.directedRoadId.getRoadId());
        System.out.print(fromIdString);
        System.out.print(' ');
        int roadIdDif = road.length - roadIdString.length();
        if (roadIdDif < 0)
            System.out.print(roadIdString);
        else {
            int roadIdLeftMargin = roadIdDif / 2;
            for (int i = 0; i < roadIdLeftMargin; i++)
                System.out.print(' ');
            System.out.print(roadIdString);
            int roadIdRightMargin = roadIdDif - roadIdLeftMargin;
            for (int i = 0; i < roadIdRightMargin; i++)
                System.out.print(' ');
        }
        System.out.print(' ');
        System.out.println(toIdString);

        String fromSpaces = String.join("", Collections.nCopies(fromIdString.length() + 1, " "));


        ArrayList<SimulationRoadCar> waitingCars = new ArrayList<>();
        for (ArrayDeque<SimulationRoadCar> channel : road.channels) {
            System.out.print(fromSpaces);
            Iterator<SimulationRoadCar> carIterator = channel.descendingIterator();
            SimulationRoadCar currentCar = carIterator.hasNext() ? carIterator.next() : null;
            for (int i = 0; i < road.length; i++) {
                if (currentCar != null && i == currentCar.position) {
                    if (currentCar.waiting) {
                        System.out.print('w');
                        waitingCars.add(currentCar);
                    } else
                        System.out.print('s');
                    currentCar = carIterator.hasNext() ? carIterator.next() : null;
                } else if (currentCar != null && i > currentCar.position) {
                    // TODO: this check can be moved to a separate function
                    throw new IllegalArgumentException("Cars in the same location or not in order: \n" +
                            "positions: " + channel.stream().map(car -> car.position).collect(Collectors.toList()) + ",\n" +
                            "cars IDs: " + channel.stream().map(car -> car.carId).collect(Collectors.toList()));
                } else
                    System.out.print('-');
            }
            System.out.println();
        }

        for (SimulationRoadCar waitingCar : waitingCars)
            System.out.println(waitingCar);

        System.out.println();
    }

    static void printRoads(Iterable<SimulationRoad> roads) {
        for (SimulationRoad road : roads)
            printRoad(road);
    }

    static void printDeadlockRing(SimulationRoadCar car, SimulationRoad road) {
        // TODO
    }
}

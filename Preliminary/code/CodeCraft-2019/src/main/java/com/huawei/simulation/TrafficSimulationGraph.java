package com.huawei.simulation;

import com.huawei.common.ArrayUtils;
import com.huawei.common.IntObjPair;
import com.huawei.common.Pair;
import com.huawei.data.Car;
import com.huawei.data.Cross;
import com.huawei.data.Path;
import com.huawei.data.Road;
import com.huawei.graph.DirectedRoadId;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.huawei.simulation.CrossDirections.getDirectionIn;
import static com.huawei.simulation.CrossDirections.getDirectionOut;
import static java.lang.Math.min;

public class TrafficSimulationGraph {
    // Use LinkedHashMap to preserve insertion order
    // Roads not necessarily sorted
    LinkedHashMap<DirectedRoadId, SimulationRoad> roads;
    // Crosses sorted by ID
    LinkedHashMap<Integer, SimulationCross> crosses;

    private SimulationRoad getRoadInFromCross(int crossId, Map<Integer, Road> roadRecordMap, int roadId) {
        return roadId == -1 ? null : roads.get(new DirectedRoadId(roadId, crossId != roadRecordMap.get(roadId).getTo()));
    }

    private SimulationRoad getRoadOutFromCross(int crossId, Map<Integer, Road> roadRecordMap, int roadId) {
        return roadId == -1 ? null : roads.get(new DirectedRoadId(roadId, crossId != roadRecordMap.get(roadId).getFrom()));
    }

    public TrafficSimulationGraph(List<Road> roadRecords, List<Cross> crossRecords) {
        roads = new LinkedHashMap<>(roadRecords.size());
        for (Road roadRecord : roadRecords) {
            DirectedRoadId directedRoadId = new DirectedRoadId(roadRecord.getId(), false);
            roads.put(directedRoadId, new SimulationRoad(directedRoadId, roadRecord.getLength(), roadRecord.getSpeed(), roadRecord.getChannel()));
            if (roadRecord.isDuplex()) {
                DirectedRoadId reverseDirectedRoadId = new DirectedRoadId(roadRecord.getId(), true);
                roads.put(reverseDirectedRoadId, new SimulationRoad(reverseDirectedRoadId, roadRecord.getLength(), roadRecord.getSpeed(), roadRecord.getChannel()));
            }
        }

        Map<Integer, Road> roadRecordMap = roadRecords.stream().collect(Collectors.toMap(Road::getId, Function.identity()));

        crosses = new LinkedHashMap<>(crossRecords.size());
        for (Cross crossRecord : crossRecords.stream().sorted(Comparator.comparingInt(Cross::getId)).collect(Collectors.toList())) {
            SimulationRoad roadInNorth = getRoadInFromCross(crossRecord.getId(), roadRecordMap, crossRecord.getRoadIdNorth()),
                    roadInEast = getRoadInFromCross(crossRecord.getId(), roadRecordMap, crossRecord.getRoadIdEast()),
                    roadInSouth = getRoadInFromCross(crossRecord.getId(), roadRecordMap, crossRecord.getRoadIdSouth()),
                    roadInWest = getRoadInFromCross(crossRecord.getId(), roadRecordMap, crossRecord.getRoadIdWest()),

                    roadOutNorth = getRoadOutFromCross(crossRecord.getId(), roadRecordMap, crossRecord.getRoadIdNorth()),
                    roadOutEast = getRoadOutFromCross(crossRecord.getId(), roadRecordMap, crossRecord.getRoadIdEast()),
                    roadOutSouth = getRoadOutFromCross(crossRecord.getId(), roadRecordMap, crossRecord.getRoadIdSouth()),
                    roadOutWest = getRoadOutFromCross(crossRecord.getId(), roadRecordMap, crossRecord.getRoadIdWest());
            SimulationRoad[] roadsIn = new SimulationRoad[]{roadInNorth, roadInEast, roadInSouth, roadInWest},
                    roadsOut = new SimulationRoad[]{roadOutNorth, roadOutEast, roadOutSouth, roadOutWest};
            SimulationCross cross = new SimulationCross(crossRecord.getId(), roadsIn, roadsOut);

            for (SimulationRoad road : roadsIn)
                if (road != null)
                    road.to = cross;
            for (SimulationRoad road : roadsOut)
                if (road != null)
                    road.from = cross;

            crosses.put(cross.crossId, cross);
        }
    }

    private void clearSimulation() {
        for (SimulationRoad road : roads.values())
            for (ArrayDeque<SimulationRoadCar> channel : road.channels)
                channel.clear();
    }

    private enum ScheduleToNewRoadResult {SUCCESS, FRONT_CAR_WAITING, NO_MORE_SPACE}

    private static ScheduleToNewRoadResult scheduleToNewRoad(SimulationRoadCar p1wCar, SimulationRoad newRoad, int maxNewPosition) {
        for (ArrayDeque<SimulationRoadCar> channel : newRoad.channels) {
            int newPosition;
            SimulationRoadCar frontCar = channel.peekLast();
            if (frontCar == null)
                // TODO: assuming road length always >= speed, may cause problems if not
                newPosition = maxNewPosition;
            else if (maxNewPosition < frontCar.getPosition())
                newPosition = maxNewPosition;
            else if (!frontCar.isWaiting())
                newPosition = frontCar.getPosition() - 1;
            else
                return ScheduleToNewRoadResult.FRONT_CAR_WAITING;

            if (newPosition >= 0) {
                channel.addLast(p1wCar);
                p1wCar.currentPathIndex++;
                p1wCar.scheduleToPosition(newPosition);
                return ScheduleToNewRoadResult.SUCCESS;
            }
        }

        return ScheduleToNewRoadResult.NO_MORE_SPACE;
    }

    /*private interface CantStartCallback {
        void onCantStart(SimulationGarageCar car);
    }*/

    // Simulate in the condition that a car starts as early as possible if it can't start at start time
    private FullSimulationResult simulate(List<CarStartTimeTurnPathSingleSolution> singleSolutions, boolean failOnCantStart) {
        LinkedList<SimulationGarageCar> garageCarsSortedByStartTimeAndId = singleSolutions.stream()
                .map(solution -> new SimulationGarageCar(solution.car, solution.startTime, solution.turnPath))
                .sorted(Comparator.comparingInt((ToIntFunction<SimulationGarageCar>) car -> car.startTime)
                        .thenComparingInt(car -> car.id))
                .collect(Collectors.toCollection(LinkedList::new));
        HashMap<Integer, SimulationRoadCar> roadCars = new HashMap<>(singleSolutions.size());

        OptionalInt optionalMinPlanTime = garageCarsSortedByStartTimeAndId.stream().mapToInt(car -> car.startTime).min();
        if (!optionalMinPlanTime.isPresent())
            return FullSimulationResult.newSuccessInstance(0, 0, Collections.emptyList());

        List<CarSimulationResult> carSimulationResults = new ArrayList<>(singleSolutions.size());

        int minPlanTime = optionalMinPlanTime.getAsInt();

        int time;
        boolean cantStartOnTime = false;
        int totalTravelTime = 0;
        for (time = minPlanTime; !(garageCarsSortedByStartTimeAndId.isEmpty() && roadCars.isEmpty()); time++) {
            // Schedule cars in the time period from time to time + 1
            // No need to mark them beforehand because they will be marked when scheduling cars on roads
            /*for (SimulationRoadCar roadCar : roadCars)
                roadCar.waiting = true;*/

            // Schedule cars on roads
            for (SimulationRoad road : roads.values())
                for (ArrayDeque<SimulationRoadCar> channel : road.channels) {
                    SimulationRoadCar frontCar = null;
                    for (SimulationRoadCar car : channel) {
                        int maxNewPosition = car.getPosition() + min(road.speed, car.speed);
                        if (frontCar == null) {
                            if (maxNewPosition < road.length)
                                car.scheduleToPositionWhenSchedulingRoad(maxNewPosition);
                            else
                                car.setWaitingWhenSchedulingRoad();
                        } else {
                            if (maxNewPosition < frontCar.getPosition())
                                car.scheduleToPositionWhenSchedulingRoad(maxNewPosition);
                            else if (!frontCar.isWaiting())
                                car.scheduleToPositionWhenSchedulingRoad(frontCar.getPosition() - 1);
                            else
                                car.setWaitingWhenSchedulingRoad();
                        }
                        frontCar = car;
                    }
                }

            // Schedule cars waiting at crosses
            boolean existsCarScheduled;
            do {
                existsCarScheduled = false;
                // Breaks when all cars are scheduled or no more cars can be scheduled any more, which is a dead lock
                for (SimulationCross cross : crosses.values()) {
                    boolean existsCrossCarScheduled;
                    do {
                        // Breaks when all cars at the cross are scheduled or no more car can be scheduled any more without outer change
                        existsCrossCarScheduled = false;
                        for (SimulationRoadWithCrossDirection roadAndDirection : cross.roadsInSortedById) {
                            // Schedule all possible cars on this road. Efficiency could be improved with an overall queue
                            do {
                                // Breaks when there is no priority one waiting car or it can't be scheduled due to turn conflict or new road not schedulable (front car waiting or all channels full)
                                // Find the priority one waiting car
                                SimulationRoad road = roadAndDirection.road;
                                Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>> p1wCarChannelPair = road.getPriorityOneWaitingCarChannelPair();
                                if (p1wCarChannelPair == null) break;
                                SimulationRoadCar p1wCar = p1wCarChannelPair.getFirst();
                                ArrayDeque<SimulationRoadCar> p1wChannel = p1wCarChannelPair.getSecond();


                                CrossTurn turn = p1wCar.getCurrentTurn();
                                int directionOut = getDirectionOut(roadAndDirection.direction, turn);
                                // Check if there are cars with higher turn priority
                                if (turn.getAllWithHigherPriority().stream().anyMatch(higherPriorityTurn -> {
                                    int higherPriorityInDirection = getDirectionIn(directionOut, higherPriorityTurn);
                                    SimulationRoad roadIn = cross.roadsIn[higherPriorityInDirection];
                                    if (roadIn == null) return false;
                                    Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>> hpidP1wPair = roadIn.getPriorityOneWaitingCarChannelPair();
                                    if (hpidP1wPair == null) return false;
                                    SimulationRoadCar hpidP1wCar = hpidP1wPair.getFirst();
                                    return hpidP1wCar.getCurrentTurn() == higherPriorityTurn;
                                }))
                                    break;


                                // Finally we can try to schedule this car
                                boolean skipFirstInChannel;
                                if (p1wCar.isArriving()) {
                                    // Arrives at destination
                                    p1wChannel.removeFirst();
                                    roadCars.remove(p1wCar.carId);
                                    // Missed this!!!
                                    existsCrossCarScheduled = true;
                                    //int arriveTime = time + 1;
                                    totalTravelTime += time - p1wCar.planTime;
                                    carSimulationResults.add(new CarSimulationResult(p1wCar.carId, p1wCar.startTime, time, p1wCar.turnPath, null, null));
                                    skipFirstInChannel = false;
                                } else {
                                    SimulationRoad roadOut = cross.roadsOut[directionOut];
                                    int s1P1 = road.length - p1wCar.getPosition(),
                                            v1 = min(road.speed, p1wCar.speed),
                                            v2 = min(roadOut.speed, p1wCar.speed);
                                    //boolean b1 = s1P1 > v1, b2 = s1P1 > v2;
                                    if (s1P1 > v1) {
                                        p1wCar.scheduleToPosition(p1wCar.getPosition() + v1);
                                        existsCrossCarScheduled = true;
                                        skipFirstInChannel = true;
                                    } else if (s1P1 > v2) {
                                        p1wCar.scheduleToPosition(road.length - 1);
                                        existsCrossCarScheduled = true;
                                        skipFirstInChannel = true;
                                    } else {
                                        // Schedule to next road
                                        int maxNewPosition = v2 - s1P1;
                                        ScheduleToNewRoadResult result = scheduleToNewRoad(p1wCar, roadOut, maxNewPosition);
                                        if (result == ScheduleToNewRoadResult.SUCCESS) {
                                            p1wChannel.removeFirst();
                                            existsCrossCarScheduled = true;
                                            skipFirstInChannel = false;
                                        } else if (result == ScheduleToNewRoadResult.NO_MORE_SPACE) {
                                            p1wCar.scheduleToPosition(road.length - 1);
                                            existsCrossCarScheduled = true;
                                            skipFirstInChannel = true;
                                        } else /*if (result == ScheduleToNewRoadResult.FRONT_CAR_WAITING)*/
                                            break;
                                    }
                                }

                                // Schedule the corresponding channel
                                Iterator<SimulationRoadCar> channelCars = p1wChannel.iterator();
                                SimulationRoadCar frontCar;
                                if (skipFirstInChannel) {
                                    channelCars.next();
                                    frontCar = p1wCar;
                                } else
                                    frontCar = null;
                                while (channelCars.hasNext()) {
                                    SimulationRoadCar car = channelCars.next();
                                    if (!car.isWaiting())
                                        break;

                                    int maxNewPosition = car.getPosition() + min(road.speed, car.speed);
                                    if (frontCar == null) {
                                        if (maxNewPosition < road.length) {
                                            car.scheduleToPosition(maxNewPosition);
                                            existsCrossCarScheduled = true;
                                        } else
                                            break;
                                    } else {
                                        // The front car can never be waiting
                                        car.scheduleToPosition(min(maxNewPosition, frontCar.getPosition() - 1));
                                        existsCrossCarScheduled = true;
                                    }
                                    frontCar = car;
                                }
                            } while (false);
                        }

                        if (existsCrossCarScheduled)
                            existsCarScheduled = true;
                    } while (false);
                }
            } while (existsCarScheduled);

            if (roadCars.values().stream().anyMatch(SimulationRoadCar::isWaiting)) {
                // TODO: remove debug code
                System.out.println("Deadlock cars: \ncarId currentPathIndex position roadId reverseDirection channelNumber");
                roadCars.values().stream().filter(SimulationRoadCar::isWaiting).forEach(car -> {
                    Pair<SimulationRoad, IntObjPair<ArrayDeque<SimulationRoadCar>>> roadChannelPair = roads.values().stream()
                            .flatMap(road -> IntStream.range(0, road.numberOfChannels).mapToObj(i -> new Pair<>(road, new IntObjPair<>(i, road.channels.get(i)))))
                            .filter(pair -> pair.getSecond().getSecond().contains(car)).findFirst().get();
                    System.out.println(car.carId + " " + car.currentPathIndex + " " + car.getPosition() + " " + roadChannelPair.getFirst().directedRoadId.getRoadId() + " " + roadChannelPair.getFirst().directedRoadId.isReverseDirection() + " " + roadChannelPair.getSecond().getFirst());
                });
                System.out.println("\n\nAll roads:");
                SimulationVisualizationUtils.printRoads(roads.values());
                clearSimulation();
                return FullSimulationResult.newDeadlockInstance(time, totalTravelTime, carSimulationResults);
            }

            Iterator<SimulationGarageCar> garageCarIterator = garageCarsSortedByStartTimeAndId.iterator();
            while (garageCarIterator.hasNext()) {
                SimulationGarageCar garageCar = garageCarIterator.next();
                if (garageCar.startTime > time)
                    break;

                // Use default order: first by start time then by ID
                // TODO: may violate the rule
                SimulationRoad road = crosses.get(garageCar.from).roadsOut[garageCar.path.firstDirection];
                int speed = min(road.speed, garageCar.speed);

                SimulationRoadCar car = new SimulationRoadCar(garageCar.id, garageCar.speed, garageCar.planTime, garageCar.path, time);
                if (scheduleToNewRoad(car, road, speed - 1) == ScheduleToNewRoadResult.SUCCESS) {
                    garageCarIterator.remove();
                    roadCars.put(car.carId, car);
                } else if (failOnCantStart) {
                    clearSimulation();
                    return FullSimulationResult.newCantStartOnTimeInstance(time, totalTravelTime, carSimulationResults);
                } else
                    cantStartOnTime = true;
            }
        }

        return FullSimulationResult.newSuccessInstance(cantStartOnTime, time - 1, totalTravelTime, carSimulationResults);
    }

    public FullSimulationResult simulateAeap(List<CarStartTimeTurnPathSingleSolution> singleSolutions) {
        return simulate(singleSolutions, false);
    }

    public FullSimulationResult simulateAeapWithPlanTimes(List<Pair<Car, TurnPath>> carPaths) {
        return simulateAeap(SimulationDataUtils.carPathsToSingleSolutions(carPaths));
    }

    public FullSimulationResult simulateStrict(List<CarStartTimeTurnPathSingleSolution> singleSolutions) {
        return simulate(singleSolutions, true);
    }

    public TurnPath convertPathToTurnPath(int from, Path path) {
        int[] roadIds = path.getRoadIds();
        int crossTurnNumber = roadIds.length - 1;
        CrossTurn[] crossTurns = new CrossTurn[crossTurnNumber];

        SimulationCross cross = crosses.get(from);
        int firstDirection = ArrayUtils.indexOf(cross.roadsOut, road -> road != null && road.directedRoadId.getRoadId() == roadIds[0]);

        int directionOut = firstDirection;
        for (int i = 0; i < crossTurnNumber; i++) {
            // Get next cross with old data
            cross = cross.roadsOut[directionOut].to;

            int roadIdIn = roadIds[i];
            int directionIn = ArrayUtils.indexOf(cross.roadsIn, road -> road != null && road.directedRoadId.getRoadId() == roadIdIn);
            int roadIdOut = roadIds[i + 1];
            directionOut = ArrayUtils.indexOf(cross.roadsOut, road -> road != null && road.directedRoadId.getRoadId() == roadIdOut);

            crossTurns[i] = CrossTurn.getWithDirectionOffset(directionOut - directionIn);
        }

        return new TurnPath(firstDirection, crossTurns);
    }

    public List<TurnPath> convertPathToTurnPath(List<IntObjPair<Path>> fromPathPairs) {
        return fromPathPairs.stream()
                .map(fromPathPair -> convertPathToTurnPath(fromPathPair.getFirst(), fromPathPair.getSecond()))
                .collect(Collectors.toList());
    }

    public List<Pair<Car, TurnPath>> convertCarPathToCarTurnPath(List<Pair<Car, Path>> carPathPairs) {
        return carPathPairs.stream()
                .map(carPathPair -> new Pair<>(carPathPair.getFirst(), convertPathToTurnPath(carPathPair.getFirst().getFrom(), carPathPair.getSecond())))
                .collect(Collectors.toList());
    }

    public Path convertTurnPathToPath(int from, TurnPath turnPath) {
        CrossTurn[] crossTurns = turnPath.crossTurns;
        int length = crossTurns.length;
        int[] roadIds = new int[length + 1];

        SimulationCross cross = crosses.get(from);
        SimulationRoad road = cross.roadsOut[turnPath.firstDirection];
        roadIds[0] = road.directedRoadId.getRoadId();

        for (int i = 0; i < length; i++) {
            cross = road.to;
            int directionIn = -1;
            for (int j = 0; j < cross.roadsIn.length; j++)
                if (cross.roadsIn[j] == road)
                    directionIn = j;

            road = cross.roadsOut[CrossDirections.getDirectionOut(directionIn, crossTurns[i])];
            roadIds[i + 1] = road.directedRoadId.getRoadId();
        }
        return new Path(roadIds);
    }
}

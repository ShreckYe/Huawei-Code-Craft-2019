package com.huawei.simulation;

import com.huawei.common.ArrayUtils;
import com.huawei.common.IntObjPair;
import com.huawei.common.Pair;
import com.huawei.data.Car;
import com.huawei.data.Cross;
import com.huawei.data.Path;
import com.huawei.data.Road;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.huawei.simulation.SimulationUtils.getDirectionIn;
import static com.huawei.simulation.SimulationUtils.getDirectionOut;
import static java.lang.Math.min;

public class CarRoadSimulationGraph {
    private static class RoadKey {
        private int roadId;
        private boolean reverseDirection;

        private RoadKey(int roadId, boolean reverseDirection) {
            this.roadId = roadId;
            this.reverseDirection = reverseDirection;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RoadKey roadKey = (RoadKey) o;

            if (roadId != roadKey.roadId) return false;
            return reverseDirection == roadKey.reverseDirection;
        }

        @Override
        public int hashCode() {
            int result = roadId;
            result = 31 * result + (reverseDirection ? 1 : 0);
            return result;
        }
    }

    private static class SimulationRoad {
        private RoadKey roadKey;
        private int length;
        private int speed;

        private int numberOfChannels;
        // Sorted by position in descending order
        private ArrayList<ArrayDeque<SimulationRoadCar>> channels;
        private SimulationCross from, to;

        private SimulationRoad(RoadKey roadKey, int length, int speed, int numberOfChannels, SimulationCross from, SimulationCross to) {
            this.roadKey = roadKey;
            this.length = length;
            this.speed = speed;
            this.numberOfChannels = numberOfChannels;
            this.channels = new ArrayList<>(numberOfChannels);
            for (int i = 0; i < numberOfChannels; i++) channels.add(new ArrayDeque<>());
            this.from = from;
            this.to = to;
        }

        private SimulationRoad(RoadKey roadKey, int length, int speed, int numberOfChannels) {
            this(roadKey, length, speed, numberOfChannels, null, null);
        }

        /**
         * Returns the cars in the first row where there are cars
         */
        Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>> getPriorityOneWaitingCarChannelPair() {
            List<Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>>> firstRowWaitingCarChannelPairs = channels.stream().map(channel -> {
                SimulationRoadCar first = channel.peekFirst();
                return first == null ? null : first.waiting ? new Pair<>(first, channel) : null;
            }).filter(Objects::nonNull).collect(Collectors.toList());

            if (firstRowWaitingCarChannelPairs.isEmpty())
                return null;

            @SuppressWarnings("OptionalGetWithoutIsPresent") int priorityOnePosition = firstRowWaitingCarChannelPairs.stream()
                    .mapToInt(carChannelPair -> carChannelPair.getFirst().position)
                    .max().getAsInt();
            @SuppressWarnings("OptionalGetWithoutIsPresent") Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>> priorityOneWaitingCarChannelPair = firstRowWaitingCarChannelPairs.stream()
                    .filter(carChannelPair -> carChannelPair.getFirst().position == priorityOnePosition)
                    .findFirst().get();

            return priorityOneWaitingCarChannelPair;
        }
    }


    private static class SimulationGarageCar {
        // Unknown start time
        int id, from, to, speed, planTime;
        PathCrossTurns path;

        private SimulationGarageCar(int id, int from, int to, int speed, int planTime, PathCrossTurns path) {
            this.id = id;
            this.from = from;
            this.to = to;
            this.speed = speed;
            this.planTime = planTime;
            this.path = path;
        }

        private SimulationGarageCar(Car car, PathCrossTurns path) {
            this(car.getId(), car.getFrom(), car.getTo(), car.getSpeed(), car.getPlanTime(), path);
        }
    }

    private static class SimulationRoadCar {
        private int carId,
                speed,
                position;
        private PathCrossTurns path;
        private int currentPathIndex;

        private int startTime;

        // Indicates whether the car is waiting to be scheduled
        private boolean waiting;

        SimulationRoadCar(int carId, int speed, int position, PathCrossTurns path, int currentPathIndex, int startTime, boolean waiting) {
            this.carId = carId;
            this.speed = speed;
            this.position = position;
            this.path = path;
            this.currentPathIndex = currentPathIndex;
            this.startTime = startTime;
            this.waiting = waiting;
        }


        SimulationRoadCar(int carId, int speed, PathCrossTurns path, int startTime) {
            this(carId, speed, -1, path, 0, startTime, false);
        }

        /*private void scheduleTo(int newPosition) {
            waiting = false;
            position = newPosition;
        }*/

        CrossTurn getCurrentTurn() {
            return currentPathIndex == path.crossTurns.length ? null : path.crossTurns[currentPathIndex];
        }
    }

    private static class SimulationRoadWithCrossDirection {
        SimulationRoad road;
        // See CrossDirections
        // Can be either direction in (which is opposite to actual driving direction) or direction out
        int direction;

        public SimulationRoadWithCrossDirection(SimulationRoad road, int direction) {
            this.road = road;
            this.direction = direction;
        }
    }

    private static class SimulationCross {
        private int crossId;
        // 4 roads in clockwise order from north to west
        private SimulationRoad[] roadsIn, roadsOut;
        // Not null roads sorted by road ID
        private List<SimulationRoadWithCrossDirection> roadsInSortedById, roadsOutSortedById;

        private SimulationCross(int crossId, SimulationRoad[] roadsIn, SimulationRoad[] roadsOut) {
            this.crossId = crossId;
            if (roadsIn == null || roadsIn.length != 4)
                throw new IllegalArgumentException();
            this.roadsIn = roadsIn;
            roadsInSortedById = IntStream.range(0, 4)
                    .filter(direction -> roadsIn[direction] != null)
                    .mapToObj(direction -> new SimulationRoadWithCrossDirection(roadsIn[direction], direction))
                    .sorted(Comparator.comparingInt(road -> road.road.roadKey.roadId))
                    .collect(Collectors.toList());
            if (roadsOut == null || roadsOut.length != 4)
                throw new IllegalArgumentException();
            this.roadsOut = roadsOut;
            roadsOutSortedById = IntStream.range(0, 4)
                    .filter(direction -> roadsOut[direction] != null)
                    .mapToObj(direction -> new SimulationRoadWithCrossDirection(roadsOut[direction], direction))
                    .sorted(Comparator.comparingInt(road -> road.road.roadKey.roadId))
                    .collect(Collectors.toList());
        }
    }

    Map<RoadKey, SimulationRoad> roads;
    // Garage cars ordered by plan time
    Map<Integer, SimulationCross> crosses;

    private SimulationRoad getRoadInFromCross(int crossId, Map<Integer, Road> roadRecordMap, int roadId) {
        return roadId == -1 ? null : roads.get(new RoadKey(roadId, crossId != roadRecordMap.get(roadId).getTo()));
    }

    private SimulationRoad getRoadOutFromCross(int crossId, Map<Integer, Road> roadRecordMap, int roadId) {
        return roadId == -1 ? null : roads.get(new RoadKey(roadId, crossId != roadRecordMap.get(roadId).getFrom()));
    }

    public CarRoadSimulationGraph(List<Road> roadRecords, List<Cross> crossRecords) {
        roads = new HashMap<>(roadRecords.size());
        for (Road roadRecord : roadRecords) {
            RoadKey roadKey = new RoadKey(roadRecord.getId(), false);
            roads.put(roadKey, new SimulationRoad(roadKey, roadRecord.getLength(), roadRecord.getSpeed(), roadRecord.getChannel()));
            if (roadRecord.isDuplex()) {
                RoadKey reverseRoadKey = new RoadKey(roadRecord.getId(), true);
                roads.put(reverseRoadKey, new SimulationRoad(roadKey, roadRecord.getLength(), roadRecord.getSpeed(), roadRecord.getChannel()));
            }
        }

        Map<Integer, Road> roadRecordMap = roadRecords.stream().collect(Collectors.toMap(Road::getId, Function.identity()));
        crosses = crossRecords.stream()
                .collect(Collectors.toMap(Cross::getId, crossRecord -> {
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

                    return cross;
                }));
    }

    public void clearSimulation() {
        for (SimulationRoad road : roads.values())
            for (ArrayDeque<SimulationRoadCar> channel : road.channels)
                channel.clear();
    }

    private static boolean scheduleToNewRoad(SimulationRoadCar car, SimulationRoad newRoad, int maxNewPosition) {
        for (ArrayDeque<SimulationRoadCar> channel : newRoad.channels) {
            int newPosition;
            SimulationRoadCar frontCar = channel.peekLast();
            if (frontCar == null)
                // TODO: assuming road length always >= speed, may cause problems if not
                newPosition = maxNewPosition;
            else if (maxNewPosition < frontCar.position)
                newPosition = maxNewPosition;
            else if (!frontCar.waiting)
                newPosition = frontCar.position - 1;
            else
                break;

            if (newPosition >= 0) {
                channel.addLast(car);
                car.currentPathIndex++;
                car.position = newPosition;
                car.waiting = false;
                return true;
            }
        }

        return false;
    }

    // Simulate in the condition that cars start as early as possible
    public SimulationResult simulateAeap(List<Pair<Car, PathCrossTurns>> carPaths) {
        LinkedList<SimulationGarageCar> garageCarsSortedByTimeAndId = carPaths.stream()
                .map(carPath -> new SimulationGarageCar(carPath.getFirst(), carPath.getSecond()))
                .sorted(Comparator.comparingInt((ToIntFunction<SimulationGarageCar>) car -> car.planTime)
                        .thenComparingInt(value -> value.id))
                .collect(Collectors.toCollection(LinkedList::new));
        List<SimulationRoadCar> roadCars = new ArrayList<>(carPaths.size());

        OptionalInt optionalMinPlanTime = garageCarsSortedByTimeAndId.stream().mapToInt(car -> car.planTime).min();
        if (!optionalMinPlanTime.isPresent())
            return SimulationResult.newSuccessInstance(0, 0, Collections.emptyList());

        List<CarSimulationResult> carSimulationResults = new ArrayList<>(carPaths.size());

        int minPlanTime = optionalMinPlanTime.getAsInt();

        int time;
        for (time = minPlanTime; !(garageCarsSortedByTimeAndId.isEmpty() && roadCars.isEmpty()); time++) {
            // Schedule cars in the time period from time to time + 1
            // No need to mark them beforehand because they will be marked when scheduling cars on roads
            /*for (SimulationRoadCar roadCar : roadCars)
                roadCar.waiting = true;*/

            // Schedule cars on roads
            for (SimulationRoad road : roads.values())
                for (ArrayDeque<SimulationRoadCar> channel : road.channels) {
                    SimulationRoadCar frontCar = null;
                    for (SimulationRoadCar car : channel) {
                        int maxNewPosition = car.position + min(road.speed, car.speed);
                        if (frontCar == null) {
                            if (maxNewPosition < road.length) {
                                car.position = maxNewPosition;
                                car.waiting = false;
                                //frontCar = null;
                            } else {
                                car.waiting = true;
                                frontCar = car;
                            }
                        } else {
                            if (maxNewPosition < frontCar.position) {
                                car.position = maxNewPosition;
                                car.waiting = false;
                            } else if (!frontCar.waiting) {
                                car.position = frontCar.position - 1;
                                car.waiting = false;
                            } else
                                car.waiting = true;

                            frontCar = car;
                        }
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
                            while (true) {
                                // Breaks when there is no  priority one waiting car or it can't be scheduled due to turn conflict or new road not schedulable (front car waiting or all channels full)
                                // Find the priority one waiting car
                                SimulationRoad road = roadAndDirection.road;
                                Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>> p1wCarChannelPair = road.getPriorityOneWaitingCarChannelPair();
                                if (p1wCarChannelPair == null) break;
                                SimulationRoadCar p1wCar = p1wCarChannelPair.getFirst();
                                ArrayDeque<SimulationRoadCar> p1wChannel = p1wCarChannelPair.getSecond();

                                CrossTurn turn = p1wCar.getCurrentTurn();
                                if (turn == null) {
                                    // Arrives at destination
                                    p1wChannel.removeFirst();
                                    roadCars.remove(p1wCar.carId);
                                    carSimulationResults.add(new CarSimulationResult(p1wCar.carId, p1wCar.startTime, null, null));
                                    continue;
                                }
                                int directionOut = getDirectionOut(roadAndDirection.direction, turn);

                                // Check if there are cars with higher turn priority
                                if (turn.getAllWithHigherPriority().anyMatch(higherPriorityTurn -> {
                                    int higherPriorityInDirection = getDirectionIn(directionOut, higherPriorityTurn);
                                    SimulationRoad roadIn = cross.roadsIn[higherPriorityInDirection];
                                    if (roadIn == null) return false;
                                    Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>> hpidP1wPair = roadIn.getPriorityOneWaitingCarChannelPair();
                                    if (hpidP1wPair == null) return false;
                                    SimulationRoadCar hpidP1wCar = hpidP1wPair.getFirst();
                                    return hpidP1wCar.getCurrentTurn() == higherPriorityTurn;
                                })) break;

                                // Finally we can try to schedule this car
                                SimulationRoad roadOut = cross.roadsOut[directionOut];
                                int s1P1 = road.length - p1wCar.position,
                                        v1 = min(road.speed, p1wCar.speed),
                                        v2 = min(roadOut.speed, p1wCar.speed);
                                boolean b1 = s1P1 > v1, b2 = s1P1 > v2;
                                if (b1 | b2) {
                                    p1wCar.position = b1 ? p1wCar.position + v1 : road.length - 1;
                                    p1wCar.waiting = false;
                                    existsCrossCarScheduled = true;

                                    // Scheduling following cars, code copied from above and adapted
                                    Iterator<SimulationRoadCar> followingCars = p1wChannel.iterator();
                                    followingCars.next();
                                    SimulationRoadCar frontCar = p1wCar;
                                    while (followingCars.hasNext()) {
                                        SimulationRoadCar car = followingCars.next();
                                        if (car.waiting) {
                                            int maxNewPosition = car.position + min(road.speed, car.speed);
                                            car.position = min(maxNewPosition, frontCar.position - 1);
                                            car.waiting = false;
                                            existsCrossCarScheduled = true;

                                            frontCar = car;
                                        } else
                                            break;
                                    }
                                } else {
                                    // Schedule to next road
                                    int maxNewPosition = v2 - s1P1;
                                    if (scheduleToNewRoad(p1wCar, roadOut, maxNewPosition)) {
                                        p1wChannel.removeFirst();
                                        existsCrossCarScheduled = true;
                                    } else
                                        break;
                                }
                            }
                        }

                        if (existsCrossCarScheduled)
                            existsCarScheduled = true;
                    } while (existsCrossCarScheduled);
                }
            } while (existsCarScheduled);

            if (roadCars.stream().anyMatch(car -> car.waiting)) {
                // TODO: remove debug code
                System.out.println("Deadlock cars: \ncarId currentPathIndex position roadId reverseDirection channelNumber");
                roadCars.stream().filter(car -> car.waiting).forEach(car -> {
                    Pair<SimulationRoad, IntObjPair<ArrayDeque<SimulationRoadCar>>> roadChannelPair = roads.values().stream()
                            .flatMap(road -> IntStream.range(0, road.numberOfChannels).mapToObj(i -> new Pair<>(road, new IntObjPair<>(i, road.channels.get(i)))))
                            .filter(pair -> pair.getSecond().getSecond().contains(car)).findFirst().get();
                    System.out.println(car.carId + " " + car.currentPathIndex + " " + car.position + " " + roadChannelPair.getFirst().roadKey.roadId +" " + roadChannelPair.getFirst().roadKey.reverseDirection + " " + roadChannelPair.getSecond().getFirst());
                });
                return SimulationResult.newDeadlockInstance(time + 1, -1, carSimulationResults);
            }

            Iterator<SimulationGarageCar> garageCarIterator = garageCarsSortedByTimeAndId.iterator();
            while (garageCarIterator.hasNext()) {
                SimulationGarageCar garageCar = garageCarIterator.next();
                if (garageCar.planTime > time)
                    break;

                // Use default order: first by plan time then by ID
                SimulationRoad road = crosses.get(garageCar.from).roadsOut[garageCar.path.firstDirection];
                int speed = min(road.speed, garageCar.speed);

                SimulationRoadCar roadCar = new SimulationRoadCar(garageCar.id, garageCar.speed, garageCar.path, time);
                if (scheduleToNewRoad(roadCar, road, speed - 1)) {
                    garageCarIterator.remove();
                    roadCars.add(roadCar);
                }
            }
        }


        return SimulationResult.newSuccessInstance(time, -1, carSimulationResults);
    }

    public PathCrossTurns convertPathToTurns(int from, Path path) {
        int[] roadIds = path.getRoadIds();
        int crossTurnNumber = roadIds.length - 1;
        CrossTurn[] crossTurns = new CrossTurn[crossTurnNumber];

        SimulationCross cross = crosses.get(from);
        int firstDirection = ArrayUtils.indexOf(cross.roadsOut, road -> road != null && road.roadKey.roadId == roadIds[0]);

        int directionOut = firstDirection;
        for (int i = 0; i < crossTurnNumber; i++) {
            // Get next cross with old data
            cross = cross.roadsOut[directionOut].to;

            int roadIdIn = roadIds[i];
            int directionIn = ArrayUtils.indexOf(cross.roadsIn, road -> road != null && road.roadKey.roadId == roadIdIn);
            int roadIdOut = roadIds[i + 1];
            directionOut = ArrayUtils.indexOf(cross.roadsOut, road -> road != null && road.roadKey.roadId == roadIdOut);

            crossTurns[i] = CrossTurn.getWithDirectionOffset(directionOut - directionIn);
        }

        return new PathCrossTurns(firstDirection, crossTurns);
    }

    public List<PathCrossTurns> convertFromPathListToTurnsList(List<IntObjPair<Path>> fromPathPairs) {
        return fromPathPairs.stream()
                .map(fromPathPair -> convertPathToTurns(fromPathPair.getFirst(), fromPathPair.getSecond()))
                .collect(Collectors.toList());
    }

    public List<Pair<Car, PathCrossTurns>> convertCarPathListToCarTurnsList(List<Pair<Car, Path>> carPathPairs) {
        return carPathPairs.stream()
                .map(carPathPair -> new Pair<>(carPathPair.getFirst(), convertPathToTurns(carPathPair.getFirst().getFrom(), carPathPair.getSecond())))
                .collect(Collectors.toList());
    }
}

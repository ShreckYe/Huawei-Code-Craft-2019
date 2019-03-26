package com.huawei.simulation;

import com.huawei.common.Pair;
import com.huawei.graph.DirectedRoadId;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class SimulationRoad {
    DirectedRoadId directedRoadId;
    int length;
    int speed;

    int numberOfChannels;
    // Sorted by position in descending order
    ArrayList<ArrayDeque<SimulationRoadCar>> channels;
    SimulationCross from, to;

    SimulationRoad(DirectedRoadId directedRoadId, int length, int speed, int numberOfChannels, SimulationCross from, SimulationCross to) {
        this.directedRoadId = directedRoadId;
        this.length = length;
        this.speed = speed;
        this.numberOfChannels = numberOfChannels;
        this.channels = new ArrayList<>(numberOfChannels);
        for (int i = 0; i < numberOfChannels; i++) channels.add(new ArrayDeque<>());
        this.from = from;
        this.to = to;
    }

    SimulationRoad(DirectedRoadId directedRoadId, int length, int speed, int numberOfChannels) {
        this(directedRoadId, length, speed, numberOfChannels, null, null);
    }

    /**
     * Returns the cars in the first row where there are cars
     */
    Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>> getPriorityOneWaitingCarChannelPair() {
        List<Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>>> firstRowWaitingCarChannelPairs = channels.stream().map(channel -> {
            SimulationRoadCar first = channel.peekFirst();
            return first != null && first.waiting ? new Pair<>(first, channel) : null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        if (firstRowWaitingCarChannelPairs.isEmpty())
            return null;

        @SuppressWarnings("OptionalGetWithoutIsPresent") int priorityOnePosition = firstRowWaitingCarChannelPairs.stream()
                .mapToInt(carChannelPair -> carChannelPair.getFirst().getPosition())
                .max().getAsInt();
        @SuppressWarnings("OptionalGetWithoutIsPresent") Pair<SimulationRoadCar, ArrayDeque<SimulationRoadCar>> priorityOneWaitingCarChannelPair = firstRowWaitingCarChannelPairs.stream()
                .filter(carChannelPair -> carChannelPair.getFirst().getPosition() == priorityOnePosition)
                .findFirst().get();

        return priorityOneWaitingCarChannelPair;
    }
}

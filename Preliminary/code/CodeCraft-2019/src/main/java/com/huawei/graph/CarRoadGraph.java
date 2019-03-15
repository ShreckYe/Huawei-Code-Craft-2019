package com.huawei.graph;

import com.huawei.data.Car;
import com.huawei.data.Cross;
import com.huawei.data.Road;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.List;

public class CarRoadGraph {
    private Graph<Integer, DirectedRoad> graph = new SimpleDirectedWeightedGraph<>(DirectedRoad.class);

    /*public CarRoadGraph(List<Cross> crosses, List<Road> roads) {
        for (Cross cross : crosses)
            graph.addVertex(cross.getId());
        for (Road road : roads) {
            graph.addEdge(road.getFrom(), road.getTo(), new DirectedRoad(road.getId(), false));
            if (road.isDuplex()) {
                graph.addEdge(road.getTo(), road.getFrom(), new DirectedRoad(road.getId(), false));
            }
        }
    }*/

    public CarRoadGraph(List<Cross> crosses, List<Road> roads, Car car) {
        for (Cross cross : crosses)
            graph.addVertex(cross.getId());
        for (Road road : roads) {
            double idealTime = (double) road.getLength() / Math.min(car.getSpeed(), road.getSpeed());

            graph.addEdge(road.getFrom(), road.getTo(), new DirectedRoad(road.getId(), false));
            graph.setEdgeWeight(road.getFrom(), road.getTo(), idealTime);
            if (road.isDuplex()) {
                graph.addEdge(road.getTo(), road.getFrom(), new DirectedRoad(road.getId(), false));
                graph.setEdgeWeight(road.getTo(), road.getFrom(), idealTime);
            }
        }
    }

    public GraphPath<Integer, DirectedRoad> dijkstraShortestPath(int source, int dest) {
        DijkstraShortestPath<Integer, DirectedRoad> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(source, dest);
    }
}

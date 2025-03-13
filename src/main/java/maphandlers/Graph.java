package maphandlers;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import datatypes.JourneyData;
import datatypes.Trip;

public class Graph {
    private static Map<Integer, List<Edge>> adjacencyList = new HashMap<>();
    private static List<Integer> shortestPath;
    private static int min = Integer.MAX_VALUE;
    private static final DatabaseHandler dbHandler = new DatabaseHandler();
    private static LocalTime startTime;
    private static LocalTime currTime;
    private static Map<Integer, LocalTime> nodeTimes;
    private static final Map<Integer, Edge> edgeTo = new HashMap<>();
    private static final Map<Integer, Integer> nodeTripIds = new HashMap<>();

    public void reset() {
        adjacencyList = new HashMap<>();
        shortestPath = new ArrayList<>();
        min = Integer.MAX_VALUE;
    }

    public void addEdge(int source, int destination, int weight, Trip trip, Trip destinationTrip) {
        if (weight <= 0) return;

        Edge edge = new Edge(destination, weight, trip, destinationTrip);

        adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(edge);
    }

    private JourneyData journeyData;

    public List<Integer> getPath() {
        return shortestPath;
    }

    public void dijkstra(int start, int end, String startTime) {
        Graph.startTime = LocalTime.parse(startTime);

        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        nodeTimes = new HashMap<>();  // To keep track of the current time for each node
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        // Initialize distances and previous nodes
        for (int node : adjacencyList.keySet()) {
            if (node == start) {
                distances.put(node, 0);
                queue.add(new Node(node, 0));
                nodeTimes.put(node, Graph.startTime);  // Set the start time for the start node
                nodeTripIds.put(node, -1);  // Initialize with -1 indicating no trip
            } else {
                distances.put(node, Integer.MAX_VALUE);
                queue.add(new Node(node, Integer.MAX_VALUE));
                nodeTimes.put(node, LocalTime.MAX);  // Initialize with max time for other nodes
                nodeTripIds.put(node, -1);  // Initialize with -1 indicating no trip
            }
            previous.put(node, null);
        }

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // If the shortest distance to the current node is infinity, break out of the loop
            if (current.distance == Integer.MAX_VALUE) break;

            // If we reached the end node, construct the path and return
            if (current.id == end) {

                List<Integer> path = new ArrayList<>();
                int totalDistance = 0;
                int node = current.id;

                while (previous.get(node) != null) {
                    path.add(node);
                    node = previous.get(node);
                }
                path.add(start);  // Add the start node at the end of the loop
                Collections.reverse(path);

                // Calculate the total distance of the path
                for (int i = 0; i < path.size() - 1; i++) {
                    int from = path.get(i);
                    int to = path.get(i + 1);

                    Edge edge = edgeTo.get(to);
                    int weight = edge.weight;

                    totalDistance += weight;
                }

                if (totalDistance < min) {
                    shortestPath = path;
                    min = totalDistance;
                }

                return;
            }

            // Consider all edges going out from the current node
            for (Edge edge : adjacencyList.getOrDefault(current.id, new ArrayList<>())) {

                LocalTime currentNodeTime = nodeTimes.get(current.id);
                currTime = currentNodeTime;

                // Skip the edge if its departure time is not after the current time
                if (edge.getTrip_id() != 1 && edge.getDeparture_time().isBefore(currentNodeTime)) continue;

                Duration duration = Duration.between(currentNodeTime, edge.getArrival_time());
                int temp = edge.weight;
                edge.weight = duration.toMinutesPart();

                edge.waitingTime = edge.weight - temp;

                int alt = current.distance + edge.weight;
                if (alt < distances.getOrDefault(edge.destination, Integer.MAX_VALUE)) {

                    if (!currentNodeTime.plusMinutes(edge.weight).equals(edge.getArrival_time())) continue;

                    distances.put(edge.destination, alt);
                    previous.put(edge.destination, current.id);
                    queue.add(new Node(edge.destination, alt));

                    nodeTimes.put(edge.destination, currentNodeTime.plusMinutes(edge.weight));

                    edgeTo.put(edge.destination, edge);

                    // Store the trip ID for the destination node
                    nodeTripIds.put(edge.destination, edge.getTrip_id());
                }
            }
        }
    }

    public void calculateJourneyDetails() throws SQLException {
        List<Integer> path = shortestPath;

        if (path == null || path.size() < 2) {
            System.out.println("Path is null or too short to calculate.");
            return;
        }
        int initialWalk = 0;
        int finalWalk = 0;
        int totalTripTime = 0;

        LinkedHashMap<String, Integer> busRoutes = new LinkedHashMap<>();
        ArrayList<String> busNumbers = new ArrayList<>();
        String currentBusNumber = ""; //initialise to provide scope to transfer checker

        boolean hasTransfers = false;
        ArrayList<String> transferDetails = new ArrayList<>();

        if (path.size() > 1) {
            Edge firstEdge = edgeTo.get(path.get(1));
            totalTripTime += firstEdge.weight;
            initialWalk = firstEdge.weight;
        }

        for (int i = 1; i < path.size() - 1; i++) {
            int to = path.get(i);
            Edge edge = edgeTo.get(to);
            totalTripTime += edge.weight;

            if(i != 1) {
                busRoutes.put(dbHandler.getStopName(to), edge.weight);

                currentBusNumber = DatabaseHandler.findRouteShortName(edge.trip.getTrip_id());
                busNumbers.add(currentBusNumber);
            }
            // Check for transfers
            if (i > 4) {
                String prevBusNumber = busNumbers.get(busNumbers.size()-2);
                if (!prevBusNumber.equals(currentBusNumber)) {
                    hasTransfers = true;
                    transferDetails.add(dbHandler.getStopName(to));
                }
            }
        }

        // Walking from last bus stop to destination
        if (path.size() > 2) {
            Edge lastEdge = edgeTo.get(path.get(path.size() - 2));
            totalTripTime += lastEdge.weight;
            finalWalk = lastEdge.weight;  // Save the final walk time
        }

        // Calculate arrival time at the destination
        LocalTime arrivalTime = startTime.plusMinutes(totalTripTime);
        System.out.println(busNumbers.toString());
        journeyData = new JourneyData(initialWalk, 0, busRoutes, finalWalk, totalTripTime, hasTransfers, transferDetails, arrivalTime, busNumbers);

    }


    public int getTotalDuration(){
        return min;
    }

    public JourneyData getJourneyData() {
        if (journeyData == null) {
            throw new IllegalStateException("Journey data has not been calculated yet.");
        }
        return journeyData;
    }

    private static class Edge {
        int destination;
        int weight;
        Trip trip;
        Trip destinationTrip;
        int waitingTime;

        Edge(int destination, int weight, Trip trip, Trip destinationTrip) {
            this.destination = destination;
            this.weight = weight;
            this.trip = trip;
            this.destinationTrip = destinationTrip;
        }

        public int getTrip_id() {
            if (trip == null) return 0;
            return trip.getTrip_id();
        }

        public LocalTime getArrival_time() { // arrival at next stop
            if (trip.getTrip_id() == 0) {
                return startTime.plusMinutes(weight);
            }
            if (trip.getTrip_id() == 1) return currTime.plusMinutes(weight);

            return parseTime(destinationTrip.getArrival_time());
        }

        public LocalTime getDeparture_time() { // departure time from start stop
            if (trip.getTrip_id() == 0) return startTime;
            return parseTime(trip.getDeparture_time());
        }

        public LocalTime parseTime(String time) {
            String[] parts = time.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);

            if (hours >= 24) {
                hours = hours - 24;
            }

            return LocalTime.of(hours, minutes, seconds);
        }

        @Override
        public String toString() {
            return destination + " " + weight;
        }
    }

    private static class Node {
        int id;
        int distance;

        Node(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }
    }

    public Map<Integer, List<Edge>> getList() {
        return adjacencyList;
    }

    public int getMin() {
        return min;
    }
}


/**
 * Main
 */

import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Main {

    public static HashMap<String, Airport> airports = new HashMap<String, Airport>();
    public static HashMap<String, Airfield> airfields = new HashMap<String, Airfield>();

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(",");
            Airport airport = new Airport(values[0], values[1], Double.parseDouble(values[2]),
                    Double.parseDouble(values[3]), Integer.parseInt(values[4]));
            airports.put(airport.airportCode, airport);
            if (airfields.containsKey(airport.airfieldName)) {
                airfields.get(airport.airfieldName).airports.put(airport.airportCode, airport);
            } else {
                Airfield airfield = new Airfield(airport.airfieldName);
                airfield.airports.put(airport.airportCode, airport);
                airfields.put(airport.airfieldName, airfield);
            }
        }

        file = new File(args[1]);
        scanner = new Scanner(file);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(",");
            Airport airport = airports.get(values[0]);
            airport.directions.add(values[1]);
        }

        file = new File(args[2]);
        scanner = new Scanner(file);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(",");
            Airfield airfield = airfields.get(values[0]);
            if (airfield == null) {
                continue;
            }
            airfield.timeStampToWeatherCode.put(Long.parseLong(values[1]), Integer.parseInt(values[2]));
        }

        task1(args[3], args[4]);
        task2(args[3], args[5]);
    }

    /******************************************************************************************************************************************************/
    // TASK - 1
    /******************************************************************************************************************************************************/

    public static void task1(String arg, String out) throws Exception {
        Long systemTime = System.currentTimeMillis();

        File file = new File(arg);
        Scanner scanner = new Scanner(file);

        System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(out)));
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(" ");
            Airport from = airports.get(values[0]);
            Airport to = airports.get(values[1]);
            Long timeOrigin = Long.parseLong(values[2]);

            HashMap<String, Double> costs = new HashMap<String, Double>();
            HashMap<String, String> fromWhere = new HashMap<String, String>();
            costs.put(from.airportCode, 0.0);
            PriorityQueue<Node> queue = new PriorityQueue<Node>();
            queue.add(new Node(from.airportCode, 0.0, timeOrigin, from.distanceTo(to)));
            while (!queue.isEmpty()) {
                Node node = queue.remove();
                if (node.airportCode.equals(to.airportCode)) {
                    break;
                }
                Airfield airfield = airfields.get(airports.get(node.airportCode).airfieldName);
                Airport airport = airports.get(node.airportCode);
                for (String direction : airport.directions) {
                    Airport nextAirport = airports.get(direction);
                    Double distance = airport.distanceTo(nextAirport);
                    Airfield nextAirfield = airfields.get(nextAirport.airfieldName);
                    Double cost = node.cost
                            + airfield.weatherCoefficient(nextAirfield, timeOrigin, Long.valueOf(0)) * 300 + distance;

                    if (!costs.containsKey(direction) || cost < costs.get(direction)) {
                        costs.put(nextAirport.airportCode, cost);
                        queue.add(new Node(nextAirport.airportCode, cost, timeOrigin, nextAirport.distanceTo(to)));
                        fromWhere.put(nextAirport.airportCode, node.airportCode);
                    }
                }
            }

            if (!costs.containsKey(to.airportCode)) {
                System.out.println("No possible solution.");
                continue;
            }

            String airportCode = to.airportCode;
            ArrayList<String> path = new ArrayList<String>();
            while (airportCode != null) {
                path.add(airportCode);
                airportCode = fromWhere.get(airportCode);
            }
            for (int i = path.size() - 1; i >= 0; i--) {
                System.out.print(path.get(i) + " ");
            }
            System.out.println(String.format("%.5f", costs.get(to.airportCode)));

            File file1 = new File(out);
            Scanner scanner1 = new Scanner(file1);

            File file2 = new File(out);
            Scanner scanner2 = new Scanner(file2);

            while (scanner1.hasNextLine() && scanner2.hasNextLine()) {
                String line1 = scanner1.nextLine();
                String line2 = scanner2.nextLine();
                if (!line1.equals(line2)) {
                    System.out.println("Wrong answer");
                    break;
                }
            }
        }

        System.out.println((System.currentTimeMillis() - systemTime) / 1000.0);
    }

    /******************************************************************************************************************************************************/
    // TASK - 2
    /******************************************************************************************************************************************************/

    public static void task2(String arg, String out) throws Exception {
        Long systemTime = System.currentTimeMillis();

        File file = new File(arg);
        Scanner scanner = new Scanner(file);

        System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(out)));

        String type = scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] values = line.split(" ");
            Airport from = airports.get(values[0]);
            Airport to = airports.get(values[1]);
            Long timeOrigin = Long.parseLong(values[2]);
            Long deadline = Long.parseLong(values[3]);

            HashMap<Node, Double> costs = new HashMap<Node, Double>();
            HashMap<Node, Node> fromWhere = new HashMap<Node, Node>();
            costs.put(new Node(from.airportCode, 0.0, timeOrigin, from.distanceTo(to)), 0.0);

            PriorityQueue<Node> queue = new PriorityQueue<Node>();
            queue.add(new Node(from.airportCode, 0.0, timeOrigin, from.distanceTo(to)));

            Long destinationTime = null;

            while (!queue.isEmpty()) {
                Node node = queue.remove();
                if (node.airportCode.equals(to.airportCode)) {
                    destinationTime = node.time;
                    break;
                }
                if (node.time > deadline) {
                    continue;
                }
                if (node.cost > costs.get(node)) {
                    continue;
                }

                Airport airport = airports.get(node.airportCode);
                Airfield airfield = airfields.get(airport.airfieldName);

                for (String direction : airport.directions) {
                    Airport nextAirport = airports.get(direction);
                    Airfield nextAirfield = airfields.get(nextAirport.airfieldName);

                    Double distance = airport.distanceTo(nextAirport);
                    Long flightDuration = airport.flightDuration(nextAirport, type, distance);

                    if (node.time + flightDuration > deadline) {
                        continue;
                    }

                    Double cost = node.cost + airfield.weatherCoefficient(nextAirfield, node.time, flightDuration) * 300
                            + distance;

                    Node newDirectionNode = new Node(nextAirport.airportCode, cost, node.time + flightDuration,
                            nextAirport.distanceTo(to));

                    if (!costs.containsKey(newDirectionNode) || cost < costs.get(newDirectionNode)
                            && newDirectionNode.time <= deadline) {
                        costs.put(newDirectionNode, cost);
                        queue.add(newDirectionNode);
                        fromWhere.put(newDirectionNode, node);
                    }
                }

                if (node.time + 6 * 60 * 60 < deadline) {
                    Node parkingNode = new Node(node.airportCode, node.cost + airport.parkingCost,
                            node.time + 6 * 60 * 60, airport.distanceTo(to));
                    if (costs.containsKey(parkingNode) && costs.get(parkingNode) < parkingNode.cost) {
                        continue;
                    }
                    costs.put(parkingNode, node.cost + airport.parkingCost);
                    queue.add(parkingNode);
                    fromWhere.put(parkingNode, node);
                }
            }

            if (destinationTime == null) {
                System.out.println("No possible solution.");
                continue;
            }

            ArrayList<String> path = new ArrayList<String>();
            Node destinationNode = new Node(to.airportCode, 0.0, destinationTime, to.distanceTo(to));
            while (destinationNode != null) {
                path.add(destinationNode.airportCode);
                destinationNode = fromWhere.get(destinationNode);
            }
            for (int i = path.size() - 1; i >= 0; i--) {
                if (i < path.size() - 1 && path.get(i).equals(path.get(i + 1))) {
                    System.out.print("PARK ");
                } else {
                    System.out.print(path.get(i) + " ");
                }
            }
            System.out.println(String.format("%.5f",
                    costs.get(new Node(to.airportCode, 0.0, destinationTime, to.distanceTo(to)))));

            File file1 = new File(out);
            Scanner scanner1 = new Scanner(file1);

            File file2 = new File(out);
            Scanner scanner2 = new Scanner(file2);

            while (scanner1.hasNextLine() && scanner2.hasNextLine()) {
                String line1 = scanner1.nextLine();
                String line2 = scanner2.nextLine();
                if (!line1.equals(line2)) {
                    System.out.println("Wrong answer");
                    break;
                }
            }
        }
        System.out.println((System.currentTimeMillis() - systemTime) / 1000.0);
    }

}

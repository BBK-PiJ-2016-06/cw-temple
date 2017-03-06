package game;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by nathanhanak on 3/6/17.
 *
 * Class which utilizes the A* Shortest path algorithm to find the shortest distance in a graph
 * between a starting location and a destination.
 */
public class AStarShortestPath {

    private Node startingLocation;
    private Node destinationNode;
    private Boolean atDestination = false;

    private Map<Node, NodeWrapper> openList = new HashMap<>();
    private Map<Node, NodeWrapper> closedMap = new HashMap<>();
    private Tile destinationTile;

    public AStarShortestPath(Node starting, Node destination) {
        startingLocation = starting;
        destinationNode = destination;
        destinationTile = destination.getTile();
        calculateShortestDistance();
    }

    private void calculateShortestDistance(){
        openList.put(startingLocation, new NodeWrapper(startingLocation, startingLocation, destinationNode));

         while (!atDestination) {
            Node currentNode = returnOpenNodeWithLowestFCost();
            closedMap.put(currentNode, openList.get(currentNode));
            openList.remove(currentNode);

            if (currentNode.equals(destinationNode)) {
                atDestination = true;
            } else {
                Set<Node> validNeighbors = currentNode.getNeighbours()
                                                      .stream()
                                                      .filter(node -> !closedMap.containsKey(node))
                                                      .collect(Collectors.toSet());

                for (Node n : validNeighbors) {
                    if (!openList.containsKey(n) || getNodeFCost(currentNode, n) < openList.get(n).getFinalCost()) {
                        if (openList.containsKey(n)) {
                            openList.get(n).setParentNode(currentNode); // also automatically changes nodeWrapper's FinalCost
                        } else {
                            openList.put(n, new NodeWrapper(n, currentNode, destinationNode));
                        }
                    }
                }
            }
         }
    }

    /**
     * Method which retrieves the shortest route from the starting location to the destination.
     * At this point, closedMap contains all evaluated Nodes, including the destination.
     * Each node has a pointer to their "parent" which routes back to the starting location,
     * method systematically retrieves and collects the pointers to the parents.
     * Method ends once the starting location is found (does not add to the list)
     * @return List<Node> a sequential route of nodes to follow from the starting location to the ending.
     */
    public List<Node> retrieveShortestRoute() {
        List<Node> shortestRoute = new ArrayList<>(Arrays.asList(destinationNode));
        System.out.println("destination is at R" + destinationTile.getRow() + " C" + destinationTile.getColumn());
        Node childNode = destinationNode;
        Boolean atOrigin = false;

        while (!atOrigin) {
            Node parentNode = closedMap.get(childNode).getParentNode();
            if (parentNode.equals(startingLocation)) {
                atOrigin = true;
            } else {
                shortestRoute.add(0, parentNode);
                childNode = parentNode;
            }
        }
        return shortestRoute;
    }


    /**
     * looks for a Node in the openList with the lowest F cost, or the cost most likely to return
     * the shortest distance to our desired location. If there is only one item in our list, returns that item.
     * @return Node with the lowest likely distance to our ending location.
     */
    private Node returnOpenNodeWithLowestFCost() {
        if (openList.size() < 2) {
            return openList.keySet().stream().findFirst().orElse(null);
        } else {
            return openList.entrySet()
                    .stream()
                    .min((n1, n2) -> n1.getValue().compareTo(n2.getValue()))
                    .get()
                    .getKey();
        }
    }

    /**
     * Returns the F cost of the node to be inspected from the current node.
     * Used to determine the optimal path from the current node to its neighbors
     * @param current the current source node
     * @param inspect the node whose distance we would like to determine
     * @return the F cost = length of edge from current to inspect + inspect's Manhattan distance
     */
    private int getNodeFCost(Node current, Node inspect) {
        return current.getEdge(inspect).length + returnManhattanDistanceToExit(inspect);
    }

    /**
     * returns the Manhattan Distance of the node to be inspected from the exit tile
     * @param inspect the Node for which we are determining the MD.
     * @return the distance in int
     */
    private int returnManhattanDistanceToExit(Node inspect) {
      Tile inspectT = inspect.getTile();
      return ( Math.abs(destinationTile.getRow() - inspectT.getRow())
                + Math.abs(destinationTile.getColumn() - inspectT.getColumn()) );
    }

}
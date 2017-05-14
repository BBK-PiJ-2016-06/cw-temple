package student;

import game.Node;
import game.Tile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by nathanhanak on 3/6/17.
 * <p>
 * Class which utilizes the A* Shortest path algorithm to find the shortest distance in a graph
 * between a starting location and a destination.
 * <p>
 * Based off of the pseudocode here: http://www.codebytes.in/2015/02/a-shortest-path-finding-algorithm.html
 */
public class AStarShortestPath {

  /**
   * Starting location of the path
   */
  private Node startingLocation;

  /**
   * Final destination Node of the path
   */
  private Node destinationNode;

  /**
   * Global variable for the path to test if it has arrived at the destinationNode
   */
  private Boolean atDestination = false;

  /**
   * The calculated path of sequential Nodes to traverse
   */
  private List<Node> route;

  /**
   * calculation result of the game's time cost it would take to traverse route
   */
  private int costForRoute;


  /**
   * a list of neighbors which are a possibility to be traversed and added to route
   */
  private Map<Node, NodeWrapper> openList = new HashMap<>();

  /**
   * a list of Nodes which may no longer be considered for the route. Some may be poor choice,
   * others may already be in route and no longer need be examined.
   */
  private Map<Node, NodeWrapper> closedMap = new HashMap<>();

  /**
   * The Tile object which corresponds to the destination Node
   */
  private Tile destinationTile;

  /**
   * Constructor for AStarShortestPath class
   * @param starting the current Node from which we are calculating all routes
   * @param destination the desired destination Node at the end of the route
   */
  public AStarShortestPath(Node starting, Node destination) {
    startingLocation = starting;
    destinationNode = destination;
    destinationTile = destination.getTile();
    calculateShortestDistance();
    calculateShortestRoute();
    calculateCostForRoute();
  }

  /**
   * Calculates the shortest route from this Node's starting location to its distance using the A*
   * pathfinding algorithm.
   */
  private void calculateShortestDistance() {
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
   * Each key's value has a pointer to their "parent" which routes back to the starting location,
   * method systematically retrieves and collects the pointers to the parents.
   * Method ends once the starting location is found (does not add to the list)
   *
   * @return List<Node> a sequential route of nodes to follow from a location to the ending.
   */
  private void calculateShortestRoute() {
    route = new ArrayList<>(Arrays.asList(destinationNode));
    Node childNode = destinationNode;
    Boolean atOrigin = false;

    while (!atOrigin) {
      Node parentNode = closedMap.get(childNode).getParentNode();
      if (parentNode.equals(startingLocation)) {
        atOrigin = true;
      } else {
        route.add(0, parentNode);
        childNode = parentNode;
      }
    }
  }

  /**
   * getter for the calculated route
   * @return
   */
  public List<Node> getRoute() {
    return route;
  }

  /**
   * method which calculates the total cost for travelling the route
   */
  private void calculateCostForRoute() {
    route.add(0, startingLocation);
    costForRoute = 0;
    for (int i = 0; i < route.size() - 1; i++) {
      costForRoute += route.get(i).getEdge(route.get(i + 1)).length;
    }
    route.remove(0);
  }

  public int getCostForRoute() {
    return costForRoute;
  }


  /**
   * looks for a Node in the openList with the lowest F cost, or the cost most likely to return
   * the shortest distance to our desired location. If there is only one item in our list, returns that item.
   *
   * @return Node with the lowest likely distance to our ending location.
   */
  private Node returnOpenNodeWithLowestFCost() {
    if (openList.size() < 2) {
      return openList.keySet().stream().findFirst().orElse(null);
    } else {
      return openList.entrySet()
              .stream()
              .min(Map.Entry.comparingByValue(NodeWrapper::compareCosts))
              .get()
              .getKey();
    }
  }

  /**
   * Returns the F cost of the node to be inspected from the current node.
   * Used to determine the optimal path from the current node to its neighbors
   *
   * @param current the current source node
   * @param inspect the node whose distance we would like to determine
   * @return the F cost = length of edge from current to inspect + inspect's Manhattan distance
   */
  private int getNodeFCost(Node current, Node inspect) {
    return current.getEdge(inspect).length + returnManhattanDistanceToExit(inspect);
  }

  /**
   * returns the Manhattan Distance of the node to be inspected from the exit tile
   *
   * @param inspect the Node for which we are determining the MD.
   * @return the distance in int
   */
  private int returnManhattanDistanceToExit(Node inspect) {
    Tile inspectT = inspect.getTile();
    return (Math.abs(destinationTile.getRow() - inspectT.getRow())
            + Math.abs(destinationTile.getColumn() - inspectT.getColumn()));
  }

}

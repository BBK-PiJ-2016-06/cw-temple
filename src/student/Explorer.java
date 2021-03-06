package student;

import game.*;

import java.util.*;
import java.util.stream.Collectors;

public class Explorer {

  /**
   * Collection of the current neighboring NodeStatus of the current NodeStatus in explore()
   */
  private Collection<NodeStatus> currentNeighbours;

  /**
   * A stack of NodeStatus objects which have previously been traversed. Used for backtracking
   */
  private Stack<NodeStatus> visitedNodeStatuses = new Stack();

  /**
   * A list of NodeStatus objects which should no longer be considered for traversal
   */
  private ArrayList<NodeStatus> exhaustedNodeStatuses = new ArrayList();

  /**
   * The NodeStatus on which the character is currently standing.
   */
  private NodeStatus currentNodeStatus;


  /**
   * Explore the cavern, trying to find the orb in as few steps as possible.
   * Once you find the orb, you must return from the function in order to pick
   * it up. If you continue to move after finding the orb rather
   * than returning, it will not count.
   * If you return from this function while not standing on top of the orb,
   * it will count as a failure.
   * <p>
   * There is no limit to how many steps you can take, but you will receive
   * a score bonus multiplier for finding the orb in fewer steps.
   * <p>
   * At every step, you only know your current tile's ID and the ID of all
   * open neighbor tiles, as well as the distance to the orb at each of these tiles
   * (ignoring walls and obstacles).
   * <p>
   * To get information about the current state, use functions
   * getCurrentLocation(), // gets the int ID of your current location
   * getNeighbours(), and
   * getDistanceToTarget()
   * in ExplorationState.
   * You know you are standing on the orb when getDistanceToTarget() is 0.
   * <p>
   * Use function moveTo(long id) in ExplorationState to move to a neighboring
   * tile by its ID. Doing this will change state to reflect your new position.
   * <p>
   * A suggested first implementation that will always find the orb, but likely won't
   * receive a large bonus multiplier, is a depth-first search.
   *
   * @param state the information available at the current state
   */
  public void explore(ExplorationState state) {
    while (state.getDistanceToTarget() != 0) {
      currentNeighbours = state.getNeighbours();
      NodeStatus closestNode = returnNodeStatusClosestToTarget();
      if (closestNode.getId() == state.getCurrentLocation()) {
        moveBackwards(state);
      } else {
        state.moveTo(closestNode.getId());
        visitedNodeStatuses.push(currentNodeStatus);
        currentNodeStatus = closestNode;
      }
    }
    return;
  }

  /**
   * Method which moves character backwards one space to previously traversed NodeStatus
   *
   * @param state the current state of the game
   */
  private void moveBackwards(ExplorationState state) {
    NodeStatus previousNode = visitedNodeStatuses.pop();
    exhaustedNodeStatuses.add(currentNodeStatus);
    state.moveTo(previousNode.getId());
    currentNodeStatus = previousNode;
  }

  /**
   * Method which finds the Node with the closest distance to the target.
   * Filters previously visited nodes and dead end nodes
   * If two nodes have equal distanceToTarget, will return the first found
   *
   * @return NodeStatus the node closest to the target, or the current node if no suitable nodes found.
   */
  private NodeStatus returnNodeStatusClosestToTarget() {
    return currentNeighbours.stream()
            .filter(n -> !visitedNodeStatuses.contains(n) && !exhaustedNodeStatuses.contains(n))
            .min(NodeStatus::compareTo)
            .orElseGet(() -> currentNodeStatus);
  }


  /**
   * Escape from the cavern before the ceiling collapses, trying to collect as much
   * gold as possible along the way. Your solution must ALWAYS escape before time runs
   * out, and this should be prioritized above collecting gold.
   * <p>
   * You now have access to the entire underlying graph, which can be accessed through EscapeState.
   * getCurrentNode() (returns Node) and getExit() (returns Node) will return you Node objects of interest,
   * and getVertices() (returns Collection<Node>) will return a collection of all nodes on the graph.
   * <p>
   * Note that time is measured entirely in the number of steps taken, and for each step
   * the time remaining is decremented by the weight of the edge taken. You can use
   * getTimeRemaining() (return int) to get the time still remaining, pickUpGold() to pick up any gold
   * on your current tile (this will fail if no such gold exists), and moveTo(Node) to move
   * to a destination node adjacent to your current node.
   * <p>
   * You must return from this function while standing at the exit. Failing to do so before time
   * runs out or returning from the wrong location will be considered a failed run.
   * <p>
   * You will always have enough time to escape using the shortest path from the starting
   * position to the exit, although this will not collect much gold.
   *
   * @param state the information available at the current state
   */
  public void escape(EscapeState state) {
    Boolean timeToExit = false;
    if (state.getCurrentNode().getTile().getGold() > 0) {
      state.pickUpGold();
    }
    while (!timeToExit) {
      try {
        AStarShortestPath pathToNearestGold = getPathToClosestGoldNode(state);
        Node goldLocation = pathToNearestGold.getRoute().get(pathToNearestGold.getRoute().size() - 1);
        AStarShortestPath pathToExit = new AStarShortestPath(goldLocation, state.getExit());
        if (pathToNearestGold.getCostForRoute() + pathToExit.getCostForRoute() > state.getTimeRemaining()) {
          timeToExit = true;
        } else {
          traversePath(pathToNearestGold, state);
        }
      } catch (NullPointerException noGoldLeft) {
        timeToExit = true;
      }
    }
    AStarShortestPath pathToEscape = new AStarShortestPath(state.getCurrentNode(), state.getExit());
    traversePath(pathToEscape, state);
    return;
  }

  /**
   * Method which moves character on the determined route. Checks if there is gold along the path and picks up.
   *
   * @param path  the shortest route from the current location to destination
   * @param state the current state of the game
   */
  private void traversePath(AStarShortestPath path, EscapeState state) {
    for (int i = 0; i < path.getRoute().size(); i++) {
      state.moveTo(path.getRoute().get(i));
      if (state.getCurrentNode().getTile().getGold() > 0) {
        state.pickUpGold();
      }
    }
  }

  /**
   * Finds the location of Nodes containing the most gold
   * and returns the path to the nearest one.
   *
   * @param state the current EscapeState of the game
   * @return the shortest path to the nearest node containing gold
   * @throws NullPointerException if no gold remains on the map.
   */
  private AStarShortestPath getPathToClosestGoldNode(EscapeState state) throws NullPointerException {
    Set<Node> richestNodes = findRichestNodes(state);
    return richestNodes.stream()
            .map(node -> new AStarShortestPath(state.getCurrentNode(), node))
            .min(Comparator.comparing(AStarShortestPath::getCostForRoute))
            .orElseGet(null);
  }

  /**
   * Method polls all nodes in the state and returns a Set of all nodes which currently have gold.
   * Then finds single richest tile. Then filters through all gold nodes and returns those with
   * at least 60% of the value. Found 60% to be optimal through manual testing.
   *
   * @param state the current state of our graph, the game
   * @return a Set<Node> of all Nodes currently containing gold within 60% of the richest node.
   * returns empty Set if all gold is collected.
   */
  private Set<Node> findRichestNodes(EscapeState state) {
    Set<Node> nodesWithGold = state.getVertices().parallelStream()
            .filter(node -> node.getTile().getGold() > 0)
            .collect(Collectors.toSet());
    if (!nodesWithGold.isEmpty()) {
      Tile richestTile = nodesWithGold.parallelStream()
              .map(Node::getTile)
              .max(Comparator.comparing(Tile::getGold))
              .orElseGet(null);
      nodesWithGold = nodesWithGold.stream()
              .filter(node -> node.getTile().getGold() > richestTile.getGold() * .6)
              .collect(Collectors.toSet());
    }
    return nodesWithGold;
  }

}

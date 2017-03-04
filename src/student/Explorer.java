package student;

import game.*;

import java.util.*;

public class Explorer {

    /**
     * Objects needed for explore()
     */
  private Collection<NodeStatus> currentNeighbours;
  private Stack<NodeStatus> visitedNodeStatuses = new Stack();
  private ArrayList<NodeStatus> exhaustedNodeStatuses = new ArrayList();
  private NodeStatus currentNodeStatus;
  private int bestScore;

    /**
     * Objects needed for escape()
     */
    private Map<Node, Node> openList = new TreeMap<>(); // Map<node, parentNode>
    private ArrayList<Node> closedList = new ArrayList(); // Map<node, parentNode>

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
        if (closestNode.getId() == state.getCurrentLocation() ) {
            NodeStatus previousNode = visitedNodeStatuses.pop();
            exhaustedNodeStatuses.add(currentNodeStatus);
            state.moveTo(previousNode.getId());
            currentNodeStatus = previousNode;
        } else {
            state.moveTo(closestNode.getId());
            visitedNodeStatuses.push(currentNodeStatus);
            currentNodeStatus = closestNode;
            // possibly in here, put a limit to how far away we are moving.
        }
    }
    return;
  }

    // note to self - check out Dijkstra's_algorithm
    // this tutorial for A* http://www.policyalmanac.org/games/aStarTutorial.htm
    // also this one: https://www.codeproject.com/Articles/9880/Very-simple-A-algorithm-implementation

    /**
     * Method which finds the Node with the closest distance to the target.
     * Filters previously visited nodes and dead end nodes
     * If two nodes have equal distanceToTarget, will return the first recent found
     * @return NodeStatus the node closest to the target, or the current node if no suitable nodes found.
     */
  private NodeStatus returnNodeStatusClosestToTarget() {
      return currentNeighbours.stream()
                  .filter(n -> !visitedNodeStatuses.contains(n) && !exhaustedNodeStatuses.contains(n) )
                  .min((n1, n2) -> n1.compareTo(n2))
                  .orElseGet( () -> currentNodeStatus);
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
      openList.put(state.getCurrentNode(), state.getCurrentNode());

     while (!state.getCurrentNode().equals(state.getExit())) {
         Node currentNode = returnOpenNodeWithLowestFCost();
         state.getCurrentNode().getNeighbours()
                               .stream()
                               .filter(node -> !closedList.contains(node))
                               .forEach( n -> openList.put(n, state.getCurrentNode()));
         openList.remove(currentNode);
         closedList.add(currentNode);



          Set<Node> currentNodeNeighbors = currentNode.getNeighbours();
          currentNodeNeighbors.forEach(node -> System.out.println("Length: " + currentNode.getEdge(node).length() + " from " +
                  "currentNode to " + node.toString() + " \n Manhattan Distance of neighour is: " + returnManhattanDistanceToExit(state.getExit(), node)));
      }
  }

  private Node returnOpenNodeWithLowestFCost() {
      return openList.entrySet().stream().min(node -> ) // min should find the lowest f cost... but how?
  }

  private int returnManhattanDistanceToExit(Node exit, Node inspect) {
      Tile exitT = exit.getTile();
      Tile inspectT = inspect.getTile();
      return ( Math.abs(exitT.getRow()-inspectT.getRow()) + Math.abs(exitT.getColumn()-inspectT.getColumn()) );
  }
}

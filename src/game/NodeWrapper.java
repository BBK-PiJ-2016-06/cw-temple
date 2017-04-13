package game;

/**
 * Created by nathanhanak on 3/6/17.
 * <p>
 * Class needed to implement the A* search method of our graph.
 * Needed to store Node's parent node and calculate the
 * distance from itself to a destination, whether it be an exit or a gold location.
 */
public class NodeWrapper implements Comparable<NodeWrapper> {

  private final Node node;
  private Node parentNode;
  private Node destination;
  private int finalCost;

  /**
   * Constructor for NodeWrapper class.
   * @param n a Node which we are wrapping.
   * @param parentNode a Node which precedes this when traversing the map
   * @param destination a Node which is the target location when traversing the map.
   */
  public NodeWrapper(Node n, Node parentNode, Node destination) {
    this.node = n;
    this.parentNode = parentNode;
    this.destination = destination;
    setFinalCost();
  }

  /**
   * Updates the preceding Node to use when traversing the map.
   * @param n the new parent Node of this Node.
   */
  public void setParentNode(Node n) {
    parentNode = n;
    setFinalCost();
  }

  /**
   * Retrieves the preceding Node to use when traversing the map.
   * @return the Node preceding this Node in the route used for traversing the map.
   */
  public final Node getParentNode() {
    return parentNode;
  }

  /**
   * Calculates: finalCost = the edge length + heuristic (estimated) distance
   */
  private void setFinalCost() {
    if (node.equals(parentNode)) { // must do this for starting location
      finalCost = Integer.MAX_VALUE;
    } else {
      finalCost = parentNode.getEdge(node).length + getManhattanDistance();
    }
  }

  /**
   * Returns the value calculated in setFinalCost()
   * @return an int representing the cost of traversing to this node.
   */
  public final int getFinalCost() {
    return finalCost;
  }

  /**
   * method which calculates the Manhattan Distance (rows away + columns away)
   * from current node to destination node.
   *
   * @return the distance in int between current node and the destination
   */
  public final int getManhattanDistance() {
    Tile destinationT = destination.getTile();
    Tile thisT = node.getTile();
    return Math.abs(destinationT.getRow() - thisT.getRow())
            + Math.abs(destinationT.getColumn() - thisT.getColumn());
  }

  /**
   * Return a negative number if this.finalCost is closer to our destination,
   * or a positive number if other.finalCost is closer to the destination, 0 if the same distance.
   *
   * Findbugs says "NodeWrapper defines compareTo(NodeWrapper) and uses Object.equals()"
   * But Integer.compare's API says:
   * "the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y"
   */
  @Override
  public final int compareTo(NodeWrapper other) {
    return Integer.compare(this.finalCost, other.getFinalCost());
  }


}

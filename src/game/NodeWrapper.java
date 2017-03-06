package game;

/**
 * Created by nathanhanak on 3/6/17.
 *
 * Class needed to implement the A* search method of our graph. Needed to store Node's parent node and calculate the
 * distance from itself to a destination, whether it be an exit or a gold location.
 */
public class NodeWrapper implements Comparable<NodeWrapper> {

    private Node node;
    private Node parentNode;
    private Node destination;
    private int finalCost;

    public NodeWrapper(Node n, Node parentNode, Node destination) {
        this.node = n;
        this.parentNode = parentNode;
        this.destination = destination;
        setFinalCost();
    }

    public void setParentNode(Node n) {
        parentNode = n;
        setFinalCost();
    }

    public Node getParentNode() { return parentNode; }

    /**
     * Calculates the F cost = the edge length + heuristic (estimated) distance
     */
    public void setFinalCost() {
        finalCost = parentNode.getEdge(node).length + getManhattanDistance();
    }

    public int getFinalCost() { return finalCost; }

    /**
     * method which calculates the Manhattan Distance (rows away + columns away) from desired destination
     * @return the distance in int between current node and the destination
     */
    private int getManhattanDistance() {
        Tile destinationT = destination.getTile();
        Tile thisT = node.getTile();
        return Math.abs(destinationT.getRow() - thisT.getRow()) + Math.abs(destinationT.getColumn() - thisT.getColumn());
    }

    /**
     * Return a negative number if this.finalCost is closer to our destination, or a positive number if other.distance
     * is closer to the destination, 0 if the same distance.
     */
    @Override
    public int compareTo(NodeWrapper other) {
        return Integer.compare(this.finalCost, other.getFinalCost());
    }




}

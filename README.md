# cw-temple
Nhanak01/Nathan Hanak's attempt at PiJ Coursework 4 for Birkbeck MSc Comp Sci program. 

Contributions may be made by Github accounts BBK-PiJ-2016-06 or NateHan, both of which are the same actual author.
Most will come from NateHan, as that is what I am using for SDP portfolio contributions and it will be more convenient 
than switching back and forth depending on the work I am doing that day. 

# About
Program is a representation of a Graph traversal problem. Program requires user to navigate two separate states.
Explore State has a user doing a blind search of a graph where little is known about the map
except the current neighbors. The EscapeState reveals the map to the user as well as 
gold which must be picked up. User must pick up as much gold as possible and exit the map before time runs out.


Program is written in Java. The explore state is written using a "brute force" method of narrowing
down the possibility for exits. The escape state uses an implementation of the A* algorithm
as an object with stored qualities in internal member fields. Program compiles paths in lists for 
evaluation upon every move. This algorithm is used to find routes to nearby gold as well as 
the exit to escape.

# Limitations/Issues
Upon manual testing, average score tends to be within high 17,000's to high 18,000's.


There is room for improvement on optimizing the initial ExploreState, as the explorer will
sometimes explore routes that are unnecessary and bring him further away from the exit.
Attempted to implement a limit of how many "bad moves" Explorer could make, but this often ended up
in infinite loops or program crashes. 

# List of modified or created classes

Explorer.java
NodeWrapper.java
AStarShortestPath.java


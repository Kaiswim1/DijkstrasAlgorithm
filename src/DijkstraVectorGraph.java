import java.awt.*;
import java.util.*;
import java.util.List;

public class DijkstraVectorGraph{

    /**
     * @author Kai Charles Kishpaugh 
     * @email kaiswim1@gmail.com
     * @date 3/20/2023 
     */

    /**
     * Dijkstra's Algorithm:
     * Step 1. Look at all neighbors from current point
     * Step 2. Pick the nearest neighbor and remember this path and distance
     * Step 3. Place this path in a priority queue where the highest priority is the lowest distance.
     * Step 4. Look at the current closest path at the top of the priority queue
     * Step 5. Set the current point to the end of the current closest path
     * Step 6. Start over from Step 1 and Repeat until the current point is the end.
     *
     *
     * Each node has the index in the arraylist at the end of its name. This allows for constant access when visiting new nodes.
     * E.g. Integer.ParseInt(point.name.substring(name.length()-1) and the index of the point in "graph" should match.
     */

    /**
     * @graph Look at VecetorNavGraph.configureForNavigation() documentation
     */
    private static ArrayList<Map.Entry<VectorNavGraph.Point, ArrayList<Map.Entry<VectorNavGraph.Point, VectorNavGraph.Line>>>> graph;



    /**
     * @pathTrace This algorithm finds the next nearest path until it reaches the finish. This is a placeholder value the current closest path
     * @Path-representation (distance, start ... end ) = (355.55, 0.0, 2.0, 5.0, 8.0) = The total distance of 0->2->5->8 = 355.55
     */
    private static LinkedList<Double> pathTrace = new LinkedList<>(); //Double for distance, integer cast for node positions


    /**
     * @possiblePaths A priority queue of linked lists to store the path Traces. The first element (distance) is compared and the lowest distance
     * has the priority.
     */
    private static PriorityQueue<LinkedList<Double>> possiblePaths = new PriorityQueue<>(Comparator.comparingDouble(o -> o.getFirst()));

    private static LinkedList<Double> found = new LinkedList<>(); //A linked list of the shortest found path

    private static double iteration = 0;


    private static int NodeIndexSerial = 0;

    private static boolean isSearching = false;


    /**
     * @constructor prepares the graph into an adjacency list representation, then begins dijkstra's algorithm
     */
    public DijkstraVectorGraph(VectorNavGraph.Point start, VectorNavGraph.Point finish){
        graph = VectorNavGraph.configureForNavigation(); //We must write this line after every time we edit a graph.
        dijkstraSearch(start, finish);
    }



    /**
     * Finds the nearest neighbors from a specified node on the graph.
     * @param v The node that you wish to check the on the graph
     * @return the index position of the nearest node
     */

    /**
     * Adds visited paths to the path-trace ll
     * @param v the point we find the nearest neighbor from
     * @param ll the path-trace linked list we use
     * @return the index of the closest path
     */
    static double f = -1.0;
    private static void addPaths(Map.Entry<VectorNavGraph.Point, ArrayList<Map.Entry<VectorNavGraph.Point, VectorNavGraph.Line>>> v, LinkedList<Double> ll, VectorNavGraph.Point end){
        double d=0.0; //initialize d for 'distance'
        Double endS = Double.parseDouble(end.name.split("_")[1]);
        if(possiblePaths.size()>0){//Assume there is at least one possible path
            ll = possiblePaths.poll(); //Get the most recent closest path
            iteration = ll.getLast(); //Set pointer to the end of the current nearest path
            v = graph.get((int)iteration);
            if(iteration == endS){//If we found the end node then stop searching and assign found to the current path trace i.e. ll
                isSearching = false;
                found = ll;
                return;
            }
            d = ll.get(0); //remember current distance
        }
        for(Map.Entry<VectorNavGraph.Point, VectorNavGraph.Line> g : v.getValue()){ //Iterate through every element in the adjacency list to find all neighbors
            if(ll.size()==0){
                ll.add(0.0);
                ll.add(iteration);
            }
            f = Double.parseDouble(g.getKey().name.split("_")[1]); //Every neighbor from the original point is placeheld by f
            if(!ll.contains(f) || ll.get(0) == 0.0) { //Add every new path and set the correct distance to the pathTrace. You may not revisit a point
                ll.set(0, d + g.getValue().distance);
                ll.add(f);
                possiblePaths.add(new LinkedList<>(ll)); //Add every new path to the priority queue
                if(ll.size() > 1)ll.removeLast(); //Remove the nearest neighbor from the path trace so it can start from the original and go to the next nearest neighbor
            }
        }
    }


    public static void dijkstraSearch(VectorNavGraph.Point start, VectorNavGraph.Point end){
        NodeIndexSerial = Integer.parseInt(start.name.split("_")[1]);
        if(start.name.split("_")[1] == end.name.split("_")[1]) throw new IllegalStateException();
        if(Integer.parseInt(start.name.split("_")[1]) < 0 || Integer.parseInt(end.name.split("_")[1]) <0)
            throw new IllegalStateException();
        for(Map.Entry<VectorNavGraph.Point, ArrayList<Map.Entry<VectorNavGraph.Point, VectorNavGraph.Line>>> m : graph){ //iterate through the graph to find the start
            if(Integer.parseInt(m.getKey().name.split("_")[1]) == Integer.parseInt(start.name.split("_")[1])){ //If start matches a point in the graph
                break;
            }
            iteration++;
        }
        isSearching = true;
        while(isSearching) addPaths(graph.get((int)iteration), pathTrace, end);
    }

    /**
     * Draws the fastest path from the start to finish in yellow
     * @param g to draw the lines
     */
    public void drawFinishedPath(Graphics g){
        //Requires two pointers for the path list
        if(graph == null) return;
        g.setColor(Color.YELLOW);
        for(int i=1; i<found.size()-1; i++){
            VectorNavGraph.Point a = graph.get(found.get(i).intValue()).getKey();
            VectorNavGraph.Point b = graph.get(found.get(i+1).intValue()).getKey();
            g.drawLine(a.x, a.y, b.x, b.y);
        }
    }

    public void clear(){
        graph = null;
    }

    public void reset(){
        possiblePaths.clear();
        pathTrace.clear();
        iteration = 0;
    }


}


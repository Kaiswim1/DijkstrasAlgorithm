import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * @author Kai Charles Kishpaugh
 * @date 3/19/2023
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 * @data-representation
 *
 * VectorNavGraph:
 * This is a vector based data structure of an undirected graph using an ArrayList (lines) and an ArrayList (points).
 * We use java.awt.Graphics to display the graph on the screen.
 *
 * Point:
 * Each point contains:
 * 1. x, y-- Cartesian coordinates for position
 * 2. Size-- used to display the point a certain size on the screen
 * 3. PointIndex-- is the index pointing to the specific points position within the ArrayList (points).
 * 4. Name-- is used as an identifier for the point. The naming convention is as such:
 *    the word name followed by an underscore, followed by a unique integer. No two points can share this integer.
 *    -Examples: "bathroom_1", "entrance_2", "exit_3"
 * 5. Color-- of the point. Each point color should be able to change independently.
 *
 * Line:
 * Each line contains:
 * 1. x1, y1-- Cartesian coordinates pointing to point A where the line starts at.
 * 2. x2, y2-- Cartesian coordinates pointing to point B where the line ends at.
 * 3. name1, name2-- The names of the points. (name1) corresponds to point A's name and (name2) corresponds to point B's name.
 * 4. startOrFinish-- This determines where the navigation should start and end. [-1 = Start, 0 = None, 1 = Finish]
 * 5. angle-- The angle of the line using arctan(height/width) assuming the line is a hypotenuse.
 * 6. distance-- The distance in pixels using the pythagorean theorem assuming the line is a hypotenuse.
 */

public class VectorNavGraph {
    public static class Point {
        public int x, y, size, pointIndex;
        public Color color;
        public String name;
        public Point(int x, int y, Color color){
            name = pointNameLogic();
            pointIndex = VectorNavGraph.pointIndex;
            this.size = 12;
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
    public static class Line {
        public int x1, y1, x2, y2; //I know I need to encapsulate
        public Color color;
        public String name1, name2;

        public int startOrFinish; //-1 = Start, 0 = None, 1 = Finish

        public double angle, distance;
        public Line(int x1, int y1, int x2, int y2, Color color){
            if(points.size()>1) name1 = points.get(points.size()-2).name;
            name2 = points.get(points.size()-1).name;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
            startOrFinish = 0;
            angle = Math.toDegrees(Math.atan((((double)y2-y1)/((double)x2-x1))));
            distance = Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
            System.out.println("Angle: "+angle);
        }

        public void setName1(String name){
            name1 = name;
        }

    }
    public static ArrayList<Line> lines = new ArrayList<>();
    public static ArrayList<Point> points = new ArrayList<>();
    static int pointIndex = 0;

    private static Stack<Line> undoRedoLines = new Stack<>();
    private static Stack<Point> undoRedoPoints = new Stack<>();

    public static void drawLine(Graphics g, Line l, Color c){
        g.setColor(c);
        g.drawLine(l.x1, l.y1, l.x2, l.y2);
    }

    public void drawPoint(Graphics g, VectorNavGraph.Point p, Color c){
        g.setColor(c);
        g.fillOval(p.x-(p.size/2), p.y-(p.size/2), p.size, p.size);
    }

    public void drawAllPoints(Graphics g){
        int i=0;
        for(Point p : points){
            try {
                drawPoint(g, p, ImgToTxtVector.pointColors.get(i));
            }catch(IndexOutOfBoundsException e){}
            if(ImgToTxtVector.pointColor.getRed() != 0 && ImgToTxtVector.pointColor.getGreen() != 0 && ImgToTxtVector.pointColor.getBlue() != 0){
                ImgToTxtVector.pointColor = new Color(0, 0, 0);
            }
            i++;
        }
    }

    public static void drawAllLines(Graphics g){
        int i=0;
        for(Line l : lines){
            try {
                drawLine(g, l, ImgToTxtVector.lineColors.get(i));
            }catch(IndexOutOfBoundsException e){}
            if(ImgToTxtVector.lineColor.getRed() != 255 && ImgToTxtVector.lineColor.getGreen() != 0 && ImgToTxtVector.lineColor.getBlue() != 0){
                ImgToTxtVector.lineColor = new Color(255, 0, 0);
            }
            i++;
        }
    }

    public static String pointNameLogic(){
        return "_"+pointIndex;
    }

    public static void addLine(Line l){
        lines.add(l);
        undoRedoLines.push(l);
    }

    public static void printLines(){
        for(Line l:lines) {
            System.out.println("Line pointing from " + l.name1 + " to " + l.name2);
        }
    }


    public void addPoint(VectorNavGraph.Point p){
        points.add(p);
        undoRedoPoints.push(p);
        pointIndex++;
    }


    public static void deletePoint(VectorNavGraph.Point p){
        points.remove(p);
        pointIndex--;
    }

    public static void deletePointAndItsLines(VectorNavGraph.Point p){
        boolean crash = false;
        Line placeholder = null;
        for(Line l:lines){
            if(l.x1 == p.x || l.x2 == p.x || l.y1 == p.y || l.y2 == p.y){
                placeholder = l;
                crash = true;
                break;
            }
        }
        if(crash) {
            deleteLine(placeholder);
            deletePointAndItsLines(p); //Recursion prevents the ConcurrentModificationException
        }
        deletePoint(p);
    }

    public static void deleteLine(VectorNavGraph.Line l){
        lines.remove(l);
    }

    /* public static void write(String name) throws IOException {
        PathFinding.clearFile(name);
        File file = new File(name);
        FileWriter fw = new FileWriter(file);
        PrintWriter pw = new PrintWriter(fw);
        pw.write("Points: name-x-y\n");
        for(Point p : points){
            pw.write(p.name+"-"+p.x+"-"+p.y+"\n");
        }
        pw.write("Lines: name1-name2-x1-y1-x2-y2\n");
        for(Line l : lines){
            pw.write(l.name1+"-"+l.name2+"-"+l.x1+"-"+l.y1+"-"+l.x2+"-"+l.y2+"\n");
        }
        pw.close();
    }*/

    private static void clearAll(){
        lines.clear();
        points.clear();
        pointIndex=0;
    }

    public static void draw(String fileName) throws FileNotFoundException {
        clearAll();
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        scanner.nextLine();
        String fileLine = scanner.nextLine();
        while(!fileLine.startsWith("Lines:")){
            String[] s = fileLine.split("-");
            Point p = new Point(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Color.BLACK);
            p.name = s[0];
            points.add(p);
            System.out.println(fileLine);
            fileLine=scanner.nextLine();
        }
        while(scanner.hasNextLine()){
            fileLine = scanner.nextLine();
            String[] s = fileLine.split("-");
            System.out.println(s[2]);
            Line l = new Line(Integer.parseInt(s[2]), Integer.parseInt(s[3]), Integer.parseInt(s[4]), Integer.parseInt(s[5]), Color.RED);
            l.name1 = s[0];
            l.name2 = s[1];
            lines.add(l);

        }
        scanner.close();
    }


    /**
     * @return Adjacency list representation using the current two ArrayLists (lines) and (points)
     * @Time-complexity O(points.length * lines.length)
     * @return:
     * <ArrayList: all the points and their corresponding adjacency list on the graph
     *      <Map.entry:
     *          <Key = Point: the point on the graph you are pointing to.
     *          <Value = <ArrayList: The adjacency list corresponding to the outer Arraylist's element you are pointing to.
     *              <Map.entry:
     *                  <Key = Point: a point at an index of the outer Point's adjacency list
     *                  <Value = Line: the line pointing from the outer line to the inner line. (From original to index of originals own adjacency list)
     */
    public static ArrayList<Map.Entry<Point, ArrayList<Map.Entry<Point, Line>>>> configureForNavigation(){
        ArrayList<Map.Entry<Point, ArrayList<Map.Entry<Point, Line>>>> navGraphAdjacency = new ArrayList<>();
        for(Point p: points){
            ArrayList<Map.Entry<Point, Line>> adjacencyList = new ArrayList();
            for(Line l:lines){
                if(l.name1 == p.name){
                    Point p1 = new Point(l.x2, l.y2, Color.RED);
                    p1.name = l.name2;
                    adjacencyList.add(new Map.Entry<>() {
                        @Override
                        public Point getKey() {return p1;}

                        @Override
                        public Line getValue() {return l;}

                        @Override
                        public Line setValue(Line value) {return null;}
                    });
                }
                else if(l.name2 == p.name){
                    Point p1 = new Point(l.x1, l.y1, Color.RED);
                    p1.name = l.name1;
                    adjacencyList.add(new Map.Entry<>() {
                        @Override
                        public Point getKey() {return p1;}

                        @Override
                        public Line getValue() {return l;}

                        @Override
                        public Line setValue(Line value) {return null;}
                    });
                }
            }
            navGraphAdjacency.add(new Map.Entry<>() {
                @Override
                public Point getKey() {return p;}

                @Override
                public ArrayList<Map.Entry<Point, Line>> getValue() {return adjacencyList;}

                @Override
                public ArrayList<Map.Entry<Point, Line>> setValue(ArrayList<Map.Entry<Point, Line>> value) {throw new IllegalArgumentException();}
            });
        }

        for(Map.Entry<Point, ArrayList<Map.Entry<Point, Line>>> i : navGraphAdjacency){
            System.out.println("["+i.getKey().name+"]");
            for(Map.Entry<Point, Line> ip : i.getValue()){
                System.out.println(ip.getKey().name);
            }
            System.out.println("~~~~~");
        }
        return navGraphAdjacency;
    }


}

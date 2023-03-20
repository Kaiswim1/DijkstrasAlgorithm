
/**
 * * @author Kai Charles Kishpaugh
 * @date 3/19/2023
 *
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *    ___ _  _ _        _             _        _   _                  _ _   _                       _                 _
 *    /   (_)(_) | _____| |_ _ __ __ _( )__    /_\ | | __ _  ___  _ __(_) |_| |__  _ __ ___   /\   /(_)___ _   _  __ _| |
 *   / /\ / || | |/ / __| __| '__/ _` |/ __|  //_\\| |/ _` |/ _ \| '__| | __| '_ \| '_ ` _ \  \ \ / / / __| | | |/ _` | |
 *  / /_//| || |   <\__ \ |_| | | (_| |\__ \ /  _  \ | (_| | (_) | |  | | |_| | | | | | | | |  \ V /| \__ \ |_| | (_| | |
 * /___,' |_|/ |_|\_\___/\__|_|  \__,_||___/ \_/ \_/_|\__, |\___/|_|  |_|\__|_| |_|_| |_| |_|   \_/ |_|___/\__,_|\__,_|_|
 *         |__/                                       |___/
 *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */


/**
 * Dijkstra's algorithm UI using swing contains the main method.
 * Disclaimer: This class was modified from a larger existing project by the same author.
 */


import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class ImgToTxtVector {

    public static File imgFile;

    private static boolean isDragged = false;
    public static BufferedImage thisImage;
    private static VectorNavGraph.Point placeholderPressed;

    private static ArrayList<VectorNavGraph.Line> linesOnPoint = new ArrayList<>();

    private static boolean isNearLine = false;
    private static boolean isNearPoint = false;

    private static boolean stopPlaceHolder =false;

    private static VectorNavGraph.Point placeholder;
    private static VectorNavGraph.Line nearLinePlaceholder;

    private static DijkstraVectorGraph dijkstra;

    public static Color lineColor = new Color(255, 0, 0);
    public static Color pointColor = new Color(0, 0, 0);

    public static ArrayList<Color> lineColors = new ArrayList<>();
    public static ArrayList<Color> pointColors = new ArrayList<>();
    private static boolean searched = false;


    public static VectorNavGraph vectorNavGraph = new VectorNavGraph();

    private static VectorNavGraph.Point pointPointer = null;
    private static boolean initImage = false;
    private static int mainCount=0;

    static JRadioButton draw = new JRadioButton("Draw");
    static JRadioButton edit = new JRadioButton("Edit");

    static JRadioButton setSearch = new JRadioButton("Set search");

    //static JRadioButton dotAndLine = new JRadioButton("Dot & Line");
    //static JRadioButton dot = new JRadioButton("dot");
    //static JRadioButton  line = new JRadioButton("line");
    private double sizeOfPixel;

    private static int startX, startY;

    public static VectorNavGraph.Point[] startAndFinish = new VectorNavGraph.Point[2]; // format: ~~~~~ [Start, Finish]
    public static int getX, getY;

    private static boolean isDrawing = false;

    public ImgToTxtVector(){
        vectorNavGraph = new VectorNavGraph();
        mainCaller();
    }

    public static void mainCaller(){
        //if(mainCount == 2) return;
        if(mainCount == 0) main(null);
    }

    public static java.util.List<VectorNavGraph.Point> bresenhamLine(int x0, int y0, int x1, int y1) {
        List<VectorNavGraph.Point> line = new ArrayList<>();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx-dy;
        int e2;
        while (true) {
            line.add(new VectorNavGraph.Point(x0, y0, Color.BLACK));
            if (x0 == x1 && y0 == y1) break;
            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }
            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }
        return line;
    }

    public double calibrateDistance(){
        JFrame setSizeOfMap = new JFrame();
        JPanel sPanel = new JPanel();
        JLabel l = new JLabel("How long is this line?");
        JTextField t = new JTextField(4);
        String[] a = {"meters", "feet", "yards", "inches"};
        JComboBox<String> c = new JComboBox<>(a);
        sPanel.add(l);
        sPanel.add(t);
        sPanel.add(c);
        setSizeOfMap.setBounds(100, 100, 150, 100);
        setSizeOfMap.add(sPanel);
        t.setBounds(10,10, 10,10);
        setSizeOfMap.setVisible(true);
        t.addActionListener(_e -> {
            boolean sizeIsValid = true;
            int _a=0; int _b=0; int _c=0;
            if(getY>startY)_a = getY - startY;
            else _a = startY - getY;
            if(getX>startX)_b = getX - startX;
            else _b = startX - getX;
            try {
                sizeOfPixel = Double.parseDouble(t.getText());
            }catch(Exception _e1){
                JOptionPane.showMessageDialog(setSizeOfMap, "Please enter a number",
                        "Error", JOptionPane.ERROR_MESSAGE);
                sizeIsValid=false;
            }
            if(startX == getX){
                System.out.println("Vertical");
                System.out.println(_a);
                sizeOfPixel = sizeOfPixel/_a;
                //measurements.setValue(sizeOfPixel+" "+c.getSelectedItem().toString());
                System.out.println(sizeOfPixel+" "+c.getSelectedItem().toString());
            }
            else if(startY == getY){
                System.out.println("Horizontal");
                System.out.println(_b);
                sizeOfPixel = sizeOfPixel/_b;
                //measurements.setValue(sizeOfPixel+" "+c.getSelectedItem().toString());
                System.out.println(sizeOfPixel+" "+c.getSelectedItem().toString());
            }
            else{
                //Pythagorean theorem
                System.out.println("Diagonal");
                long _d = Math.round(Math.pow(_a, 2) + Math.pow(_b, 2));
                double diagLength = _d;
                sizeOfPixel = sizeOfPixel/Math.round(Math.sqrt(diagLength));
            }
            if(sizeIsValid)setSizeOfMap.dispose();
        });
        return sizeOfPixel;
    }

    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    static boolean isCorrecting = false;

    static JPanel panel;



    public static void main(String[] args) {
        LookAndFeel _default = UIManager.getLookAndFeel();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }catch(Exception h){
            System.out.println("Windows look and feel doesn't work");
        }
        JFileChooser fileChooser = new JFileChooser();
        try{
            UIManager.setLookAndFeel(_default);
        } catch (UnsupportedLookAndFeelException q){
            q.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {


            @Override
            public void run() {

                fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png"));

                while (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    try {
                        imgFile = fileChooser.getSelectedFile();
                        fileChooser.accept(imgFile);
                        thisImage = ImageIO.read(imgFile);
                        panel = new JPanel(){
                            protected void paintComponent(Graphics g) {
                                super.paintComponent(g);
                                g.drawImage(thisImage, 0, 0, null);
                                initImage=true;
                                vectorNavGraph.drawAllPoints(g);
                                //g.setColor(Color.RED);
                                vectorNavGraph.drawAllLines(g);
                                if(isDrawing && draw.isSelected()){
                                    g.setColor(Color.red);
                                    g.drawLine(startX, startY, getX, getY);
                                }
                                if(searched && dijkstra!=null && setSearch.isSelected()){
                                    dijkstra.drawFinishedPath(g);
                                    dijkstra.reset();
                                }

                            }
                        };
                        JFrame frame = new JFrame();
                        JMenuBar menuBar = new JMenuBar();
                        JMenu file = new JMenu("File");
                        JMenuItem save = new JMenuItem("Save");
                        JMenuItem open = new JMenuItem("Open");
                        Font p = new Font("Arial", 0, 13);
                        file.add(save);
                        file.add(open);
                        menuBar.add(file);
                        menuBar.setVisible(false);
                        menuBar.setBackground(Color.BLACK);
                        Color color3 = new Color(0, 255, 255);
                        file.setForeground(color3);
                        file.setFont(p);
                        open.setForeground(color3);
                        open.setFont(p);
                        open.setBackground(Color.BLACK);
                        //menuBar.setForeground(color3);
                        frame.setJMenuBar(menuBar);

                        //frame.pack();
                        JLayeredPane layered = new JLayeredPane();
                        System.out.println("Press control to stop drawing path");
                        frame.setBounds(0, 0, thisImage.getWidth(), thisImage.getHeight()+60);
                        panel.setBounds(0, 60, frame.getWidth(), frame.getHeight());
                        JPanel controls = new JPanel();
                        ButtonGroup group = new ButtonGroup();
                        ButtonGroup lineDot = new ButtonGroup();
                        Color darkerGray = new Color(30, 30, 30);
                        draw.setForeground(color3);
                        edit.setForeground(color3);
                        setSearch.setForeground(color3);
                        save.setBackground(Color.BLACK);
                        save.setForeground(color3);
                        save.setFont(p);
                        draw.setBackground(darkerGray);
                        edit.setBackground(darkerGray);
                        setSearch.setBackground(darkerGray);

                        group.add(draw);
                        group.add(edit);
                        group.add(setSearch);
                        controls.add(new JLabel("           "));
                        controls.setBorder(BorderFactory.createTitledBorder(new LineBorder(color3, 1, true),"Controls", 0, 0, Font.getFont("Arial"), color3));
                        controls.setBackground(darkerGray);
                        controls.setSize(thisImage.getWidth(),60);
                        controls.add(draw);
                        draw.setSelected(true);
                        controls.add(edit);
                        controls.add(setSearch);
                        frame.getContentPane().setLayout(null);
                        frame.getContentPane().add(panel);
                        frame.getContentPane().add(controls);
                        //frame.setLayout(null);
                        frame.setVisible(true);
                        panel.setVisible(true);
                        controls.setVisible(true);
                        layered.setVisible(true);

                        save.addActionListener(e -> {
                            System.out.println("Save");
                            //VectorNavGraph.write("t.txt");
                        });
                        draw.addActionListener(e->{
                            dijkstra.clear();
                        });
                        edit.addActionListener(e->{
                            dijkstra.clear();
                        });

                        open.addActionListener( e->{
                            System.out.println("Open");
                            try {
                                VectorNavGraph.draw("t.txt");
                            } catch (FileNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                            panel.repaint();
                        });




                        //frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        KeyListener keyListener = new KeyListener() {
                            @Override
                            public void keyTyped(KeyEvent e) {

                            }

                            @Override
                            public void keyPressed(KeyEvent e) {
                                // check if the 'S' key was pressed
                                if (e.isControlDown()) isDrawing = false;
                                System.out.println("54t");
                                panel.repaint();
                            }

                            @Override
                            public void keyReleased(KeyEvent e) {

                            }
                        };
                        frame.addWindowListener(new WindowListener() {
                            @Override
                            public void windowOpened(WindowEvent e) {

                            }

                            @Override
                            public void windowClosing(WindowEvent e) {
                                isDrawing=false;
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {

                            }

                            @Override
                            public void windowIconified(WindowEvent e) {

                            }

                            @Override
                            public void windowDeiconified(WindowEvent e) {

                            }

                            @Override
                            public void windowActivated(WindowEvent e) {

                            }

                            @Override
                            public void windowDeactivated(WindowEvent e) {
                                ;
                            }
                        });

                        MouseMotionListener mouseMotionListener = new MouseMotionListener() {

                            @Override
                            public void mouseDragged(MouseEvent e) {
                                //isDragged = true;
                                if(placeholder!=null)System.out.println("Frr "+placeholder.name);
                                //placeholder = null;
                                getX = e.getX();
                                getY = e.getY();
                                if (edit.isSelected()) {
                                    int i=0;
                                    for (VectorNavGraph.Point v : VectorNavGraph.points) {
                                        if (getX >= v.x - v.size && getX <= v.x + v.size && getY >= v.y - v.size && getY <= v.y + v.size) {
                                            if(placeholder!=null&&VectorNavGraph.points.get(i).name == placeholder.name){
                                                VectorNavGraph.points.get(i).x = getX;
                                                VectorNavGraph.points.get(i).y = getY;
                                            }
                                        }
                                        i++;
                                    }
                                    i=0;
                                    if(placeholderPressed != null) {
                                        System.out.println("placeholderPressed: " + placeholderPressed.x + " " + placeholderPressed.y);
                                        for (VectorNavGraph.Line l : VectorNavGraph.lines) {
                                            try {
                                                for(VectorNavGraph.Line q : linesOnPoint) {
                                                    if (l.x2 == placeholderPressed.x && l.y2 == placeholderPressed.y && l == q) {
                                                        System.out.println("2: ");
                                                        VectorNavGraph.lines.get(i).x2 = getX;
                                                        VectorNavGraph.lines.get(i).y2 = getY;
                                                        VectorNavGraph.lines.get(i).distance = Math.sqrt(Math.pow(VectorNavGraph.lines.get(i).x2-VectorNavGraph.lines.get(i).x1, 2) + Math.pow(VectorNavGraph.lines.get(i).y2-VectorNavGraph.lines.get(i).y1, 2));
                                                        VectorNavGraph.lines.get(i).angle = Math.toDegrees(Math.atan((((double)VectorNavGraph.lines.get(i).y2-VectorNavGraph.lines.get(i).y1)/((double)VectorNavGraph.lines.get(i).x2-VectorNavGraph.lines.get(i).x1))));
                                                        isDragged = true;
                                                    } else if (l.x1 == placeholderPressed.x && l.y1 == placeholderPressed.y && l == q) {
                                                        System.out.println("1: ");
                                                        VectorNavGraph.lines.get(i).x1 = getX;
                                                        VectorNavGraph.lines.get(i).y1 = getY;
                                                        VectorNavGraph.lines.get(i).distance = Math.sqrt(Math.pow(VectorNavGraph.lines.get(i).x2-VectorNavGraph.lines.get(i).x1, 2) + Math.pow(VectorNavGraph.lines.get(i).y2-VectorNavGraph.lines.get(i).y1, 2));
                                                        VectorNavGraph.lines.get(i).angle = Math.toDegrees(Math.atan((((double)VectorNavGraph.lines.get(i).y2-VectorNavGraph.lines.get(i).y1)/((double)VectorNavGraph.lines.get(i).x2-VectorNavGraph.lines.get(i).x1))));
                                                        isDragged = true;
                                                    }
                                                }
                                            }catch(Exception w){

                                            }
                                            i++;
                                        }
                                    }
                                    placeholderPressed.x = getX;
                                    placeholderPressed.y = getY;
                                    panel.repaint();
                                }
                            }

                            @Override
                            public void mouseMoved(MouseEvent e) {
                                //System.out.println("Suh");
                                getX = e.getX();
                                getY = e.getY();
                                isNearLine=isNearPoint=false;


                                // Detect if near line.
                                for(int i=0; i<VectorNavGraph.lines.size(); i++){
                                    List<VectorNavGraph.Point> l = bresenhamLine(VectorNavGraph.lines.get(i).x1, VectorNavGraph.lines.get(i).y1, VectorNavGraph.lines.get(i).x2, VectorNavGraph.lines.get(i).y2);
                                    for(VectorNavGraph.Point p : l){
                                        if(getX <= p.x+2 && getX >= p.x-2 && getY <= p.y+2 && getY >= p.y-2){
                                            System.out.println("On line: "+VectorNavGraph.lines.get(i).name1+" "+VectorNavGraph.lines.get(i).name2);
                                            try{
                                                lineColors.set(i, color3);
                                            }catch(IndexOutOfBoundsException e1){}
                                            isNearLine = true;
                                            nearLinePlaceholder = VectorNavGraph.lines.get(i);
                                            break;
                                        }else{
                                            try{
                                                lineColors.set(i, Color.RED);
                                            }catch(IndexOutOfBoundsException g){}
                                        }
                                    }
                                    if(isNearLine) break;
                                }



                                if(draw.isSelected()) {
                                    if(dijkstra!=null)dijkstra.clear();
                                    if (e.isControlDown()) {
                                        return;
                                    }
                                    int i=0;
                                    for (VectorNavGraph.Point v : VectorNavGraph.points) {
                                        //if (v != VectorNavGraph.points.get(VectorNavGraph.points.size() - 1)) {
                                        if (getX >= v.x - v.size && getX <= v.x + v.size && getY >= v.y - v.size && getY <= v.y + v.size) {
                                            getX = v.x;
                                            getY = v.y;
                                            isNearLine = false;
                                            isNearPoint = true;
                                            System.out.println("Detected point: " + v.name);
                                            break;

                                        }
                                        //}
                                        if(isNearPoint)break;
                                        i++;
                                    }
                                    System.out.println("isNearLine: "+isNearLine);
                                }
                                else{
                                    isDrawing = false;
                                    int i=0;
                                    for (VectorNavGraph.Point v : VectorNavGraph.points) {
                                        if (getX >= v.x - v.size && getX <= v.x + v.size && getY >= v.y - v.size && getY <= v.y + v.size) {
                                            getX = v.x;
                                            getY = v.y;
                                            System.out.println("Detected point: " + v.name);
                                            if(pointColors.get(i) == Color.BLACK)pointColors.set(i, Color.ORANGE);
                                        }else{
                                            if(pointColors.get(i) == Color.ORANGE)pointColors.set(i, Color.BLACK);
                                        }
                                        i++;
                                    }
                                }
                                panel.repaint();
                                panel.revalidate();
                            }
                        };
                        MouseListener mouseListener = new MouseListener() {
                            @Override
                            public void mouseClicked(MouseEvent e) {

                            }

                            boolean switchName1;
                            String switchNameTo;

                            @Override
                            public void mousePressed(MouseEvent e) {
                                isDragged=false;
                                linesOnPoint.clear();
                                if(draw.isSelected()) {
                                    if(isNearLine) System.out.println("Is near line pressed");
                                    System.out.println("Vector ImgToTxt: " + getX + " " + getY);
                                    VectorNavGraph.Point point = new VectorNavGraph.Point(getX, getY, Color.BLACK);
                                    //if(VectorNavGraph.lines.size() == 0) vectorNavGraph.addPoint(point);
                                    vectorNavGraph.addPoint(point);
                                    pointColors.add(point.color);
                                    VectorNavGraph.Line line = new VectorNavGraph.Line(startX, startY, getX, getY, Color.RED);
                                    if (switchNameTo != null && switchName1) line.setName1(switchNameTo);
                                    switchName1 = false;
                                    if (isDrawing) {
                                        vectorNavGraph.addLine(line);
                                        lineColors.add(line.color);
                                    }
                                    isDrawing = true;
                                    startX = e.getX();
                                    startY = e.getY();
                                    getX = e.getX();
                                    getY = e.getY();
                                    int i=0;
                                    for (VectorNavGraph.Point v : VectorNavGraph.points) {
                                        if (getX >= v.x - v.size && getX <= v.x + v.size && getY >= v.y - v.size && getY <= v.y + v.size) {
                                            if (v != VectorNavGraph.points.get(VectorNavGraph.points.size() - 1) && v != point) {
                                                startX = getX = v.x;
                                                startY = getY = v.y;
                                                isCorrecting = true;
                                                if (VectorNavGraph.points.size() >= 2) {
                                                    line.name2 = v.name;
                                                    switchNameTo = v.name;
                                                    switchName1 = true;
                                                }
                                            }
                                        }
                                        i++;
                                    }
                                    if(isNearLine) {
                                        int j=0;
                                        for (VectorNavGraph.Line l : VectorNavGraph.lines) {
                                            if (l.name1 == nearLinePlaceholder.name1 && l.name2 == nearLinePlaceholder.name2) {
                                                //Change the existing one from (x1, x2) to (x1, getX) & y
                                                int x2pl = l.x2;
                                                int y2pl = l.y2;
                                                String n1pl = l.name1;
                                                String n2pl = l.name2;
                                                VectorNavGraph.lines.get(j).x2 = getX;
                                                VectorNavGraph.lines.get(j).y2 = getY;
                                                VectorNavGraph.lines.get(j).distance = Math.sqrt(Math.pow(getX-VectorNavGraph.lines.get(j).x1, 2) + Math.pow(getY-VectorNavGraph.lines.get(j).y1, 2));
                                                VectorNavGraph.lines.get(j).name1 = n1pl;
                                                VectorNavGraph.lines.get(j).name2 = line.name2;

                                                //Add a line going from (getX, x2)
                                                VectorNavGraph.Line secondLine = new VectorNavGraph.Line(getX, getY, x2pl, y2pl, Color.RED);
                                                secondLine.name1 = line.name2;
                                                secondLine.name2 = n2pl;
                                                VectorNavGraph.addLine(secondLine);
                                                lineColors.add(Color.RED);
                                                //
                                                break;
                                            }
                                            j++;
                                        }
                                    }
                                    System.out.println("Pressed");
                                    if(isCorrecting)vectorNavGraph.deletePoint(point);
                                    isCorrecting = false;
                                    VectorNavGraph.printLines();
                                }
                                else if(edit.isSelected()){
                                    if(dijkstra!=null)dijkstra.clear();
                                    stopPlaceHolder = false;
                                    startX = e.getX();
                                    startY = e.getY();
                                    getX = e.getX();
                                    getY = e.getY();
                                    for (VectorNavGraph.Point v : VectorNavGraph.points) {
                                        //if (v != VectorNavGraph.points.get(VectorNavGraph.points.size() - 1)) {
                                        if (getX >= v.x - v.size && getX <= v.x + v.size && getY >= v.y - v.size && getY <= v.y + v.size) {
                                            System.out.println("Detected point: " + v.name);
                                            placeholderPressed = new VectorNavGraph.Point(v.x, v.y, Color.BLACK);
                                            System.out.println("Place holder pressed: "+placeholderPressed.name);
                                            placeholder = v;
                                            for(VectorNavGraph.Line l : VectorNavGraph.lines){
                                                if(l.x1 == placeholder.x || l.x2== placeholder.x || l.y1 == placeholder.y || l.y2 == placeholder.y){
                                                    linesOnPoint.add(l);
                                                }
                                            }
                                            if(e.getButton() == MouseEvent.BUTTON3) {
                                                NodeAndLineAttributes n = new NodeAndLineAttributes(v);
                                            }
                                            System.out.println("Detected point placeholder: " + placeholder.name);
                                        }
                                        //}
                                    }
                                    if(isNearLine) {
                                        int j = 0;
                                        for (VectorNavGraph.Line l : VectorNavGraph.lines) {
                                            if (l.name1 == nearLinePlaceholder.name1 && l.name2 == nearLinePlaceholder.name2 && e.getButton() == MouseEvent.BUTTON3) {
                                                NodeAndLineAttributes n = new NodeAndLineAttributes(l);
                                                break;
                                            }
                                            j++;
                                        }
                                    }
                                }
                                else if(setSearch.isSelected()) {
                                    startX = e.getX();
                                    startY = e.getY();
                                    getX = e.getX();
                                    getY = e.getY();
                                    for (VectorNavGraph.Point v : VectorNavGraph.points) {
                                        if (e.getButton() == MouseEvent.BUTTON1 && pointColors.get(v.pointIndex) == Color.GREEN) pointColors.set(v.pointIndex, Color.BLACK);
                                        if (e.getButton() == MouseEvent.BUTTON3 && pointColors.get(v.pointIndex) == Color.RED) pointColors.set(v.pointIndex, Color.BLACK);
                                        if (getX >= v.x - v.size && getX <= v.x + v.size && getY >= v.y - v.size && getY <= v.y + v.size) {
                                            System.out.println("Detected point: " + v.name);
                                            if (e.getButton() == MouseEvent.BUTTON1) {
                                                System.out.println("Left click"); //Start
                                                startAndFinish[0] = v;
                                                pointColors.set(v.pointIndex, Color.GREEN);
                                            } else if (e.getButton() == MouseEvent.BUTTON3) {
                                                System.out.println("Right click"); //Finish
                                                startAndFinish[1] = v;
                                                pointColors.set(v.pointIndex, Color.RED);
                                            }
                                            searched = true;
                                            if(startAndFinish[0] != startAndFinish[1])dijkstra = new DijkstraVectorGraph(startAndFinish[0], startAndFinish[1]);
                                            else searched = false;
                                            for (VectorNavGraph.Point f : startAndFinish) {
                                                if(f!=null)System.out.print("fg "+f.name);
                                            }
                                            System.out.println();

                                        }
                                        panel.repaint();
                                    }
                                }
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                                //isDrawing=false;
                                int i=0;
                                if(placeholder!=null) {
                                    for (VectorNavGraph.Point p : VectorNavGraph.points) {
                                        if (p.name == placeholder.name && isDragged) {
                                            VectorNavGraph.points.get(i).x = e.getX();
                                            VectorNavGraph.points.get(i).y = e.getY();
                                        }
                                        i++;
                                    }
                                }
                                panel.repaint();
                            }

                            @Override
                            public void mouseEntered(MouseEvent e) {

                            }

                            @Override
                            public void mouseExited(MouseEvent e) {

                            }
                        };
                        panel.addMouseListener(mouseListener);
                        panel.addMouseMotionListener(mouseMotionListener);
                        panel.setFocusable(true);
                        panel.requestFocus();
                        panel.addKeyListener(keyListener);


                        String ImageFileName = (imgFile.getName().substring(0, (imgFile.getName().length()) - 4));


                        //resize(imgFile);

                        //mainCount=1;
                        //alphaFrame.dispose();
                        return;
                        /**
                         * This enables us to see a text dialog box of the text file if we want to. It helps for bug fixing, but it is
                         * inefficient for importing text files.
                         */
                        //final JTextArea textArea = new JTextArea(ascii, image.getHeight(), image.getWidth());
                        //textArea.setFont(new Font("Monospaced", Font.BOLD, 5));
                        //textArea.setEditable(false);
                        //final JDialog dialog = new JOptionPane(new JScrollPane(textArea), JOptionPane.PLAIN_MESSAGE).createDialog(ImgToTxt.class.getName());
                        //dialog.setResizable(false);
                        //dialog.setVisible(true);


                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });
    }
}

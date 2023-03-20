import javax.swing.*;
import java.awt.*;

public class NodeAndLineAttributes {

    /**
     * This class aims to provide the user a way to view and edit the information on the
     */
    private JButton applyChanges = new JButton("Apply");
    private JButton delete = new JButton("Delete");

    private VectorNavGraph.Point p;
    private VectorNavGraph.Line l;


    private JFrame frame = new JFrame();

    public NodeAndLineAttributes(VectorNavGraph.Point p){
        this.p = p;
        initializePointUI();
    }

    public NodeAndLineAttributes(VectorNavGraph.Line l){
        this.l=l;
        initializeLineUI();
    }

    private void initializeGenericUI(JFrame frame, int height){
        frame.setLayout(null);
        frame.setBounds(350, 200, 500, height);
        frame.setVisible(true);
        frame.setBackground(Color.DARK_GRAY);
        applyChanges.setBounds(390, height-84, 80, 30);
        delete.setBounds(390-90, height-84, 80, 30);
        frame.add(delete);
        frame.add(applyChanges);
    }

    private void initializePointUI(){
        initializeGenericUI(frame, 154);
        JLabel pointName = new JLabel("Point name: ");
        pointName.setBounds(20, 28, 80, 12);
        frame.add(pointName);
        JTextField name = new JTextField();
        name.setBounds(89, 25, 56, 18);
        frame.add(name);
        name.setText(p.name);
        JLabel cartesianLocation = new JLabel("Cartesian Location X:                   Y:");
        cartesianLocation.setBounds(20, 53, 325, 12);
        frame.add(cartesianLocation);
        JTextField xPos = new JTextField();
        xPos.setBounds(144, 50, 56, 18);
        xPos.setText(String.valueOf(p.x));
        frame.add(xPos);
        JTextField yPos = new JTextField();
        yPos.setBounds(216, 50, 56, 18);
        yPos.setText(String.valueOf(p.y));
        frame.add(yPos);


        applyChanges.addActionListener(e ->{
            int i=0;
            for(VectorNavGraph.Line l:VectorNavGraph.lines){
                if(l.name1 == p.name)VectorNavGraph.lines.get(i).name1 = name.getText();
                if(l.name2 == p.name) VectorNavGraph.lines.get(i).name2 = name.getText();
                if(l.x1 == p.x) VectorNavGraph.lines.get(i).x1 = Integer.parseInt(xPos.getText());
                if(l.x2 == p.x) VectorNavGraph.lines.get(i).x2 = Integer.parseInt(xPos.getText());
                if(l.y1 == p.y) VectorNavGraph.lines.get(i).y1 = Integer.parseInt(yPos.getText());
                if(l.y2 == p.y) VectorNavGraph.lines.get(i).y2 = Integer.parseInt(yPos.getText());
                i++;
            }
            p.x = Integer.parseInt(xPos.getText());
            p.y = Integer.parseInt(yPos.getText());
            p.name = name.getText()+"_"+p.pointIndex;
            frame.dispose();
        });

        delete.addActionListener(e ->{
            VectorNavGraph.deletePointAndItsLines(p);
            ImgToTxtVector.panel.repaint();
            frame.dispose();
        });
    }

    private void initializeLineUI(){
        initializeGenericUI(frame, 234);
        JLabel pointName = new JLabel("Point Names A:                   B:");
        int xSpace = 72;
        pointName.setBounds(20, 28, 310, 12);
        frame.add(pointName);
        JTextField name1 = new JTextField();
        name1.setBounds(108, 25, 56, 18);
        name1.setText(String.valueOf(l.name1));
        frame.add(name1);
        JTextField name2 = new JTextField();
        name2.setBounds(name1.getX()+xSpace,25, 56, 18);
        name2.setText(String.valueOf(l.name2));
        frame.add(name2);
        JLabel cartesianLocation = new JLabel("(A) Cartesian Location X:                   Y:");
        cartesianLocation.setBounds(20, 53, 325, 12);
        frame.add(cartesianLocation);
        JTextField xPos = new JTextField();
        xPos.setBounds(162, 50, 56, 18);
        xPos.setText(String.valueOf(l.x1));
        frame.add(xPos);
        JTextField yPos = new JTextField();
        yPos.setBounds(xPos.getX()+xSpace, 50, 56, 18);
        yPos.setText(String.valueOf(l.y1));
        frame.add(yPos);
        JLabel cartesianLocationB = new JLabel("(B) Cartesian Location X:                   Y:");
        cartesianLocationB.setBounds(20, 53+25, 325, 12);
        frame.add(cartesianLocationB);
        JTextField xPos1 = new JTextField();
        xPos1.setBounds(162, 50+25, 56, 18);
        xPos1.setText(String.valueOf(l.x2));
        frame.add(xPos1);
        JTextField yPos1 = new JTextField();
        yPos1.setBounds(xPos.getX()+xSpace, 50+25, 56, 18);
        yPos1.setText(String.valueOf(l.y2));
        frame.add(yPos1);
        JLabel label = new JLabel("Angle: ("+l.angle+"°)");
        label.setBounds(20, 53+25+25, 325, 12);
        frame.add(label);
        JLabel distance = new JLabel("Distance: ("+l.distance+" pixels)");
        distance.setBounds(20, 53+75, 325, 12);
        frame.add(distance);

        delete.addActionListener(e ->{
            VectorNavGraph.deleteLine(l);
            ImgToTxtVector.panel.repaint();
            frame.dispose();
        });




        //53 x
    }
}


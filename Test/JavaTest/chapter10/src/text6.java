import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class color extends JFrame implements ActionListener{
    Panel p1,p2;
    Button b1,b2,b3;
    Label l1;
    Change change;
    public color(){
        this.setSize(300,100);
        this.setLocation(300,300);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        p1=new Panel();
        this.add(p1,BorderLayout.NORTH);
        b1=new Button("Red");
        b2=new Button("Green");
        b3=new Button("Blue");
        l1=new Label("                 ");
        change=new Change();
        p2=new Panel();
        p2.add(l1);
        p1.add(b1);
        p1.add(b2);
        p1.add(b3);
        b1.addActionListener(this);
        b2.addActionListener(change);
        b3.addActionListener(change);
        this.add(p2,BorderLayout.SOUTH);
        b1.addMouseListener(change);
        b2.addMouseListener(new MouseAdapter() {
        @Override
            public void mouseEntered(MouseEvent e) {
            b2.setBackground(Color.GREEN);
        }
        @Override
        public void mouseExited(MouseEvent e) {
            b2.setBackground(b1.getBackground());
        }
        });
        b3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                b3.setBackground(Color.blue);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                b3.setBackground(b1.getBackground());
            }
        });
        this.setVisible(true);

    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==b1){
            l1.setText("this is Red");
            p1.setBackground(Color.RED);
        }
        if(e.getSource()==b2){
            l1.setText("this is Green");
            p1.setBackground(Color.GREEN);
        }
        if(e.getSource()==b3){
            l1.setText("this is Blue");
            p1.setBackground(Color.BLUE);
        }
        this.setVisible(true);
    }


    class Change extends MouseAdapter implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==b1){
                l1.setText("this is Red");
                p1.setBackground(Color.RED);
            }
            if(e.getSource()==b2){
                l1.setText("this is Green");
                p1.setBackground(Color.GREEN);
            }
            if(e.getSource()==b3){
                l1.setText("this is Blue");
                p1.setBackground(Color.BLUE);
            }
        }
        public void mouseEntered(MouseEvent e) {
            if(e.getSource()==b1){
                b1.setBackground(Color.RED);
            }
            if(e.getSource()==b2){
                b2.setBackground(Color.GREEN);
            }
            if(e.getSource()==b3){
                b3.setBackground(Color.BLUE);
            }
        }

        public void mouseExited(MouseEvent e) {
            if(e.getSource()==b1){
                b1.setBackground(b2.getBackground());
            }
            if(e.getSource()==b2){
                b2.setBackground(b1.getBackground());
            }
            if(e.getSource()==b3){
                b3.setBackground(b1.getBackground());
            }
        }
    }
}



class text6 {
    public static void main(String[] args) {
        new color();
    }
}

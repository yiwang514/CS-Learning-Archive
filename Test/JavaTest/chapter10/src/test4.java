import java.awt.*;
import java.awt.event.*;

class light2 extends Frame {
    Label l1;
    Button bt1,bt2;
    Deal deal;
    public light2(){
        this.setSize(300,300);
        this.setTitle("a simple menu");
        this.setLocation(300,300);
        this.setLayout(new FlowLayout());
        deal =new Deal();
        l1=new Label("   ");
        l1.setBackground(Color.black) ;
        this.add(l1);
        bt1=new Button("open");
        bt2=new Button("close");
        this.add(bt1);
        this.add(bt2);
        bt1.addActionListener(deal);
        bt2.addActionListener(deal);
        this.addWindowListener(deal);
        this.setVisible(true);
    }
    class Deal extends WindowAdapter implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==bt1){
                l1.setBackground(Color.red);
            }
            if(e.getActionCommand()=="close"){
                l1.setBackground(Color.black);
            }
        }
        public void windowClosing(WindowEvent e){System.exit(0);}
    }
}

class test4 {
    public static void main(String[] args){
        new light2();
    }
}

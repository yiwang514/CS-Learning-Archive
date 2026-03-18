import java.awt.*;
import java.awt.event.*;


class light extends Frame implements ActionListener,WindowListener {
    Label l1;
    Button bt1,bt2;
    public light(){
        this.setSize(300,300);
        this.setTitle("一个简单的界面");
        this.setLocation(300,300);
        this.setLayout(new FlowLayout());
        l1=new Label("   ");
        l1.setBackground(Color.black) ;
        this.add(l1);
        bt1=new Button("open");
        bt1.addActionListener(this);
        bt2=new Button("close");
        bt2.addActionListener(this);
        this.add(bt1);
        this.add(bt2);
        this.addWindowListener(this);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==bt1){
            l1.setBackground(Color.RED);
        }
        if(e.getActionCommand()=="close"){
            l1.setBackground(Color.black);
        }
    }
    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {System.exit(0); }
}

class Test3 {
    public static void main(String[] args) {
        new light();
    }
}

import java.awt.*;
import java.awt.event.*;

class login2 extends Frame {

    TextField t_username,t_password;
    Button bt_login;
    Button bt_exit;
    Label tl;
    Deal deal ;
    public void setDeal(Deal deal) {
        this.deal = deal;
        bt_login.addActionListener(deal);
        bt_exit.addActionListener(deal);
        this.addWindowListener(deal);
    }
    public login2(){
        this.setTitle("一个简单的界面");
        this.setSize(500,200);
        this.setLocation(500,200);
        this.setBackground(Color.lightGray);
        this.setLayout(new FlowLayout());
        this.add(new Label("username:"));
        t_username=new TextField("unknown");
        this.add(t_username);
        this.add(new Label("password:"));
        t_password=new TextField(6);
        this.add(t_password);

        bt_login=new Button("LOGIN");
        bt_exit=new Button("EXIT");
        this.add(bt_login);
        this.add(bt_exit);
        tl=new Label("   ");
        this.add(tl);

        this.setVisible(true);
    }
}
class Deal implements ActionListener,WindowListener{
    login2 beast;
    public void setService(login2 login){
        this.beast=login;
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==beast.bt_login){
            if(beast.t_username.getText().equals("beast") && beast.t_password.getText().equals("114514")){
                beast.tl.setText("welcome,beast!");
            }
            else{
                beast.tl.setText("username or password is wrong!");
            }
            beast.setVisible(true);
        }
        if(e.getActionCommand()=="EXIT"){
            System.exit(0);
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

class text2 {
    public static void main(String[] args) {
        login2 beast = new login2();
        Deal deal = new Deal();
        beast.setDeal(deal);
        deal.setService(beast);
    }

}

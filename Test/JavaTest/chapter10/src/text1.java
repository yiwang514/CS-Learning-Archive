import java.awt.*;
import java.awt.event.*;
class login extends Frame implements ActionListener,WindowListener{
    TextField t_username,t_password;
    Button bt_login;
    Button bt_exit;
    Label tl;
    public login(){
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
        bt_login.addActionListener(this);
        bt_exit.addActionListener(this);
        this.addWindowListener(this);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==bt_login){
            if(t_username.getText().equals("beast") && t_password.getText().equals("114514")){
                this.tl.setText("welcome,beast!");
            }
            else{
                this.tl.setText("username or password is wrong!");
            }
            this.setVisible(true);
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

class Test {
    public static void main(String[] args) {
        new login();
    }
}
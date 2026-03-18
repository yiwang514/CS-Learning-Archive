import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

class NullTextFieldException extends Exception {
    public NullTextFieldException(String message) {
        super(message);
    }
}

class Calculator extends JFrame implements ActionListener{
    JTextField num1,num2,result,op;
    JLabel errorLabel;
    JButton equalButton;
    Panel p1,p2;
    public Calculator(){

        this.setTitle("Calculator");
        this.setSize(300,100);
        this.setLocation(500,200);
        this.setLayout( new BorderLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        num1 = new JTextField(5);
        num2 = new JTextField(5);
        op = new JTextField(1);
        result = new JTextField(5);
        result.setEditable(false);
        result.setEnabled(false);
        equalButton = new JButton("=");
        equalButton.addActionListener(this);
        p1=new Panel();
        p1.add(num1);
        p1.add(op);
        p1.add(num2);
        p1.add(equalButton);
        p1.add(result);
        this.add(p1,BorderLayout.NORTH);
        p2 = new Panel();
        p2.add(errorLabel = new JLabel());
        this.add(p2,BorderLayout.SOUTH);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == equalButton) {
            try {
                errorLabel.setText("");
                if (num1.getText().isEmpty() && num2.getText().isEmpty()) {
                    throw new NullTextFieldException("数字不能为空");
                }
                double a = Double.parseDouble(num1.getText());
                double b = Double.parseDouble(num2.getText());
                String operator = op.getText();
                double res = calculate(a,b,operator);
                result.setText(String.valueOf(res));
            }
            catch (NullTextFieldException ex) {
                errorLabel.setText(ex.getMessage());
                result.setText("");
            }
            catch (NumberFormatException ex) {
                errorLabel.setText("请输入正确数字");
                result.setText("");
            }
            catch (ArithmeticException ex) {
                errorLabel.setText(ex.getMessage());
                result.setText("");
            }
            catch (IllegalArgumentException ex) {
                errorLabel.setText(ex.getMessage());
                result.setText("");
            }
            this.setVisible(true);
        }
    }
    public double calculate(double a,double b,String operator){
        switch(operator){
            case "+":
                return a+b;
            case "-":
                return a-b;
            case "*":
                return a*b;
            case  "/":
                if (b==0){
                    throw new ArithmeticException("除零错误");
                }
                return a/b;
            default:
                throw new IllegalArgumentException("不支持的操作符");
        }

    }
}

class test5 {
    public static void main(String[] args) {
        new Calculator();
    }
}


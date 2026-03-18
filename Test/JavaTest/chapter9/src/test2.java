import java.util.Scanner;
import  java.util.InputMismatchException;
import java.lang.ArithmeticException;

class opException extends Exception{
    public opException(String message){
        super(message);
    }
}
class ComputeException extends Exception {
    public ComputeException(String message) {
        super(message);
    }
}


class test2 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true){
            try{
                System.out.println("请输入两个整数x、y：");
                int a = sc.nextInt();
                int b = sc.nextInt();
                System.out.println("请输入运算符op：");
                String st = sc.next();
                char op = st.charAt(0);

                int result = compute(a,b,op);
                System.out.println(a + " " + op + " " + b + " = " + result);
                break;
            }
            catch(InputMismatchException e){
                System.out.println("输入的数据不是int！");
                sc.nextLine();
            }
            catch(ArithmeticException e){
                System.out.println("除零错误！");
            }
            catch(opException e){
                System.out.println(e.getMessage());
            }
            catch(ComputeException e){
                System.out.println(e.getMessage());
            }
        }
    }
    public static int compute (int a,int b,char op)throws opException,ComputeException{
        if(op!='+'&&op!='-'&&op!='*'&&op!='/'){
            throw new opException("输入的操作符只能是+、-、*、/！");
        }
        if(op=='+'){
            if (b > 0 && a > Integer.MAX_VALUE - b) {
                throw new ComputeException("运算溢出错误！");
            }
            if (b < 0 && a < Integer.MIN_VALUE - b) {
                throw new ComputeException("运算溢出错误！");
            }
            return a+b;
        }
        else if(op=='-'){
            if (b < 0 && a > Integer.MAX_VALUE + b) {
                throw new ComputeException("运算溢出错误！");
            }
            if (b > 0 && a < Integer.MIN_VALUE + b) {
                throw new ComputeException("运算溢出错误！");
            }
            return  a-b;
        }
        else if(op=='*'){
            if (a == 0 || b == 0) {
                return 0;
            }
            if (a > 0) {
                if (b > 0 && a > Integer.MAX_VALUE / b) {
                    throw new ComputeException("运算溢出错误！");
                }
                if (b < 0 && b < Integer.MIN_VALUE / a) {
                    throw new ComputeException("运算溢出错误！");
                }
            } else {
                if (b > 0 && a < Integer.MIN_VALUE / b) {
                    throw new ComputeException("运算溢出错误！");
                }
                if (b < 0 && a < Integer.MAX_VALUE / b) {
                    throw new ComputeException("运算溢出错误！");
                }
            }
            return  a*b;
        }
        else if(op=='/'){
            if(a == Integer.MIN_VALUE && b == -1){
                throw new ComputeException("运算溢出错误！");
            }
            return  a/b;
        }
        return 0;
    }

}

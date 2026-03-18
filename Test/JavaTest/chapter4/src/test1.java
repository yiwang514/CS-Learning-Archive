import java.util.Scanner;
import java.util.regex.*;


class test1 {
        public static void main(String[] args){
            Scanner sc=new Scanner(System.in);
            System.out.println("请输入算术表达式：");
            String experssion=sc.nextLine();
            Pattern p1=Pattern.compile("\\d+(\\.\\d+)+");
            Matcher m1=p1.matcher(experssion);
            System.out.print("提取的操作数:");
            while(m1.find()){
                System.out.print(m1.group()+"  ");
            }
            System.out.println();

            Pattern p2 = Pattern.compile("[+\\-*/()]");
            Matcher m2=p2.matcher(experssion);
            System.out.print("提取的运算符：");
            while(m2.find()){
                System.out.print(m2.group()+"  ");
            }
        }
}

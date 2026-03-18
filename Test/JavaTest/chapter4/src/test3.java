import java.util.Scanner;


class Bank{
    String name;
    int value;

    Bank(String name,int value){
        this.name=name;
        this.value=value;
    }

    public String toString(){
        return name+"账户的余额为："+value;
    }

    boolean transfer(Bank other,int amount){
        if(amount<=0){
            return false;
        }
        if(amount>this.value){
            System.out.println("您的余额不足！");
            return false;
        }
        this.value -= amount;
        other.value += amount;
        return true;
    }

    public static void main(String[] args) {
        Bank A = new Bank("A", 100);
        Bank B = new Bank("B", 250);

        System.out.println(A.toString());
        System.out.println(B.toString());

        System.out.println("请输入要转账的的金额：");
        Scanner sc = new Scanner(System.in);
        int amount = sc.nextInt();

        boolean success = A.transfer(B, amount);

        if (success) {
            System.out.println("A向B转账" + amount + "元后:");
            System.out.println(A.toString());
            System.out.println(B.toString());
        }
        else  {
            System.out.println(A.toString());
            System.out.println(B.toString());
        }

    }
}

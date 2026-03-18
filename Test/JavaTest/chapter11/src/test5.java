class bank{
    private int balance=1000;
    public void deposit(String name,double money,int num){
        synchronized (this){
            double temp=balance;
            balance+=money;
            System.out.println("银行当前余额为："+temp+"，"+name + "第"+num + "次"+"存入"+money+"元");
            System.out.println("当前银行总额为:" + balance);
        }
    }

}

class Customer extends Thread{
    private bank bank;
    private String name;
    private double money;
    private int num;
    public Customer(bank bank, String name, double money, int num){
        this.bank = bank;
        this.name = name;
        this.money = money;
        this.num = num;
    }
    public void run(){
        for(int i=1;i<=num;i++){
            bank.deposit(name,money,i);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
class test5 {
    public static void main(String[] args) {
        bank bank = new bank();
        Customer customer1 = new Customer(bank, "aa", 100, 3);
        Customer customer2 = new Customer(bank, "bb", 100, 3);
        customer1.start();
        customer2.start();

    }
}

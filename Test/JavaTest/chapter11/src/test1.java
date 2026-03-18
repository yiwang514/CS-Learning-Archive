
class T1{
    public static void main(String[] args) {
        sum100 s1 = new sum100(1, 50);
        sum100 s2 = new sum100(51, 100);

        s1.start();
        s2.start();

        s1.join();
        s2.join();

        int totalSum = s1.getSum() + s2.getSum();
        System.out.println("sum=" + totalSum);
    }
}
class sum100 implements Runnable{
    private int begin;
    private int end;
    private int sum;
    private Thread t;
    public sum100(int a,int b){
        this.begin=a;
        this.end=b;
        this.sum=0;
        this.t=new Thread(this);
    }
    public void run(){
        for(int i=begin;i<=end;i++){
            sum+=i;
        }
    }
    public void start(){
        t.start();
    }
    public void join(){
        try{
            t.join();
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
    public  int getSum(){
        return sum;
    }
}

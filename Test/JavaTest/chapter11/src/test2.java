
class T2{
    public static void main(String[] args){
        eSum100 s1=new eSum100(1,50);
        eSum100 s2=new eSum100(51,100);

        s1.start();
        s2.start();


        try {
            s1.join();
            s2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int totalSum = s1.getSum() + s2.getSum();
        System.out.println("sum=" + totalSum);

    }
}
class eSum100 extends Thread{
    private int begin;
    private int end;
    private int sum;

    public eSum100(int a,int b){
        this.begin=a;
        this.end=b;
        this.sum=0;
    }

    public void run(){
        for(int i=begin;i<=end;i++){
            sum+=i;
        }
    }
    public int getSum() {
        return sum;
    }
}
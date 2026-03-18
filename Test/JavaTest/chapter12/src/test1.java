class test1{
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Main 开始");
        Sign s =new Sign();
        odd_even1 t1=new odd_even1(s,1,0,1);
        odd_even1 t2=new odd_even1(s,2,1,0);
        t1.start();
        t2.start();
    }
}
class Sign{
    private int cur = 1;
    public void printnum(int x,int y){
        while(cur!=x){
            try{wait();}catch(InterruptedException e){;}
        }
        cur=y;
        notify();
    }
}
class odd_even1 implements Runnable{
    Sign s=new Sign();
    private int num;
    private int now,next;
    Thread t;
    public odd_even1(Sign s,int num,int now,int next){
        this.s=s;
        this.num=num;
        this.now=now;
        this.next=next;
        this.t=new Thread(this);
    }

    public void run(){
        synchronized(s){
            for(int i=num;i<=20;i+=2){
                s.printnum(now,next);
                System.out.print(i+" ");
            }

        }
    }
    public void start(){
        t.start();
    }
}
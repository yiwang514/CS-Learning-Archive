class test2 {
    public static void main(String[] args){
        shardspace s=new shardspace();
        numprint1 p1= new numprint1(s,1,"奇数");
        numprint2 p2= new numprint2(s,2,"偶数");
        p1.start();
        p2.start();
    }
}
class shardspace{

}
class numprint1 extends Thread{
    shardspace s=new shardspace();
    int num;
    String name;
    public numprint1(shardspace s,int num,String name){
        this.s=s;
        this.num=num;
        this.name=name;
    }
    public void run(){
        synchronized(s){
            for(int i=num;i<=50;i+=2){
                System.out.print(i+" ");
            }
            System.out.println(name+"线程 结束！");
            try {
                sleep(1);
            }
            catch(InterruptedException e){;}
        }
    }
}

 class numprint2 implements Runnable {
     Thread t;
     int num;
     shardspace s = new shardspace();

     public numprint2(shardspace s, int num, String name) {
         this.s=s;
         t = new Thread(this, name);
         this.num=num;
     }

     public void run() {
         synchronized (s) {
             for (int i = num; i < 50; i += 2) {
                 System.out.print(i + " ");
             }
             System.out.println(t.getName() + "线程 结束！");
             try {
                 t.sleep(1);
             } catch (InterruptedException e) {
                 ;
             }
         }
     }
     public void start(){
         t.start();
     }

 }
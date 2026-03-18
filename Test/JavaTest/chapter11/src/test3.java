
class Test{
    public static void main (String[] args) throws Exception {
        System.out.print("线程开始：\n");
        shareResource sr=new shareResource();

        T t2=new T(sr,"t2",2,0,1);
        T t3=new T(sr,"t3",3,1,2);
        T t5=new T(sr,"t5",5,2,0);
        t2.start();
        t3.start();
        t5.start();
        t2.join();
        t3.join();
        t5.join();


        System.out.print("程序结束。");
    }
}



class shareResource{
    private int signstate=1;
    public void defined(int x,int y){
        while(signstate!=x){
            try {
                wait();
            } catch (InterruptedException e) {e.printStackTrace();}
        }
        signstate=y;
        notifyAll();
    }
}

class T implements Runnable{//采用内嵌线程的方式
         private shareResource sr;
         private int now,next,data;
         String name;
         private Thread t;
         public T(shareResource sr,String name,int data,int now,int next){
             this.sr=sr;
             this.name=name;
             this.data=data;
             this.now=now;
             this.next=next;
             this.t=new Thread(this,name);
         }
         public void start(){
             t.start();
         }
         public void join(){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
         }
         public void run(){
             synchronized(sr){
                 for(int i=1;i<=10;i++){
                     sr.defined(now,next);
                     System.out.print(this.data*i+" ");
                 }
             }

         }

}
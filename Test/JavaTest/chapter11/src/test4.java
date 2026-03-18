class DianHuaTing{
    private final String dht="我是电话亭";
    public synchronized void speak(String name,String[] words){
        System.out.println(name+"开始使用电话亭");
        for(int i=0;i<words.length;i++){
            System.out.println(name + " 说: " + words[i]);
        }
        System.out.println(name + " 结束使用电话亭\n");
    }
}
class App2{
    public static void main (String[] args) {
        DianHuaTing d=new DianHuaTing();
        String[] s1={"1","2","3"};
        String[] s2={"A","B","C","D","E"};
        String[] s3={"你好，","我也好，","大家都好！"};
        Talkers t1=new Talkers(d,"张三",s1);
        Talkers t2=new Talkers(d,"李四",s2);
        Talkers t3=new Talkers(d,"王五",s3);
        t1.start (); t2.start (); t3.start ();
    }
}
class  Talkers extends Thread{
     private DianHuaTing payphone;
     private String name;
     private String[] words;
     public Talkers(DianHuaTing payphone,String name,String[] words){
         this.payphone=payphone;
         this.name=name;
         this.words=words;
     }
     public void run(){
         payphone.speak(name,words);
     }
}
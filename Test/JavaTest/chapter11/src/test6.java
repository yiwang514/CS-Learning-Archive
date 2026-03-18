import java.util.Random;

class BufferArea {
    private int number = 0;
    private boolean isEmpty = true;

    public void put(int num) {
        if(!isEmpty){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        number = num;
        isEmpty = false;
        System.out.println("put number is "+number);
        notifyAll();
    }

    public int get(){
        try{
            if(isEmpty){
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int result = number;
        isEmpty = true;
        notifyAll();
        return result;
    }


}
class ThreadPutNumber extends Thread{
    private BufferArea buffer;
    private Random random ;
    public ThreadPutNumber(BufferArea buffer) {
        this.buffer = buffer;
        this.random = new Random();
    }
    public void run(){
        synchronized (buffer){
            for(int i=1;i<=7;i++){
                int num = random.nextInt(100)+1;
                buffer.put(num);
            }
        }
    }
}

class ThreadGetNumber extends Thread{
    private BufferArea buffer;
    public ThreadGetNumber(BufferArea buffer) {
        this.buffer = buffer;
    }
    private boolean isPrime(int num) {
        if (num <= 1)
            return false;
        if (num <= 3)
            return true;
        if (num % 2 == 0 )
            return false;


        for (int i = 3; i * i <= num; i += 2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
    public void run(){
        synchronized (buffer){
            for(int i=1;i<=7;i++){
                int num=buffer.get();
                boolean isPrime = isPrime(num);
                if(isPrime){
                    System.out.println(num+" is a Prime!");
                }
                else{
                    System.out.println(num+" is not a Prime!");
                }
            }
        }
    }
}

class test6 {
    public static void main(String[] args) throws InterruptedException {
        BufferArea buffer = new BufferArea();
        ThreadPutNumber thread = new ThreadPutNumber(buffer);
        ThreadGetNumber thread1 = new ThreadGetNumber(buffer);
        thread.start();
        thread1.start();
        thread1.join();
        thread.join();
        System.out.println("main is over!");
    }
}

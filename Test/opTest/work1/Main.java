import java.util.Scanner;

class Queue {
    int[] queue;
    int front;
    int rear;
    int maxsize;
    int size;

    public Queue() {
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public void createQueue(int capacity) {
        this.maxsize = capacity;
        this.queue = new int[maxsize];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public void initQueue() {
        if (queue == null) {
            System.out.println("顺序队列是空的！");
            return;
        }
        this.front = 0;
        this.rear = -1;
        this.size = 0;
        System.out.println("顺序队列是空的！");
    }

    public void dequeue() {
        if (isEmpty()) {
            System.out.println("队列是空的！");
            return;
        }
        int value = queue[front];
        for (int i = 0; i < size - 1; i++) {
            queue[i] = queue[i + 1];
        }
        rear--;
        size--;
        System.out.println("出队的元素为: " + value);
    }

    public void enqueue(int value) {
        if (isFull()) {
            System.out.println("队列已满！");
            return;
        }
        rear++;
        queue[rear] = value;
        size++;
    }

    public void printQueue() {
        if (isEmpty()) {
            System.out.println("该队列为：");
            return;
        }
        System.out.println("该队列为：");
        for (int i = 0; i < size; i++) {
            System.out.print("   " + queue[i]);
        }
        System.out.println();
    }

    public void clearQueue() {
        initQueue();
    }

    public boolean isFull() {
        return size >= maxsize;
    }

    public boolean isEmpty() {
        return size == 0;
    }

}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Queue queue = new Queue();
        boolean run = true;

        
        System.out.println("***************************");
        System.out.println("*1、新建                  *");
        System.out.println("*2、初始化                *");
        System.out.println("*3、出队                  *");
        System.out.println("*4、入队                  *");
        System.out.println("*5、打印队列              *");
        System.out.println("*6、清空队列              *");
        System.out.println("*7、队列是否为满          *");
        System.out.println("*8、队列是否为空          *");
        System.out.println("*9、退出                  *");
        System.out.println("***************************");

        while (run) {
            System.out.print("请选择要进行的操作:");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("请输入需要创建队列的个数:");
                    int capacity = scanner.nextInt();
                    queue.createQueue(capacity);
                    
                    
                    for (int i = 0; i < capacity; i++) {
                        System.out.print("请输入需要插入的值:");
                        int value = scanner.nextInt();
                        queue.enqueue(value);
                    }
                    
                 
                    queue.printQueue();
                    break;

                case 2:
                    queue.initQueue();
                    break;

                case 3:
                    queue.dequeue();
                    queue.printQueue();  
                    break;

                case 4:
                    System.out.print("请输入需要插入的值:");
                    int value = scanner.nextInt();
                    queue.enqueue(value);
                    queue.printQueue();  
                    break;

                case 5:
                    queue.printQueue();
                    break;

                case 6:
                    queue.clearQueue();
                    break;

                case 7:
                    if (queue.isFull()) {
                        System.out.println("队列是满的！");
                    } else {
                        System.out.println("队列不是满的！");
                    }
                    break;

                case 8:
                    if (queue.isEmpty()) {
                        System.out.println("队列是空的！");
                    } else {
                        System.out.println("队列不是空的！");
                    }
                    break;

                case 9:
                    run = false;
                    break;

                default:
                    System.out.println("无效的选择！");
            }
        }
        scanner.close();
    }
}
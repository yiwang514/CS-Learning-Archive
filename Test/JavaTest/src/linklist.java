import java.util.Scanner;

class App{

    public static void main(String[] args){

        LinkedList h1=new LinkedList(0);
        LinkedList h2=new LinkedList(0);

        int[] a={12,10,9,7,5,3,2,2,2,1};

        h1.append();
        System.out.println("输出h1:");
        h1.show();

        h2.insert(a);
        System.out.println("输出h2:");
        h2.show();
        System.out.print("删除h2所有值为2的结点后：");
        h2.deleAllX(2);
        h2.show();
        h1.mergeup(h2);
        System.out.print("将两个升序链表h1，h2合并到h1中，结果为：");
        h1.show();

    }

}

class LinkedList{//带头结点的单链表
    int data;
    LinkedList next;
    LinkedList(int x){ data=x;next=null; }

    void append(){
        Scanner sc=new Scanner(System.in);
        LinkedList tail=this;

        int x=sc.nextInt();
        while(x!=-1){
            LinkedList newnode=new LinkedList(x);
            x=sc.nextInt();
            tail.next=newnode;
            tail=newnode;
        }
    }

    void insert(int[] arr){
        for(int i=0;i<arr.length;i++){
            LinkedList newnode=new LinkedList(arr[i]);
            newnode.next=this.next;
            this.next=newnode;
        }
    }

    void deleAllX(int y){
        LinkedList p=this.next;
        LinkedList pre=this;
        while(p!=null){
            if(p.data==y){
                p=p.next;
                pre.next=p;
            }
            else {
                pre=p;
                p=p.next;
            }
        }
    }

    void show(){
        LinkedList q=this.next;
        while(q!=null){
            System.out.print(q.data+" ");
            q=q.next;
        }
        System.out.println();
    }

    void mergeup(LinkedList h){
        LinkedList p1=this.next;
        LinkedList p2=h.next;
        LinkedList pre=this;
        while(p1!=null && p2!=null){
            if(p1.data<=p2.data){
                pre=p1;
                p1=p1.next;
            }
            else{
                pre.next=p2;
                pre=p2;
                p2=p2.next;
                pre.next=p1;
            }
        }
        if(p2!=null){
            pre.next=p2;
        }
    }


}

import java.util.Scanner;

class App4{

    public static void main (String[] args) {
        SeqList2 La;
        int a[]={1,3,5,6,9};

        La=new SeqList2(5,a);
        System.out.print("La=");
        La.print();
        La.append();
        System.out.print("在末尾所加后，La=");
        La.print();


    }

}

class SeqList2{          //顺序表结构=数组+表长

    int []a;
    int len;
    SeqList2(int size,int[] data){
        a=new int[size];
        len=data.length;
        for(int i=0;i<len;i++){
            a[i]=data[i];
        }
    }

    void print(){
        for(int i=0;i<len;i++){
            System.out.print(a[i]+" ");
        }
        System.out.println();
    }

    void append(){
        Scanner sc=new Scanner(System.in);
        int n=sc.nextInt();
        while(n!=0){

            if(len>=a.length){
                expand();
            }
            a[len++]=n;
            n=sc.nextInt();
        }
    }

    void expand(){
        int[] newa=new int[(int)(a.length*1.5)];
        for(int i=0;i<len;i++){
            newa[i]=a[i];
        }
        a=newa;
    }
}
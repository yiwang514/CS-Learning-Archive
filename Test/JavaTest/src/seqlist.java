import java.util.Scanner;

class App3{
    public static void main (String[] args) {

        SeqList La,Lb;
        int a[]={1,3,5,6,9};

        La=new SeqList(100,a);
        Lb=new SeqList(100);

        Scanner sc=new Scanner(System.in);

        while(true){
            int n=sc.nextInt();
            if(n==0)break;
            Lb.insert(Lb.len,n);
        }
        System.out.print("La=");
        La.print();
        System.out.print("Lb=");
        Lb.print();

        La.insert(4,8);
        System.out.println("在La的第4个位置插入8后的结果为：");
        La.print();

        La = La.merge(Lb);
        System.out.print("将La、Lb合并成升序表，结果放在La中。La=");
        La.print();
    }

}

class SeqList{          //顺序表结构=数组+表长
    int []a;
    int len;
    SeqList(int size){
         a=new int[size];
         len=0;
    }

    SeqList(int size,int[] data){
        a=new int[size];
        len=data.length;
        for(int i=0;i<data.length;i++){
            a[i]=data[i];
        }
    }

    void insert(int pos,int value){
        if(pos!=0){
            for(int i=this.len;i>pos;i--){
                a[i]=a[i-1];
            }
            a[pos]=value;
            this.len++;
        }
        else{
            a[pos]=value;
            this.len++;
        }

    }

    void print(){
        for(int i=0;i<len;i++){
            System.out.print(a[i] + " ");
        }
        System.out.println();
    }

    SeqList merge(SeqList h){
        int i=0;
        int j=0;
        int k=0;
        SeqList temp=new SeqList(this.len+h.len);
        while(i<this.len && j<h.len){
            if(this.a[i]>h.a[j]){
                temp.a[k]=h.a[j];
                j++;
                k++;
            }
            else{
                temp.a[k]=this.a[i];
                i++;
                k++;
            }
        }
        while (i < this.len) {
            temp.a[k] = this.a[i];
            i++;
            k++;
        }

        while (j < h.len) {
            temp.a[k] = h.a[j];
            j++;
            k++;
        }

        temp.len=k;
        return temp;
    }
}

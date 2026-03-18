import java.util.Scanner;

class App2{

    public static void main(String[] x){

        BinTree t=new BinTree('#');
        //System.out.print("请输入二叉树建树字符，#表示null：\n");
        Scanner sc=new Scanner(System.in);
        String s=sc.nextLine();
        ReaderChar r=new ReaderChar(s);
        t=t.creatBinTree(r);

        System.out.print("pre="); t.pre();  //输出递归前序
        System.out.print("\npreN="); t.preN(); //输出非递归前序
        System.out.print("\nin="); t.in();   //输出递归中序
        System.out.print("\ninN="); t.inN(); //输出非递归中序
        System.out.print("\npost="); t.post(); //输出递归后序
        System.out.print("\npostN="); t.postN(); //输出非递归后序

    }

}

class ReaderChar{

    String creatBinTreeStr;
    int pos;
    ReaderChar(String s){ creatBinTreeStr=s; }
    char getChar(){
        char x=creatBinTreeStr.charAt(pos);
        pos++;
        return x;
    }

}

class BinTree{
    private char data;
    private BinTree left;
    private BinTree right;

    public BinTree(char data){
        this.data=data;
        this.left=null;
        this.right=null;
    }

    public BinTree creatBinTree(ReaderChar r){
        char ch=r.getChar();
        if(ch=='#'){
            return null;
        }
        BinTree Node=new BinTree(ch);
        Node.left=creatBinTree(r);
        Node.right=creatBinTree(r);
        return Node;
    }

    public void pre(){
        System.out.print(data+" ");
        if(left!=null){
            left.pre();
        }
        if(right!=null){
            right.pre();
        }
    }

    public void in(){
        if(left!=null){
            left.in();
        }
        System.out.print(data+" ");
        if(right!=null){
            right.in();
        }
    }

    public void post(){
        if(left!=null){
            left.post();
        }
        if(right!=null){
            right.post();
        }
        System.out.print(data+" ");
    }

    public void preN(){
        Stack stack=new Stack();
        BinTree p=this;
        while(p!=null || !stack.isEmpty()){
            if(p!=null){
                System.out.print(p.data+" ");
                stack.push(p);
                p=p.left;
            }
            else{
                p=stack.pop();
                p=p.right;
            }
        }
    }

    public void inN(){
        Stack stack=new Stack();
        BinTree p=this;
        while(p!=null || !stack.isEmpty()){
            if(p!=null){
                stack.push(p);
                p=p.left;
            }
            else {
                p=stack.pop();
                System.out.print(p.data+" ");
                p=p.right;
            }
        }
    }

    public void postN(){
        Stack stack=new Stack();
        BinTree p=this;
        while(p!=null || !stack.isEmpty()){
            if(p!=null){
                stack.push(p);
                stack.tag[stack.top-1]=1;
                p=p.left;
            }
            else {
                if(stack.tag[stack.top-1]==1){
                    stack.tag[stack.top-1]=2;
                    p=stack.s[stack.top-1].right;
                }
                else {
                    p=stack.pop();
                    System.out.print(p.data+" ");
                    p=null;
                }
            }
        }
    }

    class Stack{
        BinTree[] s=new BinTree[20];
        int[] tag=new int[20];//用于非递归后序
        int top;
        boolean isEmpty(){
            return top==0;}
        void push(BinTree x){
            s[top]=x; top++;}
        BinTree pop(){
            top--;  return s[top];}
    }



}
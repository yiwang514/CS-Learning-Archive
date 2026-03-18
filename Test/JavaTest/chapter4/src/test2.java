import java.util.Random;



class juzhen {
    int [][] data;
    int rows;
    int cols;

    public juzhen(int rows,int cols){
        this.rows=rows;
        this.cols=cols;
        data=new int[rows][cols];

        Random random=new Random(100);
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                data[i][j]=random.nextInt(100);
            }
        }
    }

    juzhen transpose(){
        juzhen result=new juzhen(cols,rows);
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                result.data[j][i]=data[i][j];
            }
        }
        return result;
    }

    juzhen add(juzhen other){
        juzhen result=new juzhen(rows,cols);
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                result.data[i][j]=other.data[i][j]+this.data[i][j];
            }
        }
        return result;
    }

    void print(String name){
        System.out.println(name + ":");
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                System.out.printf("%4d",data[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args){
        juzhen A=new juzhen(3,4);
        juzhen B=new juzhen(3,4);
        A.print("矩阵A");
        B.print("矩阵B");

        juzhen aTranspose=A.transpose();
        aTranspose.print("A的转置");

        juzhen ab = A.add(B);
        ab.print("A+B");
    }


}

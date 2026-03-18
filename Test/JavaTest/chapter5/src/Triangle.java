class Triangel_ks{

    public static void main(String[] args){

        Triangle t1=new Triangle(2,3,4);

        Triangle t2=new Triangle( );

        Triangle t3=new Triangle(t2);

        t1.disp();

        t2.disp();

        t3.disp();

        System.out.println("三角形的面积为："+t3.area());

        System.out.println("三角形的周长为："+t1.per());

        t2.setEdge(4,5,6);

        t2.disp();

        System.out.println(t2.equals(t3));

    }



}

class Triangle {
    double x,y,z;
    Triangle(double x,double y,double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    Triangle(){
        x=2.0;
        y=2.0;
        z=3.0;
    }
    Triangle(Triangle t){
        x=t.x;
        y=t.y;
        z=t.z;
    }
    double per(){
        return x+y+z;
    }
    double area(){
        double p=per()/2;
        return Math.sqrt(p*(p-x)*(p-y)*(p-z));
    }
    void setEdge(double x,double y,double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
     public boolean equals(Triangle t){
        return x==t.x&&y==t.y&&z==t.z;
     }
    public String toString(){
        return "三角形的边为：x=" + x + ",y=" + y + ",z=" + z;
    }
    public void disp(){
        System.out.println(this.toString());
    }

}
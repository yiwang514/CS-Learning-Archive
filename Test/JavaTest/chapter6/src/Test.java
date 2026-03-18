public class Test{

    public static void main(String[] args){

        Triangle t1=new Triangle(2,3,4);

        Triangle t2=new Triangle(3,5);

        Triangle t3=new Triangle(3);

        t1.disp();

        t2.disp();

        t3.disp();

        Rt_Triangle r1=new Rt_Triangle(3,4);

        System.out.println(r1);

        System.out.println("等边三角形的面积为："+t3.area());

        System.out.println("直角三角形的面积为："+r1.area());

    }



}

class Triangle {

    double x,y,z;

    public Triangle(double x,double y,double z) {

        this.x=x;

        this.y=y;

        this.z=z;

    }

    Triangle(double x,double y){

        this(x,x,y);

    }

    Triangle(double x){

        this(x,x,x);

    }

    public double area(){

        double r=(x+y+z)/2;

        return Math.sqrt(r*(r-x)*(r-y)*(r-z));

    }

    public double per(){

        return x+y+z;

    }

    public String toString(){

        return "普通三角形的边为：x="+x+",y="+y+",z="+z;

    }

    void disp(){

        System.out.println(this);

    }

}

class Rt_Triangle extends Triangle{

    public Rt_Triangle(double x,double y){

        super(x,y,Math.sqrt(x*x+y*y));

    }

    public double area(){

        return x*y/2;

    }

    public String toString(){

        return "直角三角形的边为：x="+x+",y="+y+",z="+Math.sqrt(x*x+y*y);

    }

}


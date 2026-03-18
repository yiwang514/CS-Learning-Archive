class Ci_Cy
{
    public static void main(String[] args)
    {
        Cylinder cy1=new Cylinder(2.0,3.0);
        cy1.disVol();
    }
}

class Cylinder
{
    double PI=3.14;
    private double height;
    private Circle circle;

    Cylinder(double radius,double height){
        this.circle=new Circle(radius);
        this.height=height;
    }

    public double getHeight() {
        return height;
    }
    class Circle
    {
        private double radius;
        public Circle(double radius)
        {
            this.radius=radius;
        }
        public double getRadius() {
            return radius;
        }
        public double areaCircle()
        {
            return PI*radius*radius;
        }
        public  double perimeterCircle()
        {
            return 2*PI*radius;
        }


    }
    public double areaCylinder()
    {
        return 2*circle.areaCircle()+circle.perimeterCircle()*height;
    }
    public double getVolume()
    {
        return circle.areaCircle()*height;
    }
    public void disVol()
    {
        System.out.printf("该圆柱体的体积为：%.2f\n",getVolume());
    }
}
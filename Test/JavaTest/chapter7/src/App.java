interface Add{
    void addSalary();
}

class App{

    public static void main(String[] args){

        employee[] emp={new manager("kkk",8900),new saleMan("sss",4000,35000),new worker("eee",20,300)};
        System.out.println("涨工资前各雇员的工资：");
        for(int i=0;i<emp.length;i++)
        {
            System.out.println(emp[i].getName()+"'s salary is :"+emp[i].computeSalary());
        }
        for(int i=0;i<emp.length;i++){emp[i].addSalary(); }
        System.out.println("涨工资后各雇员的工资：");
        for(int i=0;i<emp.length;i++)
        {
            System.out.println(emp[i].getName()+"'s salary is :"+emp[i].computeSalary());
        }
    }
}

abstract class employee implements Add{
    private String name;
    private double salary;
    public String getName() {
        return name;
    }
    public double getSalary() {
        return salary;
    }
    public void setSalary(double salary) {
        this.salary = salary;
    }
    public employee(String name,int salary){
        this.name=name;
        this.salary=salary;
    }


    public abstract double computeSalary();
}

class manager extends employee{

    public manager(String name,int salary){
        super(name,salary);

    }

    @Override
    public double computeSalary(){
        return getSalary();
    };

    public void addSalary(){
        setSalary(getSalary()*1.2);
    }

}

class saleMan extends employee{
    double revenue;

    public saleMan(String name,int salary,double revenue){
        super(name,salary);
        this.revenue=revenue;
    }
    @Override
    public double computeSalary(){
        return getSalary()+revenue*0.1;
    }

    public void addSalary(){
        setSalary(getSalary()*1.1);
    }
}
class worker extends employee{
    int days;
    public worker(String name,int salary,int days){
        super(name,salary);
        this.days=days;
    }
    @Override
    public double computeSalary(){
        return getSalary()*days;
    }
    public void addSalary(){
        setSalary(getSalary()*1.1);
    }
}
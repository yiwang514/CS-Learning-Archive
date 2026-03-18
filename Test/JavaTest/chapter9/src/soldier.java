
class Test{
    public static void main(String[] args){
        soldier[] x={new Spearman(),new Rocketeer(),new Grenader()};
        soldier[] y={new Grenader(),new Rocketeer(),new Spearman() };
        for (int i=0;i<x.length;i++){
            x[i].attack(y[i]);
        }
    }

}

abstract class soldier {
    public String name;
    public String attacktype;
    soldier(String name,String attacktype){
        this.name=name;
        this.attacktype=attacktype;
    }
    public abstract void attack(soldier s);
}
class Spearman extends soldier{
    Spearman(){
        super("枪兵","用枪射击");
    }
    @Override
    public String toString() {
        return this.name;
    }
    public void attack(soldier s){
        System.out.println(this+this.attacktype+s);
    }
}
class Rocketeer extends soldier{
    Rocketeer(){
        super("火箭兵","发射火箭攻击");
    }
    @Override
    public String toString() {
        return this.name;
    }
    public void attack(soldier s){
        System.out.println(this+this.attacktype+s);
    }
}
class Grenader extends soldier{
    Grenader(){
        super("手雷兵","用手雷炸");
    }
    @Override
    public String toString() {
        return this.name;
    }
    public void attack(soldier s){
        System.out.println(this+this.attacktype+s);
    }
}
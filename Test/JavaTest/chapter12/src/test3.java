import java.io.*;

class PrimesFileCopy {
    public static void main(String[] args) {
        try{
            FileWriter fr =new FileWriter("primes.txt");
            for(int i=1;i<=100;i+=1){
                if(isPrime(i)) {
                    fr.write(i+" ");
                }
                fr.write("\n\r");
            }
            fr.close();
        }catch (FileNotFoundException e){
            ;}
        catch(IOException e){
            ;}

    }
    private static boolean isPrime(int n) {
        if(n<=1) return false;
        for (int i = 2; i < n/2; i++) {
            if (n % i == 0) {return false;}
        }
        return true;
    }
}

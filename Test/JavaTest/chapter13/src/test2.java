import java.io.*;

class test2 {
    public static void main(String[] args) {
        try (FileWriter fw=new FileWriter("D:\\javatest\\chapter13\\src\\Fibonacci.txt")){
            int f1 = 1, f2 = 1;
            for (int i = 0; i < 20; i++) {
                fw.write(f1 + " ");
                int next = f1 + f2;
                f1 = f2;
                f2 = next;
            }
        } catch (FileNotFoundException e){;}
          catch (IOException e) {;}
        try (BufferedReader reader = new BufferedReader(new FileReader("D:\\javatest\\chapter13\\src\\Fibonacci.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e){;}
        catch (IOException e) {;}
    }

}

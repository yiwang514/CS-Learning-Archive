import java.io.*;

class test3 {
    public static void main(String[] args) {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream("D:\\javatest\\chapter13\\src\\Fibonacci.dat"))) {
            int f1 = 1, f2 = 1;
            for (int i = 0; i < 20; i++) {
                out.writeInt(f1);
                int next = f1 + f2;
                f1 = f2;
                f2 = next;
            }
        }catch (FileNotFoundException e){;}
        catch (IOException e) {;}

        try (DataInputStream in = new DataInputStream(new FileInputStream("D:\\javatest\\chapter13\\src\\Fibonacci.dat"))) {
            while (true) {
                try {
                    int num = in.readInt();
                    System.out.print(num + " ");
                } catch (EOFException e) {
                    break;
                }
            }
            System.out.println();
        }catch (FileNotFoundException e){;}
        catch (IOException e) {;}
    }
}

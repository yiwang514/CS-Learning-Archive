import java.io.*;

class FileCopy {
    public static void copyFile(String source, String target) {
        try (FileInputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int num;
            while ((num = in.read(buffer)) != -1) {
                out.write(buffer, 0, num);
            }
            System.out.println("文件复制完成。");
        }catch (FileNotFoundException e){;}
        catch (IOException e) {;}
    }

    public static void main(String[] args) {
        String source = "D:\\javatest\\chapter13\\src\\text1.txt";
        String target = "D:\\javatest\\chapter13\\src\\text2.txt";
        copyFile(source, target);
    }
}
import java.awt.event.*;
import java.util.Scanner;
import java.util.Random;
import javax.swing.*;
import java.awt.*;


class SuperSimpleWarshall{
    public static void main(String[] args){
        JFrame frame = new JFrame("Warshall算法");
        frame.setLayout(new FlowLayout());

        JTextField sizeField = new JTextField(5);
        JTextArea inputArea = new JTextArea(8, 10);
        JTextArea outputArea = new JTextArea(8, 10);
        JButton runButton = new JButton("运行");
        inputArea.setEditable(false);
        outputArea.setEditable(false);
        frame.add(new JLabel("矩阵大小:"));
        frame.add(sizeField);
        frame.add(runButton);
        frame.add(new JLabel("原始矩阵"));
        frame.add(new JScrollPane(inputArea));
        frame.add(new JLabel("传递闭包"));
        frame.add(new JScrollPane(outputArea));
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int n = Integer.parseInt(sizeField.getText());
                    boolean[][] matrix = generateRandomMatrix(n);
                    boolean[][] result = warshall(matrix);

                    // 显示结果
                    inputArea.setText(matrixToString(matrix));
                    outputArea.setText(matrixToString(result));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "错误");
                }
            }
        });
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public static boolean[][] generateRandomMatrix(int n) {
        Random random = new Random();
        boolean[][] matrix = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = random.nextBoolean();
            }
        }
        return matrix;
    }
    public static boolean[][] warshall(boolean[][] matrix){
        int n = matrix.length;
        boolean[][] result = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = matrix[i][j];
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (result[i][k] == result[k][j]) {
                        result[i][j] = true;
                    }
                }
            }
        }
        return result;
    }
    public static String matrixToString(boolean[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (boolean[] row : matrix) {
            for (boolean cell : row) {
                sb.append(cell ? "1 " : "0 ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
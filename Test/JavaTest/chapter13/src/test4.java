import java.io.*;

class ListNode implements Serializable {
    int a;
    ListNode next;
    ListNode(int a) {
        this.a = a;
        this.next = null;
    }
}

class test4 {
    public static void printList(ListNode head) {
        ListNode cur = head.next;
        while (cur != null) {
            System.out.print(cur.a + " ");
            cur = cur.next;
        }
        System.out.println("null");
    }

    public static void main(String[] args) {
        ListNode ha = new ListNode(-1);
        ListNode cur = ha;
        for (int i = 1; i <= 5; i++) {
            cur.next = new ListNode(i);
            cur = cur.next;
        }

        System.out.println("原链表 ha：");
        printList(ha);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.dat"))) {
            oos.writeObject(ha);
            System.out.println("链表已序列化保存到 data.dat");
        } catch (FileNotFoundException e){;}
        catch (IOException e) {;}

        ListNode hb = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.dat"))) {
            hb = (ListNode) ois.readObject();
            System.out.println("链表已从文件恢复为 hb");
        } catch (Exception e) {;}

        System.out.println("恢复后的链表 hb：");
        printList(hb);
    }
}
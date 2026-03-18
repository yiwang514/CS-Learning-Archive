class Node {
    int data;
    Node next;

    public Node(int data) {
        this.data = data;
        this.next = null;
    }
}

class LinkedList {
    Node head;

    public LinkedList() {
        head = new Node(0);
    }

    public void createByHeadInsert(int... elements) {
        for (int i = 0; i < elements.length; i++) {
            Node newNode = new Node(elements[i]);
            newNode.next = head.next;
            head.next = newNode;
            head.data++;
        }
    }

    public void insert(int position, int value) {

        if (position < 1 || position > head.data + 1) {
            System.out.println("插入位置不合法！当前位置：" + position + "，链表长度：" + head.data);
            return;
        }

        Node newNode = new Node(value);
        Node current = head;

        for (int i = 1; i < position; i++) {
            current = current.next;
        }

        newNode.next = current.next;
        current.next = newNode;
        head.data++;

        System.out.println("在位置 " + position + " 插入元素 " + value + " 成功");
    }

    public void delete(int position) {
        if (position < 1 || position > head.data) {
            System.out.println("删除位置不合法！当前位置：" + position + "，链表长度：" + head.data);
            return;
        }

        Node current = head;

        for (int i = 1; i < position; i++) {
            current = current.next;
        }
        Node deletedNode = current.next;
        current.next = deletedNode.next;
        head.data--;
        System.out.println("删除位置 " + position + " 的元素 " + deletedNode.data + " 成功");
    }

    public void print() {
        if (head.data == 0) {
            System.out.println("链表为空");
            return;
        }
        System.out.print("链表元素：");
        Node current = head.next;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println("(长度：" + head.data + ")");
    }
    public static LinkedList merge(LinkedList list1, LinkedList list2) {
        LinkedList result = new LinkedList();
        Node p1 = list1.head.next;
        Node p2 = list2.head.next;
        Node current = result.head;


        while (p1 != null && p2 != null) {
            if (p1.data <= p2.data) {
                current.next = new Node(p1.data);
                p1 = p1.next;
            } else {
                current.next = new Node(p2.data);
                p2 = p2.next;
            }
            current = current.next;
            result.head.data++;
        }
        while (p1 != null) {
            current.next = new Node(p1.data);
            p1 = p1.next;
            current = current.next;
            result.head.data++;
        }
        while (p2 != null) {
            current.next = new Node(p2.data);
            p2 = p2.next;
            current = current.next;
            result.head.data++;
        }
        return result;
    }
    public String toString() {
        if (head.data == 0) {
            return "[]";
        }
        String result = "[";
        Node current = head.next;
        while (current != null) {
            result += current.data;
            if (current.next != null) {
                result += ", ";
            }
            current = current.next;
        }
        result += "]";
        return result;
    }
}

class TestLinkedList {
    public static void main(String[] args) {
        System.out.println("=== 测试带头结点的单链表 ===");
        System.out.println("头插法创建链表测试：");
        LinkedList list1 = new LinkedList();
        list1.createByHeadInsert(5, 4, 3, 2, 1);
        System.out.println("头插法创建链表list1：");
        list1.print();
        System.out.println("直接打印list1: " + list1);

        System.out.println("插入操作测试：");
        list1.insert(3, 100);
        list1.print();

        list1.insert(8, 200);
        list1.print();

        list1.insert(10, 300);
        list1.print();

        System.out.println("删除操作测试：");
        list1.delete(3);
        list1.print();

        list1.delete(7);
        list1.print();

        list1.delete(10);
        list1.print();


        System.out.println("合并两个升序链表测试：");
        LinkedList L1 = new LinkedList();
        LinkedList L2 = new LinkedList();


        L1.createByHeadInsert(5, 4, 3, 2, 1);
        L2.createByHeadInsert(8, 7, 6);

        System.out.println("合并前：");
        System.out.println("L1 = " + L1);
        System.out.println("L2 = " + L2);

        LinkedList L3 = LinkedList.merge(L1, L2);
        System.out.println("合并后：");
        System.out.println("L3 = " + L3);
        L3.print();

        System.out.println("空链表测试：");
        LinkedList emptyList = new LinkedList();
        emptyList.print();
        emptyList.insert(1, 10);
        emptyList.print();
        emptyList.delete(1);
    }
}

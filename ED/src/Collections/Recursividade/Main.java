package Collections.Recursividade;

public class Main {
    public static void main(String[] args) {

        LinkedList<Integer> ll1 = new LinkedList<>();
        ll1.add(1);
        ll1.add(2);
        ll1.add(3);
        ll1.add(4);
        ll1.add(5);
        ll1.recurseLoop(ll1.head);
        System.out.println(ll1.recurseLoopBack(ll1.head));

        System.out.println("DOUBLE LINKED LIST\n");
        DoublyLinkedList<Integer> dl1 = new DoublyLinkedList<>();
        dl1.addNode(1);
        dl1.addNode(20);
        dl1.addNode(3);
        dl1.addNode(40);
        dl1.addNode(5);
        dl1.addNode(6);
        dl1.recursividade(dl1.head);
        dl1.recursividadeBack(dl1.tail);


    }
}
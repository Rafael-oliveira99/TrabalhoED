package Collections.ListasIterador;


import Collections.ListasIterador.Classes.OrderedDoubleLinkedList;

public class Main {
    public static void main(String[] args) {
/*
        OrderedListADT<Integer> orderarraylist = new OrderedArrayList<>();
        orderarraylist.add(1);
        orderarraylist.add(2);
        orderarraylist.add(3);
        orderarraylist.add(4);
        orderarraylist.add(5);
        System.out.println(orderarraylist.toString());
        orderarraylist.remove(4);
        System.out.println(orderarraylist.toString());

        UnorderedListADT<Integer> unorderarraylist = new UnorderedArrayList<>();
        unorderarraylist.addToFront(1);
        unorderarraylist.addToRear(2);
        unorderarraylist.addToRear(3);
        unorderarraylist.addToRear(5);
        System.out.println(unorderarraylist.toString());
        unorderarraylist.addAfter(4,3);
        System.out.println(unorderarraylist.toString());

        OrderedListADT<Integer> orderlinkedlist = new OrderedLinkedList<>();
        orderlinkedlist.add(1);
        orderlinkedlist.add(2);
        orderlinkedlist.add(3);
        orderlinkedlist.add(5);
        orderlinkedlist.add(4);
        orderlinkedlist.add(4);
        System.out.println(orderlinkedlist.toString());
        System.out.println(orderlinkedlist.contains(3));

        UnorderedListADT<Integer> unorderlinkedlist = new UnorderedLinkedList<>();
        unorderlinkedlist.addToRear(1);
        unorderlinkedlist.addToRear(2);
        unorderlinkedlist.addToRear(3);
        unorderlinkedlist.addToRear(5);
        System.out.println(unorderlinkedlist.toString());
        unorderlinkedlist.addAfter(4,3);
        System.out.println(unorderlinkedlist.toString());
*/
        OrderedDoubleLinkedList<Integer> ordereddll = new OrderedDoubleLinkedList<>();
        ordereddll.add(10);
        ordereddll.add(11);
        ordereddll.add(9);

        System.out.println(ordereddll.toString());
    }
}
package Collections.ListasIterador.Classes;


import Collections.ListasIterador.Abstratas.LinkedList;
import Collections.ListasIterador.Interfaces.OrderedListADT;

public class OrderedLinkedList<T> extends LinkedList<T> implements OrderedListADT<T> {
    public OrderedLinkedList() {
        super();
    }

    @Override
    public void add(T element) {
        Comparable<T> comparableElement = (Comparable<T>) element;
        Node<T> newNode = new Node<T>(element);
        if (isEmpty()){
            setHead(newNode);
            setTail(newNode);
        }else{
            boolean found= false;
            Node<T> current = getHead();
            while (current.getNext() !=null && !found){
                if(comparableElement.compareTo(current.getNext().getElement())<0){
                    found=true;
                }
                else{
                    current=current.getNext();
                }
            }
            Node<T> temp = current.getNext();
            current.setNext(newNode);
            newNode.setNext(temp);
            if (current == getTail()){
                setTail(newNode);
            }

        }
        count++;
        modCount++;
    }
}

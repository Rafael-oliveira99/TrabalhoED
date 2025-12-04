package Collections.Pesquisas_Sorts.Classes;

import Collections.Pesquisas_Sorts.Interface.OrderedListADT;

import java.lang.reflect.Array;


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
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        T[] result = (T[]) Array.newInstance(Comparable.class, count);
        //T[] result = (T[]) new Object[count];
        Node<T> current = head;
        int i = 0;
        while (current != null) {
            result[i++] = current.element;
            current = current.next;
        }
        return result;
    }
}

package Collections.ListasIterador.Classes;


import Collections.ListasIterador.Abstratas.DoubleLinkedList;
import Collections.ListasIterador.Interfaces.OrderedListADT;

public class OrderedDoubleLinkedList<T>extends DoubleLinkedList<T> implements OrderedListADT<T> {

    public OrderedDoubleLinkedList(){
        super();
    }
    @Override
    public void add(T element) {
        Comparable<T> comparableElement = (Comparable<T>) element;
        DoubleNode<T> newNode = new DoubleNode<T>(element);
        if (isEmpty()){
            setHead(newNode);
            setTail(newNode);
        }else{
            boolean found= false;
            DoubleNode<T> current = getHead();
            while (current !=null && !found){
                if(comparableElement.compareTo(current.getElement())<0){
                    found=true;
                }
                else{
                    current=current.getNext();
                }
            }
            DoubleNode<T> temp = current;
            current.setNext(newNode);
            newNode.setPrevious(current);
            newNode.setNext(temp);
            temp.setPrevious(newNode);
            if (current == getTail()){
                setTail(newNode);
            }
        }
        count++;
        modCount++;
    }
}

package Collections.ListasIterador.Classes;


import Collections.ListasIterador.Abstratas.LinkedList;
import Collections.ListasIterador.Interfaces.UnorderedListADT;

public class LinkedUnorderedList<T> extends LinkedList<T> implements UnorderedListADT<T> {
    @Override
    public void addToFront(T element) {
        Node<T> newNode = new Node<T>(element);
        if (isEmpty()){
            setHead(newNode);
            setTail(newNode);
        }else{
            newNode.setNext(getHead());
            setHead(newNode);
        }
        setCount(getCount()+1);
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        Node<T> newNode = new Node<T>(element);
        if (isEmpty()){
            setHead(newNode);
            setTail(newNode);
        }else{
            getTail().setNext(newNode);
            setTail(newNode);
        }
        setCount(getCount()+1);
        modCount++;
    }

    @Override
    public void addAfter(T element, T target) {
        Node<T> newNode = new Node<T>(element);
        if (isEmpty()) {
            throw new NullPointerException("Lista vazia");
        } else {
            Node<T> current = getHead();
            boolean found = false;
            while (current.getNext() != null && !found){
                if (current.getElement().equals(target)){
                    found = true;
                }else{
                    current = current.getNext();
                }
            }
            if(found){
                newNode.setNext(current.getNext());
                current.setNext(newNode);
            }
            if (current==getTail()){
                setTail(newNode);
            }
        }
        setCount(getCount()+1);
        modCount++;
    }
}

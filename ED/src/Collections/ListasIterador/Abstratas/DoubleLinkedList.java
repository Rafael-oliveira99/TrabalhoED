package Collections.ListasIterador.Abstratas;

import Collections.Exceptions.ElementNotFoundException;
import Collections.Exceptions.EmptyCollectionException;
import Collections.ListasIterador.Interfaces.ListADT;

import java.util.Iterator;

public abstract class DoubleLinkedList<T> implements ListADT<T> {

    protected class DoubleNode<T>{
        protected T element;
        protected DoubleNode<T> next;
        protected DoubleNode<T> previous;
        public DoubleNode(T element){
            this.element=element;
            this.next=null;
            this.previous=null;
        }

        public T getElement() {
            return element;
        }

        public void setElement(T element) {
            this.element = element;
        }

        public DoubleNode<T> getNext() {
            return next;
        }

        public void setNext(DoubleNode<T> next) {
            this.next = next;
        }

        public DoubleNode<T> getPrevious() {
            return previous;
        }

        public void setPrevious(DoubleNode<T> previous) {
            this.previous = previous;
        }

    }

    protected DoubleNode<T> head;
    protected DoubleNode<T> tail;
    protected int count;
    protected transient int modCount=0;

    public DoubleLinkedList(){
        head=null;
        tail=null;
        count=0;
    }

    public DoubleNode<T> getHead() {
        return head;
    }

    public void setHead(DoubleNode<T> head) {
        this.head = head;
    }

    public DoubleNode<T> getTail() {
        return tail;
    }

    public void setTail(DoubleNode<T> tail) {
        this.tail = tail;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getModCount() {
        return modCount;
    }

    public void setModCount(int modCount) {
        this.modCount = modCount;
    }

    @Override
    public T removeFirst() {
        if (count==0)throw new EmptyCollectionException("Lista Vazia");
        DoubleNode<T> temp = head;
        head=head.next;
        head.previous=null;
        count--;
        if (count==0){
            head=null;
            tail=null;
        }
        modCount++;
        return temp.element;
    }

    @Override
    public T removeLast() {
        if (count==0) throw new EmptyCollectionException("Lista Vazia");
        DoubleNode<T> temp=tail;
        tail=tail.previous;
        tail.next=null;
        count--;
        if (count==0){
            head=null;
            tail=null;
        }
        modCount++;
        return temp.element;
    }

    @Override
    public T remove(T Element) throws EmptyCollectionException, ElementNotFoundException {
        DoubleNode<T> current;
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista Vazia");
        } else {
            boolean found = false;
            current = head;
            while (current != null && !found) {
                if (Element.equals(current.element)) {
                    found = true;
                } else {
                    current = current.next;
                }
                if (!found) {
                    throw new ElementNotFoundException("Elemento nao encontrado");
                }
                if (size() == 1) {
                    head = null;
                    tail = null;
                } else if (current.equals(head)) {
                    head = current.next;
                    head.previous = null;
                } else if (current.equals(tail)) {
                    tail = tail.previous;
                    tail.next = null;
                } else {
                    current.previous.next = current.next;
                    current.next.previous = current.previous;
                }
            }
        }modCount++;
        return current.element;
    }

    @Override
    public T first() {
        return head.element;
    }

    @Override
    public T last() {
        return tail.element;
    }

    @Override
    public boolean contains(T target) {
        DoubleNode<T> current = head;
        while (current !=null){
            if (current==target){
                return true;
            }
            current=current.next;
        }
        return false;
    }


    @Override
    public boolean isEmpty() {
        return count==0;
    }

    @Override
    public int size() {
        return count;
    }
    @Override
    public String toString() {
        StringBuilder dlString = new StringBuilder("[");
        DoubleNode<T> current = head;
        while (current != null) {
            dlString.append(current.element);
            current = current.next;
            if (current != null) {
                dlString.append(", ");
            }
        }
        dlString.append("]");
        return dlString.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }
}

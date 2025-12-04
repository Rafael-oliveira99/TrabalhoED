package Collections.Pesquisas_Sorts.Classes;


import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import Collections.Exceptions.EmptyCollectionException;
import Collections.Exceptions.ElementNotFoundException;
import Collections.Pesquisas_Sorts.Interface.ListADT;

public class LinkedList<T> implements ListADT<T> {

    protected class Node<T>{
        protected T element;
        protected Node<T> next;

        public Node(T element) {
            this.element = element;
            this.next = null;
        }

        public T getElement() {
            return element;
        }

        public void setElement(T element) {
            this.element = element;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

    }

    protected Node<T> head;
    protected Node<T> tail;
    protected int count;
    protected transient int modCount=0;

    public LinkedList(){
        head = null;
        tail = null;
        count = 0;
    }

    public Node<T> getHead() {
        return head;
    }

    public void setHead(Node<T> head) {
        this.head = head;
    }

    public Node<T> getTail() {
        return tail;
    }

    public void setTail(Node<T> tail) {
        this.tail = tail;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public T removeFirst() {
        if (count == 0) throw new EmptyCollectionException("Lista Vazia");
        Node<T> temp = head;
        head = head.next;
        temp.next = null;
        count--;
        if (count == 0) {
            head = null;
            tail = null;
        }
        modCount++;
        return temp.element;
    }

    @Override
    public T removeLast() {
        if (count == 0) throw new EmptyCollectionException("Lista Vazia");
        Node<T> temp = head;
        Node<T> pre = head;
        while (temp.next != null) {
            pre = temp;
            temp = temp.next;
        }
        tail= pre;
        tail.next = null;
        count--;
        if (count == 0) {
            head = null;
            tail = null;
        }
        modCount++;
        return temp.element;
    }

    @Override
    public T remove(T Element) throws EmptyCollectionException, ElementNotFoundException {
        if (isEmpty()) {
            throw new EmptyCollectionException("Lista Vazia");
        } else {
            boolean found = false;
            Node<T> prev = null;
            Node<T> current = head;

            while (current != null && !found) {
                if (Element.equals(current.element)) {
                    found = true;
                } else {
                    prev = current;
                    current = current.next;
                }
                if (!found) {
                    throw new ElementNotFoundException("Elemento nao encontrado");
                }
                if (size() == 1) {
                    head = tail = null;
                } else if (current.equals(head)) {
                    head = current.next;
                } else if (current.equals(tail)) {
                    tail = prev;
                    tail.next = null;
                } else {
                    prev.next = current.next;
                    count--;
                    return current.element;
                }
            }
            modCount++;
            return null;
        }
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
       Node<T> current = head;
        while (current !=null){
            if (current.element.equals(target)){
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
        StringBuilder llString = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            llString.append(current.element);
            current = current.next;
            if (current != null) {
                llString.append(", ");
            }
        }
        llString.append("]");
        return llString.toString();
    }

    /////ITERADOR
    @Override
    public Iterator<T> iterator() {
        return new LinkdedListIterator();
    }
    public class LinkdedListIterator implements Iterator<T>{
        Node<T> current = head;
        protected int index=0;
        private int expectedModCount;
        private boolean okToRemove;
        public LinkdedListIterator() {
            this.expectedModCount = modCount;
            this.okToRemove=false;
        }

        @Override
        public boolean hasNext() {
            return index < count;
        }

        @Override
        public T next() {
            checkForComodification();
            okToRemove=true;
            if (!hasNext()) throw new IndexOutOfBoundsException("Nao ha mais elementos");
            T temp = current.element;
            current = current.next;
            index++;
            return temp;
        }

        public void remove(){
        checkForComodification();
        if (current==null) throw new NoSuchElementException();
        if (!okToRemove) throw new IllegalStateException();
            try {
                LinkedList.this.remove(current.getElement());
            } catch (EmptyCollectionException | NoSuchElementException e) {
                throw new RuntimeException(e);
            }
            expectedModCount = modCount;
            okToRemove = false;
        }
        final void checkForComodification(){
            if(modCount != expectedModCount) throw new ConcurrentModificationException();
        }
    }

}

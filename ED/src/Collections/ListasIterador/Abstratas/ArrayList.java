package Collections.ListasIterador.Abstratas;


import Collections.ListasIterador.Interfaces.ListADT;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public abstract class ArrayList <T> implements ListADT<T> {
    protected T[] array;;
    protected int last;
    protected int first;
    protected int count;
    protected transient int modCount=0;
    protected int DEFAULT_CAPACITY = 100;

    public ArrayList() {
        array = (T[]) new Object[DEFAULT_CAPACITY];
        last = -1;
        first = 0;
        count = 0;
    }
    protected void expandCapacity() {
        T[] larger = (T[])(new Object[array.length*2]);
        for (int i = 0; i < array.length; i++) {
            larger[i] = array[first + i];
        }
        first = 0;
        last = size() - 1;
        array = larger;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        T result = array[first];
        array[first] = null;
        first++;
        count--;
        System.arraycopy(array, first, array, 0, array.length-1);
        first = 0;
        last = size() - 1;
        modCount++;
        return result;
    }


    public T removeLast() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        if (count==1) {
            T result = array[first];
            array[first] = null;
            last = -1;
            count--;
            modCount++;
            return result;
        } else {
            T result = array[--count];
            array[count] = null;
            modCount++;
            return result;
        }
    }

    @Override
    public T remove(T Element) {
        int index = find(Element);
        for (int i = index; i < count; i++) {
            array[i] = array[i+1];
        }
        array[count] = null;
        count--;
        modCount++;
        return Element;
    }

    @Override
    public T first() {
        return array[first];
    }

    @Override
    public T last() {
        return array[last];
    }
    public int find(T element) {
        int index = -1;
        for (int i = 0; i < size(); i++) {
            index++;
            if (array[i].equals(element)) {
                break;
            }
        } return index;
    }

    @Override
    public boolean contains(T target) {
        if (isEmpty()) return false;
        for (int i = 0; i < size(); i++) {
            if (array[i].equals(target)) return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return size()==0;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public String toString() {
        StringBuilder arrString = new StringBuilder("[");
        for (int i = 0; i < count; i++) {
            arrString.append(array[first + i]);
            if (i < count - 1) {
                arrString.append(", ");
            }
        }
        arrString.append("]");
        return arrString.toString();
    }

    /////ITERADOR
    @Override
    public Iterator<T> iterator() {

        return new ArrayIterator();
    }
    class ArrayIterator implements Iterator<T>{
        private int index=0;
        private int expectedModCount;
        private boolean okToRemove;
        public ArrayIterator() {
            this.expectedModCount=modCount;
            this.okToRemove=false;
            this.index=0;
        }

        @Override
        public boolean hasNext() {
            return index<size();
        }

        @Override
        public T next() {
            checkForComodification();
            if(!hasNext())throw new IllegalStateException();
            okToRemove=true;
            return array[index++];
        }
        final void checkForComodification(){
            if(modCount != expectedModCount) throw new ConcurrentModificationException();
        }
        public void remove(){
            checkForComodification();
            if(!okToRemove) throw new IllegalStateException();
            index--;
            ArrayList.this.remove(array[index]);
            expectedModCount = modCount;
        }
        }

        public void clear(){
            for (int i = 0; i < size(); i++) {
                array[i]=null;
            }
            count=0;
            modCount++;
        }

}


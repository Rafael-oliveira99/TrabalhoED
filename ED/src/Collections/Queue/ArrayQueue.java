package Collections.Queue;

public class ArrayQueue<T> implements QueueADT<T> {

    private int first;
    private int last;
    private int size;
    private int DEFAULT_CAPACITY=100;
    private T[] queue;


    public ArrayQueue() {
        queue= (T[]) new Object[DEFAULT_CAPACITY];
        first=0;
        last=-1;
        size =0;
    }
    public ArrayQueue(int capacity) {
        queue= (T[]) new Object[capacity];
        first=0;
        last=-1;
        size=0;
    }

    @Override
    public void enqueue(T element) {
        if(size()==queue.length) {expandCapacity();}
        last = (last + 1) % queue.length;
        queue[last] = element;
        size++;
    }

    private void expandCapacity() {
        T[] larger = (T[])(new Object[queue.length*2]);
        for (int i = 0; i < queue.length; i++) {
            larger[i] = queue[(first + i) % queue.length];
        }
        first = 0;
        last = size - 1;
        queue = larger;
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue esta vazia.");
        }
        T element = (T) queue[first];
        first = (first + 1) % queue.length;
        size--;
        return element;
    }

    @Override
    public T first() {
        if(isEmpty()) {throw new IllegalStateException("Queue esta vazia.");
    }
        T element = (T) queue[first];
        return element;
    }

    @Override
    public boolean isEmpty() {
        if (size ==0)return true;
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder arrString = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            arrString.append(queue[(first + i) % queue.length]);
            if (i < size - 1) {
                arrString.append(", ");
            }
        }
        arrString.append("]");
        return arrString.toString();
    }
}

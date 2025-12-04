package Collections.Queue;

public interface QueueADT<T> {
    void enqueue(T element);
    T dequeue();
    T first();
    boolean isEmpty();
    int size();

    @Override
    String toString();


}

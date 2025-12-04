package Collections.Heaps;

public class PriorityQueue<T> extends ArrayHeap<PriorityQueueNode<T>> {
    public PriorityQueue() {
        super();
    }
    public void addElement (T object, int priority) {
        PriorityQueueNode<T> node =
                new PriorityQueueNode<T> (object, priority);
        super.addElement(node);
    }
    public T removeNext() {
        PriorityQueueNode<T> temp =
                (PriorityQueueNode<T>)super.removeMin();
        return temp.getElement();
    }
}


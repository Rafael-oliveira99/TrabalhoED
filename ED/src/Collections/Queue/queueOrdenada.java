package Collections.Queue;

public class queueOrdenada{
    private int first;
    private int last;
    private int size;
    private int DEFAULT_CAPACITY = 100;
    private int[] queue;

    public queueOrdenada() {
        queue =  new int[DEFAULT_CAPACITY];
        first = 0;
        last = -1;
        size = 0;
    }

    public queueOrdenada(int capacity) {
        queue = new int[capacity];
        first = 0;
        last = -1;
        size = 0;
    }

    public static queueOrdenada mergeQueue(QueueADT<Integer> queue1, QueueADT<Integer> queue2) {
        queueOrdenada combinedQueue = new queueOrdenada(queue1.size() + queue2.size());

        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            if(queue1.first()<queue2.first()){
                combinedQueue.enqueue(queue1.dequeue());
            }else{
                combinedQueue.enqueue(queue2.dequeue());
            }
        }

        while (!queue1.isEmpty()) {
            combinedQueue.enqueue(queue1.dequeue());
        }

        while (!queue2.isEmpty()) {
            combinedQueue.enqueue(queue2.dequeue());

        }

        return combinedQueue;
    }



    public void enqueue(int element) {
        if(size()==queue.length) {expandCapacity();}
        last = (last + 1) % queue.length;
        queue[last] = element;
        size++;
    }

    private void expandCapacity() {
        int[] larger = new int[queue.length*2];
        for (int i = 0; i < queue.length; i++) {
            larger[i] = queue[(first + i) % queue.length];
        }
        first = 0;
        last = size - 1;
        queue = larger;
    }


    public int dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue esta vazia.");
        }
        int element =  queue[first];
        first = (first + 1) % queue.length;
        size--;
        return element;
    }


    public int first() {
        if(isEmpty()) {throw new IllegalStateException("Queue esta vazia.");
        }
        int element = queue[first];
        return element;
    }


    public boolean isEmpty() {
        if (size ==0)return true;
        return false;
    }


    public int size() {
        return size;
    }


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

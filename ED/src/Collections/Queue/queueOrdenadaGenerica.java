package Collections.Queue;

public class queueOrdenadaGenerica<T extends Comparable<T>> implements QueueADT<T> {

    private int first;
    private int last;
    private int size;
    private int DEFAULT_CAPACITY=100;
    private T[] queue;


    public queueOrdenadaGenerica() {
        Comparable[] tempQueue = new Comparable[DEFAULT_CAPACITY];
        queue = (T[]) tempQueue;
        first = 0;
        last = -1;
        size = 0;
    }

        public QueueADT<T> merge(QueueADT<T> queue1, QueueADT<T> queue2) {
            QueueADT<T> mergedQueue = new ArrayQueue<>();
            while (!queue1.isEmpty() && !queue2.isEmpty()) {
                int value = queue1.first().compareTo(queue2.first());
                if (value <= 0) {
                    mergedQueue.enqueue(queue1.dequeue());
                } else {
                    mergedQueue.enqueue(queue2.dequeue());
                }
            }

            while (!queue1.isEmpty()) {
                mergedQueue.enqueue(queue1.dequeue());
            }

            while (!queue2.isEmpty()) {
                mergedQueue.enqueue(queue2.dequeue());
            }

            return mergedQueue;

        }

        public QueueADT<T> mergeSort(QueueADT<T> queue) {
            if (queue.size() <= 1) {
                return queue;
            }

            QueueADT<T> left = new ArrayQueue<>();
            QueueADT<T> right = new ArrayQueue<>();

            int mid = queue.size() / 2;
            for (int i = 0; i < mid; i++) {
                left.enqueue(queue.dequeue());
            }
            while (!queue.isEmpty()) {
                right.enqueue(queue.dequeue());
            }

            left = mergeSort(left);
            right = mergeSort(right);

            return merge(left, right);
        }

    public void toChar(String inputString, QueueADT<T> queue) {
        for (char character : inputString.toCharArray()) {
            T element = (T) (Character) character;
            queue.enqueue(element);
        }
    }

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



    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue esta vazia.");
        }
        T element = (T) queue[first];
        first = (first + 1) % queue.length;
        size--;
        return element;
    }


    public T first() {
        if(isEmpty()) {throw new IllegalStateException("Queue esta vazia.");
        }
        T element = (T) queue[first];
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


package Collections.Queue;

public class queueOrdenadaGenerica2<T extends Comparable<T>> {
    ArrayQueue<T> queue = new ArrayQueue<>();

    public void merge(QueueADT<T> queue1, QueueADT<T> queue2) {
        while (!queue1.isEmpty() && !queue2.isEmpty()) {
            if (queue1.first().compareTo(queue2.first()) <= 0) {
                queue.enqueue(queue1.dequeue());
            } else {
                queue.enqueue(queue2.dequeue());
            }
        }

        while (!queue1.isEmpty()) {
            queue.enqueue(queue1.dequeue());
        }

        while (!queue2.isEmpty()) {
            queue.enqueue(queue2.dequeue());
        }
    }
}

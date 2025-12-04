package Collections.Heaps;



public class HeapsDemo {
    public static void main(String[] args) {
        HeapADT<Integer> heap = new LinkedHeap<>();
        heap.addElement(10);
        heap.addElement(5);
        heap.addElement(3);
        heap.addElement(20);
        heap.addElement(30);
        heap.addElement(2);

        System.out.println(heap);
        heap.removeMin();
        System.out.println(heap);
        System.out.println(heap.findMin());
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.addElement(10,1   );
        priorityQueue.addElement(5,2);
        priorityQueue.addElement(3,3);
        priorityQueue.addElement(20,3);
        System.out.println(priorityQueue);
    }

}
package Collections.Queue;

public class QueueDemo {
    public static void main(String[] args) {
       /* LinkedQueue<Integer> LQueue = new LinkedQueue<>(10);

        LQueue.getFirst();
        LQueue.getLast();
        LQueue.getLength();
        LQueue.print();
        LQueue.enqueue(20);
        LQueue.print();
        LQueue.dequeue();
        LQueue.dequeue();
        LQueue.print();


        ArrayQueue<String> ArrQueue = new ArrayQueue<>(5);
        ArrQueue.enqueue("Ola");
        ArrQueue.enqueue("1");
        ArrQueue.enqueue("2");
        ArrQueue.enqueue("3");
        ArrQueue.dequeue();

        System.out.println(ArrQueue.toString());

        Cifra cifra = new Cifra();
        cifra.mensagemQueue("Knowledge is power");
        System.out.println(cifra.toString());
        int[] codigos = {3, 1, 7, 4, 2, 5};
        cifra.codificar(codigos);
        System.out.println(cifra.toString());

        QueueADT<Integer> queue1 = new ArrayQueue<>(5);
        queue1.enqueue(1);
        queue1.enqueue(3);
        queue1.enqueue(5);
        queue1.enqueue(7);
        queue1.enqueue(9);
        QueueADT<Integer> queue2 = new ArrayQueue<>(5);
        queue2.enqueue(0);
        queue2.enqueue(2);
        queue2.enqueue(4);
        queue2.enqueue(6);
        queue2.enqueue(8);
        System.out.println(queue1.toString());
        System.out.println(queue2.toString());
        System.out.println("Merged Queue: " + queueOrdenada.mergeQueue(queue1,queue2));
*/
        String inputA="ola";
        String inputB="teste";
        queueOrdenadaGenerica<Character> Queue1 = new queueOrdenadaGenerica<>();
        queueOrdenadaGenerica<Character> Queue2 = new queueOrdenadaGenerica<>();
        Queue1.toChar(inputA, Queue1);
        Queue2.toChar(inputB, Queue2);
        System.out.println(Queue1.toString());
        System.out.println(Queue2.toString());
        queueOrdenadaGenerica<Character> QueueFinal = new queueOrdenadaGenerica<>();
        System.out.println( QueueFinal.merge(QueueFinal.mergeSort(Queue1),QueueFinal.mergeSort(Queue2)));

        LinkedQueue<Character> queue1 = new LinkedQueue<>();
        LinkedQueue<Character> queue2 = new LinkedQueue<>();
        for (Character c : inputA.toCharArray()) {
            queue1.enqueue(c);
        }
        for (Character c : inputB.toCharArray()) {
            queue2.enqueue(c);
        }

        queueOrdenadaGenerica2<Character> queueFinal = new queueOrdenadaGenerica2<>();
        queueFinal.merge(queue1, queue2);
        System.out.println(queueFinal.queue);
    }
}
package Collections.Queue;

public class LinkedQueue<T> implements QueueADT<T> {

    class Node<T>{
        T value;
        Node <T> next;

        public Node(T value) {
            this.value = value;
        }


    }

    private Node<T> first;
    private Node<T> last;
    private int count;

    public LinkedQueue() {
        first = null;
        last = null;
        count = 0;
    }



    public LinkedQueue(T value) {
        Node<T> newNode = new Node<>(value);
        first = newNode;
        last=newNode;
        count=1;
    }

    public void print(){
        Node<T> temp = first;
        System.out.println("Print Queue: ");
        while(temp!=null){
            System.out.println(temp.value);
            temp=temp.next;
        }
    }

    public void getFirst() {
        System.out.println("First: "+first.value);
    }

    public void getLast() {
        System.out.println("Last: "+last.value);
    }

    public void getLength() {
        System.out.println("Length: "+count);
    }

    @Override
    public void enqueue(T value){
        Node<T> newNode = new Node<>(value);
        if (count==0){
            first=newNode;
            last=newNode;
        }else{
        last.next=newNode;
        last=newNode;
        }
        count++;
    }
    @Override
    public T dequeue() {
        if (count == 0) return null;
        Node<T> temp = first;
        if (count == 1) {
            first = null;
            last = null;
        } else{
            first = first.next;
            temp.next = null;
            }
        count--;
        return temp.value;
    }

    @Override
    public T first() {
        return first.value;
    }

    @Override
    public boolean isEmpty() {
        if (count==0)return true;
        return false;
    }

    @Override
    public int size() {
        return count;
    }


}

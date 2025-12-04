package Collections.Stacks;

public class LinkedStack<T> implements StackADT<T> {

    private Node<T> top;
    private int height;
    class Node<T> {
        T element;
        Node<T> next;

        Node(T element) {
            this.element = element;
        }
    }

    public LinkedStack(){
        top = null;
        height=0;
    }

    public LinkedStack(T element){
        Node<T> newNode = new Node<>(element);
        top = newNode;
        height=1;
    }

    @Override
    public void push(T element) {
        Node<T> newNode = new Node<>(element);
        if(height==0){
            top=newNode;
        }else{
            newNode.next=top;
            top=newNode;
        }
        height++;
    }

    @Override
    public T pop() {
        if (height==0) return null;
        Node<T> temp=top;
        top=top.next;
        temp.next=null;

        height--;
        return temp.element;
    }

    @Override
    public T peek() {
        return top.element;
    }

    @Override
    public boolean isEmpty() {
        if (height==0)return true;
        else return false;
    }

    @Override
    public int size() {
        return height;
    }
    public void printStack(){
        Node<T> temp = top;
        while(temp !=null){
            System.out.println(temp.element);
            temp=temp.next;
        }
    }

    @Override
    public String toString() {
        return "LinkedStack{" +
                "top=" + top +
                ", height=" + height +
                '}';
    }
}

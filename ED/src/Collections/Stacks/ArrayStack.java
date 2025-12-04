package Collections.Stacks;

import Collections.Exceptions.EmptyCollectionException;

import java.util.EmptyStackException;

public class ArrayStack<T> implements StackADT<T> {

    private final int DEFAULT_CAPACITY=100;
    private int top;
    private T[] stack;


    public ArrayStack(){
        top=0;
        stack=(T[])(new Object[DEFAULT_CAPACITY]);
    }

    public ArrayStack(int initialCapacity){
        top=0;
        stack=(T[])(new Object[initialCapacity]);
    }

    @Override
    public void push(T element) {
        if(size()== stack.length)
            expandCapacity();
        stack[top]=element;
        top++;
    }

    public void expandCapacity(){
        T[] larger = (T[])(new Object[stack.length*2]);
        for (int index=0; index< stack.length;index++)
            larger[index]=stack[index];
        stack=larger;
    }

    @Override
    public T pop() throws EmptyCollectionException {
        if(isEmpty())
            throw new EmptyCollectionException("Stack");
        top--;
        T result= stack[top];
        stack[top]=null;
        return result;
    }

    @Override
    public T peek() throws EmptyStackException {
        if (isEmpty())
            throw new EmptyStackException();
        return stack[top-1];
    }

    @Override
    public boolean isEmpty() {
        if(top==0) return true;
        return false;
    }

    @Override
    public int size() {
        return top;
    }
    public void printStack(){
        for(int i=0;i< stack.length;i++){
           T element=stack[i];
            System.out.println(element);
        }
    }

    public String toString(){
        String result = "";
        for (int i=0; i < top; i++)
            result = result + stack[i].toString() + "\n";
        return result;
    }
}

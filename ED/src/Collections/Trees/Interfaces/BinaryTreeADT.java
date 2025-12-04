package Collections.Trees.Interfaces;


import java.util.Iterator;

public interface BinaryTreeADT<T>{
    public T getRoot();
    public boolean isEmpty();
    public int size();
    public boolean contains(T targetElement);
    public T find(T targetElement);
    public String toString();
    public Iterator<T> iteratorInOrder();
    public Iterator<T> iteratorPreOrder();
    public Iterator<T> iteratorPostOrder();
    public Iterator<T> iteratorLevelOrder();

}

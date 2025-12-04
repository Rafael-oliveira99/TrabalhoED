package Collections.Heaps;

import Collections.Trees.Interfaces.BinaryTreeADT;

public interface HeapADT<T> extends BinaryTreeADT<T> {
    public void addElement(T obj);
    public T removeMin();
    public T findMin();
}

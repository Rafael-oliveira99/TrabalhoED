package Collections.Trees.Arvore;


import Collections.Exceptions.ElementNotFoundException;
import Collections.ListasIterador.Classes.ArrayUnorderedList;
import Collections.Queue.ArrayQueue;
import Collections.Queue.QueueADT;
import Collections.Trees.Interfaces.BinaryTreeADT;



import java.util.Iterator;

public class ArrayBinaryTree<T> implements BinaryTreeADT<T> {
    protected int count;
    protected T[] tree;
    private final int CAPACITY = 100;
    /**
     * Creates an empty binary tree.
     */
    public ArrayBinaryTree() {
        count = 0;
        tree = (T[]) new Object[CAPACITY];
    }
    /**
     * Creates a binary tree with the specified element as its root.
     *
     * @param element the element which will become the root
     * of the new tree
     */
    public ArrayBinaryTree (T element) {
        count = 1;
        tree = (T[]) new Object[CAPACITY];
        tree[0] = element;
    }
    public void expandCapacity(){
        T[] temp = (T[]) new Object[tree.length*2];
        for (int ct=0; ct<tree.length; ct++)
            temp[ct] = tree[ct];
        tree = temp;
    }
    @Override
    public T getRoot() {
        return tree[0];
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean contains(T targetElement) {
        try {
            find(targetElement);
            return true;
        }catch (ElementNotFoundException e){
            return false;
        }
    }

    @Override
    public T find (T targetElement) throws ElementNotFoundException {
        T temp=null;
        boolean found = false;
        for (int ct=0; ct<count && !found; ct++)
            if (targetElement.equals(tree[ct])) {
                found = true;
                temp = tree[ct];
            }
        if (!found)
            throw new ElementNotFoundException("binary tree");
        return temp;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "(" + size() + ")[]";
        }

        Iterator<T> treeIterator = iteratorLevelOrder();
        StringBuilder message = new StringBuilder("[");
        int counter = 1;

        while (treeIterator.hasNext()) {
            message.append(treeIterator.next());
            if (counter < size()) {
                message.append(", ");
            } else {
                message.append("]");
            }
            counter++;
        }

        return message.toString();
    }


    protected void inorder(int node, ArrayUnorderedList<T> tempList){
        if(node< tree.length){
            if (tree[node]!=null){
                inorder(node*2+1, tempList);
                tempList.addToRear(tree[node]);
                inorder((node+1)*2, tempList);
            }
        }
    }
    protected void preorder(int node, ArrayUnorderedList<T> tempList){
        if(node< tree.length){
            if(tree[node]!=null){
                tempList.addToRear(tree[node]);
                preorder(node*2+1,tempList);
                preorder((node+1)*2,tempList);
            }
        }
    }
    protected void postorder(int node, ArrayUnorderedList<T> tempList){
        if(node < tree.length){
            if(tree[node] != null){
                postorder(node * 2 + 1, tempList);
                postorder((node + 1) * 2, tempList);
                tempList.addToRear(tree[node]);
            }
        }
    }
    protected void levelorder(int node, ArrayUnorderedList<T> tempList){
        if (node>=tree.length || tree[node]==null){
        throw new ElementNotFoundException("Arvore Vazia");
        }
        QueueADT<Integer> queue = new ArrayQueue<>();
        queue.enqueue(node);

        while (!queue.isEmpty()){
            int current = queue.dequeue();
            tempList.addToRear(tree[current]);
            int leftChild = current * 2 + 1;
            int rightChild = (current + 1) * 2;
            if (leftChild< tree.length && tree[leftChild]!=null){
                queue.enqueue(leftChild);
            }
            if (rightChild<tree.length && tree[rightChild]!=null){
                queue.enqueue(rightChild);
            }
        }
    }

    @Override
    public Iterator<T> iteratorInOrder() {
        ArrayUnorderedList<T> tempList = new ArrayUnorderedList<>();
        inorder(0,tempList);
        return tempList.iterator();
    }

    @Override
    public Iterator<T> iteratorPreOrder() {
        ArrayUnorderedList<T> tempList = new ArrayUnorderedList<>();
        preorder(0,tempList);
        return tempList.iterator();
    }

    @Override
    public Iterator<T> iteratorPostOrder() {
        ArrayUnorderedList<T> tempList = new ArrayUnorderedList<>();
        postorder(0,tempList);
        return tempList.iterator();
    }

    @Override
    public Iterator<T> iteratorLevelOrder() {
    ArrayUnorderedList<T> tempList = new ArrayUnorderedList<>();
    levelorder(0,tempList);
    return tempList.iterator();
    }
}

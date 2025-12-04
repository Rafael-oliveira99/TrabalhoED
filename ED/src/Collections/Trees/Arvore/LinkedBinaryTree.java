package Collections.Trees.Arvore;

import Collections.Exceptions.ElementNotFoundException;

import Collections.ListasIterador.Classes.LinkedUnorderedList;
import Collections.Queue.LinkedQueue;
import Collections.Queue.QueueADT;
import Collections.Trees.Interfaces.BinaryTreeADT;

import java.util.Iterator;
public class LinkedBinaryTree<T> extends BinaryTreeNode<T> implements BinaryTreeADT<T>{
    protected int count;
    protected BinaryTreeNode<T> root;

    public LinkedBinaryTree() {
        super();
        count = 0;
        root = null;
    }

    public LinkedBinaryTree(T element) {
        super();
        count = 1;
        root = new BinaryTreeNode<T>(element);
    }

    // Inner class TreeNode
//    public class BinaryTreeNode<T> {
//        protected T element;
//        protected BinaryTreeNode<T> left, right;
//
//        BinaryTreeNode(T obj) {
//            element = obj;
//            left = null;
//            right = null;
//        }
//
//        public int numChildren() {
//            int children = 0;
//            if (left != null)
//                children = 1 + left.numChildren();
//            if (right != null)
//                children = children + 1 + right.numChildren();
//            return children;
//        }
//    }

    @Override
    public T getRoot() {
        return root.element;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean contains(T targetElement) {
        return find(targetElement) != null;
    }

    @Override
    public T find(T targetElement) throws ElementNotFoundException {
        BinaryTreeNode<T> current = findAgain(targetElement, root);
        if (current == null)
            throw new ElementNotFoundException("binary tree");
        return current.element;
    }

    private BinaryTreeNode<T> findAgain(T targetElement, BinaryTreeNode<T> next) {
        if (next == null)
            return null;
        if (next.element.equals(targetElement))
            return next;
        BinaryTreeNode<T> temp = findAgain(targetElement, next.left);
        if (temp == null)
            temp = findAgain(targetElement, next.right);
        return temp;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "(" + size() + ")[]";
        }
        Iterator<T> treeIterator = iteratorLevelOrder();
        StringBuilder message = new StringBuilder("[");
        int counter = 0;

        while (treeIterator.hasNext()) {
            message.append(treeIterator.next());
            counter++;

            if (counter < size()) {
                message.append(", ");
            } else {
                message.append("]");
            }
        }

        return message.toString();
    }

    protected void inorder(BinaryTreeNode<T> node, LinkedUnorderedList<T> tempList) {
        if (node != null) {
            inorder(node.left, tempList);
            tempList.addToRear(node.element);
            inorder(node.right, tempList);
        }
    }

    protected void preorder(BinaryTreeNode<T> node, LinkedUnorderedList<T> tempList) {
        if (node != null) {
            tempList.addToRear(node.element);
            preorder(node.left, tempList);
            preorder(node.right, tempList);
        }
    }

    protected void postorder(BinaryTreeNode<T> node, LinkedUnorderedList<T> tempList) {
        if (node != null) {
            postorder(node.left, tempList);
            postorder(node.right, tempList);
            tempList.addToRear(node.element);
        }
    }

    protected void levelorder(BinaryTreeNode<T> node, LinkedUnorderedList<T> tempList) {
        if (node==null) {
            throw new ElementNotFoundException("Arvore vazia");
        }
        QueueADT<BinaryTreeNode<T>> queue = new LinkedQueue<>();
        queue.enqueue(node);
        while (!queue.isEmpty()){
        BinaryTreeNode<T> current = queue.dequeue();
        tempList.addToRear(current.element);
        if (current.left!=null){
            queue.enqueue(current.left);
        }
        if (current.right!=null){
            queue.enqueue(current.right);
        }
    }
    }

    @Override
    public Iterator<T> iteratorInOrder() {
        LinkedUnorderedList<T> tempList = new LinkedUnorderedList<>();
        inorder(root, tempList);
        return tempList.iterator();
    }

    @Override
    public Iterator<T> iteratorPreOrder() {
        LinkedUnorderedList<T> tempList = new LinkedUnorderedList<>();
        preorder(root, tempList);
        return tempList.iterator();
    }

    @Override
    public Iterator<T> iteratorPostOrder() {
        LinkedUnorderedList<T> tempList = new LinkedUnorderedList<>();
        postorder(root, tempList);
        return tempList.iterator();
    }

    @Override
    public Iterator<T> iteratorLevelOrder() {
    LinkedUnorderedList<T> tempList = new LinkedUnorderedList<>();
    levelorder(root, tempList);
    return tempList.iterator();
    }


}

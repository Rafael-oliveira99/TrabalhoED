package Collections.Trees.Arvore;

import Collections.Exceptions.ElementNotFoundException;
import Collections.Trees.Interfaces.BinarySearchTreeADT;

public class LinkedBinarySearchTree <T>extends LinkedBinaryTree<T>implements BinarySearchTreeADT<T> {
    public LinkedBinarySearchTree() {
        super();
    }
    public LinkedBinarySearchTree(T element) {
        super(element);
    }
    @Override
    public void addElement(T element) {
    BinaryTreeNode<T> temp = new BinaryTreeNode<T>(element);
    Comparable<T> comparableElement = (Comparable<T>) element;
    if (isEmpty()){
        root=temp;
    }else{
        BinaryTreeNode<T>current=root;
        boolean added = false;
        while(!added){
            if (comparableElement.compareTo(current.element)<0){
               if (current.left == null){
                   current.left=temp;
                   added=true;
                    }else{
                        current=current.left;
                         }
                }else{
                if(current.right==null){
                  current.right=temp;
                  added=true;
                }else{
                    current=current.right;
                }
            }
        }
    }
    count++;
    }

    @Override
    public T removeElement(T targetElement) throws ElementNotFoundException {
        T result = null;
        if (!isEmpty()){
            if (((Comparable)targetElement).equals(root.element)){
                result=root.element;
                root=replacement(root);
                count--;
            }else{
                BinaryTreeNode<T> parent = root;
                boolean found = false;
                while (!found && parent!=null){
                    if (parent.left!=null&&((Comparable)targetElement).compareTo(parent.left.element)==0){
                        found=true;
                        count--;
                        parent.left=replacement(parent.left);
                    }else if (parent.right!=null&&((Comparable)targetElement).compareTo(parent.right.element)==0){
                        found=true;
                        count--;
                        parent.right=replacement(parent.right);
                    }else{
                        if (((Comparable)targetElement).compareTo(parent.element)<0){
                            parent=parent.left;
                        }else{
                            parent=parent.right;
                        }
                    }
                }
                if (!found){
                    throw new ElementNotFoundException("binary search tree");
                }
            }
        }
        return result;
    }
    protected BinaryTreeNode<T> replacement (BinaryTreeNode<T> node){
        BinaryTreeNode<T> result = null;
        if ((node.left==null)&&(node.right==null)){
            result=null;
        }else if ((node.left!=null)&&(node.right==null)){
            result=node.left;
        }
        else if ((node.left==null)&&(node.right!=null)){
            result=node.right;
        }else{
            BinaryTreeNode<T> current=node.right;
            BinaryTreeNode<T> parent=node;
            while (current.left!=null){
                parent=current;
                current=current.left;
            }
            if (node.right==current){
                current.left=node.left;
            }else{
                parent.left=current.right;
                current.right=node.right;
                current.left=node.left;
            }
            result=current;
        }
        return result;
    }

    @Override
    public void removeAllOccurrences(T targetElement) {
        while (contains(targetElement)) {
            try {
                removeElement(targetElement);
            } catch (ElementNotFoundException e) {
                throw new RuntimeException("Erro ao remover todas as ocorrências de um elemento");
            }
        }
    }

    @Override
    public T removeMin() {
    if (isEmpty()){
        return null;
    }
    BinaryTreeNode<T> parent = null;
    BinaryTreeNode<T> current = root;
    while (current.left!=null){
        parent=current;
        current=current.left;
    }
    T result = current.element;
    if (parent==null){
        root=replacement(root);
    }else{
        parent.left=replacement(parent.left);
    }
    count--;
    return result;
    }

    @Override
    public T removeMax() {
        if (isEmpty()){
            return null;
        }
        BinaryTreeNode<T> parent = null;
        BinaryTreeNode<T> current = root;
        while (current.right!=null){
            parent=current;
            current=current.right;
        }
        T result = current.element;
        if (parent==null){
            root=replacement(root);
        }else{
            parent.right=replacement(parent.right);
        }
        count--;
        return result;

    }

    @Override
    public T findMin() {
        if (isEmpty()){
            return null;
        }
        BinaryTreeNode<T> current = root;
        while (current.left!=null){
            current=current.left;
        }
        return current.element;
    }

    @Override
    public T findMax() {
        if (isEmpty()){
            return null;
        }
        BinaryTreeNode<T> current = root;
        while (current.right!=null){
            current=current.right;
        }
        return current.element;
    }
}

package Collections.Trees;


import Collections.Trees.Arvore.ArrayBinarySearchTree;
import Collections.Trees.Arvore.ArrayBinaryTree;
import Collections.Trees.Arvore.LinkedBinarySearchTree;
import Collections.Trees.Arvore.LinkedBinaryTree;
import Collections.Trees.Interfaces.BinarySearchTreeADT;
import Collections.Trees.Interfaces.BinaryTreeADT;

public class TreeDemo {
    public static void main(String[] args) {
        BinarySearchTreeADT<Integer> tree = new LinkedBinarySearchTree<>(10);
        tree.addElement(5);
        tree.addElement(3);
        tree.addElement(2);
        tree.addElement(7);
        tree.addElement(30);
        tree.addElement(20);
        tree.addElement(35);
        tree.addElement(35);
        tree.addElement(70);

        System.out.println(tree);
        System.out.println(tree.removeMax());

        System.out.println(tree);

    }

}
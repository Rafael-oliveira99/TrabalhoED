package Collections.Trees.Arvore;

public class BinaryTreeNode<T> {
    public T element;
    public BinaryTreeNode<T> left;
    public BinaryTreeNode<T> right;

    public BinaryTreeNode() {
        left = null;
        right = null;
    }

    public BinaryTreeNode(T obj) {
        element = obj;
        left = null;
        right = null;
    }




    public int numChildren() {
        int children = 0;
        if (left != null)
            children = 1 + left.numChildren();
        if (right != null)
            children = children + 1 + right.numChildren();
        return children;
    }
}

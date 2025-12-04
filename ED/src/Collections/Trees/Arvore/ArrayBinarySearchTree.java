package Collections.Trees.Arvore;

import Collections.Exceptions.ElementNotFoundException;
import Collections.Trees.Interfaces.BinarySearchTreeADT;

public class ArrayBinarySearchTree<T>extends ArrayBinaryTree<T> implements BinarySearchTreeADT<T>{
    protected int height;
    protected int maxIndex;
    public ArrayBinarySearchTree() {
        super();
        height=0;
        maxIndex=-1;
    }
    public ArrayBinarySearchTree(T element) {
        super(element);
        height=1;
        maxIndex=0;
    }

    @Override
    public void addElement(T element) {
        if (tree.length < maxIndex*2+3) {
            try {
                expandCapacity();
            } catch (Exception e) {
                throw new RuntimeException("Erro ao expandir a capacidade da árvore");
            }

        }
        Comparable<T> tempelement = (Comparable<T>)element;
        if (isEmpty())
        {
            tree[0] = element;
            maxIndex = 0;
        }
        else{
            boolean added = false;
            int currentIndex = 0;
            while (!added)
            {
                if (tempelement.compareTo((tree[currentIndex]) ) < 0)
                {
/** go left */
                    if (tree[currentIndex*2+1] == null)
                    {
                        tree[currentIndex*2+1] = element;
                        added = true;
                        if (currentIndex*2+1 > maxIndex)
                            maxIndex = currentIndex*2+1;
                    }
                    else
                        currentIndex = currentIndex*2+1;
                }else
                {
/** go right */
                    if (tree[currentIndex*2+2] == null)
                    {
                        tree[currentIndex*2+2] = element;
                        added = true;
                        if (currentIndex*2+2 > maxIndex)
                            maxIndex = currentIndex*2+2;
                    }
                    else
                        currentIndex = currentIndex*2+2;
                }
            }
        }
        height = (int)(Math.log(maxIndex + 1) / Math.log(2)) + 1;
        count++;
    }


    @Override
    public T removeElement(T targetElement) {
        if (isEmpty()) {
            throw new ElementNotFoundException("Binary search tree is empty");
        }

        Comparable<T> target = (Comparable<T>) targetElement;
        boolean found = false;
        int currentIndex = 0;
        int parentIndex = -1; // Initialize parentIndex to an invalid value

        // Locate the target element in the array
        while (currentIndex <= maxIndex && !found) {
            if (target.compareTo(tree[currentIndex]) == 0) {
                found = true;
            } else {
                parentIndex = currentIndex;

                if (target.compareTo(tree[currentIndex]) < 0) {
                    currentIndex = currentIndex * 2 + 1; // go left
                } else {
                    currentIndex = currentIndex * 2 + 2; // go right
                }
            }
        }

        if (!found) {
            throw new ElementNotFoundException("Element not found in the binary search tree");
        }

        T result = tree[currentIndex];

        // Case 1: Node to be removed is a leaf node
        if (tree[currentIndex * 2 + 1] == null && tree[currentIndex * 2 + 2] == null) {
            tree[currentIndex] = null;
            if (currentIndex == maxIndex) {
                // Adjust maxIndex if the removed node is the last node
                maxIndex--;
            }
        } else if (tree[currentIndex * 2 + 1] == null || tree[currentIndex * 2 + 2] == null) {
            // Case 2: Node to be removed has one child
            int childIndex = (tree[currentIndex * 2 + 1] != null) ? currentIndex * 2 + 1 : currentIndex * 2 + 2;
            tree[currentIndex] = tree[childIndex];
            tree[childIndex] = null;
        } else {
            // Case 3: Node to be removed has two children
            int successorIndex = findSuccessorIndex(currentIndex * 2 + 2);
            tree[currentIndex] = tree[successorIndex];
            // Recursively remove the successor
            removeSuccessor(successorIndex);
        }

        count--;
        height = (int) (Math.log(maxIndex + 1) / Math.log(2)) + 1;

        return result;
    }

    // Helper method to find the index of the successor node
    private int findSuccessorIndex(int currentIndex) {
        while (tree[currentIndex * 2 + 1] != null) {
            currentIndex = currentIndex * 2 + 1; // Keep going left
        }
        return currentIndex;
    }

    // Helper method to remove the successor node
    private void removeSuccessor(int successorIndex) {
        int parentIndex = (successorIndex - 1) / 2;

        // Find the rightmost node in the left subtree of the successor
        while (tree[successorIndex * 2 + 2] != null) {
            successorIndex = successorIndex * 2 + 2; // Keep going right
        }

        // Move the rightmost node to the position of the removed successor
        tree[successorIndex] = tree[parentIndex * 2 + 1];
        tree[parentIndex * 2 + 1] = null;

        // Update maxIndex if the removed successor was the last node
        if (successorIndex == maxIndex) {
            maxIndex = parentIndex * 2 + 1;
        }
    }

    public void removeAllOccurrences(T targetElement) {
        if (isEmpty()) {
            throw new ElementNotFoundException("Binary search tree is empty");
        }
        while (contains(targetElement)) {
                T foundElement = find(targetElement);
                removeElement(foundElement);
        }
    }
    @Override
    public T removeMin() {
        if (isEmpty()) {
            throw new ElementNotFoundException("Binary search tree is empty");
        }
        int minIndex = findMinIndex(); // Find the index of the leftmost (smallest) element
        T minElement = tree[minIndex];
        // Remove the leftmost element
            removeElement(minElement);
        return minElement;
    }

    // Helper method to find the index of the leftmost (smallest) element
    private int findMinIndex() {
        int currentIndex = 0;
        // Traverse to the leftmost element
        while (tree[currentIndex * 2 + 1] != null) {
            currentIndex = currentIndex * 2 + 1; // Keep going left
        }
        return currentIndex;
    }

    @Override
    public T removeMax() {
        if (isEmpty()) {
            throw new ElementNotFoundException("Binary search tree is empty");
        }
        int maxIndex = findMaxIndex(); // Find the index of the rightmost (largest) element
        T maxElement = tree[maxIndex];
        // Remove the rightmost element
            removeElement(maxElement);
        return maxElement;
    }

    // Helper method to find the index of the rightmost (largest) element
    private int findMaxIndex() {
        int currentIndex = 0;

        // Traverse to the rightmost element
        while (tree[currentIndex * 2 + 2] != null) {
            currentIndex = currentIndex * 2 + 2; // Keep going right
        }

        return currentIndex;
    }

    @Override
    public T findMin() {
        if (isEmpty()) {
            // Tree is empty, no minimum element
            return null;
        }
        T minElement = tree[0]; // Assume the root is the minimum element
        // Check if there is a smaller element in the left subtree
        for (int i = 1; i < count; i = i * 2 + 1) {
            if (tree[i] != null && ((Comparable<T>) tree[i]).compareTo(minElement) < 0) {
                minElement = tree[i];
            }
        }
        return minElement;
    }

    @Override
    public T findMax() {
        if (isEmpty()) {
            // Tree is empty, no maximum element
            return null;
        }

        T maxElement = tree[0]; // Assume the root is the maximum element

        // Check if there is a larger element in the right subtree
        for (int i = 0; i < count; i=i * 2 + 2) {
            if (tree[i] != null && ((Comparable<T>) tree[i]).compareTo(maxElement) > 0) {
                maxElement = tree[i];
            }
        }
        return maxElement;
    }
}

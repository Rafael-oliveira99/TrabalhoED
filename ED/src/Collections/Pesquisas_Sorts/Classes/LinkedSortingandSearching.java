package Collections.Pesquisas_Sorts.Classes;



import java.util.ArrayList;

public class LinkedSortingandSearching {
    public static <T extends Comparable<? super T>> boolean linearSearch(LinkedList<T> list, T target) {
        /*
        boolean found = false;
        LinkedList<T>.Node<T> temp = list.head;
        while (temp!=null && !found){
            if (temp.getElement().compareTo(target)==0){
                found=true;
            }
            temp=temp.getNext();
        }return found;
    */
        return list.contains(target);
    }

    public static <T extends Comparable<? super T>> boolean binarySearch(OrderedLinkedList<T> list, T target) {
        T[] data = list.toArray();
        return ArraySortingandSearching.binarySearch(data, 0, data.length - 1, target);
    }

    public static <T extends Comparable<? super T>> void selectionSort(UnorderedLinkedList<T> data) {
        LinkedList<T>.Node<T> current = data.head;
        while (current != null) {
            LinkedList<T>.Node<T> min = current;
            LinkedList<T>.Node<T> scan = current.getNext();
            while (scan != null) {
                if (scan.getElement().compareTo(min.getElement()) < 0) {
                    min = scan;
                }
                scan = scan.getNext();
            }
            // Troca os elementos do current e min
            T temp = current.getElement();
            current.setElement(min.getElement());
            min.setElement(temp);
            current = current.getNext();
        }
    }

    public static <T extends Comparable<? super T>> void insertionSort (UnorderedLinkedList<T> data) {
        if (data.head==null || data.head.next == null){
            System.out.println("Lista apenas tem um elemento ou esta vazia");
        }
        LinkedList<T>.Node<T> sortList =null;
        LinkedList<T>.Node<T> current = data.head;


        while (current !=null){
            LinkedList<T>.Node<T> next = current.next;
            sortList=sortedInsert(sortList, current);
            current = next;
        }
        data.head=sortList;
     }

    private static<T extends Comparable<? super T>> LinkedList<T>.Node<T> sortedInsert(LinkedList<T>.Node<T> sortedList, LinkedList<T>.Node<T> newNode) {
        if (sortedList == null || sortedList.element.compareTo(newNode.element) >= 0) {
            newNode.next = sortedList;
            return newNode;
        } else {
            LinkedList<T>.Node<T> current = sortedList;
            while (current.next != null && current.next.element.compareTo(newNode.element) < 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
            return sortedList;
        }
    }

    public static <T extends Comparable<? super T>> void bubbleSort(UnorderedLinkedList<T> data) {
        boolean swapped;

        do {
            swapped = false;
            LinkedList<T>.Node<T> current = data.head;
            LinkedList<T>.Node<T> prev = null;

            while (current != null && current.getNext() != null) {
                LinkedList<T>.Node<T> nextNode = current.getNext();

                if (current.getElement().compareTo(nextNode.getElement()) > 0) {
                    // Troca os elementos
                    if (prev == null) {
                        // Se o elemento a ser trocado está no início da lista
                        data.head = nextNode;
                    } else {
                        prev.setNext(nextNode);
                    }

                    current.setNext(nextNode.getNext());
                    nextNode.setNext(current);

                    prev = nextNode;
                    swapped = true;
                } else {
                    prev = current;
                    current = current.getNext();
                }
            }
        } while (swapped);
    }

    public static <T extends Comparable<? super T>> void quickSort(UnorderedLinkedList<T> data) {
        data.head = quickSortRec(data.head);
    }
    private static <T extends Comparable<? super T>> LinkedList<T>.Node<T> quickSortRec(LinkedList<T>.Node<T> head) {
        if (head == null || head.getNext() == null) {
            return head;
        }

        LinkedList<T>.Node<T> pivot = head;
        LinkedList<T>.Node<T> current = head.getNext();
        LinkedList<T>.Node<T> smaller = null;
        LinkedList<T>.Node<T> smallerTail = null;
        LinkedList<T>.Node<T> bigger = null;
        LinkedList<T>.Node<T> biggerTail = null;

        while (current != null) {
            LinkedList<T>.Node<T> next = current.getNext();
            current.setNext(null);

            if (current.getElement().compareTo(pivot.getElement()) < 0) {
                if (smaller == null) {
                    smaller = current;
                    smallerTail = current;
                } else {
                    smallerTail.setNext(current);
                    smallerTail = current;
                }
            } else {
                if (bigger == null) {
                    bigger = current;
                    biggerTail = current;
                } else {
                    biggerTail.setNext(current);
                    biggerTail = current;
                }
            }

            current = next;
        }

        smaller = quickSortRec(smaller);
        bigger = quickSortRec(bigger);

        if (smaller == null) {
            head = pivot;
        } else {
            head = smaller;
            smallerTail.setNext(pivot);
        }

        pivot.setNext(bigger);

        return head;
    }
    public static <T extends Comparable<? super T>> void mergeSort(UnorderedLinkedList<T> data) {
        data.head = mergeSortRec(data.head);
    }
    private static <T extends Comparable<? super T>> LinkedList<T>.Node<T> mergeSortRec(LinkedList<T>.Node<T> head) {
        if (head == null || head.getNext() == null) {
            return head;
        }
        LinkedList<T>.Node<T> middle = getMiddle(head);
        LinkedList<T>.Node<T> nextOfMiddle = middle.getNext();
        middle.setNext(null);

        LinkedList<T>.Node<T> left = mergeSortRec(head);
        LinkedList<T>.Node<T> right = mergeSortRec(nextOfMiddle);

        LinkedList<T>.Node<T> sortedList = merge(left, right);

        return sortedList;
    }
    private static <T extends Comparable<? super T>> LinkedList<T>.Node<T> getMiddle(LinkedList<T>.Node<T> head) {
        if (head == null) {
            return head;
        }
        LinkedList<T>.Node<T> slow = head;
        LinkedList<T>.Node<T> fast = head;
        while (fast.getNext() != null && fast.getNext().getNext() != null) {
            slow = slow.getNext();
            fast = fast.getNext().getNext();
        }
        return slow;
    }
    private static <T extends Comparable<? super T>> LinkedList<T>.Node<T> merge(LinkedList<T>.Node<T> left, LinkedList<T>.Node<T> right) {
        LinkedList<T>.Node<T> result = null;

        if (left == null) {
            return right;
        }

        if (right == null) {
            return left;
        }

        if (left.getElement().compareTo(right.getElement()) <= 0) {
            result = left;
            result.setNext(merge(left.getNext(), right));
        } else {
            result = right;
            result.setNext(merge(left, right.getNext()));
        }

        return result;
    }
}

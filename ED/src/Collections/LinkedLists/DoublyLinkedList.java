package Collections.LinkedLists;



public class DoublyLinkedList<P>{

    class Node<P>{
        P data;
        Node<P> previous;
        Node<P> next;

        public Node(P data) {
            this.data = data;
        }
    }

    private Node<P> head, tail =null ;
    private int contador=0;

    public int getContador() {
        System.out.println("\nNumero de elementos: " + contador);
        return contador;
    }

    public void addNode(P data) {
        Node<P> newNode = new Node(data);
        if(contador == 0) {
            head = tail = newNode;
            head.previous = null;
            tail.next = null;
        }
        else {
            tail.next = newNode;
            newNode.previous = tail;
            tail = newNode;
            tail.next = null;
        }
        contador++;
    }

    public void print() {
        Node<P> current = head;
        if(head == null) {
            System.out.println("List is empty");
            return;
        }
        System.out.println("\nDoubly Linked List: ");
        while(current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
    }

    public Node<P> deleteCabeca(){
        if (contador == 0) {return null;}  //verifica se esta vazio
        Node<P> temp = head;
        head = head.next;
        temp.next = null;
        contador--;
        if(contador==0){
            tail=null;
        }
        return temp;
    }

    public void deleteFim(){
        if (tail == null) {
            return;
        }
        if (head == tail) {
            head = null;
            tail = null;
            contador--;
            return;
        }
        Node temp = tail;
        tail = tail.previous;
        tail.next = null;
        temp.previous = null;
        contador--;
    }

    public void dListVaziaCheck(){
        if(contador==0){
            System.out.println("\nA lista esta vazia");
        }else{
            System.out.println("\nA lista contem " + contador+ " elementos");
        }
    }

    public Node<P> get(int index) {
        if (index < 0 || index >= contador) return null;
        Node<P> temp = head;
        if (index < contador/2) {
            for (int i = 0; i < index; i++) {
                temp = temp.next;
            }
        } else {
            temp = tail;
            for (int i = contador - 1; i > index; i--) {
                temp = temp.previous;
            }
        }
        return temp;
    }


    public void removeNodeByValue(P value) {
        Node<P> current = head;

        while (current != null) {
            if (current.data.equals(value)) {
                if (current.previous != null) {
                    current.previous.next = current.next;
                } else {
                    head = current.next;
                }
                if (current.next != null) {
                    current.next.previous = current.previous;
                } else {
                    tail = current.previous;
                }
                return;
            }
            current = current.next;
        }
    }


    public int countSameElementsAndRemove(P data) {
        Node<P> current = head;
        int count = 0;
        while (current != null) {
            if (current.data.equals(data)) {
                count++;
                removeNodeByValue(current.data);
            }
            current = current.next;
        }
        return count;
    }

    public DoublyLinkedList<P> getPares(){
        DoublyLinkedList<Integer> dLListaPares = new DoublyLinkedList<>();
        Node<P> current = head;

        while(current != null){
            if(current.data instanceof Integer){
                int value= (int) current.data;
            if(value%2==0){
                dLListaPares.addNode((Integer) current.data);
            }
            }
            current=current.next;
        }
        return (DoublyLinkedList<P>) dLListaPares;
    }

    public boolean set(int index, P data) {
        Node<P> temp = get(index);
        if(temp != null) {
            temp.data = data;
            return true;
        }
        return false;
    }

    public Object[] toArray(){
        Object[] arr = new Object[contador];
        Node<P> newNode = head;
        int i = 0;
        while (newNode != null) {
            arr[i]=newNode.data;
            i++;
            newNode=newNode.next;
        }
        return arr;
    }



}

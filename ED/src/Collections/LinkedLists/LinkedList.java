package Collections.LinkedLists;
public class LinkedList<T>{


    public class Node<T> {
        T data;
        Node<T> next;
        Node<T> previous;


        Node(T data) {
            this.data = data;
            this.next = null;
            this.previous=null;
        }
    }


        private Node<T> head;
        private Node<T> tail;
        private int contador;
        public LinkedList(){
            this.head=null;
            this.tail=null;
            contador = 0;
        }


        public Node<T> getElement(int index){
            if(index < 0 || index>= contador){
                return null;
            }
            Node<T> temp=head;
            for(int i=0; i<index;i++){
                temp=temp.next;
            }
            return temp;
        } //Obter elemento em uma posicao especifica

        public boolean setElement(int index, T data){
            Node<T> temp = getElement(index);
            if (temp != null){
                temp.data=data;
                return true;
            }
            return false;
        }

        public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (contador == 0) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
               /* Node<T> current = head;
                while (current.next != null) {
                    current = current.next; */
        }
        contador++;
    } //Add no fim

        public void addFirst(T data){
            Node<T> newNode = new Node<>(data);
            if(contador == 0){
                head=newNode;
                tail=newNode;
            }else{
                newNode.next=head;
                head = newNode;
            }
            contador++;
        } //Add no inicio

        public boolean addtAtIndex(int index, T data){
            if(index<0 || index >contador) return false;
            if(index==0){
                addFirst(data);
                return true;
            }
            if(index==contador){
                add(data);
                return true;
            }

            Node<T> newNode = new Node<>(data);
            Node<T> temp = getElement(index -1);
            newNode.next = temp.next;
            temp.next = newNode;
            contador++;
            return true;
        }//inserir elemento num index especifico

        public void printList(){
            Node<T> current = head;
            System.out.println("Linked List: ");
            while(current != null){
                System.out.print(current.data + " ");
                current = current.next;
            }
            System.out.println();
        } //Print a Lista

        public Node<T> removeAtIndex(int index){
            if(index<0 || index >= contador)return null; // index fora da lista
            if(index==0) return removeFirst(); //posicao 0 do index chama o metodo removefirst
            if(index== contador -1) return removeLast();//ultima posicao do index chama o metodo removelast

            Node<T> previous = getElement(index -1);
            Node<T> temp = previous.next;
            previous.next=temp.next;
            temp.next=null;
            contador--;
            return temp;
        } //remover num index especifico

        public Node<T> removeFirst(){
        if (contador == 0) {return null;}  //verifica se esta vazio
        Node<T> temp = head;
        head = head.next;
        temp.next = null;
        contador--;
        if(contador==0){
            tail=null;
        }
        return temp;
    }//remover no inicio

        public Node<T> removeLast() {
        if (contador == 0) return null;
        Node<T> temp = head;
        Node<T> pre = head;
        while(temp.next != null) {
            pre = temp;
            temp = temp.next;
        }
        tail = pre;
        tail.next = null;
        contador--;
        if (contador == 0) {
            head = null;
            tail = null;
        }
        return temp;
    }//remover no fim

        public void reverseList() {
        Node<T> prev = null;
        Node<T> current = head;
        Node<T> next = null;

        while (current != null) {
            next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }

        head = prev;
    }
        public void reverse() {
        Node<T> temp = head;
        head = tail;
        tail = temp;
        Node<T> after = temp.next;
        Node<T> before = null;
        for (int i = 0; i < contador; i++) {
            after = temp.next;
            temp.next = before;
            before = temp;
            temp = after;
        }
    }


}

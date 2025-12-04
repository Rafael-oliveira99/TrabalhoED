package Collections.Pesquisas_Sorts.Classes;


public class Main {
    public static void main(String[] args) {
    /*
    Carro Carro1 = new Carro("Fiat","500", 50000);
    Carro Carro2 = new Carro("Fiat","Uno", 20000);
    Carro Carro3 = new Carro("BMW","X1", 100000);

    OrderedLinkedList<Carro> llsearch = new OrderedLinkedList<>();
    llsearch.add(Carro1);
    llsearch.add(Carro2);
    llsearch.add(Carro3);
    System.out.println(LinkedSortingandSearching.binarySearch(llsearch,Carro2));
    */
    Contactos c1 = new Contactos("Joao", 913456789);
    Contactos c2 = new Contactos("Ana", 962345678);
    Contactos c3 = new Contactos("Pedro", 932345678);
    Contactos c4 = new Contactos("Antonio", 923456789);

    UnorderedLinkedList<Contactos> ListaContactos = new UnorderedLinkedList<>();

    ListaContactos.addToRear(c1);
    ListaContactos.addToRear(c2);
    ListaContactos.addToRear(c3);
    ListaContactos.addToRear(c4);
    System.out.println(ListaContactos);
    LinkedSortingandSearching.mergeSort(ListaContactos);
    System.out.println(ListaContactos);
    }
}
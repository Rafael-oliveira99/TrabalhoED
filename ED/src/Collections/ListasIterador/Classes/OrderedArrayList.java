package Collections.ListasIterador.Classes;


import Collections.ListasIterador.Abstratas.ArrayList;
import Collections.ListasIterador.Interfaces.OrderedListADT;

public class OrderedArrayList<T> extends ArrayList<T> implements OrderedListADT<T> {
    public OrderedArrayList() {
        super();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void add(T element) {
        Comparable<T> comparableElement = (Comparable<T>) element;
        if(count==array.length){
            expandCapacity();
        }
        if(count==0){
            array[first]=element;
            last=first;
            count++;
        }else{
        boolean found = false;
        int i;
        for (i = 0 ; i < count && !found ; i++){
                if(comparableElement.compareTo(array[i])<0){
                    found = true;
                }
        }
        for (int j = count ; j > i ; j--){
            array[j]=array[j-1];
        }
        array[i]=element;
        count++;
        modCount++;
    }}

}

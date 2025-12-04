package Collections.ListasIterador.Classes;


import Collections.ListasIterador.Abstratas.ArrayList;
import Collections.ListasIterador.Interfaces.UnorderedListADT;

public class ArrayUnorderedList<T> extends ArrayList<T> implements UnorderedListADT<T> {

    @Override
    public void addToFront(T element) {
        if (count==array.length){
            expandCapacity();
        }
        if (isEmpty()){
            array[first]=element;
            last=first;
            count++;
        }else{
            for (int i = count ; i > 0 ; i--){
                array[i]=array[i-1];
            }
            array[first]=element;
            count++;
            modCount++;
        }
    }

    @Override
    public void addToRear(T element) {
        if (count==array.length){
            expandCapacity();
        }
        if (isEmpty()){
            array[first]=element;
            last=first;

        }else{
            array[count]=element;
            last=count;
        }
        count++;
        modCount++;
    }

    @Override
    public void addAfter(T element, T target) {
        if (count == array.length) {
            expandCapacity();
        }
        if (isEmpty()) {
            throw new RuntimeException("Lista vazia");
        } else {
            int index = find(target);
            for (int i = count ; i > index+1 ; i--){
                array[i]=array[i-1];
            }
            array[index+1] = element;
            last = count;
            count++;
            modCount++;
        }
        }


    }


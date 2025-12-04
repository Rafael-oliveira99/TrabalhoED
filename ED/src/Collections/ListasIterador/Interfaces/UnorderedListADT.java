package Collections.ListasIterador.Interfaces;

public interface UnorderedListADT<T> extends ListADT<T>{
    void addToFront(T element);
    void addToRear(T element);
    void addAfter(T element, T target);
}

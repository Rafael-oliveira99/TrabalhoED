package Collections.Pesquisas_Sorts.Interface;

import java.util.Iterator;

public interface ListADT<T>extends Iterable<T>{
  T removeFirst();
  T removeLast();
  T remove(T Element);
  T first();
  T last();
  boolean contains(T target);
  boolean isEmpty();
  int size();

  Iterator<T> iterator();

  String toString();



}

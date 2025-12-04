package Collections.Grafos;

import java.util.Iterator;

public interface GraphADT<T> {
    void addVertex(T vertex);
    void removeVertex(T vertex);
    void addEdge(T vertex1, T vertex2);
    void removeEdge(T vertex1, T vertex2);
    Iterator<T> iteratorBFS(T startVertex);
    Iterator<T> iteratorDFS(T startVertex);
    Iterator<T> iteratorShortestPath(T startVertex, T targetVertex);
    boolean isEmpty();
    boolean isConnected();
    int size();
    String toString();

}

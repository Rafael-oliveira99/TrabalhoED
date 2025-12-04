package Collections.Grafos;

import Collections.ListasIterador.Classes.ArrayUnorderedList;
import Collections.Queue.LinkedQueue;
import Collections.Stacks.LinkedStack;

import java.util.Iterator;

public class Graph<T> implements GraphADT<T> {
    protected final int DEFAULT_CAPACITY = 10;
    protected int numVertices;
    protected boolean[][] adjMatrix;
    protected T[] vertices;

    public Graph() {
        numVertices = 0;
        this.adjMatrix = new boolean[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
        this.vertices = (T[]) new Object[DEFAULT_CAPACITY];
    }

    //METODOS SUPORT
    public void expandCapacity() {
        T[] temp = (T[]) new Object[vertices.length * 2];
        boolean[][] tempMatrix = new boolean[vertices.length * 2][vertices.length * 2];
        for (int i = 0; i < vertices.length; i++) {
            temp[i] = vertices[i];
            for (int j = 0; j < vertices.length; j++) {
                tempMatrix[i][j] = adjMatrix[i][j];
            }
        }
        vertices = temp;
        adjMatrix = tempMatrix;
    }

    public int getIndex(T vertex) {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[i].equals(vertex)) {
                return i;
            }
        }
        return -1;
    }

    public boolean indexIsValid(int index) {
        return (index < numVertices && index >= 0);
    }

    @Override
    public void removeVertex (T vertex){
        int index = getIndex(vertex);
        if (indexIsValid(index)) {

            for (int i = index; i < numVertices - 1; i++) {
                vertices[i] = vertices[i + 1];
            }


            for (int i = index; i < numVertices - 1; i++) {
                for (int j = 0; j < numVertices; j++) {
                    adjMatrix[i][j] = adjMatrix[i + 1][j];
                }
            }
            for (int i = index; i < numVertices - 1; i++) {
                for (int j = 0; j < numVertices; j++) {
                    adjMatrix[j][i] = adjMatrix[j][i + 1];
                }
            }

            numVertices--;
        }

    }

    @Override
    public void addEdge(T vertex1, T vertex2) {
        addEdge(getIndex(vertex1), getIndex(vertex2));
    }

    public void addEdge(int vertex1, int vertex2) {
        if (indexIsValid(vertex1) && indexIsValid(vertex2)) {
            adjMatrix[vertex1][vertex2] = true;
            adjMatrix[vertex2][vertex1] = true;
        }
    }

    @Override
    public void addVertex(T vertex) {
        if (numVertices == vertices.length) {
            expandCapacity();
        }
        vertices[numVertices] = vertex;
        for (int i = 0; i <= numVertices; i++) {
            adjMatrix[numVertices][i] = false;
            adjMatrix[i][numVertices] = false;
        }
        numVertices++;
    }

    @Override
    public void removeEdge(T vertex1, T vertex2) {
        removeEdge(getIndex(vertex1), getIndex(vertex2));
    }
    public void removeEdge(int vertex1, int vertex2) {
        if (indexIsValid(vertex1) && indexIsValid(vertex2)) {
            adjMatrix[vertex1][vertex2] = false;
            adjMatrix[vertex2][vertex1] = false;
        }
    }
    @Override
    public Iterator<T> iteratorBFS(T startVertex) {
       Integer x;
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<Integer>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<T>();
        if (!indexIsValid(getIndex(startVertex)))
            return resultList.iterator();

        boolean[] visited = new boolean[numVertices];
        for (int i = 0; i < numVertices; i++)
            visited[i] = false;
        traversalQueue.enqueue(getIndex(startVertex));
        visited[getIndex(startVertex)] = true;

        while (!traversalQueue.isEmpty()) {
            x = traversalQueue.dequeue();
            resultList.addToRear(vertices[x]);
            for (int i = 0; i < numVertices; i++) {
                if (adjMatrix[x][i] && !visited[i]) {
                    traversalQueue.enqueue(i);
                    visited[i] = true;
                }
            }
        }
        return resultList.iterator();
    }

    @Override
    public Iterator<T> iteratorDFS(T startVertex) {
        Integer x;
        boolean found;
        LinkedStack<Integer> traversalStack = new LinkedStack<Integer>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<T>();
        boolean[] visited = new boolean[numVertices];
        if (!indexIsValid(getIndex(startVertex)))
            return resultList.iterator();
        for (int i = 0; i < numVertices; i++)
            visited[i] = false;
        traversalStack.push(getIndex(startVertex));
        resultList.addToRear(vertices[getIndex(startVertex)]);
        visited[getIndex(startVertex)] = true;

        while (!traversalStack.isEmpty()) {
            x = traversalStack.peek();
            found = false;
            /** Find a vertex adjacent to x that has not been visited
             * and push it on the stack */
            for (int i = 0; (i < numVertices) && !found; i++) {
                if (adjMatrix[x][i] && !visited[i]) {
                    traversalStack.push(i);
                    resultList.addToRear(vertices[i]);
                    visited[i] = true;
                    found = true;
                }
            }
            if (!found && !traversalStack.isEmpty())
                traversalStack.pop();
        }
        return resultList.iterator();
    }

    @Override
    public Iterator<T> iteratorShortestPath(T startVertex, T targetVertex) {
        Integer x;
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<Integer>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<T>();
        int[] pathLength = new int[numVertices];
        int i, index;
        boolean[] visited = new boolean[numVertices];
        boolean found;

        if (!indexIsValid(getIndex(startVertex)) || !indexIsValid(getIndex(targetVertex)))
            return resultList.iterator();
        for (i = 0; i < numVertices; i++) {
            visited[i] = false;
            pathLength[i] = -1;
        }
        index = getIndex(startVertex);
        traversalQueue.enqueue(index);
        visited[index] = true;
        pathLength[index] = 0;

        while (!traversalQueue.isEmpty() && !visited[getIndex(targetVertex)]) {
            x = traversalQueue.dequeue();
            for (i = 0; i < numVertices; i++) {
                if (adjMatrix[x][i] && !visited[i]) {
                    pathLength[i] = 1 + pathLength[x];
                    traversalQueue.enqueue(i);
                    visited[i] = true;
                }
            }
        }
        if (!visited[getIndex(targetVertex)])
            return resultList.iterator();

        LinkedStack<T> stack = new LinkedStack<T>();
        index = getIndex(targetVertex);
        stack.push(vertices[index]);
        i = pathLength[index] - 1;
        while (i >= 0) {
            found = false;
            for (int j = 0; (j < numVertices) && !found; j++) {
                if ((adjMatrix[index][j]) && (pathLength[j] == i)) {
                    stack.push(vertices[j]);
                    index = j;
                    found = true;
                }
            }
            i--;
        }
        while (!stack.isEmpty())
            resultList.addToRear(stack.pop());
        return resultList.iterator();
    }

    @Override
    public boolean isEmpty() {
        return (numVertices== 0);
    }

    @Override
    public boolean isConnected() {
        if(isEmpty()){
            return false;
        }
        Iterator<T> it = iteratorBFS(vertices[0]);
        int count = 0;
        while(it.hasNext()){
            it.next();
            count++;
        }
        return (count == numVertices);
    }

    @Override
    public int size() {
        return numVertices;
    }

    public String toString() {
        if (numVertices == 0) {
            return "Graph is empty";
        }

        String result = "";

        result += "Adjacency Matrix\n";
        result += "----------------\n";
        result += "index\t";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i;
            if (i < 10) {
                result += " ";
            }
        }
        result += "\n\n";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i + "\t";

            for (int j = 0; j < numVertices; j++) {
                if (adjMatrix[i][j]) {
                    result += "1 ";
                } else {
                    result += "0 ";
                }
            }
            result += "\n";
        }

        result += "\n\nVertex Values";
        result += "\n-------------\n";
        result += "index\tvalue\n\n";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i + "\t";
            result += vertices[i].toString() + "\n";
        }
        result += "\n";
        return result;
    }

}

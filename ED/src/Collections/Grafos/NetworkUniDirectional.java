package Collections.Grafos;

import Collections.Exceptions.EmptyCollectionException;
import Collections.Heaps.ArrayHeap;
import Collections.Heaps.LinkedHeap;
import Collections.ListasIterador.Classes.ArrayUnorderedList;
import Collections.Queue.LinkedQueue;
import Collections.Stacks.LinkedStack;

import java.util.Iterator;

public class NetworkUniDirectional<T> implements NetworkADT<T> {
    protected final int DEFAULT_CAPACITY = 10;
    protected int numVertices;
    protected double[][] adjMatrix;
    protected T[] vertices;

    public NetworkUniDirectional() {
        numVertices = 0;
        this.vertices = (T[]) new Object[DEFAULT_CAPACITY];
        this.adjMatrix = new double[DEFAULT_CAPACITY][DEFAULT_CAPACITY];
        for (int i = 0; i < DEFAULT_CAPACITY; i++) {
            for (int j = 0; j < DEFAULT_CAPACITY; j++) {
                adjMatrix[i][j] = Double.POSITIVE_INFINITY;
            }
        }
    }

    public boolean indexIsValid(int index) {
        return (index < numVertices && index >= 0);
    }

    protected void expandCapacity() {
        T[] largerVertices = (T[]) (new Object[vertices.length * 2]);
        double[][] largerAdjMatrix = new double[vertices.length * 2][vertices.length * 2];
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                largerAdjMatrix[i][j] = adjMatrix[i][j];
            }
            largerVertices[i] = vertices[i];
        }

        vertices = largerVertices;
        adjMatrix = largerAdjMatrix;
    }

    public int getIndex(T vertex) {
        for (int i = 0; i < numVertices; i++) {
            if (vertices[i].equals(vertex)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void addVertex(T vertex) {
        if (numVertices == vertices.length) {
            expandCapacity();
        }

        vertices[numVertices] = vertex;
        for (int i = 0; i <= numVertices; i++) {
            adjMatrix[numVertices][i] = Double.POSITIVE_INFINITY;
            adjMatrix[i][numVertices] = Double.POSITIVE_INFINITY;
        }
        numVertices++;
    }

    @Override
    public void removeVertex(T vertex) {
        int indexToRemove = getIndex(vertex);
        if (indexToRemove == -1) {
            return; // Vertex not found, exit the method
        }

        // Shift vertices left to remove the vertex
        for (int i = indexToRemove; i < numVertices - 1; i++) {
            vertices[i] = vertices[i + 1];
        }

        // Shift rows up in the adjacency matrix
        for (int i = indexToRemove; i < numVertices - 1; i++) {
            for (int j = 0; j < numVertices; j++) {
                adjMatrix[i][j] = adjMatrix[i + 1][j];
            }
        }

        // Shift columns left in the adjacency matrix
        for (int i = 0; i < numVertices; i++) {
            for (int j = indexToRemove; j < numVertices - 1; j++) {
                adjMatrix[i][j] = adjMatrix[i][j + 1];
            }
        }

        numVertices--;
    }


    public void addEdge(T vertex1, T vertex2) {
        addEdge(getIndex(vertex1), getIndex(vertex2), 0);
    }

    private void addEdge(int index, int index1, int weight) {
        if (indexIsValid(index) && indexIsValid(index1)) {
            adjMatrix[index][index1] = weight;
        }
    }


    @Override
    public void addEdge(T vertex1, T vertex2, double weight) {
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);
        if (indexIsValid(index1) && indexIsValid(index2)) {
            adjMatrix[index1][index2] = weight; // Only set the weight for one direction
        }
    }

    @Override
    public void removeEdge(T vertex1, T vertex2) {
        int index1 = getIndex(vertex1);
        int index2 = getIndex(vertex2);

        if (indexIsValid(index1) && indexIsValid(index2)) {
            adjMatrix[index1][index2] = Double.POSITIVE_INFINITY; // Only one direction
        }
    }

    @Override
    public Iterator<T> iteratorBFS(T startVertex) {
        int startIndex = getIndex(startVertex);
        Integer x;
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();

        if (!indexIsValid(startIndex)) {
            return resultList.iterator();
        }

        boolean[] visited = new boolean[numVertices];
        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
        }

        traversalQueue.enqueue(startIndex);
        visited[startIndex] = true;

        while (!traversalQueue.isEmpty()) {
            x = traversalQueue.dequeue();
            resultList.addToRear(vertices[x]);


            for (int i = 0; i < numVertices; i++) {
                if ((adjMatrix[x][i] < Double.POSITIVE_INFINITY) && !visited[i]) {
                    traversalQueue.enqueue(i);
                    visited[i] = true;
                }
            }
        }
        return resultList.iterator();
    };

    @Override
    public Iterator<T> iteratorDFS(T startVertex) {
        int startIndex = getIndex(startVertex);
        Integer x;
        boolean found;
        LinkedStack<Integer> traversalStack = new LinkedStack<>();
        ArrayUnorderedList<T> resultList = new ArrayUnorderedList<>();
        boolean[] visited = new boolean[numVertices];

        if (!indexIsValid(startIndex)) {
            return resultList.iterator();
        }

        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
        }

        traversalStack.push(startIndex);
        resultList.addToRear(vertices[startIndex]);
        visited[startIndex] = true;

        while (!traversalStack.isEmpty()) {
            x = traversalStack.peek();
            found = false;


            for (int i = 0; (i < numVertices) && !found; i++) {
                if ((adjMatrix[x][i] < Double.POSITIVE_INFINITY) && !visited[i]) {
                    traversalStack.push(i);
                    resultList.addToRear(vertices[i]);
                    visited[i] = true;
                    found = true;
                }
            }
            if (!found && !traversalStack.isEmpty()) {
                traversalStack.pop();
            }
        }
        return resultList.iterator();
    }

    @Override
    public Iterator<T> iteratorShortestPath(T startVertex, T targetVertex) {
        int startIndex = getIndex(startVertex);
        int targetIndex = getIndex(targetVertex);

        Iterator<Integer> pathIndices = iteratorShortestPathIndices(startIndex, targetIndex);
        ArrayUnorderedList<T> path = new ArrayUnorderedList<>();

        while (pathIndices.hasNext()) {
            path.addToRear(vertices[pathIndices.next()]);
        }

        return path.iterator();
    }

    protected Iterator<Integer> iteratorShortestPathIndices(int startIndex, int targetIndex) {
        double[] distances = new double[numVertices];
        int[] previous = new int[numVertices];
        ArrayHeap<Double> heap = new ArrayHeap<>();

        for (int i = 0; i < numVertices; i++) {
            distances[i] = Double.POSITIVE_INFINITY;
            previous[i] = -1;
        }
        distances[startIndex] = 0;
        heap.addElement((double) startIndex);

        while (!heap.isEmpty()) {
            int currentVertex = heap.removeMin().intValue();

            for (int neighbor = 0; neighbor < numVertices; neighbor++) {
                if (adjMatrix[currentVertex][neighbor] < Double.POSITIVE_INFINITY) {
                    double potentialDistance = distances[currentVertex] + adjMatrix[currentVertex][neighbor];
                    if (potentialDistance < distances[neighbor]) {
                        distances[neighbor] = potentialDistance;
                        previous[neighbor] = currentVertex;
                        heap.addElement((double) neighbor);
                    }
                }
            }
        }

        ArrayUnorderedList<Integer> path = new ArrayUnorderedList<>();
        for (int vertex = targetIndex; vertex != -1 && vertex < previous.length; vertex = (vertex == -1) ? -1 : previous[vertex]) {
            path.addToFront(vertex);
        }

        if (distances[targetIndex] == Double.POSITIVE_INFINITY) {
            return new ArrayUnorderedList<Integer>().iterator(); // No path exists
        }

        return path.iterator();
    }

    @Override
    public double shortestPathWeight(T startVertex, T targetVertex) {
            if (isEmpty()) throw new EmptyCollectionException("Network is empty");
            int startIndex = getIndex(startVertex);
            int targetIndex = getIndex(targetVertex);

            double result = 0;
            int index1, index2;
            Iterator<Integer> it = iteratorShortestPathIndices(startIndex, targetIndex);
            if (!it.hasNext()) return Double.POSITIVE_INFINITY;

            index1 = it.next();
            while (it.hasNext()) {
                index2 = it.next();
                result += adjMatrix[index1][index2];
                index1 = index2;
            }

            return result;
        }

    @Override
    public boolean isEmpty() {
        return numVertices==0;
    }

    @Override
    public boolean isConnected() {
        if (isEmpty()) {
            return false;
        }
        Iterator<T> it = iteratorBFS(vertices[0]);
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return (count == numVertices);
    }

    @Override
    public int size() {
        return numVertices;
    }

    @Override
    public String toString() {
        if (numVertices == 0)
            return "Graph is empty";

        String result = new String("");

        /** Print the adjacency Matrix */
        result += "Adjacency Matrix\n";
        result += "----------------\n";
        result += "index\t";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i;
            if (i < 10)
                result += " ";
        }
        result += "\n\n";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i + "\t";

            for (int j = 0; j < numVertices; j++) {
                if (adjMatrix[i][j] < Double.POSITIVE_INFINITY)
                    result += "1 ";
                else
                    result += "0 ";
            }
            result += "\n";
        }

        /** Print the vertex values */
        result += "\n\nVertex Values";
        result += "\n-------------\n";
        result += "index\tvalue\n\n";

        for (int i = 0; i < numVertices; i++) {
            result += "" + i + "\t";
            result += vertices[i].toString() + "\n";
        }

        /** Print the weights of the edges */
        result += "\n\nWeights of Edges";
        result += "\n----------------\n";
        result += "index\tweight\n\n";

        for (int i = 0; i < numVertices; i++) {
            for (int j = numVertices - 1; j > i; j--) {
                if (adjMatrix[i][j] < Double.POSITIVE_INFINITY) {
                    result += i + " to " + j + "\t";
                    result += adjMatrix[i][j] + "\n";
                }
            }
        }

        result += "\n";
        return result;
    }

    public NetworkUniDirectional<T> mstNetwork() {
        int x, y;
        int index;
        double weight;
        int[] edge = new int[2];
        LinkedHeap<Double> minHeap = new LinkedHeap<Double>();
        NetworkUniDirectional<T> resultGraph = new NetworkUniDirectional<T>();

        if (isEmpty() || !isConnected()) {
            return resultGraph;
        }
        resultGraph.adjMatrix = new double[numVertices][numVertices];
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; i++) {
                resultGraph.adjMatrix[i][j] = Double.POSITIVE_INFINITY;
            }
            resultGraph.vertices = (T[]) (new Object[numVertices]);
        }
        boolean[] visited = new boolean[numVertices];
        for (int i = 0; i < numVertices; i++) {
            visited[i] = false;
        }
        edge[0] = 0;
        resultGraph.vertices[0] = this.vertices[0];
        resultGraph.numVertices++;
        visited[0] = true;

        /**
         * Add all edges, which are adjacent to the starting vertex, to the heap
         */
        for (int i = 0; i < numVertices; i++) {
            minHeap.addElement(adjMatrix[0][i]);
        }

        while ((resultGraph.size() < this.size()) && !minHeap.isEmpty()) {
            /**
             * Get the edge with the smallest weight that has exactly one vertex
             * already in the resultGraph
             */
            do {
                weight = minHeap.removeMin();
                edge = getEdgeWithWeightOf(weight, visited);
            } while (!indexIsValid(edge[0]) || !indexIsValid(edge[1]));

            x = edge[0];
            y = edge[1];
            if (!visited[x]) {
                index = x;
            } else {
                index = y;
            }

            /**
             * Add the new edge and vertex to the resultGraph
             */
            resultGraph.vertices[index] = this.vertices[index];
            visited[index] = true;
            resultGraph.numVertices++;

            resultGraph.adjMatrix[x][y] = this.adjMatrix[x][y];
            resultGraph.adjMatrix[y][x] = this.adjMatrix[y][x];

            /**
             * Add all edges, that are adjacent to the newly added vertex, to
             * the heap
             */
            for (int i = 0; i < numVertices; i++) {
                if (!visited[i] && (this.adjMatrix[i][index]
                        < Double.POSITIVE_INFINITY)) {
                    edge[0] = index;
                    edge[1] = i;
                    minHeap.addElement(adjMatrix[index][i]);
                }
            }
        }
        return resultGraph;
    }
    protected int[] getEdgeWithWeightOf(double weight, boolean[] visited) {
        int[] edge = new int[2];
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if ((adjMatrix[i][j] == weight) && (visited[i] ^ visited[j])) {
                    edge[0] = i;
                    edge[1] = j;
                    return edge;
                }
            }
        }

        /**
         * Will only get to here if a valid edge is not found
         */
        edge[0] = -1;
        edge[1] = -1;
        return edge;
    }
}



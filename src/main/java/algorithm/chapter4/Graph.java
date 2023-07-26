package algorithm.chapter4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// V is the type of the vertices in the Graph
// E is the type of the edges
public abstract class Graph<V, E extends Edge> {

    private List<V> vertices = new ArrayList<>();
    protected List<List<E>> edges = new ArrayList<>();

    public Graph() {

    }

    public Graph(List<V> vertices) {
        this.vertices.addAll(vertices);
        for (V vertex : vertices) {
            edges.add(new ArrayList<>());
        }
    }

    // Number of vertices
    public int getVertexCount() {
        return vertices.size();
    }

    // Number of edges
    public int getEdgeCount() {
        return edges.stream().mapToInt(List::size).sum();
    }

    // Add a vertex to the graph and return its index
    public int addVertex(V vertex) {
        vertices.add(vertex);
        edges.add(new ArrayList<>());
        return getVertexCount() - 1;
    }

    // Find the vertex at a specific index
    public V vertexAt(int index) {
        return vertices.get(index);
    }

    // Find the index of a vertex in the graph
    public int indexOf(V vertex) {
        return vertices.indexOf(vertex);
    }

    // Find the vertices that a vertext at same index is connected to
    public List<V> neighborsOf(int index) {
        return edges.get(index).stream().map(edge -> vertexAt(edge.v)).collect(Collectors.toList());
    }

    // Look up a vertex's index and find its neighbors (convenience method)
    public List<V> getNeighborsOf(V vertex) {
        return neighborsOf(indexOf(vertex));
    }

    // Return all the edges associated with a vertex at some index
    public List<E> edgesOf(int index) {
        return edges.get(index);
    }

    // Look up the index of a vertex and return its edges (convenience method)
    public List<E> edgesOf(V vertex) {
        return edges.get(indexOf(vertex));
    }

    // Make it easy to pretty-print a Graph
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getVertexCount(); i++) {
            sb.append(vertexAt(i));
            sb.append(" -> ");
            sb.append(Arrays.toString(neighborsOf(i).toArray()));
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }
}

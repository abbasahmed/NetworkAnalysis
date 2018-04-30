import java.util.Iterator;
import java.util.LinkedList;

public class AdjacencyList<Item> implements Iterable<Edge> {

	private LinkedList<Edge> adj;
	private int num;
	private int size;

	/*Constructors
	*/

	public AdjacencyList() {
		adj = null;
		size = 0;
	}

	public AdjacencyList(int num) {
		adj = new LinkedList<Edge>();
		this.num = num;
	}

	/*
	*Add a new edge to the linked list.
	*/

	public void add(Edge e) {
		adj.add(e);
		size++;
	}

	/*
	*Return the linked list associated to this vertex.
	*/

	public LinkedList<Edge> listAll(){
		return adj;
	}

	public int numEdges() {
		return size;
	}

	/*
	* Return the vertex number
	*/

	public int num() {
		return num;
	}

	/*
	* ----Helper/Additional Methods----
	*/
	
	public boolean isEmpty() {
		if (size == 0 || adj.isEmpty()) {
			return true;
		} else
			return false;
	}

	public LinkedList<Edge> list() {
		return adj;
	}

	public Iterator<Edge> iterator() {
		return adj.iterator();
	}

}

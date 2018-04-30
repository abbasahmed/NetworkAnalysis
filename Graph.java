import java.io.*;

public class Graph {
	@SuppressWarnings("rawtypes")
	AdjacencyList[] vertices; //This Adjacency List Array contains all the vertices of the graph.
	@SuppressWarnings("rawtypes")
	AdjacencyList[] copperVertices; //This Adjacency List Array contains only the vertices that have a copper edge.

	int vert;  //No. of vertices

	private boolean copper = true; 
	private boolean failed = false;
	
	private BufferedReader bufRead;

	public boolean[] marked;   //Boolean array used in Copper Connectivity to keep track of all the vertices visited.
	public boolean[] visited;  //Boolean array used in Vertice Failure to keep track of all the vertices visited when two vertices fail.

	/*
	*Constructor for Graph.
	*Reads the file and stores in the information.
	*/

	@SuppressWarnings("rawtypes")
	public Graph(String fileName) {

		try {
			FileReader gr = new FileReader(fileName);
			bufRead = new BufferedReader(gr);
			String fileRead = bufRead.readLine();
			vert = Integer.parseInt(fileRead);
			marked = new boolean[vert];
			vertices = new AdjacencyList[vert];
			copperVertices = new AdjacencyList[vert];
			for (int i = 0; i < vertices.length; i++) {
				vertices[i] = new AdjacencyList(i);
				copperVertices[i] = new AdjacencyList(i);
			}
			fileRead = bufRead.readLine();
			if (vert != 0) {
				while (fileRead != null) {
					String[] data = fileRead.split(" ");
					if (data.length != 5)
						continue;
					int vert1 = Integer.parseInt(data[0]);
					int vert2 = Integer.parseInt(data[1]);
					String material = data[2];
					boolean type = false;
					int bWidth = Integer.parseInt(data[3]);
					int len = Integer.parseInt(data[4]);
					AdjacencyList v1 = vertices[vert1];
					AdjacencyList v2 = vertices[vert2];
					if (material.equals("optical")) {
						type = true;
					} else if (material.equals("copper")) {
						type = false;
						AdjacencyList c1 = copperVertices[vert1];
						AdjacencyList c2 = copperVertices[vert2];
						//Also, make a duplicate graph to check for copper connectivity
						Edge cForward = new Edge(c1, c2, false, bWidth, len);
						Edge cReverse = new Edge(c2, c1, false, bWidth, len);
						c1.add(cForward);
						c2.add(cReverse);
					}
					Edge forward = new Edge(v1, v2, type, bWidth, len);
					Edge reverse = new Edge(v2, v1, type, bWidth, len);
					v1.add(forward);
					v2.add(reverse);
					if (!material.equals("copper"))
						copper = false;

					fileRead = bufRead.readLine();
				}
			}

		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("FILE DOES NOT EXIST.");
		} catch (IOException e) {
			System.out.println("IOException occured.");
			System.exit(0);
		}
	}

	/*
	* FOR NORMAL GRAPH
	* Returns edges connected to
	*	 @param v
	*/

	@SuppressWarnings("unchecked")
	public Iterable<Edge> adj(int v) {
		validateVertex(v);
		return vertices[v];
	}

	/*
	* FOR COPPER CONNECTED GRAPH
	* Returns edges connected to
	*	 @param v
	*/


	@SuppressWarnings("unchecked")
	public Iterable<Edge> copper_adj(int v) {
		validateVertex(v);
		return copperVertices[v];
	}

	/*
	*Validates vertex 
	*	@param v
	*/

	public void validateVertex(int v) {
		if (v < 0 || v >= vert)
			throw new IllegalArgumentException("vertex " + v + " is not between 0 and" + (vert - 1));
	}

	/*
	* Returns all the copper edges.
	*/


	public Iterable<Edge> copperEdges() {
		AdjacencyList<Edge> list = new AdjacencyList<Edge>();
		for (int v = 0; v < vert; v++) {
			int selfLoops = 0;
			for (Edge e : copper_adj(v)) {
				if (e.other(v) > v) {
					list.add(e);
				}
				// add only one copy of each self loop (self loops will be
				// consecutive)
				else if (e.other(v) == v) {
					if (selfLoops % 2 == 0)
						list.add(e);
					selfLoops++;
				}
			}
		}
		return list;
	}

	/*
	* Checks whether all the vertices are connected after performing a DFS on copper only graph.
	* Also prints out which vertices are disconnected.
	*/

	public boolean getCopperConnectivity() {
		if (copper) {
			return true;
		} else {
			boolean[] copperConnect = new boolean[vert];
			copperConnect = CopperDFS(0);
			for (int i = 0; i < vert; i++) {
				if (copperConnect[i]) {
					copper = true;
				} else if (!copperConnect[i]) {
					copper = false;
					for (int j = i; j < vert; j++) {
						if (!copperConnect[j])
							System.out.println("\t The Vertex " + j + " is disconnected.\n");
					}
					return copper;
				}
			}
			return copper;
		}

	}

	/*
	* Performs Depth First Search on the copper connected graph
	*/

	private boolean[] CopperDFS(int v) {
		marked[v] = true;

		for (Edge e : copper_adj(v)) {
			int w = e.to();
			if (!marked[w]) {
				CopperDFS(w);
			}
		}
		return marked;
	}

	/*
	* Disconnect all the permutations of 2 vertices and run until a permutation fails the graph. 
	* Check if the graph fails by performing DFS on each permutation.
	*/

	public boolean check() {
		for (int i = 0; i < vert - 1; i++) {
			for (int j = 1; j < vert; j++) {

				System.out.println("Checking failure by removing the vertices: " + i + " & " + j + ". ");
				visited = new boolean[vert];
				for (int z = 0; z < visited.length; z++) {
					visited[z] = false;
				}
				visited[i] = true;
				visited[j] = true;

				dfsCheck(0);

				for (int x = 0; x < visited.length; x++) {
					if (visited[x]) {
						failed = false;
					} else {
						failed = true;
						System.out.println(
								"\n\t Vertex " + x + " is no more connected on removal of the Vertices: " + i + " & " + j);
						return failed;
					}
				}

			}
		}
		return failed;
	}

	/*
	* Depth First Search on all the vertices except the ones that are disconnected/
	*/

	private void dfsCheck(int v) {
		if (visited[v]) {
			dfsCheck(v + 1);
		} else {
			visited[v] = true;
			for (Edge e : adj(v)) {
				int w = e.to();
				if (!visited[w]) {
					dfsCheck(w);
				}
			}
		}
	}

	/*
	* Returns all the edges.
	*/

	public Iterable<Edge> edges() {
		AdjacencyList<Edge> list = new AdjacencyList<Edge>(vert);
		for (int v = 0; v < vert; v++) {
			int selfLoops = 0;
			for (Edge e : adj(v)) {
				if (e.other(v) > v) {
					list.add(e);
				}
				// add only one copy of each self loop (self loops will be
				// consecutive)
				else if (e.other(v) == v) {
					if (selfLoops % 2 == 0)
						list.add(e);
					selfLoops++;
				}
			}
		}
		return list;
	}

	/*
	* Returns the number of vertices.
	*/

	public int V() {
		return vert;
	}
}

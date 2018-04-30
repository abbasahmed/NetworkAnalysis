import java.util.*;

public class NetworkAnalysis {
	@SuppressWarnings("rawtypes")
	static HashMap<AdjacencyList, Boolean> visited;
	private static Scanner input;

	public static void main(String[] args) {

		if (args.length != 1) {
			throw new IllegalArgumentException("Invalid Argument Passed. Please pass the file name only.");
		}

		Graph graph = new Graph(args[0]);
		input = new Scanner(System.in);
		boolean active = true;

		System.out.println("\n\tWelcome to Network Analysis!");

		while (active) {
			printMenu();
			System.out.println();
			int response = 0;
			boolean accept = true;

			// Make sure that response is an integer

			while (accept) {
				accept = false;
				try {
					response = input.nextInt();
				} catch (InputMismatchException e) {
					response = 9;
					input.nextLine();
				}
			}

			int v1, v2;

			switch (response) {
			case 1:
				v1 = getVertex(true);
				v2 = getVertex(false);
				findLowestLatency(graph, v1, v2);
				System.out.println();
				break;
			case 2:
				findCopperConnectivity(graph);
				break;
			case 3:
				v1 = getVertex(true);
				v2 = getVertex(false);
				findMaxFlow(graph, v1, v2);
				System.out.println();
				break;
			case 4:
				getAveragelatency(graph);
				System.out.println();
				break;
			case 5:
				checkFailure(graph);
				System.out.println();
				break;
			case 6:
				active = false;
				break;
			default:
				System.out.println();
				System.out.println("Please enter a number between 1 to 6 only\n");
				System.out.println();
				break;
			}
		}
		System.out.println("\n\t Program exited. goodbye!");
	}


	/* 
	* Find Lowest Latency path between two vertices using Dijkstra's algorithm.
	*/

	public static void findLowestLatency(Graph g, int s, int t) {
		DijkstraAllPairsSP lowestLatency = new DijkstraAllPairsSP(g);
		if (lowestLatency.hasPath(s, t)) {
			int minBandwidth = lowestLatency.minBandwidth(s, t);
			String path = "";
			double minLatency = lowestLatency.dist(s, t);
			boolean start = true;
			for (Edge e : lowestLatency.path(s, t)) {
				if (start) {
					path += e.from();
					path += " -> ";
					path += e.to();
					start = false;
				} else {
					path += " -> ";
					path += e.to();
				}
			}
			System.out.printf("\n\t Lowest latency path: %s |  %.1f nanoseconds.\n", path, minLatency);
			System.out.println("\n\t Bandwidth of the path: " + minBandwidth + " MBPS");
		} else {
			System.out.println("\n\t Path doesn't exist.");
		}

	}

	/* 
	* Find Copper Connectivity.
	*/

	public static void findCopperConnectivity(Graph g) {
		boolean copperConnectivity = false;
		copperConnectivity = g.getCopperConnectivity();
		if (copperConnectivity) {
			System.out.println("\n\t The graph IS copper-only connected.");
		} else {
			System.out.println("\n\t The graph IS NOT copper-only connected.");
		}

	}


	/* 
	* Find Max Flow using Ford Fulkerson's algorithm.
	*/


	public static void findMaxFlow(Graph g, int v1, int v2) {
		for (int i = 0; i < g.V(); i++) {
			for (Edge e : g.adj(i)) {
				e.setFlow(0.0);
			}
		}

		FordFulkerson maxFlow = new FordFulkerson(g, v1, v2);
		int max = (int) maxFlow.value();

		System.out.println("\n\t The maximum data that can be transferred from vertex " + v1 + " to vertex " + v2 + " is: ");
		if (max != 0)
			System.out.println("\t " + max + " MBPS");
		else
			System.out.println("\n\t Cannot transfer any data from vertex " + v1 + " to vertex " + v2 + ".");
	}


	/* 
	* Find the MST by using Prim's algorithm.
	*/

	public static void getAveragelatency(Graph g) {
		PrimMST kmst;
		double lowestavg;
		kmst = new PrimMST(g);
		lowestavg = kmst.weight() / kmst.MSTSize();

		System.out.println("\n\t The edges of the lowest average latency spanning tree are: \n");

		for (Edge e : kmst.edges()) {
			System.out.println("\t\t( " + e.from() + " , " + e.to() + " )");
		}

		System.out.printf("\n\t Average Latency: %.5f nanoseconds.\n", lowestavg);
	}


	/* 
	* Check if disconnecting two vertices fail disconnect a graph.
	*/

	public static void checkFailure(Graph g) {

		if (g.check()) {
			System.out.println("\n\t This graph does NOT REMAIN CONNECTED when the given two vertices fail.");
		} else {
			System.out.println("\n\t This graph REMAINS CONNECTED when any two vertices fail.");
		}

	}

	/*
	*------------ HELPER METHODS---------------
	*/

	public static void printMenu() {
		System.out.println("\n\t1. Find the lowest latency path between any two points.");
		System.out.println(
				"\t2. Determine whether or not the graph is copper-only connected, or whether it is connected considering only copper links (i.e., ignoring fiber optic cables).");
		System.out.println("\t3. Find the maximum amount of data that can be transferred from one vertex to another.");
		System.out.println("\t4. Find the lowest average latency spanning tree.");
		System.out.println(
				"\t5. Determine whether or not the graph would remain connected if any two vertices in the graph were to fail.");
		System.out.println("\t6. Quit the program.");
		System.out.println("\n[ 1 - 6 ] Please enter the number corresponding to the option you want to choose.");
	}


	public static int getVertex(boolean num) {

		int vertex = 0;
		boolean active = true;

		while (active) {
			active = false;
			if (num) {
				System.out.println("\nEnter the first vertex: ");
			} else {
				System.out.println("\nEnter the second vertex: ");
			}
			try {
				vertex = input.nextInt();
				input.nextLine();
				if (vertex < 0) {
					active = true;
					System.out.println("\nIncorrect format. Please Try again!");
				}
			} catch (InputMismatchException e) {
				active = true;
				System.out.println("\nIncorrect format. Please Try again!");
				input.nextLine();
			}
		}
		return vertex;

	}

}

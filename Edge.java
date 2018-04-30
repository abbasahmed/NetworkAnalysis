
public class Edge implements Comparable<Edge> {
	@SuppressWarnings("unused")
	private boolean cableType;
	private int bandwidth;
	private int length;
	@SuppressWarnings("rawtypes")
	private AdjacencyList vertex1;
	@SuppressWarnings("rawtypes")
	private AdjacencyList vertex2;
	private double travelTime;
	private double flow;
	private static final double FLOATING_POINT_EPSILON = 1E-10;

	/*
	*Edge Constructor
	*/

	@SuppressWarnings("rawtypes")
	public Edge(AdjacencyList v1, AdjacencyList v2, boolean cableType, int bandwidth, int length) {
		vertex1 = v1;
		vertex2 = v2;
		this.cableType = cableType; // true for Optical, false for copper
		this.bandwidth = bandwidth;
		this.length = length;
		travelTime = calculateTime(cableType);
		this.flow = 0.0;
	}

	/*
	*Assign the other Edge to this edge
	*/

	public Edge(Edge e) {
		this.vertex1 = e.vertex1();
		this.vertex2 = e.vertex2();
		this.bandwidth = e.bandwidth();
		this.flow = e.flow();
	}

	/*
	* Calculate the time by passing in the Cable Type
	*/

	public double calculateTime(boolean cableType) {
		if (cableType) {
			travelTime = calculate(200000000);
		} else if (!cableType) {
			travelTime = calculate(230000000);
		}
		return travelTime * Math.pow(10, 9); // Convert into nano seconds
	}

	private double calculate(double i) {
		double time = (length / i); // Time = Distance/Speed
		return time;
	}

	/*
	*Return the weight(Travel Time)
	*/

	public double weight() {
		return travelTime;
	}

	/*
	*Return Bandwidth
	*/

	public int bandwidth() {
		return bandwidth;
	}

	/*
	*returns either vertex
	*/

	public int either() {
		return vertex1.num();
	}

	/*
	*Returns the first vertex 
	*/

	public int from() {
		return vertex1.num();
	}

	/*
	*Returns the second vertex 
	*/

	public int to() {
		return vertex2.num();
	}

	/*
	* Returns the LinkedList of edges of Vertex 1 
	*/

	public AdjacencyList vertex1() {
		return vertex1;
	}

	/*
	* Returns the LinkedList of edges of Vertex 2
	*/

	public AdjacencyList vertex2() {
		return vertex2;
	}

	/*
	* Set new flow
	*/

	public void setFlow(double flow) {
		this.flow = flow;
	}

	/*
	* Calculate residual capacity to the vertex @param vertex
	*/

	public double residualCapacityTo(int vertex) {
		if (vertex == from()) {
			return flow;
		} else if (vertex == to()) {
			return bandwidth - flow;
		} else {
			throw new IllegalArgumentException("Invalid Endpoint.");
		}
	}

	/*
	* Add Residual Flow
	*/

	public void addResidualFlowTo(int vertex, double delta) {
		if (!(delta >= 0.0))
			throw new IllegalArgumentException("Delta must be nonnegative");

		if (vertex == from())
			flow -= delta; // backward edge
		else if (vertex == to())
			flow += delta; // forward edge
		else
			throw new IllegalArgumentException("invalid endpoint");

		// round flow to 0 or capacity if within floating-point precision
		if (Math.abs(flow) <= FLOATING_POINT_EPSILON)
			flow = 0;
		if (Math.abs(flow - bandwidth) <= FLOATING_POINT_EPSILON)
			flow = bandwidth;

		if (!(flow >= 0.0))
			throw new IllegalArgumentException("Flow is negative");
		if (!(flow <= bandwidth))
			throw new IllegalArgumentException("Flow exceeds capacity");
	}

	/*
	* Return the flow
	*/

	public double flow() {
		return flow;
	}

	/*
	* Duplicate of from() and to() [Should have deleted, don't know why I didn't.]
	*/

	@SuppressWarnings("rawtypes")
	public AdjacencyList start() {
		return vertex1;
	}

	@SuppressWarnings("rawtypes")
	public AdjacencyList end() {
		return vertex2;
	}

	/*
	* Returns the other vertex
	*/

	public int other(int vertex) {
		if (vertex == vertex1.num()) {
			return vertex2.num();
		} else if (vertex == vertex2.num()) {
			return vertex1.num();
		} else
			throw new IllegalArgumentException("Illegal endpoint");
	}

	/*
	*Compares to @param that
	*/

	public int compareTo(Edge that) {
		return Double.compare(this.weight(), that.weight());
	}

	/*
	* toString method
	*/

	public String toString() {
		return String.format("%d-%d %.5f", vertex1.num(), vertex2.num(), weight());
	}
}

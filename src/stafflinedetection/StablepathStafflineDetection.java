package stafflinedetection;

import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import general.Staffline;
import interfaces.StafflineDetection;

/**
 * This class as of now is very heavy, thus we try to utilize multithreading
 * @author Marius
 *
 */
public class StablepathStafflineDetection implements StafflineDetection{

	private int stafflineHeight;
	private int staffspaceHeight;
	private int maxThreads;

	private volatile int runCounter;
	
	
	public StablepathStafflineDetection(int stafflineHeight, int staffspaceHeight, int maxThreads) {
		super();
		this.stafflineHeight = stafflineHeight;
		this.staffspaceHeight = staffspaceHeight;
		this.maxThreads = maxThreads;
	}
	
	private synchronized void changeCounter(int value) {
		runCounter += value;
	}
	
	
	@Override
	public ArrayList<Staffline> detectStafflines(boolean[][] system) {
		
		//ArrayList<ArrayList<Integer>> runlengths = Util.GetVerticalRunLengths(system);
		
		Graph<Integer, DefaultWeightedEdge> graph = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		System.out.println("Creating graph");
		//From left to right starting with the upmost pixel
		for(int x = 0; x < system.length; x++) {
			for(int y = 0; y < system[x].length; y++) {
				int currentGraphId = x*system[x].length + y;
	
				int leftGraphId = (x-1)*(system[x].length) + y;
				
				graph.addVertex(currentGraphId);
				
				if(x > 0) {
					graph.addEdge(leftGraphId, currentGraphId);
					graph.setEdgeWeight(leftGraphId, currentGraphId, weight(system[x-1][y], system[x][y],false));
					
					if(y > 0) {
						graph.addEdge(leftGraphId-1, currentGraphId);
						graph.setEdgeWeight(leftGraphId-1, currentGraphId, weight(system[x-1][y-1], system[x][y],true));
					}	
					if(y < system[x].length-1) {
						graph.addEdge(leftGraphId+1, currentGraphId);
						graph.setEdgeWeight(leftGraphId+1, currentGraphId, weight(system[x-1][y+1], system[x][y],true));
					}
				}
			}
		}
		
		System.out.println("Finished Graph");
		int threadPool = maxThreads;
		ShortestPathAlgorithm<Integer,DefaultWeightedEdge> shortestPathAlg = new FloydWarshallShortestPaths<>(graph);
		
		for(int y = 0; y < system[0].length; y++) {

			// Without multithreading
			/*
			long startTime = System.currentTimeMillis();
			int minWeight = -1;

			for(int y2 = 0; y2 < system[0].length; y2++) {
				
				GraphPath<Integer,DefaultWeightedEdge> path = shortestPathAlg.getPath(y, ((system.length-1) * (system[0].length) + y2));
				int weight = (int) path.getWeight();
				if(weight < minWeight || minWeight == -1) {
					minWeight = weight;
				}

			}
			System.out.println("Checked: y=" + y +" with minweight= " + minWeight +" in " + ((System.currentTimeMillis()-startTime)/1000.0) + " seconds");
			*/
			
			//With multithreading
			
			while(runCounter >= threadPool) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(system[0].length - y + " threads waiting in queue");
			}
			
			changeCounter(1);
			Thread t = new Thread (new PathCheckerThread(graph, y, system.length, system[0].length));
			t.start();
			
			
		}

		while(runCounter > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			System.out.println(runCounter + " threads left");
		}
		
		System.out.println("Checked all paths");
		
		return null;
	}
	
	
	/*
	 * Listing 2. Pseudocode for the weight function. The base weight
	was set to 4 on black pixels and 8 on white pixels for 4-
	neighborhoods, and 6 and 12 on for 8-neighborhoods. The delta
	penalizing term in the weight function was set to 1. For efficiency,
	weights were designed with integer values. (Seite 1137)
	 */
	
	//White -> white (8/12)
	//black -> black / white (4/6)
	//White -> black (4/6)
	public int weight(boolean pixVal1, boolean pixVal2, boolean isVertical) {
		boolean isBlack = pixVal1 || pixVal2; //Determine if one of the pixels is black
		
		if(isBlack) {
			if(isVertical) return 6;
			else return 4;
		}
		else {
			if(isVertical) return 12;
			else return 8;
		}
	}

	class PathCheckerThread implements Runnable {
		
		Graph<Integer, DefaultWeightedEdge> synchronizedGraph;
		int threadY;
		int xLength;
		int yLength;
		
		
		public PathCheckerThread(Graph<Integer, DefaultWeightedEdge> graph, int threadY,
				int xLength, int yLength) {
			super();
			this.synchronizedGraph = graph;
			this.threadY = threadY;
			this.xLength = xLength;
			this.yLength = yLength;
		}



		@Override
		public void run() {
			long startTime = System.currentTimeMillis();
			int minWeight = -1;
			ShortestPathAlgorithm<Integer,DefaultWeightedEdge> shortestPathAlg = new DijkstraShortestPath<>(synchronizedGraph);

			System.out.println("I am the thread for y = " + threadY);
			for(int y2 = 0; y2 < yLength; y2++) {

				GraphPath<Integer,DefaultWeightedEdge> path = shortestPathAlg.getPath(threadY, ((xLength-1) * (yLength) + y2));
				int weight = (int) path.getWeight();
				if(weight < minWeight || minWeight == -1) {
					minWeight = weight;
				}
			}
			System.out.println("I am the thread for y = "+threadY+" and finished. shortest path: " + minWeight + "| Exectime: "+ ((System.currentTimeMillis()-startTime)/1000.0) + "s");
			changeCounter(-1);	
		}
	}



}

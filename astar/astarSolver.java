package astar;

import heap.ArrayHeapMinPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @see ShortestPathsSolver for more method documentation
 */
public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private List<Vertex> solution;
    private SolverOutcome outcome;
    private double solutionWeight;
    private double timeSpent;
    private int numStatesExplored;
    private HashMap<Vertex, Vertex> edgeTo;

    /**
     * Immediately solves and stores the result of running memory optimized A*
     * search, computing everything necessary for all other methods to return
     * their results in constant time. The timeout is given in seconds.
     */
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch sw = new Stopwatch();
        numStatesExplored = -1;
        solution = new ArrayList();
        solution.add(start);
        ArrayHeapMinPQ<Vertex> pq= new ArrayHeapMinPQ<>();
        pq.add(start, input.estimatedDistanceToGoal(start, end));
        edgeTo = new HashMap<>();
        HashMap<Vertex, Double> distTo = new HashMap<>();
        HashSet<Vertex> marked = new HashSet<>();
        distTo.put(start, 0.0);
        

        while (!pq.isEmpty()) {
            Vertex newV = pq.removeSmallest();
            marked.add(newV);
            numStatesExplored++;
            if (newV.equals(end)) {
                solutionWeight = distTo.get(newV);
                save(newV, start, solution);
                outcome = SolverOutcome.SOLVED;
                timeSpent = sw.elapsedTime();
                return;
            }
            for (WeightedEdge<Vertex> edges : input.neighbors(newV)) {
                double potentialDist = distTo.get(edges.from()) + edges.weight();
                if (pq.contains(edges.to())) {
                    if (potentialDist < distTo.get(edges.to())) {
                        distTo.put(edges.to(), potentialDist);
                        edgeTo.put(edges.to(), newV);
                        pq.changePriority(edges.to(), potentialDist +
                                input.estimatedDistanceToGoal(edges.to(), end));
                    }
                } else if (!marked.contains(edges.to())){
                    distTo.put(edges.to(), potentialDist);
                    edgeTo.put(edges.to(), newV);
                    pq.add(edges.to(), potentialDist +
                            input.estimatedDistanceToGoal(edges.to(), end));
                }
            }
        }


        outcome = SolverOutcome.UNSOLVABLE;
        timeSpent = sw.elapsedTime();
    }

    private void save(Vertex v, Vertex start, List<Vertex> result) {
        if (!v.equals(start)) {
            save(edgeTo.get(v), start, result);
            result.add(v);
        }
    }

    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    @Override
    public List<Vertex> solution() {
        if (outcome == SolverOutcome.UNSOLVABLE) {
            solution.clear();
        }
        return solution;
    }

    @Override
    public double solutionWeight() {
        if (outcome == SolverOutcome.UNSOLVABLE) {
            solutionWeight = 0;
        }
        return solutionWeight;
    }

    /** The total number of priority queue removeSmallest operations. */
    @Override
    public int numStatesExplored() {
        if (outcome == SolverOutcome.UNSOLVABLE) {
            numStatesExplored = 0;
        }
        return numStatesExplored;
    }

    @Override
    public double explorationTime() {
        return timeSpent;
    }
}

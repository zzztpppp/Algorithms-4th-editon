/**************************************************************************************************
  * SAP class of the assignment wordnet
  *************************************************************************************************/
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;

public class SAP
{
    private Digraph G;         // Digraph passed to SAP
                                 
    
    
    // Constructor
    public SAP(Digraph G)
    {
        if (G == null) throw new java.lang.IllegalArgumentException("Argument is null");
        
        // To keep SAP immutable, copy the digraph passed in and save it as 
        // an instnace variable
        this.G = new Digraph(G.V());
        for (int v = 0; v < G.V(); v++)
        {
            for(int w: G.adj(v))
            {
                this.G.addEdge(v, w);
            }
        }        
    }
    
    /**
     * Compute the length of the shortest ancestral path
     * between two vertices
     */
    public int length(int a, int b)
    {
        validVertex(a);
        validVertex(b);
        
        int minDistance = G.E() + 1;
        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(G, a);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(G, b);
        for (int s = 0; s < G.V(); s++)
        {
            if (bfsA.hasPathTo(s) && bfsB.hasPathTo(s))
                if (bfsA.distTo(s) + bfsB.distTo(s) < minDistance)
                    minDistance = bfsA.distTo(s) + bfsB.distTo(s);
        }
        if (minDistance == G.E() + 1)
            return -1;
        else
            return minDistance;       
    }
    
    /**
     * Returns the common ancestors in shortest ancestral path
     */
    public int ancestor(int a, int b)
    {
        validVertex(a);
        validVertex(b);
        
        int minDistance = G.E() + 1;
        int commonAncestor = -1;
        BreadthFirstDirectedPaths bfsA = new BreadthFirstDirectedPaths(G, a);
        BreadthFirstDirectedPaths bfsB = new BreadthFirstDirectedPaths(G, b);
        for (int s = 0; s < G.V(); s++)
        {
            if (bfsA.hasPathTo(s) && bfsB.hasPathTo(s))
                if (bfsA.distTo(s) + bfsB.distTo(s) < minDistance)
                {
                    minDistance = bfsA.distTo(s) + bfsB.distTo(s);
                    commonAncestor = s;
                }
        }
        return commonAncestor;
    }
    
    /**
     * Returns the length of shortest path between any vertex 
     * in v and any vertex in w, -1 if no such a path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w)
    {
        if (v == null || w == null)
            throw new java.lang.IllegalArgumentException("Null arguement");
        int minDistance = G.E() + 1;
        for (int vI: v)
        {
            for(int wI: w)
            {
                int dist = length(vI, wI);
                if (dist < minDistance)
                    minDistance = dist;
            }
        }
        if (minDistance == G.E() + 1)
            return -1;
        return minDistance;
    }
    
    /**
     * Returns the common ancestor of the sap between 
     * any vertex in v and any vertex in w
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w)
    {
        if (v == null || w == null)
            throw new java.lang.IllegalArgumentException("Null arguement");
        int minDistance = G.E() + 1;
        int commonAncestor = -1;
        for (int vI: v)
        {
            for(int wI: w)
            {
                int dist = length(vI, wI);
                if (dist < minDistance)
                {
                    minDistance = dist;
                    commonAncestor = ancestor(vI, wI);
                }
            }
        }
        return commonAncestor;
    }
    
    // Helper functions
        
    private void validVertex(int v)
    {
        if (v < 0 || v >= G.V())
            throw new java.lang.IllegalArgumentException("Vertex boundary should between 0 and" + (G.V() - 1));
    }
    
    
    // Test Client
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        StdOut.println(G.toString());
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
}
}
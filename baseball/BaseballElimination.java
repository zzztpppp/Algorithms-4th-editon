/***************************************************************************************************
  * Baseball team elimination detection
  * The third assignment of Algorithms part II
  *************************************************************************************************/
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;


public class BaseballElimination
{
    private String[] teams;              // Names of all teams
    private final int[]    w;                  // Wins of team i has achieved
    private final int[]    l;                  // Loses of team i has gained
    private final int[]    r;                  // Remaining games for team i to play
    private final int[][]  g;                  // Number of games for team i, j to play
    private final RedBlackBST<String, Integer> bst; // Mapping from team name to number
    private Queue<String>[] elimination;             // Teams that eliminates team i
    private boolean[]       isEliminated;            // Is team i mathmatically eliminated?
    
    public BaseballElimination(String filename)
    {
        if (filename == null)
            throw new java.lang.IllegalArgumentException("Input filename is null");
        In in = new In(filename);
        int numOfTeams = Integer.parseInt(in.readLine());
        
        // Initialize all instance variables
        elimination = (Queue<String>[]) new Queue[numOfTeams];
        isEliminated = new boolean[numOfTeams];
        teams = new String[numOfTeams];
        w     = new int[numOfTeams];
        r     = new int[numOfTeams];
        l     = new int[numOfTeams];
        g     = new int[numOfTeams][numOfTeams];
        bst   = new RedBlackBST<String, Integer>();
        for (int t = 0; t < numOfTeams; t++)
        {
            String[] info = in.readLine().trim().split("\\s+");
            for (int i = 0; i < info.length; i++)
            {
                if      (i == 0)
                {
                    teams[t] = info[i];
                    bst.put(info[i], t);
                }
                else if (i == 1)
                    w[t] = Integer.parseInt(info[i]);
                else if (i == 2)
                    l[t] = Integer.parseInt(info[i]);
                else if (i == 3)
                    r[t] = Integer.parseInt(info[i]);
                else
                    g[t][i - 4] = Integer.parseInt(info[i]);
            }
        }
        
        // Compute elimination
        for (int t = 0; t < numOfTeams; t++)
            elimination(t);
    }
    
    private void elimination(int t)
    {
        // Trivial situation
        int gamesRemaining = 0;
        for (int team = 0; team < numberOfTeams(); team++)
        {
            if (w[t] + r[t] < w[team])
            {
                isEliminated[t] = true;
                elimination[t] = new Queue<String>();
                elimination[t].enqueue(teams[team]);
                return;
            }
        }
        
        FlowNetwork fn = 
            new FlowNetwork(numberOfTeams() * numberOfTeams() + numberOfTeams() + 2);
        
        // Construct edges
        for (int v = 0; v < numberOfTeams(); v++)
        {
            // Set the capacities from team veritcs to sink as w[t] + r[t] - w[v]
            // , 0 if v == t
            if (v != t)
                fn.addEdge(new FlowEdge(v, fn.V() - 1, w[t] + r[t] - w[v]));
            
            // Set capacities from source to matches
            for (int v1 = v + 1; v1 < numberOfTeams(); v1++)
            {
                if (v != t && v1 != t)
                {
                    int matchV = v * numberOfTeams() + v1;
                    fn.addEdge(new FlowEdge(fn.V() - 2, matchV, g[v ][v1]));
                    fn.addEdge(new FlowEdge(matchV, v, Double.POSITIVE_INFINITY));
                    fn.addEdge(new FlowEdge(matchV, v1, Double.POSITIVE_INFINITY));
                    gamesRemaining += g[v][v1];
                }
            }       
        }
        FordFulkerson maxflow = new FordFulkerson(fn, fn.V() - 2, fn.V() - 1);
        if ((int)maxflow.value() == gamesRemaining)
        {
            isEliminated[t] = false;
            return;
        }
        else
        {
            isEliminated[t] = true;
            elimination[t] = new Queue<String>();
            for (int v = 0; v < numberOfTeams(); v++)
            {
                if (maxflow.inCut(v))
                    elimination[t].enqueue(teams[v]);
            }
        }
    }
    
    /**
     *  Returns number of teams involved
     */
    public int numberOfTeams()
    {
        return teams.length;
    }
    
    /**
     * Returns number of wins for given team
     */
    public int wins(String team)
    {
        validTeam(team);
        return w[bst.get(team)];
    }
    
    /**
     * Returns all teams
     */
    public Iterable<String> teams()
    {
        return bst.keys();
    }
    
    /**
     * Return number of losses for given team
     */
    public int losses(String team)
    {
        validTeam(team);
        return l[bst.get(team)];
    }
    
    /**
     * Returns number of remaining games for given team
     */
    public int remaining(String team)
    {
        validTeam(team);
        return r[bst.get(team)];
    }
    
    /**
     * Return number of remaining games between team1 and 2
     */
    public int against(String team1, String team2)
    {
        validTeam(team1);
        validTeam(team2);
        return g[bst.get(team1)][bst.get(team2)];
    }
    
    /**
     * Is given team eliminated?
     */
    public boolean isEliminated(String team)
    {
        validTeam(team);
        return isEliminated[bst.get(team)];
    }
    
    /**
     * Subsets of teams that eliminates given team
     */
    public Iterable<String> certificateOfElimination(String team)
    {
        return elimination[bst.get(team)];
    }
    
    //Helper function
    private void validTeam(String team)
    {
        if (team == null)
            throw new java.lang.IllegalArgumentException("Input filename is null");
        if (!bst.contains(team))
            throw new java.lang.IllegalArgumentException("Team not exist");  
    }
      
    // Test client
    public static void main(String[] args)
    {
        BaseballElimination division = new BaseballElimination(args[0]);
    for (String team : division.teams()) {
        if (division.isEliminated(team)) {
            StdOut.print(team + " is eliminated by the subset R = { ");
            for (String t : division.certificateOfElimination(team)) {
                StdOut.print(t + " ");
            }
            StdOut.println("}");
        }
        else {
            StdOut.println(team + " is not eliminated");
        }
    }

    }
}
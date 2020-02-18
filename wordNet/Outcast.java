/**************************************************************************************************
  * Part of the assignment of wordNet
  ************************************************************************************************/
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
public class Outcast
{
    private WordNet wn;
    public Outcast(WordNet wordNet)
    {
        this.wn = wordNet;       
    }
    
    /**
     * Given the outcast of a sequence of word-net nouns
     */
    public String outcast(String[] nouns)
    {
        int maxDistance = -1;
        String outCastNoun = null;
        for (int i = 0; i < nouns.length; i++)
        {
            int dist = 0;
            for (int j = 0; j < nouns.length; j++)
            {
                dist += wn.distance(nouns[i], nouns[j]);
            }
            if (dist > maxDistance)
            {
                maxDistance = dist;
                outCastNoun = nouns[i];
            }
        }
        
        return outCastNoun;
    }
    
    // Test client
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
    }
}
}
/**************************************************************************************************
  * This is a programming assignment of Couresera Course Algorithms; WordNet. It describes the 
  * semantic relationships between words
  ************************************************************************************************/
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Topological;

public class WordNet
{
    private Digraph wordNet;  // Word net whose vertices denote the index in words
    private String[] allWords;   // Entries are words in word net
    private RedBlackBST<String, Bag<Integer>> allWordsBST; // Nouns stored in a binary search tree
    private SAP shortestAncestralPath;
    /**
     * Constructor takes the name of two input files
     */
    public WordNet(String synsets, String hypernyms)
    {
        if (synsets == null || hypernyms == null)
            throw new  java.lang.IllegalArgumentException("Null arguements");
        allWordsBST = new RedBlackBST<String, Bag<Integer>>();
        In inSynsets = new In(synsets);
        In inHypernyms = new In(hypernyms);
        
        // Read in synsets
        String[] synsetArray = inSynsets.readAllLines(); 
        allWords = new String[synsetArray.length];       
        inSynsets.close();
            
        wordNet = new Digraph(synsetArray.length);
        for (int i = 0; i < synsetArray.length; i++)
        {
            String[] word = synsetArray[i].split(",");
            int value = Integer.parseInt(word[0]);
            allWords[value] = word[1];
            
            // Split nouns from this synset and put them into BST
            // use a bag to store id of synsets since a noun may 
            // belong to multiple synsets
            String[] nouns = word[1].split(" ");
            for (int j = 0; j < nouns.length; j++)
            {
                if (allWordsBST.contains(nouns[j]))
                {
                    Bag<Integer> synset = (Bag<Integer>) allWordsBST.get(nouns[j]);
                    synset.add(value);
                }
                else
                {
                    allWordsBST.put(nouns[j], new Bag<Integer>());
                    Bag<Integer> synset = (Bag<Integer>) allWordsBST.get(nouns[j]);
                    synset.add(value);
                }
            }
        }
        // Construct word net
        String[] hypernymArray = inHypernyms.readAllLines();
        inHypernyms.close();
        for (int i = 0; i < hypernymArray.length; i++)
        {
            String[] words = hypernymArray[i].split(",");
            // Add vertices into wordnet
            int from = Integer.parseInt(words[0]);
            for (int j = 1; j < words.length; j++)
            {
                wordNet.addEdge(from, Integer.parseInt(words[j]));
            }
        }
        Topological top = new Topological(wordNet);
        if (!top.isDAG())
            throw new java.lang.IllegalArgumentException("The word net should be a DAG");
        
        int numRoot = 0;
        for (int v = 0; v < wordNet.V(); v++)
        {      
            boolean hasAdj = false;
            for (int w: wordNet.adj(v))
            {
                hasAdj = true;
                break;
            }
            if (!hasAdj) numRoot += 1;
            hasAdj = false;
            
        }
        if (numRoot > 1)
                throw new java.lang.IllegalArgumentException("The word net should be a rooted DAG");
        shortestAncestralPath= new SAP(wordNet);
    }
    
    /**
     * Returns all nouns in wordNet
     */
    public Iterable<String> nouns()
    {
        Queue<String> nouns = new Queue<String>();
        for (Object noun: allWordsBST.keys())
        {
            nouns.enqueue((String)noun);
        }
        return nouns;
    }
    
    /**
     * Is the given noun a word net noun?
     */
    public boolean isNoun(String word)
    {
        if (word == null) throw new java.lang.IllegalArgumentException("Null arguements");
        return allWordsBST.contains(word);
    }
    
    /**
     * return the distance between noun a and noun b
     */
    public int distance(String a, String b)
    {
        if (!isNoun(a) || !isNoun(b)) 
            throw new java.lang.IllegalArgumentException("Not word net nouns");
        return shortestAncestralPath.length(allWordsBST.get(a), allWordsBST.get(b));
    }
    
    /**
     *  Return the common ancestor in the shortest ancestral path of nounA and nounB
     */
    public String sap(String nounA, String nounB)
    {
        if (!isNoun(nounA) || !isNoun(nounB)) 
            throw new java.lang.IllegalArgumentException("Not word net nouns");
        return allWords[shortestAncestralPath.ancestor(allWordsBST.get(nounA), allWordsBST.get(nounB))];
    }
    
    // Methods for test 
    /*
    public Digraph graph()
    {
        return wordNet;
    }*/
    
    // Helper functions
    // Return ids of synsets a noun belongs to    
    private Iterable<Integer> ids(String word)
    {
        if (isNoun(word))
            return (Bag<Integer>)allWordsBST.get(word);
        else
            return null;
    }
    
    
    // Test client
    public static void main(String[] args)
    {
        WordNet net = new WordNet("synsets3.txt", "hypernyms3InvalidTwoRoots.txt");
        for (String noun: net.nouns())
        {
            StdOut.println(noun);
        }
        
        StdOut.println(net.isNoun("p"));
        StdOut.println(net.isNoun("a"));
        StdOut.println("------------------------");
        for (String noun:net.nouns())
        {
            for (int id: net.ids(noun))
            {
                StdOut.print(id + " ");
            }
            StdOut.println();
        }
       //StdOut.println(net.graph().toString());
        
       // StdOut.println("Number of synsets: " + net.graph().V());
        
        
    }
}
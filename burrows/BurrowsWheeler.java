/************************************************************************************************
  * Burrows-wheeler transform, one of the componet of burrows-wheeler compression
  * 
  ***********************************************************************************************/
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;

public class BurrowsWheeler
{
    private static final int R = 256; // ASCII codes
    
    /**
     * Apply burrows-wheeler transform, reading from standard input
     * and writingt to stand output
     */
    public static void transform()
    {
         String input = BinaryStdIn.readString();
         BinaryStdIn.close();
         CircularSuffixArray array = new CircularSuffixArray(input);
         // Last column of sorted suffixes
         for (int i = 0; i < array.length(); i++)
         {
             if (array.index(i) == 0)
                 BinaryStdOut.write(i);
         }
         for (int j = 0; j < array.length(); j++)
         {
             int index = array.index(j) - 1; // Last column character index in original string
             if (index < 0) index  = array.length() - 1;
             BinaryStdOut.write(input.charAt(index));
         } 
         BinaryStdOut.close();
    }
    
    /**
     * Apply inverse Burrows-wheeler transform reading code from standard
     * input and write to standard output
     */
    public static void inverseTransform()
    {
        int first = BinaryStdIn.readInt(); //Position where first original suffix appears
        Queue<Character> queue = new Queue<Character>();
        // Collect all characters of the original string
        while (!BinaryStdIn.isEmpty())
        {
            char t = BinaryStdIn.readChar();
            queue.enqueue(t);
        }
        char[] t = new char[queue.size()];
        for (int i = 0; i < t.length; i++)
            t[i] = queue.dequeue();
        
        int[] count = new int[R + 1]; // Key-indexed counting
        for (char c: t)
            count[c + 1]++;
        
        // Convert frequncy count into index
        for (int i = 0; i < R; i++)
            count[i + 1] += count[i];
        
        // Sort t[] to get the first column of sorted suffix array
        char[] sortedT = new char[t.length];
        for (char c: t)
            sortedT[count[c]++] = c;
        
        // Construct next[]
        int[] next = new int[t.length];
        int i = 0;
        while(i < next.length)
        {
            int numConsecutive = 1; // Number of same characters consecutive 
            for (int j = i + 1; j < next.length; j++)
            {
                if (sortedT[j] == sortedT[i]) numConsecutive += 1;
            }
            
            for (int j = 0, k= 0; k < numConsecutive && j < next.length; j++)
            {
                if (t[j] == sortedT[i + k])
                {
                    next[i + k] = j;
                    k++;
                }
            }
            i += numConsecutive;  
        }
        
        for (int j = 0; j < next.length; j++)
        {
            BinaryStdOut.write(sortedT[first]);
            first = next[first];
        }
        BinaryStdOut.close();
        
    }
    
    
    //Unit test if args[0] is "+" apply inverse transform
    // if args[0] is "-" apply transform
    public static void main(String[] args)
    {
        if (args[0].equals("-")) transform();
        if (args[0].equals("+")) inverseTransform();
    }
}
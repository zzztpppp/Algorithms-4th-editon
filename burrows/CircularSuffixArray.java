/********************************************************************************************
  * A component of Burrows-Wheeler compression algorithm --- Circular suffix array
  * 
  ******************************************************************************************/
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray
{
    private final String input; // Inpute string to be compressed
    private final Integer[] sortedArray; // Sorted circular suffix array
    private final int length;         // Length of the input
    
    public CircularSuffixArray(String s)
    {
        if (s == null)
            throw new java.lang.IllegalArgumentException("Input string is null");
        length = s.length();
        input = s;
        sortedArray = new Integer[length];
        for (int i = 0; i < length; i++)
            sortedArray[i] = i;
        Arrays.sort(sortedArray, new Comparator<Integer>(){
            @Override
            public int compare(Integer a, Integer b)
            {
                int suffixAt1 = a;
                int suffixAt2 = b;
                for (int i = 0; i < length; i++)
                {
                    if (suffixAt1 >= length) suffixAt1 = 0;
                    if (suffixAt2 >= length) suffixAt2 = 0;
                    if (input.charAt(suffixAt1) > input.charAt(suffixAt2) )
                        return 1;
                    if (input.charAt(suffixAt1) < input.charAt(suffixAt2))
                        return -1;
                    suffixAt1++;
                    suffixAt2++;
                }
                return 0;
            }
        });
    }
    
    /**
     * Returns length of input string
     */
    public int length()
    {
        return length;
    }
    
    /**
     * Returns the original index in sorted suffix array
     */
    public int index(int i)
    {
        if (i < 0 || i >length - 1)
            throw new java.lang.IllegalArgumentException("Index out of bound");
        return sortedArray[i];
    }

    // Unit test
    public static void main(String[] args)
    {
        CircularSuffixArray a = new CircularSuffixArray(args[0]);
        for (int i = 0; i < a.length(); i++)
            StdOut.print(a.index(i) + " ");
    }

}
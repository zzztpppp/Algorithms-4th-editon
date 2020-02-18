/****************************************************************************************
  * The last programming assignment for Algorithms 4th edtion
  **************************************************************************************/
import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront
{
    private static final int R = 256;
    
    /**
     * Encode the input string by move-to-front encoder
     */
    public static void encode()
    {
        String input = BinaryStdIn.readString();
        BinaryStdIn.close();
        char[] charSequence = new char[R];  // Position to charaters mapping
        for (char c = 0; c < R; c++)
            charSequence[c] = c;
          
        // Start encoding input string
        for (int i = 0; i < input.length(); i++)
        {
            char c = input.charAt(i);
            int position = 0;
            for (int pos = 0; pos < R; pos++)
            {
                if (charSequence[pos] == c)
                    position = pos;
            }
            BinaryStdOut.write(position, 8);
            // Move current character c to the front of sequence
            moveToFront(c, position, charSequence);
        }
        BinaryStdOut.close();
    }
    
    /**
     * Decode the code from move-to-front encoding
     */
    public static void decode()
    {
        char[] charSequence = new char[R];  // Position to charaters mapping
        for (char c = 0; c < R; c++)
            charSequence[c] = c;
        while(!BinaryStdIn.isEmpty())
        {
            char code = BinaryStdIn.readChar(8);
            char c = charSequence[code];
            BinaryStdOut.write(c);
            moveToFront(c, (int) code, charSequence);     
        }
        BinaryStdIn.close();
        BinaryStdOut.close();
        
    }
    
    // Helper functions
    private static void moveToFront(char c, int position, char[] charSequence)
    {
        // The case that the chararter c is already at front
        if (position == 0) return;
        
        System.arraycopy(charSequence, 0, charSequence, 1, position);
        charSequence[0] = c;
    }
    
    public static void main(String[] args)
    {
        if (args[0].equals("-")) encode();
        if (args[0].equals("+")) decode();
    }
}
/**************************************************************************************************
  * A class that is used to find seams in a picture, which is useful in picture re-sizing.
  * An assignment of Algrorithms-Part II
  *************************************************************************************************/
import edu.princeton.cs.algs4.StdOut;
import java.awt.Color;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Queue;

public class SeamCarver
{
    private int width;           // Width of the input picture
    private int height;          // Height of the input picture
    private double[][] energy;   // Engergy of input picture according to each pixel
    private int[][] red;         // Red color information
    private int[][] green;       // Green color information
    private int[][] black;        // Black color information
    
    /**
     * Class constructor
     */
    public SeamCarver(Picture picture)
    {
        if (picture == null)
            throw new java.lang.IllegalArgumentException("Input picture is null");
        // Initialize instance variables
        width  = picture.width();
        height = picture.height();
        energy = new double[height][width];
        red    = new int[height][width];
        black  = new int[height][width];
        green  = new int[height][width];
        
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                Color color = picture.get(j, i);
                red[i][j] = color.getRed();
                black[i][j] = color.getBlue();
                green[i][j] = color.getGreen();
            }
        }
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                // Initailize energy array
                energy[j][i] = calculateEnergy(i, j);              
            }
        }
        
    }
    
    /**
     * Returns current picture
     */
    public Picture picture()
    {
        Picture pic = new Picture(width, height);
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                pic.set(i, j, new Color(red[j][i], green[j][i], black[j][i]));
            }
        }
        return pic;
    }
    
    /**
     * Returns the width of current picture
     */
    public int width()
    {
        return width;
    }
    
    /**
     * Returns height of current picture
     */
    public int height()
    {
        return height;
    }
    
    /**
     * Reurns the energy of the given pixel specified by row and column 
     * in current picture
     */
    public double energy(int x, int y)
    {
        validIndex(x, y);
        return energy[y][x];
    }
    
   
    /**
     * Returns a sequence of a vertical seam(a relatively efficient immplementaiton)
     */
    public int[] findVerticalSeam()
    {
        if (height == 1)
        {
            int[] path = new int[1];
            path[0] = 0;
            return path;
        }
        int[] path = findVerticalSeam(0, 0);
        return path;
    }
    
    private int[] findVerticalSeam(int x, int y)
    {
        validIndex(x, y);
        
        int[][]         paths = new int[height][width];           // Record all paths from top to bottom
        double[][] pathEnergy = new double[height][width];// Total engergy from this pixel to top
        int[]    shortestPath = new int[height];
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                pathEnergy[i][j] = Double.POSITIVE_INFINITY;
                paths[i][j] = -1; 
            }
        }
        pathEnergy[0][0] = 1000;
        
        // Relax each vertices(pixel) in topological order
        relax(x, y, paths, pathEnergy);
        y += 1;
        while (y < height - 1 )
        { 
            for (int x1 = 0; x1 < width; x1++)
                relax(x1, y, paths, pathEnergy);
            
            y ++;
        }
        
        // Retrieving the shortest path
        shortestPath[height - 1] = width- 1;
        for (int h = height - 2; h > 0; h-- )
        {
            shortestPath[h] = paths[h + 1][shortestPath[h +1]];
        }
        if (height > 1)
        {
            shortestPath[0] = shortestPath[1];
            shortestPath[height - 1] = shortestPath[height - 2]; 
        }
        return shortestPath; 
        
    }
    
    /**
     * Returns a sequence of indices of horizontal seam
     */
    public int[] findHorizontalSeam()
    {
        int[] result = new int[width];
        transpose();
        result = findVerticalSeam();
        transpose();
        return result;
    }
    
    /**
     * Remove vertical seam from current picture
     */
    public void removeVerticalSeam(int[] seam)
    {
        validSeam(seam);
        for (int i = 0; i < height; i++)
        {
            arrayShift(green[i], seam[i]);
            arrayShift(black[i], seam[i]);
            arrayShift(red[i], seam[i]);
            arrayShift(energy[i], seam[i]);
        }
        width -= 1;
        // Re-calculate energy array
        for (int h = 0; h < height; h++)
        {
            if ( h > 0 && h < height - 1)
            {
                if (seam[h] > 0)
                    energy[h][seam[h] - 1] = calculateEnergy(seam[h] - 1, h); 
                energy[h][seam[h]] = calculateEnergy(seam[h], h); 
            }
        }
        
    }
    
    /**
     * Remove the horizontal line
     */
    public void removeHorizontalSeam(int[] seam)
    {
         transpose();
         removeVerticalSeam(seam);
         transpose();
    }
    
         
    
    // Helper functions
    private void validSeam(int[] seam)
    {
        if (seam == null)
            throw new java.lang.IllegalArgumentException("Seam is null");
        if (seam.length != height)
            throw new java.lang.IllegalArgumentException("Illegal Seam");
        for (int i = 0; i < seam.length; i++)
        {
            int j = i + 1;
            if (j < seam.length)
            {
                if (Math.abs(seam[i] - seam[j]) > 1)
                    throw new java.lang.IllegalArgumentException("Illegal Seam");
            }
            if (seam[i] < 0 || seam[i] > width - 1)
                throw new java.lang.IllegalArgumentException("Illegal Seam");
        }
    }
        
    private double calculateEnergy(int x, int y)
    {
        if (x > 0 && y > 0 && x < width - 1 && y < height - 1)
            return Math.sqrt(deltaX(x, y) + deltaY(x, y));
        else
            return 1000.0;
    }
    
    private double deltaX(int x, int y)
    {
        validIndex(x, y);
        double greenDiff = Math.pow(green[y][x + 1] - green[y][x - 1], 2);
        double redDiff   = Math.pow(red[y][x + 1] - red[y][x - 1], 2);
        double blackDiff = Math.pow(black[y][x + 1] - black[y][x - 1], 2);
        return greenDiff + redDiff + blackDiff;
    }
    
    private double deltaY(int x, int y)
    {
        validIndex(x, y);
        double greenDiff = Math.pow(green[y + 1][x] - green[y - 1][x], 2);
        double redDiff   = Math.pow(red[y + 1][x ] - red[y - 1][x], 2);
        double blackDiff = Math.pow(black[y + 1][x] - black[y - 1][x], 2);
        return greenDiff + redDiff + blackDiff;
    }
    
    private void validIndex(int x, int y)
    {
        if (x >= width || x < 0)
            throw new java.lang.IllegalArgumentException("Index out of boundary");
        if (y >= height || y < 0)
            throw new java.lang.IllegalArgumentException("Index out of boundary");
    }
    
    private Iterable<Integer> adj(int x, int y)
    {
        Queue<Integer> adj = new Queue<Integer>();
        if (y > 0 && y < height - 2)
        {
            if (x > 0)
                adj.enqueue(x - 1);
            if (x < width - 1)
                adj.enqueue(x + 1);
            adj.enqueue(x);
        }
        else if (y == 0)
        {
            for (int x1 = 0; x1 < width; x1++)
                adj.enqueue(x1);
        }
        else if (y == height - 2)
        {
            adj.enqueue(width- 1);
        }
        return adj;
    }
    
    private void transpose()
    {
        int temp;
        temp = width;
        width = height;
        height = temp;
        
        red = transpose(red);
        black = transpose(black);
        green = transpose(green);
        energy = transpose(energy);
    }
    
    private int[][] transpose(int[][] array)
    {
        int[][] tempArray = new int[height][width];
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                tempArray[i][j] = array[j][i];
            }
        }
        return tempArray;
    }
    
    private double[][] transpose(double[][] array)
    {
        double[][] tempArray = new double[height][width];
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                tempArray[i][j] = array[j][i];
            }
        }
        return tempArray;
    }
    
    private void relax(int x, int y, int[][] paths, double[][] pathEnergy)
    {
        for (int v: adj(x, y))
        {
            if (pathEnergy[y][x] + energy[y + 1][v] < pathEnergy[y + 1][v])
            {
                pathEnergy[y + 1][v] = pathEnergy[y][x] + energy[y + 1][v];
                paths[y + 1][v] = x;
            }
        }
    }
    
    private static void arrayShift(int[] array, int i)
    {
        if (i < array.length - 1)
        {
            System.arraycopy(array, i + 1, array, i, array.length - i - 1);
        }
    }
    
     private static void arrayShift(double[] array, int i)
    {
        if (i < array.length - 1)
        {
            System.arraycopy(array, i + 1, array, i, array.length - i - 1);
        }
    }
    
    
    
    //Functions for test
    private void printRed()
    {
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
                StdOut.print(green[i][j] + " ");
            StdOut.println();
        }
    }
    
    // Test client
    public static void main(String[] args)
    {
       Picture pic = new Picture(args[0]);
       SeamCarver sc = new SeamCarver(pic);
       sc.removeVerticalSeam(sc.findVerticalSeam());
       StdOut.print(sc.width());
       /* 
       StdOut.println("height = " + sc.height());
        int[] a = new int[3];
        for (int i = 0; i < a.length; i++)
            a[i] = i;
        SeamCarver.arrayShift(a, 0);
        for (int i = 0; i < a.length; i++)
            StdOut.print(a[i] + " ");
            */ 
    }
}
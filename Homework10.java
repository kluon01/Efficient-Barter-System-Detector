import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class Homework10
{
    // Global variables
    static double[][] adjMatrix;
    static int vertices;
    static int[][] path;
    static String[][] inputLines;
    static DecimalFormat df4 = new DecimalFormat("#.####");

    static void readFile(File filename)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            br = new BufferedReader(new FileReader(filename));
            vertices = Integer.parseInt(br.readLine());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Number of vertices: " + vertices);

        // Initialize all values in adjacency matrix to be infinity
        adjMatrix = new double[vertices+1][vertices+1];
        for(int i = 1; i < vertices+1; i++)
        {
            for(int j = 1; j < vertices+1; j++)
            {
                adjMatrix[i][j] = Double.POSITIVE_INFINITY;
            }
        }

        String thisLine;
        inputLines = new String[vertices+1][vertices+1]; // Store input file lines into array to be used for output file
        try
        {
            // Read input file and populate adjMatrix and inputLines 2D arrays
            while ((thisLine = br.readLine()) != null)
            {
                String[] strs = thisLine.trim().split(" ");
                double[] a = new double[4];
                for (int i = 0; i < strs.length; i++)
                {
                    a[i] = Double.parseDouble(strs[i]);
                }
                inputLines[(int)a[0]][(int)a[1]] = a[2] + " " + a[3];
                // Use log to calculate weights of edges in order to find a negative cycle
                adjMatrix[(int)a[0]][(int)a[1]] = Math.log10(a[2]/a[3]);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static void writeFile(String filename, boolean foundNegCycle, ArrayList<Integer> negPath) throws IOException
    {
        double total = 1;
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        if(foundNegCycle == true)
        {
            writer.write("yes");
            writer.newLine();
            String line;
            for(int i = 0; i < negPath.size() - 1; i++)
            {
                String[] pathComponents = inputLines[negPath.get(i)][negPath.get(i+1)].split(" "); // Split components of negative cycle path
                line = negPath.get(i) + " " + negPath.get(i+1) + " " +  inputLines[negPath.get(i)][negPath.get(i+1)];
                total = total * Double.parseDouble(pathComponents[1]) / Double.parseDouble(pathComponents[0]);
                writer.write(line);
                writer.newLine();
            }
            writer.write("one kg of product " + negPath.get(0) + " gets " + df4.format(total) + " kg of product " + negPath.get(0) + " from the above exchange sequence.");
            writer.close();
        }
        else
        {
            writer.write("no");
            writer.close();
            return;
        }
    }


    public static boolean FloydWarshall(double[][] matrix, int N)
    {
        // Declare cost 2D array to be used for finding the negative cycle
        double[][] cost = new double[N+1][N+1];

        // Initialize path 2D array
        path = new int[N+1][N+1];
        for (int v = 1; v < N+1; v++)
        {
            for (int u = 1; u < N+1; u++)
            {
                cost[v][u] = matrix[v][u];
                if(matrix[v][u] != Double.POSITIVE_INFINITY && v != u)
                {
                    path[v][u] = v;
                }
                else
                {
                    path[v][u] = -1;
                }
            }
        }
        for (int i = 1; i < vertices+1; i++)
        {
            for (int j = 1; j < vertices+1; j++)
            {
                System.out.print(path[i][j] + "        ");
            }
            System.out.println();
        }

        // Run the Floyd-Warshall algorithm
        for (int k = 1; k < N+1; k++)
        {
            for (int v = 1; v < N+1; v++)
            {
                for (int u = 1; u < N+1; u++)
                {
                    if (cost[v][k] + cost[k][u] < cost[v][u])
                    {
                        cost[v][u] = cost[v][k] + cost[k][u];
                        path[v][u] = path[k][u];
                    }
                }
            }
        }

        for (int i = 1; i < vertices+1; i++)
        {
            for (int j = 1; j < vertices+1; j++)
            {
                System.out.print(cost[i][j] + "        ");
            }
            System.out.println();
        }
        for (int i = 1; i < vertices+1; i++)
        {
            for (int j = 1; j < vertices+1; j++)
            {
                System.out.print(path[i][j] + "        ");
            }
            System.out.println();
        }

        // Check diagonals for a negative value which indicates a negative cycle
        for(int i = 1; i < vertices+1; i++)
        {
                if (cost[i][i] < 0)
                {
                    System.out.println("Negative cycle found.");
                    return true;
                }
        }

        return false; // No negative cycle was found
    }


    static ArrayList<Integer> findNegCycle(int[][] path, int startNode)
    {
        // Initialize array list that will hold the path from v to v
        ArrayList<Integer> pathList = new ArrayList<>();
        int endNode = startNode;

        pathList.add(0,endNode);

        System.out.println("Finding the negative cycle path: ");

        while(true)
        {
            endNode = path[startNode][endNode];
            pathList.add(0,endNode);
            System.out.println(pathList.toString());
            if(endNode == startNode)
            {
                break;
            }
        }

        return pathList;
    }


    public static void main(String[] args)
    {
        // Example terminal run command:
        // javac Homework10.java
        // java Homework10 input1.txt output1.txt
        readFile(new File(args[0]));
        try
        {
            writeFile(args[1], FloydWarshall(adjMatrix,vertices), findNegCycle(path,path[1][1]));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    
        for (int i = 1; i < vertices+1; i++)
        {
            for (int j = 1; j < vertices+1; j++)
            {
                System.out.print(adjMatrix[i][j] + "        ");
            }
            System.out.println();
        }
    }
}

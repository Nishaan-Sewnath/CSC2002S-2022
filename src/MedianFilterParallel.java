import java.util.Arrays;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;

import java.util.concurrent.RecursiveAction;

public class MedianFilterParallel extends RecursiveAction{
    
	private int lo;
	private int hi;
	private int filtS;
    private int[] s;
    private int[] d;
	protected static int SEQUENTIAL_CUTOFF= 10000;

    static long startTime = 0;


    /**
	 *starts the timer
	 *
	 * */

	private static void tick(){

		startTime = System.nanoTime();

	}

	/**
	 *
	 *Stops the timer
	 *@return time in milliseconds
	 *
	 *
	 * */

	private static float tock(){

		return (System.nanoTime()-startTime)/1000000.0f;

	}
    

    /**
     * Constructor for the Parallel filter program median
     * @param l starting index
     * @param h ending index
     * @param filt size of the window filter
     * @param src array containing the data
     * @param dst array to store the processed data
     */
    public MedianFilterParallel(int l, int h, int filt, int[] src, int[] dst){


		lo = l;
		filtS = filt;
        s= src;
        hi = h;
        d= dst;


    }

    /**
     * Compute method for the parallel program
     */

    @Override
    protected void compute(){

        if((hi)<SEQUENTIAL_CUTOFF){
          
            computeDirectly();
            
           
            return;
        }
    


        invokeAll(new MedianFilterParallel(lo, ((hi)/2), filtS, s, d), new MedianFilterParallel((((hi)/2)+lo), (hi-((hi)/2)), filtS, s, d));
        
        


    }
    /**
     * computeDirectly method 
     * contains the actaully parallel program
     */

    protected void computeDirectly(){


        //initializing the variabless
        int fil = (filtS-1)/2;
        float r[] = new float[filtS*filtS]; 
        float g[] = new float[filtS*filtS]; 
        float b[] = new float[filtS*filtS];
        int count = 0;



        

        for(int x = lo; x < hi+lo; x++){

            
            

            for(int y = -fil; y <= fil; y++){

            
                long sum = Math.min(Math.max(y+x, 0), s.length-1);

                int p = s[(int) sum];
                
               

                r[count] = (float)((p>>16)&0xFF);
                g[count] = (float)((p>>8)&0xFF);
                b[count] = (float)((p)&0xFF);

                count++;
                    

            }

            //sorting the individual rgb arrays
            Arrays.parallelSort(r);
            Arrays.parallelSort(g);
            Arrays.parallelSort(b);

            //combining the rbg values to a single integer
            int color = (((int)r[(r.length-1)/2])<<16)|(((int) g[(g.length-1)/2])<<8)|(((int) b[(b.length-1)/2])<<0);

            d[x]= color;

            if(count>(filtS*filtS)-1){


                count = 0;

            }
            


        }
        
            

        
    }

    /**
     * Main method
     * @param args
     * @throws IOException
    */
    public static void main(String []args) throws IOException{

        String input;
        String arr[] = new String[3];
        


        //code to get input from user
        Scanner into = new Scanner(System.in);
        System.out.println("Please enter a file input name, a file output name and a window size greater or equal to 3: ");
        input = into.nextLine();
        arr = input.split(" ");

        //code to check correctness of parameter inputs
        try{

            if(arr[0].equals(null)||arr[1].equals(null)||arr[2].equals(null)){

                  System.out.println("Error");            
    
            }


        }catch(Exception e){

            System.out.println("Incorrect number of parameters. Please remember to separate the parameters with spaces!");
            System.exit(0);;

        }

        int temp = Integer.parseInt(arr[2]);

        if((temp < 3 )||(temp%2==0)){
        
            
            System.out.println("Incorrect input for parameter 3. Please make sure value is greater or equal to 3 and an ODD number.");
            System.exit(0);

        }


        //initialization of the variables
        int filter;
        filter = Integer.parseInt(arr[2]);

        File f;
        BufferedImage bi;
        f = new File(arr[0]);
        bi = ImageIO.read(f);


        
        int [] src = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), null, 0, bi.getWidth());
        int [] dst = new int[src.length];

        //displays system info
        int proc  = Runtime.getRuntime().availableProcessors();
        System.out.println(proc + " processor(s) avaiable");


        //Declaraction of the constructor
        MedianFilterParallel par = new MedianFilterParallel(0, src.length, filter, src, dst);

        //Declaring and invoking the forkjoinPool
        ForkJoinPool pool = new ForkJoinPool();

        //Starts timer
        tick();
        //does the work
        pool.invoke(par);
        //Stops the timer
        float timed = tock();

        //displays the time the main work took to run
        System.out.println("System took: " + timed + " milliseconds to run.");

        //initializing a new buffered Image and storing the new processed data in it
        BufferedImage fl = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        fl.setRGB(0, 0, fl.getWidth(), fl.getHeight(), dst, 0, fl.getWidth());
        File fn = new File(arr[1]);
        
        
        //writing the data from the buffered Image to a new file
        ImageIO.write(fl, "jpg", fn);
        System.out.println("success");

    }

    
}

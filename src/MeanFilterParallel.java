import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;

import java.util.concurrent.RecursiveAction;

public class MeanFilterParallel extends RecursiveAction{
    

    //global variables
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
     * Constructor for the program
     * @param l starting index
     * @param h ending index
     * @param filt window filter size
     * @param src array in question
     * @param dst resulting array
     */
    
    public MeanFilterParallel(int l, int h, int filt, int[] src, int[] dst){

    
		lo = l;
		filtS = filt;
        s= src;
        hi = h;
        d= dst;


    }

     /**
      * Overriden compute method
      */

    @Override
    protected void compute(){

        if((hi)<SEQUENTIAL_CUTOFF){
          
            computeDirectly();
            
           
            return;
        }
    


        invokeAll(new MeanFilterParallel(lo, ((hi)/2), filtS, s, d), new MeanFilterParallel((((hi)/2)+lo), (hi-((hi)/2)), filtS, s, d));
        
        


    }

    /**
     * The actaully parallel method of the program
     */

    protected void computeDirectly(){



        int fil = (filtS-1)/2;
        
        

        

        for(int x = lo; x < hi+lo; x++){

            float a = 0; 
            float r=0; 
            float g=0; 
            float b=0;
            

            for(int y = -fil; y <= fil; y++){

            
                long sum = Math.min(Math.max(y+x, 0), s.length-1);

                int p = s[(int) sum];
                
                r += (float)(((p )>>16) & 0xff)/filtS;
                g += (float)(((p)>>8) & 0xff)/filtS;
                b += (float)(((p )>>0) &0xff)/filtS;
                    
                }



            int color = (((int)r)<<16)|(((int) g)<<8)|(((int) b)<<0);

            d[x]= color;

   


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
        

        //initializing variables
        int filter;
        filter = Integer.parseInt(arr[2]);

        File f;
        BufferedImage bi;
        f = new File(arr[0]);

        //reading input image into a bufferedImage
        bi = ImageIO.read(f);


        //getting values from bufferedImage into an array
        
        int [] src = bi.getRGB(0, 0, bi.getWidth(), bi.getHeight(), null, 0, bi.getWidth());
        int [] dst = new int[src.length];

        //invokes the actual parallel part of the program
        MeanFilterParallel par = new MeanFilterParallel(0, src.length, filter, src, dst);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(par);


        BufferedImage fl = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);

        //Saving the result into a new bufferedImage
        fl.setRGB(0, 0, fl.getWidth(), fl.getHeight(), dst, 0, fl.getWidth());
        File fn = new File(arr[1]);
            
        //writing the contents of the new BufferedImage into a new file
        ImageIO.write(fl, "jpg", fn);
        System.out.println("success");

    }

    
}

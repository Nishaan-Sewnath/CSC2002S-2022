import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class MedianFilterSerial {
    
    //global variables
    private static int[] src, dst, block;
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
     * Method to calculate the mean of the data
     * @param blk the data in question
     * @param fil the filter size
     * @return the median value of the data
     */
    public static long MeanCalc(int[] blk, int fil){

        
        int length = blk.length;
        float []resultR = new float[length];
        float []resultG = new float[length];
        float []resultB = new float[length];
        int rp = 0;
        
        for(int i = 0; i< length; i++){

            resultR[i] = (float)((blk[i]&0x00FF0000)>>16);
            resultG[i] = (float)((blk[i]&0x0000FF00)>>8);
            resultB[i] = (float)((blk[i]&0x000000FF)>>0);

        }

        Arrays.sort(resultR);
        Arrays.sort(resultG);
        Arrays.sort(resultB);
        

        rp = (((int)resultR[(length-1)/2]<<16)|((int)resultG[(length-1)/2]<<8)|((int)resultB[(length-1)/2]<<0));
      

        return rp;
    }

    

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

                              
    
            }


        }catch(Exception e){

            System.out.println("Incorrect number of parameters. Please remember to separate the parameters with spaces!");
            System.exit(0);;

        }

        int temp = Integer.parseInt(arr[2]);

        if(temp < 3 || (temp%2==0)){
        
            
            System.out.println("Incorrect input for parameter 3. Please make sure value is greater or equal to 3 and an ODD number.");
            System.exit(0);

        }

        int filter;
        filter = Integer.parseInt(arr[2]);

        File f;
        BufferedImage bi = null;

        try{

            f = new File(arr[0]);
            bi = ImageIO.read(f);



        }catch(Exception e){

            System.out.println("File not found!");

        }

        int count1= 0;
        
        int w = bi.getWidth();
        int h = bi.getHeight();
        block = new int[filter*filter];

        //getting the data from the input image
        src = bi.getRGB(0, 0, w, h, null, 0, w);
        dst = new int[src.length];

        int range = (filter-1)/2;

        for(int x = 0; x<src.length; x++){
            
            if((x-range)>=0&&(x+range)<src.length){
                for(int y = x-(w*range); y<x+(w*range); y++){
                    
                    if((y-range>=0)&&(y+range<src.length)){

                       

                        for(int j = x-range;j<x+range;j++){

                        
                            
                            block[count1] = src[j];
                            
                            
                            count1++;
                            //to ensure no index out of bounds errors
                            if(count1>(filter*filter)-1){
    
                                count1= 0;
    
                            }
                                
                                
    
                            


                        }

                        
                        
                        
                    
                        
                        

                    
                    


                    }

                
                    //places the processed data into the new array
                    dst[x] = (int)MedianFilterSerial.MeanCalc(block, filter);

                    
                

                }

                
               
               

                
            }
  


        }
        
 
        //set the new buffered image to the newly processed array
        BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        File nf = new File(arr[1]);

        res.setRGB(0, 0, w, h, dst, 0, w);

        //write the data from the new buffered image to a new file
        ImageIO.write(res, "jpg", nf);
        System.out.println("Success!");




    }


}


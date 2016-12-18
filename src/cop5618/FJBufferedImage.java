package cop5618;

import java.awt.List;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.imageio.ImageIO;

import org.junit.BeforeClass;

public class FJBufferedImage extends BufferedImage {
	
   /**Constructors*/
	
	public FJBufferedImage(int width, int height, int imageType) {
		super(width, height, imageType);
	}

	public FJBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
		super(width, height, imageType, cm);
	}

	public FJBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied,
			Hashtable<?, ?> properties) {
		super(cm, raster, isRasterPremultiplied, properties);
	}
	

	/**
	 * Creates a new FJBufferedImage with the same fields as source.
	 * @param source
	 * @return
	 */
	public static FJBufferedImage BufferedImageToFJBufferedImage(BufferedImage source){
	       Hashtable<String,Object> properties=null; 
	       String[] propertyNames = source.getPropertyNames();
	       if (propertyNames != null) {
	    	   properties = new Hashtable<String,Object>();
	    	   for (String name: propertyNames){properties.put(name, source.getProperty(name));}
	    	   }
	 	   return new FJBufferedImage(source.getColorModel(), source.getRaster(), source.isAlphaPremultiplied(), properties);		
	}
	
	@Override
	public void setRGB(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize){
        /****IMPLEMENT THIS METHOD USING PARALLEL DIVIDE AND CONQUER*****/
		
		
		int height = h;
		int width = w;
		
		ForkJoinPool forkJoin = new ForkJoinPool();
		for(int row = 0; row < height; row++) {
			Set task = new Set(rgbArray, row, this, scansize, yStart, width);
			forkJoin.execute(task);
		}
		
		forkJoin.shutdown();
	}
	

	@Override
	public int[] getRGB(int xStart, int yStart, int w, int h, int[] rgbArray, int offset, int scansize){
	       /****IMPLEMENT THIS METHOD USING PARALLEL DIVIDE AND CONQUER*****/	
		
		int height = h;
		int width = w;
		
        ForkJoinPool forkJoin = new ForkJoinPool();
        for(int row = 0; row < height; row++) {
            forkJoin.execute(new Get(rgbArray, row, this, scansize, yStart, width));
        }
        
        while(forkJoin.getActiveThreadCount() != 0){
        	try {
				forkJoin.awaitTermination(1, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        forkJoin.shutdown();
        return rgbArray;
		
	}
}


class Get extends RecursiveAction {
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
    private int[] array;
    private int off;
    private int w;
    private int start;
    private int end;
 
    public final static int threshold = Integer.MAX_VALUE;
 
    public Get(int[] array, int offset, BufferedImage image, int width, int srt, int end) {
        this.w = width;
        this.off = offset;
        this.start = srt;
        this.end = end;
        this.img = image;
        this.array = array;
    }
 
    @Override
    protected void compute() {
        int len = end - start;
        if(len > threshold) {
        	int middle = (start + end) >> 1;
            Get left = new Get(array, off, img,  w, middle, end);
            left.fork();
            new Get(array, off, img,  w, start, middle).compute();
            left.join();
        }
        else {
            for(int column = start; column < start + len; column++) {
                array[column+(w*off)] = img.getRGB(column, off);
            }	
        }
    }
}
 
class Set extends RecursiveAction {
	private static final long serialVersionUID = 1L;
	private BufferedImage img;
    private int[] array;
    private int off;
    private int width;
    private int start;
    private int end;
 
    public final static int threshold = Integer.MAX_VALUE;
 
    public Set(int[] array, int off, BufferedImage img, int width, int srt, int end) {
        this.img = img;
        this.array = array;
        this.width = width;
        this.off = off;
        this.start = srt;
        this.end = end;
    }
 
    @Override
    protected void compute() {
        int len = end - start;
        if(len > threshold) {
            int mid = (start + end) >> 1;
            Set right = new Set(array, off, img, width, mid, end);
            right.fork();
            new Set(array, off, img, width, start, mid).compute();
            right.join();
        }
        
        else {
            for(int column = start; column < start + len; column++) {
                int pixel = array[(off*width) + column];
                img.setRGB(column, off, pixel);
            }
        }
    }
}



package cop5618;


import java.awt.image.BufferedImage;

public class ColorHistEq {

    //Use these labels to instantiate you timers.  You will need 8 invocations of now()
	static String[] labels = { "getRGB", "convert to HSB", "create brightness map", "probability array",
			"parallel prefix", "equalize pixels", "setRGB" };

	static Timer colorHistEq_serial(BufferedImage image, BufferedImage newImage) {
		Timer times = new Timer(labels);
		/**
		 * IMPLEMENT SERIAL METHOD
		 */
		return times;
	}



	static Timer colorHistEq_parallel(FJBufferedImage image, FJBufferedImage newImage) {
		Timer times = new Timer(labels);
		/**
		 * IMPLEMENT PARALLEL METHOD
		 */
		return times;
	}

}

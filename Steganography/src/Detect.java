import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Detect {
	public static void main(String[] args) throws IOException{
		detect(new File(args[1]));
	}
	public static void detect(File file) throws IOException{
		BufferedImage input = ImageIO.read(file);
		BufferedImage output2 = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage output4 = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage output6 = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		for(int x = 0; x < input.getWidth(); x++){
			for(int y = 0; y < input.getHeight(); y++){
				Color color = new Color(input.getRGB(x, y), true);
				int newColor = getIntFromColor(color.getRed() << 2, color.getGreen() << 2, color.getBlue() << 2);
				output2.setRGB(x, y, newColor);
				
				newColor = getIntFromColor(color.getRed() << 4, color.getGreen() << 4, color.getBlue() << 4);
				output4.setRGB(x, y, newColor);
				
				newColor = getIntFromColor(color.getRed() << 6, color.getGreen() << 6, color.getBlue() << 6);
				output6.setRGB(x, y, newColor);
			}
		}
		try {
		    File outputfile = new File("output2.png");
		    ImageIO.write(output2, "png", outputfile);
		    
		    outputfile = new File("output4.png");
		    ImageIO.write(output4, "png", outputfile);
		    
		    outputfile = new File("output6.png");
		    ImageIO.write(output6, "png", outputfile);
		} catch (IOException e) {
		}
	}
	public static void rdetect(File file) throws IOException{
		BufferedImage input = ImageIO.read(file);
		BufferedImage output2 = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage output4 = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage output6 = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		for(int x = 0; x < input.getWidth(); x++){
			for(int y = 0; y < input.getHeight(); y++){
				Color color = new Color(input.getRGB(x, y), true);
				int newColor = getIntFromColor(color.getRed() >> 2, color.getGreen() >> 2, color.getBlue() >> 2);
				output2.setRGB(x, y, newColor);
				
				newColor = getIntFromColor(color.getRed() >> 4, color.getGreen() >> 4, color.getBlue() >> 4);
				output4.setRGB(x, y, newColor);
				
				newColor = getIntFromColor(color.getRed() >> 6, color.getGreen() >> 6, color.getBlue() >> 6);
				output6.setRGB(x, y, newColor);
			}
		}
		try {
		    File outputfile = new File("output2.png");
		    ImageIO.write(output2, "png", outputfile);
		    
		    outputfile = new File("output4.png");
		    ImageIO.write(output4, "png", outputfile);
		    
		    outputfile = new File("output6.png");
		    ImageIO.write(output6, "png", outputfile);
		} catch (IOException e) {
		}
	}
	private static int getIntFromColor(int Red, int Green, int Blue){
	    int R = (Red << 16) & 0x00FF0000;
	    int G = (Green << 8) & 0x0000FF00;
	    int B = Blue & 0x000000FF;
	    return 0xFF000000 | R | G | B;
	}
}

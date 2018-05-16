import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;

public class Steganography{
	public static File embed(File filePath, Image imagePath){ // Embeds a file into a picture, given the picture is large enough
		int extraBits = 32 + (8 * 5); // Number of header bits needed, 32 for the end point and 40 for the file type
		long neededBits = 	getNeededBits(filePath) + extraBits; // The number of bits needed to hold the file and header
		long availableBits = getAvailableBits(imagePath); // The number of bits the image can hold

		System.out.println(neededBits);
		if(neededBits > availableBits){
			return null;
		}

		byte[] data;
		BufferedImage image;
		try {
			image = (BufferedImage) imagePath ; // Reads the input image
			data = Files.readAllBytes( filePath.toPath() ); // Pulls all the bytes out of a file and puts them in an array (Note: This is why the file can only be < 33mb)
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		int[] headerBits = new int[extraBits]; // Creates the 'header' array
		for(int i = 0; i <= 31; i++){
			if( (neededBits & (1 << 31-i) ) == 0 ){ // Uses bit shift to convert bytes to bits, writing the end point
				headerBits[i] = 0;
			}else{
				headerBits[i] = 1;
			}
		}
		String rawFileExtension = filePath.getAbsolutePath().substring(filePath.getAbsolutePath().lastIndexOf(".") + 1); // Finds the file type
		String fileExtension = rawFileExtension + "     ".substring(rawFileExtension.length()); // Adds spaces until the total length is 5
		for(int i = 0; i <= 4; i++){
			for(int j = 0; j <= 7; j++){
				if( (fileExtension.charAt(i) & (1 << 7-j)) == 0 ){ // Uses bit shift to convert bytes to bits, writing the extension
					headerBits[32 + (i*8) + j] = 0;
				}else{
					headerBits[32 + (i*8) + j] = 1;
				}
			}
		}

		int[] inputBitArray = new int[(int) neededBits]; // Large bit array to hold input file's bits (Max input file size can only be 33mb or 268435455 bits)
		int bitPosition = 0;
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < 8; j++){
				if( ( data[i] & (1 << (7-j)) ) == 0 ){ // Uses bit shifting to find the current bit in the selected byte
					inputBitArray[bitPosition] = 0;
				}else{
					inputBitArray[bitPosition] = 1;
				}
				bitPosition++;
			}
		}

		int[] bitArray = concatArrays(headerBits, inputBitArray);

		int x = 0;
		int y = 0;
		int imageWidth = image.getWidth();
		int outputColorInteger;
		bitPosition = 0;
		for(int i = 0; i < Math.ceil((double)neededBits/(double)(2 * 3)); i++){ // Loops a number of times equal to needed bits / (2*3)
			outputColorInteger = image.getRGB(x, y);
			for(int j = 0; j <= 2; j++){
				for(int k = 0; k < 2; k++){
					if(bitPosition >= neededBits){
						break;
					}
					outputColorInteger ^= (-bitArray[ bitPosition ] ^ outputColorInteger) & (1 << k + j*8 ); // Bit shift to write 2 bits into each RGB channel
					bitPosition++;
				}
			}
			image.setRGB(x, y, outputColorInteger); // Writes to image

			x++;
			if(x >= imageWidth){
				x = 0;
				y++;
			}
		}

		File outputfile = new File("output.png"); // Creates a blank output file
		try {
			ImageIO.write(image, "png", outputfile); // Writes image to the outputfile with extension "png"
			return outputfile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File detach(Image imagePath, HeaderInfo hi){
		int extraBits = 32 + (8 * 5); // Number of header bits needed, 32 for the end point and 40 for the file type
		BufferedImage image = (BufferedImage) imagePath;

		int width = image.getWidth();
		int x = 12;
		int y = 0;

		int endPoint = hi.getHeaderBits();
		System.out.println(endPoint);
		String extention = hi.getFileExtention();
		int currentRGB;

		int bitPos = 0;
		int[] bitArray = new int[endPoint-extraBits]; // Makes an array the size of all the bits
		for(int i = 0; i < Math.ceil((double)bitArray.length / (double)(2*3)); i++){ // Loops an amount of times equal to bits needed / (bits per pixel * channels)
			currentRGB = image.getRGB(x, y);
			for(int j = 0; j <= 2; j++){
				for(int k = 0; k < 2; k++){
					if( bitPos >= bitArray.length){ // If we reached the end of the bits (Say, half way through a pixel), breaks the loop.
						break;
					}
					if( ( currentRGB & ( 1 << k + j*8 ) ) == 0 ){ // Does a bit shift to read the pixel at the current location
						bitArray[bitPos] = 0;
					}else{
						bitArray[bitPos] = 1;
					}
					bitPos++;
				}
			}
			x++;
			if(x >= width){
				x = 0;
				y++;
			}
		}
		byte currentByte = (byte) 255;
		byte[] fileBytes = new byte[bitArray.length / 8]; // Makes a new array that will contain all the file's bytes (1/8th the size of byte array)
		for(int i = 0; i < fileBytes.length; i++){ // Loops through all of fileBytes
			currentByte = (byte) 255; // Sets the byte default to 11111111
			for(int j = 0; j < 8; j++){
				currentByte ^= (-bitArray[(i * 8)+j] ^ currentByte) & (1 << (7-j)); // Applies a bit shift to the currentByte 8 times
			}
			fileBytes[i] = currentByte;
		}

		try {
			FileOutputStream fos = new FileOutputStream("output." + extention);
			fos.write(fileBytes);
			fos.close();
			return new File("output." + extention);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static HeaderInfo getHeaderInfo(Image img){
		int extraBits = 32 + (8 * 5); // Number of header bits needed, 32 for the end point and 40 for the file type
		BufferedImage image = (BufferedImage) img;

		int width = image.getWidth();
		int x = 0;
		int y = 0;

		int[] headerBits = new int[extraBits]; // Array to contain the header bits
		int currentRGB;
		int bitPos = 0;
		for(int i = 0; i < Math.ceil((double)headerBits.length / (double)(2*3)); i++){ // Loops an amount of times equal to bits needed / (bits per pixel * channels)
			currentRGB = image.getRGB(x, y);
			for(int j = 0; j <= 2; j++){
				for(int k = 0; k < 2; k++){
					if( bitPos >= headerBits.length){ // If we reached the end of the bits (Say, half way through a pixel), breaks the loop.
						break;
					}
					if( ( currentRGB & ( 1 << k + j*8 ) ) == 0 ){ // Does a bit shift to read the pixel at the current location
						headerBits[bitPos] = 0;
					}else{
						headerBits[bitPos] = 1;
					}
					bitPos++;
				}
			}
			x++;
			if(x >= width){
				x = 0;
				y++;
			}
		}

		byte[] headerBytes = new byte[9]; // Creating an array to contain the converted bits to bytes
		byte currentByte;
		for(int i = 0; i < headerBits.length / 8; i++){ // Converting bits to bytes
			currentByte = (byte) 255; // Creates a byte of value "11111111"
			for(int j = 0; j < 8; j++){
				currentByte ^= (-headerBits[(i * 8)+j] ^ currentByte) & (1 << (7-j)); // edits currentByte with the headerBit array
			}
			headerBytes[i] = currentByte;
		}
		int endPoint = 0;
		for(int i = 0; i <= 3; i++){ // Determining the end point by copying the first 4 header bytes to a new 32 bit number
			for(int j = 0; j <= 7; j++){
				if( (headerBytes[i] & (1 << 7-j) ) == 0 ){
					endPoint ^= (-(0) ^ endPoint) & (1 << (31-( (i*8)+j )) );
				}else{
					endPoint ^= (-(1) ^ endPoint) & (1 << (31-( (i*8)+j )) );
				}
			}
		}

		endPoint = toInt(headerBytes, 0);

		String extention = "";
		for(int i = 4; i <= 8; i++){
			extention += (char) headerBytes[i]; // Appends the extension string for each byte char
		}

		extention = extention.replace(" ", ""); // Gets rid of spaces in the file type

		if(extention.length() < 2 || endPoint < 2 || endPoint > getAvailableBits(img)){
			return null;
		}else{
			return new HeaderInfo(endPoint, extention);
		}
	}

	public static int toInt(byte[] bytes, int offset) {
		int ret = 0;
		for (int i=0; i<4 && i+offset<bytes.length; i++) {
			ret <<= 8;
			ret |= (int)bytes[i] & 0xFF;
		}
		return ret;
	}

	public static long bitsToMegabytes(long bits){
		return bits / (long)8388608;
	}
	public static long bitsToKilobytes(long bits){
		return bits / (long)8192;
	}

	public static long getNeededBits(File file){
		long fileLength = file.length();
		if(fileLength == 0L){
			return 0;
		}else{
			return 8 * fileLength;
		}
	}

	public static long getAvailableBits(Image img){
		return (long)img.getWidth(null) * (long)img.getHeight(null) * (long)(2 * 3); // Two bits per color channel 
	}

	public static int[] concatArrays(int[] a, int[] b) {
		int aLen = a.length;
		int bLen = b.length;
		int[] c= new int[aLen+bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}

	public static Image getDrawnOutline(Image input, long neededBits, Dimension dim){
		int neededPixels = (int)(neededBits / 6);
		BufferedImage bufferedImage = new BufferedImage((int)dim.getWidth(), (int)dim.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Image newImage = input.getScaledInstance((int)dim.getWidth(), (int)dim.getHeight(), Image.SCALE_SMOOTH);
		Graphics2D g2a = bufferedImage.createGraphics();
		g2a.drawImage(newImage, 0, 0, null);

		g2a.setColor(Color.RED);
		Double scaleFactor = (double)dim.getWidth() / (double)input.getWidth(null);

		int endY = (int)(((int)(neededPixels / input.getWidth(null)) + 1) * scaleFactor);
		int endX = (int)((neededPixels % input.getWidth(null)) * scaleFactor);

		if(neededPixels <= bufferedImage.getWidth()){
			g2a.drawLine(0, 0, neededPixels, 0);
			g2a.dispose();
			return bufferedImage;
		}else{
			g2a.drawLine(0, 0, bufferedImage.getWidth() - 1, 0);
			g2a.drawLine(0, 0, 0, endY);
			g2a.drawLine(bufferedImage.getWidth() - 1, 0, bufferedImage.getWidth() - 1, endY - 1);
			g2a.drawLine(0, endY, endX, endY);
			g2a.drawLine(endX, endY - 1, bufferedImage.getWidth() - 1, endY - 1);

			g2a.dispose();
			return bufferedImage;
		}
	}
}

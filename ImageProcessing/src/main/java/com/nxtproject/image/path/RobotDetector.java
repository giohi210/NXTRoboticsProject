package com.nxtproject.image.path;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;

public class RobotDetector {
	public static String getImageCharacters(int startX, int startY, int endX, int endY, int[] pixels, int scansize, boolean hasAlpha) {
		int bpp = hasAlpha ? 4 : 3;
		ByteBuffer buffer = ByteBuffer.allocate((endX - startX) * (endY - startY) * bpp);
		int[] out = new int[(endX - startX) * (endY - startY)];
		int count = 0;
		
		for(int j = startY; j < endY; j++) {
			for(int i = startX; i < endX; i++) {
				int data = pixels[j * scansize + i];
				out[count++] = data;
				
				if(hasAlpha) buffer.put((byte)((data >> 24) & 0xFF));
				buffer.put((byte)((data >> 16) & 0xFF));
				buffer.put((byte)((data >> 8) & 0xFF));
				buffer.put((byte)(data & 0xFF));
			}
		}
		buffer.flip();
		
		TessBaseAPI api = new TessBaseAPI();
		
        if (api.Init("../ImageProcessing/", "ENG") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }
        
        api.SetImage(buffer, endX - startX, endY - startY, bpp, (endX - startX) * bpp);
        
        BytePointer outText = api.GetUTF8Text();
        String result = outText.getString();

        api.End();
        api.close();
        outText.deallocate();
        
        return result;
	}
	
	public static PolarSystem toPolar(CartesianSystem system, float degreeMultiplier) throws IOException {
		return toPolar(system, -1, -1, degreeMultiplier);
	}
	
	public static PolarSystem toPolar(CartesianSystem system, int minRadius, int maxRadius, float degreeMultiplier) throws IOException {
		int sideLength = system.sideLength;
		
		float radius = (int)(sideLength * Math.sqrt(2) / 2);
		int degree = (int)(360 * degreeMultiplier);
		int[] data = new int[(int)radius * degree];
		
		if(maxRadius < 0) maxRadius = (int)radius;
		if(minRadius < 0) minRadius = 0;
		
		int temp = maxRadius;
		maxRadius = Math.max(maxRadius, minRadius);
		minRadius = Math.min(minRadius, temp);
		
		for(int y = minRadius; y < Math.min((int)radius, maxRadius); y++) {
			for(int x = 0; x < degree; x++) {
				float actualAngle = 360.0f * x / degree;
				int cX = (int)((sideLength / 2) + Math.cos(Math.toRadians(actualAngle)) * y);
				int cY = (int)((sideLength / 2) + Math.sin(Math.toRadians(actualAngle)) * y);
				
				if(cY < 0 || cX < 0 || cX >= sideLength || cY >= sideLength) data[y * degree + x] = 0xFF000000;
				else data[y * degree + x] = system.data[cY * sideLength + cX];
			}
		}
		
		return new PolarSystem(data, (int)radius, degree);
	}
	
	public static CartesianSystem toCartesian(PolarSystem system) throws IOException {
		float length = (float)(system.maxRadius * 2 / Math.sqrt(2));
		int sideLength = (int) length;
		int[] data = new int[sideLength * sideLength];
		
		for (int y = 0; y < sideLength; y++) {
			for (int x = 0; x < sideLength; x++) {
				float actualX = x - length / 2;
				float actualY = y - length / 2;
				
				int pX = (int)(Math.toDegrees(Math.atan2(actualY, actualX)) * system.getMaxDegree() / 360);
				int pY = (int)Math.sqrt(actualX * actualX + actualY * actualY);
				
				data[y * sideLength + x] = system.data[pY * system.maxDegree + pX];
			}
		}
		
		return new CartesianSystem(data, sideLength);
	}
	
	public static class PolarSystem {
		private final int maxRadius;
		private final int maxDegree;
		private final int[] data;
		
		public PolarSystem(int[] data, int maxRadius, int maxDegree) {
			this.maxRadius = maxRadius;
			this.maxDegree = maxDegree;
			this.data = data;
		}

		public int getMaxRadius() {
			return maxRadius;
		}

		public int getMaxDegree() {
			return maxDegree;
		}

		public int[] getData() {
			return data;
		}
	}
	
	public static class CartesianSystem {
		private final int sideLength;
		private final int[] data;
		
		public CartesianSystem(int[] data, int sideLength) {
			this.sideLength = sideLength;
			this.data = data;
		}
		
		public int getSideLength() {
			return sideLength;
		}

		public int[] getData() {
			return data;
		}
	}
}
package com.nxtproject.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.nxtproject.image.path.RobotDetector;
import com.nxtproject.image.path.RobotDetector.CartesianSystem;
import com.nxtproject.image.path.RobotDetector.PolarSystem;

public class App {
	public static void main(String[] args) throws IOException {
		Window window = new Window();
		window.showWindow();
		
		/*try {
			BufferedImage image = ImageIO.read(new File("test.png"));
			int[] polar = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), polar, 0, image.getWidth());
			CartesianSystem system = RobotDetector.toCartesian(new PolarSystem(polar, image.getHeight(), image.getWidth()));
			
			BufferedImage output = new BufferedImage(system.getSideLength(), system.getSideLength(), BufferedImage.TYPE_INT_ARGB);
			output.setRGB(0, 0, system.getSideLength(), system.getSideLength(), system.getData(), 0, system.getSideLength());
			ImageIO.write(output, "PNG", new File("save.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		try {
			BufferedImage image = ImageIO.read(new File("../ImageProcessing/save.png"));
			int[] data = new int[image.getWidth() * image.getHeight()];
			image.getRGB(0, 0, image.getWidth(), image.getHeight(), data, 0, image.getWidth());
			PolarSystem system = RobotDetector.toPolar(new CartesianSystem(data, image.getWidth()), 240, 430, 3);
			
			int width = system.getMaxDegree();
			int height = system.getMaxRadius();
			
			boolean hasAlpha = image.getColorModel().hasAlpha();
			
			System.out.println("OCR Outout:\n" + RobotDetector.getImageCharacters(0, 240, width, 430, system.getData(), width, hasAlpha));
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}

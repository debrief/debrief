/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.media.utility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.mwc.cmap.media.PlanetmayoFormats;

public class ImageGenerationUtility {
	private String imageFormat;
	private String targetFolder;
	private String backgroundImageFile;
	private String startTimestampStr;
	
	private int timeInterval = -1;
	private int width = -1;
	private int height = -1;
	private int number = -1;
	private int fontSize = 30;
	private Date startTimestamp;	
	private boolean random;
	
	private BufferedImage backgroundImage;	
	
	public ImageGenerationUtility() {
		
	}
	
	private String getArg(String[] args, int i) {
		if (i >= args.length) {
			return null;
		}
		return args[i];
	}
	
	public void parseCommandLine(String[] args) {
		int inc = 0;
		for (int i = 0; i < args.length; i += inc) {
			inc = 1;
			if ("-f".equals(args[i])) {
				imageFormat = getArg(args, i + 1);
				inc++;
			}
			if ("-t".equals(args[i])) {
				targetFolder = getArg(args, i + 1);
				inc++;
			}
			if ("-b".equals(args[i])) {
				backgroundImageFile = getArg(args, i + 1);
				inc++;
			}
			if ("-w".equals(args[i])) {
				width = Integer.valueOf(getArg(args, i + 1));
				inc++;
			}
			if ("-h".equals(args[i])) {
				height = Integer.valueOf(getArg(args, i + 1));
				inc++;
			}
			if ("-n".equals(args[i])) {
				number = Integer.valueOf(getArg(args, i + 1));
				inc++;
			}	
			if ("-i".equals(args[i])) {
				timeInterval = Integer.valueOf(getArg(args, i + 1));
				inc++;
			}				
			if ("-r".equals(args[i])) {
				random = true;
			}	
			if ("-sd".equals(args[i])) {
				startTimestampStr = getArg(args, i + 1);
				inc++;
			}
			if ("-fs".equals(args[i])) {
				fontSize = Integer.valueOf(getArg(args, i + 1));
				inc++;
			}				
		}
	}
	
	public void validate() throws Exception {
		if (width == -1) {
			throw new Exception("Width must be specified");
		}
		if (width < 100) {
			throw new Exception("Width must be at least 100 pixels");
		}
		if (height == -1) {
			throw new Exception("Height must be specified");
		}
		if (height < 100) {
			throw new Exception("Height must be at least 100 pixels");
		}		
		if (number == -1) {
			throw new Exception("Number of images must be specified");
		}
		if (number < 1) {
			throw new Exception("Number of images must be greater or equals 1");
		}		
		if (imageFormat == null) {
			throw new Exception("Image format must be specified");
		}
		imageFormat = imageFormat.toLowerCase();
		if (! (imageFormat.equals("png") || imageFormat.equals("jpg") || imageFormat.equals("jpeg") || imageFormat.equals("tif") || imageFormat.equals("tiff"))) {
			throw new Exception("Image format " + imageFormat + " isn't support");
		}
		if (targetFolder == null) {
			throw new Exception("Target folder must be specified");
		}
		File file = new File(targetFolder);
		file.mkdirs();
		if (! file.isDirectory()) {
			throw new Exception("Target folder must be folder");
		}		
		if (timeInterval == -1) {
			throw new Exception("Time interval must be specified");
		}
		if (timeInterval < 1) {
			throw new Exception("Time interval must be greater than 0");
		}
		if (fontSize < 1) {
			throw new Exception("Font size must be greater than 0");
		}		
		if (backgroundImageFile == null) {
			backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2d = backgroundImage.createGraphics();
			graphics2d.setColor(Color.WHITE);
			graphics2d.setBackground(Color.WHITE);
			graphics2d.fillRect(0, 0, width, height);
		} else {
			if (backgroundImageFile.equals("screen")) {
				Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
				backgroundImage = new Robot().createScreenCapture(new Rectangle(size));
			} else {
				backgroundImage = ImageIO.read(new File(backgroundImageFile));
			}
		}
		if (startTimestampStr != null) {
			startTimestamp = PlanetmayoFormats.getInstance().getDateFormat().parse(startTimestampStr);
		} else {
			startTimestamp = new Date();
		}
	}
	
	public void printHelp() {
		System.out.println("Usage: java -jar generate-images.jar <args>[1..n] ");
		System.out.println("\t-t <folder name> - target folder (required)");
		System.out.println("\t-f <format name> - format name 'png', 'jpg' or 'tif' (required)");
		System.out.println("\t-n <number> - number of generated images (required >= 1)");
		System.out.println("\t-w <width> - width of generated images (required >= 100)");
		System.out.println("\t-h <height> - height of generated images (required >= 100)");
		System.out.println("\t-i <seconds> - time interval between images (required >= 1)");
		System.out.println("\t-sd \"<date in format yyyy-MM-dd hh:mm:ss>\" - date and time of first generated image (default: current date time)");
		System.out.println("\t-b <filename> - background image, if filename = 'screen' it captures current screenshot");
		System.out.println("\t-r - random interval");
		System.out.println("\t-fs <font size> - font size to write timestamp (default: 30)");		
		System.out.println();
		System.out.println("Example: java -jar generate-images.jar -t ./images -f png -n 30 -w 1024 -h 768 -i 10 -sd \"2011-02-04 15:00:00\"");
	}
	
	public void generateImages() throws Exception {
		long current = 0;
		final SimpleDateFormat timeFormat = PlanetmayoFormats.getInstance().getTimeFormat();
		for (int i = 0; i < number; i++) {
			Date currentDate = new Date(startTimestamp.getTime() + current);
			String fileName = PlanetmayoFormats.getInstance().encodeDateInFileName(currentDate, imageFormat);
			
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image.createGraphics();
			graphics.drawImage(backgroundImage, 0, 0, width, height, 0, 0, backgroundImage.getWidth(), backgroundImage.getHeight(), null);
			graphics.setColor(Color.BLACK);
			graphics.setFont(new Font(graphics.getFont().getFamily(), Font.BOLD, fontSize));
			graphics.drawString(timeFormat.format(currentDate), width / 2 - 50, height / 2 - 10);			
			ImageIO.write(image, imageFormat, new File(targetFolder, fileName));
			current += (random ? ((Math.random() * (timeInterval - 1.0)) + 1) : timeInterval) * 1000;
		}
	}
	
	
	public static void main(String[] args) {
		ImageGenerationUtility utility = new ImageGenerationUtility();
		if (args.length == 0) {
			utility.printHelp();
			return;
		}
		try {
			utility.parseCommandLine(args);
			utility.validate();
			utility.generateImages();
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
			System.out.println("---------------------------------------------------------");
			utility.printHelp();
		}
	}
}

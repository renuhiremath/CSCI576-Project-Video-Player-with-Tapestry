/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 *
 * @author prana
 */
public class MeanBlockProcessor {

    public BufferedImage frameImage;
    public double[][] intensityValues;

    public MeanBlockProcessor(BufferedImage image) {
        this.frameImage = image;
        this.intensityValues = new double[image.getWidth()][image.getHeight()];
    }

    public void calculateIntensityValues() {

        Color c;
        for (int i = 0; i < frameImage.getWidth(); i++) {
            for (int j = 0; j < frameImage.getHeight(); j++) {
                c = new Color(frameImage.getRGB(i, j));
                intensityValues[i][j] = c.getRed() * 0.3 + c.getGreen() * 0.59 + c.getBlue() * 0.11;
            }
        }

    }

    public double getMeanBlockIntensity() {
        double sum = 0;
        for (int i = 0; i < intensityValues.length; i++) {
            for (int j = 0; j < intensityValues[0].length; j++) {
                sum += intensityValues[i][j];
            }
        }
        return (double) (sum / (frameImage.getWidth() * frameImage.getHeight()));
    }

    public double[][] getIntensityValues() {
        return this.intensityValues;
    }

    public static void main(String[] args) {

        try {
            BufferedImage img1 = ImageIO.read(new File("kframe_50.png"));
            BufferedImage img2 = ImageIO.read(new File("kframe_52.png"));
            System.out.println("Hello");
            MeanBlockProcessor f1 = new MeanBlockProcessor(img1);
            f1.calculateIntensityValues();
            MeanBlockProcessor f2 = new MeanBlockProcessor(img2);
            f2.calculateIntensityValues();
            System.out.println(Arrays.deepToString(f1.getIntensityValues()));
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

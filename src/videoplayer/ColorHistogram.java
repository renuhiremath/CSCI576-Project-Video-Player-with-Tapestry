/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author prana
 */
public class ColorHistogram {

    public int[] redChannel;
    public int[] greenChannel;
    public int[] blueChannel;

    public static int width = 352;
    public static int height = 288;

    public BufferedImage frameImage = null;

    public BufferedImage getFrameImage() {
        return frameImage;
    }

    public ColorHistogram() {

        //Initialize arrays for each color channel
        this.redChannel = new int[256];
        this.greenChannel = new int[256];
        this.blueChannel = new int[256];

    }

    public ColorHistogram(BufferedImage frameImage) {
        this.redChannel = new int[256];
        this.greenChannel = new int[256];
        this.blueChannel = new int[256];
        this.frameImage = frameImage;
        computeHistogram();
    }

    public void setFrameImage(BufferedImage frameImage) {
        this.frameImage = frameImage;
    }

    public void computeHistogram() {
        if (this.frameImage == null) {
            System.out.println("Histogram Not Calculated : Image NULL");
        }

        Color c = null;

        for (int i = 0; i < frameImage.getWidth(); i++) {
            for (int j = 0; j < frameImage.getHeight(); j++) {
                c = new Color(frameImage.getRGB(i, j));
                redChannel[c.getRed()]++;
                greenChannel[c.getGreen()]++;
                blueChannel[c.getBlue()]++;
            }
        }

    }

    public static float getSimpleDifference(ColorHistogram previous, ColorHistogram current) {

        double histogramSum = 0;
        double coeff = width * height * 2 * 3;

        for (int i = 0; i < 256; i++) {

            histogramSum += Math.abs(previous.getRedChannel()[i] - current.getRedChannel()[i]);
            histogramSum += Math.abs(previous.getGreenChannel()[i] - current.getGreenChannel()[i]);
            histogramSum += Math.abs(previous.getBlueChannel()[i] - current.getBlueChannel()[i]);
        }

        return (float) (histogramSum / coeff);

    }

    public static float getMaxDifference(ColorHistogram previous, ColorHistogram current) {
        double histogramSum = 0;
        double coeff = width * height * 2;

        for (int i = 0; i < 256; i++) {
            if (previous.getRedChannel()[i] >= previous.getGreenChannel()[i] && previous.getRedChannel()[i] >= previous.getBlueChannel()[i]) {
                histogramSum += Math.abs(previous.getRedChannel()[i] - current.getRedChannel()[i]);
            }

            if (previous.getGreenChannel()[i] >= previous.getBlueChannel()[i] && previous.getGreenChannel()[i] >= previous.getRedChannel()[i]) {
                histogramSum += Math.abs(previous.getGreenChannel()[i] - current.getGreenChannel()[i]);

            }

            if (previous.getBlueChannel()[i] >= previous.getGreenChannel()[i] && previous.getBlueChannel()[i] >= previous.getRedChannel()[i]) {
                histogramSum += Math.abs(previous.getBlueChannel()[i] - current.getBlueChannel()[i]);

            }
        }

        return (float) (histogramSum / coeff);
    }

    public float getWeightedDifference(ColorHistogram previous, ColorHistogram current) {

        double coeffs[] = calculateCoefficients(current.getFrameImage());
        int diff1 = 0;
        int diff2 = 0;
        int diff3 = 0;
        for (int i = 0; i < 256; i++) {
            diff1 += Math.abs(current.getRedChannel()[i] - previous.getRedChannel()[i]);
            diff2 += Math.abs(current.getGreenChannel()[i] - previous.getGreenChannel()[i]);
            diff3 += Math.abs(current.getBlueChannel()[i] - previous.getBlueChannel()[i]);
        }
        //System.out.println(Arrays.toString(coeffs));
        float returnValue = (float)(diff1 * coeffs[0] + diff2 * coeffs[1] + diff3 * coeffs[2]);
        double norm = 2 * 3 * width * height;
        return (float)((float)returnValue/norm);
    }

    public double[] calculateCoefficients(BufferedImage image) {
        double coeffs[] = new double[3];
        ImageSplitter colorSplit = new ImageSplitter(image);
        coeffs[0] = calculateIntensity(colorSplit.getChannelImages()[0]);
        coeffs[1] = calculateIntensity(colorSplit.getChannelImages()[1]);
        coeffs[2] = calculateIntensity(colorSplit.getChannelImages()[2]);
        double completeLuminance = (coeffs[0] + coeffs[1] + coeffs[2]) / (double) 3;
        coeffs[0] = coeffs[0] / completeLuminance;
        coeffs[1] = coeffs[1] / completeLuminance;
        coeffs[2] = coeffs[2] / completeLuminance;

        return coeffs;
    }

    public double calculateIntensity(BufferedImage image) {
        long counter = 0;
        Color c;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {

                try {
                    c = new Color(image.getRGB(x, y));
                    counter += c.getBlue();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }

        double intensity = (double) counter / ((double) image.getWidth() * image.getHeight());
        return intensity;
    }

    public int[] getRedChannel() {
        return redChannel;
    }

    public int[] getGreenChannel() {
        return greenChannel;
    }

    public int[] getBlueChannel() {
        return blueChannel;
    }

}

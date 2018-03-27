/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author prana
 */
public class Video {

    public SeamCarving seamCarving = new SeamCarving();
    public ArrayList<BufferedImage> videoFrames = new ArrayList<BufferedImage>();
    public final int width = 352;
    public final int height = 288;
    public DescriptiveStatistics stats;
    public float mean;
    public float stdev;
    public float threshold;
    boolean first = true;
    public TapestryGenerator tapestryGen;

    public final int numberOfFrames = 5 * 20 * 60;
    public File cifVideoFile;
    public int currentFrame = 0;
    public ArrayList<Integer> keyframeNumbers;

    public static void main(String args[]) {
        System.out.println("Main!");
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }
    public Audio audio;

    public Audio getAudio() {
        return audio;
    }

    public ArrayList<Float> histogramDiff;
    public LinkedList<BufferedImage> keyFrames;

    public Video(String cifFilePath, String audioFilePath) {
        
        keyframeNumbers = new ArrayList<>();
        audio = new Audio(audioFilePath);
        stats = new DescriptiveStatistics();
        this.cifVideoFile = new File(cifFilePath);
        histogramDiff = new ArrayList<Float>();
    }

    public BufferedImage getVideoFrame(int frameNumber) {
        try {

            InputStream inputStream = new FileInputStream(this.cifVideoFile);
            long length = width * height * 3;
            byte[] bytes = new byte[(int) length];
            BufferedImage frameImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            inputStream.skip(frameNumber * length);
            inputStream.read(bytes);

            int ind = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    byte r = bytes[ind];
                    byte g = bytes[ind + height * width];
                    byte b = bytes[ind + height * width * 2];
                    int pixel = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                    frameImage.setRGB(x, y, pixel);
                    ind = ind + 1;
                }
            }
            return frameImage;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public BufferedImage getNextFrame() {

        this.currentFrame++;
        BufferedImage k = this.getVideoFrame(this.currentFrame);
        return k;
    }

    public void computeHistogramDifference() {
        float diff = 0;
        ColorHistogram previous;
        ColorHistogram current;

        //Complete Five Minute Video === 6000 Frames
        for (int i = 1; i < 2500; i++) {
            previous = new ColorHistogram(this.getVideoFrame(i - 1));
            current = new ColorHistogram(this.getVideoFrame(i));
            diff = ColorHistogram.getSimpleDifference(previous, current);
            this.histogramDiff.add(diff);
        }
    }

    public void computeMaxHistogramDifference() {
        float diff = 0;
        ColorHistogram previous;
        ColorHistogram current;

        for (int i = 1; i < 6000; i++) {
            previous = new ColorHistogram(this.getVideoFrame(i - 1));
            current = new ColorHistogram(this.getVideoFrame(i));
            diff = ColorHistogram.getMaxDifference(previous, current);
            this.histogramDiff.add(diff);
        }

    }

    public void computeWeightedHistogramDifference() {
        float diff = 0;
        ColorHistogram previous;
        ColorHistogram current;
        ColorHistogram temp = new ColorHistogram();
        for (int i = 1; i < 6000; i++) {
            previous = new ColorHistogram(this.getVideoFrame(i - 1));
            current = new ColorHistogram(this.getVideoFrame(i));
            diff = temp.getWeightedDifference(previous, current);
            //System.out.println(diff);
            this.histogramDiff.add(diff);
        }
    }

    public void computeSecondOrder() {
        ArrayList<Float> secondOrderDifference = new ArrayList<Float>();
        for (int i = 1; i < histogramDiff.size(); i++) {
            //secondOrderDifference.add(Math.abs(histogramDiff.get(i) - histogramDiff.get(i-1)));
            secondOrderDifference.add(histogramDiff.get(i) - histogramDiff.get(i - 1));
        }
        histogramDiff = secondOrderDifference;
    }

    public void dumpKeyFrames(String destFolder) {
        System.out.println("DKF");
        //File opFile;
        int i = 1;
        for (float k : histogramDiff) {
            if (k > this.threshold) {
                try {
                    SeamCarving.performSeamCarving(this.getVideoFrame(i+1), destFolder + "/kFrame_" + i + ".png", 100, 100);
                    keyframeNumbers.add(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }

    public void dumpKeyFrames() {
        File opFile;
        int i = 1;
        for (float k : histogramDiff) {
            if (k > this.threshold) {
                try {
                    opFile = new File("kFrame_" + i + ".png");
                    SeamCarving.performSeamCarving(this.getVideoFrame(i+1), "test/" + "kFrame_" + i + ".png", 100, 100);
                    keyframeNumbers.add(i + 1);
                } catch (Exception e) {

                }
            }
            i++;
        }

    }

    public ArrayList<BufferedImage> getVideoFrames() {
        return videoFrames;
    }

    public ArrayList<Float> getHistogramDiff() {
        return histogramDiff;
    }

    public float recFunction(float value) {
        if (value <= 0.5f) {
            return 0;
        } else {
            return 1;
        }
    }

    public void calculateThreshold(float factor) {

        for (float k : histogramDiff) {
            stats.addValue((double) k);
        }

        this.mean = (float) stats.getMean();
        this.stdev = (float) stats.getStandardDeviation();
        this.threshold = this.mean + factor * (this.stdev);

    }

    public void createDiffHeirarchy(int levels) {

        float factor = 4.5f;
        try {
            new File("frame_hr").mkdir();
            for (int i = 0; i < levels; i++) {
                new File("frame_hr/level_" + i).mkdir();
                calculateThreshold(factor);
                factor = factor / 2.0f;
                dumpKeyFrames("frame_hr/level_" + i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void generateTapestry() {

        //ArrayList<File> imgFiles = new ArrayList<File>();
        try {
            File[] imgFiles = new File("frame_hr/level_0").listFiles();
            imgFiles = sortFiles(imgFiles);
            for (File f : imgFiles) {
                System.out.println(f.getName());
            }
            tapestryGen = new TapestryGenerator(imgFiles.length);
            tapestryGen.level1(imgFiles, imgFiles.length);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Integer> getKeyframeNumbers() {
        return this.keyframeNumbers;
    }

    public File[] sortFiles(File[] imgFiles) {
        try {
            File temp;
            for (int i = 0; i < imgFiles.length; i++) {
                for (int j = i + 1; j < imgFiles.length; j++) {
                    if (getFrameFromFile(imgFiles[i].getName()) > getFrameFromFile(imgFiles[j].getName())) {
                        temp = imgFiles[i];
                        imgFiles[i] = imgFiles[j];
                        imgFiles[j] = temp;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgFiles;
    }

    public int getFrameFromFile(String fileName) {
        fileName = stripExtension(fileName);
        fileName = fileName.split("_")[1];
        return Integer.parseInt(fileName);
    }

    public String stripExtension(final String s) {
        return s != null && s.lastIndexOf(".") > 0 ? s.substring(0, s.lastIndexOf(".")) : s;
    }
    
    public void calcKeyFramesfromEdge(){
        try{
            this.keyframeNumbers = new ArrayList<Integer>();
            File[] imgFiles = new File("frame_hr/level_0").listFiles();
            imgFiles = sortFiles(imgFiles);
            for (File f : imgFiles) {
                this.keyframeNumbers.add(getFrameFromFile(f.getName()));
            }
        } catch (Exception e){
            
        }
    }

}

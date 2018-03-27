/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author renuhiremath
 */
public class TapestryGenerator {

    String rgbFileName;
    String keyFramesFolder;
    String outputFolder;
    public final int width = 352;
    public final int height = 288;
    int numImages;
    BufferedImage images[];
    BufferedImage finalImage;
    //SeamCarving seamCarvingObject;
    //boolean seamCarvingEnabled = true;
    double ratioL2 = 0.7;
    double ratioL3 = 0.5;

    public TapestryGenerator(int numImages) {
        this.numImages = numImages;
        int newWidth = (int) ((this.numImages / 2.0) + 0.5);
        //creating a black background image
        this.finalImage = new BufferedImage(newWidth * width, height * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = finalImage.createGraphics();
        ig2.setBackground(Color.BLACK);
        ig2.clearRect(0, 0, newWidth * width, height * 2);

        //seamCarvingObject = new SeamCarving();
        //seamCarvingEnabled = true;

    }

    public void level1(File[] imgFiles, int numImages) {
        int rows = 2;
        int cols = numImages / 2 + (numImages % 2);

        int chunkWidth, chunkHeight;
        int type;
        //creating a bufferd image array from image files
        BufferedImage[] buffImages = new BufferedImage[numImages];
        for (int i = 0; i < numImages; i++) {
            try {
                buffImages[i] = ImageIO.read(imgFiles[i]);
            } catch (IOException e) {
                System.err.println("Cant read the file " + imgFiles[i]);
                return;
            }
        }
        type = buffImages[0].getType();
        chunkWidth = buffImages[0].getWidth();
        chunkHeight = buffImages[0].getHeight();

        int newWidth = (int) ((numImages * chunkWidth / 2.0)+chunkWidth );
        //Initializing the final image
        //this.finalImage = new BufferedImage(newWidth, chunkHeight * rows, type);
        this.finalImage = new BufferedImage(newWidth, chunkHeight * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = finalImage.createGraphics();
        ig2.setBackground(Color.BLACK);
        ig2.clearRect(0, 0, newWidth * width, height * 2);
        
        int num = 0;
        int width = 0;
        BufferedImage blackImg;
        try {
            blackImg = ImageIO.read(new File("black.bmp"));
        } catch (IOException e) {
            System.err.println("Cant read the file black.bmp");
            return;
        }
        //construct row1
        for (int i = 0; i < cols && num < numImages; i++, num += 2) {
            System.out.println(num);
            if ((num == (numImages - 2) && numImages % 2 == 0)) {
                BufferedImage tempImg = new BufferedImage((int) (chunkWidth * 1.5), chunkHeight, type);
                tempImg.createGraphics().drawImage(buffImages[num], 0, 0, null);
                tempImg.createGraphics().drawImage(blackImg, chunkWidth, 0, null);
                this.finalImage.createGraphics().drawImage(tempImg, width, 0, null);
                width += (chunkWidth * 1.5);
            } else {
                this.finalImage.createGraphics().drawImage(buffImages[num], width, 0, null);
                width += chunkWidth;
            }
        }

        num = 1;
        width = 0;
        //construct row2
        for (int i = 0; i < cols && num < numImages; i++, num += 2) {
            System.out.println(num);
            if (num == 1) {
                BufferedImage tempImg = new BufferedImage((int) (chunkWidth * 1.5), chunkHeight, type);
                tempImg.createGraphics().drawImage(blackImg, 0, 0, null);
                tempImg.createGraphics().drawImage(buffImages[num], chunkWidth / 2, 0, null);
                this.finalImage.createGraphics().drawImage(tempImg, width, chunkHeight, null);
                width += (chunkWidth * 1.5);
            } else {
                this.finalImage.createGraphics().drawImage(buffImages[num], width, chunkHeight, null);
                width += chunkWidth;
            }
            if ((numImages % 2 != 0 && num == numImages - 2)) {        
                BufferedImage tempImg = new BufferedImage((int) (chunkWidth * 0.5), chunkHeight, type);
                tempImg.createGraphics().drawImage(blackImg, 0, 0, null);
                this.finalImage.createGraphics().drawImage(tempImg, width, chunkHeight, null);
                width += (chunkWidth * 0.5);
            }
        }

        try {
            ImageIO.write(this.finalImage, "png", new File("level1.bmp"));
        } catch (IOException e) {
            System.err.println("Trouble saving " + "finalImg.bmp");
            return;
        }

        int horizontalSeam = 100;
        int verticalSeam = 100;
        //SeamCarving seamCarvingObject = new SeamCarving();
        //if (seamCarvingEnabled)
            //seamCarvingObject.performSeamCarving(this.finalImage, "level1.bmp", horizontalSeam, verticalSeam);
    }

    public void level2(File[] imgFiles1, int n1, File[] imgFiles2, int n2, int pos) {
        int rows = 2;
        int chunkWidth, chunkHeight;
        int type;

        BufferedImage blackImg;
        try {
            blackImg = ImageIO.read(new File("black.bmp"));
        } catch (IOException e) {
            System.err.println("Cant read the file black.bmp");
            return;
        }

        //creating a bufferd image array from image files
        BufferedImage[] buffImages1 = new BufferedImage[n1];
        BufferedImage[] buffImages2 = new BufferedImage[n2];
        for (int i = 0; i < n1; i++) {
            try {
                buffImages1[i] = ImageIO.read(imgFiles1[i]);
            } catch (IOException e) {
                System.err.println("Cant read the file " + imgFiles1[i]);
                return;
            }
        }
        for (int i = 0; i < n2; i++) {
            try {
                buffImages2[i] = ImageIO.read(imgFiles2[i]);
            } catch (IOException e) {
                System.err.println("Cant read the file " + imgFiles2[i]);
                return;
            }
        }

        type = buffImages1[0].getType();
        chunkWidth = buffImages1[0].getWidth();
        chunkHeight = buffImages1[0].getHeight();

        int newWidth = (int) ((n1 * chunkWidth / 3.0) + (chunkWidth / 3.0) + (n2 * chunkWidth / 2.0));
        //Initializing the final image
        //this.finalImage = new BufferedImage(newWidth, chunkHeight * rows, type);
        this.finalImage = new BufferedImage(newWidth, chunkHeight * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = finalImage.createGraphics();
        ig2.setBackground(Color.BLACK);
        ig2.clearRect(0, 0, newWidth * width, height * 2);

        int num = 0;
        int width = 0;
        boolean up = true;
        int row1Width = 0;
        int row2Width = 0;

        BufferedImage tempImg = new BufferedImage((int) (chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), type);
        tempImg.createGraphics().drawImage(blackImg, 0, 0, null);
        this.finalImage.createGraphics().drawImage(tempImg, 0, chunkHeight, null);
        row2Width = (int) (chunkWidth / 3.0);

        for (int i = 0; i < pos; i++) {

            if (up)//row1
            {
                this.finalImage.createGraphics().drawImage(buffImages1[i], row1Width, (int) (chunkHeight / 3.0), (int) (2 * chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), null);
                row1Width += (int) (2 * chunkWidth / 3.0);
                up = false;
            } else//row2
            {
                this.finalImage.createGraphics().drawImage(buffImages1[i], row2Width, chunkHeight, (int) (2 * chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), null);
                row2Width += (int) (2 * chunkWidth / 3.0);
                up = true;
            }
        }

        for (int  j = 0; j < n2;  j++) {

            if (up)//row1
            {
                this.finalImage.createGraphics().drawImage(buffImages2[j], row1Width, 0, null);
                row1Width += (int) (chunkWidth);
                up = false;
            } else//row2
            {
                this.finalImage.createGraphics().drawImage(buffImages2[j], row2Width, chunkHeight, null);
                row2Width += (int) (chunkWidth);
                up = true;
            }
        }

        for (int i = pos; i < n1; i++) {

            if (up)//row1
            {
                this.finalImage.createGraphics().drawImage(buffImages1[i], row1Width, (int) (chunkHeight / 3.0), (int) (2 * chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), null);
                row1Width += (int) (2 * chunkWidth / 3.0);
                up = false;
            } else//row2
            {
                this.finalImage.createGraphics().drawImage(buffImages1[i], row2Width, chunkHeight, (int) (2 * chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), null);
                row2Width += (int) (2 * chunkWidth / 3.0);
                up = true;
            }
        }

        if (up) {
            this.finalImage.createGraphics().drawImage(tempImg, row1Width, (int) (chunkHeight / 3.0), null);
            row1Width = (int) (chunkWidth / 3.0);
        } else {
            this.finalImage.createGraphics().drawImage(tempImg, row2Width, chunkHeight, null);
            row2Width = (int) (chunkWidth / 3.0);
        }

        try {
            ImageIO.write(this.finalImage, "png", new File("level2.bmp"));
        } catch (IOException e) {
            System.err.println("Trouble saving " + "finalImg.bmp");
            return;
        }

        int horizontalSeam = 100;
        int verticalSeam = 100;
        //SeamCarving seamCarvingObject = new SeamCarving();
        //if (seamCarvingEnabled) {
        //    seamCarvingObject.performSeamCarving(this.finalImage, "level2.bmp", horizontalSeam, verticalSeam);
        //}
    }

    public void level3(File[] imgFiles1, int n1, File[] imgFiles2, int n2, File[] imgFiles3, int n3, int pos1, int pos2) {
        int rows = 2;
        int chunkWidth, chunkHeight;
        int type;
        //creating a bufferd image array from image files
        BufferedImage[] buffImages1 = new BufferedImage[n1];
        BufferedImage[] buffImages2 = new BufferedImage[n2];
        BufferedImage[] buffImages3 = new BufferedImage[n3];
        for (int i = 0; i < n1; i++) {
            try {
                buffImages1[i] = ImageIO.read(imgFiles1[i]);
            } catch (IOException e) {
                System.err.println("Cant read the file " + imgFiles1[i]);
                return;
            }
        }
        for (int i = 0; i < n2; i++) {
            try {
                buffImages2[i] = ImageIO.read(imgFiles2[i]);
            } catch (IOException e) {
                System.err.println("Cant read the file " + imgFiles2[i]);
                return;
            }
        }

        for (int i = 0; i < n3; i++) {
            try {
                buffImages3[i] = ImageIO.read(imgFiles3[i]);
            } catch (IOException e) {
                System.err.println("Cant read the file " + imgFiles2[i]);
                return;
            }
        }

        BufferedImage blackImg;
        try {
            blackImg = ImageIO.read(new File("black.bmp"));
        } catch (IOException e) {
            System.err.println("Cant read the file black.bmp");
            return;
        }

        type = buffImages1[0].getType();
        chunkWidth = buffImages1[0].getWidth();
        chunkHeight = buffImages1[0].getHeight();

        int newWidth = (int) ((n1 * chunkWidth / 6.0) + (chunkWidth / 6.0) + (n2 * chunkWidth / 3.0) + (n3 * chunkWidth / 2.0));
        //Initializing the final image
        //this.finalImage = new BufferedImage(newWidth, chunkHeight * rows, type);
        this.finalImage = new BufferedImage(newWidth, chunkHeight * 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D ig2 = finalImage.createGraphics();
        ig2.setBackground(Color.BLACK);
        ig2.clearRect(0, 0, newWidth * width, height * 2);

        int num = 0;
        int width = 0;
        boolean up = true;
        int row1Width = 0;
        int row2Width = 0;

        BufferedImage tempImg = new BufferedImage((int) (chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), type);
        tempImg.createGraphics().drawImage(blackImg, 0, 0, null);
        this.finalImage.createGraphics().drawImage(tempImg, 0, chunkHeight, null);
        row2Width = (int) (chunkWidth / 6.0);

        for (int i = 0; i < pos1; i++) {

            if (up)//row1
            {
                this.finalImage.createGraphics().drawImage(buffImages1[i], row1Width, (int) (2 * chunkHeight / 3.0), (int) (chunkWidth / 3.0), (int) (chunkHeight / 3.0), null);
                row1Width += (int) (chunkWidth / 3.0);
                up = false;
            } else//row2
            {
                this.finalImage.createGraphics().drawImage(buffImages1[i], row2Width, chunkHeight, (int) (chunkWidth / 3.0), (int) (chunkHeight / 3.0), null);
                row2Width += (int) (chunkWidth / 3.0);
                up = true;
            }
        }

        for (int i = 0; i < pos2; i++) {

            if (up)//row1
            {
                this.finalImage.createGraphics().drawImage(buffImages2[i], row1Width, (int) (chunkHeight / 3.0), (int) (2 * chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), null);
                row1Width += (int) (2 * chunkWidth / 3.0);
                up = false;
            } else//row2
            {
                this.finalImage.createGraphics().drawImage(buffImages2[i], row2Width, chunkHeight, (int) (2 * chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), null);
                row2Width += (int) (2 * chunkWidth / 3.0);
                up = true;
            }
        }

        for (int j = 0; j < n3; j++) {

            if (up)//row1
            {
                this.finalImage.createGraphics().drawImage(buffImages3[j], row1Width, 0, null);
                row1Width += (int) (chunkWidth);
                up = false;
            } else//row2
            {
                this.finalImage.createGraphics().drawImage(buffImages3[j], row2Width, chunkHeight, null);
                row2Width += (int) (chunkWidth);
                up = true;
            }
        }

        for (int i = pos2; i < n2; i++) {

            if (up)//row1
            {
                this.finalImage.createGraphics().drawImage(buffImages2[i], row1Width, (int) (chunkHeight / 3.0), (int) (2 * chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), null);
                row1Width += (int) (2 * chunkWidth / 3.0);
                up = false;
            } else//row2
            {
                this.finalImage.createGraphics().drawImage(buffImages2[i], row2Width, chunkHeight, (int) (2 * chunkWidth / 3.0), (int) (2 * chunkHeight / 3.0), null);
                row2Width += (int) (2 * chunkWidth / 3.0);
                up = true;
            }
        }

        for (int i = pos1; i < n1; i++) {

            if (up)//row1
            {
                this.finalImage.createGraphics().drawImage(buffImages1[i], row1Width, (int) (2 * chunkHeight / 3.0), (int) (chunkWidth / 3.0), (int) (chunkHeight / 3.0), null);
                row1Width += (int) (chunkWidth / 3.0);
                up = false;
            } else//row2
            {
                this.finalImage.createGraphics().drawImage(buffImages1[i], row2Width, chunkHeight, (int) (chunkWidth / 3.0), (int) (chunkHeight / 3.0), null);
                row2Width += (int) (chunkWidth / 3.0);
                up = true;
            }
        }

        if (up) {
            this.finalImage.createGraphics().drawImage(tempImg, row1Width, (int) (chunkHeight / 3.0), null);
            row1Width = (int) (chunkWidth / 3.0);
        } else {
            this.finalImage.createGraphics().drawImage(tempImg, row2Width, chunkHeight, null);
            row2Width = (int) (chunkWidth / 3.0);
        }

        try {
            ImageIO.write(this.finalImage, "png", new File("level3.bmp"));
        } catch (IOException e) {
            System.err.println("Trouble saving " + "finalImg.bmp");
            return;
        }

        BufferedImage temp = this.finalImage;

        int horizontalSeam = 100;
        int verticalSeam = 100;
        //SeamCarving seamCarvingObject = new SeamCarving();
        //if (seamCarvingEnabled) {
        //    seamCarvingObject.performSeamCarving(this.finalImage, "level3.bmp", horizontalSeam, verticalSeam);
        //}
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int n = 20;
        String[] names1 = new String[20];
        String[] names2 = new String[20];
        String[] names3 = new String[20];
        TapestryGenerator tapGen = new TapestryGenerator(n);
        //for (int i = 0; i < n; i++) {
        //    names1[i] = "output/output_" + String.valueOf((i + 1) * 100) + "_modified.bmp";
        //}

        names1[0] = "output/output_100_modified.bmp";
        names1[1] = "output/output_200_modified.bmp";
        names1[2] = "output/output_300_modified.bmp";
        names1[3] = "output/output_400_modified.bmp";
        names1[4] = "output/output_900_modified.bmp";
        names1[5] = "output/output_1100_modified.bmp";
        
        names2[0] = "output/output_500_modified.bmp";
        names2[1] = "output/output_600_modified.bmp";
        names2[2] = "output/output_700_modified.bmp";
        names2[3] = "output/output_800_modified.bmp";
        names2[4] = "output/output_900_modified.bmp";
        names2[5] = "output/output_1100_modified.bmp";
        names2[6] = "output/output_1200_modified.bmp";
        names2[7] = "output/output_1300_modified.bmp";
        
        names3[0] = "output/output_1400_modified.bmp";
        names3[1] = "output/output_1500_modified.bmp";
        names3[2] = "output/output_1000_modified.bmp";
        names3[3] = "output/output_1600_modified.bmp";
        names3[4] = "output/output_1700_modified.bmp";
        names3[5] = "output/output_1800_modified.bmp";
        names3[6] = "output/output_1900_modified.bmp";
        names3[7] = "output/output_2000_modified.bmp";

        //fetching image files
        File[] imgFiles1 = new File[n];
        File[] imgFiles2 = new File[n];
        File[] imgFiles3 = new File[n];
        for (int i = 0; i < 6; i++) {
            imgFiles1[i] = new File(names1[i]);
        }
        for (int i = 0; i < 8; i++) {
            imgFiles2[i] = new File(names2[i]);
        }
        for (int i = 0; i < 8; i++) {
            imgFiles3[i] = new File(names3[i]);
        }
        //tapGen.level1(imgFiles1,10);
        tapGen.level2(imgFiles1, 6, imgFiles2, 6, 1);
        //tapGen.level2(imgFiles1, 9, imgFiles2, 10, 5);
        //tapGen.level2(imgFiles1, 10, imgFiles2, 9, 5);
        //tapGen.level2(imgFiles1, 9, imgFiles2, 9, 5);
        //tapGen.level3(imgFiles1, 4, imgFiles2, 8, imgFiles3, 6, 2, 4);
    }

}

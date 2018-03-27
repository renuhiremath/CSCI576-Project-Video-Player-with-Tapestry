/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author prana
 */
public final class ImageSplitter {
    
    private BufferedImage channelR;
    private BufferedImage channelG;
    private BufferedImage channelB;
    
    public ImageSplitter(BufferedImage image){
        channelR = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        channelG = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        channelB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        splitImageChannels(image);
    }
    
    public void splitImageChannels(BufferedImage image){
        
        Color c;
        for(int i=0; i<image.getWidth(); i++){
            for(int j=0; j<image.getHeight(); j++){
                c = new Color(image.getRGB(i, j));
                //System.out.println(c.getRed()+ " " + c.getGreen() + " " + c.getBlue());
                channelR.setRGB(i, j, new Color(c.getRed(),0,0).getRGB());
                channelG.setRGB(i, j, new Color(0,c.getGreen(),0).getRGB());
                channelB.setRGB(i, j, new Color(0,0,c.getBlue()).getRGB());
            }
        }
    }
    
    public BufferedImage[] getChannelImages(){
        BufferedImage[] ret = new BufferedImage[3];
        ret[0] = channelR;
        ret[1] = channelG;
        ret[2] = channelB;
        return ret;
    }
}

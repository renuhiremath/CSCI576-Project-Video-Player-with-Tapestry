/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
 
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
 

/**
 *
 * @author prana
 */
public class Audio {

    long currentFrame;
    public Clip clip;
    String state;
    AudioInputStream audioInputStream;
    String audioFilePath;
    
    public Audio(String audioFilePath){
        this.audioFilePath = audioFilePath;
        try{
            audioInputStream = AudioSystem.getAudioInputStream(new File(audioFilePath).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            //clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void play(){
        clip.start();
        state = "PLAY";
    }
    
    public void pause(){
        if(state.equals("PAUSE")){
            System.out.println("Audio is already PAUSED");
        }
        this.currentFrame = this.clip.getMicrosecondPosition();
        clip.stop();
        state = "PAUSE";
    }
    
    public void resume(){
        if(state.equals("PLAY")){
            System.out.println("Still Playin'");
            return;
        }
        
        clip.close();
        resetAudioStream();
        clip.setMicrosecondPosition(currentFrame);
        this.play();
    }
    
    public void restart()
    {
        clip.stop();
        clip.close();
        resetAudioStream();
        currentFrame = 0L;
        clip.setMicrosecondPosition(0);
        this.play();
    }
    
    public void stop() 
    {
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }
    
    public void jump(long c)
    {
        if (c > 0 && c < clip.getMicrosecondLength()) 
        {
            clip.stop();
            clip.close();
            resetAudioStream();
            currentFrame = c;
            clip.setMicrosecondPosition(c);
            this.play();
        }
    }
    
     
    
    public void resetAudioStream(){
        try{
             audioInputStream = AudioSystem.getAudioInputStream(new File(audioFilePath).getAbsoluteFile());
             clip.open(audioInputStream);
             clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
   
}

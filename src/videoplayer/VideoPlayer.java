/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package videoplayer;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.Timer;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
//import org.apache.commons.io
/**
 *
 * @author prana
 */
public class VideoPlayer extends Application {

    public VBox rootBox;
    //public HBox videoFrame;
    public GridPane controlsPane;
    public Button playButton;
    public Button stopButton;
    public Button pauseButton;
    public Image frameImage;
    public ImageView frameImageView;
    public Image playButtonIcon;
    public Image pauseButtonIcon;
    public Image stopButtonIcon;
    public Timer playVideoTimer;
    public ImageView tapestry;
    //public Timer playAudioTimer;
    //public Thread playAudio; 
    public Label testLabel;
    public VideoPlayerState playerState;
    public Audio audio;
    public HBox tapestryBox;
    //LinkedList<Rectangle> rects;
    public TapestryState tapestry_state;

    public int currentFrame = 0;

    @Override
    public void init() {
        this.playerState = VideoPlayerState.PLAYER_RUNNING;
        this.tapestry_state = TapestryState.LEVEL_ONE;
    }

    @Override
    public void start(Stage primaryStage) {

        final Video video = new Video("data/USCVillage.rgb", "data/USCVillage.wav");

        video.computeMaxHistogramDifference();
        video.computeSecondOrder();
        //video.calculateThreshold(5.7f);
        video.createDiffHeirarchy(1);
        //video.calcKeyFramesfromEdge();
        video.generateTapestry();
        int dimensionX = video.width;
        int dimensionY = video.height;

        //Initialize Nodes
        controlsPane = new GridPane();
        rootBox = new VBox();
        //videoFrame = new HBox();
        playButton = new Button();
        pauseButton = new Button();
        stopButton = new Button();
        tapestryBox = new HBox();

        //Seek Testing
        Button seek = new Button("Seek");

        seek.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //playVideoTimer.stop();
                video.setCurrentFrame(1000);
                video.getAudio().jump(1000 * 50000);
            }
        });

        //Window Title
        String title = "Smart Video Player";

        //frameImage = SwingFXUtils.toFXImage(video.getVideoFrame(1000), null);
        frameImageView = new ImageView();
        frameImageView.setFitHeight(480);
        frameImageView.setFitWidth(640);
        frameImageView.setPreserveRatio(true);
        playButtonIcon = new Image(getClass().getClassLoader().getResourceAsStream("resources/icons/play.png"));
        pauseButtonIcon = new Image(getClass().getClassLoader().getResourceAsStream("resources/icons/pause.png"));
        stopButtonIcon = new Image(getClass().getClassLoader().getResourceAsStream("resources/icons/stop.png"));

        BufferedImage tapImage = null;
        //Adding the tapestry
        try {
            tapImage = ImageIO.read(new File("level1.bmp"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        tapestry = new ImageView();
        tapestry.setImage(SwingFXUtils.toFXImage(tapImage, null));
        tapestry.setPickOnBounds(true);
        tapestry.setPreserveRatio(true);
        tapestry.setFitWidth(1280);
        tapestry.setFitHeight(200);

        playButton.setGraphic(new ImageView(playButtonIcon));
        pauseButton.setGraphic(new ImageView(pauseButtonIcon));
        stopButton.setGraphic(new ImageView(stopButtonIcon));
        //videoFrame.getChildren().add(frameImageView);

        controlsPane.setAlignment(Pos.CENTER);
        controlsPane.add(playButton, 1, 1);
        controlsPane.add(pauseButton, 2, 1);
        controlsPane.add(stopButton, 3, 1);
        controlsPane.add(seek, 4, 1);

        Pane p = new Pane(tapestry);
        tapestryBox.getChildren().add(p);
        tapestryBox.setAlignment(Pos.CENTER);

        LinkedList<Rectangle> rects = initTapestryRect(tapImage, video);
        for (Rectangle r : rects) {
            p.getChildren().add(r);
        }

        rootBox.getChildren().addAll(frameImageView, controlsPane, tapestryBox);
        rootBox.setAlignment(Pos.CENTER);
        Scene initialScene = new Scene(rootBox, dimensionX, dimensionY + 100);

        //Using Swing Timers
        playVideoTimer = new Timer(49, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                frameImageView.setImage(SwingFXUtils.toFXImage(video.getNextFrame(), null));
            }
        });

        //Button Actions
        pauseButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switch (playerState) {
                    case PLAYER_RUNNING:
                        playVideoTimer.stop();
                        video.getAudio().pause();
                        playerState = VideoPlayerState.PLAYER_PAUSED;
                        break;
                    case PLAYER_PAUSED:
                        playVideoTimer.start();
                        video.getAudio().resume();
                        playerState = VideoPlayerState.PLAYER_RUNNING;
                        break;
                }
            }
        });

        playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switch (playerState) {
                    case PLAYER_STOPPED:
                        playVideoTimer.start();
                        video.getAudio().restart();
                        playerState = VideoPlayerState.PLAYER_RUNNING;
                        break;
                    case PLAYER_RUNNING:
                        break;
                    case PLAYER_PAUSED:
                        playVideoTimer.start();
                        video.getAudio().resume();
                        playerState = VideoPlayerState.PLAYER_RUNNING;
                        break;
                }
            }
        });

        stopButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switch (playerState) {
                    case PLAYER_RUNNING:
                        frameImageView.setImage(SwingFXUtils.toFXImage(video.getVideoFrame(0), null));
                        playVideoTimer.stop();
                        video.setCurrentFrame(0);
                        video.getAudio().stop();
                        playerState = VideoPlayerState.PLAYER_STOPPED;
                        break;
                }
            }
        });

        primaryStage.setTitle(title);
        primaryStage.setScene(initialScene);
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        video.getAudio().play();
        playVideoTimer.start();

        primaryStage.show();

    }

    @Override
    public void stop() {
        playVideoTimer.stop();
        System.out.println("Video Player Stopped!");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Launch the JavaFX Application
        launch(args);
    }

    public LinkedList<Rectangle> initTapestryRect(BufferedImage tapestryImage, Video v) {

        LinkedList<Rectangle> rects = new LinkedList<>();
        final ArrayList<Integer> keyFrames = v.getKeyframeNumbers();
        System.out.println(keyFrames.size());
        Rectangle temp;
        int recth, rectw, w, h;
        switch (tapestry_state) {
            case LEVEL_ONE:
                w = 0;
                h = 0;
                rectw = (int)(tapestry.getBoundsInParent().getWidth())/((keyFrames.size()/2)+1);
                recth = (int)tapestry.getBoundsInParent().getHeight()/2;
                for (int i = 1; i <= keyFrames.size(); i++) {
                    System.out.println("Im here " + i);
                    if (i % 2 == 0) {
                        temp = new Rectangle();
                        temp.setFill(Color.TRANSPARENT);
                        temp.setX(w);
                        temp.setY(h);
                        temp.setWidth(rectw);
                        temp.setHeight(recth);
                        w = w + rectw / 2;
                        h = h - recth;
                        final int k = i-1;
    
                        temp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                System.out.println(k);
                                System.out.println(keyFrames.get(k));
                                v.setCurrentFrame(keyFrames.get(k));
                                v.getAudio().jump(keyFrames.get(k) * 50000);
                            }
                        });

                        temp.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                //System.out.println("Hovered::" + event.getSource().getClass());
                            }
                        });

                        temp.setOnScroll(new EventHandler<ScrollEvent>() {
                            @Override
                            public void handle(ScrollEvent event) {
                                if (event.isControlDown()) {
                                    //System.out.println("Scroll plus Ctrl");
                                    try {
                                        BufferedImage newtap = ImageIO.read(new File("level_1.bmp"));
                                        tapestry.setImage(SwingFXUtils.toFXImage(newtap, null));
                                        //System.out.println("Set new tapestry");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });

                        rects.add(temp);
                    } else {
                        temp = new Rectangle();
                        temp.setFill(Color.TRANSPARENT);
                        temp.setX(w);
                        temp.setY(h);
                        temp.setWidth(rectw);
                        temp.setHeight(recth);
                        w = w + rectw / 2;
                        h = h + recth;
                        final int k = i-1;
                        temp.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                //playVideoTimer.stop();
                                System.out.println(keyFrames.get(k));
                                v.setCurrentFrame(keyFrames.get(k));
                                v.getAudio().jump(keyFrames.get(k) * 50000);
                            }
                        });

                        temp.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent event) {
                                //System.out.println("Hovered::" + event.getSource().getClass());
                            }
                        });

                        temp.setOnScroll(new EventHandler<ScrollEvent>() {
                            @Override
                            public void handle(ScrollEvent event) {
                                if (event.isControlDown()) {
                                    //System.out.println("Scroll plus Ctrl");
                                    try {
                                        BufferedImage newtap = ImageIO.read(new File("level_1.bmp"));
                                        tapestry.setImage(SwingFXUtils.toFXImage(newtap, null));
                                        //System.out.println("Set new tapestry");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });

                        rects.add(temp);
                    }
                }
                break;
            case LEVEL_TWO:
                break;
            case LEVEL_THREE:
                w = 0;
                h = 0;
                recth = 720 / (2*3/1);
                rectw = 1280 / (6*3/1);
                for(int i=0; i<2; i++){
                    temp = new Rectangle();
                    temp.setFill(new Color(0,0,1,0.3));
                    temp.setX(w);
                    temp.setY(h + 2*recth);
                    temp.setWidth(rectw);
                    temp.setHeight(recth);
                    w = w + rectw/2;
                    h = h + recth;
                    rects.add(temp);
                }
                break;

            default:
                break;

        }

        return rects;
    }

}

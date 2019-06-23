package com.skorobahatko.webcamcapture.controller;

import com.github.sarxos.webcam.Webcam;
import com.skorobahatko.webcamcapture.exception.AudioDeviceException;
import com.skorobahatko.webcamcapture.exception.NoCamerasFoundException;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainController {

    private VideoController videoController;
    private AudioController audioController;

    private boolean isRecord;

    private IMediaWriter writer;

    public MainController() throws NoCamerasFoundException, AudioDeviceException {
        videoController = new VideoController();
        audioController = new AudioController();
    }

    public List<Webcam> getCameras() {
        return videoController.getCameras();
    }

    public void chooseCamera(int id) {
        videoController.setCurrentCamera(id);
    }

    public Webcam getCurrentCamera() {
        return videoController.getCurrentCamera();
    }

    public void startRecord() {

        isRecord = true;
        String outputFileName = getOutputFileName();
        writer = ToolFactory.makeWriter(outputFileName + ".mp4");
        Dimension size = videoController.getImageSize();
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, size.width, size.height);

        // write video stream to the file
        new Thread(() -> {
            long start = System.currentTimeMillis();

            int i = 0;
            while (isRecord) {
                BufferedImage image = ConverterFactory.convertToType(videoController.getImage(),
                        BufferedImage.TYPE_3BYTE_BGR);
                IConverter converter = ConverterFactory.createConverter(image, IPixelFormat.Type.YUV420P);
                IVideoPicture frame = converter.toPicture(image, (System.currentTimeMillis() - start) * 1000);

                frame.setKeyFrame(i == 0);
                frame.setQuality(100);

                writer.encodeVideo(0, frame);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                i++;
            }

            writer.close();

        }).start();


        // write audio stream to the file
        try {
            audioController.open();

            AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;;
            File audioFile = new File(outputFileName + ".wav");

            Thread recorderThread = new Thread(() -> {
                try {
                    AudioSystem.write(new AudioInputStream(audioController.getLine()), fileType, audioFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            recorderThread.start();

        } catch (AudioDeviceException e) {
            e.printStackTrace();
        }

    }

    public void stopRecord() {
        isRecord = false;
        audioController.close();
    }

    public void close() {
        if (writer.isOpen()) {
            writer.close();
        }
        videoController.close();
    }

    private String getOutputFileName() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YY_hh.mm.ss");
        return localDateTime.format(formatter);
    }
}

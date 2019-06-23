package com.skorobahatko.webcamcapture.controller;

import com.github.sarxos.webcam.Webcam;
import com.skorobahatko.webcamcapture.exception.NoCamerasFoundException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class VideoController {

    private static final int DEFAULT_IMAGE_WIDTH = 640;
    private static final int DEFAULT_IMAGE_HEIGHT = 480;
    private static final int DEFAULT_CAMERA_ID = 0;

    private List<Webcam> cameras;
    private Webcam currentCamera;

    public VideoController() throws NoCamerasFoundException {
        cameras = Webcam.getWebcams();
        if (!cameras.isEmpty()) {
            setCurrentCamera(DEFAULT_CAMERA_ID);
        } else {
            throw new NoCamerasFoundException("No cameras found");
        }
    }

    public List<Webcam> getCameras() {
        return cameras;
    }

    public Webcam getCurrentCamera() {
        return currentCamera;
    }

    public BufferedImage getImage() {
        return currentCamera.getImage();
    }

    public Dimension getImageSize() {
        return currentCamera.getViewSize();
    }

    public void setCurrentCamera(int id) {
        if (currentCamera != null && currentCamera.isOpen()) currentCamera.close();

        currentCamera = cameras.get(id);

        // Get camera supported view sizes and set best one as default view size
        if (!currentCamera.isOpen()) {
            Dimension[] viewSizes = currentCamera.getViewSizes();
            Dimension resultViewSize = viewSizes[0];
            for (Dimension d : viewSizes) {
                if (d.getWidth() > resultViewSize.getWidth() && d.getHeight() > resultViewSize.getHeight()) {
                    resultViewSize = d;
                }
            }
            currentCamera.setViewSize(resultViewSize);
            open();
        }

    }

    public void open() {
        currentCamera.open();
    }

    public void close() {
        if (currentCamera.isOpen()) {
            currentCamera.close();
        }
    }

}

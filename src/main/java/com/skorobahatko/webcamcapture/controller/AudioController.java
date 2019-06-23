package com.skorobahatko.webcamcapture.controller;

import com.skorobahatko.webcamcapture.exception.AudioDeviceException;

import javax.sound.sampled.*;

public class AudioController {

    private TargetDataLine line;

    public AudioController() throws AudioDeviceException {
        try {
            AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            line = (TargetDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            throw new AudioDeviceException("Something wrong with audio devices: ", e);
        }
    }

    public TargetDataLine getLine() {
        return line;
    }

    public void open() throws AudioDeviceException {
        try {
            line.open();
            line.start();
        } catch (LineUnavailableException e) {
            throw new AudioDeviceException("Something wrong with audio device: ", e);
        }
    }

    public void close() {
        if (line.isOpen()) {
            line.stop();
            line.drain();
            line.close();
        }
    }

}

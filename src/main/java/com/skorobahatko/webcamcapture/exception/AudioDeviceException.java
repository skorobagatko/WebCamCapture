package com.skorobahatko.webcamcapture.exception;

public class AudioDeviceException extends Exception {

    public AudioDeviceException() {
    }

    public AudioDeviceException(String message) {
        super(message);
    }

    public AudioDeviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public AudioDeviceException(Throwable cause) {
        super(cause);
    }
}

package com.skorobahatko.webcamcapture.exception;

public class NoCamerasFoundException extends Exception {

    public NoCamerasFoundException() {
    }

    public NoCamerasFoundException(String message) {
        super(message);
    }

    public NoCamerasFoundException(Throwable cause) {
        super(cause);
    }
}

package com.enrique.stackblur;


import android.graphics.Bitmap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StackBlurManager {
    static final int EXECUTOR_THREADS = Runtime.getRuntime().availableProcessors();
    static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(EXECUTOR_THREADS);

    /**
     * Original image
     */
    private final Bitmap _image;
    /**
     * Method of blurring
     */
    private final BlurProcess _blurProcess;
    /**
     * Most recent result of blurring
     */
    private Bitmap _result;

    /**
     * Constructor method (basic initialization and construction of the pixel array)
     *
     * @param image The image that will be analyed
     */
    public StackBlurManager(Bitmap image) {
        _image = image;
        _blurProcess = new JavaBlurProcess();
    }

    /**
     * Process the image on the given radius. Radius must be at least 1
     */
    public Bitmap process(int radius) {
        _result = _blurProcess.blur(_image, radius);
        return _result;
    }

    /**
     * Returns the blurred image as a bitmap
     *
     * @return blurred image
     */
    public Bitmap returnBlurredImage() {
        return _result;
    }
    /* */
}

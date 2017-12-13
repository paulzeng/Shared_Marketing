package com.github.yoojia.qrcode.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

/**
 * @since 2.0
 */
public interface CaptureCallback {

    void onCaptured(Bitmap bitmap);
}

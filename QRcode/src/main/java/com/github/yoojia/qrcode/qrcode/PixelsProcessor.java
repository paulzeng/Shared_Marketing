package com.github.yoojia.qrcode.qrcode;

import android.graphics.Bitmap;

/**
 * @since 2.0
 */
public interface PixelsProcessor {

    Bitmap create(int[] pixels, int width, int height);
}

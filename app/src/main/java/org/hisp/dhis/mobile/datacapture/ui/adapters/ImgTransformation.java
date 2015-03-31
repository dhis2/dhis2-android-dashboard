package org.hisp.dhis.mobile.datacapture.ui.adapters;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class ImgTransformation implements Transformation {
        private static final String KEY = "imageResizeTransformation";

        @Override
        public Bitmap transform(Bitmap source) {
            float initialArea = source.getHeight() * source.getWidth();
            float compArea = calculateArea(initialArea, 10);
            Double rate = Math.sqrt(compArea / initialArea);

            int x = Math.round(source.getWidth() * rate.floatValue());
            int y = Math.round(source.getHeight() * rate.floatValue());

            Bitmap result = Bitmap.createScaledBitmap(source, x, y, false);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return KEY;
        }

        private float calculateArea(float initialArea, int resizeRate) {
            return (initialArea * resizeRate) / 100;
        }
    }
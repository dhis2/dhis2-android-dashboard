package org.hisp.dhis.android.dashboard.api.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.view.Gravity;

import com.squareup.picasso.Transformation;

import org.hisp.dhis.android.dashboard.api.models.DataMap;
import org.hisp.dhis.android.dashboard.api.utils.PicassoProvider;

import java.io.IOException;


public class BaseMapLayerDhisTransformation implements Transformation {

    private DataMap mDataMap;
    private Context mContext;

    public BaseMapLayerDhisTransformation(Context context, DataMap dataMap) {
        this.mContext = context;
        this.mDataMap = dataMap;
    }

/*    @Override public Bitmap transform(Bitmap source) {

        int width = source.getWidth();
        int height = source.getHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        ColorMatrix saturation = new ColorMatrix();
        saturation.setSaturation(0f);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(saturation));
        canvas.drawBitmap(source, 0, 0, paint);
        source.recycle();

        return bitmap;
    }*/

    @Override
    public Bitmap transform(Bitmap source) {

        try {
            Log.d(this.getClass().getSimpleName(), "Begin Transform image map");

            Log.d(this.getClass().getSimpleName(), "Resizing Dhis Image To Zoom7 ...");
            Bitmap resizedBitmap = ResizeDhisImageToZoom7(source);

            Log.d(this.getClass().getSimpleName(), "Cropping legend and title ...");
            Bitmap cropOriginal = cropLegendAndTitle(resizedBitmap);

            Bitmap staticMap = getStaticMap();

            Log.d(this.getClass().getSimpleName(), "Creating base map image");
            Bitmap transformedBitmap = createTransformedBitmap(cropOriginal, staticMap);

            Log.d(this.getClass().getSimpleName(), "End Transform image map");
            source.recycle();

            return transformedBitmap;
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "An error occurred transform image map: " + e
                    .getMessage());
            e.printStackTrace();
            return source;
        }
    }

    private Bitmap getStaticMap() throws IOException {
/*        String url = String.format(
                "http://staticmap.openstreetmap.de/staticmap.php?center=%s,%s&zoom=7&size=430x320",
                mDataMap.getLatitude(), mDataMap.getLongitude());*/

        String url = "http://staticmap.openstreetmap.de/staticmap.php?center=8.462084457245883,-11.784650000000008&zoom=7&size=430x320";

        Log.d(this.getClass().getSimpleName(), "Getting static map: " + url);

        return PicassoProvider.getInstance(mContext, false).load(url).get();
    }


    private Bitmap ResizeDhisImageToZoom7(Bitmap mapOriginal) {

        double percentageToResize = 0.76;

        return Bitmap.createScaledBitmap(
                mapOriginal,
                (int) (mapOriginal.getWidth() * percentageToResize),
                (int) (mapOriginal.getHeight() * percentageToResize), true);
    }

    private Bitmap cropLegendAndTitle(Bitmap resizedBitmap) {

        double percentageToCropWidth = 0.26;
        double percentageToCropHeight = 0.05;

        return Bitmap.createBitmap(resizedBitmap,
                (int) (resizedBitmap.getWidth() * percentageToCropWidth),
                (int) (resizedBitmap.getHeight() * percentageToCropHeight),
                resizedBitmap.getWidth() - (int) (resizedBitmap.getWidth() * percentageToCropWidth),
                resizedBitmap.getHeight() - (int) (resizedBitmap.getHeight()
                        * percentageToCropHeight));
    }

    private Bitmap createTransformedBitmap(Bitmap cropOriginal, Bitmap staticMap) {
        Drawable[] layers = new Drawable[2];

        BitmapDrawable baseMapDrawable = new BitmapDrawable(mContext.getResources(), staticMap);
        baseMapDrawable.setGravity(Gravity.CENTER);

        BitmapDrawable mapDrawable = new BitmapDrawable(mContext.getResources(), cropOriginal);
        mapDrawable.setGravity(Gravity.CENTER);

        layers[0] = baseMapDrawable;
        layers[1] = mapDrawable;
        LayerDrawable layerDrawable = new LayerDrawable(layers);


        Bitmap transformedBitmap = Bitmap.createBitmap(
                staticMap.getWidth(), staticMap.getHeight(), Bitmap.Config.ARGB_8888);
        layerDrawable.setBounds(0, 0, staticMap.getWidth(), staticMap.getHeight());
        layerDrawable.draw(new Canvas(transformedBitmap));

        return transformedBitmap;
    }

    @Override
    public String key() {
        return "BaseMapLayerDhisTransformation";
    }
}

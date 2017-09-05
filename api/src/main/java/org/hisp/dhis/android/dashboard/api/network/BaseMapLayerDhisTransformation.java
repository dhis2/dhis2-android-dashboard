package org.hisp.dhis.android.dashboard.api.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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

    @Override
    public Bitmap transform(Bitmap source) {
        //Bitmap mapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.maporiginal5);

        Bitmap resizedBitmap = ResizeDhisImageToZoom7(source);
        Bitmap cropOriginal = cropLegendAndTitle(resizedBitmap);
        Bitmap staticMap;

        try {
            staticMap = getStaticMap();
        } catch (IOException e) {
            return source;
        }

        Bitmap transformedBitmap = createTransformedBitmap(cropOriginal, staticMap);

        return transformedBitmap;
    }

    private Bitmap getStaticMap() throws IOException {
        String url = String.format(
                "http://staticmap.openstreetmap.de/staticmap.php?center=%s,%s&zoom=7&size=430x320",
                mDataMap.getLatitude(), mDataMap.getLongitude());

        return PicassoProvider.getInstance(mContext,false).load(url).get();
    }


    private Bitmap ResizeDhisImageToZoom7(Bitmap mapOriginal) {

        double percentageToResize = 0.76;

        return Bitmap.createScaledBitmap(
                mapOriginal,
                (int) (mapOriginal.getWidth() *  percentageToResize),
                (int) (mapOriginal.getHeight() * percentageToResize), true);
    }

    private Bitmap cropLegendAndTitle(Bitmap resizedBitmap) {

        double percentageToCropWidth = 0.26;
        double percentageToCropHeight = 0.05;

        return Bitmap.createBitmap(resizedBitmap,
                (int) (resizedBitmap.getWidth() * percentageToCropWidth),
                (int) (resizedBitmap.getHeight() * percentageToCropHeight),
                resizedBitmap.getWidth() - (int) (resizedBitmap.getWidth() * percentageToCropWidth),
                resizedBitmap.getHeight() - (int) (resizedBitmap.getHeight() * percentageToCropHeight));
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
                cropOriginal.getWidth(), cropOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        layerDrawable.setBounds(0, 0, cropOriginal.getWidth(), cropOriginal.getHeight());
        layerDrawable.draw(new Canvas(transformedBitmap));

        return transformedBitmap;
    }

    @Override
    public String key() {
        return "BaseMapLayerDhisTransformation(UID=" + mDataMap.getUId() +")";
    }
}

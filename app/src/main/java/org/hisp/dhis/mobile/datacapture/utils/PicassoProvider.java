package org.hisp.dhis.mobile.datacapture.utils;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.hisp.dhis.mobile.datacapture.api.managers.DHISManager;

import java.io.IOException;
import java.net.HttpURLConnection;

public final class PicassoProvider {
    private static Picasso mPicasso;

    private PicassoProvider() {
    }

    public static Picasso getInstance(Context context) {
        if (mPicasso == null) {
            mPicasso = new Picasso.Builder(context)
                    .downloader(new MyPicassoDownloader(context))
                    .build();
        }

        return mPicasso;
    }

    private static class MyPicassoDownloader extends OkHttpDownloader {

        public MyPicassoDownloader(Context context) {
            super(context);
        }

        @Override
        protected HttpURLConnection openConnection(Uri path) throws IOException {
            String credentials = DHISManager.getInstance().getCredentials();
            HttpURLConnection connection = super.openConnection(path);
            connection.setRequestProperty("Authorization", credentials);
            return connection;
        }
    }
}

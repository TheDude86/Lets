package com.main.lets.lets.LetsAPI;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.load.engine.Resource;
import com.main.lets.lets.R;

/**
 * Created by Joe on 12/27/2016.
 */
public class BitmapLoader {
    BitmapFactory.Options options;
    AppCompatActivity mActivity;
    String file;
    int ID;

    public BitmapLoader(AppCompatActivity a, String s) {

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(s, options);
        mActivity = a;
        file = s;

    }

    public BitmapLoader(AppCompatActivity a, int resID) {

        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(a.getResources(), resID, options);
        mActivity = a;
        ID = resID;

    }

    public Bitmap decodeSampledBitmapFromResource(int reqWidth, int reqHeight) {


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(mActivity.getResources(), ID, options);
    }

    public Bitmap decodeSampledBitmapFromFile(int reqWidth, int reqHeight) {


        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}

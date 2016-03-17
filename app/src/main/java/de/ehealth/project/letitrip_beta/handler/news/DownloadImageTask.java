package de.ehealth.project.letitrip_beta.handler.news;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Boolean mDownsize = false;

    public DownloadImageTask(ImageView bmImage, boolean downsize) {
        this.bmImage = bmImage;
        mDownsize = downsize;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if(mDownsize){
            try {
                result = Bitmap.createScaledBitmap(result, (int) (result.getWidth() * 0.5), (int) (result.getHeight() * 0.5), true);
            }catch(Exception ex){ ex.printStackTrace(); }
        }
        bmImage.setImageBitmap(result);
    }
}

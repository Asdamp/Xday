package com.asdamp.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;

import com.asdamp.x_day.DateListActivity;

import java.io.ByteArrayOutputStream;

public class ImageUtils {
    public static Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static void shareView(Context c,View v) {
        Bitmap b= ImageUtils.viewToBitmap(v);
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("image/*");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        intent.putExtra(Intent.EXTRA_STREAM, ImageUtils.getImageUri(c, b));
        try {
            c.startActivity(Intent.createChooser(intent, "My Profile ..."));
        } catch (android.content.ActivityNotFoundException ex) {

            ex.printStackTrace();
        }
    }
}

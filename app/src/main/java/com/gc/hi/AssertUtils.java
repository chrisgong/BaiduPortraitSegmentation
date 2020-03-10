package com.gc.hi;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssertUtils {
    public static byte[] readFileByBytes(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            in = assetManager.open(filePath);
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int len;
            while (-1 != (len = in.read(buffer, 0, bufSize))) {
                bos.write(buffer, 0, len);
            }

            byte[] var7 = bos.toByteArray();
            return var7;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException var14) {
                var14.printStackTrace();
            }

            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    //base64转为bitmap
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}

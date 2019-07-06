package com.funsoul.jwcmmvtc.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.funsoul.jwcmmvtc.R;
import com.funsoul.jwcmmvtc.jwcApploaction;

public class xkBG {
    private static Context context = jwcApploaction.getContext();

    public static int[] catarray = {R.drawable.cat0, R.drawable.cat1, R.drawable.cat2,
            R.drawable.cat3, R.drawable.cat4, R.drawable.cat5, R.drawable.cat6};

    public static int[] loginarray = {R.drawable.login0, R.drawable.login1, R.drawable.login2,
            R.drawable.login3, R.drawable.login4, R.drawable.login5, R.drawable.login6, R.drawable.login7};

    public static int[] bgarray = {R.drawable.bg0, R.drawable.bg1, R.drawable.bg2,
            R.drawable.bg3, R.drawable.bg4, R.drawable.bg5, R.drawable.bg6, R.drawable.bg7};

    public static Drawable getRandm(int[] array) {
        int random = (int) (Math.random() * 100 % 10);
        int i = random;
        while (random > array.length - 1) {
            if (random == i) {
                random = (int) (Math.random() * 100 % 10);
            }
        }
        int id = array[random];
        Drawable drawable = ResourcesCompat.getDrawable(context.getResources(),id,null);
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        return drawable;
    }
}

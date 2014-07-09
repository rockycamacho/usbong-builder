package com.squareup.picasso;

import com.squareup.picasso.Picasso;

/**
 * Created by Rocky Camacho on 7/2/2014.
 */
public class PicassoUtils {

    public static void clearCache (Picasso p) {
        p.cache.clear();
    }

}

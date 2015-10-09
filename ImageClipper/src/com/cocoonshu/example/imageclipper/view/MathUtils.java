package com.cocoonshu.example.imageclipper.view;

/**
 * Math tools
 * @author Cocoonshu@Plf.MediaCenter.Gallery
 * @date 2015-09-14 17:09:49
 */
public class MathUtils {

    public static final float PI_2   = (float) (Math.PI * 2.0);
    public static final float PI     = (float) Math.PI;
    public static final float PI_1_2 = (float) (Math.PI * 0.5);
    public static final float PI_1_4 = (float) (Math.PI * 0.25);

    /**
     * Clamp value between <tt>min</tt> and <tt>max</tt>
     * @param src
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float src, float min, float max) {
        return src < min ? min : src > max ? max : src;
    }

}

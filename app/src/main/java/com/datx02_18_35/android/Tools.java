package com.datx02_18_35.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.ImageView;



/**
 * Created by raxxor on 2018-03-15.
 */

class Tools {

    static final String debug = "test123";
    private static final String userData = "userDatas";


    //save userdata
    public static void writeUserData(byte[] array, Context context){
        String toSave =  Base64.encodeToString(array, Base64.DEFAULT);
        SharedPreferences preferences = context.getSharedPreferences(userData,Context.MODE_PRIVATE);
        preferences.edit().putString(userData,toSave).apply();
    }


    //get userdata
    public static byte[] getUserData(Context context){
        SharedPreferences preferences = context.getSharedPreferences(userData,Context.MODE_PRIVATE);

        String string = preferences.getString(userData,null);
        if(string != null){
            return Base64.decode(string, Base64.DEFAULT);
        }
        return null;

    }



    //screen
    static float getWidthOfDisplayInDp(){
        float px = Resources.getSystem().getDisplayMetrics().widthPixels;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }



    static class GameBoardScreenInfo {
        public int spanCounts=2;
        public float cardWidth;
        public float cardHeight;
        static final float paperRatio = (float) Math.sqrt(2);
        static final float minimumCardWidth = 120;
        static final float cardMargin=10;
        static final float openInventoryRequire=30;
        static final float gameLeftSideMargin=6;
        static final float gameRightSideMargin=6;
        static final float extraMargin=gameLeftSideMargin+gameRightSideMargin;
        static final float minTotalRequirement=2*minimumCardWidth+2*cardMargin+openInventoryRequire
                +gameLeftSideMargin+gameRightSideMargin+extraMargin;
        static final float cardSize = minimumCardWidth+cardMargin;


        GameBoardScreenInfo(){
            float widthInDP = Tools.getWidthOfDisplayInDp();
            // extremely small screen, probably don't exist and won't work with our app, however
            // use 1 expression and 1 rule column
            if(widthInDP < minTotalRequirement){
                float diffPerCard=(minTotalRequirement-widthInDP)/spanCounts;
                cardWidth=minimumCardWidth-diffPerCard;
            }
            else{
                float totalLeftOverSpace = widthInDP-minTotalRequirement;
                float extraSpanCounts = (float) Math.floor((double)(totalLeftOverSpace / cardSize));
                spanCounts+=extraSpanCounts;
                float totalLeftOverSpaceAfterNewSpanCounts = widthInDP-minTotalRequirement
                        +2*minimumCardWidth+2*cardMargin-spanCounts*(minimumCardWidth+cardMargin);
                float leftoverToAddToWidthForEachCard =totalLeftOverSpaceAfterNewSpanCounts/spanCounts;

                cardWidth=minimumCardWidth+leftoverToAddToWidthForEachCard;
            }
            cardHeight=paperRatio*cardWidth;
        }

    }

    static class SandboxScreenInfo{
        public int spanCounts=2;
        public float cardWidth;
        public float cardHeight;
        static final float paperRatio = (float) Math.sqrt(2);
        static final float minimumCardWidth = 120;
        static final float cardMargin=10;
        static final float gameLeftSideMargin=6;
        static final float gameRightSideMargin=6;
        static final float extraMargin=gameLeftSideMargin+gameRightSideMargin;
        static final float minTotalRequirement=2*minimumCardWidth+2*cardMargin +gameLeftSideMargin
                +gameRightSideMargin+extraMargin;
        static final float cardSize = minimumCardWidth+cardMargin;

        SandboxScreenInfo(){
            float widthInDP = Tools.getWidthOfDisplayInDp();
            // extremely small screen, probably don't exist and won't work with our app, however
            // use 1 expression and 1 operator column
            if(widthInDP < minTotalRequirement){
                float diffPerCard=(minTotalRequirement-widthInDP)/spanCounts;
                cardWidth=minimumCardWidth-diffPerCard;
            }
            else{
                float totalLeftOverSpace = widthInDP-minTotalRequirement;
                float extraSpanCounts = (float) Math.floor((double)(totalLeftOverSpace / cardSize));
                spanCounts+=extraSpanCounts;
                float totalLeftOverSpaceAfterNewSpanCounts = widthInDP-minTotalRequirement
                        +2*minimumCardWidth+2*cardMargin-spanCounts*(minimumCardWidth+cardMargin);
                float leftoverToAddToWidthForEachCard =totalLeftOverSpaceAfterNewSpanCounts/spanCounts;
                cardWidth=minimumCardWidth+leftoverToAddToWidthForEachCard;
            }
            cardHeight=paperRatio*cardWidth;
        }


    }




    public static void setImage(ImageView image, int rId){
        int imageHeight = 100;
        int imageWidth = 100;
        image.setImageBitmap(decodeSampledBitmapFromResource(image.getResources(), rId, imageWidth, imageHeight));

    }


    //imported code from the official Android documentation
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }


    //imported code from the official Android documentation
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


    //Convertions
    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
    public static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }
}

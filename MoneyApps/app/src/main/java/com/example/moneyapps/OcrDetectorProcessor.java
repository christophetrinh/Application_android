package com.example.moneyapps;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseArray;

import com.example.moneyapps.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;


public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
    private static final String TAG = "OcrDetectorProcessor";

    private final Activity mActivity;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay, Activity activity) {
        mGraphicOverlay = ocrGraphicOverlay;
        mActivity = activity;
    }

    /**
     * Called by the detector to deliver detection results.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        String raw;
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);

            // Retrieve the amount
            if (item != null && item.getValue() != null) {
                raw = item.getValue();
                String[] token = raw.split(" ");
                String value = new String();

                if (raw.contains("EUR")) {
                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                    mGraphicOverlay.add(graphic);
                    for (int j = 0; j < token.length; j++) {
                        if (isNumeric(token[j])){
                            value = token[j];
                            final String amount = value;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TakePicture) mActivity).updateData(amount);
                                }
                            });
                        }

                    }
                    if (!value.isEmpty()) Log.d(TAG, "Europe: " + value);
                }
            }


            String date_value;
            //Retrieve the date in the format dd/mm/yy
            if (item != null && item.getValue().contains("/")) {
                raw = item.getValue();
                String[] token = raw.split(" ");
                for (int j = 0; j < token.length; j++) {
                    if (isDate(token[j])){
                        OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                        mGraphicOverlay.add(graphic);
                        Log.d(TAG, "DATE: " + token[j]);
                        date_value = token[j];
                        final String date = date_value;
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((TakePicture) mActivity).updateDate(date);
                            }
                        });

                    }
                }
            }
        }


    }

    public static boolean isDate(String str) {
        return str.matches("([0-9]{2})/([0-9]{2})/([0-9]{2})");
    }
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        Log.v("CLEAN","clean the screen");
        mGraphicOverlay.clear();

    }
}

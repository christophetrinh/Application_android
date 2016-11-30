package com.example.moneyapps;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseArray;

import com.example.moneyapps.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

/**
 * Created by trinh on 22/11/16.
 */


public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        String raw = new String();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            //ALL TEXT
/*
            if (item != null && item.getValue() != null) {
                Log.d("OcrDetectorProcessor", "RAW TEXT: " + item.getValue());
            }
*/

            //PRICE
            if (item != null && item.getValue() != null) {
                raw = item.getValue();
                String[] token = raw.split(" ");
                String value = new String();

                if (raw.contains("CHF")) {
                    for (int j = 0; j < token.length; j++) {
                        if (isNumeric(token[j]))
                            value = token[j];
                    }
                    Log.d("Processor", "Suisse: " + value);
                }
                else if (raw.contains("EUR")) {
                    for (int j = 0; j < token.length; j++) {
                        if (isNumeric(token[j]))
                            value = token[j];
                    }
                    Log.d("Processor", "Europe: " + value);
                }
            }


            //DATE & TIME
            if (item != null && item.getValue().contains("/")) {
                raw = item.getValue();
                String[] token = raw.split(" ");
                for (int j = 0; j < token.length; j++) {
                    if (isDate(token[j]))
                        Log.d("OcrDetectorProcessor", "DATE: " + token[j]);
                    if (isTime(token[j]))
                        Log.d("OcrDetectorProcessor", "TIME: " + token[j]);
                }
                //Log.d("OcrDetectorProcessor", "DATE: " + item.getValue());
            }
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }
    }
    public static boolean isTime(String str) {
        return str.matches("([0-9]{2}):([0-9]{2})");
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
        mGraphicOverlay.clear();
    }
}

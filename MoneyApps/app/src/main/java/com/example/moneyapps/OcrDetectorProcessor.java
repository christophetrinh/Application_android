package com.example.moneyapps;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.example.moneyapps.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;


public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
    private static final String TAG = "OcrDetectorProcessor";

    boolean date_detected = false;
    boolean amount_detected= false;
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

            String date_value;
            //Retrieve the date in the format dd/mm/yy
            if (item != null && item.getValue().contains("/") && !date_detected) {
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
                                date_detected = true;
                                Toast.makeText(mActivity,"Date detected",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }

            // Retrieve the amount
            if (item != null && item.getValue() != null && !amount_detected) {
                raw = item.getValue();
                String[] token = raw.split(" ");
                String value;

                if (raw.contains("EUR")) {
                    OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                    mGraphicOverlay.add(graphic);
                    for (int j = 0; j < token.length; j++) {
                        if (token[j].equals("EUR")){
                            value = token[j-1];
                            final String amount = value;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((TakePicture) mActivity).updateData(amount);
                                    Toast.makeText(mActivity,"Amount detected",Toast.LENGTH_SHORT).show();
                                    amount_detected = true;
                                }
                            });
                        }
                    }
                }
            }

        }

    }

    public static boolean isDate(String str) {
        return str.matches("([0-9]{2})/([0-9]{2})/([0-9]{2})");
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

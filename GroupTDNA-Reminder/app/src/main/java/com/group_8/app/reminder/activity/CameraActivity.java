package com.group_8.app.reminder.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.group_8.app.reminder.ProcessImage;
import com.group_8.app.reminder.R;
import com.group_8.app.reminder.utils.CharDetectOCR;
import com.group_8.app.reminder.utils.CommonUtils;
import com.group_8.app.reminder.view.CameraPreview;
import com.group_8.app.reminder.view.CameraSurfaceView;



import static com.group_8.app.reminder.model.ConstKey.GENERATE_TEXT_FROM_CAMERA;
import static com.group_8.app.reminder.utils.CommonUtils.info;
public class CameraActivity extends Activity {

    public CameraPreview camPreview;
    CameraSurfaceView cameraSurfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

    ImageButton buttonTakePicture;
    int previewSizeWidth = 0;
    int previewSizeHeight = 0;


    static ProcessImage processImg = new ProcessImage();

    Button btnStartCamera;
    Button btnExit;

    private String language;

    private EditText recognizeResult;

    private int sourceW = 0;
    private int sourceH = 0;
    private String lastFileName = "";
    private boolean isRecognized = false;


    ProgressDialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_camera2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Bundle b = getIntent().getExtras();
//        final String outputUri = b.getString("output");

        lastFileName = CommonUtils.APP_PATH + "capture" + System.currentTimeMillis() + ".jpg";
        info(lastFileName);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        cameraSurfaceView = (CameraSurfaceView) findViewById(R.id.camerapreview);
        camPreview = new CameraPreview(this, cameraSurfaceView);
        language = "English";
        ProcessImage.language = language;
        ProcessImage.thresholdMin =  150;
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            new InitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new InitTask().execute();
        }


        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        ViewGroup.LayoutParams layoutParamsControl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        buttonTakePicture = (ImageButton) findViewById(R.id.takepicture);
        buttonTakePicture.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                camPreview.takePicture(lastFileName);
            }
        });

    }

    public void callProcessImage(String output, int top, int bot, int right, int left) {
        // showProgressBar("In processing...", "Please wait a second.");

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap imageBitmap = BitmapFactory.decodeFile(lastFileName, options);

        if (imageBitmap == null) {
            isRecognized = false;
            hideProcessBar();
            return;
        }
        final Bitmap finalImageBitmap = imageBitmap.getWidth() > imageBitmap.getHeight()
                ? rotateBitmap(imageBitmap, 90) : imageBitmap;


        displayResult(finalImageBitmap, top, bot, right, left);

        String resultText = processImg.recognizeResult;
        Intent returnIntent = new Intent();
        returnIntent.putExtra(GENERATE_TEXT_FROM_CAMERA,resultText);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }

    public void displayResult(Bitmap imageBitmap, int top, int bot, int right, int left) {
        info("Origin size: " + imageBitmap.getWidth() + ":" + imageBitmap.getHeight());
        // Parser
        //recognizeResult.setText("");
        if (processImg.parseBitmap(imageBitmap, top, bot, right, left)) {
            // TODO: set result

            // TODO: write result to image


            isRecognized = true;
            hideProcessBar();

        } else {
            // Try again
            isRecognized = false;
            hideProcessBar();

        }
    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void hideProcessBar() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
    }

    private class InitTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... data) {
            try {
                CharDetectOCR.init(getAssets());
                return "";
            } catch (Exception e) {
                Log.e("COMPA", "Error init data OCR. Message: " + e.getMessage());
            }
            return "";
        }

    }
}
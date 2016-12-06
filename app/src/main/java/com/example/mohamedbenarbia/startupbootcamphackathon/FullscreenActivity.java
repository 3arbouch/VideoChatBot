package com.example.mohamedbenarbia.startupbootcamphackathon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import android.support.design.widget.Snackbar;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import   com.google.android.gms.* ;

import java.io.IOException;

import javax.xml.datatype.Duration;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private VideoView myVideoView;
    String questionId  = "q1qs1" ;


    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private  TextView text ;

    private CameraSource mCameraSource = null;


    String buttonClicked = "" ;
    Button lastClickedButton  = null ;

    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        myVideoView = (VideoView) findViewById(R.id.video_view);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +R.raw.q1qs1));
        final Button yesB  = (Button) findViewById(R.id.Yes);
        final Button noB  = (Button) findViewById(R.id.No);
        final Button ContinueB = (Button) findViewById(R.id.Continue);
        text = (TextView) findViewById(R.id.question) ;

        yesB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked = "Yes" ;
                Toast.makeText(v.getContext(),"You selected Yes", Toast.LENGTH_LONG) ;
                v.setBackgroundColor(getResources().getColor(R.color.DeepSkyBlue));
                if(lastClickedButton!=null) lastClickedButton.setBackgroundColor(getResources().getColor(R.color.WhiteSmoke));
                lastClickedButton = (Button) v ;


            }
        });

        noB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClicked = "No" ;
                Toast.makeText(v.getContext(),"You selected No ", Toast.LENGTH_LONG) ;
                v.setBackgroundColor(getResources().getColor(R.color.DeepSkyBlue));
                if(lastClickedButton!=null) lastClickedButton.setBackgroundColor(getResources().getColor(R.color.WhiteSmoke));
                lastClickedButton = (Button) v ;


            }

        });

        hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        myVideoView.start();
        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer vmp) {
                if(questionId.length()== 7) {
                    Log.d("UPLOAD NEW VIDEO", "HEEREE") ;
                    int questionNumber = Character.getNumericValue(questionId.charAt(1))  + 1;
                    if(questionNumber<=4) {
                        String aux = Integer.toString(questionNumber);
                        questionId = "q" + aux + "qs1";
                        myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + getResources().getIdentifier(questionId, "raw", getPackageName())));
                    }
                    else {
                        myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + getResources().getIdentifier("signoff", "raw", getPackageName())));
                    }
                    myVideoView.start();

                }
                else {

                    yesB.setVisibility(View.VISIBLE);
                    noB.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                    ContinueB.setVisibility(View.VISIBLE);
                    ContinueB.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (buttonClicked.equals(""))
                                Toast.makeText(v.getContext(), "Please choose an option", Toast.LENGTH_LONG);
                            else {

                                yesB.setVisibility(View.GONE);
                                noB.setVisibility(View.GONE);
                                //text.setVisibility(View.GONE);
                                ContinueB.setVisibility(View.GONE);
                                myVideoView.stopPlayback();
                                if (buttonClicked.equals("Yes")) {
                                    questionId = questionId + "1";
                                    myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + getResources().getIdentifier(questionId, "raw", getPackageName())));
                                } else if (buttonClicked.equals("No")) {
                                    questionId = questionId + "0";
                                    myVideoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + getResources().getIdentifier(questionId, "raw", getPackageName())));
                                }

                                myVideoView.start();
                            }
                        }
                    });
                }
            }
        });

        // Set up the user interaction to manually show or hide the system UI.

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        startCameraSource();

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        //startCameraSource();
    }


    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                //mPreview.start(mCameraSource, mGraphicOverlay);
                //mPreview.start(mCameraSource);
                SurfaceView surface = new SurfaceView(this);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mCameraSource.start(surface.getHolder());
            } catch (IOException e) {
                Log.e("", "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }


    private void requestCameraPermission() {
        Log.w("", "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make((View)text.getParent(), R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());


        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.

            Log.d("Detector", "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        Log.d("Create Source Camera", "Camera source called ") ;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void updateText(float smiling){
        Log.d("FACE", "Smiling" + smiling);
    }




    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new FaceTracker();
        }


    }


    private class FaceTracker extends Tracker<Face>{



        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, final Face face) {
            Log.d("FACE", "Face detected") ;
            updateText(face.getIsSmilingProbability());
/*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.setText("Happy" + String.format("%.2f", face.getIsSmilingProbability()));
                }
            });
*/
        }





    }
}

package com.example.videocalling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// Java
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_ID = 22;

    // Fill the App ID of your project generated on Agora Console.
    private String appId = "50217f78f8414a47a959a3b610206dc9";
    // Fill the channel name.
    private String channelName = "calling";
    // Fill the temp token generated on Agora Console.
    private String token = "0061cc7b2ce26154a8fae5869e110b52cb4IACP0glHO47ZNBsvDjKcF3Lftd2c2Zz5RHxgRsRU9mGz9HPFBqYAAAAAEABCwUE+na+HYgEAAQCcr4di";
    private RtcEngine mRtcEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID) &&
                checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID)) {
            initializeAndJoinChannel();
        }
    }


    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel to get the uid of the user.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Call setupRemoteVideo to set the remote video view after getting uid from the onUserJoined callback.
                    setupRemoteVideo(uid);
                    Toast.makeText(MainActivity.this, "start remote viode", Toast.LENGTH_SHORT).show();
                }
            });
        }


    };

    private void initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), appId, mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }

        // By default, video is disabled, and you need to call enableVideo to start a video stream.
        mRtcEngine.enableVideo();

        FrameLayout container = findViewById(R.id.local_video_view_container);
        // Call CreateRendererView to create a SurfaceView object and add it as a child to the FrameLayout.
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        // Pass the SurfaceView object to Agora so that it renders the local video.
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));

        // Join the channel with a token.
        mRtcEngine.joinChannel(token, channelName, "", 0);
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        Toast.makeText(MainActivity.this, "start remote setupRemoteVideo", Toast.LENGTH_SHORT).show();
    }

    protected void onDestroy() {
        super.onDestroy();

        mRtcEngine.leaveChannel();
        mRtcEngine.destroy();
    }
}
/*
 * Copyright (C) 2015 Amha Mogus amha.mogus@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package amhamogus.com.spotifystreamer;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;

import java.util.ArrayList;

import amhamogus.com.spotifystreamer.PlaybackService;
import amhamogus.com.spotifystreamer.R;
import amhamogus.com.spotifystreamer.fragments.PlaybackFragment;
import amhamogus.com.spotifystreamer.model.MyTrack;

public class PlaybackActivity extends Activity implements PlaybackFragment.PlaybackEventListener {

    private PlaybackService mService;
    private Boolean isBound = false;
    private PlaybackFragment fragment;
    ArrayList<MyTrack> trackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_playback);

        Intent intent = getIntent();
        String trackID = intent.getStringExtra("trackID");
        trackList = intent.getParcelableArrayListExtra("trackList");
        int trackNumber = intent.getIntExtra("playListNumber", 0);

        if (savedInstanceState != null) {
            fragment = (PlaybackFragment) getFragmentManager().findFragmentByTag("playback_fragment");
        } else {
            fragment = PlaybackFragment.newInstance(trackID, trackList, trackNumber);
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.playback_fragment_holder, fragment, "playback_fragment");
        transaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlaybackService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
    }

    public void passTrackPreview(String previewURL) {
        Bundle args = new Bundle();
        args.putString("PREVIEW", previewURL);

        Intent intent = new Intent();
        intent.putExtras(args);

        mService.onHandleIntent(intent);
    }

    public void pauseTrack() {
        if (mService.isMediaPlaying()) {
            mService.pause();
        }
    }

    public void stopTrack() {
        if (mService.isMediaPlaying()) {
            mService.stop();
        }
    }

    public void seekTo(int seekPosition) {
        mService.setSeekTime(seekPosition);
    }

    @Override
    public boolean isMediaplayerNull(){
        if(mService.isMediaplayerNull()){
            return true;
        }
        else
        {
            return false;
        }
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlaybackService.MyBinder binder = (PlaybackService.MyBinder) service;
            mService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBound = false;
        }
    };
}

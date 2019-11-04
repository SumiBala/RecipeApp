package com.example.recipeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.media.session.MediaButtonReceiver;

import com.example.recipeapp.database.Steps;
import com.example.recipeapp.utils.API_Confiq;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VideoFragment extends Fragment implements ExoPlayer.EventListener {

    @BindView(R.id.nextVw)
    ImageButton nextStep;
    @BindView(R.id.prevVw)
    ImageButton prevStep;
    @BindView(R.id.stepsDescription)
    TextView stepDescription;
    @BindView(R.id.emptytxt)
    TextView emptyTxt;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private AudioManager mAudioManager;
    private Context mContext;
    private static final String STEPS = "steps";
    private static final String INDEX = "index";
    private static final String IMAGE = "image";
    private static final String TAG = VideoActivity.class.getSimpleName();
    private int mIndex;
    private String mImageSrc;
    private List<Steps> mSteps;
    private int mTotalSteps;
    private API_Confiq config;
    private String tUrl;
    private long mPlayerPos;
    private boolean mPlayerState;
    private int firstStep = 0;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String img, ArrayList<Steps> steps, int index) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE, img);
        args.putParcelableArrayList(STEPS, steps);
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mSteps = getArguments().getParcelableArrayList(STEPS);
            mIndex = getArguments().getInt(INDEX);
            mImageSrc = getArguments().getString(IMAGE);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        ButterKnife.bind(this, view);
        mContext = getContext();
        config = new API_Confiq(mContext);
        stepDescription.setMovementMethod(new ScrollingMovementMethod());
        if (mContext != null) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        initializeMediaSession();
        if (mSteps != null) {
            mTotalSteps = mSteps.size();
        }
        if (savedInstanceState != null) {
            mIndex = savedInstanceState.getInt("curIndex");
            mPlayerPos = savedInstanceState.getLong("playerPos");
            mPlayerState = savedInstanceState.getBoolean("playerState");
        }
        visibilityLoadData(mIndex);
        return view;
    }

    private void visibilityLoadData(int mIndex) {
        if (mIndex == firstStep) {
            prevStep.setVisibility(View.INVISIBLE);
            nextStep.setVisibility(View.VISIBLE);
        } else if (mIndex == mTotalSteps - 1) {
            prevStep.setVisibility(View.VISIBLE);
            nextStep.setVisibility(View.INVISIBLE);
        }
        loadData(mIndex);
    }

    private void loadData(int index) {
        releasePlayer();
        if (mSteps != null) {
            stepDescription.setText(mSteps.get(index).getDescription());
            tUrl = mSteps.get(index).getVideoUrl();
            String imgUrl = mSteps.get(index).getImageUrl();
            if (!tUrl.isEmpty()) {
                //Video is not empty,load VideoUrl
                loadViews(tUrl);
            } else if (!imgUrl.isEmpty()) {
                tUrl = imgUrl;
                //If Video is empty and thumbnailurl is not empty
                loadViews(tUrl);
            } else if (!mImageSrc.isEmpty()) {
                //If Video is empty,Thumbnail is empty, Load image.
                Picasso.with(mContext)
                        .load(mImageSrc)
                        .error(R.drawable.placholder)
                        .placeholder(R.drawable.placholder)
                        .into(imageView);
            } else {
                //If Video is empty,Thumbnail is empty, image also empty,place default image.
                Picasso.with(mContext)
                        .load(R.drawable.recipe)
                        .error(R.drawable.placholder)
                        .placeholder(R.drawable.placholder)
                        .into(imageView);
                showError(getString(R.string.no_video));
            }
        }
    }

    private void loadViews(String tUrl) {
        emptyTxt.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.GONE);
        if (config.netIsConnected()) {
            mPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(tUrl));
        } else {
            showError(getString(R.string.no_internet_connection));
        }
    }

    private void showError(String msg) {
        imageView.setVisibility(View.VISIBLE);
        mPlayerView.setVisibility(View.GONE);
        emptyTxt.setVisibility(View.VISIBLE);
        emptyTxt.setText(msg);
    }

    private void initializePlayer(Uri mediaUri) {
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (mExoPlayer == null) {
                // Create an instance of the ExoPlayer.
                TrackSelector trackSelector = new DefaultTrackSelector();
                LoadControl loadControl = new DefaultLoadControl();
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
                mPlayerView.setPlayer(mExoPlayer);
                mExoPlayer.addListener(this);
                // Prepare the MediaSource.
                String userAgent = Util.getUserAgent(mContext, getString(R.string.app_name));
                MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                        mContext, userAgent), new DefaultExtractorsFactory(), null, null);
                LoopingMediaSource loopingMediaSource = new LoopingMediaSource(mediaSource);
                mExoPlayer.prepare(loopingMediaSource);
                mExoPlayer.setPlayWhenReady(true);
            }
        }
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.
                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mExoPlayer.setPlayWhenReady(false);
                mExoPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mExoPlayer.setPlayWhenReady(true);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releasePlayer();
            }
        }
    };

    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(mContext, "MEDIA");
        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);
        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);
        mMediaSession.setPlaybackState(mStateBuilder.build());
        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());
        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle currentState) {
        currentState.putInt("curIndex", mIndex);
        currentState.putLong("playerPos", mPlayerPos);
        currentState.putBoolean("playerState", mPlayerState);
        super.onSaveInstanceState(currentState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (tUrl != null) {
            initializePlayer(Uri.parse(tUrl));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer != null) {
            //Resume Video properly.
            mExoPlayer.setPlayWhenReady(mPlayerState);
            mExoPlayer.seekTo(mPlayerPos);
        } else {
            if (tUrl != null) {
                initializePlayer(Uri.parse(tUrl));
                mExoPlayer.setPlayWhenReady(mPlayerState);
                mExoPlayer.seekTo(mPlayerPos);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            mPlayerState = mExoPlayer.getPlayWhenReady();
            mPlayerPos = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    @OnClick(R.id.nextVw)
    void setNextStep() {
        prevStep.setVisibility(View.VISIBLE);
        mIndex = mIndex + 1;
        if (mIndex < mTotalSteps) {
            loadData(mIndex);
        }
        if (mIndex == mTotalSteps - 1) {
            nextStep.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.prevVw)
    void setPrevStep() {
        nextStep.setVisibility(View.VISIBLE);
        mIndex = mIndex - 1;
        if (mIndex >= firstStep) {
            loadData(mIndex);
            if (mIndex == firstStep) {
                prevStep.setVisibility(View.INVISIBLE);
            }
        }
    }
}

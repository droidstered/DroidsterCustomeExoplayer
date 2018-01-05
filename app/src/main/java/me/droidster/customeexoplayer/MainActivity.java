package me.droidster.customeexoplayer;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.AdaptiveMediaSourceEventListener;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import me.droidster.customeexoplayer.VideoUtil.CustomeTrackSelectionHelper;

public class MainActivity extends AppCompatActivity implements ExoPlayer.EventListener, MediaController.MediaPlayerControl {

    //Exoplayer
    private SimpleExoPlayerView simpleExoPlayerView;
    SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;

    Handler mainHandler = new Handler();
    private Timer timer;

    private CustomeTrackSelectionHelper customeTrackSelectionHelper;
    private DataSource.Factory dataSourceFactory;
    private DefaultBandwidthMeter defaultBandwidthMeter;

    //Progressbar
    ProgressBar progress_loading;

    //Control
    private ImageView img_play, img_pause, img_fullscreen, img_fullscreen_exit, img_preview;
    private TextView txt_quality, txt_time;

    //Seekbar
    SeekBar sekkbar;

    private RelativeLayout rel_player;


    //Check Orientation
    int orientation;

    long pauseSeekTo = 0;
    private boolean isresume = false;

    //video_Url and video_type
    String video_url = "http://www.streambox.fr/playlists/x36xhzz/x36xhzz.m3u8";
    String video_type = "hls"; //either hls or mp4


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orientation = MainActivity.this.getResources().getConfiguration().orientation;

        init();
        setListener();


        //For play video and pass video_type hls (.m3u8) or mp4
        loadPlayer(video_url,video_type);

    }


    public void init() {
        progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
        progress_loading.setVisibility(View.VISIBLE);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.requestFocus();

        //For Controll and preview image
        img_preview = (ImageView) findViewById(R.id.img_preview);
        img_play = (ImageView) findViewById(R.id.img_play);
        img_pause = (ImageView) findViewById(R.id.img_pause);
        img_fullscreen = (ImageView) findViewById(R.id.img_fullscreen);
        img_fullscreen_exit = (ImageView) findViewById(R.id.img_fullscreen_exit);

        txt_quality = (TextView) findViewById(R.id.txt_quality);
        txt_time = (TextView) findViewById(R.id.txt_time);

        rel_player = (RelativeLayout) findViewById(R.id.rel_player);

        progress_loading = (ProgressBar) findViewById(R.id.progress_loading);

        sekkbar = (SeekBar) findViewById(R.id.sekkbar);


        initExoplayer();
    }

    public void setListener() {

        txt_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Demo class track selector
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    customeTrackSelectionHelper.showSelectionDialog(MainActivity.this, ((TextView) v).getText(),
                            trackSelector.getCurrentMappedTrackInfo(), 0);
                }
            }
        });

        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                start();

                img_pause.setVisibility(View.VISIBLE);
                img_play.setVisibility(View.GONE);

            }
        });

        img_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                pause();

                img_play.setVisibility(View.VISIBLE);
                img_pause.setVisibility(View.GONE);


            }
        });

        img_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //player.set
                orientation = MainActivity.this.getResources().getConfiguration().orientation;

                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //code for portrait mode
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

                    DisplayMetrics dimension = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dimension);
//                    int w = dimension.widthPixels - getStatusBarHeight();
                    int w = dimension.widthPixels;
                    int h = dimension.heightPixels;

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, w);
                    params.setMargins(0, 0, 0, 0);
                    rel_player.setLayoutParams(params);

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


                } else {
                    //code for landscape mode
                }

                img_fullscreen_exit.setVisibility(View.VISIBLE);
                img_fullscreen.setVisibility(View.GONE);
            }
        });


        img_fullscreen_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //player.set

                orientation = MainActivity.this.getResources().getConfiguration().orientation;

                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //code for portrait mode
                } else {
                    //code for landscape mode
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(300, MainActivity.this));
                    rel_player.setLayoutParams(params);

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                }

                img_fullscreen_exit.setVisibility(View.GONE);
                img_fullscreen.setVisibility(View.VISIBLE);

            }
        });

        sekkbar.setMax(100);
        sekkbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateTime((long) (progress / 100.0f * player.getDuration()));

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                timer.cancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo((int) (player.getDuration() / 100.0f * seekBar.getProgress()));
            }
        });

    }


    void updateTime(final long ms) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                txt_time.setText(
                        String.format("%s / %s"
                                , ms2Time(ms)
                                , ms2Time(player.getDuration())
                        ));

            }
        });
    }

    private String ms2Time(long ms) {

        StringBuffer buf = new StringBuffer();
        int hours = (int) (ms / (1000 * 60 * 60));
        int minutes = (int) ((ms % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((ms % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        if (hours > 0) {
            buf
                    .append(String.format("%02d", hours))
                    .append(":")
                    .append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds));
        } else {
            buf
                    .append(String.format("%02d", minutes))
                    .append(":")
                    .append(String.format("%02d", seconds));
        }

        return buf.toString();

    }


    private void initExoplayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        customeTrackSelectionHelper = new CustomeTrackSelectionHelper(trackSelector, videoTrackSelectionFactory);
        player =
                ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        defaultBandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Droidster CutomeExoplayer"), defaultBandwidthMeter);

    }

    private void loadPlayer(String video_url,String type) {

        MediaSource hlsMediaSource;

        if(type.equalsIgnoreCase("mp4")) {

            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            hlsMediaSource = new ExtractorMediaSource(Uri.parse(video_url),
                    dataSourceFactory, extractorsFactory, null, null);

        }else
        {

             hlsMediaSource = new HlsMediaSource(Uri.parse(video_url), dataSourceFactory, mainHandler, new AdaptiveMediaSourceEventListener() {
                @Override
                public void onLoadStarted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs) {

                }

                @Override
                public void onLoadCompleted(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {
                    long bit = defaultBandwidthMeter.getBitrateEstimate();
                }

                @Override
                public void onLoadCanceled(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded) {

                }

                @Override
                public void onLoadError(DataSpec dataSpec, int dataType, int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaStartTimeMs, long mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs, long bytesLoaded, IOException error, boolean wasCanceled) {

                }

                @Override
                public void onUpstreamDiscarded(int trackType, long mediaStartTimeMs, long mediaEndTimeMs) {

                }

                @Override
                public void onDownstreamFormatChanged(int trackType, Format trackFormat, int trackSelectionReason, Object trackSelectionData, long mediaTimeMs) {

                }
            });
        }

        player.addListener(this);
        boolean haveResumePosition = false;
        player.prepare(hlsMediaSource, !haveResumePosition, false);
        simpleExoPlayerView.setPlayer(player);
        player.setPlayWhenReady(true);

        if (isresume) {
            try {

                player.seekTo(pauseSeekTo);

            } catch (Exception e) {
                e.printStackTrace();
            }
            isresume = false;
        }
    }

    public void setqualitytext(String quality_text) {
        txt_quality.setText(quality_text);
    }

    void updateSeekBar() {
        sekkbar.setProgress((int) (player.getCurrentPosition() * 1.0f / player.getDuration() * 100));
        sekkbar.setSecondaryProgress(player.getBufferedPercentage());
    }


    //Event Listner
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {


        long bufferedPosition = player == null ? 0 : player.getBufferedPosition();

        img_preview.setVisibility(View.GONE);

        sekkbar.setVisibility(View.GONE);


    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {


        if (playbackState == ExoPlayer.STATE_READY) {

            Log.v("trackcheckstate", " check" + playbackState + "  Ready " + playWhenReady + "  duration " + getDuration());

            progress_loading.setVisibility(View.GONE);

            sekkbar.setVisibility(View.VISIBLE);

            timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    updateTime(player.getCurrentPosition());
                    updateSeekBar();
                }
            }, 0, 1000);


        }

        if (playbackState == ExoPlayer.STATE_ENDED) {


            pause();
            img_play.setVisibility(View.VISIBLE);
            img_pause.setVisibility(View.GONE);
            player.seekTo(0);
        }

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    //Media control
    @Override
    public void start() {
        player.setPlayWhenReady(true);

    }

    @Override
    public void pause() {
        player.setPlayWhenReady(false);
    }

    @Override
    public int getDuration() {
        return player == null ? 0 : (int) player.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return player == null ? 0 : (int) player.getCurrentPosition();

    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;

    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    @Override
    protected void onStop() {
        super.onStop();

        if (player != null) {
            player.release();
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onPause();
        isresume = true;
        pauseSeekTo = player.getCurrentPosition();
        player.stop();
        player.release();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (isresume) {
            // isresume=false;
            player.stop();
            player.clearVideoSurface();
            // isInPlayer = false;
            initExoplayer();
            loadPlayer(video_url,video_type);
        } else {
            loadPlayer(video_url,video_type);
        }
    }

}

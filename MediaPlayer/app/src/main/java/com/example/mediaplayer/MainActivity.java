package com.example.mediaplayer;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {
    private ListView lv_list;
    private ImageView iv_shuffle, iv_prev, iv_play, iv_next, iv_repeat;
    private TextView tv_song, tv_artist, tv_album, tv_order, tv_duration;
    private SeekBar sb_player;

    private static final int LEVEL_PLAY = 0;
    private static final int LEVEL_PAUSE = 1;

    private int i_progress;

    private static final int LEVEL_ON = 1;
    private static final int LEVEL_OFF = 0;

    private ArrayList<Song> al_song;
    private MediaManager mm_manager;
    private SongAdapter sa_song;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
    }
    

    private void initData() {
        mm_manager = new MediaManager(this);
        al_song = mm_manager.getAl_song();
        sa_song = new SongAdapter(this, al_song);
    }

    private void initView() {
        lv_list = (ListView) findViewById(R.id.lv_list);
        lv_list.setOnItemClickListener(this);
        lv_list.setAdapter(sa_song);

        tv_song = (TextView) findViewById(R.id.tv_song);
        tv_artist = (TextView) findViewById(R.id.tv_artist);
        tv_duration = (TextView) findViewById(R.id.tv_duration);
        tv_order = (TextView) findViewById(R.id.tv_order);
        tv_album = (TextView) findViewById(R.id.tv_album);

        iv_shuffle = (ImageView) findViewById(R.id.iv_shuffle);
        iv_prev = (ImageView) findViewById(R.id.iv_prev);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_repeat = (ImageView) findViewById(R.id.iv_repeat);
        iv_shuffle.setOnClickListener(this);
        iv_prev.setOnClickListener(this);
        iv_play.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_repeat.setOnClickListener(this);

        sb_player = (SeekBar) findViewById(R.id.sb_player);
        sb_player.setMax(mm_manager.getCurrentSong().getI_duration());
        sb_player.setOnSeekBarChangeListener(this);

    }

    public void updateSong() {
        Song song = mm_manager.getCurrentSong();
        setInfoSong(song);
        new updateSeekBar().execute();
    }

    private void setInfoSong(Song song) {
        tv_song.setText(song.getS_name());
        tv_artist.setText(song.getS_artist());
        tv_album.setText(song.getS_album());
        tv_order.setText(String.valueOf(mm_manager.getIndex() + 1) + "/" + String.valueOf(mm_manager.getAl_song().size()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_shuffle:
                doShuffle();
                break;
            case R.id.iv_prev:
                doPrev();
                break;
            case R.id.iv_play:
                doPlay();
                break;
            case R.id.iv_next:
                doNext();
                break;
            case R.id.iv_repeat:
                doRepeat();
                break;
            default:
                break;
        }
    }

    private void doRepeat() {
        if(mm_manager.getRp_mode() == mm_manager.rp_off) {
            mm_manager.setRp_mode(mm_manager.rp_one);
            iv_repeat.setImageLevel(MediaManager.rp_one);
            Toast.makeText(this, "Repeat ONE", Toast.LENGTH_SHORT).show();
        } else if(mm_manager.getRp_mode() == mm_manager.rp_one) {
            mm_manager.setRp_mode(mm_manager.rp_all);
            iv_repeat.setImageLevel(MediaManager.rp_all);
            Toast.makeText(this, "Repeat ALL", Toast.LENGTH_SHORT).show();
        } else {
            mm_manager.setRp_mode(mm_manager.rp_off);
            iv_repeat.setImageLevel(MediaManager.rp_off);
            Toast.makeText(this, "Repeat OFF", Toast.LENGTH_SHORT).show();
        }
    }


    private void doNext() {
        if(mm_manager.next()) {
            iv_play.setImageLevel(LEVEL_PAUSE);
            Toast.makeText(this, "Next song", Toast.LENGTH_SHORT).show();
            updateSong();
        }
    }

    private void doPrev() {
        if(mm_manager.prev()) {
            iv_play.setImageLevel(LEVEL_PAUSE);
            Toast.makeText(this, "Previous song", Toast.LENGTH_SHORT).show();
            updateSong();
        }
    }

    private void doShuffle() {
        if(mm_manager.b_shuffle()) {
            mm_manager.setB_shuffle(false);
            iv_shuffle.setImageLevel(LEVEL_OFF);
            Toast.makeText(this, "Shuffle OFF", Toast.LENGTH_SHORT).show();
        } else {
            mm_manager.setB_shuffle(true);
            iv_shuffle.setImageLevel(LEVEL_ON);
            Toast.makeText(this, "Shuffle ON", Toast.LENGTH_SHORT).show();
        }
    }

    private void doPlay() {
        if(mm_manager.play()) {
            iv_play.setImageLevel(LEVEL_PAUSE);
            Toast.makeText(this, "Played", Toast.LENGTH_SHORT).show();
            updateSong();
        } else {
            iv_play.setImageLevel(LEVEL_PLAY);
            Toast.makeText(this, "Paused", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mm_manager.play(position);
        iv_play.setImageLevel(LEVEL_PAUSE);
        Toast.makeText(this, "Playing...", Toast.LENGTH_SHORT).show();
        updateSong();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.i_progress = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mm_manager.seek(i_progress);
        seekBar.setProgress(seekBar.getProgress());
    }

    private class updateSeekBar extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground (Void... params) {
            while (mm_manager.isPlaying()) {
                try {
                    Thread.sleep(1000);
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate (Void... values) {
            tv_duration.setText(mm_manager.getTimeText());
            sb_player.setProgress(mm_manager.getCurrentTime());
            updateSong();
        }
    }
}

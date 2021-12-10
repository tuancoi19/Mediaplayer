package com.example.mediaplayer;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class MediaManager implements MediaPlayer.OnCompletionListener {
    private MediaPlayer mp_player;
    private Context c_context;
    private ArrayList<Song> al_song;

    private static final int idle = 0;
    private static final int playing = 1;
    private static final int paused = 2;
    private static final int stopped = 3;

    private int state = idle;
    private int i_index;
    private int rp_mode = rp_off;

    private boolean b_shuffle;

    public static final int rp_off = 0;
    public static final int rp_one = 1;
    public static final int rp_all = 2;

    public MediaManager(Context context) {
        c_context = context;
        initData();
    }

    private void initData() {
        mp_player = new MediaPlayer();
        al_song = new ArrayList<>();

        Uri audio = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String projection[] = new String[] {
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DURATION,
        };

        String where = MediaStore.Audio.AudioColumns.DISPLAY_NAME + " LIKE '%.mp3'";

        Cursor c = c_context.getContentResolver().query(audio, projection, where, null, null);
        if (c == null) {
            return;
        }
        c.moveToFirst();

        int indexTitle = c.getColumnIndex(projection[0]);
        int indexData = c.getColumnIndex(projection[1]);
        int indexAlbum = c.getColumnIndex(projection[2]);
        int indexArtist = c.getColumnIndex(projection[3]);
        int indexDuration = c.getColumnIndex(projection[4]);

        String s_name, s_path, s_album, s_artist;
        int i_duration;

        while (!c.isAfterLast()) {
            s_name = c.getString(indexTitle);
            s_path = c.getString(indexData);
            s_album = c.getString(indexAlbum);
            s_artist = c.getString(indexArtist);
            i_duration = c.getInt(indexDuration);

            al_song.add(new Song(s_name, s_path, s_album, s_artist, i_duration));

            c.moveToNext();
        }
        c.close();
    }
    public boolean play() {
        try {
            if(state == idle || state == stopped) {
                Song song = al_song.get(i_index);
                mp_player.setDataSource(song.getS_path());
                mp_player.setOnCompletionListener(this);
                mp_player.prepare();
                mp_player.start();
                state = playing;
                return true;
            } else if(state == playing) {
                mp_player.pause();
                state = paused;
                return false;
            } else {
                mp_player.start();
                state = playing;
                return true;
            }
        } catch (IOException e) {
                e.printStackTrace();
            Toast.makeText(c_context, "Error", Toast.LENGTH_SHORT).show();
                return false;
        }
    }

    public boolean b_shuffle() {
        return b_shuffle;
    }

    public void setB_shuffle (boolean shuffle) {
        b_shuffle = shuffle;
    }

    public void play (int position) {
        i_index = position;
        stop();
        play();
    }

    public void stop() {
        if(state == playing || state == paused) {
            mp_player.stop();
            mp_player.reset();
            state = stopped;
        }
    }

    public boolean prev() {
        if(i_index == 0) {
            i_index = al_song.size();
        }
        i_index--;
        stop();
        return play();
    }

    public boolean next() {
        if(b_shuffle) {
            i_index = new Random().nextInt(al_song.size());
        } else {
            i_index = (i_index + 1) % al_song.size();
        }
        stop();
        return play();
    }

    public ArrayList<Song> getAl_song() {
        return al_song;
    }

    public int getRp_mode() {
        return rp_mode;
    }

    public void setRp_mode(int mode) {
        this.rp_mode = mode;
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (rp_mode) {
            case rp_off:
                if(i_index < (al_song.size() - 1)) {
                    i_index++;
                    stop();
                    play();
                }
                break;
            case rp_one:
                stop();
                play();
                break;
            case rp_all:
                i_index++;
                if(i_index == al_song.size()) {
                    i_index = 0;
                }
                stop();
                play();
                break;
            default:
                break;

        }
    }

    public Song getCurrentSong() {
        return  getAl_song().get(i_index);
    }

    public int getIndex() {
        return i_index;
    }

    public boolean isPlaying() {
        return state == playing || state == paused;
    }

    public String getTimeText() {
        int currentTime = mp_player.getCurrentPosition();
        int totalTime = al_song.get(i_index).getI_duration();
        SimpleDateFormat sdf_time = new SimpleDateFormat("mm:ss");
        return sdf_time.format(currentTime) + "/" + sdf_time.format(totalTime);
    }

    public int getCurrentTime() {
        return mp_player.getCurrentPosition();
    }

    public void seek(int progress) {
        mp_player.seekTo(progress);
    }
}

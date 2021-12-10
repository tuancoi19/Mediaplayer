package com.example.mediaplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter
{
    private ArrayList<Song> al_song;
    private LayoutInflater lif_layout;

    public SongAdapter(Context c_context, ArrayList<Song> songs) {
        this.al_song = songs;
        lif_layout = LayoutInflater.from(c_context);
    }

    @Override
    public int getCount() {
        return al_song.size();
    }

    @Override
    public Object getItem(int position) {
        return al_song.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh_view;
        if (convertView == null) {
            vh_view = new ViewHolder();
            convertView = lif_layout.inflate(R.layout.list_view, parent, false);
            vh_view.tv_song = (TextView) convertView.findViewById(R.id.tv_song);
            vh_view.tv_artist = (TextView) convertView.findViewById(R.id.tv_artist);
            vh_view.tv_album = (TextView) convertView.findViewById(R.id.tv_album);
            convertView.setTag(vh_view);
        }else {
            vh_view = (ViewHolder) convertView.getTag();
        }
        Song song = al_song.get(position);
        vh_view.tv_song.setText(song.getS_name());
        vh_view.tv_artist.setText(song.getS_artist());
        vh_view.tv_album.setText(song.getS_album());

        return convertView;
    }

    private class ViewHolder{
            TextView tv_song;
            TextView tv_artist;
            TextView tv_album;
    }
}

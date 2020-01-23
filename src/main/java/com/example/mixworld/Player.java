package com.example.mixworld;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.graphics.PorterDuff.Mode.SRC_IN;

public class Player extends AppCompatActivity {
    Button next,pause,previous;
    TextView song;
    SeekBar songseek;
    static MediaPlayer  myMedia;
    int position;
    ArrayList<File> mySongs;
    Thread updateseekBar;
    String sname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        next=(Button)findViewById(R.id.next);
        pause=(Button)findViewById(R.id.pause);
        previous=(Button)findViewById(R.id.previous);
        song=(TextView)findViewById(R.id.song);
        songseek=(SeekBar)findViewById(R.id.songseek);
        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        updateseekBar=new Thread(){
            @Override
            public void run() {
                int totalDuration=myMedia.getDuration();
                int currentPosition=0;
                while (currentPosition<totalDuration){
                    try {
                        sleep(500);
                        currentPosition=myMedia.getCurrentPosition();
                        songseek.setProgress(currentPosition);

                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };
        if (myMedia!=null){
            myMedia.stop();
            myMedia.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mySongs=(ArrayList) bundle.getParcelableArrayList("songs");
        sname=mySongs.get(position).getName().toString();
        String songName = i.getStringExtra("songname");
        song.setText(songName);
        song.setSelected(true);
        position=bundle.getInt("pos",0);
        Uri u=Uri.parse(mySongs.get(position).toString());
        myMedia=MediaPlayer.create(getApplicationContext(),u);
        myMedia.start();
        songseek.setMax(myMedia.getDuration());
        updateseekBar.start();
        songseek.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        songseek.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), SRC_IN);
        songseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMedia.seekTo(seekBar.getProgress());

            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songseek.setMax(myMedia.getDuration());
                if(myMedia.isPlaying()){
                    pause.setBackgroundResource(R.drawable.play);
                    myMedia.pause();
                }
                else{
                    pause.setBackgroundResource(R.drawable.pause);
                    myMedia.start();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMedia.stop();
                myMedia.release();
                position=((position)%mySongs.size());
                Uri u=Uri.parse(mySongs.get(position).toString());
                myMedia=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).toString();
                song.setText(sname);
                myMedia.start();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMedia.stop();
                myMedia.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u=Uri.parse(mySongs.get(position).toString());
                myMedia=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).toString();
                song.setText(sname);
                myMedia.start();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

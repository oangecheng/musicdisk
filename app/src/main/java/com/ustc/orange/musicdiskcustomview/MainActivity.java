package com.ustc.orange.musicdiskcustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MusicDiskLayout musicDiskLayout = findViewById(R.id.music_disk);
    musicDiskLayout
        .setAnimationDurationMills(3000)
        .setMusicIconNum(2)
        .init();
    musicDiskLayout.start();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }
}

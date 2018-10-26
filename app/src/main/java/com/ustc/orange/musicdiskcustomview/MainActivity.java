package com.ustc.orange.musicdiskcustomview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

  MusicDiskLayout musicDiskLayout;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    musicDiskLayout = findViewById(R.id.music_disk);
    musicDiskLayout
        .setAnimationDurationMills(5000)
        .setMusicIconNum(3)
        .init();
    musicDiskLayout.start();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }
}

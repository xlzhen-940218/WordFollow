package com.xlzhen.wordfollow;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.Slider;
import com.xlzhen.wordfollow.adapter.TextAdapter;
import com.xlzhen.wordfollow.mdel.WordListModel;
import com.xlzhen.wordfollow.mdel.WordModel;
import com.xlzhen.wordfollow.utils.FileUidUtils;
import com.xlzhen.wordfollow.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private boolean pause = false;
    private boolean speakChinese = true;
    private TextToSpeech textToSpeech;
    ActivityResultLauncher<Intent> launcher;
    TextAdapter textAdapter;
    private int currentSpeakIndex;
    private int loopSpeakIndex = -1;
    private String firstUtteranceId;
    private String startUtteranceId;
    private String endUtteranceId;
    private WordListModel model;

    private float speed = 1f;

    private MediaPlayer mp;

    private boolean playStart;

    public void audioPlayer(String path) {

        if (mp == null) {
            mp = new MediaPlayer();
            mp.setOnCompletionListener(mediaPlayer -> {
                mp.stop();
                if (playStart) {
                    playStart = false;
                    speak(model.getWordModels().get(currentSpeakIndex));
                } else {
                    if (loopSpeakIndex != -1) {
                        currentSpeakIndex = loopSpeakIndex;
                    } else {
                        if (currentSpeakIndex < model.getWordModels().size() - 1) {
                            currentSpeakIndex++;
                        } else {
                            currentSpeakIndex = 0;
                        }
                    }
                    if (!pause) {
                        playStart = true;
                        speak(model.getWordModels().get(currentSpeakIndex));
                    }
                }
            });
        }
        if (mp.isPlaying()) {
            return;
        }
        try {
            mp.reset();
            mp.setDataSource(path);
            mp.prepare();
            mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));
            mp.start();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    private void speak(WordModel wordModel) {
        if (playStart || !speakChinese) {
            startUtteranceId = UUID.randomUUID().toString();
            endUtteranceId = UUID.randomUUID().toString();
            textAdapter.setSelectedPosition(currentSpeakIndex);
        }
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (new File(playStart || !speakChinese ? wordModel.getWordVoicePath() : wordModel.getChineseVoicePath()).exists()) {
                    audioPlayer(playStart || !speakChinese ? wordModel.getWordVoicePath() : wordModel.getChineseVoicePath());
                } else {
                    textToSpeech.speak(playStart || !speakChinese ? wordModel.getWord() : wordModel.getChinese(), TextToSpeech.QUEUE_FLUSH, null, playStart ? startUtteranceId : endUtteranceId);
                }
            }
        }, playStart || !speakChinese ? 1000 : 500);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        // 处理数据
                        if (data != null && data.getStringExtra("uid") != null) {
                            String uid = data.getStringExtra("uid");
                            WordListModel wordListModel = StorageUtils.getExternalFilesData(MainActivity.this, FileUidUtils.getFileName(MainActivity.this, uid), WordListModel.class);
                            if (wordListModel != null) {
                                if (textAdapter.getItemCount() > 0) {
                                    currentSpeakIndex = 0;
                                }
                                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                                model = wordListModel;
                                textAdapter.setData(model.getWordModels());
                                String speak = getString(R.string.please_ready_speak);
                                firstUtteranceId = UUID.randomUUID().toString();
                                textToSpeech.speak(speak, TextToSpeech.QUEUE_FLUSH, null, firstUtteranceId);

                            }
                        }
                    }
                });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        textAdapter = new TextAdapter(new ArrayList<>(), new TextAdapter.LoopClickListener() {
            @Override
            public void onClick(int position, boolean loop) {
                if(loop) {
                    loopSpeakIndex = position;
                }else{
                    loopSpeakIndex = -1;
                }
            }
        });
        recyclerView.setAdapter(textAdapter);

        // 初始化 TTS
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // 设置语言（例如：中文）
                    int result = textToSpeech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(MainActivity.this, R.string.tts_data_error, Toast.LENGTH_SHORT).show();
                        // 提示用户下载语言包
                        Intent installIntent = new Intent();
                        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(installIntent);
                    }
                    textToSpeech.setSpeechRate(1f); // 1.0 为正常速度
                    textToSpeech.setPitch(1.1f);     // 1.0 为正常音调
                } else {
                    Toast.makeText(MainActivity.this, R.string.tts_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // 朗读开始
            }

            @Override
            public void onDone(String utteranceId) {
                // 朗读完成
                runOnUiThread(() -> {
                    if (model != null) {
                        if (MainActivity.this.firstUtteranceId.equals(utteranceId)) {
                            firstUtteranceId = "";
                            playStart = true;
                            speak(model.getWordModels().get(currentSpeakIndex));
                        } else if (MainActivity.this.startUtteranceId.equals(utteranceId)) {
                            playStart = false;
                            speak(model.getWordModels().get(currentSpeakIndex));

                        } else if (MainActivity.this.endUtteranceId.equals(utteranceId)) {
                            if (loopSpeakIndex != -1) {
                                currentSpeakIndex = loopSpeakIndex;
                            } else {
                                if (currentSpeakIndex < model.getWordModels().size() - 1) {
                                    currentSpeakIndex++;
                                } else {
                                    currentSpeakIndex = 0;
                                }
                            }
                            if (!pause) {
                                playStart = true;
                                speak(model.getWordModels().get(currentSpeakIndex));
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {
                // 发生错误
            }
        });

        Slider slider = findViewById(R.id.slider);
        slider.addOnChangeListener((slider1, value, fromUser) -> {
            speed = 1f + (value - 5) * 0.1f;
            textToSpeech.setSpeechRate(speed);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            startActivity(new Intent(this, CreateVocabActivity.class));
            // 处理创建单词表逻辑
            return true;
        } else if (id == R.id.action_load) {
            // 处理加载单词表逻辑
            launcher.launch(new Intent(this, LoadVocabActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_speak_chinese) {
            item.setChecked(!item.isChecked());
            // 在此处实现朗读中文的逻辑切换
            speakChinese = item.isChecked();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();   // 停止朗读
            textToSpeech.shutdown(); // 释放资源
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pause = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pause) {
            pause = false;
            if (model != null && model.getWordModels() != null && firstUtteranceId.isEmpty()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                playStart = true;
                speak(model.getWordModels().get(currentSpeakIndex));
            }
        }
    }
}
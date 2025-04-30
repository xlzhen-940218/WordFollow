package com.xlzhen.wordfollow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson2.JSON;
import com.xlzhen.wordfollow.adapter.WordAdapter;
import com.xlzhen.wordfollow.mdel.WordListModel;
import com.xlzhen.wordfollow.mdel.WordModel;
import com.xlzhen.wordfollow.mdel.WordPair;
import com.xlzhen.wordfollow.utils.FileUidUtils;
import com.xlzhen.wordfollow.utils.StorageUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadByFileVocabActivity extends AppCompatActivity {

    private WordAdapter adapter;
    private List<WordPair> wordList = new ArrayList<>();
    private EditText nameEditText;

    private void parseAndDisplayJson(String json) {
        if (json == null) {
            Toast.makeText(this, "Failed to read JSON", Toast.LENGTH_SHORT).show();
            return;
        }
        WordListModel wordListModel = JSON.parseObject(json, WordListModel.class);
        if (wordListModel != null && wordListModel.getTitle() != null) {
            nameEditText.setText(wordListModel.getTitle());
        }
        if (wordListModel.getWordModels() != null && wordListModel.getWordModels().size() > 0) {
            for (WordModel wordModel :
                    wordListModel.getWordModels()) {
                wordList.add(new WordPair(wordModel.getWord(), wordModel.getChinese(), "", ""));
                adapter.notifyItemInserted(wordList.size() - 1);
            }
        }
    }

    private void handleSharedJson(Intent intent) {
        Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            String json = readJsonFromUri(uri);
            parseAndDisplayJson(json);
        }
    }

    private void handleViewJson(Uri uri) {
        if (uri != null) {
            String json = readJsonFromUri(uri);
            parseAndDisplayJson(json);
        }
    }

    private String readJsonFromUri(Uri uri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return stringBuilder.toString();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_vocab);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_vocab_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        nameEditText = findViewById(R.id.name_edit_text);
        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rv_word_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordAdapter(wordList);
        recyclerView.setAdapter(adapter);

        // 添加按钮点击事件
        findViewById(R.id.btn_add).setOnClickListener(v -> {
            wordList.add(new WordPair("", "", "", ""));
            adapter.notifyItemInserted(wordList.size() - 1);
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("application/json".equals(type)) {
                handleSharedJson(intent); // 处理分享的JSON
            }
        } else if (Intent.ACTION_VIEW.equals(action)) {
            handleViewJson(intent.getData()); // 处理直接打开的JSON文件
        }
    }

    private void saveWordList() {
        long id = 0;
        List<WordModel> validPairs = new ArrayList<>();
        for (WordPair pair : wordList) {
            if (!pair.english.isEmpty() /*&& !pair.chinese.isEmpty()*/) {
                validPairs.add(new WordModel(id, pair.english, pair.chinese, pair.englishVoicePath, pair.chineseVoicePath));
                id++;
            }
        }

        String title = nameEditText.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, R.string.please_input_vocab_name, Toast.LENGTH_SHORT).show();
            return;
        }
        // 保存逻辑实现...
        String uid = UUID.randomUUID().toString();
        WordListModel wordListModel = new WordListModel(uid, title, validPairs);
        StorageUtils.saveExternalFilesData(this, FileUidUtils.getFileName(this, uid), wordListModel);
        Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveWordList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

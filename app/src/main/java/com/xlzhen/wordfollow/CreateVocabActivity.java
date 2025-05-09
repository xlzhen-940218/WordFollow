package com.xlzhen.wordfollow;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlzhen.edgetts.EdgeTTS;
import com.xlzhen.wordfollow.adapter.WordAdapter;
import com.xlzhen.wordfollow.mdel.WordListModel;
import com.xlzhen.wordfollow.mdel.WordModel;
import com.xlzhen.wordfollow.mdel.WordPair;
import com.xlzhen.wordfollow.utils.FileUidUtils;
import com.xlzhen.wordfollow.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class CreateVocabActivity extends AppCompatActivity {

    private WordAdapter adapter;
    private List<WordPair> wordList = new ArrayList<>();

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

        // 添加初始条目
        wordList.add(new WordPair("", "", "", ""));
        adapter.notifyItemInserted(0);

        // 添加按钮点击事件
        findViewById(R.id.btn_add).setOnClickListener(v -> {
            wordList.add(new WordPair("", "","", ""));
            adapter.notifyItemInserted(wordList.size() - 1);
        });

        // 保存按钮点击事件
        // findViewById(R.id.btn_save).setOnClickListener(v -> saveWordList());
    }

    private void saveWordList() {
        long id = 0;
        List<WordModel> validPairs = new ArrayList<>();
        for (WordPair pair : wordList) {
            if (!pair.english.isEmpty() /*&& !pair.chinese.isEmpty()*/) {
                validPairs.add(new WordModel(id, pair.english, pair.chinese,pair.englishVoicePath,pair.chineseVoicePath));
                id++;
            }
        }
        EditText editText = findViewById(R.id.name_edit_text);
        String title = editText.getText().toString();
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
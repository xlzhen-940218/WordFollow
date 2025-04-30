package com.xlzhen.wordfollow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlzhen.wordfollow.adapter.VocabAdapter;
import com.xlzhen.wordfollow.mdel.VocabFile;
import com.xlzhen.wordfollow.mdel.WordListModel;
import com.xlzhen.wordfollow.mdel.WordModel;
import com.xlzhen.wordfollow.mdel.WordPair;
import com.xlzhen.wordfollow.utils.FileUidUtils;
import com.xlzhen.wordfollow.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LoadVocabActivity extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_load_vocab);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.load_vocab_main), (v, insets) -> {
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
        recyclerView = findViewById(R.id.rv_vocab_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void switchVocab(String uid) {
        // 处理切换单词表逻辑
        Intent intent = new Intent();
        intent.putExtra("uid", uid);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void exportVocab(String uid) {
        File file = StorageUtils.getExternalFileByKey(this, FileUidUtils.getFileName(this, uid));
        WordListModel model = StorageUtils.getExternalFilesData(this, file.getName(), WordListModel.class);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType(getContentResolver().getType(uri));
        if (getPackageManager().resolveActivity(intent, 0) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<VocabFile> vocabFiles = new ArrayList<>();
        File[] files = StorageUtils.getExternalFiles(this);
        if (files != null) {
            for (File f : files) {
                WordListModel model = StorageUtils.getExternalFilesData(this, f.getName(), WordListModel.class);
                List<WordPair> wordPairs = new ArrayList<>();
                if (model != null) {
                    for (WordModel wordModel : model.getWordModels()) {
                        wordPairs.add(new WordPair(wordModel.getWord(), wordModel.getChinese()));
                    }
                    VocabFile vocabFile = new VocabFile(model.getUid(), model.getTitle(), wordPairs);
                    vocabFiles.add(vocabFile);
                }
            }
        }

        VocabAdapter adapter = new VocabAdapter(vocabFiles, this::switchVocab, this::exportVocab);
        recyclerView.setAdapter(adapter);
    }
}
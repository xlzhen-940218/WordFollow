package com.xlzhen.wordfollow;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xlzhen.wordfollow.mdel.WordListModel;
import com.xlzhen.wordfollow.mdel.WordModel;
import com.xlzhen.wordfollow.utils.FileUidUtils;
import com.xlzhen.wordfollow.utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        wordList.add(new WordPair("", ""));
        adapter.notifyItemInserted(0);

        // 添加按钮点击事件
        findViewById(R.id.btn_add).setOnClickListener(v -> {
            wordList.add(new WordPair("", ""));
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
                validPairs.add(new WordModel(id, pair.english, pair.chinese));
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

    // 适配器类
    private static class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

        private final List<WordPair> data;

        public WordAdapter(List<WordPair> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_word_input, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WordPair pair = data.get(position);
            holder.etEnglish.setText(pair.english);
            holder.etChinese.setText(pair.chinese);

            holder.etEnglish.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    data.get(position).english = s.toString();
                }
            });

            holder.etChinese.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    data.get(position).chinese = s.toString();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            EditText etEnglish, etChinese;

            ViewHolder(View itemView) {
                super(itemView);
                etEnglish = itemView.findViewById(R.id.et_english);
                etChinese = itemView.findViewById(R.id.et_chinese);
            }
        }
    }

    // 数据模型
    private static class WordPair {
        String english;
        String chinese;

        public WordPair(String english, String chinese) {
            this.english = english;
            this.chinese = chinese;
        }
    }

    // 简化TextWatcher
    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
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
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

import com.xlzhen.wordfollow.mdel.WordListModel;
import com.xlzhen.wordfollow.mdel.WordModel;
import com.xlzhen.wordfollow.utils.FileUidUtils;
import com.xlzhen.wordfollow.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LoadVocabActivity extends AppCompatActivity {

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
        RecyclerView recyclerView = findViewById(R.id.rv_vocab_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 示例数据
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

//        files.add(new VocabFile("四六级词汇", Arrays.asList(
//            new WordPair("apple", "苹果"),
//            new WordPair("banana", "香蕉"),
//            new WordPair("computer", "电脑"),
//            new WordPair("mobile", "手机"),
//            new WordPair("keyboard", "键盘")
//        )));

        VocabAdapter adapter = new VocabAdapter(vocabFiles, this::switchVocab, this::exportVocab);
        recyclerView.setAdapter(adapter);
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

    // 数据模型
    public static class VocabFile {
        String uid;
        String fileName;
        List<WordPair> words;
        boolean isExpanded = false;

        public VocabFile(String uid, String name, List<WordPair> words) {
            this.uid = uid;
            this.fileName = name;
            this.words = words.size() > 5 ? words.subList(0, 5) : words;
        }
    }

    // 适配器
    private static class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.ViewHolder> {

        private final List<VocabFile> files;
        private final Consumer<String> switchListener;
        private final Consumer<String> exportListener;

        public VocabAdapter(List<VocabFile> files, Consumer<String> switchListener, Consumer<String> exportListener) {
            this.files = files;
            this.switchListener = switchListener;
            this.exportListener = exportListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_vocab_file, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VocabFile file = files.get(position);

            // 设置文件名
            String name = file.fileName + " - " + file.uid;
            holder.tvFileName.setText(name);

            // 处理展开状态
            holder.itemView.setOnClickListener(v -> toggleExpand(holder, position));
            updateExpandState(holder, file.isExpanded);

            // 动态生成单词列表
            holder.layoutWordList.removeAllViews();
            for (WordPair pair : file.words) {
                TextView tv = new TextView(holder.itemView.getContext());
                tv.setText(String.format("%s - %s", pair.english, pair.chinese));
                tv.setTextSize(14);
                tv.setPadding(0, 4, 0, 4);
                holder.layoutWordList.addView(tv);
            }

            // 切换按钮点击事件
            holder.btnSwitch.setOnClickListener(v ->
                    switchListener.accept(file.uid));

            holder.btnExport.setOnClickListener(v -> {
                exportListener.accept(file.uid);
            });
        }

        private void toggleExpand(ViewHolder holder, int position) {
            VocabFile file = files.get(position);
            file.isExpanded = !file.isExpanded;
            updateExpandState(holder, file.isExpanded);
            notifyItemChanged(position);
        }

        private void updateExpandState(ViewHolder holder, boolean isExpanded) {
            holder.layoutExpandContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.ivExpand.setRotation(isExpanded ? 180 : 0);
        }

        @Override
        public int getItemCount() {
            return files.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFileName;
            ImageView ivExpand;
            LinearLayout layoutExpandContent;
            LinearLayout layoutWordList;
            Button btnSwitch, btnExport;

            public ViewHolder(View itemView) {
                super(itemView);
                tvFileName = itemView.findViewById(R.id.tv_file_name);
                ivExpand = itemView.findViewById(R.id.iv_expand);
                layoutExpandContent = itemView.findViewById(R.id.layout_expand_content);
                layoutWordList = itemView.findViewById(R.id.layout_word_list);
                btnSwitch = itemView.findViewById(R.id.btn_switch);
                btnExport = itemView.findViewById(R.id.btn_export);
            }
        }
    }

    // 单词对数据类
    public static class WordPair {
        String english;
        String chinese;

        public WordPair(String eng, String chi) {
            english = eng;
            chinese = chi;
        }
    }
}
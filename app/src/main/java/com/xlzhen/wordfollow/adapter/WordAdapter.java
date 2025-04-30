package com.xlzhen.wordfollow.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.xlzhen.edgetts.EdgeTTS;
import com.xlzhen.wordfollow.R;
import com.xlzhen.wordfollow.mdel.WordPair;
import com.xlzhen.wordfollow.textwatcher.SimpleTextWatcher;
import com.xlzhen.wordfollow.utils.StorageUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

// 适配器类
public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

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

    // 2. 统一处理逻辑的泛型方法
    void handleTTS(Context context, String text, String existingPath, Consumer<String> pathSetter, @StringRes int errorRes) {
        if (text.isEmpty()) {
            Toast.makeText(context, errorRes, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!existingPath.isEmpty()) return;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String path = StorageUtils.generateVoiceFilePath(context);
                path = EdgeTTS.textToMp3(text, path);

                if (new File(path).exists()) {
                    String finalPath = path;
                    ((Activity) context).runOnUiThread(() -> {
                        pathSetter.accept(finalPath);
                        // 3. 可在此添加数据更新通知
                    });
                }
            } catch (Exception e) {
                Log.e("TTS", "Generate voice failed", e);
            }
        });
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
        holder.generateVoiceText.setOnClickListener(v -> {
            // 1. 使用局部变量确保数据一致性
            final int safePosition = holder.getAdapterPosition();
            if (safePosition == RecyclerView.NO_POSITION) return;
            final WordPair item = data.get(safePosition);

            // 4. 并行处理中英文语音生成
            handleTTS(v.getContext(), item.english, item.englishVoicePath,
                    path -> {
                        item.englishVoicePath = path;
                        Toast.makeText(v.getContext(), R.string.generate_eng_success, Toast.LENGTH_SHORT).show();
                        if(!item.chineseVoicePath.isEmpty()){
                            v.setVisibility(View.GONE);
                        }
                    }, R.string.please_input_word);

            handleTTS(v.getContext(), item.chinese, item.chineseVoicePath,
                    path -> {
                        item.chineseVoicePath = path;
                        Toast.makeText(v.getContext(), R.string.generate_chi_success, Toast.LENGTH_SHORT).show();
                        if(!item.englishVoicePath.isEmpty()){
                            v.setVisibility(View.GONE);
                        }
                    }, R.string.please_input_chinese);

        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etEnglish, etChinese;
        TextView generateVoiceText;

        ViewHolder(View itemView) {
            super(itemView);
            etEnglish = itemView.findViewById(R.id.et_english);
            etChinese = itemView.findViewById(R.id.et_chinese);
            generateVoiceText = itemView.findViewById(R.id.btn_tts);
        }
    }
}
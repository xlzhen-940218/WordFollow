package com.xlzhen.wordfollow.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xlzhen.wordfollow.R;
import com.xlzhen.wordfollow.mdel.VocabFile;
import com.xlzhen.wordfollow.mdel.WordPair;

import java.util.List;
import java.util.function.Consumer;

// 适配器
public class VocabAdapter extends RecyclerView.Adapter<VocabAdapter.ViewHolder> {

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
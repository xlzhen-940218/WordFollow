package com.xlzhen.wordfollow.adapter;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xlzhen.wordfollow.R;
import com.xlzhen.wordfollow.mdel.WordModel;

import java.util.ArrayList;
import java.util.List;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.ViewHolder> {
    private List<WordModel> items;
    private int selectedPosition = -1;
    private boolean loop = false;

    public interface LoopClickListener {
        void onClick(int position, boolean loop);
    }

    private LoopClickListener loopClickListener;

    public TextAdapter(List<WordModel> items, LoopClickListener loopClickListener) {
        this.items = items;
        this.loopClickListener = loopClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(items.get(position).getWord());

        if (selectedPosition == -1) {
            holder.loopView.setVisibility(View.GONE);
            applyDefaultStyle(holder.textView);
        } else {
            int distance = Math.abs(position - selectedPosition);
            switch (distance) {
                case 0: // 选中项
                    holder.textView.setTextColor(holder.itemView.getResources().getColor(R.color.black, holder.itemView.getContext().getTheme()));
                    holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                    holder.loopView.setVisibility(View.VISIBLE);
                    holder.loopView.setOnClickListener(v -> {
                        if (loopClickListener != null) {
                            loop = !loop;

                            loopClickListener.onClick(holder.getAdapterPosition(), loop);
                            ((TextView)v).setText(loop?R.string.unloop:R.string.loop);
                        }
                    });
                    break;
                case 1: // 相邻项
                    holder.textView.setTextColor(holder.itemView.getResources().getColor(R.color.gray_8, holder.itemView.getContext().getTheme()));
                    holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    holder.loopView.setVisibility(View.GONE);
                    break;
                default: // 其他项
                    holder.loopView.setVisibility(View.GONE);
                    applyDefaultStyle(holder.textView);
            }
        }
        holder.loopView.setText(loop?R.string.unloop:R.string.loop);
    }

    private void applyDefaultStyle(TextView textView) {

        textView.setTextColor(textView.getResources().getColor(R.color.gray_a, textView.getContext().getTheme()));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
    }

    private void updateItems(int oldPos, int newPos) {
        List<Integer> positions = new ArrayList<>();
        if (oldPos != -1) {
            positions.add(oldPos);
            if (oldPos > 0) positions.add(oldPos - 1);
            if (oldPos < getItemCount() - 1) positions.add(oldPos + 1);
        }
        positions.add(newPos);
        if (newPos > 0) positions.add(newPos - 1);
        if (newPos < getItemCount() - 1) positions.add(newPos + 1);

        for (int pos : positions) {
            notifyItemChanged(pos);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setData(List<WordModel> wordModels) {
        notifyItemRangeRemoved(0, this.items.size());
        this.items = wordModels;
        this.selectedPosition = -1;
        notifyItemRangeChanged(0, items.size());

    }

    public void setSelectedPosition(int selectedPosition) {
        if (selectedPosition >= items.size())
            return;
        int oldPosition = this.selectedPosition;
        this.selectedPosition = selectedPosition;
        updateItems(oldPosition, this.selectedPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView loopView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.itemTextView);
            loopView = itemView.findViewById(R.id.loop_tv);
        }
    }
}
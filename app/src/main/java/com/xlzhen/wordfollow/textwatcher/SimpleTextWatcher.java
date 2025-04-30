package com.xlzhen.wordfollow.textwatcher;

import android.text.TextWatcher;

// 简化TextWatcher
public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
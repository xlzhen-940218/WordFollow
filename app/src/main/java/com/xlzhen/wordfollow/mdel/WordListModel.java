package com.xlzhen.wordfollow.mdel;

import java.util.List;

public class WordListModel {
    /**
     * 单词组 uuid
     */
    private String uid;
    /**
     * 标题
     */
    private String title;
    /**
     * 单词组 具体内容
     */
    private List<WordModel> wordModels;

    public WordListModel() {
    }

    public WordListModel(String uid,String title, List<WordModel> wordModels) {
        this.uid = uid;
        this.title = title;
        this.wordModels = wordModels;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<WordModel> getWordModels() {
        return wordModels;
    }

    public void setWordModels(List<WordModel> wordModels) {
        this.wordModels = wordModels;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

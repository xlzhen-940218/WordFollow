package com.xlzhen.wordfollow.mdel;

public class WordModel {
    /**
     * id
     */
    private long id;
    /**
     * 单词
     */
    private String word;
    /**
     * 对应中文
     */
    private String chinese;

    public WordModel() {
    }

    public WordModel(long id, String word, String chinese) {
        this.id = id;
        this.word = word;
        this.chinese = chinese;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }
}

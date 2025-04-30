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

    /**
     * 单词语音路径
     */
    private String wordVoicePath = "";
    /**
     * 中文语音路径
     */
    private String chineseVoicePath = "";

    public WordModel() {
    }

    public WordModel(long id, String word, String chinese,String wordVoicePath,String chineseVoicePath) {
        this.id = id;
        this.word = word;
        this.chinese = chinese;
        this.wordVoicePath = wordVoicePath;
        this.chineseVoicePath = chineseVoicePath;
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

    public String getWordVoicePath() {
        return wordVoicePath;
    }

    public void setWordVoicePath(String wordVoicePath) {
        this.wordVoicePath = wordVoicePath;
    }

    public String getChineseVoicePath() {
        return chineseVoicePath;
    }

    public void setChineseVoicePath(String chineseVoicePath) {
        this.chineseVoicePath = chineseVoicePath;
    }
}

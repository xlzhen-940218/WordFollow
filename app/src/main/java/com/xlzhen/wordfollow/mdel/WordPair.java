package com.xlzhen.wordfollow.mdel;


// 数据模型
public class WordPair {
    public String english;
    public String chinese;
    public String englishVoicePath;
    public String chineseVoicePath;

    public WordPair(String english, String chinese, String englishVoicePath, String chineseVoicePath) {
        this.english = english;
        this.chinese = chinese;
        this.englishVoicePath = englishVoicePath;
        this.chineseVoicePath = chineseVoicePath;
    }

    public WordPair(String english, String chinese) {
        this.english = english;
        this.chinese = chinese;
    }
}
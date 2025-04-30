package com.xlzhen.wordfollow.mdel;

import java.util.List;

public class VocabFile {
    public String uid;
    public String fileName;
    public List<WordPair> words;
    public boolean isExpanded = false;

    public VocabFile(String uid, String name, List<WordPair> words) {
        this.uid = uid;
        this.fileName = name;
        this.words = words.size() > 5 ? words.subList(0, 5) : words;
    }
}

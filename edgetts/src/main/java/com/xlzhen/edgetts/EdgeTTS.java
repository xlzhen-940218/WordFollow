package com.xlzhen.edgetts;

import java.util.stream.Collectors;

import io.github.whitemagic2014.tts.TTS;
import io.github.whitemagic2014.tts.TTSVoice;
import io.github.whitemagic2014.tts.bean.Voice;

public class EdgeTTS {
    public static final String XIAO_YI = "zh-CN-XiaoyiNeural";
    public static final String YUN_YANG = "zh-CN-YunyangNeural";
    public static String textToMp3(String text, String audioPath,String ttsName) {
        Voice voice = TTSVoice.provides().stream().filter(v -> v.getShortName().equals(ttsName)).collect(Collectors.toList()).get(0);
        // Set to true to resolve the rate limiting issue in certain regions..
        // You can customize the file name; if omitted, a random file name will be generated.
        // When the specified file name is the same, it will either overwrite or append to the file.
        // default mp3.
        return new TTS(voice, text)
                .findHeadHook()
                .isRateLimited(true) // Set to true to resolve the rate limiting issue in certain regions..
                .fileName(audioPath)// You can customize the file name; if omitted, a random file name will be generated.
                .overwrite(false) // When the specified file name is the same, it will either overwrite or append to the file.
                .formatMp3()  // default mp3.
                .trans();
    }
}

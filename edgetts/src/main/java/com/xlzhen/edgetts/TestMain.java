package com.xlzhen.edgetts;

import java.util.stream.Collectors;

import io.github.whitemagic2014.tts.TTS;
import io.github.whitemagic2014.tts.TTSVoice;
import io.github.whitemagic2014.tts.bean.Voice;

public class TestMain {
    public static void main(String[] args) {
        Voice voice = TTSVoice.provides().stream().filter(v -> v.getShortName().equals("zh-CN-XiaoyiNeural")).collect(Collectors.toList()).get(0);
        String content = "tiger";
        String fileName = new TTS(voice, content)
                .findHeadHook()
                .isRateLimited(true) // Set to true to resolve the rate limiting issue in certain regions..
                .fileName("tts_output.mp3")// You can customize the file name; if omitted, a random file name will be generated.
                .overwrite(false) // When the specified file name is the same, it will either overwrite or append to the file.
                .formatMp3()  // default mp3.
//                .formatOpus() // or opus
//                .voicePitch()
//                .voiceRate()
//                .voiceVolume()
//                .storage()  // the output file storage ,default is ./storage
//                .connectTimeout(0) // set connect timeout
                .trans();

    }
}
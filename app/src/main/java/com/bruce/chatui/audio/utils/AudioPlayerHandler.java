
package com.bruce.chatui.audio.utils;

import com.bruce.chatui.audio.SpeexDecoder;

import java.io.File;


public class AudioPlayerHandler {
    private String fileName = null;
    private SpeexDecoder speexdec = null;
    private static Thread th = null;

    private static AudioPlayerHandler instance = null;

    public static synchronized AudioPlayerHandler getInstance() {
        if (null == instance) {
            instance = new AudioPlayerHandler();
        }
        return instance;
    }

    public AudioPlayerHandler() {
    }

    public void stopPlayer() {
        try {
            if (null != th) {
                th.interrupt();
                th = null;
                Thread.currentThread().interrupt();
            } else {
            }
        } catch (Exception e) {
        }
    }

    public boolean isPlaying() {
        return null != th;
    }

    public void startPlay(String filePath) {
        this.fileName = filePath;
        try {
            speexdec = new SpeexDecoder(new File(this.fileName));
            RecordPlayThread rpt = new RecordPlayThread();
            if (null == th)
                th = new Thread(rpt);
            th.start();
        } catch (Exception e) {
        }
    }

    class RecordPlayThread extends Thread {

        public void run() {
            try {
                if (null != speexdec)
                    speexdec.decode();

            } catch (Exception e) {
            }
        }
    };
}

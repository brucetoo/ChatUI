
package com.mogujie.tt.support.audio;

import com.bruce.chatui.utils.Logger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class SpeexWriter implements Runnable {

    private final Object mutex = new Object();

    private SpeexWriteClient client = new SpeexWriteClient();
    private volatile boolean isRecording;
    private processedData pData;
    private List<processedData> list;

    private SpeexEncoder.TaskCallback mCallback;
    public static int write_packageSize = 1024;

    public SpeexWriter(String fileName, SpeexEncoder.TaskCallback callback) {
        super();
        list = Collections.synchronizedList(new LinkedList<processedData>());
        this.mCallback = callback;
        client.setSampleRate(8000);

        client.start(fileName);
    }

    public void run() {
        Logger.info("","write thread runing");
        while (this.isRecording() || list.size() > 0) {

            if (list.size() > 0) {
                pData = list.remove(0);
                // gauss_packageSize/2
                Logger.info("pData size=" , pData.size+"");

                client.writeTag(pData.processed, pData.size);

                Logger.info("list size = {}" ,""+ list.size());
            } else {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        Logger.info("","write thread exit");
        stop();
        mCallback.callback(null);
    }

    public void putData(final byte[] buf, int size) {

        Logger.info("after convert. size=====================[640]:", ""+ size);

        processedData data = new processedData();
        // data.ts = ts;
        data.size = size;
        System.arraycopy(buf, 0, data.processed, 0, size);
        list.add(data);
    }

    public void stop() {
        client.stop();
    }

    public void setRecording(boolean isRecording) {
        synchronized (mutex) {
            this.isRecording = isRecording;
            if (this.isRecording) {
                mutex.notify();
            }
        }
    }

    public boolean isRecording() {
        synchronized (mutex) {
            return isRecording;
        }
    }

    class processedData {
        // private long ts;
        private int size;
        private byte[] processed = new byte[write_packageSize];
    }

}

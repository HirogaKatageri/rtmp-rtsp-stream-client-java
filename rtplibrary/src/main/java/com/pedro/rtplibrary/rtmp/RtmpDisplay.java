package com.pedro.rtplibrary.rtmp;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Build;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.pedro.rtplibrary.base.DisplayBase;

import net.ossrs.rtmp.ConnectCheckerRtmp;
import net.ossrs.rtmp.SrsFlvMuxer;

import java.nio.ByteBuffer;

/**
 * More documentation see:
 * {@link com.pedro.rtplibrary.base.DisplayBase}
 * <p>
 * Created by pedro on 9/08/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RtmpDisplay extends DisplayBase {

    private SrsFlvMuxer srsFlvMuxer1;
    private SrsFlvMuxer srsFlvMuxer2;
    private SrsFlvMuxer srsFlvMuxer3;
    private boolean streamForTwo = false;
    private boolean streamForThree = false;

    public RtmpDisplay(Context context, boolean useOpengl, ConnectCheckerRtmp connectChecker) {
        super(context, useOpengl);
        srsFlvMuxer1 = new SrsFlvMuxer(connectChecker);
        srsFlvMuxer2 = new SrsFlvMuxer(connectChecker);
        srsFlvMuxer3 = new SrsFlvMuxer(connectChecker);
    }

    /**
     * H264 profile.
     *
     * @param profileIop Could be ProfileIop.BASELINE or ProfileIop.CONSTRAINED
     */
    public void setProfileIop(byte profileIop) {
        srsFlvMuxer1.setProfileIop(profileIop);
        srsFlvMuxer2.setProfileIop(profileIop);
        srsFlvMuxer3.setProfileIop(profileIop);
    }

    @Override
    public void resizeCache(int newSize) throws RuntimeException {
        srsFlvMuxer1.resizeFlvTagCache(newSize);
        srsFlvMuxer2.resizeFlvTagCache(newSize);
        srsFlvMuxer3.resizeFlvTagCache(newSize);
    }

    @Override
    public int getCacheSize() {
        int cache = 0;
        if (streamForTwo) {
            cache = srsFlvMuxer1.getFlvTagCacheSize() + srsFlvMuxer2.getFlvTagCacheSize();
        } else if (streamForThree) {
            cache = srsFlvMuxer1.getFlvTagCacheSize() + srsFlvMuxer2.getFlvTagCacheSize() + srsFlvMuxer3.getFlvTagCacheSize();
        } else {
            cache = srsFlvMuxer1.getFlvTagCacheSize();
        }
        return cache;
    }

    @Override
    public long getSentAudioFrames() {
        long audioFrame = 0;
        if (streamForTwo) {
            audioFrame = srsFlvMuxer1.getSentAudioFrames() + srsFlvMuxer2.getSentAudioFrames();
        } else if (streamForThree) {
            audioFrame = srsFlvMuxer1.getSentAudioFrames() + srsFlvMuxer2.getSentAudioFrames() + srsFlvMuxer3.getSentAudioFrames();
        } else {
            audioFrame = srsFlvMuxer1.getSentAudioFrames();
        }
        return audioFrame;
    }

    @Override
    public long getSentVideoFrames() {
        long videoFrame = 0;
        if (streamForTwo) {
            videoFrame = srsFlvMuxer1.getSentVideoFrames() + srsFlvMuxer2.getSentVideoFrames();
        } else if (streamForThree) {
            videoFrame = srsFlvMuxer1.getSentVideoFrames() + srsFlvMuxer2.getSentVideoFrames() + srsFlvMuxer3.getSentVideoFrames();
        } else {
            videoFrame = srsFlvMuxer1.getSentVideoFrames();
        }
        return videoFrame;
    }

    @Override
    public long getDroppedAudioFrames() {
        long audioFrame = 0;
        if (streamForTwo) {
            audioFrame = srsFlvMuxer1.getDroppedAudioFrames() + srsFlvMuxer2.getDroppedAudioFrames();
        } else if (streamForThree) {
            audioFrame = srsFlvMuxer1.getDroppedAudioFrames() + srsFlvMuxer2.getDroppedAudioFrames() + srsFlvMuxer3.getDroppedAudioFrames();
        } else {
            audioFrame = srsFlvMuxer1.getDroppedAudioFrames();
        }
        return audioFrame;
    }

    @Override
    public long getDroppedVideoFrames() {
        long videoFrame = 0;
        if (streamForTwo) {
            videoFrame = srsFlvMuxer1.getDroppedVideoFrames() + srsFlvMuxer2.getDroppedVideoFrames();
        } else if (streamForThree) {
            videoFrame = srsFlvMuxer1.getDroppedVideoFrames() + srsFlvMuxer2.getDroppedVideoFrames() + srsFlvMuxer3.getDroppedVideoFrames();
        } else {
            videoFrame = srsFlvMuxer1.getDroppedVideoFrames();
        }
        return videoFrame;
    }

    @Override
    public void resetSentAudioFrames() {
        srsFlvMuxer1.resetSentAudioFrames();
        srsFlvMuxer2.resetSentAudioFrames();
        srsFlvMuxer3.resetSentAudioFrames();
    }

    @Override
    public void resetSentVideoFrames() {
        srsFlvMuxer1.resetSentVideoFrames();
        srsFlvMuxer2.resetSentVideoFrames();
        srsFlvMuxer3.resetSentVideoFrames();
    }

    @Override
    public void resetDroppedAudioFrames() {
        srsFlvMuxer1.resetDroppedAudioFrames();
        srsFlvMuxer2.resetDroppedAudioFrames();
        srsFlvMuxer3.resetDroppedAudioFrames();
    }

    @Override
    public void resetDroppedVideoFrames() {
        srsFlvMuxer1.resetDroppedVideoFrames();
        srsFlvMuxer2.resetDroppedVideoFrames();
        srsFlvMuxer3.resetDroppedVideoFrames();
    }

    @Override
    public void setAuthorization(String user, String password) {
        srsFlvMuxer1.setAuthorization(user, password);
        srsFlvMuxer2.setAuthorization(user, password);
        srsFlvMuxer3.setAuthorization(user, password);
    }

    /**
     * Some Livestream hosts use Akamai auth that requires RTMP packets to be sent with increasing
     * timestamp order regardless of packet type.
     * Necessary with Servers like Dacast.
     * More info here:
     * https://learn.akamai.com/en-us/webhelp/media-services-live/media-services-live-encoder-compatibility-testing-and-qualification-guide-v4.0/GUID-F941C88B-9128-4BF4-A81B-C2E5CFD35BBF.html
     */
    public void forceAkamaiTs(boolean enabled) {
        srsFlvMuxer1.forceAkamaiTs(enabled);
        srsFlvMuxer2.forceAkamaiTs(enabled);
        srsFlvMuxer3.forceAkamaiTs(enabled);
    }

    @Override
    protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
        srsFlvMuxer1.setIsStereo(isStereo);
        srsFlvMuxer1.setSampleRate(sampleRate);

        srsFlvMuxer2.setIsStereo(isStereo);
        srsFlvMuxer2.setSampleRate(sampleRate);

        srsFlvMuxer3.setIsStereo(isStereo);
        srsFlvMuxer3.setSampleRate(sampleRate);

    }

    @Override
    protected void startStreamRtp(String url) {
        this.streamForTwo = false;
        if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
            srsFlvMuxer1.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
        } else {
            srsFlvMuxer1.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
        }
        srsFlvMuxer1.start(url);
    }

    protected void startStreamRtp(String url, String url2) {
        this.streamForTwo = true;
        if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
            srsFlvMuxer1.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
            srsFlvMuxer2.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
        } else {
            srsFlvMuxer1.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
            srsFlvMuxer2.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
        }
        srsFlvMuxer1.start(url);
        srsFlvMuxer2.start(url2);
    }

    protected void startStreamRtp(String url, String url2, String url3) {
        this.streamForThree = true;
        if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
            srsFlvMuxer1.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
            srsFlvMuxer2.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
            srsFlvMuxer3.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
        } else {
            srsFlvMuxer1.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
            srsFlvMuxer2.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
            srsFlvMuxer3.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
        }
        srsFlvMuxer1.start(url);
        srsFlvMuxer2.start(url2);
        srsFlvMuxer3.start(url3);
    }

    @Override
    protected void stopStreamRtp() {
        if (srsFlvMuxer1.isConnected()) srsFlvMuxer1.stop();
        if (srsFlvMuxer2.isConnected()) srsFlvMuxer2.stop();
        if (srsFlvMuxer3.isConnected()) srsFlvMuxer3.stop();
    }

    @Override
    public void setReTries(int reTries) {
        if (streamForTwo) srsFlvMuxer2.setReTries(reTries);
        else if (streamForThree) srsFlvMuxer3.setReTries(reTries);
        else srsFlvMuxer1.setReTries(reTries);
    }


    @Override
    protected boolean shouldRetry(String reason) {
        boolean shouldRetry = false;
        if (streamForTwo) {
            shouldRetry = srsFlvMuxer1.shouldRetry(reason) && srsFlvMuxer2.shouldRetry(reason);
        } else if (streamForThree) {
            shouldRetry = srsFlvMuxer1.shouldRetry(reason) && srsFlvMuxer2.shouldRetry(reason) && srsFlvMuxer3.shouldRetry(reason);
        } else {
            shouldRetry = srsFlvMuxer1.shouldRetry(reason);
        }
        return shouldRetry;
    }

    @Override
    public void reConnect(long delay, @Nullable String backupUrl) {
        if (streamForTwo) srsFlvMuxer2.reConnect(delay, backupUrl);
        else if (streamForThree) srsFlvMuxer3.reConnect(delay, backupUrl);
        else srsFlvMuxer1.reConnect(delay, backupUrl);
    }

    @Override
    public boolean hasCongestion() {
        boolean hasCongestion = false;
        if (streamForTwo) {
            hasCongestion = srsFlvMuxer1.hasCongestion() && srsFlvMuxer2.hasCongestion();
        } else if (streamForThree) {
            hasCongestion = srsFlvMuxer1.hasCongestion() && srsFlvMuxer2.hasCongestion() && srsFlvMuxer3.hasCongestion();
        } else {
            hasCongestion = srsFlvMuxer2.hasCongestion();
        }
        return hasCongestion;
    }

    @Override
    protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
        if (streamForTwo) {
            srsFlvMuxer1.sendAudio(aacBuffer.duplicate(), info);
            srsFlvMuxer2.sendAudio(aacBuffer, info);
        } else if (streamForThree) {
            srsFlvMuxer1.sendAudio(aacBuffer.duplicate(), info);
            srsFlvMuxer2.sendAudio(aacBuffer, info);
            srsFlvMuxer3.sendAudio(aacBuffer, info);
        } else srsFlvMuxer1.sendAudio(aacBuffer, info);
    }

    @Override
    protected void onSpsPpsVpsRtp(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
        if (streamForTwo) srsFlvMuxer2.setSpsPPs(sps, pps);
        else if (streamForThree) srsFlvMuxer3.setSpsPPs(sps, pps);
        else srsFlvMuxer1.setSpsPPs(sps, pps);
    }

    @Override
    protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
        if (streamForTwo) srsFlvMuxer2.sendVideo(h264Buffer, info);
        else if (streamForThree) srsFlvMuxer3.sendVideo(h264Buffer, info);
        else srsFlvMuxer1.sendVideo(h264Buffer, info);
    }

    @Override
    public void setLogs(boolean enable) {
        if (streamForTwo) srsFlvMuxer2.setLogs(enable);
        else if (streamForThree) srsFlvMuxer3.setLogs(enable);
        else srsFlvMuxer1.setLogs(enable);
    }
}

package com.pedro.rtplibrary.rtmp;

import android.content.Context;
import android.media.MediaCodec;
import android.os.Build;
import android.util.Log;


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

    private SrsFlvMuxer srsFlvMuxer;
    private SrsFlvMuxer srsFlvMuxer1;
    private boolean isMultiStream = false;

    public RtmpDisplay(Context context, boolean useOpengl, ConnectCheckerRtmp connectChecker) {
        super(context, useOpengl);
        srsFlvMuxer = new SrsFlvMuxer(connectChecker);
        srsFlvMuxer1 = new SrsFlvMuxer(connectChecker);
    }

    /**
     * H264 profile.
     *
     * @param profileIop Could be ProfileIop.BASELINE or ProfileIop.CONSTRAINED
     */
    public void setProfileIop(byte profileIop) {
        srsFlvMuxer.setProfileIop(profileIop);
        srsFlvMuxer1.setProfileIop(profileIop);
    }

    @Override
    public void resizeCache(int newSize) throws RuntimeException {
        srsFlvMuxer.resizeFlvTagCache(newSize);
        srsFlvMuxer1.resizeFlvTagCache(newSize);
    }

    @Override
    public int getCacheSize() {
        return srsFlvMuxer.getFlvTagCacheSize();
    }

    @Override
    public long getSentAudioFrames() {
        return srsFlvMuxer.getSentAudioFrames() + srsFlvMuxer1.getSentAudioFrames();
    }

    @Override
    public long getSentVideoFrames() {
        return srsFlvMuxer.getSentVideoFrames() + srsFlvMuxer1.getSentVideoFrames();
    }

    @Override
    public long getDroppedAudioFrames() {
        return srsFlvMuxer.getDroppedAudioFrames() + srsFlvMuxer1.getDroppedAudioFrames();
    }

    @Override
    public long getDroppedVideoFrames() {
        return srsFlvMuxer.getDroppedVideoFrames() + srsFlvMuxer1.getDroppedVideoFrames();
    }

    @Override
    public void resetSentAudioFrames() {
        srsFlvMuxer.resetSentAudioFrames();
        srsFlvMuxer1.resetSentAudioFrames();
    }

    @Override
    public void resetSentVideoFrames() {
        srsFlvMuxer.resetSentVideoFrames();
        srsFlvMuxer1.resetSentVideoFrames();
    }

    @Override
    public void resetDroppedAudioFrames() {
        srsFlvMuxer.resetDroppedAudioFrames();
        srsFlvMuxer1.resetDroppedAudioFrames();
    }

    @Override
    public void resetDroppedVideoFrames() {
        srsFlvMuxer.resetDroppedVideoFrames();
        srsFlvMuxer1.resetDroppedVideoFrames();
    }

    @Override
    public void setAuthorization(String user, String password) {
        srsFlvMuxer.setAuthorization(user, password);
        srsFlvMuxer1.setAuthorization(user, password);
    }

    /**
     * Some Livestream hosts use Akamai auth that requires RTMP packets to be sent with increasing
     * timestamp order regardless of packet type.
     * Necessary with Servers like Dacast.
     * More info here:
     * https://learn.akamai.com/en-us/webhelp/media-services-live/media-services-live-encoder-compatibility-testing-and-qualification-guide-v4.0/GUID-F941C88B-9128-4BF4-A81B-C2E5CFD35BBF.html
     */
    public void forceAkamaiTs(boolean enabled) {
        srsFlvMuxer.forceAkamaiTs(enabled);
    }

    @Override
    protected void prepareAudioRtp(boolean isStereo, int sampleRate) {
        srsFlvMuxer.setIsStereo(isStereo);
        srsFlvMuxer.setSampleRate(sampleRate);

        srsFlvMuxer1.setIsStereo(isStereo);
        srsFlvMuxer1.setSampleRate(sampleRate);

    }

    @Override
    protected void startStreamRtp(String url) {
        this.isMultiStream = false;
        if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
            srsFlvMuxer.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
        } else {
            srsFlvMuxer.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
        }
        srsFlvMuxer.start(url);
    }

    protected void startStreamRtp(String url, String url2) {
        this.isMultiStream = true;
        if (videoEncoder.getRotation() == 90 || videoEncoder.getRotation() == 270) {
            srsFlvMuxer.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
            srsFlvMuxer1.setVideoResolution(videoEncoder.getHeight(), videoEncoder.getWidth());
        } else {
            srsFlvMuxer.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
            srsFlvMuxer1.setVideoResolution(videoEncoder.getWidth(), videoEncoder.getHeight());
        }
        srsFlvMuxer.start(url);
        srsFlvMuxer1.start(url2);
    }

    @Override
    protected void stopStreamRtp() {
        srsFlvMuxer.stop();
        if (srsFlvMuxer1.isConnected()) srsFlvMuxer1.stop();
    }

    @Override
    public void setReTries(int reTries) {
        srsFlvMuxer.setReTries(reTries);
        srsFlvMuxer1.setReTries(reTries);
    }


    @Override
    protected boolean shouldRetry(String reason) {
        return srsFlvMuxer.shouldRetry(reason);
    }

    @Override
    public void reConnect(long delay, @Nullable String backupUrl) {
        srsFlvMuxer.reConnect(delay, backupUrl);
        srsFlvMuxer1.reConnect(delay, backupUrl);
    }

    @Override
    public boolean hasCongestion() {
        return srsFlvMuxer.hasCongestion();
    }

    @Override
    protected void getAacDataRtp(ByteBuffer aacBuffer, MediaCodec.BufferInfo info) {
        if (isMultiStream) {
            srsFlvMuxer.sendAudio(aacBuffer.duplicate(), info);
            srsFlvMuxer1.sendAudio(aacBuffer, info);
        } else srsFlvMuxer.sendAudio(aacBuffer, info);
    }

    @Override
    protected void onSpsPpsVpsRtp(ByteBuffer sps, ByteBuffer pps, ByteBuffer vps) {
        srsFlvMuxer.setSpsPPs(sps, pps);
        srsFlvMuxer1.setSpsPPs(sps, pps);
    }

    @Override
    protected void getH264DataRtp(ByteBuffer h264Buffer, MediaCodec.BufferInfo info) {
        srsFlvMuxer.sendVideo(h264Buffer, info);
        srsFlvMuxer1.sendVideo(h264Buffer, info);
    }

    @Override
    public void setLogs(boolean enable) {
        srsFlvMuxer.setLogs(enable);
        srsFlvMuxer1.setLogs(enable);
    }
}

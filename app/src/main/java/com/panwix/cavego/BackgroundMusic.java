package com.panwix.cavego;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

public class BackgroundMusic {
    
    private static final String TAG = "Bg_Music";
    private float mLeftVolume;
    private float mRightVolume;
    private Context mContext;
    private MediaPlayer mBackgroundMediaPlayer;
    private boolean mIsPaused;
    private String mCurrentPath;

    public BackgroundMusic(Context context){
        this.mContext = context;
        initData();
    }
    //��ʼ��һЩ����
    private void initData(){
            mLeftVolume =0.5f;
            mRightVolume = 0.5f;
            mBackgroundMediaPlayer = null;
            mIsPaused = false;
            mCurrentPath = null;
    }
    
    /**
     * ����path·�����ű�������
     * @param path :assets�е���Ƶ·��
     * @param isLoop  :�Ƿ�ѭ������
     */
    public void playBackgroundMusic(String path, boolean isLoop){
        if (mCurrentPath == null){
            //���ǵ�һ�β��ű�������--- it is the first time to play background music
            //������ִ��end()���������±���---or end() was called
            mBackgroundMediaPlayer = createMediaplayerFromAssets(path);    
            mCurrentPath = path;
        } 
        else {
            if (! mCurrentPath.equals(path)){
                //����һ���µı�������--- play new background music
                //�ͷžɵ���Դ������һ���µ�----release old resource and create a new one
                if (mBackgroundMediaPlayer != null){
                    mBackgroundMediaPlayer.release();                
                }                
                mBackgroundMediaPlayer = createMediaplayerFromAssets(path);
                //��¼���·��---record the path
                mCurrentPath = path;
              }
        }
        
        if (mBackgroundMediaPlayer == null){
            Log.e(TAG, "playBackgroundMusic: background media player is null");
        } else {        
            // �����������ڲ��Ż��ѽ��жϣ�ֹͣ��---if the music is playing or paused, stop it
            mBackgroundMediaPlayer.stop();            
            mBackgroundMediaPlayer.setLooping(isLoop);            
            try {
                mBackgroundMediaPlayer.prepare();
                mBackgroundMediaPlayer.seekTo(0);
                mBackgroundMediaPlayer.start();                
                this.mIsPaused = false;
            } catch (Exception e){
                Log.e(TAG, "playBackgroundMusic: error state");
            }            
        }
    }
    
    /**
     * ֹͣ���ű�������
     */
    public void stopBackgroundMusic(){
        if (mBackgroundMediaPlayer != null){
            mBackgroundMediaPlayer.stop();
            // should set the state, if not , the following sequence will be error
            // play -> pause -> stop -> resume
            this.mIsPaused = false;
        }
    }
    /**
     * ��ͣ���ű�������
     */
    public void pauseBackgroundMusic(){        
        if (mBackgroundMediaPlayer != null && mBackgroundMediaPlayer.isPlaying()){
            mBackgroundMediaPlayer.pause();
            this.mIsPaused = true;
        }
    }
    /**
     * �������ű�������
     */
    public void resumeBackgroundMusic(){
        if (mBackgroundMediaPlayer != null && this.mIsPaused){
            mBackgroundMediaPlayer.start();
            this.mIsPaused = false;
        }
    }
    /**
     * ���²��ű�������
     */
    public void rewindBackgroundMusic(){        
        if (mBackgroundMediaPlayer != null){
            mBackgroundMediaPlayer.stop();            
            try {
                mBackgroundMediaPlayer.prepare();
                mBackgroundMediaPlayer.seekTo(0);
                mBackgroundMediaPlayer.start();
                this.mIsPaused = false;
            } catch (Exception e){
                Log.e(TAG, "rewindBackgroundMusic: error state");
            }            
        }
    }
    /**
     * �жϱ��������Ƿ����ڲ���
     * @return�����ص�booleanֵ�����Ƿ����ڲ���
     */
    public boolean isBackgroundMusicPlaying(){
        boolean ret = false;
        if (mBackgroundMediaPlayer == null){
            ret = false;
        } else {
            ret = mBackgroundMediaPlayer.isPlaying();
        }
        return ret;
    }
    /**
     * �����������֣����ͷ���Դ
     */
    public void end(){
        if (mBackgroundMediaPlayer != null){
            mBackgroundMediaPlayer.release();
        }
        //���¡���ʼ�����ݡ�
        initData();
    }
    /**
     * �õ��������ֵġ�������
     * @return
     */
    public float getBackgroundVolume(){
        if (this.mBackgroundMediaPlayer != null){
            return (this.mLeftVolume + this.mRightVolume) / 2;
        } else {
            return 0.0f;
        }
    }
    /**
     * ���ñ������ֵ�����
     * @param volume�����ò��ŵ�������float����
     */
    public void setBackgroundVolume(float volume){
        this.mLeftVolume = this.mRightVolume = volume;
        if (this.mBackgroundMediaPlayer != null){
            this.mBackgroundMediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
        }
    }
    /**
     * create mediaplayer for music
     * @param path the path relative to assets
     * @return 
     */
    private MediaPlayer createMediaplayerFromAssets(String path){
        MediaPlayer mediaPlayer = null;
        try{            
            AssetFileDescriptor assetFileDescritor = mContext.getAssets().openFd(path);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(assetFileDescritor.getFileDescriptor(), 
                    assetFileDescritor.getStartOffset(), assetFileDescritor.getLength());
            mediaPlayer.prepare();
            mediaPlayer.setVolume(mLeftVolume, mRightVolume);
        }catch (Exception e) {
            mediaPlayer = null;
            Log.e(TAG, "error: " + e.getMessage(), e);
        }
        return mediaPlayer;
    }
}
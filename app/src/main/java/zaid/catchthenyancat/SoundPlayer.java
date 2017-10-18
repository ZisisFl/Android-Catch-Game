package zaid.catchthenyancat;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundPlayer {

    private static SoundPool soundPool;
    private static int hitSound;
    private static int overSound;

    public SoundPlayer(Context context)
    {
        //SoundPool (int maxStreams,int streamType,int srcQuality)
        soundPool = new SoundPool (2, AudioManager.STREAM_MUSIC,0);

        hitSound = soundPool.load(context, R.raw.hit, 1);
    }

    public void playhitSound()
    {
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}

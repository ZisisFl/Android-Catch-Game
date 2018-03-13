package zaid.catchthenyancat;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;


public class SoundPlayer {

    private static SoundPool soundPool;
    private static int hitSound;
    private static int time_bonusSound;

    public float master_volume = 1.0f;

    public SoundPlayer(Context context)
    {
        //SoundPool (int maxStreams,int streamType,int srcQuality)
        soundPool = new SoundPool (2, AudioManager.STREAM_MUSIC,0);

        hitSound = soundPool.load(context, R.raw.hit, 1);
        time_bonusSound = soundPool.load(context, R.raw.time_bonus, 1);
    }

    public void playhitSound()
    {
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(hitSound, master_volume, master_volume, 1, 0, 1.0f);
    }

    public void playtimebonusSound()
    {
        //play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(time_bonusSound, master_volume, master_volume, 1, 0, 1.0f);
    }

    public void muteSounds()
    {
        master_volume = 0.0f;
    }

    public void unmuteSounds()
    {
        master_volume = 1.0f;
    }
}

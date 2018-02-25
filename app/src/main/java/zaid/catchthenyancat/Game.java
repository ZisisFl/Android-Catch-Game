package zaid.catchthenyancat;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Game extends AppCompatActivity {

    //initialization of objects textviews, imageviews, button
    TextView time_text;
    TextView current_score_view;
    TextView high_score_view;
    TextView combo_view;
    TextView time_bonus;

    ImageView imgclick;
    ImageView pause_img;
    ImageView pause_screen;
    ImageView sound_img;
    ImageView timer_image;

    Button restart_button;

    ConstraintLayout  con;

    //initialization of classes
    DisplayMetrics metrics = new DisplayMetrics();
    Random rand = new Random();
    Handler handler = new Handler();
    Timer clocktimer = new Timer();
    SoundPlayer sound;


    int combo = 0;
    int count = 0;
    int highscore;
    int score;
    int ball_speed = 500;

    //character's coordinates
    int x;
    int y;

    //screen width and height
    int w;
    int h;
    int timeleft = 60;

    //flags to check if buttons clicked
    boolean clicked = false;
    boolean pause_flag = false;
    boolean sound_flag = false;
    boolean gamestopped = false;

    float dp;
    int pixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sound = new SoundPlayer(this);

        time_text = (TextView) findViewById(R.id.time_text);
        current_score_view = (TextView) findViewById(R.id.current_score_view);
        high_score_view = (TextView) findViewById(R.id.high_score_view);
        combo_view = (TextView) findViewById(R.id.combo_view);
        time_bonus = (TextView) findViewById(R.id.time_bonus);

        pause_screen = (ImageView) findViewById(R.id.pause_screen);
        imgclick = (ImageView) findViewById(R.id.ball);
        pause_img = (ImageView) findViewById(R.id.pause_img);
        sound_img = (ImageView) findViewById(R.id.sound_img);
        timer_image = (ImageView) findViewById(R.id.timer_image);

        con = (ConstraintLayout)findViewById(R.id.con_layout);


        mute_sound();
        clickevent();
        pauseclick();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);//screen height width

        restart_game();//restarts game if restart button is clicked

        //timer for game clock
        clocktimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        gameClock();
                    }
                });
            }
        }, 0, 1000);

        //timer for ball's movement
        clocktimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        movementClock();
                    }
                });
            }
        }, 0, ball_speed);
    }

    public void onStart()
    {
        super.onStart();

    }

    public void onPause()
    {
        super.onPause(); //Stops the timer and store time remaining in a variable.

    }

    public void onResume()
    {
        //Start a new timer with the time remaining in the beforementioned variable
        super.onResume();
    }

    public void gameClock() //timer for the game
    {
        if (timeleft > 0)
        {
            timeleft--;
            time_text.setText(timeleft + "s");
        }

        if (timeleft == 0) //end of the game
        {
            time_text.setText("Time is up!");
            imgclick.setVisibility(View.INVISIBLE);
            combo_view.setVisibility(View.INVISIBLE);
            restart_button.setVisibility(View.VISIBLE);
            high_score_view.setVisibility(View.VISIBLE);
            pause_img.setVisibility(View.INVISIBLE);
            sound_img.setVisibility(View.INVISIBLE);
            timer_image.setVisibility(View.INVISIBLE);

            score = count;
            score_save();
        }
    }

    public void movementClock() //clock for the ball to move around
    {
        //screen metrics
        w = metrics.widthPixels;
        h = metrics.heightPixels;

        x = rand.nextInt(w-140);//generate random x (0<=x<=max width) 65size of pic
        y = rand.nextInt((h-200) - 100) + 100;//generate random y (0<=x<=max height)


        imgclick.setX(x);
        imgclick.setY(y);

        check_combo();
    }

    public void clickevent() //clickevent for ball image
    {
        imgclick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (gamestopped == false)
                {
                    count++;
                    levels();
                    clicked = true;
                    sound.playhitSound();// calls the fuction for the click sound
                    current_score_view.setText(""+count);
                }
            }
        });
    }

    public void pauseclick() //pause game fuction
    {
        pause_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (pause_flag == false)
                {
                    pause_flag = true;
                    //Stop timer
                    clocktimer.cancel();
                    clocktimer = null;

                    //Change image to play
                    pause_img.setImageResource(R.drawable.play);

                    pause_screen.setVisibility(View.VISIBLE);

                    gamestopped = true;
                }
                else
                {
                    pause_flag = false;

                    //change image to pause
                    pause_img.setImageResource(R.drawable.pause);

                    gamestopped = false;

                    pause_screen.setVisibility(View.GONE);

                    //create and start the timer
                    clocktimer = new Timer();
                    clocktimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    gameClock();
                                }
                            });
                        }
                    }, 0, 1000);


                    clocktimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    movementClock();
                                }
                            });
                        }
                    }, 0, 500);
                }
            }
        });
    }

    public void mute_sound()
    {
        sound_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (sound_flag == false)
                {
                    sound_flag = true;

                    //Mute sounds
                    sound.muteSounds();

                    //Change image to mute
                    sound_img.setImageResource(R.drawable.mute);
                }
                else
                {
                    sound_flag = false;

                    //Unmute sounds
                    sound.unmuteSounds();

                    //change image to on
                    sound_img.setImageResource(R.drawable.on);
                }
            }
        });
    }

    public void restart_game() //call fuction recreate with button click
    {
        restart_button = (Button) findViewById(R.id.restart_button);
        restart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                recreate();
            }
        });
    }

    public void score_save() //saves high score only
    {
        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highscore = settings.getInt("HIGH_SCORE", 0);

        if (score > highscore)
        {
            high_score_view.setText("Top Score: "+ score);

            //Save
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.commit();
        }
        else
        {
            high_score_view.setText("Top Score: "+ highscore);
        }
    }

    public void check_combo() //fuction for the combo
    {
        if (clicked == true)
        {
            combo++;
            combo_view.setText("Combo: "+combo);
            clicked = false;

            //give extra time
            if (combo == 5)
            {
                timeleft = timeleft + 2;
                sound.playtimebonusSound();
                time_bonus.setText("+ " + 2);
                time_bonus.setVisibility(View.VISIBLE);
                time_bonus.postDelayed(new Runnable() {
                    public void run() {
                        time_bonus.setVisibility(View.INVISIBLE);
                    }
                }, 1000);

            }
            else if (combo == 10)
            {
                timeleft = timeleft + 3;
                time_bonus.setText("+ " + 3);
                time_bonus.setVisibility(View.VISIBLE);
                time_bonus.postDelayed(new Runnable() {
                    public void run() {
                        time_bonus.setVisibility(View.INVISIBLE);
                    }
                }, 1000);
            }
            else if (combo == 15)
            {
                timeleft = timeleft + 5;
                time_bonus.setText("+ " + 5);
                time_bonus.setVisibility(View.VISIBLE);
                time_bonus.postDelayed(new Runnable() {
                    public void run() {
                        time_bonus.setVisibility(View.INVISIBLE);
                    }
                }, 1000);
            }

        }
        else
        {
            combo = 0;
            combo_view.setText("Combo: "+combo);
        }
    }

    public void levels()
    {
        Resources r = getResources();

        if (count == 1)
        {
            dp = 60;
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            ball_speed = ball_speed - 50;
            timeleft = timeleft + 30;
            imgclick.setImageResource(R.drawable.basketball);
            imgclick.getLayoutParams().height = Math.round(pixels);
            imgclick.getLayoutParams().width = Math.round(pixels);
            imgclick.requestLayout();
            con.setBackgroundResource(R.color.red);
        }
        else if (count == 2)
        {
            dp = 55;
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            ball_speed = ball_speed - 50;
            timeleft = timeleft + 30;
            imgclick.setImageResource(R.drawable.bowling);
            imgclick.getLayoutParams().height = Math.round(pixels);
            imgclick.getLayoutParams().width = Math.round(pixels);
            imgclick.requestLayout();
            con.setBackgroundResource(R.color.red);
        }
        else if (count == 3)
        {
            dp = 50;
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            ball_speed = ball_speed - 50;
            timeleft = timeleft + 30;
            imgclick.setImageResource(R.drawable.volleyball);
            imgclick.getLayoutParams().height = Math.round(pixels);
            imgclick.getLayoutParams().width = Math.round(pixels);
            imgclick.requestLayout();
            con.setBackgroundResource(R.color.red);
        }
        else if (count == 4)
        {
            dp = 45;
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            ball_speed = ball_speed - 50;
            timeleft = timeleft + 30;
            imgclick.setImageResource(R.drawable.baseball);
            imgclick.getLayoutParams().height = Math.round(pixels);
            imgclick.getLayoutParams().width = Math.round(pixels);
            imgclick.requestLayout();
            con.setBackgroundResource(R.color.red);
        }
        else if (count == 5)
        {
            dp = 40;
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            ball_speed = ball_speed - 50;
            timeleft = timeleft + 30;
            imgclick.setImageResource(R.drawable.tennisball);
            imgclick.getLayoutParams().height = Math.round(pixels);
            imgclick.getLayoutParams().width = Math.round(pixels);
            imgclick.requestLayout();
            con.setBackgroundResource(R.color.red);
        }
        else if (count == 6)
        {
            dp = 35;
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            ball_speed = ball_speed - 50;
            timeleft = timeleft + 30;
            imgclick.setImageResource(R.drawable.eightball);
            imgclick.getLayoutParams().height = Math.round(pixels);
            imgclick.getLayoutParams().width = Math.round(pixels);
            imgclick.requestLayout();
            con.setBackgroundResource(R.color.red);
        }
        else if (count == 7)
        {
            dp = 30;
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            ball_speed = ball_speed - 50;
            timeleft = timeleft + 30;
            imgclick.setImageResource(R.drawable.golfball);
            imgclick.getLayoutParams().height = Math.round(pixels);
            imgclick.getLayoutParams().width = Math.round(pixels);
            imgclick.requestLayout();
            con.setBackgroundResource(R.color.red);
        }
        else if (count == 8)
        {
            dp = 25;
            float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
            ball_speed = ball_speed - 50;
            timeleft = timeleft + 30;
            imgclick.setImageResource(R.drawable.shuttlecock);
            imgclick.getLayoutParams().height = Math.round(pixels);
            imgclick.getLayoutParams().width = Math.round(pixels);
            imgclick.requestLayout();
            con.setBackgroundResource(R.color.red);
        }
    }
}

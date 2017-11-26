package zaid.catchthenyancat;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class Game extends AppCompatActivity {

    TextView time_text;
    TextView current_score_view;
    TextView high_score_view;
    TextView combo_view;
    TextView time_bonus;

    ImageView imgclick;
    ImageView bonusclick;
    ImageView pause_img;
    ImageView pause_screen;

    Button restart_button;

    //initialize class
    DisplayMetrics metrics = new DisplayMetrics();
    Random rand = new Random();
    Random r = new Random();
    Handler handler = new Handler();
    Timer clocktimer = new Timer();
    SoundPlayer sound;

    int combo = 0;
    int count = 0;
    int highscore;
    int score;

    //character's coordinates
    int x;
    int y;

    //bonus' coordinates
    int x_b;
    int y_b;
    //screen width and height
    int w;
    int h;
    int timeleft = 60;

    //flags to check if buttons clicked
    boolean clicked = false;
    boolean pause_flag = false;
    boolean gamestopped = false;
    boolean bonus_clicked = false;


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
        imgclick = (ImageView) findViewById(R.id.nyancat);
        bonusclick = (ImageView) findViewById(R.id.bonusclick);
        pause_img = (ImageView) findViewById(R.id.pause_img);

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

        //timer for characters movement
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

    public void onStart()
    {
        super.onStart();

    }

    public void onPause()
    {
        super.onPause();
        //Stop the timer and store time remaining in a variable.
    }

    public void onResume()
    {
        super.onResume();
        //Start a new timer with the time remaining in the
        // aforementioned variable (of course silly, only if not zero)
    }

    public void gameClock()
    {
        if (timeleft > 0)
        {
            timeleft--;
            time_text.setText("Time: " + timeleft);
        }

        if (timeleft == 0)
        {
            time_text.setText("Time is up!");
            imgclick.setVisibility(View.INVISIBLE);//make nyan cat invisible
            combo_view.setVisibility(View.INVISIBLE);
            restart_button.setVisibility(View.VISIBLE);
            high_score_view.setVisibility(View.VISIBLE);
            pause_img.setVisibility(View.INVISIBLE);

            score = count;
            score_save();
        }
    }

    public void movementClock()
    {
        w = metrics.widthPixels;
        h = metrics.heightPixels;

        x = rand.nextInt(w-140);//generate random x (0<=x<=max width) 65size of pic
        y = rand.nextInt((h-200) - 100) + 100;//generate random x (0<=x<=max height)

        imgclick.setX(x);
        imgclick.setY(y);

        check_combo();
    }

    public void clickevent() {
        imgclick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //if (gamestopped == false)
                {
                    count++;
                    clicked = true;
                    sound.playhitSound();
                    current_score_view.setText("Score: " + count);
                }
            }
        });
    }

    public void pauseclick()
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
                    pause_img.setImageResource(R.drawable.ic_play_arrow);

                    pause_screen.setVisibility(View.VISIBLE);

                    gamestopped = true;
                }
                else
                {
                    pause_flag = false;

                    //change image to pause
                    pause_img.setImageResource(R.drawable.ic_pause);

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

    public void restart_game()
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

    public void score_save()
    {
        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highscore = settings.getInt("HIGH_SCORE", 0);

        if (score > highscore)
        {
            high_score_view.setText("Top: "+ score);

            //Save
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", score);
            editor.commit();
        }
        else
        {
            high_score_view.setText("Top: "+ highscore);
        }
    }

    public void check_combo()
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
            combo=0;
            combo_view.setText("Combo: "+combo);
        }
    }

    public void bonus()
    {
        x_b = r.nextInt(w-140);
        y_b = r.nextInt((h-200) - 100) + 100;

        bonusclick.setX(x_b);
        bonusclick.setY(y_b);

        //set bonus visible for 3 seconds
        bonusclick.setVisibility(View.VISIBLE);
        bonusclick.postDelayed(new Runnable() {
            public void run() {
                bonusclick.setVisibility(View.INVISIBLE);
            }
        }, 3000);
    }

}

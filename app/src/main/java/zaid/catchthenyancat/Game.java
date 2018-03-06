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
    TextView coins_text;

    ImageView imgclick;
    ImageView pause_img;
    ImageView pause_screen;
    ImageView sound_img;
    ImageView timer_image;
    ImageView extra_coins;

    Button restart_button;
    Button resume_button;

    ConstraintLayout  con;

    //initialization of classes
    DisplayMetrics metrics = new DisplayMetrics();
    Random rand = new Random();
    Handler handler = new Handler();
    Timer clocktimer = new Timer();
    Timer gametimer = new Timer();
    SoundPlayer sound;


    int combo = 0;
    int count = 0;
    int highscore;
    int score;
    int ball_speed = 600;//starting speed

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

    int coins = 200;
    boolean resume_flag = true;

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
        coins_text = (TextView) findViewById(R.id.coins);

        pause_screen = (ImageView) findViewById(R.id.pause_screen);
        imgclick = (ImageView) findViewById(R.id.ball);
        pause_img = (ImageView) findViewById(R.id.pause_img);
        sound_img = (ImageView) findViewById(R.id.sound_img);
        timer_image = (ImageView) findViewById(R.id.timer_image);
        extra_coins = (ImageView) findViewById(R.id.extra_coins);

        restart_button = (Button) findViewById(R.id.restart_button);
        resume_button = (Button) findViewById(R.id.resume_button);

        con = (ConstraintLayout)findViewById(R.id.con_layout);


        mute_sound();
        clickevent();
        pauseclick();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);//screen height width

        resume_game();
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
        gametimer.schedule(new TimerTask() {
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
            time_text.setText("");
            coins_text.setText("Coins: "+coins);

            imgclick.setVisibility(View.INVISIBLE);
            combo_view.setVisibility(View.INVISIBLE);
            restart_button.setVisibility(View.VISIBLE);
            resume_button.setVisibility(View.VISIBLE);
            coins_text.setVisibility(View.VISIBLE);
            high_score_view.setVisibility(View.VISIBLE);
            pause_img.setVisibility(View.INVISIBLE);
            sound_img.setVisibility(View.INVISIBLE);
            timer_image.setVisibility(View.INVISIBLE);

            if (resume_flag == false)
                resume_button.setVisibility(View.GONE);
            else
            {
                resume_button.setVisibility(View.VISIBLE);
                extra_coins.setVisibility(View.VISIBLE);
                coins = coins + (count/100);//earn the 1/100 of score as coins
            }


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
                    gametimer.cancel();
                    gametimer = null;

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
                    gametimer = new Timer();

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


                    gametimer.schedule(new TimerTask() {
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
        restart_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                recreate();
            }
        });
    }

    public void resume_game()
    {
        resume_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (coins >= 200 && resume_flag == true)
                {
                    resume_flag = false;
                    coins = coins - 200;

                    //restart game with prev score and 30 sec time
                    timeleft = 30;

                    clocktimer.cancel();
                    clocktimer = null;
                    gametimer.cancel();
                    gametimer = null;

                    clocktimer = new Timer();
                    gametimer = new Timer();

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


                    gametimer.schedule(new TimerTask() {
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

                    imgclick.setVisibility(View.VISIBLE);
                    restart_button.setVisibility(View.INVISIBLE);
                    resume_button.setVisibility(View.INVISIBLE);
                    coins_text.setVisibility(View.INVISIBLE);
                    high_score_view.setVisibility(View.INVISIBLE);
                    pause_img.setVisibility(View.VISIBLE);
                    sound_img.setVisibility(View.VISIBLE);
                    timer_image.setVisibility(View.VISIBLE);
                    resume_button.setVisibility(View.INVISIBLE);
                    extra_coins.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    public void score_save() //saves high score only
    {
        SharedPreferences settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highscore = settings.getInt("HIGH_SCORE", 0);

        if (score > highscore)
        {
            high_score_view.setText("Top "+ score);

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

    public void get_coins()
    {

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
                time_bonus(2);
            }
            else if (combo == 10)
            {
                time_bonus(3);
            }
            else if (combo == 15)
            {
                time_bonus(5);
            }
        }
        else
        {
            combo = 0;
            combo_view.setText("Combo: "+combo);
        }
    }

    public void time_bonus(int extra_time)
    {
        timeleft = timeleft + extra_time;
        sound.playtimebonusSound();
        time_bonus.setText("+ " + extra_time);
        time_bonus.setVisibility(View.VISIBLE);
        time_bonus.postDelayed(new Runnable() {
            public void run() {
                time_bonus.setVisibility(View.INVISIBLE);
            }
        }, 1000);
    }

    public void level_attributes(int dp,int ball_speed, String image_name, String color_name)
    {
        Resources r = getResources();
        //convert dp to pixels
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        //get extra time
        time_bonus(30);
        //change image source size and background color
        imgclick.setImageResource(getResources().getIdentifier(image_name, "drawable", getPackageName()));
        imgclick.getLayoutParams().height = Math.round(pixels);
        imgclick.getLayoutParams().width = Math.round(pixels);
        imgclick.requestLayout();
        con.setBackgroundResource(getResources().getIdentifier(color_name, "color", getPackageName()));

        //reset gametimer speed
        gametimer.cancel();
        gametimer = null;
        //accelarate gametimer speed
        gametimer = new Timer();

        gametimer.schedule(new TimerTask() {
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

    public void levels()
    {
        if (count == 100)
        {
            ball_speed = 600; //update variable in case of pausing game
            level_attributes(60, 600, "basketball", "basket");
        }
        else if (count == 200)
        {
            ball_speed = 550;
            level_attributes(55, 550, "bowling", "bowling");
        }
        else if (count == 300)
        {
            ball_speed = 550;
            level_attributes(50, 550, "volleyball", "volley");
        }
        else if (count == 400)
        {
            ball_speed = 500;
            level_attributes(45, 500, "baseball", "baseball");
        }
        else if (count == 500)
        {
            ball_speed = 500;
            level_attributes(40, 500, "tennisball", "tennis");
        }
        else if (count == 600)
        {
            ball_speed = 450;
            level_attributes(35, 450, "eightball", "billiard");
        }
        else if (count == 700)
        {
            ball_speed = 450;
            level_attributes(30, 450, "golfball", "golf");
        }
        else if (count == 800)
        {
            ball_speed = 350;
            level_attributes(30, 350, "shuttlecock", "shuttlecock");
        }
    }
}

package zaid.catchthenyancat;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import java.util.Random;


public class Game extends AppCompatActivity {

    TextView time_text;
    TextView current_score_view;
    TextView high_score_view;
    TextView combo_view;

    ImageView imgclick;
    ImageView bonusclick;
    ImageView pause_img;

    Button restart_button;

    //initialize class
    DisplayMetrics metrics = new DisplayMetrics();
    Random rand = new Random();
    Random r = new Random();
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
    int time;

    //flags to check if buttons clicked
    boolean clicked = false;
    boolean pause_flag = false;
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

        imgclick = (ImageView) findViewById(R.id.nyancat);
        bonusclick = (ImageView) findViewById(R.id.bonusclick);
        pause_img = (ImageView) findViewById(R.id.pause_img);

        clickevent();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);//screen height width

        restart_game();//restarts game if restart button is clicked
    }

    public void onStart()
    {
        super.onStart();

        game();
    }

    public void onPause()
    {
        super.onPause();
        //if pause button is clicked
        //else
        //Stop the timer and store time remaining in a variable.
    }

    public void onResume()
    {
        super.onResume();
        //if resume button is clicked
        //else
        //Start a new timer with the time remaining in the
        // aforementioned variable (of course silly, only if not zero)
    }

    public void game()
    {
        CountDownTimer timer = new CountDownTimer(60 * 1000, 1000){
            @Override
            public void onTick(long millisUntilFinish)
            {
                time_text.setText("Time: " + millisUntilFinish / 1000);
                //time++;
                //if (time == 10)
                //{
                //   bonus();
                //}
            }

            @Override
            public void onFinish()
            {
                time_text.setText("Time is up!");
                imgclick.setVisibility(View.INVISIBLE);//make nyan cat invisible
                restart_button.setVisibility(View.VISIBLE);
                high_score_view.setVisibility(View.VISIBLE);

                score = count;

                score_save();
            }
        };
        timer.start();

        final CountDownTimer speed_timer = new CountDownTimer(120 * 1000, 500){
            @Override
            public void onTick(long millisUntilFinish)
            {
                w = metrics.widthPixels;
                h = metrics.heightPixels;

                x = rand.nextInt(w-140);//generate random x (0<=x<=max width) 65size of pic
                y = rand.nextInt((h-200) - 100) + 100;//generate random x (0<=x<=max height)

                imgclick.setX(x);
                imgclick.setY(y);

                check_combo();
            }

            @Override
            public void onFinish()
            {

            }
        };
        speed_timer.start();
    }

    public void clickevent() {
        imgclick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                count++;
                clicked = true;
                sound.playhitSound();
                current_score_view.setText("Score: " + count);
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
                pause_flag = false;
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

    public void pausePushed(View view)
    {
        if (pause_flag == false)
        {
            pause_flag = true;
            //Stop timer

            //Change image to play

        }
        else
        {
            pause_flag = false;

            //change image to pause

            //create and start the timer

        }
    }

}

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

    Random rand = new Random();

    Button restart_button;

    DisplayMetrics metrics = new DisplayMetrics();

    int combo = 0;
    int count = 0;
    int highscore;
    int score;
    boolean clicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        time_text = (TextView) findViewById(R.id.time_text);
        current_score_view = (TextView) findViewById(R.id.current_score_view);
        high_score_view = (TextView) findViewById(R.id.high_score_view);
        combo_view = (TextView) findViewById(R.id.combo_view);

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

        CountDownTimer speed_timer = new CountDownTimer(120 * 1000, 500){
            @Override
            public void onTick(long millisUntilFinish)
            {
                int w = metrics.widthPixels;
                int h = metrics.heightPixels;

                int x = rand.nextInt(w-140);//generate random x (0<=x<=max width) 65size of pic
                int y = rand.nextInt((h-200) - 100) + 100;//generate random x (0<=x<=max height)

                imgclick.setX(x);
                imgclick.setY(y);

                if (clicked == true)
                {
                    combo++;
                    combo_view.setText("Combo:"+combo);
                    clicked = false;
                }
                else
                {
                    combo=0;
                    combo_view.setText("Combo:"+combo);
                }
            }

            @Override
            public void onFinish()
            {

            }
        };
        speed_timer.start();
    }

    public void clickevent() {
        imgclick = (ImageView) findViewById(R.id.nyancat);

        imgclick.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                count++;
                clicked = true;
                current_score_view.setText("Score: " + count);
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

}

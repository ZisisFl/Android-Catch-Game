package zaid.catchthenyancat;


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
    TextView score;
    TextView textView2;
    ImageView imgclick;
    Random rand = new Random();
    int count = 0;
    Button restart_button;
    DisplayMetrics metrics = new DisplayMetrics();
    int combo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        time_text = (TextView) findViewById(R.id.time_text);
        score = (TextView) findViewById(R.id.score);
        textView2 = (TextView) findViewById(R.id.textView2);

        clickevent();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);//screen height width

        restart_game();//restarts game if restart button is clicked
    }

    public void onResume()
    {
        super.onResume();

        CountDownTimer timer = new CountDownTimer(60 * 1000, 1000){
            @Override
            public void onTick(long millisUntilFinish)
            {
                time_text.setText("" + millisUntilFinish / 1000);
            }

            @Override
            public void onFinish()
            {
                time_text.setText("Time is up!");
                time_text.setX(0);
                textView2.setVisibility(View.INVISIBLE);
                imgclick.setVisibility(View.INVISIBLE);//make nyan cat invisible
                restart_button.setVisibility(View.VISIBLE);
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
                score.setText(""+count);
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

}

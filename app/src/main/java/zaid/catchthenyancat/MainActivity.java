package zaid.catchthenyancat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button start_button;
    TextView top_score;
    DatabaseHelper DH;

    public void init() {
        start_button = (Button) findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent nextact = new Intent(MainActivity.this, Game.class);
                startActivity(nextact);
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        top_score = (TextView) findViewById(R.id.top_score);
        DH = new DatabaseHelper(this, "", null, 1);

        DH.top(top_score);
    }
}

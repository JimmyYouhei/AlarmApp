package vn.org.quan.hong.nguyen.myalarmclock;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

import vn.org.quan.hong.nguyen.myalarmclock.MethodInterfaceEnum.Command;

public class AlarmTimePicker extends AppCompatActivity {
    private TextView title;
    private ImageButton btnBack;
    private Button btnAdd;
    Intent intentFromMainActivity;
    private android.widget.TimePicker timePicker;
    public static final String HOUR_KEY ="hour , key";
    public static final String MINUTE_KEY = "minute , key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        title = findViewById(R.id.txtTimeTitle);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd);
        timePicker = findViewById(R.id.timePicker);

        intentFromMainActivity = getIntent();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        if (intentFromMainActivity.getAction().equals(Intent.ACTION_INSERT)){
            title.setText("Add");
            btnAdd.setText("ADD");

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK , Command.extractIntentFromTimePicker(timePicker));
                    finish();
                }
            });

        } else if (intentFromMainActivity.getAction().equals(Intent.ACTION_EDIT)) {
            title.setText("Edit");
            btnAdd.setText("EDIT");

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editAlarm = Command.extractIntentFromTimePicker(timePicker);
                    Command.putExtraInformationAboutOldAlarm(intentFromMainActivity , editAlarm);
                    setResult(RESULT_OK , editAlarm);
                    finish();

                }
            });
        } else if (intentFromMainActivity.getAction().equals(Intent.ACTION_DELETE)){
            setResult(RESULT_OK , intentFromMainActivity);
            finish();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

}

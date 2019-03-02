/*
 * MIT License
 *
 * Copyright (c) 2019.  Jimmy Youhei(Quan Nguyen)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package vn.org.quan.hong.nguyen.myalarmclock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import vn.org.quan.hong.nguyen.myalarmclock.method_interface_enum.Command;


// Time Picker Activity
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

        // Tie the View to its place in layout
        title = findViewById(R.id.txtTimeTitle);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd);
        timePicker = findViewById(R.id.timePicker);

        //get intent detail from MainActivity
        intentFromMainActivity = getIntent();

        // On Back Image Icon pressed
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cancel the intent for result and get back to MainActivity
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // get intent action from MainActivity to determine the text of this Activity's title and button

        //case to add more alarm
        if (intentFromMainActivity.getAction().equals(Intent.ACTION_INSERT)){
            // set Text
            title.setText("Add");
            btnAdd.setText("ADD");

            // when click on the add Button
            //set Result Ok and send back result intent with data
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_OK , Command.extractIntentFromTimePicker(timePicker));
                    finish();
                }
            });

            //case edit an alarm
        } else if (intentFromMainActivity.getAction().equals(Intent.ACTION_EDIT)) {
            //set Text;
            title.setText("Edit");
            btnAdd.setText("EDIT");

            // when click on the edit Button
            // set Result ok the send back the result itent to edit the alarm
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editAlarm = Command.extractIntentFromTimePicker(timePicker);
                    Command.putExtraInformationAboutOldAlarm(intentFromMainActivity , editAlarm);
                    setResult(RESULT_OK , editAlarm);
                    finish();

                }
            });
            // if get the intent to delete then will still send the result intent back to delete
        } else if (intentFromMainActivity.getAction().equals(Intent.ACTION_DELETE)){
            setResult(RESULT_OK , intentFromMainActivity);
            finish();
        }


    }

    // Hard back Button like Back image Button above
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

}

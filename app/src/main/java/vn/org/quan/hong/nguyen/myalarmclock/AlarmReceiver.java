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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// BroadcastReceiver class to receive broadcast
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public static final String FROM_ALARM_RECEIVER_KEY = "from , AlarmReceiver";
    public static final String FROM_ALARM_RECEIVER_ADAPTER_KEY = "from , AlarmReciver , adapterPosition";

    @Override
    public void onReceive(Context context, Intent intent) {

        // pass forward the intent
        Intent alarm = new Intent(context , AlarmService.class);
        alarm.putExtra(FROM_ALARM_RECEIVER_KEY , intent.getStringExtra(AlarmAdapter.FROM_ALARM_ADAPTER_KEY));
        alarm.putExtra(AlarmAdapter.ADAPTER_ID_KEY , intent.getIntExtra(AlarmAdapter.ADAPTER_ID_KEY , -10));
        context.startService(alarm);

    }
}

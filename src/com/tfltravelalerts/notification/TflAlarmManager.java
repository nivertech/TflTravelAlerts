
package com.tfltravelalerts.notification;

import java.util.Date;

import org.holoeverywhere.app.Application;
import org.holoeverywhere.widget.Toast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tfltravelalerts.TflApplication;
import com.tfltravelalerts.alerts.events.AlertDeletedEvent;
import com.tfltravelalerts.alerts.events.AlertTriggerEvent;
import com.tfltravelalerts.alerts.events.AlertsUpdatedEvent;
import com.tfltravelalerts.model.LineStatusAlert;
import com.tfltravelalerts.model.LineStatusAlertSet;

/**
 * Class to setup alarms to refresh data according to alert times.
 */
public class TflAlarmManager {

    private Application mContext;
    private LineStatusAlertSet mLineStatusAlertSet;

    public TflAlarmManager() {
        mContext = TflApplication.getLastInstance();
    }

    public void onEventMainThread(AlertsUpdatedEvent event) {
        mLineStatusAlertSet = event.getData();
        setAlarms();
    }

    public void onEventMainThread(AlertTriggerEvent event) {
        setAlarms();
    }

    public void onEventMainThread(AlertDeletedEvent event) {
        removeAlarm(event.getData());
    }

    private void removeAlarm(LineStatusAlert alert) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = makePendingIntentForAlert(alert);
        alarmManager.cancel(pendingIntent);

        Toast.makeText(mContext, "removing alarm", Toast.LENGTH_SHORT).show();
    }

    private void setAlarms() {
        if(mLineStatusAlertSet == null) {
            return;
        }
        
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        long now = System.currentTimeMillis();
        for (LineStatusAlert alert : mLineStatusAlertSet.getAlerts()) {
            PendingIntent pendingIntent = makePendingIntentForAlert(alert);
            long triggerTime = alert.getNextAlertTime(now);
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);

            Toast.makeText(mContext, "setting alarm for " + new Date(triggerTime),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private PendingIntent makePendingIntentForAlert(LineStatusAlert alert) {
        Intent intent = new Intent(TflAlarmBroadcastReceiver.ALARM_ACTION);
        return PendingIntent.getBroadcast(mContext, alert.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
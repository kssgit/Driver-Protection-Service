package com.example.dps.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dps.notification.Constants;
import com.example.dps.notification.NotificationHelper;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class WorkerA extends Worker {
    public WorkerA(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationHelper mNotificationHelper = new NotificationHelper(getApplicationContext());
        long currentMillis = Calendar.getInstance(TimeZone.getTimeZone(Constants.KOREA_TIMEZONE), Locale.KOREA).getTimeInMillis();

        // 알림 범위(20:00-21:00)에 해당하는지 기준 설정
        Calendar eventCal = NotificationHelper.getScheduledCalender(Constants.A_NIGHT_EVENT_TIME);
        long nightNotifyMinRange = eventCal.getTimeInMillis();

        eventCal.add(Calendar.HOUR_OF_DAY, Constants.NOTIFICATION_INTERVAL_HOUR);
        long nightNotifyMaxRange = eventCal.getTimeInMillis();

        // 현재 시각이 오후 알림 범위에 해당하는지
        boolean isNightNotifyRange = nightNotifyMinRange <= currentMillis && currentMillis <= nightNotifyMaxRange;


        if (isNightNotifyRange) {
            // 현재 시각이 알림 범위에 해당하면 알림 생성
            mNotificationHelper.createNotification(Constants.WORK_A_NAME);
        }else {
            // 그 외의 경우 가장 빠른 A 이벤트 예정 시각까지의 notificationDelay 계산하여 딜레이 호출
            long notificationDelay = NotificationHelper.getNotificationDelay(Constants.WORK_A_NAME);
            OneTimeWorkRequest workRequest =
                    new OneTimeWorkRequest.Builder(WorkerA.class)
                            .setInitialDelay(notificationDelay, TimeUnit.MILLISECONDS)
                            .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
        }
        return Result.success();
    }
}

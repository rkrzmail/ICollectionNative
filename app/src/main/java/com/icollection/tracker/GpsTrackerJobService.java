package com.icollection.tracker;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.icollection.util.Utils;

/**
 * Created by jmarkstar on 24/05/2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GpsTrackerJobService extends JobService {

    private static final String TAG = "GpsTrackerJobService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.v(TAG, "Job have started!");
        Intent intent = new Intent(this, GpsTrackerIntentService.class);
        startService(intent);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            scheduleRefresh();

        //Call Job Finished
        jobFinished(jobParameters, false );
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.v(TAG, "Job have finished!");
        return false;
    }

    private void scheduleRefresh() {
        JobScheduler mJobScheduler = (JobScheduler)getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder mJobBuilder =
                new JobInfo.Builder(Utils.JOB_ID,
                        new ComponentName(getPackageName(),
                                GpsTrackerJobService.class.getName()));

  /* For Android N and Upper Versions */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mJobBuilder
                    .setMinimumLatency(60*1000) //YOUR_TIME_INTERVAL
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }


        if (mJobScheduler != null && mJobScheduler.schedule(mJobBuilder.build())
                <= JobScheduler.RESULT_FAILURE) {
            //Scheduled Failed/LOG or run fail safe measures
            Log.d(TAG, "Unable to schedule the service!");
        }
    }
}

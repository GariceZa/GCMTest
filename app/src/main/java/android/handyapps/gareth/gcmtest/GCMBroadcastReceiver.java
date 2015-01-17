package android.handyapps.gareth.gcmtest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Gareth on 2015-01-15.
 */

/**
 * A WakefulBroadcastReceiver is a helper for the common pattern of implementing a BroadcastReceiver that
 * receives a device wakeup event and then passes the work off to a Service, while ensuring that the device
 * does not go back to sleep during the transition
 */

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * A ComponentName is an identifier for a specific application component (Activity, Service, BroadcastReceiver, or ContentProvider) that is available.
         * Two pieces of information, encapsulated here, are required to identify a component: the package (a String) it exists in, and the class (a String) name inside of that package.
         */
        ComponentName comp = new ComponentName(context.getPackageName(),GCMNotificationIntentService.class.getName());

        //  starts the service that does the work
        startWakefulService(context, (intent.setComponent(comp)));

        // Change the current result code of this broadcast
        setResultCode(Activity.RESULT_OK);
    }
}

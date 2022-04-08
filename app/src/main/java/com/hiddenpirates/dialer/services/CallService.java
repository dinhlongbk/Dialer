package com.hiddenpirates.dialer.services;

import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;
import android.widget.Toast;

import com.hiddenpirates.dialer.activities.MainActivity;
import com.hiddenpirates.dialer.activities.CallActivity;
import com.hiddenpirates.dialer.helpers.CallManager;
import com.hiddenpirates.dialer.helpers.Constant;
import com.hiddenpirates.dialer.helpers.NotificationHelper;

public class CallService extends InCallService {

    int call_state;
    String phone_number;

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);

        Log.d(MainActivity.TAG, "Call State:> " + call_state);

        new CallManager().setValues(call, this);
        CallManager.inCallService = this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            call_state = call.getDetails().getState();
        }
        else{
            call_state = call.getState();
        }

        CallManager.HP_CALL_STATE = call_state;
        phone_number = call.getDetails().getHandle().getSchemeSpecificPart();

        if (call_state == Call.STATE_RINGING){

            Intent intent = new Intent(this, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("phoneNumber", phone_number);
            intent.putExtra("callState", Constant.HP_CALL_STATE_INCOMING);
            startActivity(intent);

            NotificationHelper.createIncomingNotification(this,"(Unknown)", call.getDetails().getHandle().getSchemeSpecificPart());
        }
        else if (call_state == Call.STATE_CONNECTING || call_state == Call.STATE_DIALING){

            Intent intent = new Intent(this, CallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("phoneNumber", phone_number);
            intent.putExtra("callState", Constant.HP_CALL_STATE_OUTGOING);
            startActivity(intent);
        }
    }

    @Override
    public void onCallRemoved(Call call) {
        Toast.makeText(this, "Call ended", Toast.LENGTH_SHORT).show();
    }
}

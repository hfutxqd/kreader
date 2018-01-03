package com.onyx.jdread.main.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.onyx.jdread.R;
import com.onyx.jdread.main.activity.LockScreenActivity;
import com.onyx.jdread.main.activity.MainActivity;
import com.onyx.jdread.shop.event.MenuWifiSettingEvent;
import com.onyx.jdread.shop.view.CustomDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-3-14.
 */

public class ManagerActivityUtils {
    private static final String TAG = ManagerActivityUtils.class.getSimpleName();

    public static void showWifiDialog(final Context context) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        builder.setTitle(context.getString(R.string.wifi_dialog_title))
                .setMessage(context.getString(R.string.wifi_dialog_content))
                .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new MenuWifiSettingEvent(context.getString(R.string.menu_wifi_setting)));
                        dialog.dismiss();
                    }
                }).setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public static void lockScreen(Context context) {
        Intent intent = new Intent(context, LockScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}

package com.onyx.jdread.setting.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.Build;

import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.main.util.RegularUtil;
import com.onyx.jdread.setting.event.BackToDeviceConfigFragment;
import com.onyx.jdread.setting.event.PasswordSettingEvent;
import com.onyx.jdread.setting.utils.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.Observable;

/**
 * Created by hehai on 18-1-2.
 */

public class PswSettingModel extends Observable {
    public final TitleBarModel titleBarModel = new TitleBarModel(SettingBundle.getInstance().getEventBus());
    public final ObservableField<String> passwordEdit = new ObservableField<>();
    public final ObservableField<String> phoneEdit = new ObservableField<>();
    public final ObservableField<String> unlockPasswordEdit = new ObservableField<>();
    public final ObservableBoolean encrypted = new ObservableBoolean(false);
    private final EventBus eventBus;
    private String password;

    private PswFailModel pswFailModel;

    public PswSettingModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarModel.title.set(JDReadApplication.getInstance().getString(R.string.password_setting));
        titleBarModel.backEvent.set(new BackToDeviceConfigFragment());
        password = JDPreferenceManager.getStringValue(R.string.password_key, null);
        phoneEdit.set(JDPreferenceManager.getStringValue(R.string.phone_key, null));
        encrypted.set(StringUtils.isNotBlank(password));
        pswFailModel = new PswFailModel(eventBus);
    }

    public void confirmPassword() {
        if (!NetworkUtil.isWiFiConnected(JDReadApplication.getInstance())) {
            ToastUtil.showToast(R.string.wifi_no_connected);
            return;
        }
        String password = passwordEdit.get();
        if (!checkPasswordValid(password)) {
            return;
        }

        if (StringUtils.isNullOrEmpty(phoneEdit.get()) || !RegularUtil.isMobile(phoneEdit.get())) {
            ToastUtil.showToast(R.string.phone_number_format_error);
            return;
        }

        PswSettingData data = PswSettingData.create(phoneEdit.get(), password,
                NetworkUtil.getMacAddress(JDReadApplication.getInstance()), Build.MODEL);
        eventBus.post(new PasswordSettingEvent(data));
    }

    public void unlockPassword() {
        String unlockPassword = unlockPasswordEdit.get();
        if (StringUtils.isNullOrEmpty(password)) {
            password = JDPreferenceManager.getStringValue(R.string.password_key, null);
        }
        boolean valid = pswFailModel.checkUnlockFailData();
        if (!valid) {
            return;
        }
        if (StringUtils.isNotBlank(unlockPassword) && FileUtils.computeMD5(unlockPassword).equals(password)) {
            JDPreferenceManager.setStringValue(R.string.password_key, "");
            pswFailModel.saveUnlockFailData(null);
            encrypted.set(false);
        } else {
            ToastUtil.showToast(ResManager.getString(R.string.wrong_password));
        }
    }

    private boolean checkPasswordValid(String password) {
        if (StringUtils.isNullOrEmpty(password) || (password.length() < Constants.PASSWORD_MIN_LENGTH)
                || password.length() > Constants.PASSWORD_MAX_LENGTH) {
            ToastUtil.showToast(String.format(ResManager.getString(R.string.password_format_error), Constants.PASSWORD_MIN_LENGTH, Constants.PASSWORD_MAX_LENGTH));
            return false;
        }

        if (RegularUtil.isAllCharSame(password) || RegularUtil.isOrderChar(password)) {
            ToastUtil.showToast(R.string.password_too_simple_and_reset);
            return false;
        }
        return true;
    }
}

package com.onyx.jdread.setting.model;

import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.setting.utils.UpdateUtil;

/**
 * Created by li on 2017/12/26.
 */

public class SettingUpdateModel {
    private SystemUpdateData systemUpdateData = new SystemUpdateData();
    private static final String DOWNLOAD_VERSION = "download_version";

    public SystemUpdateData getSystemUpdateData() {
        return systemUpdateData;
    }

    public void setSystemUpdateData(SystemUpdateData systemUpdateData) {
        this.systemUpdateData = systemUpdateData;
    }

    public void saveDownloadVersion(String version) {
        if (StringUtils.isNotBlank(version)) {
            PreferenceManager.setStringValue(JDReadApplication.getInstance(), DOWNLOAD_VERSION, version);
        }
    }

    public String getDownloadVersion() {
        return PreferenceManager.getStringValue(JDReadApplication.getInstance(), DOWNLOAD_VERSION, UpdateUtil.getAPPVersionName(JDReadApplication.getInstance()));
    }
}

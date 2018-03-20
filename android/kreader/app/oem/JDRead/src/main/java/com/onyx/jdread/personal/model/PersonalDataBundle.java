package com.onyx.jdread.personal.model;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.StatisticalData;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.JDPreferenceManager;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.personal.action.DeleteStatisticAction;
import com.onyx.jdread.personal.action.SaveReadTimeAction;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetOrderUrlResultBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.GetRechargePackageBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadOverInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.ReadTotalInfoBean;
import com.onyx.jdread.personal.cloud.entity.jdbean.UserInfo;
import com.onyx.jdread.reader.data.ReadingData;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.util.TimeUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/26.
 */

public class PersonalDataBundle {
    private static PersonalDataBundle personalDataBundle;
    private EventBus eventBus;
    private PersonalViewModel personalViewModel;
    private DataManager dataManager;
    private PersonalModel personalModel;
    private TitleBarModel titleModel;
    private PersonalAccountModel personalAccountModel;
    private List<GetRechargePackageBean.DataBean> topValueBeans;
    private GetOrderUrlResultBean orderUrlResultBean;
    private ReadTotalInfoBean readTotalInfo;
    private ReadOverInfoBean readOverInfo;
    private PointsForModel pointsForModel;
    private PersonalTaskModel personalTaskModel;
    private PersonalBookModel personalBookModel;
    private String salt;
    private UserInfo userInfo;
    private boolean signed;
    private boolean isTodaySign;
    private String targetView;
    private List<StatisticalData> statisticList;

    private PersonalDataBundle() {

    }

    public static PersonalDataBundle getInstance() {
        if (personalDataBundle == null) {
            synchronized (BookDetailViewModel.class) {
                if (personalDataBundle == null) {
                    personalDataBundle = new PersonalDataBundle();
                }
            }
        }
        return personalDataBundle;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = EventBus.getDefault();
        }
        return eventBus;
    }

    public PersonalViewModel getPersonalViewModel() {
        if (personalViewModel == null) {
            synchronized (PersonalViewModel.class) {
                if (personalViewModel == null) {
                    personalViewModel = new PersonalViewModel(getEventBus());
                }
            }
        }
        return personalViewModel;
    }

    public DataManager getDataManager() {
        if (dataManager == null) {
            synchronized (DataManager.class) {
                if (dataManager == null) {
                    dataManager = new DataManager();
                }
            }
        }
        return dataManager;
    }

    public TitleBarModel getTitleModel() {
        if (titleModel == null) {
            titleModel = new TitleBarModel(getEventBus());
        }
        return titleModel;
    }

    public PersonalModel getPersonalModel() {
        if (personalModel == null) {
            personalModel = new PersonalModel();
            personalModel.loadPersonalData();
        }
        return personalModel;
    }

    public PersonalAccountModel getPersonalAccountModel() {
        if (personalAccountModel == null) {
            personalAccountModel = new PersonalAccountModel();
        }
        return personalAccountModel;
    }

    public void setTopValueBeans(List<GetRechargePackageBean.DataBean> topValueBeans) {
        this.topValueBeans = topValueBeans;
    }

    public List<GetRechargePackageBean.DataBean> getTopValueBeans() {
        return topValueBeans;
    }

    public void setOrderUrlResultBean(GetOrderUrlResultBean orderUrlResultBean) {
        this.orderUrlResultBean = orderUrlResultBean;
    }

    public GetOrderUrlResultBean getOrderUrlResultBean() {
        return orderUrlResultBean;
    }

    public void setReadTotalInfo(ReadTotalInfoBean readTotalInfo) {
        this.readTotalInfo = readTotalInfo;
    }

    public ReadTotalInfoBean getReadTotalInfo() {
        return readTotalInfo;
    }

    public void setReadOverInfo(ReadOverInfoBean readOverInfo) {
        this.readOverInfo = readOverInfo;
    }

    public ReadOverInfoBean getReadOverInfo() {
        return readOverInfo;
    }

    public PointsForModel getPointsForModel() {
        if (pointsForModel == null) {
            pointsForModel = new PointsForModel();
            pointsForModel.loadData();
        }
        return pointsForModel;
    }

    public PersonalTaskModel getPersonalTaskModel() {
        if (personalTaskModel == null) {
            personalTaskModel = new PersonalTaskModel();
        }
        personalTaskModel.loadData();
        String currentTime = TimeUtils.getCurrentDataInString();
        String saveTime = getCurrentDay();
        setSigned(StringUtils.isNullOrEmpty(saveTime) || !currentTime.equals(saveTime));
        return personalTaskModel;
    }

    public void setCurrentDay(String day) {
        JDPreferenceManager.setStringValue(Constants.CURRENT_DAY, day);
    }

    public String getCurrentDay() {
        return JDPreferenceManager.getStringValue(Constants.CURRENT_DAY, "");
    }

    public PersonalBookModel getPersonalBookModel() {
        if (personalBookModel == null) {
            personalBookModel = new PersonalBookModel();
            personalBookModel.loadPopupData();
        }
        return personalBookModel;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public boolean isUserVip() {
        boolean isVip = false;
        UserInfo userInfo = getUserInfo();
        if (userInfo != null && userInfo.vip_remain_days > 0) {
            isVip = true;
        }
        return isVip;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public boolean getSigned() {
        return signed;
    }

    public void setReceiveReadVoucherTime(String receiveReadVoucherTime) {
        JDPreferenceManager.setStringValue(Constants.RECEIVED_VOUCHER, receiveReadVoucherTime);
    }

    public String getReceiveReadVoucherTime() {
        return JDPreferenceManager.getStringValue(Constants.RECEIVED_VOUCHER, "");
    }

    public void setIsTodaySign(boolean isTodaySign) {
        this.isTodaySign = isTodaySign;
    }

    public boolean isTodaySign() {
        return isTodaySign;
    }

    public String getTargetView() {
        return targetView;
    }

    public void setTargetView(String targetView) {
        this.targetView = targetView;
    }

    public void deleteReadingData(ReadingData readingData) {
        DeleteStatisticAction action = new DeleteStatisticAction(readingData.ebook_id);
        action.execute(PersonalDataBundle.getInstance(), null);
    }

    public void saveReadingData(ReadingData readingData) {
        StatisticalData statisticalData = new StatisticalData();
        statisticalData.cloudId = readingData.ebook_id;
        statisticalData.length = readingData.length;
        statisticalData.startReadTime = readingData.start_time;
        statisticalData.endReadTime = readingData.end_time;
        SaveReadTimeAction action = new SaveReadTimeAction(statisticalData);
        action.execute(PersonalDataBundle.getInstance(), null);
    }

    public void setStatisticList(List<StatisticalData> statisticList) {
        this.statisticList = statisticList;
    }

    public List<StatisticalData> getStatisticList() {
        return statisticList;
    }
}

package com.onyx.android.sdk.data.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/9.
 */

public class HomeworkSubmitAnswer {

    public String question;
    public String value;
    public List<String> attachment;
    public String drawData;
    public List<Bitmap> bitmaps;

    public void setAttachment(List<String> attachment) {
        this.attachment = attachment;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
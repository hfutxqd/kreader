package com.onyx.edu.homework.action;

import android.content.Context;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.homework.Homework;
import com.onyx.android.sdk.data.model.homework.Question;
import com.onyx.edu.homework.DataBundle;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.HomeworkListRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2017/12/5.
 */

public class HomeworkListAction extends BaseAction {

    private String publicHomeworkId;
    private String personalHomeworkId;
    private List<Question> questions = new ArrayList<>();

    public HomeworkListAction(String publicHomeworkId, String personalHomeworkId) {
        this.publicHomeworkId = publicHomeworkId;
        this.personalHomeworkId = personalHomeworkId;
    }

    @Override
    public void execute(Context context, final BaseCallback baseCallback) {
        final HomeworkListRequest listRequest = new HomeworkListRequest(publicHomeworkId, personalHomeworkId);
        listRequest.setContext(context.getApplicationContext());
        getCloudManager().submitRequest(context, listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                DataBundle.getInstance().setState(listRequest.getHomeworkState());
                Homework homework = listRequest.getHomework();
                if (homework != null) {
                    questions.addAll(homework.questions);
                }
                DataBundle.getInstance().setHomework(homework);
                baseCallback.done(request, e);
            }
        });
    }

    public List<Question> getQuestions() {
        return questions;
    }
}

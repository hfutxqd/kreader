package com.onyx.edu.reader.ui.data;

import android.graphics.Color;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.reader.api.ReaderFormCheckbox;
import com.onyx.android.sdk.reader.api.ReaderFormField;
import com.onyx.android.sdk.reader.api.ReaderFormRadioButton;
import com.onyx.android.sdk.reader.api.ReaderFormRadioGroup;
import com.onyx.android.sdk.reader.api.ReaderFormScribble;
import com.onyx.android.sdk.reader.api.ReaderFormText;

/**
 * Created by joy on 5/25/17.
 */

public class FormFieldControlFactory {
    private static View createEditInput(RelativeLayout parentView, ReaderFormText textField) {
        EditText editText = new EditText(parentView.getContext());
        editText.setBackgroundColor(Color.LTGRAY);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)textField.getRect().width(),
                (int)textField.getRect().height());
        params.leftMargin = (int)textField.getRect().left;
        params.topMargin = (int)textField.getRect().top;

        editText.setLayoutParams(params);
        return editText;
    }

    private static CheckBox createCheckBox(RelativeLayout parentView, ReaderFormCheckbox checkboxField) {
        CheckBox checkBox = new CheckBox(parentView.getContext());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)checkboxField.getRect().width(),
                (int)checkboxField.getRect().height());
        params.leftMargin = (int)checkboxField.getRect().left;
        params.topMargin = (int)checkboxField.getRect().top;

        checkBox.setLayoutParams(params);
        return checkBox;
    }

    private static RadioGroup createRadioGroup(RelativeLayout parentView, ReaderFormRadioGroup groupField) {
        if (groupField.getButtons().size() <= 0) {
            return null;
        }

        RadioGroup radioGroup = new RadioGroup(parentView.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        params.leftMargin = (int)groupField.getButtons().get(0).getRect().left;
        params.topMargin = (int)groupField.getButtons().get(0).getRect().top;

        for (ReaderFormRadioButton buttonField : groupField.getButtons()) {
            RadioButton button = new RadioButton(parentView.getContext());
            RadioGroup.LayoutParams buttonParams = new RadioGroup.LayoutParams((int)buttonField.getRect().width(),
                    (int)buttonField.getRect().height());
            radioGroup.addView(button, buttonParams);
        }

        radioGroup.setLayoutParams(params);
        return radioGroup;
    }

    private static View createScribbleRegion(RelativeLayout parentView, ReaderFormScribble scribbleField) {
        SurfaceView view = new SurfaceView(parentView.getContext());
        view.setBackgroundColor(Color.LTGRAY);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)scribbleField.getRect().width(),
                (int)scribbleField.getRect().height());
        params.leftMargin = (int)scribbleField.getRect().left;
        params.topMargin = (int)scribbleField.getRect().top;

        view.setLayoutParams(params);
        return view;
    }

    public static View createFormControl(RelativeLayout parentView, ReaderFormField field) {
        if (field instanceof ReaderFormText) {
            return createEditInput(parentView, (ReaderFormText)field);
        } else if (field instanceof ReaderFormCheckbox) {
            return createCheckBox(parentView, (ReaderFormCheckbox)field);
        } else if (field instanceof ReaderFormRadioGroup) {
            return createRadioGroup(parentView, (ReaderFormRadioGroup)field);
        } else if (field instanceof ReaderFormScribble) {
            return createScribbleRegion(parentView, (ReaderFormScribble)field);
        } else {
            return null;
        }
    }
}

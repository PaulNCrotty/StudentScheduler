package edu.wgu.android.studentscheduler.widget;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

public class ObjectCheckBox extends AppCompatCheckBox {

    Object object;

    public ObjectCheckBox(@NonNull Context context, Object object) {
        super(context);
        this.object = object;
    }
}

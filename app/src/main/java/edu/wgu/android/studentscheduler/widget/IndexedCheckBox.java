package edu.wgu.android.studentscheduler.widget;

import android.content.Context;

import androidx.appcompat.widget.AppCompatCheckBox;

import lombok.Getter;
import lombok.Setter;

public class IndexedCheckBox extends AppCompatCheckBox {

    @Setter
    @Getter
    int viewIndex;

    public IndexedCheckBox(Context context) {
        super(context);
    }

}

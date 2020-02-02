package com.edu_app.controller;

import android.view.View;
import android.widget.TextView;

import com.edu_app.R;
import com.edu_app.model.Question;

public class TeacherAddPracticeItemController {
    private static View.OnClickListener clickItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO: 进入问题详情
        }
    };

    private Question model;
    private View view;

    public TeacherAddPracticeItemController(View view, Question model){
        this.view = view;
        this.model = model;
        setValues();
        bindListener();
    }

    private void setValues(){
        TextView title = view.findViewById(R.id.order_number_text);
        title.setText("题目"+model.getOrderNumber());
    }

    private void bindListener(){
        view.setOnClickListener(clickItemListener);
    }


}
package com.edu_app.view.teacher.person;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.edu_app.R;
import com.edu_app.controller.teacher.person.PersonCourseController;
import com.edu_app.view.teacher.activityTeaFunction;


public class activityPersonCourse extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_person_course);
        PersonCourseController controller = new PersonCourseController(this);
        controller.setOnClick();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.putExtras(this.getIntent().getExtras());
            intent.setClass(activityPersonCourse.this, activityTeaFunction.class);
            startActivity(intent);
            activityPersonCourse.this.finish();

        }
        return true;
    }
}

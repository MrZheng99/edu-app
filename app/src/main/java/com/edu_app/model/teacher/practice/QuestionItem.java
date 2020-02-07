package com.edu_app.model.teacher.practice;

import androidx.core.util.Pair;

import com.edu_app.R;
import com.edu_app.controller.teacher.Controller;
import com.edu_app.model.teacher.Model;
import com.edu_app.model.Question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionItem implements Model, Serializable {
    protected Question question;

    public QuestionItem(Question question){
        if(question == null){
            question = new Question(0, "short_answer", null);
        }
        this.question = question;
    }

    public Question getEntity(){
        return question;
    }

    @Override
    public List<Pair<Integer, Object>> getShowField() {
        ArrayList<Pair<Integer, Object>> list = new ArrayList<>();
        String type = question.getQuestionType();
        String typeText = Question.typeText.get(type);
        String s = String.format("%d(%s): %s", question.getOrderNumber(), typeText, question.getQuestion());
        list.add(new Pair<Integer, Object>(R.id.order_number_text, s));
        return list;
    }

    @Override
    public void setController(Controller controller) {

    }

    public String getQuestion() {
        return question.getQuestion();
    }

    public String getAnswer() {
        return question.getAnswer();
    }

    public void setQuestion(String o) {
        question.setQuestion(o);
    }

    public void setAnswer(String s) {
        question.setAnswer(s);
    }

    public void setOrderNumber(int i) {
        question.setOrderNumber(i);
    }

    public String getQuestionType() {
        return question.getQuestionType();
    }
}

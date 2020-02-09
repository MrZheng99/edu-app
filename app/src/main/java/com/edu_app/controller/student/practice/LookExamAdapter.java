package com.edu_app.controller.student.practice;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.edu_app.R;

import java.util.Map;

/**
 *
 * 查看习题的适配器
 */
public class LookExamAdapter extends RecyclerView.Adapter<LookExamAdapter.ViewHolder> {
    private Context context;
//    本次测试得分
    private Integer examscore;
    /**
     * 返回的数据包括试卷总体信息，以及详细题目，学生的答案，正确答案，学生每题得分
     * {practiceInfo:{练习id:"",本次总分:""...},everyQuestion:{题号:"1",问题:"question",学生答案:"stu_answer",学生分数:"",此题满分:""...}...}
     */
    private Map<String,Map<String, String>> questionAnswerScore;

    public LookExamAdapter(Context context, Map<String,Map<String, String>> questionAnswerScore) {
        this.context = context;
        this.questionAnswerScore = questionAnswerScore;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView questiontext;
        private TextView stu_answertext;
        private TextView right_answertext;
//        学生本题得分
        private TextView stuscore;
//        本题满分
        private TextView fullscore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questiontext = itemView.findViewById(R.id.practicePage_practice_lookexam_question);
            stu_answertext = itemView.findViewById(R.id.practicePage_practice_lookexam_stuanswer);
            right_answertext = itemView.findViewById(R.id.practicePage_practice_lookexam_rightanswer);
            stuscore = itemView.findViewById(R.id.practicePage_practice_lookexam_stuscore);
            fullscore = itemView.findViewById(R.id.practicePage_practice_lookexam_fullscore);

        }
    }

    @NonNull
    @Override
    public LookExamAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.activity_stu_practice_lookexam_item, null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LookExamAdapter.ViewHolder holder, int position) {
        Log.e("error",questionAnswerScore.keySet().toString());
        Map<String,String> questionDetail = questionAnswerScore.get(String.valueOf(position+1));
        holder.questiontext.setText(questionDetail.get("question").toString());
        holder.stuscore.setText(questionDetail.get("stuscore").toString());
        holder.fullscore.setText(questionDetail.get("fullscore").toString());
        holder.stu_answertext.setText(questionDetail.get("stu_answer").toString());
        holder.right_answertext.setText(questionDetail.get("right_answer").toString());
    }

    @Override
    public int getItemCount() {
        return questionAnswerScore.size();
    }
}
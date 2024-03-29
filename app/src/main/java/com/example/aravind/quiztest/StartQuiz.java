package com.example.aravind.quiztest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class StartQuiz extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference ref, ref1;
    private Map<String, Long> data;
    private String uname;
    private Toast mToast;
    private ProgressDialog mProgressDialog;
    private Map<String, Long> quiz;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startquiz);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("QuizState");
        ref1 = db.getReference("UserAttempt");
        data = new HashMap<>();
        quiz = new HashMap<>();
        uname = getIntent().getStringExtra("UserName");
        TextView t=(TextView)findViewById(R.id.uname);
        t.setText(uname);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        ref1.child(uname.split("@")[0]).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d: dataSnapshot.getChildren()) {
                    Log.d("Quiz", "User Attempt: " + d.getKey().toString() + "," + d.getValue().toString());
                    quiz = (Map<String, Long>) d.getValue();
                }
                String inst = getString(R.string.listofinstructions1) + " " + String.valueOf(quiz.get("Quiz")) + getString(R.string.listofinstructions2);
                ((TextView) findViewById(R.id.instructions)).setText(inst);
                if(mProgressDialog!=null)
                    mProgressDialog.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Toast.makeText(StartQuiz.this, "Data retrieved!", Toast.LENGTH_SHORT).show();
                for(DataSnapshot d: dataSnapshot.getChildren()){
                    data.put(d.getKey(), (Long) d.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                if(mToast!=null){ mToast.cancel(); }
                mToast = Toast.makeText(StartQuiz.this, "Data not retrieved! Please check your internet connection", Toast.LENGTH_SHORT);
                mToast.show();
            }
        });

    }

    //Drop down - to view reports / logout
    public void dropdown(View view)
    {

    }


    public void GoToQuestionPage(View view)
    {
        //Check if checkbox is ticked
        CheckBox chkbox=(CheckBox)findViewById(R.id.checkbutton);
        try {
            if (data.get("QuizRound") > 0 && data.get("QuizRound") <= 3 && quiz.get("Quiz") == 1 && chkbox.isChecked()) {
                Intent intent = new Intent(StartQuiz.this, Question_Answer.class);
                intent.putExtra("uname", uname.split("@")[0]);
                intent.putExtra("round", String.valueOf(data.get("QuizRound")));
                startActivity(intent);
            }

            else if (!chkbox.isChecked())
            {
                Toast.makeText(this,"Please check the agreement box",Toast.LENGTH_LONG).show();
            }

            else if(quiz.get("Quiz1") == 0){
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(this, "No Attempts left.", Toast.LENGTH_SHORT);
                mToast.show();
            } else {
                if (mToast != null) {
                    mToast.cancel();
                }
                mToast = Toast.makeText(this, "Quiz hasn't started yet! Patience.", Toast.LENGTH_SHORT);
                mToast.show();
                Log.d("Quiz", String.valueOf(data.get("Quiz1")));
            }
        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

package com.android.notepad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Tag;

public class AddTagActivity extends AppCompatActivity {

    private EditText editText;
    private Button doneBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        // 设置该Activity的宽高
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels / 1.5);
        getWindow().setAttributes(layoutParams);

        editText = (EditText) findViewById(R.id.add_tag_edit);
        doneBtn = (Button) findViewById(R.id.add_tag_done);
        cancelBtn = (Button) findViewById(R.id.add_tag_cancel);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tagName = editText.getText().toString();
                Tag newTag = new Tag(tagName, 0);
                Repository.getInstance().insertTag(newTag);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
package com.android.notepad.ui.edit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.notepad.R;
import com.android.notepad.adapter.TagAdapter;
import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Tag;

import java.util.List;

public class TagActivity extends AppCompatActivity {

    private RecyclerView tagRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        // 设置该Activity的宽高
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels / 1.5);
        layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels / 2);
        getWindow().setAttributes(layoutParams);

        tagRecyclerView = findViewById(R.id.tag_recycler_view);
        List<Tag> tagList = Repository.getInstance().queryTag();
        RecyclerView.LayoutManager tagLayoutManager = new LinearLayoutManager(this);
        tagRecyclerView.setLayoutManager(tagLayoutManager);
        TagAdapter tagAdapter = new TagAdapter(tagList);
        tagRecyclerView.setAdapter(tagAdapter);

    }
}
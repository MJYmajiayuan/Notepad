package com.android.notepad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.notepad.login.model.Note;
import com.android.notepad.ui.edit.EditActivity;
import com.android.notepad.ui.home.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private FloatingActionButton editBtn;
    private RecyclerView noteRecycler;
    private List<Note> noteList;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();   // 初始化组件和布局

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);   // 创建ViewModel对象

        setSupportActionBar(myToolbar);     // 设置自定义标题栏
        mainViewModel.createNoteDatabase(this);  // 创建数据库

        initNotes();        // 从SQLite获取数据
        RecyclerView.LayoutManager layoutManager= new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);   // 瀑布流布局
        noteRecycler.setLayoutManager(layoutManager);
        NoteAdapter noteAdapter = new NoteAdapter(this, noteList);
        noteRecycler.setAdapter(noteAdapter);

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initComponents() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        editBtn = (FloatingActionButton) findViewById(R.id.edit_btn);
        noteRecycler = (RecyclerView) findViewById(R.id.note_recycler_view);
    }

    private void initNotes() {
        noteList = mainViewModel.queryNote();
    }
}
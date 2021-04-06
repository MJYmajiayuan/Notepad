package com.android.notepad;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.notepad.login.model.Note;
import com.android.notepad.ui.edit.EditActivity;
import com.android.notepad.ui.home.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private FloatingActionButton editBtn;
    private RecyclerView noteRecycler;
//    private List<Note> noteList;
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
        NoteAdapter noteAdapter = new NoteAdapter(this, mainViewModel.noteList);
        noteRecycler.setAdapter(noteAdapter);

        /**
         * 悬浮按钮点击事件，点击后进入编辑界面
         */
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("tag", "insert");   // 表示这是个插入操作
                startActivity(intent);
            }
        });

        mainViewModel.noteLiveData.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                Log.d("MainActivity", "onChange");
                noteAdapter.notifyDataSetChanged();
            }
        });

        /**
         * 设置item的点击事件
         */
        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, Note note) {
                Log.d("MainActivity", "onItemClick, " + position + ", " + note.getContent());
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("tag", "update");   // 表示这是个更新操作
                intent.putExtra("note", note);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MainActivity", "onResume refresh");
        mainViewModel.refreshNoteList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化控件
     */
    private void initComponents() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        editBtn = (FloatingActionButton) findViewById(R.id.edit_btn);
        noteRecycler = (RecyclerView) findViewById(R.id.note_recycler_view);
    }

    /**
     * 初始化数据
     */
    private void initNotes() {
        mainViewModel.noteList.addAll(mainViewModel.queryNote());
    }
}
package com.android.notepad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.notepad.login.model.Note;
import com.android.notepad.ui.edit.EditActivity;
import com.android.notepad.ui.home.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final int FROM_BUTTON = 1;
    private final int FROM_ITEM = 2;

    private Toolbar mainToolbar;
    private FloatingActionButton editBtn;
    private RecyclerView noteRecycler;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();   // 初始化组件和布局
        setSupportActionBar(mainToolbar);     // 设置自定义标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);   // 创建ViewModel对象


        mainViewModel.createNoteDatabase(this);  // 创建数据库

        mainViewModel.refreshNoteList(this);

        RecyclerView.LayoutManager layoutManager= new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);   // 瀑布流布局
        noteRecycler.setLayoutManager(layoutManager);
        NoteAdapter noteAdapter = new NoteAdapter(this, mainViewModel.noteList, mainViewModel.noteBitmapMap);
        noteRecycler.setAdapter(noteAdapter);

        /**
         * 通过点击悬浮按钮进入编辑界面
         * 悬浮按钮点击事件，点击后进入编辑界面
         */
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("tag", "insert");   // 表示这是个插入操作
                startActivityForResult(intent, FROM_BUTTON);
            }
        });

        mainViewModel.noteLiveData.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
//                Log.d("MainActivity", "onChange");
                noteAdapter.notifyDataSetChanged();
            }
        });

        /**
         * 通过点击item进入编辑界面
         * 设置item的点击事件
         */
        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, int noteId) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("tag", "update");   // 表示这是个更新操作
                intent.putExtra("noteId", noteId);
                startActivityForResult(intent, FROM_ITEM);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Log.d("MainActivity", "onResume refresh");
        mainViewModel.refreshNoteList(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 加载edit_toolbar.xml布局
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

        MenuItem searchItem = menu.findItem(R.id.main_search);
        SearchView mainSearch = null;
        if (searchItem != null) {
            mainSearch = (SearchView) searchItem.getActionView();
        }
        mainSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    mainViewModel.refreshNoteList(MainActivity.this);
                } else {
                    mainViewModel.refreshNoteListBySearch(MainActivity.this, newText);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化控件
     */
    private void initComponents() {
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        editBtn = (FloatingActionButton) findViewById(R.id.edit_btn);
        noteRecycler = (RecyclerView) findViewById(R.id.note_recycler_view);
    }

    /**
     * 初始化数据
     */
    private void initNotes(List<Note> noteList) {
        noteList.addAll(mainViewModel.queryNote());
    }
}
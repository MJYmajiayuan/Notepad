package com.android.notepad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.notepad.login.model.Note;
import com.android.notepad.login.model.Tag;
import com.android.notepad.ui.edit.EditActivity;
import com.android.notepad.adapter.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private final int FROM_BUTTON = 1;
    private final int FROM_ITEM = 2;

    private DrawerLayout mainDrawerLayout;
    private Toolbar mainToolbar;
    private FloatingActionButton editBtn;
    private RecyclerView noteRecycler;
    private RecyclerView drawerRecycler;
    private MainViewModel mainViewModel;

    private NavigationView navigationView;

    // 标签列表
    private List<Tag> tagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();   // 初始化组件和布局
        setSupportActionBar(mainToolbar);     // 设置自定义标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);   // 创建ViewModel对象
        mainViewModel.createNoteDatabase(this);  // 创建数据库
        mainViewModel.refreshNoteList(this);    //刷新列表以获取数据

        refreshNavigationView();
        // 通过查询Tag表设置NavigationView
//        List<Tag> tagList = mainViewModel.queryTag();
//        for (int i = 0; i < tagList.size(); i++) {
//            navigationView.getMenu().add(tagList.get(i).getTagName());
//            navigationView.getMenu().getItem(i).setIcon(R.drawable.ic_baseline_label_24);
//        }

        RecyclerView.LayoutManager mainLayoutManager= new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);   // 瀑布流布局
        noteRecycler.setLayoutManager(mainLayoutManager);
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

        // 通过点击item进入编辑界面，设置item的点击事件
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
        refreshNavigationView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 加载main_toolbar.xml布局
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar, menu);

        // 初始化SearchView
        MenuItem searchItem = menu.findItem(R.id.main_search);
        SearchView mainSearch = null;
        if (searchItem != null) {
            mainSearch = (SearchView) searchItem.getActionView();
        }
        // 给SearchView添加监听事件
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

    /**
     * 点击toolbar按钮事件
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mainDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 侧边栏item点击事件
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0) {

        } else if (item.getItemId() == tagList.size() + 1) {
            Intent intent = new Intent(MainActivity.this, AddTagActivity.class);
            startActivity(intent);
            mainDrawerLayout.closeDrawers();
        } else {

        }
        return true;
    }


    /**
     * 初始化控件
     */
    private void initComponents() {
        mainDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        editBtn = (FloatingActionButton) findViewById(R.id.edit_btn);
        noteRecycler = (RecyclerView) findViewById(R.id.note_recycler_view);
//        drawerRecycler = (RecyclerView) findViewById(R.id.drawer_recycler_view);
        navigationView = (NavigationView) findViewById(R.id.nav_View);
    }

    private void refreshNavigationView() {
        navigationView.getMenu().removeGroup(0);
        navigationView.getMenu().add(0, 0, 0, "全部记事");
        navigationView.getMenu().getItem(0).setIcon(R.drawable.ic_baseline_event_note_24);
        tagList = mainViewModel.queryTag();
        int tagListSize = tagList.size();
        for (int i = 1; i <= tagListSize; i++) {
            navigationView.getMenu().add(0, i, i, tagList.get(i - 1).getTagName());
            navigationView.getMenu().getItem(i).setIcon(R.drawable.ic_baseline_label_24);
        }
        navigationView.getMenu().add(0, tagListSize + 1, tagListSize + 1, "添加新标签");
        navigationView.getMenu().getItem(tagListSize + 1).setIcon(R.drawable.ic_baseline_add_24);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 初始化数据
     */
    private void initNotes(List<Note> noteList) {
        noteList.addAll(mainViewModel.queryNote());
    }


}
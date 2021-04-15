package com.android.notepad.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.notepad.R;
import com.android.notepad.login.model.Note;
import com.android.notepad.ui.UiUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements View.OnClickListener {

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageItem;
        TextView contentText;
        ImageView imageSound;
        TextView timeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = (ImageView) itemView.findViewById(R.id.image_item);
            contentText = (TextView) itemView.findViewById(R.id.content_text);
            imageSound = (ImageView) itemView.findViewById(R.id.image_sound);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
        }
    }

    /**
     * 定义点击事件回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position, int noteId);
    }

    private OnItemClickListener onItemClickListener;
    private List<Note> noteList;
    private Map<Integer, Bitmap> noteBitmapMap;
    private Context context;

    public NoteAdapter(Context context, List<Note> noteList, Map<Integer, Bitmap> noteBitmapMap) {
        this.noteList = noteList;
        this.context = context;
        this.noteBitmapMap = noteBitmapMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        // 绑定监听事件
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = noteList.get(position);

        // 从noteBitmapMap中获取Bitmap，若map里没有对应id的bitmap则返回空
        Bitmap bitmap = noteBitmapMap.get(note.getId());
        holder.imageItem.setImageBitmap(bitmap);
        UiUtil.adjustImageView(context, holder.imageItem, bitmap);

        String noteContent = note.getContent();
        String noteTime = note.getTime();
        String noteSound = note.getSound();
        holder.contentText.setText(noteContent);
        holder.timeText.setText(noteTime);
        if (!TextUtils.isEmpty(noteSound)) {
            holder.imageSound.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(noteContent)
                    && TextUtils.isEmpty(note.getImage())) {
                holder.contentText.setText("语音记事");
            }
        } else {
            holder.imageSound.setVisibility(View.GONE);
        }
        // 保存position
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    /**
     * 提供set方法
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            int position = (int) v.getTag();
            onItemClickListener.onItemClick((RecyclerView) v.getParent(), v, position, noteList.get(position).getId());
        }
    }
}

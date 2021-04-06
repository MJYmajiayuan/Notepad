package com.android.notepad.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.notepad.R;
import com.android.notepad.login.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> implements View.OnClickListener {

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView contentText;
        TextView timeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.content_text);
            timeText = itemView.findViewById(R.id.time_text);
        }
    }

    /**
     * 定义点击事件回调接口
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position, Note note);
    }

    private OnItemClickListener onItemClickListener;
    private List<Note> noteList;
    private Context context;

    public NoteAdapter(Context context, List<Note> noteList) {
        this.noteList = noteList;
        this.context = context;
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
        holder.contentText.setText(note.getContent());
        holder.timeText.setText(note.getTime());
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
            onItemClickListener.onItemClick((RecyclerView) v.getParent(), v, position, noteList.get(position));
        }
    }
}

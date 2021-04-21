package com.android.notepad.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.notepad.R;
import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;
import com.android.notepad.login.model.Tag;
import com.android.notepad.ui.edit.TagActivity;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

//        ImageView tagImg;
        TextView tagName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tagImg = (ImageView) itemView.findViewById(R.id.tag_img);
            tagName = (TextView) itemView.findViewById(R.id.tag_text);
        }
    }

    private Context context;
    private List<Tag> tagList;
    private Note note;

    public TagAdapter(Context context, List<Tag> tagList, Note note) {
        this.context = context;
        this.tagList = tagList;
        this.note = note;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        // 为每一项添加点击事件
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                Log.d("TagAdapter", position + "");
                Tag tag = tagList.get(position);
                Log.d("TagAdapter", tag.getTagName());
                note.setTagId(tag.getTagId());          // 设置记事标签
                Log.d("TagAdapter", note.getId() + "");
                tag.setTagNum(tag.getTagNum() + 1);     // 标签下记事数+1
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Repository.getInstance().updateNote(note);
                        Repository.getInstance().updateTag(tag);
                    }
                }).start();
                if (context instanceof TagActivity) {
                    ((TagActivity) context).finish();
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = tagList.get(position);
//        holder.tagImg.setImageResource(R.drawable.icon_label);
        holder.tagName.setText(tag.getTagName());
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }


}

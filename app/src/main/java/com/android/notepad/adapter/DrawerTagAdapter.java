package com.android.notepad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.notepad.R;
import com.android.notepad.login.model.Tag;

import java.util.List;

public class DrawerTagAdapter extends RecyclerView.Adapter<DrawerTagAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tagName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tagName = (TextView) itemView.findViewById(R.id.tag_text);
        }
    }

    private List<Tag> tagList;

    public DrawerTagAdapter(List<Tag> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        holder.tagName.setTag(tag.getTagName());
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }


}

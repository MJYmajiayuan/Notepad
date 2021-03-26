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

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView contentText;
        TextView timeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.content_text);
            timeText = itemView.findViewById(R.id.time_text);
        }
    }

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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.contentText.setText(note.getContent());
        holder.timeText.setText(note.getTime());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }


}

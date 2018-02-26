package com.example.pamplins.apptfg;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pamplins.apptfg.Model.Comment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gustavo on 24/02/2018.
 */

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView tvAuthor;
    public TextView tvDescription;


    public CommentViewHolder(View itemView) {
        super(itemView);

        tvAuthor = itemView.findViewById(R.id.comment_author);
        tvDescription = itemView.findViewById(R.id.comment_body);
    }


    public static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context ctx;
        private DatabaseReference databaseReference;
        private ChildEventListener childEventListener;

        private List<String> commentIds = new ArrayList<>();
        private List<Comment> comments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            ctx = context;
            databaseReference = ref;

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                    Comment comment = dataSnapshot.getValue(Comment.class);

                    commentIds.add(dataSnapshot.getKey());
                    comments.add(comment);
                    notifyItemInserted(comments.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = commentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        comments.set(commentIndex, newComment);

                        notifyItemChanged(commentIndex);
                    } else {
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = commentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        commentIds.remove(commentIndex);
                        comments.remove(commentIndex);

                        notifyItemRemoved(commentIndex);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

                    // Ver que si un comentario tiene muchos votos. ponerlo como primera posicion en el foro ya que es la top response
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ctx, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);

            this.childEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = comments.get(position);
            holder.tvAuthor.setText(comment.author);
            holder.tvDescription.setText(comment.text);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        public void cleanupListener() {
            if (childEventListener != null) {
                databaseReference.removeEventListener(childEventListener);
            }
        }

    }

}



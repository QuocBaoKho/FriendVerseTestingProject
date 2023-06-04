package com.example.friendverse.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.friendverse.AddReelActivity;
import com.example.friendverse.CommentActivity;
import com.example.friendverse.Fragment.ProfileFragment;
import com.example.friendverse.Model.Post;
import com.example.friendverse.Model.User;
import com.example.friendverse.Profile.FollowActivity;
import com.example.friendverse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReelAdapter extends RecyclerView.Adapter<ReelAdapter.viewHolder> {
    Context thisContext;
    List<Post> reelList;
    FirebaseUser firebaseUser;
    public ReelAdapter(Context context, List<Post> reelList){
        thisContext = context;
        this.reelList = reelList;

    }
    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reel_video, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.setData(position);
        Post reel = reelList.get(position);
        holder.content.setText(reel.getDescription());

        isLikes(reel.getPostid(), holder.like);
        String id = reel.getPublisher();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(thisContext.getApplicationContext()).load(user.getImageurl()).into(holder.user_Icon);
                holder.username.setText(user.getUsername());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(reel.getPostid())
                            .child(firebaseUser.getUid()).setValue(true);
                    //addNotifications(post.getPublisher() , post.getPostid());
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(reel.getPostid())
                            .child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext , CommentActivity.class);
                intent.putExtra("postid" , reel.getPostid());
                intent.putExtra("publisherid" , reel.getPublisher());
                thisContext.startActivity(intent);
            }
        });
        holder.likes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(thisContext, FollowActivity.class);
                                                intent.putExtra("id", reel.getPostid());
                                                intent.putExtra("title", "Likes");
                                                thisContext.startActivity(intent);
                                            }
                                        });
        holder.addReel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x = new Intent(thisContext, AddReelActivity.class);
                thisContext.startActivity(x);
            }
        });
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sharePostWithFollowers(reel);

            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = thisContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", reel.getPublisher());
                editor.apply();

                ((FragmentActivity) thisContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });
        holder.user_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = thisContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", reel.getPublisher());
                editor.apply();

                ((FragmentActivity) thisContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child(reel.getPostid())
                            .setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid()).child(reel.getPostid()).removeValue();
                }
            }
        });

        getLikes(holder.likes, reel.getPostid());
        getComments(holder.comments, reel.getPostid());
        isSaved(reel.getPostid(), holder.save);
    }

    @Override
    public int getItemCount() {
        return reelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        VideoView reelVid;
        ImageView user_Icon, like, share, comment, save;
        ImageView addReel;
        TextView content;
        TextView username;
        TextView likes, shares, comments;
        public viewHolder(@NonNull View itemView){
            super(itemView);
            reelVid = itemView.findViewById(R.id.ReelVideo);
            user_Icon = itemView.findViewById(R.id.reel_image_profile);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            share = itemView.findViewById(R.id.share);
            content = itemView.findViewById(R.id.contentReel);
            username = itemView.findViewById(R.id.reelUsername);
            likes = itemView.findViewById(R.id.likes);
            comments = itemView.findViewById(R.id.comments);
            shares = itemView.findViewById(R.id.shares);
            save = itemView.findViewById(R.id.save);
            addReel = itemView.findViewById(R.id.addReel);

        }

        void setData(int position){
            if(reelList.get(position).getPostid() != null){
                reelVid.setVideoURI(Uri.parse(reelList.get(position).getPostvid()));
            }
            reelVid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                    float videoRatio = mediaPlayer.getVideoWidth()/(float)mediaPlayer.getVideoHeight();
                    float screenRatio = reelVid.getWidth()/(float)reelVid.getHeight();
                    float scale = videoRatio/screenRatio;
                    if(scale >= 1f){
                        reelVid.setScaleX(scale);
                    }
                    else {
                        reelVid.setScaleY(1f/scale);
                    }
                }
            });
            reelVid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        }
    }
    private void isSaved (final String postid , final ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isLikes (String reelID , ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(reelID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_favorite);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_favorited);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public int getLikes(TextView likes, String commentID){
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes").child(commentID);

        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    likes.setText(getNum(snapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return 0;
    }
    private String getNum(long x){
        if(x < 1000){
            return x + "";
        }
        if(x >= 1000 && x <= 1000000)
            return x/1000 + "K";
        return x/1000000 + "M";
    }
    public void getComments(TextView comments, String reelID){
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comments").child(reelID);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    comments.setText(getNum(snapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void sharePostWithFollowers(final Post post) {
        DatabaseReference followersRef = FirebaseDatabase.getInstance().getReference().child("Followers").child(firebaseUser.getUid()).child("followers");
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<String> followersList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    followersList.add(snapshot.getKey());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
                builder.setTitle("Select followers to share with")
                        .setMultiChoiceItems(followersList.toArray(new String[0]), null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // Handle follower selection
                            }
                        })
                        .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SparseBooleanArray checkedItems = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                                for (int i = 0; i < followersList.size(); i++) {
                                    if (checkedItems.get(i)) {
                                        String followerId = followersList.get(i);
                                        sharePostWithFollower(followerId, post);
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sharePostWithFollower(String followerId, Post post) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Shares")
                .child(followerId)
                .child(post.getPostid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("postid", post.getPostid());

        reference.updateChildren(hashMap);
    }

}
package com.example.lostandfoundapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class dataforfound extends AppCompatActivity {


    private RecyclerView postfoundlist ;
    private DatabaseReference postref, UserRef;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    String current_user_id;
    String useremail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataforfound);
        getSupportActionBar().setTitle("LOST ITEMS DATA");
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#AF0895"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);



        postfoundlist = (RecyclerView) findViewById(R.id.recyclerviewforfoundposts);
        postfoundlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postfoundlist.setLayoutManager(linearLayoutManager);

        fetch();

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Registered Users");


    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("FoundItems");

        FirebaseRecyclerOptions<modellostposts> options =
                new FirebaseRecyclerOptions.Builder<modellostposts>().setQuery(query, new SnapshotParser<modellostposts>() {
                    @NonNull
                    @Override
                    public modellostposts parseSnapshot(@NonNull DataSnapshot snapshot) {

                        return new modellostposts(
                                snapshot.child("FullName").getValue().toString(),
                                snapshot.child("Message").getValue().toString(),


                                snapshot.child("date").getValue().toString(),
                                snapshot.child("time").getValue().toString(),

                                snapshot.child("Imagelink").getValue().toString(),

                                snapshot.child("PhoneNumber").getValue().toString(),
                                snapshot.child("emailfound").getValue().toString());




                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<modellostposts, viewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull modellostposts model) {
                holder.setDatetv(modellostposts.getDate());
                holder.setFullNametv(modellostposts.getFullName());
                holder.setMessagetv(modellostposts.getMessage());
                holder.setTimetv(modellostposts.getTime());
                holder.setPhonetv(modellostposts.getPhoneNumber());
                holder.setEmailtv(modellostposts.getEmail());
                Glide.with(holder.checklostimageIv.getContext()).load(modellostposts.getImagelink()).into(holder.checklostimageIv);
                holder.Claimbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String emailsend = modellostposts.getEmail();
                        String emailsubject ="Hey ! I have lost my object";
                        String emailbody = "Thats my thing i lost it somewhere yesterday";
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsend});
                        intent.putExtra(Intent.EXTRA_SUBJECT, emailsubject);
                        intent.putExtra(Intent.EXTRA_TEXT, emailbody);
                        intent.setType("message/rfc822");
                        startActivity(Intent.createChooser(intent,"Choose an Email Client:"));


                    }
                });


            }








            @NonNull
            @Override
            public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_found_posts, parent, false);
                return new viewHolder(view) {
                };
            }
        };
        postfoundlist.setAdapter(adapter);


    }

    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }









    public class viewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root ;


        //views from xml file

        ImageView checklostimageIv;
        TextView FullNametv, datetv, timetv, Messagetv,Phonetv,Emailtv;
        Button Claimbutton;


        public viewHolder(@NonNull View itemView) {
            super(itemView);
            //init views

            root = itemView.findViewById(R.id.list_root);
            checklostimageIv = itemView.findViewById(R.id.checklostimageviewIv);
            FullNametv = itemView.findViewById(R.id.checklostusername);

            datetv = itemView.findViewById(R.id.checklostdate);
            timetv = itemView.findViewById(R.id.checklosttime);
            Phonetv = itemView.findViewById(R.id.lostpostphonetv);
            Emailtv = itemView.findViewById(R.id.lostpostemailtv);
            Messagetv = itemView.findViewById(R.id.check_lost_message);
            Claimbutton = itemView.findViewById(R.id.Ifounditbutton);


        }




        public void setFullNametv(String string){
            FullNametv.setText(string);
        }
        public void setMessagetv(String string){
            Messagetv.setText(string);
        }
        public void setDatetv(String string){
            datetv.setText(string);
        }





        public void setTimetv(String string){
            timetv.setText(string);}
        public void setPhonetv(String string){ Phonetv.setText(string);}
        public void setEmailtv(String string){ Emailtv.setText(string);}
    }




}
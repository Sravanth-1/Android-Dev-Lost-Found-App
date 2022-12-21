package com.example.lostandfoundapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class postLost extends AppCompatActivity {

    private ImageSwitcher imageIS;
    private Button addimagefound,submitlost;
    private EditText messagepostLost;
    private static final int  PICK_IMAGES_CODE = 0 ;
    int position =0 ;
    private ArrayList<Uri> imageUris;

    private String current_user_id;
    private String messageLost;
    private String saveCurrentDate,saveCurrentTime ,Postlostname;
    private StorageReference PostlostReference;
    private DatabaseReference reference,postfoundtref;

    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_lost);
        PostlostReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("Registered Users");

        authProfile = FirebaseAuth.getInstance();
        current_user_id =  authProfile.getCurrentUser().getUid();




        messagepostLost = findViewById(R.id.lostMessage);

        progressBar = findViewById(R.id.progressLost);



        imageIS = findViewById(R.id.imageSwitcherLost);



        imageUris = new ArrayList<>();
        imageIS.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });

        submitlost = findViewById(R.id.submitLost);
        Button buttonaddimagefound = findViewById(R.id.addimagebuttonlost);




        submitlost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidatePostFound();
                progressBar.setVisibility(View.VISIBLE);

            }
        });


        buttonaddimagefound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImagesintent();


            }
        });

    }
    private void ValidatePostFound() {


        messageLost =  messagepostLost.getText().toString();
        if(imageUris == null ){
            Toast.makeText(postLost.this, "Please select an image ", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(messageLost)){
            Toast.makeText(postLost.this, "Please add a message", Toast.LENGTH_SHORT).show();
        }


        else{
            StoringImagetoFirebaseStorage();
        }

    }

    private void StoringImagetoFirebaseStorage() {


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        Postlostname = current_user_id+  saveCurrentDate + saveCurrentTime   ;

        StorageReference ImagefoundFolder = FirebaseStorage.getInstance().getReference().child("LostItems");

        for(int j =0 ; j < imageUris.size() ; j++){
            Uri IndividualImage = imageUris.get(j);
            StorageReference ImageName  = ImagefoundFolder.child("Image" + IndividualImage.getLastPathSegment() + Postlostname + ".jpg " );
            ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url =  String.valueOf(uri);
                            StoreLink(url);

                        }
                    });
                }
            });
        }



    }

    private void StoreLink(String url) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("LostItems");
        reference.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String namelost = snapshot.child("FullName").getValue().toString();
                    String phonelost= snapshot.child("PhoneNumber").getValue().toString();
                    String emaillost = snapshot.child("Email").getValue().toString();


                    HashMap postlostMap = new HashMap();
                    postlostMap.put("date",saveCurrentDate);
                    postlostMap.put("Email",emaillost);
                    postlostMap.put("Imagelink",url);




                    postlostMap.put("time",saveCurrentTime);
                    postlostMap.put("uid",current_user_id);
                    postlostMap.put("FullName",namelost);

                    postlostMap.put("PhoneNumber",phonelost);
                    postlostMap.put("Message",messageLost);

                    databaseReference.child(Postlostname).updateChildren(postlostMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){

                                sendusertopostmainactivity();
                                progressBar.setVisibility(View.GONE);


                                Toast.makeText(postLost.this, "Post is updated succesfully ", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(postLost.this, "Error occurred while updating your post", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void sendusertopostmainactivity() {
        Intent intent = new Intent(postLost.this,postLost.class);
        startActivity(intent);

    }


    private void pickImagesintent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(Intent.createChooser(intent,"Select Image(s)"),PICK_IMAGES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGES_CODE){
            if(data.getClipData()!=null){

                int count = data.getClipData().getItemCount();

                for(int i =0 ; i < count ; i++ ){
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
                imageIS.setImageURI(imageUris.get(0));
                position = 0;
            }
            else{
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
                imageIS.setImageURI(imageUris.get(0));
                position = 0;
            }
        }
    }
}

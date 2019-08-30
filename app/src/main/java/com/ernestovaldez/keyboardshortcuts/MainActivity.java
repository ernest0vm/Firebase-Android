package com.ernestovaldez.keyboardshortcuts;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.ernestovaldez.keyboardshortcuts.DTO.Shortcut;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends MainBaseActivity {

    public static final int READ_STORAGE_REQUEST_CODE = 1997;
    public static final String SHORTCUT_CHANNEL_ID = "SHORTCUT_CHANNEL_ID";
    public static final int GALLERY_REQUEST_CODE = 30;
    public static final String VOTE_RECEIVER = "VOTE_RECEIVER";
    public static final String SHORTCUT_ID = "SHORTCUT_ID";
    public static final String VOTE_TYPE = "VOTE_TYPE";
    public static final int VOTE_UP = 1;
    public static final int VOTE_DOWN = 0;

    @BindView(R.id.edtShortcutName)
    EditText edtShortcutName;

    @BindView(R.id.actKeys)
    AutoCompleteTextView actKeys;

    @BindView(R.id.lblAllKeys)
    TextView lblAllKeys;

    @BindView(R.id.imageView)
    ImageView image;

    List<String> allKeys;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(getApplicationContext());

        allKeys = new ArrayList<String>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                createAndShowNotification();
            }
        });

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("root").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children){
                    Shortcut shortcut = child.getValue(Shortcut.class);
                    Toast.makeText(getApplicationContext(), shortcut.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //initialize our notification channel
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        //define the channel
        String name = "Shortcuts";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(SHORTCUT_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("A channel for shortcuts");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void createAndShowNotification(){

        //this is what we want the pending intent do.
        Intent intent = new Intent(this, VoteReceiver.class);
        intent.setAction(VOTE_RECEIVER);
        intent.putExtra(SHORTCUT_ID, 0);
        intent.putExtra(VOTE_TYPE, VOTE_UP);

        //now, wrap our intent in a pending intent.
        PendingIntent votePendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //this is what we want the pending intent do.
        Intent downIntent = new Intent(this, VoteReceiver.class);
        downIntent.setAction(VOTE_RECEIVER);
        downIntent.putExtra(SHORTCUT_ID, 0);
        downIntent.putExtra(VOTE_TYPE, VOTE_DOWN);

        //now, wrap our intent in a pending intent.
        PendingIntent voteDownPendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 20, downIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, SHORTCUT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notification Title")
                .setContentText("Notification Text")
                .addAction(R.drawable.ic_thumb_up_24dp, "Vote Up", votePendingIntent)
                .addAction(R.drawable.ic_thumb_down_24dp, "Vote Down", voteDownPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source source = ImageDecoder.createSource(getResources(), R.raw.testpicture);

            try {
                Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));

                //image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1,builder.build());
    }

    @OnClick(R.id.btnAddKey)
    public void onBtnAddKeyClicked(){
        String key = actKeys.getText().toString();
        allKeys.add(key);
        String currentKeys = lblAllKeys.getText().toString();
        lblAllKeys.setText(currentKeys + key + " ");
        actKeys.setText("");
    }

    @OnClick(R.id.btnOpenGallery)
    public void onBtnOpenGalleryClicked(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            openGallery();

        } else {

            String[] permissionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionRequest, READ_STORAGE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //are we hearing back from read storage?
        if(requestCode == READ_STORAGE_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //we are ok to open the gallery
                openGallery();
            }else {
                //the permission was not granted
                Toast.makeText(this, R.string.storage_permission, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openGallery(){
        //Toast.makeText(this, "Gallery Opened", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_PICK);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Uri data = Uri.parse(pictureDirectory.getPath());
        String type = "image/*";
        intent.setDataAndType(data, type);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == GALLERY_REQUEST_CODE){

                assert data != null;
                imageUri = data.getData();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);

                    try {
                        Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                        image.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @OnClick(R.id.btnSave)
    public void saveShortcut(){

        if(imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();

            final StorageReference imageRef = storageReference.child("images/" + imageUri.getLastPathSegment());
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO handle this error
                    int i = 1 + 1;

                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageMetadata metadata = taskSnapshot.getMetadata();
                    Task<Uri> downloadUrlTask = imageRef.getDownloadUrl();
                    downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String link = uri.toString();
                            int i = 1 + 1;
                        }
                    });
                    int i = 1 + 1;
                }
            });
        }

        Shortcut shortcut = new Shortcut();
        shortcut.setName(edtShortcutName.getText().toString());
        shortcut.setKeys(allKeys);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference();
        reference.child("root").push().setValue(shortcut);

        allKeys = new ArrayList<String>();
        lblAllKeys.setText("");
        edtShortcutName.setText("");
    }

}

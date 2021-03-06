package com.example.msphone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TalkActivity extends AppCompatActivity {

    private List<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;
    private Context mContext;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        initMsgs();
        inputText=(EditText) findViewById(R.id.input_text);
        send=(Button) findViewById(R.id.send);
        Button takePhoto = findViewById(R.id.take_photo);
        picture = findViewById(R.id.picture);
        mContext = TalkActivity.this;
        msgRecyclerView=(RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter=new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ????????????File????????????????????????????????????????????????????????????????????????output_image.jpg
                // ????????????????????????SD?????????????????????????????????
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");

                // ????????????????????????
                try {
                    // ??????????????????????????????????????????
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    // ????????????????????????
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // ??????Android??????????????????7.0
                if (Build.VERSION.SDK_INT >= 24) {
                    // ???File?????????????????????????????????Uri??????
                    imageUri = FileProvider.getUriForFile(TalkActivity.this, "com.example.lenovo.cameraalbumtest.fileprovider", outputImage);
                } else {
                    // ???File???????????????Uri???????????????Uri?????????output_image.jpg?????????????????????????????????
                    imageUri = Uri.fromFile(outputImage);
                }

                // ??????????????????
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, 100);
                } else {
                    // ??????????????????
                    startCamera();
                }

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content=inputText.getText().toString();
                if(!"".equals(content)){
                    Msg msg=new Msg(content,Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
                }
            }
        });
    }

    private void startCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // ??????????????????????????????imageUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
    // ??????startActivityForResult()????????????Intent?????????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (requestCode == RESULT_OK) {
                    try {
                        // ??????????????????Bitmap??????
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        // ?????????????????????
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // ??????????????????
                    startCamera();
                } else {
                    Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    private void initMsgs(){
        Msg msg1=new Msg("(????????????)?????????",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2=new Msg("??????",Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3=new Msg("?????????",Msg.TYPE_RECEIVED);
        msgList.add(msg3);

    }
}
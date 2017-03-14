package com.example.bmobdemo.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.bmobdemo.R;
import com.example.bmobdemo.javabean.Person;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


public class AddActivity extends AppCompatActivity {
    private static final String TAG = "AddActivity";
    public final static int ACTIVITY_RESULT_CAMERA = 0001;//选择拍照的返回码
    public final static int ACTIVITY_RESULT_ALBUM = 0002;//选择相册的返回码
    public final static int CROP_HEAD = 0003;//处理头像照片
    private Button mSave;
    private TextInputLayout mTilUserName, mTilAddress,mTilStuNum,mTilPhoneNum;
    private FloatingActionButton mBack;
    private ImageView mHeader;
    private ProgressDialog dialog;
    private BmobFile mBmobFile;
   private  RadioButton mIsBoy,mIsGirl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Bmob.initialize(AddActivity.this, "f83c7903132eea0295b25c60662fc548");
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
        mBack = (FloatingActionButton) findViewById(R.id.back);
        mSave = (Button) findViewById(R.id.save);
        mHeader = (ImageView) findViewById(R.id.header);
        mIsBoy = (RadioButton) findViewById(R.id.rd_add_boy);
        mIsGirl = (RadioButton) findViewById(R.id.rd_add_girl);
        mTilUserName = (TextInputLayout) findViewById(R.id.til_username);
        mTilAddress = (TextInputLayout) findViewById(R.id.til_address);
        mTilStuNum = (TextInputLayout) findViewById(R.id.til_studentNumber);
        mTilPhoneNum = (TextInputLayout) findViewById(R.id.til_phoneNum);
        mTilUserName.setHint("UserName");
        mTilAddress.setHint("Address");
        mTilStuNum.setHint("StudentNumber");
        mTilPhoneNum.setHint("PhoneNum");
        dialog = new ProgressDialog(AddActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setTitle("正在上传");
        dialog.setMax(100);
        initData();

    }

    private void initData() {
        //头像操作
        mHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerDialog = new AlertDialog.Builder(AddActivity.this);
                alerDialog.setTitle("选择头像")
                        .setNegativeButton("相册", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                openAlbum();

                            }
                        })
                        .setPositiveButton("拍照", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                takePhone();
                            }
                        }).show();
            }
        });
        //将数据保存到后端云
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String UserName = mTilUserName.getEditText().getText().toString();
                final String Address = mTilAddress.getEditText().getText().toString();
               final String StuNum = mTilStuNum.getEditText().getText().toString();
                final String PhoneNum = mTilPhoneNum.getEditText().getText().toString();
                final boolean IsBoy = mIsBoy.isChecked();
                if (UserName.equals("") || Address.equals("") || StuNum.equals("") ||PhoneNum.equals("")) {
                    Toast.makeText(AddActivity.this, "信息不能为空！！！", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if(mBmobFile !=null){
                        dialog.show();
                        dialog.setProgress(0);
                        mBmobFile.uploadblock(new UploadFileListener() {
                            @Override
                            public void done(BmobException e) {
                                Person person = new Person(UserName,StuNum,IsBoy,Address,mBmobFile,PhoneNum);
                                person.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            dialog.dismiss();
                                            Toast.makeText(AddActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(AddActivity.this, "failure" + e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onProgress(Integer value) {
                                super.onProgress(value);
                                dialog.setProgress(value);
                                dialog.show();
                            }
                        });
                    }else{
                        Toast.makeText(AddActivity.this, "头像不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //返回主界面
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
//屏蔽返回键
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_HOME:return true;
            case KeyEvent.KEYCODE_BACK:return true;
            case KeyEvent.KEYCODE_CALL:return true;
            case KeyEvent.KEYCODE_SYM: return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
            case KeyEvent.KEYCODE_VOLUME_UP: return true;
            case KeyEvent.KEYCODE_STAR: return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //打开相册
    private void openAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ACTIVITY_RESULT_ALBUM);
    }

    //打开相机
    private void takePhone() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, ACTIVITY_RESULT_CAMERA);
    }

    //裁剪照片
    private void getHeader(Uri imgUri) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(imgUri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_HEAD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle bundle = null;
        Bitmap bitmap = null;
        switch (requestCode) {
            case ACTIVITY_RESULT_CAMERA:
                Date date = new Date();
                SimpleDateFormat sp = new SimpleDateFormat("yyyyMMdd_hhmmss");
                String name = sp.format(date);
                Toast.makeText(this, name, Toast.LENGTH_LONG).show();
                bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                Uri cameraUri = Uri.parse(MediaStore.Images.Media.insertImage(
                        getContentResolver(), bitmap, null, null));
                getHeader(cameraUri);
                break;
            case ACTIVITY_RESULT_ALBUM:
                Uri albumUri = data.getData();
                getHeader(albumUri);
                break;
            case CROP_HEAD:
                bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
                        getContentResolver(), bitmap, null, null));
                File file = new File(getRealFilePath(AddActivity.this, uri));
                Log.d(TAG, "file: " + file.getAbsolutePath());
                if (file != null) {
                    mBmobFile = new BmobFile(file);
                }
                    Bitmap bmp = bundle.getParcelable("data");
                    mHeader.setImageBitmap(bmp);


                break;
            default:
                break;
        }
    }

    //从URI中获取文件路径
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
}

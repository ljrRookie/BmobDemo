package com.example.bmobdemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bmobdemo.R;
import com.example.bmobdemo.javabean.Person;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by user on 2017/3/13.
 */
public class UpDateActivity extends Activity {
    private static final int READDATA = 0001;//读取数据
    public final static int ACTIVITY_RESULT_CAMERA = 0001;//选择拍照的返回码
    public final static int ACTIVITY_RESULT_ALBUM = 0002;//选择相册的返回码
    public final static int CROP_HEAD = 0003;//处理头像照片
    private static final String TAG = "UpDateActivity";
    private ScrollView mScrollView;
    private ImageView mUpdateHeader;
    private EditText mUpdateUserName;
    private EditText mUpdateAddress;
    private EditText mUpdateStuNum;
    private EditText mUpdatePhoneNum;
    private RadioButton mUpdateBoy;
    private RadioButton mUpdateGirl;
    private Button mUpdate;
    private BmobFile mBmobFile;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READDATA:
                    mPerson = (Person) msg.obj;
                    if (mPerson != null) {
                        readData(mPerson);
                        Toast.makeText(UpDateActivity.this, "读取数据成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpDateActivity.this, "读取数据失败！", Toast.LENGTH_SHORT).show();

                    }

                    break;
                default:
                    break;
            }
        }

        private void readData(Person person) {
            Glide.with(UpDateActivity.this)
                    .load(person.getHeader().getFileUrl())
                    .into(mUpdateHeader);
            mUpdateUserName.setText(person.getName());
            mUpdateAddress.setText(person.getAddress());
            mUpdatePhoneNum.setText(person.getPhoneNum());
            mUpdateStuNum.setText(person.getStudentNumber());
            if (person.isBoy()) {
                mUpdateBoy.setChecked(true);
            } else {
                mUpdateGirl.setChecked(true);
            }
        }
    };
    private ProgressDialog mDialog;
    private Person mPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String bql = "select * from Person where name = ?";
        BmobQuery<Person> query = new BmobQuery<Person>();
        //设置SQL语句
        query.setSQL(bql);
        //设置占位符参数
        query.setPreparedParams(new Object[]{name});
        query.doSQLQuery(new SQLQueryListener<Person>() {

            @Override
            public void done(BmobQueryResult<Person> result, BmobException e) {
                if (e == null) {
                    List<Person> list = (List<Person>) result.getResults();
                    if (list != null && list.size() > 0) {
                        Person person = list.get(0);
                        Message msg = Message.obtain();
                        msg.what = READDATA;
                        msg.obj = person;
                        mHandler.sendMessage(msg);
                    } else {
                        Log.i("smile", "查询成功，无数据返回");
                        Toast.makeText(UpDateActivity.this, "无数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UpDateActivity.this, "读取失败！", Toast.LENGTH_SHORT).show();
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        });
    }

    private void initView() {
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mUpdateHeader = (ImageView) findViewById(R.id.update_header);
        mUpdateUserName = (EditText) findViewById(R.id.update_userName);
        mUpdateAddress = (EditText) findViewById(R.id.update_address);
        mUpdateStuNum = (EditText) findViewById(R.id.update_stuNum);
        mUpdatePhoneNum = (EditText) findViewById(R.id.update_phoneNum);
        mUpdateBoy = (RadioButton) findViewById(R.id.update_boy);
        mUpdateGirl = (RadioButton) findViewById(R.id.update_girl);
        mUpdate = (Button) findViewById(R.id.update);
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("正在加载中……");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setMax(100);
    }

    //修改头像
    public void updateHeader(View view) {
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(UpDateActivity.this);
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

    public void update(View view) {
        final String userNmae = mUpdateUserName.getText().toString().trim();
        final String address = mUpdateAddress.getText().toString().trim();
        final String stuNum = mUpdateStuNum.getText().toString().trim();
        final String phoneNum = mUpdatePhoneNum.getText().toString().trim();
        final boolean isBoy = mUpdateBoy.isChecked();
        if(userNmae.equals("") || address.equals("") || stuNum.equals("") ||phoneNum.equals("")){
            Toast.makeText(this, "修改信息不能为空！！", Toast.LENGTH_SHORT).show();
        }else{
            //修改了头像
            if(mBmobFile!=null){
                mDialog.show();
                mDialog.setProgress(0);
                mBmobFile.uploadblock(new UploadFileListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            mPerson.setHeader(mBmobFile);
                            mPerson.setName(userNmae);
                            mPerson.setAddress(address);
                            mPerson.setPhoneNum(phoneNum);
                            mPerson.setStudentNumber(stuNum);
                            mPerson.setBoy(isBoy);
                            mPerson.update(mPerson.getObjectId(), new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        mDialog.dismiss();
                                        Toast.makeText(UpDateActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(UpDateActivity.this, MainActivity.class));
                                        finish();
                                    }else {
                                        mDialog.dismiss();
                                        Toast.makeText(UpDateActivity.this, "修改失败！", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(UpDateActivity.this, "头像修改失败！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgress(Integer value) {
                        super.onProgress(value);
                        mDialog.setProgress(value);
                        mDialog.show();
                    }
                });
            }else{
                mDialog.show();
                mDialog.setProgress(0);
                mPerson.setName(userNmae);
                mPerson.setAddress(address);
                mPerson.setPhoneNum(phoneNum);
                mPerson.setStudentNumber(stuNum);
                mPerson.setBoy(isBoy);
                mPerson.update(mPerson.getObjectId(),new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            mDialog.dismiss();
                            Toast.makeText(UpDateActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(UpDateActivity.this, MainActivity.class));
                        }else {
                            mDialog.dismiss();
                            Toast.makeText(UpDateActivity.this, "没改头像修改失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
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
                File file = new File(getRealFilePath(UpDateActivity.this, uri));
                Log.d(TAG, "file: " + file.getAbsolutePath());
                if (file != null) {
                    mBmobFile = new BmobFile(file);
                }
                Bitmap bmp = bundle.getParcelable("data");
                mUpdateHeader.setImageBitmap(bmp);
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

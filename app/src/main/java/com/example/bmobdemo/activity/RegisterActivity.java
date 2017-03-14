package com.example.bmobdemo.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.bmobdemo.R;
import com.example.bmobdemo.javabean.User;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


public class RegisterActivity extends AppCompatActivity {
    public final static int ACTIVITY_RESULT_CAMERA = 0001;//选择拍照的返回码
    public final static int ACTIVITY_RESULT_ALBUM = 0002;//选择相册的返回码
    public final static int CROP_HEAD = 0003;//处理头像照片
    public final static int REGIST_SUCCESS = 1000;//登录成功
    private static final String TAG = "RegisterActivity";
    private TextInputLayout mTilUsername;
    private TextInputLayout mTilPassword;
    private TextInputLayout mTilRepassword;
    private TextInputLayout mTilCode;

    public Button mGetcode;
    private Button mRegister;
    private MyTimer mMyTimer;
    private String mUserName;
    private BmobFile mBmobFile;
    private ScrollView mScrollView;
    private ImageView mHeader;
    private RadioButton mIsBoy;
    private RadioButton mIsGirl;
    private ProgressDialog dialog;
    private File mFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(RegisterActivity.this, "f83c7903132eea0295b25c60662fc548");
        setContentView(R.layout.activity_register);
        initView();

    }


    private void initView() {
        mTilUsername = (TextInputLayout) findViewById(R.id.til_username);
        mTilPassword = (TextInputLayout) findViewById(R.id.til_password);
        mTilRepassword = (TextInputLayout) findViewById(R.id.til_repassword);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mHeader = (ImageView) findViewById(R.id.header);
        mTilCode = (TextInputLayout) findViewById(R.id.til_code);
        mGetcode = (Button) findViewById(R.id.getcode);
        mRegister = (Button) findViewById(R.id.register);
        mIsBoy = (RadioButton) findViewById(R.id.isBoy);
        mIsGirl = (RadioButton) findViewById(R.id.isGirl);
        dialog = new ProgressDialog(RegisterActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setTitle("正在注册");
        dialog.setMax(100);
    }

    public void header(View view) {
        AlertDialog.Builder alerDialog = new AlertDialog.Builder(RegisterActivity.this);
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


    //====获取验证码======
    public void getCode(View view) {
        mUserName = mTilUsername.getEditText().getText().toString().trim();
        mMyTimer = new MyTimer(60000, 1000);//倒数60秒，每1秒更新一次。
        mMyTimer.start();
        mUserName = mTilUsername.getEditText().getText().toString().trim();
        if (mUserName.equals("") || mUserName.length() != 11) {
            Toast.makeText(this, "对不起，发送失败!请正确输入手机号", Toast.LENGTH_LONG).show();
        } else {
            BmobSMS.requestSMSCode(mUserName, "register", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {// 验证码发送成功
                        Log.i("bmob", "短信id：" + integer);// 用于查询本次短信发送详情}
                        Toast.makeText(RegisterActivity.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "验证码发送失败" + "bmoberrorCode = " + e.getErrorCode() + ",errorMsg = " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "bmoberrorCode = " + e.getErrorCode() + ",errorMsg = " + e.getLocalizedMessage());

                    }
                }
            });

        }
    }

    public void register(View view) {
        mUserName = mTilUsername.getEditText().getText().toString().trim();
        final String passWord = mTilPassword.getEditText().getText().toString().trim();
        String rePassWord = mTilRepassword.getEditText().getText().toString().trim();
        final String registerCode = mTilCode.getEditText().getText().toString().trim();
        final boolean isBoy = mIsBoy.isChecked();
        if (mUserName.equals("") || passWord.equals("") || rePassWord.equals("") || registerCode.equals("")) {
            Toast.makeText(this, "注册信息不能为空！！！", Toast.LENGTH_SHORT).show();
        } else {
            if (passWord.equals(rePassWord)) {
                if (mBmobFile != null) {
                    dialog.show();
                    dialog.setProgress(0);
                    mBmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                BmobSMS.verifySmsCode(mUserName, registerCode, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            registerWithHeader(passWord, isBoy);
                                        }else{
                                            dialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "验证码错误！", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "done: "+e.getErrorCode()+"/"+e.getMessage());
                                            return;
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "上传失败!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "done: "+e.getErrorCode()+"/"+e.getMessage());
                            }
                        }

                        @Override
                        public void onProgress(Integer value) {
                            super.onProgress(value);
                            dialog.setProgress(value);
                            dialog.show();
                        }
                    });
                } else {
                    Toast.makeText(this, "头像不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                Toast.makeText(this, "密码不一致！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerWithHeader(final String passWord, boolean isBoy) {
        User user = new User();
        user.setUsername(mUserName);
        user.setPassword(passWord);
        user.setBoy(isBoy);
        user.setHeader(mBmobFile);
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User bmobUser, BmobException e) {
                if (e == null) {
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("username",mUserName);
                    intent.putExtra("password",passWord);
                    setResult(REGIST_SUCCESS);
                    startActivityForResult(intent,REGIST_SUCCESS);
                } else {
                    dialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "bmoberrorCode = " + e.getErrorCode() + ",errorMsg = " + e.getLocalizedMessage());
                }
            }
        });
    }


    //获取验证码倒计时
    public class MyTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long millisUntilFinished) {
            mGetcode.setClickable(false);
            mGetcode.setText(millisUntilFinished / 1000 + "s");

        }

        //计时完成
        @Override
        public void onFinish() {
            mGetcode.setClickable(true);
            mGetcode.setText("重新获取验证码");
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
                File file = new File(getRealFilePath(RegisterActivity.this, uri));
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

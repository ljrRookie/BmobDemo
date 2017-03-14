package com.example.bmobdemo.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bmobdemo.Adapter.MyAdapter;
import com.example.bmobdemo.Listener.BmobListener;
import com.example.bmobdemo.R;
import com.example.bmobdemo.javabean.Person;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity implements BmobListener {
    private static final String TAG = "MainActivity";
    private static final int INITDATAS = 0;

    private RecyclerView mRecyclerview;
    private FloatingActionButton mAdd;
    private List<Person> mPersonList = new ArrayList<>();
    private MyAdapter mMyAdapter;
    private SwipeRefreshLayout mSwiperRefresh;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INITDATAS:
                    Toast.makeText(MainActivity.this, "读取数据库成功！", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化Bmob
        Bmob.initialize(this, "f83c7903132eea0295b25c60662fc548");
        //初始化View
        initView();
        //读取数据
        initDatas();
    }

    private void initDatas() {
        mSwiperRefresh.setRefreshing(true);
        BmobQuery<Person> query = new BmobQuery<>();
        query.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if (list != null) {
                    mPersonList=list;
                } else {
                    Toast.makeText(MainActivity.this, "读取数据失败！" + e.toString(), Toast.LENGTH_SHORT).show();
                }
                //更新数据
                mMyAdapter = new MyAdapter(mPersonList,MainActivity.this);
                mMyAdapter.RefreshList(mPersonList);
                mMyAdapter.setBmobListener(MainActivity.this);
                mRecyclerview.setAdapter(mMyAdapter);
                mSwiperRefresh.setRefreshing(false);
            }
        });
    }

    private void initView() {
        mAdd = (FloatingActionButton) findViewById(R.id.add);
        mSwiperRefresh = (SwipeRefreshLayout) findViewById(R.id.swiper_refresh);
        mSwiperRefresh.setColorSchemeResources(R.color.colorAccent);
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mSwiperRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initDatas();
            }
        });
        //添加数据
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    @Override
    public void deleteData(Person person) {
        person.setObjectId(person.getObjectId());
        person.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "done: "+e.toString());
                }
            }
        });
        initDatas();
    }

    @Override
    public void UpDateData(String name) {
        Intent intent = new Intent(this, UpDateActivity.class);
        intent.putExtra("name",name);
        startActivity(intent);
    }
}

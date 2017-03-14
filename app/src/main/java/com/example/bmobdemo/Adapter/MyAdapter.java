package com.example.bmobdemo.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bmobdemo.Listener.BmobListener;
import com.example.bmobdemo.R;
import com.example.bmobdemo.javabean.Person;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by user on 2017/3/6.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Person> mPersonList;
    private BmobListener mBmobListener;
    private Context mContext;
    private static final String TAG = "MyAdapter";
    public void setBmobListener(BmobListener Listener) {
        mBmobListener = Listener;
    }
    public MyAdapter(List<Person> personList,Context context) {
        this.mPersonList = personList;
        this.mContext=context;
    }
public void RefreshList(List<Person> personList){
    mPersonList = personList;
    notifyDataSetChanged();
}

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mName, mAddress;
        private ImageView mRemove;
        private View mItem;
        private ImageView mIsBoy;


        public ViewHolder(View itemView) {
            super(itemView);
            mItem = itemView;
            mIsBoy = (ImageView) itemView.findViewById(R.id.is_Boy);
            mImage = (ImageView) itemView.findViewById(R.id.img);
            mName = (TextView) itemView.findViewById(R.id.name);
            mAddress = (TextView) itemView.findViewById(R.id.address);
            mRemove = (ImageView) itemView.findViewById(R.id.remove);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //初始化Bmob

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Person person = mPersonList.get(position);
        holder.mName.setText(person.getName());
        holder.mAddress.setText(person.getAddress());
        if(person.isBoy()){
            holder.mIsBoy.setImageResource(R.drawable.man);
        }else{
            holder.mIsBoy.setImageResource(R.drawable.woman);
        }
        if(person.getHeader().getFileUrl() != null){
            Glide.with(mContext).load(person.getHeader().getFileUrl()).into(holder.mImage);
        }
        holder.mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBmobListener.deleteData(person);
            }
        });
        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBmobListener.UpDateData(person.getName());
            }
        });

            }

            @Override
            public int getItemCount() {
                if (mPersonList.size() != 0) {
                    return mPersonList.size();
                }
                return 0;
            }
        }

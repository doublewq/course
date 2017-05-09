package com.mxn.soul.flowingdrawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijie.liu on 17/04/2017.
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseInfoViewHolder> {

    private Context mContext;

    private List<ClassInfo1> mData;

    CourseAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>(0);
    }

    @Override
    public CourseInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_course, parent, false);

        return new CourseInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseInfoViewHolder holder, int position) {
        ClassInfo1 data = mData.get(position);
        if (data == null) {
            return;
        }
        holder.title.setText("星期"+data.getWeek());
        holder.author.setText("-第"+data.getLesson()+"节");
        holder.info.setText("");
        holder.content.setText("详情："+data.getInfo());
    }

    void addCourseData(List<ClassInfo1> datas) {
        for (ClassInfo1 data : datas) {
            mData.add(data);
        }
    }

    void setCourseData(List<ClassInfo1> datas) {
        if (datas != null) {
            mData = datas;
        } else {
            mData = new ArrayList<>(0);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class CourseInfoViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;
        TextView info;
        TextView lookMore;
        TextView author;

        public CourseInfoViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.book_title);
            content = (TextView) itemView.findViewById(R.id.content);
            info = (TextView) itemView.findViewById(R.id.store_info);
            lookMore = (TextView) itemView.findViewById(R.id.load_raw);
            author = (TextView) itemView.findViewById(R.id.author);
        }
    }
}

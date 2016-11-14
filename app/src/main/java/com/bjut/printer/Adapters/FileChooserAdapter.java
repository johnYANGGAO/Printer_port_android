package com.bjut.printer.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bjut.printer.bean.FileInfo;
import com.bjut.printer2.R;

import java.util.ArrayList;

/**
 * Created by johnsmac on 9/12/16.
 */
public class FileChooserAdapter extends BaseAdapter {


   private ArrayList<FileInfo> fileinfos=new ArrayList<FileInfo>();
   private Context context;

    public FileChooserAdapter(Context mContext,ArrayList<FileInfo> list){
        this.context=mContext;
        this.fileinfos=list;
    }

    public int getCount() {
        return fileinfos.size();
    }

    public Object getItem(int position) {
        return fileinfos.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = View.inflate(context, R.layout.filechooselist_item, null);
            holder = new ViewHolder();
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_applock_icon);
            holder.tv_name = (TextView) view.findViewById(R.id.filename);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        FileInfo fileinfo = fileinfos.get(position);
        holder.tv_name.setText(fileinfo.getName());
        if (fileinfo.isIsdir()) {
            holder.iv_icon.setImageResource(R.drawable.dir1);
        } else {
            holder.iv_icon.setImageResource(R.drawable.pic);
        }

        return view;
    }

    public static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
    }
}



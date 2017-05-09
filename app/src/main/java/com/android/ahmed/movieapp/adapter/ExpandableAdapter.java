package com.android.ahmed.movieapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ahmed.movieapp.R;
import com.android.ahmed.movieapp.models.MovieTrailer;

import java.util.HashMap;
import java.util.List;


public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> st;
    private HashMap<String,List<MovieTrailer>>it;
    public ExpandableAdapter(Context context, List<String> st, HashMap<String,List<MovieTrailer>>it)
    {
this.context=context;
        this.st=st;
        this.it=it;
    }

    @Override
    public int getGroupCount() {
        return this.st.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return  this.it.get(this.st.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return this.st.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.it.get(this.st.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String title=(String)getGroup(i);
        LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.listgroup,null);
        TextView t2=(TextView)view.findViewById(R.id.textView);
        t2.setText(title);
        return view;
    }

    @Override
    public View getChildView(int i,final int i1, boolean b, View view, ViewGroup viewGroup) {
        MovieTrailer s=(MovieTrailer) getChild(i,i1);
        LayoutInflater inflater=(LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.listitem,null);
        TextView t1=(TextView)view.findViewById(R.id.textView2);
        Log.v("Trailer",s.getName());
        t1.setText(s.getName());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}

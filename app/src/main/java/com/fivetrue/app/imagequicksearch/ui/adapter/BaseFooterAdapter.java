package com.fivetrue.app.imagequicksearch.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public abstract class BaseFooterAdapter<V> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseAdapterImpl<V>{

    private static final String TAG = "BaseFooterAdapter";

    public interface OnItemClickListener<V>{
        void onItemClick(RecyclerView.ViewHolder holder, V item);
        boolean onItemLongClick(RecyclerView.ViewHolder holder, V item);
    }

    public static final int FOOTER = 0x99;

    private SparseBooleanArray mSelectedItems;
    private List<V> mData;

    private OnItemClickListener mOnItemClickListener;


    public BaseFooterAdapter(List<V> data){
        this.mData = data;
        mSelectedItems = new SparseBooleanArray();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == FOOTER){
            return onCreateFooterHolder(parent.getContext(), viewType);
        }
        return onCreateHolder(parent.getContext(), viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == FOOTER){
            onBindFooterHolder(holder, position);
        }else{
            onBindHolder(holder, position);
        }
    }

    private void onBindFooterHolder(RecyclerView.ViewHolder holder, int position){

    }

    protected abstract RecyclerView.ViewHolder onCreateFooterHolder(Context context, int viewType);

    protected abstract RecyclerView.ViewHolder onCreateHolder(Context context, int viewType);

    protected abstract void onBindHolder(final RecyclerView.ViewHolder holder, final int position);

    @Override
    public int getItemViewType(int position) {
        if(showFooter()){
            if(mData.size() == position){
                return FOOTER;
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public V getItem(int pos) {
        if(mData.size() > pos){
            return mData.get(pos);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        if(showFooter()){
            if (mData == null) {
                return 0;
            }

            if (mData.size() == 0) {
                //Return 1 here to show nothing
                return 1;
            }

            return mData.size() + 1;
        }else{
            return mData.size();
        }
    }

    @Override
    public List<V> getData() {
        return mData;
    }

    @Override
    public void setData(List<V> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public void add(V data) {
        mData.add(data);
        notifyItemChanged(mData.size());
    }

    @Override
    public void toggle(int pos) {
        if(getItemViewType(pos) == FOOTER){
            return;
        }
        mSelectedItems.put(pos, !mSelectedItems.get(pos));
        notifyItemChanged(pos);
    }

    @Override
    public boolean isSelect(int pos) {
        return mSelectedItems.get(pos);
    }

    @Override
    public void selection(int pos, boolean b) {
        if(getItemViewType(pos) == FOOTER){
            return;
        }

        mSelectedItems.put(pos, b);
        notifyItemChanged(pos);
    }

    @Override
    public void clearSelection() {
        mSelectedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public List<V> getSelections() {
        ArrayList<V> list = new ArrayList<>();
        for(int i = 0 ; i < getItemCount() ; i++){
            if(mSelectedItems.get(i)){
                list.add(getItem(i));
            }
        }
        return list;
    }

    @Override
    public void clear(){
        mData.clear();
        clearSelection();
    }

    public void setOnItemClickListener(OnItemClickListener<V> ll){
        mOnItemClickListener = ll;
    }

    protected void onClickItem(RecyclerView.ViewHolder holder, V item){
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(holder, item);
        }
    }

    protected boolean onLongClickItem(RecyclerView.ViewHolder holder, V item){
        if(mOnItemClickListener != null){
            return mOnItemClickListener.onItemLongClick(holder, item);
        }
        return false;
    }

    protected boolean showFooter(){
        return true;
    }
}

package com.fivetrue.app.imagequicksearch.ui.adapter;

import java.util.List;

/**
 * Created by ojin.kwon on 2016-11-18.
 */

public interface BaseAdapterImpl<T> {

    T getItem(int pos);
    int getItemCount();
    List<T> getData();
    void setData(List<T> data);
    void add(T data);
    void toggle(int pos);
    boolean isSelect(int pos);
    void selection(int pos, boolean b);
    void clearSelection();
    void clear();
    List<T> getSelections();
}

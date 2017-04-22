package com.fivetrue.app.imagequicksearch.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.app.imagequicksearch.LL;

/**
 * Created by ojin.kwon on 2016-11-18.
 */

public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    public abstract String getTitle(Context context);

    public String getSubTitle(Context context){
        return null;
    }

    public abstract int getImageResource();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LL.D)
            Log.d(TAG, getClass().getSimpleName() + " : onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onAttach() called with: context = [" + context + "]");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onDetach() called");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(LL.D)
            Log.d(TAG, getClass().getSimpleName() + " : onCreateView() called with: inflater = [" + inflater + "], container = [" + container + "], savedInstanceState = [" + savedInstanceState + "]");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(LL.D)
            Log.d(TAG, getClass().getSimpleName() + " : onActivityCreated() called with: savedInstanceState = [" + savedInstanceState + "]");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onDestroy() called");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onStart() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(LL.D) Log.d(TAG, getClass().getSimpleName() + " : onStop() called");
    }

    public boolean onBackPressed(){
        return false;
    }

    public Fragment addFragment(Class< ? extends BaseFragment> cls, Bundle arguments, int anchorLayout, boolean addBackstack
            , int enterAnim, int exitAnim, Object sharedTransition, Pair<View, String>... pair){
        BaseFragment f = null;
        try {
            f = (BaseFragment) cls.newInstance();
            if(sharedTransition != null){
                f.setSharedElementEnterTransition(sharedTransition);
                f.setSharedElementReturnTransition(sharedTransition);
            }
        } catch (java.lang.InstantiationException e) {
            Log.e(TAG, "addFragment: ", e);
        } catch (IllegalAccessException e) {
            Log.e(TAG, "addFragment: ", e);
        }
        if(f != null){
            if(arguments != null){
                f.setArguments(arguments);
            }
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim);
            ft.replace(anchorLayout, f, f.getTitle(getActivity()));
            if(pair != null && pair.length > 0){
                for(Pair<View, String> p : pair){
                    ft.addSharedElement(p.first, p.second);
                }
            }
            if(addBackstack){
                ft.addToBackStack(f.getTitle(getActivity()));
                ft.setBreadCrumbTitle(f.getTitle(getActivity()));
                ft.setBreadCrumbShortTitle(f.getSubTitle(getActivity()));
            }
            ft.commitAllowingStateLoss();
        }
        return f;
    }
}

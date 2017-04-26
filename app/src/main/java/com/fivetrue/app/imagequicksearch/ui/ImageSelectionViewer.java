package com.fivetrue.app.imagequicksearch.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivetrue.app.imagequicksearch.LL;
import com.fivetrue.app.imagequicksearch.R;
import com.fivetrue.app.imagequicksearch.model.image.GoogleImage;
import com.fivetrue.app.imagequicksearch.provider.LocalFileProvider;
import com.fivetrue.app.imagequicksearch.utils.CommonUtils;
import com.fivetrue.app.imagequicksearch.utils.ImageStoreUtil;
import com.fivetrue.app.imagequicksearch.utils.SimpleViewUtils;
import com.fivetrue.app.imagequicksearch.utils.TrackingUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 23..
 */

public class ImageSelectionViewer extends LinearLayout {

    private static final String TAG = "ImageSelectionViewer";

    public interface ImageSelectionClient {
        List<GoogleImage> getSelections();
        String getKeyword();
        void clearSelection();
        void onSendFailed(GoogleImage failedImage);
        void onClickAction();
    }

    private View layout;

    private TextView mCount;
    private Button mCancel;
    private Button mAction;

    private ImageSelectionClient mSelectionClient;
    private boolean mSendAction = true;

    public ImageSelectionViewer(Context context) {
        super(context);
        initView(context, null);
    }

    public ImageSelectionViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ImageSelectionViewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attr){
        LayoutInflater.from(context).inflate(R.layout.image_selection_viewer, this);
        mCount = (TextView) findViewById(R.id.tv_image_selection_viewer);
        mCancel = (Button) findViewById(R.id.btn_image_selection_viewer_cancel);
        mAction = (Button) findViewById(R.id.btn_image_selection_viewer_action);
        mAction.setOnClickListener(view -> {
            if(mSendAction){
                if(CommonUtils.isOnline(getContext())){
                    if(CommonUtils.isMobileConnected(getContext())){
                        new AlertDialog.Builder(getContext())
                                .setTitle(android.R.string.dialog_alert_title)
                                .setMessage(R.string.use_mobile_network_message)
                                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    doAction();
                                })
                                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                                .show();
                    }else{
                        doAction();
                    }
                }else{
                    Toast.makeText(getContext(),  R.string.check_network_setting_message, Toast.LENGTH_SHORT).show();
                }
            }else{
                if(mSelectionClient != null){
                    mSelectionClient.onClickAction();
                }
            }
        });

        mCancel.setOnClickListener(view -> {
            if(mSelectionClient != null){
                mSelectionClient.clearSelection();
                update();
            }
        });

        if(attr != null){
            TypedArray a = context.obtainStyledAttributes(attr, R.styleable.ImageSelectionViewer);
            Drawable background = a.getDrawable(R.styleable.ImageSelectionViewer_actionButtonBackground);
            mSendAction = a.getBoolean(R.styleable.ImageSelectionViewer_sendAction, mSendAction);
            String actionText = a.getString(R.styleable.ImageSelectionViewer_actionButtonText);
            String cancelText = a.getString(R.styleable.ImageSelectionViewer_cancelButtonText);

            if(background != null){
                mAction.setBackground(background);
                mCancel.setBackground(background);
            }

            if(!TextUtils.isEmpty(actionText)){
                mAction.setText(actionText);
            }

            if(!TextUtils.isEmpty(cancelText)){
                mCancel.setText(cancelText);
            }
            a.recycle();
        }
    }

    private void doAction(){
        if(LL.D) Log.d(TAG, "doAction() called");
        if(mSelectionClient != null){
            List<GoogleImage> selectedImages = Observable.fromIterable(mSelectionClient.getSelections())
                    .distinct(GoogleImage::getOriginalImageUrl).toList().blockingGet();
            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setTitle(R.string.send);
            dialog.setMessage(getContext().getString(R.string.prepare_images_message));
            dialog.setCancelable(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
            dialog.setMax(selectedImages.size());
            /**
             * Checking Stored image
             */
            if(LL.D) Log.d(TAG, "send : try to convert image from network images size = " + selectedImages.size());

            Observable.create(e -> {
                for(GoogleImage gi : selectedImages){
                    ImageStoreUtil.getInstance(getContext())
                            .saveNetworkImage(gi, mSelectionClient.getKeyword())
                            .subscribe(file ->{
                                dialog.setProgress(dialog.getProgress() + 1);
                                e.onNext(file);
                            } ,throwable -> {
                                dialog.dismiss();
                                mSelectionClient.onSendFailed(gi);
                                mSelectionClient.clearSelection();
                                update();
                                Toast.makeText(getContext(), R.string.send_image_failure_message, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "send failure: ", throwable);
                            });
                }
            }).buffer(selectedImages.size())
                    .subscribe(objects -> {
                        dialog.dismiss();
                        internalSend(Observable.fromIterable(objects)
                                .map(o -> (File) o)
                                .toList().blockingGet());
                    }, throwable -> {
                        Toast.makeText(getContext(), R.string.send_image_failure_message, Toast.LENGTH_SHORT).show();
                        TrackingUtil.getInstance().report(throwable);
                        Log.e(TAG, "send failure: ", throwable);
                    });


        }
    }

    private void internalSend(List<File> files){
        if(LL.D) Log.d(TAG, "internal Send: uri = " + files);
//        Intent intent = makeSendIntent(files);
        ChooserActivity.startActivity(getContext(), files, getResources().getString(R.string.send));
        mSelectionClient.clearSelection();
        update();
    }

    private Intent makeSendIntent(List<File> files){
        Intent intent = null;
        if (files.size() == 1) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, LocalFileProvider.makeLocalFileUri(files.get(0)))
                    .setType("image/*");
        } else if (files.size() > 1) {
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> uris = new ArrayList<>(Observable.fromIterable(files)
                    .map(file -> LocalFileProvider.makeLocalFileUri(file))
                    .toList().blockingGet());

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                    .setType("image/*");
        }
        return intent;
    }

    public void update(){
        if(mSelectionClient != null){
            List<GoogleImage> selection = mSelectionClient.getSelections();
            if(LL.D) Log.d(TAG, "checkSelection() called count = " + selection.size());
            if(selection.size() > 0){
                show(selection);
            }else{
                hide();
            }
        }
    }

    public void show(List<GoogleImage> list){
        if(list != null){
            if(!isShown()){
                SimpleViewUtils.showView(this, View.VISIBLE);
            }
            mCount.setText(getContext().getString(R.string.selection_image, list.size()));
        }else{
            hide();
        }
    }

    public void hide(){
        if(isShown()){
            SimpleViewUtils.hideView(this, View.INVISIBLE);
        }
    }

    public void setSelectionClient(ImageSelectionClient client){
        mSelectionClient = client;
    }

    public void setSendAction(boolean b, String text) {
        mSendAction = b;
        if (mAction != null) {
            mAction.setText(mSendAction ? getResources().getString(R.string.send) : text);
        }
    }

    public void setSendAction(boolean b){
        setSendAction(b, getResources().getString(R.string.delete));
    }
}

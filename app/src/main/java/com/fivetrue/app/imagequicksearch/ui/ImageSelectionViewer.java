package com.fivetrue.app.imagequicksearch.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Created by kwonojin on 2017. 4. 23..
 */

public class ImageSelectionViewer extends LinearLayout {

    private static final String TAG = "ImageSelectionViewer";

    public interface ImageSelectInfo{
        List<GoogleImage> getSelections();
        String getKeyword();
        void clearSelection();
    }

    private TextView mCount;
    private Button mCancel;
    private Button mSend;

    private ImageSelectInfo mInfo;

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
        mSend = (Button) findViewById(R.id.btn_image_selection_viewer_send);

        mSend.setOnClickListener(view -> {
            if(CommonUtils.isOnline(getContext())){
                if(CommonUtils.isMobileConnected(getContext())){
                    new AlertDialog.Builder(getContext())
                            .setTitle(android.R.string.dialog_alert_title)
                            .setMessage(R.string.use_mobile_network_message)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                send();
                            })
                            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                            .show();
                }else{
                    send();
                }
            }else{
                Toast.makeText(getContext(),  R.string.check_network_setting_message, Toast.LENGTH_SHORT).show();
            }
        });

        mCancel.setOnClickListener(view -> {
            if(mInfo != null){
                mInfo.clearSelection();
                update();
            }
        });
    }

    private void send(){
        if(LL.D) Log.d(TAG, "send() called");
        if(mInfo != null){
            List<GoogleImage> selectedImages = mInfo.getSelections();
            ProgressDialog dialog = new ProgressDialog(getContext());
            dialog.setTitle(R.string.send);
            dialog.setMessage(getContext().getString(R.string.prepare_images_message));
            dialog.setCancelable(false);
            dialog.setProgressStyle(android.R.style.Widget_DeviceDefault_Light_ProgressBar_Horizontal);
            dialog.show();
            dialog.setMax(selectedImages.size());
            /**
             * Checking Stored image
             */
            if(LL.D) Log.d(TAG, "send: try to convert image from network images size = " + selectedImages.size());

            Observable.create(e -> {
                for(GoogleImage gi : selectedImages){
                    ImageStoreUtil.getInstance(getContext())
                            .saveNetworkImage(gi, mInfo.getKeyword())
                            .subscribe(file ->{
                                dialog.setProgress(dialog.getProgress() + 1);
                                e.onNext(file);
                            } ,throwable -> {
                                dialog.dismiss();
                                Log.e(TAG, "send: ", throwable);
                                Toast.makeText(getContext(), "Image send failure", Toast.LENGTH_SHORT).show();
                                mInfo.clearSelection();
                                update();
                            });
                }
            }).buffer(selectedImages.size())
                    .subscribe(objects -> {
                        dialog.dismiss();
                        internalSend(Observable.fromIterable(objects)
                                .map(o -> (File) o)
                                .toList().blockingGet());
                    });


        }
    }

    private void internalSend(List<File> files){
        if(LL.D) Log.d(TAG, "internal Send: uri = " + files);
        if(files != null){
            Intent intent = null;
            if(files.size() == 1){
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, LocalFileProvider.makeLocalFileUri(files.get(0)))
                        .setType("image/*");
            }else if(files.size() > 1){
                intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                ArrayList<Uri> uris = new ArrayList<>(Observable.fromIterable(files)
                        .map(file -> LocalFileProvider.makeLocalFileUri(file))
                        .toList().blockingGet());

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                        .setType("image/*");
            }

            if(intent != null){
                getContext().startActivity(Intent.createChooser(intent, getResources().getString(R.string.send)));
                mInfo.clearSelection();
                update();
            }
        }

    }

    public void update(){
        if(mInfo != null){
            List<GoogleImage> selection = mInfo.getSelections();
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

    public void setImageSelectorInfo(ImageSelectInfo info){
        mInfo = info;
    }
}

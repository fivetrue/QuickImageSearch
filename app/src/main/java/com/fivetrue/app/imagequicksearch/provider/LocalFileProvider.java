package com.fivetrue.app.imagequicksearch.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by kwonojin on 2017. 4. 20..
 */

public class LocalFileProvider extends ContentProvider {

    private static final String TAG = "LocalFileProvider";

    public static final String AUTHORITY = "com.fivetrue.app.imagequicksearch.provider";
    private UriMatcher uriMatcher;
    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "*", 1);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if(LL.D) Log.d(TAG, "openFile() called with: uri = [" + uri + "], mode = [" + mode + "]");
        switch (uriMatcher.match(uri)) {
            case 1:// If it returns 1 - then it matches the Uri defined in onCreate
                File localFile = new File(getContext().getFilesDir(), uri.getLastPathSegment());
                ParcelFileDescriptor pfd = ParcelFileDescriptor.open(localFile, ParcelFileDescriptor.MODE_READ_ONLY);
                return pfd;
            default:// Otherwise unrecognised Uri
                throw new FileNotFoundException("Unsupported uri: " + uri.toString());
        }
    }

    public static Uri makeLocalFileUri(File file){
        return Uri.parse("content://" +
                AUTHORITY + "/" + file.getName());
    }

}

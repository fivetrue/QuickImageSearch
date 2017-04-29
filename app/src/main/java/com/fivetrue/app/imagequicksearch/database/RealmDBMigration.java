package com.fivetrue.app.imagequicksearch.database;

import android.util.Log;

import com.fivetrue.app.imagequicksearch.LL;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

/**
 * Created by kwonojin on 2017. 3. 28..
 */

public class RealmDBMigration implements io.realm.RealmMigration {

    private static final String TAG = "RealmDBMigration";

    public static final int DB_VERSION = 2;

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        if(LL.D)
            Log.d(TAG, "migrate() called with: realm = [" + realm + "], oldVersion = [" + oldVersion + "], newVersion = [" + newVersion + "]");

        /**
         * 1.0.6 version 에서 DB 필드 변경
         */
        if(oldVersion == 1){
            RealmSchema schema = realm.getSchema();
            schema.get("AppInfo")
                    .addField("favorite", boolean.class);
            oldVersion++;
        }
        // DynamicRealm는 편집가능한 스키마를 노출합니다
//        RealmSchema schema = realm.getSchema();
//        if (oldVersion == 1) {
//            schema.rename("StoredImage", "SavedImage");
//            oldVersion ++;
//        }
//
//            // 버전 2로 마이그레이션: 기본 키를 넣고 객체를 참조합니다
//            if (oldVersion == 1) {
//                schema.get("Person")
//                        .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
//                        .addRealmObjectField("favoriteDog", schema.get("Dog"))
//                        .addRealmListField("dogs", schema.get("Dog"));
//                oldVersion++;
//            }
    }
}

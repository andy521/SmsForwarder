package com.idormy.sms.forwarder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.idormy.sms.forwarder.receiver.RebootBroadcastReceiver;
import com.idormy.sms.forwarder.utils.CacheUtil;
import com.idormy.sms.forwarder.utils.CommonUtil;
import com.idormy.sms.forwarder.utils.SettingUtil;
import com.xuexiang.xupdate.easy.EasyUpdate;
import com.xuexiang.xupdate.proxy.impl.DefaultUpdateChecker;

import java.util.List;


public class AboutActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "AboutActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);
        Log.d(TAG, "onCreate: " + RebootBroadcastReceiver.class.getName());

        XXPermissions.with(this)
                // 申请安装包权限
                .permission(Permission.REQUEST_INSTALL_PACKAGES)
                // 申请通知栏权限
                .permission(Permission.NOTIFICATION_SERVICE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            Toast.makeText(getBaseContext(), R.string.toast_granted_all, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getBaseContext(), R.string.toast_granted_part, Toast.LENGTH_SHORT).show();
                        }
                        SettingUtil.switchEnableSms(true);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(getBaseContext(), R.string.toast_denied_never, Toast.LENGTH_SHORT).show();
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(AboutActivity.this, permissions);
                        } else {
                            Toast.makeText(getBaseContext(), R.string.toast_denied, Toast.LENGTH_SHORT).show();
                        }
                        SettingUtil.switchEnableSms(false);
                    }
                });

        final TextView version_now = findViewById(R.id.version_now);
        Button check_version_now = findViewById(R.id.check_version_now);
        try {
            version_now.setText(CommonUtil.getVersionName(AboutActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        check_version_now.setOnClickListener(v -> {
            try {
                String updateUrl = "https://xupdate.bms.ink/update/checkVersion?appKey=com.idormy.sms.forwarder&versionCode=";
                updateUrl += CommonUtil.getVersionCode(AboutActivity.this);

                EasyUpdate.create(AboutActivity.this, updateUrl)
                        .updateChecker(new DefaultUpdateChecker() {
                            @Override
                            public void onBeforeCheck() {
                                super.onBeforeCheck();
                                Toast.makeText(AboutActivity.this, R.string.checking, Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void noNewVersion(Throwable throwable) {
                                super.noNewVersion(throwable);
                                // 没有最新版本的处理
                                Toast.makeText(AboutActivity.this, R.string.up_to_date, Toast.LENGTH_LONG).show();
                            }
                        })
                        .update();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        final TextView cache_size = findViewById(R.id.cache_size);
        try {
            cache_size.setText(CacheUtil.getTotalCacheSize(AboutActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button clear_all_cache = findViewById(R.id.clear_all_cache);
        clear_all_cache.setOnClickListener(v -> {
            CacheUtil.clearAllCache(AboutActivity.this);
            try {
                cache_size.setText(CacheUtil.getTotalCacheSize(AboutActivity.this));
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(AboutActivity.this, R.string.cache_purged, Toast.LENGTH_LONG).show();
        });

        Button join_qq_group1 = findViewById(R.id.join_qq_group1);
        join_qq_group1.setOnClickListener(v -> {
            String key = "Mj5m39bqy6eodOImrFLI19Tdeqvv-9zf";
            joinQQGroup(key);
        });

        Button join_qq_group2 = findViewById(R.id.join_qq_group2);
        join_qq_group2.setOnClickListener(v -> {
            String key = "jPXy4YaUzA7Uo0yPPbZXdkb66NS1smU_";
            joinQQGroup(key);
        });

    }

    //发起添加群流程
    public void joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            Toast.makeText(AboutActivity.this, R.string.unknown_qq_version, Toast.LENGTH_LONG).show();
        }
    }

}

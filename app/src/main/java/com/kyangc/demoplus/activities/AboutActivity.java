package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.dialog.AnimatedProgressDialog;
import com.kyangc.developkit.base.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.functions.Action1;
import timber.log.Timber;

public class AboutActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.ivAvatar)
    CircleImageView avatar;

    AnimatedProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //Init views
        initViews();
    }

    @Override
    public void initViews() {
        super.initViews();

        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Avatar
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new AnimatedProgressDialog();
                mDialog.setDoNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        Timber.i("ahahahahahahahaha");
                    }
                });
                mDialog.show(AboutActivity.this.getFragmentManager(), "tag");
            }
        });
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}

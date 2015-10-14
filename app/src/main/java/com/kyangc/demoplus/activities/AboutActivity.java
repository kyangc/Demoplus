package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.activities.base.BaseActivity;
import com.kyangc.demoplus.utils.S;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.ivAvatar)
    CircleImageView avatar;

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
                S.show(mContext, avatar, "BAZINGA!", "CANCEL", false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        });
    }

    public static void start(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }
}

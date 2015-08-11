package com.kyangc.demoplus.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.utils.S;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AboutActivity extends AppCompatActivity {

    public static final String TAG = "AboutActivity";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.ivAvatar)
    CircleImageView avatar;

    AboutActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        context = this;
        ButterKnife.bind(this);
        initToolbar();
        initAvatar();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAvatar() {
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                S.show(context, avatar, "BAZINGA!", "CANCEL", false, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
            }
        });
    }
}

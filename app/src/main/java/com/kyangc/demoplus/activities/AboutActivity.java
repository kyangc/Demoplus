package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.demoplus.dialog.AnimatedProgressDialog;
import com.kyangc.demoplus.dialog.DuitangShopAnimatedView;
import com.kyangc.demoplus.dialog.InteractionListenerAdapter;
import com.kyangc.developkit.base.BaseActivity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class AboutActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.ivAvatar)
    CircleImageView avatar;

    @Bind(R.id.flContainer)
    FrameLayout flContainer;

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
                AnimatedProgressDialog dialog = new AnimatedProgressDialog();
                dialog
                        .setAnimationView(new DuitangShopAnimatedView(dialog))
                        .setInteractionListener(new InteractionListenerAdapter() {
                            @Override
                            public void onStart(DialogFragment dialog) {
                                super.onStart(dialog);
                                Timber.i("Start!");
                            }

                            @Override
                            public void onCancel(DialogFragment dialog) {
                                super.onCancel(dialog);
                                dialog.dismiss();
                            }

                            @Override
                            public void onEnd(DialogFragment dialog) {
                                super.onEnd(dialog);
                                dialog.dismiss();
                                Timber.i("End!");
                            }
                        })
                        .show(getFragmentManager(), "animated_dialog");
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

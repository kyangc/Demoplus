package com.kyangc.demoplus.activities;

import com.kyangc.demoplus.R;
import com.kyangc.developkit.base.BaseActivity;
import com.kyangc.demoplus.settings.SettingManager;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import butterknife.Bind;

public class SettingsActivity extends BaseActivity
        implements CompoundButton.OnCheckedChangeListener {

    @Bind(R.id.switchButton)
    Switch aSwitch;

    @Bind(R.id.httpsSwitch)
    Switch httpSwitch;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
    }

    @Override
    public void initViews() {
        super.initViews();

        //toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Switch
        aSwitch.setChecked(SettingManager.getIsFabShown());
        aSwitch.setOnCheckedChangeListener(this);
        httpSwitch.setChecked(SettingManager.getIsHttpsFirst());
        httpSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchButton:
                SettingManager.setIsFabShown(isChecked);
                break;
            case R.id.httpsSwitch:
                SettingManager.setIsHttpFirst(isChecked);
                break;
            default:
                break;
        }
    }
}

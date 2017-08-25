package com.aurorachat;

import android.os.Bundle;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;

/**
 * Created by admin on 2017/8/23.
 */

public class TReactActivity extends ReactActivity {

    private String mainComponentName="AuroraChat";
    private ReactInstanceManager instanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String bundle = getIntent().getStringExtra("component");
        if(!"".equals(bundle)) {
            mainComponentName = bundle;
        }
        ReactRootView reactRootView=new ReactRootView(this);
        instanceManager=this.createReactActivityDelegate().getReactInstanceManager();
        reactRootView.startReactApplication(instanceManager,mainComponentName);
        setContentView(reactRootView);
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return mainComponentName;
    }



}

package com.golike.customviews;

import android.app.Activity;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;

/**
 * Created by admin on 2017/8/25.
 */

public class ReactChatViewManager extends ViewGroupManager<ChatView> {

    private ChatView mChatView;
    private static final String REACT_CHAT_INPUT = "RCTChatView";
    @Override
    public String getName() {
        return REACT_CHAT_INPUT;
    }

    @Override
    protected ChatView createViewInstance(ThemedReactContext reactContext) {
        final Activity activity=reactContext.getCurrentActivity();
        if(mChatView==null)
            mChatView=new ChatView(activity);
        return mChatView;
    }


}

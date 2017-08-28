package com.golike.customviews.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.golike.customviews.EditExtension;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.R;
import com.golike.customviews.plugin.image.PictureSelectorActivity;
import com.golike.customviews.utilities.PermissionCheckUtil;

/**
 * Created by admin on 2017/8/10.
 */

public class ImagePlugin implements IPluginModule {
    ConversationType conversationType;
    String targetId;

    public ImagePlugin() {
    }

    public Drawable obtainDrawable(Context context) {
        return ContextCompat.getDrawable(context, R.drawable.rc_ext_plugin_image_selector);
    }

    public String obtainTitle(Context context) {
        return context.getString(R.string.rc_plugin_image);
    }

    public void onClick(Activity currentActivity, EditExtension extension) {
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        if(PermissionCheckUtil.requestPermissions(currentActivity, permissions)) {
            this.conversationType = extension.getConversationType();
            this.targetId = extension.getTargetId();
            Intent intent = new Intent(currentActivity, PictureSelectorActivity.class);
            extension.startActivityForPluginResult(intent, 23, this);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}

package com.golike.customviews;

import android.content.Context;
import android.view.View;

import com.golike.customviews.model.Conversation;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.UserInfo;
import com.golike.customviews.plugin.image.AlbumBitmapCacheHelper;
import com.golike.customviews.widget.provider.IContainerItemProvider.*;
import com.golike.customviews.widget.provider.ImageMessageItemProvider;
import com.golike.customviews.widget.provider.RichContentMessageItemProvider;
import com.golike.customviews.widget.provider.TextMessageItemProvider;
import com.golike.customviews.widget.provider.VoiceMessageItemProvider;

/**
 * Created by admin on 2017/8/9.
 */

public class ChatViewManger {

    private static final String TAG ="ChatViewManger" ;
    private static Context mContext;
    public static void init(Context context) {
        mContext = context;
        ChatContext.init(context);
        registerMessageTemplate(new TextMessageItemProvider());
        registerMessageTemplate(new ImageMessageItemProvider());
        registerMessageTemplate(new VoiceMessageItemProvider(context));
        registerMessageTemplate(new RichContentMessageItemProvider());
        EditExtensionManager.init(context, "xxxxx");
        EditExtensionManager.getInstance().registerExtensionModule(new DefaultExtensionModule());
        AlbumBitmapCacheHelper.init(context);
    }

    public static void registerMessageTemplate(MessageProvider provider) {
        if(ChatContext.getInstance() != null) {
            ChatContext.getInstance().registerMessageTemplate(provider);
        }

    }

}

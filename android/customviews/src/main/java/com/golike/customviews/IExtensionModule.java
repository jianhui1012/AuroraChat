package com.golike.customviews;

import android.os.Message;

import com.golike.customviews.emoticon.IEmoticonTab;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.plugin.IPluginModule;

import java.util.List;

/**
 * Created by admin on 2017/8/8.
 */

public interface IExtensionModule {
    void onInit(String var1);

    void onConnect(String var1);

    void onAttachedToExtension(EditExtension var1);

    void onDetachedFromExtension();

    void onReceivedMessage(Message var1);

    List<IPluginModule> getPluginModules(ConversationType var1);

    List<IEmoticonTab> getEmoticonTabs();

    void onDisconnect();
}

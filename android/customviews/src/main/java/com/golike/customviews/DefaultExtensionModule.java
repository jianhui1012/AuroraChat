package com.golike.customviews;

import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import com.golike.customviews.emoticon.EmojiTab;
import com.golike.customviews.emoticon.IEmojiItemClickListener;
import com.golike.customviews.emoticon.IEmoticonTab;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.plugin.IPluginModule;
import com.golike.customviews.plugin.ImagePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by admin on 2017/8/10.
 */

public class DefaultExtensionModule implements IExtensionModule {
    private static final String TAG = DefaultExtensionModule.class.getSimpleName();
    private EditText mEditText;
    private Stack<EditText> stack;
    String[] types = null;

    public DefaultExtensionModule() {
    }

    public void onInit(String appKey) {
        this.stack = new Stack();
    }

    public void onConnect(String token) {
    }

    public void onAttachedToExtension(EditExtension extension) {
        this.mEditText = extension.getInputEditText();
        Context context = extension.getContext();
        Log.i(TAG, "attach " + this.stack.size());
        this.stack.push(this.mEditText);
        Resources resources = context.getResources();

        try {
            this.types = resources.getStringArray(resources.getIdentifier("rc_realtime_support_conversation_types", "array", context.getPackageName()));
        } catch (Resources.NotFoundException var5) {
            ;
        }

    }

    public void onDetachedFromExtension() {
        Log.i(TAG, "detach " + this.stack.size());
        if(this.stack.size() > 0) {
            this.stack.pop();
            this.mEditText = this.stack.size() > 0?this.stack.peek():null;
        }

    }

    public void onReceivedMessage(Message message) {
    }

    public List<IPluginModule> getPluginModules(ConversationType conversationType) {
        ArrayList pluginModuleList = new ArrayList();
        ImagePlugin image = new ImagePlugin();
        pluginModuleList.add(image);

        return pluginModuleList;
    }

    public List<IEmoticonTab> getEmoticonTabs() {
        EmojiTab emojiTab = new EmojiTab();
        emojiTab.setOnItemClickListener(new IEmojiItemClickListener() {
            public void onEmojiClick(String emoji) {
                int start = DefaultExtensionModule.this.mEditText.getSelectionStart();
                DefaultExtensionModule.this.mEditText.getText().insert(start, emoji);
            }

            public void onDeleteClick() {
                DefaultExtensionModule.this.mEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            }
        });
        ArrayList list = new ArrayList();
        list.add(emojiTab);
        return list;
    }

    public void onDisconnect() {
    }
}

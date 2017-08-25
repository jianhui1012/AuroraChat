package com.golike.customviews.widget.provider;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;

import com.golike.customviews.R;
import com.golike.customviews.model.MessageContent;
import com.golike.customviews.model.UIMessage;
import com.golike.customviews.model.UserInfo;

/**
 * Created by admin on 2017/8/15.
 */

public interface IContainerItemProvider<T> {
    View newView(Context var1, ViewGroup var2);

    void bindView(View var1, int var2, T var3);

    public interface ConversationProvider<T extends Parcelable> extends IContainerItemProvider<T> {
        String getTitle(String var1);

        Uri getPortraitUri(String var1);
    }

    public abstract static class MessageProvider<K extends MessageContent> implements IContainerItemProvider<UIMessage>, Cloneable {
        public MessageProvider() {
        }

        public final void bindView(View v, int position, UIMessage data) {
            this.bindView(v, position, (K) data.getContent(), data);
        }

        public abstract void bindView(View var1, int var2, K var3, UIMessage var4);

        public final Spannable getSummary(UIMessage data) {
            return this.getContentSummary((K) data.getContent());
        }

        public abstract Spannable getContentSummary(K var1);

        public abstract void onItemClick(View var1, int var2, K var3, UIMessage var4);

        public abstract void onItemLongClick(View var1, int var2, K var3, UIMessage var4);

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public String getPushContent(Context context, UIMessage message) {
            String userName = "";
//            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(message.getSenderUserId());
//            if(userInfo != null) {
//                userName = userInfo.getName();
//            }

            return context.getString(R.string.rc_user_recalled_message, new Object[]{userName});
        }
    }
}

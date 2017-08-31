package com.golike.customviews.widget.provider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.golike.customviews.R;
import com.golike.customviews.RongContext;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.ProviderTag;
import com.golike.customviews.model.RichContentMessage;
import com.golike.customviews.model.UIMessage;
import com.golike.customviews.widget.provider.IContainerItemProvider.MessageProvider;

/**
 * Created by admin on 2017/8/31.
 */

@ProviderTag(
        messageContent = RichContentMessage.class,
        showReadState = true
)
public class RichContentMessageItemProvider extends MessageProvider<RichContentMessage> {
    private static final String TAG = "RichContentMessageItemProvider";
    private Context mContext;

    public RichContentMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        this.mContext=context;
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_rich_content_message, (ViewGroup)null);
        RichContentMessageItemProvider.ViewHolder holder = new RichContentMessageItemProvider.ViewHolder();
        holder.title = (TextView)view.findViewById(R.id.rc_title);
        holder.content = (TextView)view.findViewById(R.id.rc_content);
        holder.img = (ImageView) view.findViewById(R.id.rc_img);
        holder.mLayout = (RelativeLayout)view.findViewById(R.id.rc_layout);
        view.setTag(holder);
        return view;
    }

    public void onItemClick(View view, int position, RichContentMessage content, UIMessage message) {
//        String action = "io.rong.imkit.intent.action.webview";
//        Intent intent = new Intent(action);
//        intent.addFlags(268435456);
//        intent.putExtra("url", content.getUrl());
//        intent.setPackage(view.getContext().getPackageName());
//        view.getContext().startActivity(intent);
    }

    public void onItemLongClick(final View view, int position, RichContentMessage content, final UIMessage message) {
    }

    public void bindView(View v, int position, RichContentMessage content, UIMessage message) {
        RichContentMessageItemProvider.ViewHolder holder = (RichContentMessageItemProvider.ViewHolder)v.getTag();
        holder.title.setText(content.getTitle());
        holder.content.setText(content.getContent());
        if(content.getImgUrl() != null) {
            Glide.with(mContext)
                    .load(content.getImgUrl())
                    .placeholder(R.drawable.rc_loading)
                    .into(holder.img);
        }

        if(message.getMessageDirection() == Message.MessageDirection.SEND) {
            holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_right_file);
        } else {
            holder.mLayout.setBackgroundResource(R.drawable.rc_ic_bubble_left_file);
        }

    }

    public Spannable getContentSummary(RichContentMessage data) {
        return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_content_rich_text));
    }

    private static class ViewHolder {
        ImageView img;
        TextView title;
        TextView content;
        RelativeLayout mLayout;

        private ViewHolder() {
        }
    }
}

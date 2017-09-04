package com.golike.customviews.widget.provider;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.golike.customviews.R;
import com.golike.customviews.ChatContext;
import com.golike.customviews.model.Message.SentStatus;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.UIMessage;
import com.golike.customviews.widget.provider.IContainerItemProvider.MessageProvider;
import com.golike.customviews.model.ImageMessage;
import com.golike.customviews.model.ProviderTag;

/**
 * Created by admin on 2017/8/23.
 */

@ProviderTag(
        messageContent = ImageMessage.class,
        showProgress = false,
        showReadState = true
)
public class ImageMessageItemProvider extends MessageProvider<ImageMessage> {
    private static final String TAG = "ImageMessageItemProvider";
    private Context mContext;

    public ImageMessageItemProvider() {
    }

    public View newView(Context context, ViewGroup group) {
        this.mContext=context;
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_image_message, (ViewGroup)null);
        ImageMessageItemProvider.ViewHolder holder = new ImageMessageItemProvider.ViewHolder();
        holder.message = (TextView)view.findViewById(R.id.rc_msg);
        holder.img = (ImageView)view.findViewById(R.id.rc_img);
        view.setTag(holder);
        return view;
    }

    public void onItemClick(View view, int position, ImageMessage content, UIMessage message) {
//        if(content != null) {
//            Intent intent = new Intent("io.rong.imkit.intent.action.picturepagerview");
//            intent.setPackage(view.getContext().getPackageName());
//            intent.putExtra("message", message.getMessage());
//            view.getContext().startActivity(intent);
//        }
    }

    public void onItemLongClick(final View view, int position, ImageMessage content, final UIMessage message) {

    }

    public void bindView(View v, int position, ImageMessage content, UIMessage message) {
        ImageMessageItemProvider.ViewHolder holder = (ImageMessageItemProvider.ViewHolder)v.getTag();
        if(message.getMessageDirection() == Message.MessageDirection.SEND) {
            v.setBackgroundResource(R.drawable.rc_ic_bubble_no_right);
        } else {
            v.setBackgroundResource(R.drawable.rc_ic_bubble_no_left);
        }
        Glide.with(mContext)
                .load(content.getThumUri())
                .placeholder(R.drawable.rc_loading)
                .into(holder.img);
        int progress = message.getProgress();
        SentStatus status = message.getSentStatus();
        if(status.equals(SentStatus.SENDING) && progress < 100) {
            holder.message.setText(progress + "%");
            holder.message.setVisibility(View.VISIBLE);
        } else {
            holder.message.setVisibility(View.GONE);
        }

    }

    public Spannable getContentSummary(ImageMessage data) {
        return new SpannableString(ChatContext.getInstance().getResources().getString(R.string.rc_message_content_image));
    }

    private static class ViewHolder {
        ImageView img;
        TextView message;

        private ViewHolder() {
        }
    }
}

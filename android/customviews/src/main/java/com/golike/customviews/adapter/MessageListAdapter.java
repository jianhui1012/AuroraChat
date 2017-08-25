package com.golike.customviews.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.golike.customviews.R;
import com.golike.customviews.RongContext;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.Message.MessageDirection;
import com.golike.customviews.model.Message.SentStatus;
import com.golike.customviews.model.ProviderTag;
import com.golike.customviews.model.UIMessage;
import com.golike.customviews.model.UserInfo;
import com.golike.customviews.utilities.RongUtils;
import com.golike.customviews.widget.ProviderContainerView;
import com.golike.customviews.widget.provider.IContainerItemProvider;

import java.text.SimpleDateFormat;


/**
 * Created by admin on 2017/8/14.
 */

public class MessageListAdapter extends BaseAdapter<UIMessage> {

    private LayoutInflater mInflater;
    private boolean timeGone;

    public MessageListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(this.mContext);
    }

    @Override
    protected View newView(Context context, int pos, ViewGroup parent) {
        View result = this.mInflater.inflate(R.layout.rc_item_message, (ViewGroup) null);
        MessageListAdapter.ViewHolder holder = new MessageListAdapter.ViewHolder();
        holder.leftIconView = (ImageView) this.findViewById(result, R.id.rc_left);
        holder.rightIconView = (ImageView) this.findViewById(result, R.id.rc_right);
        holder.nameView = (TextView) this.findViewById(result, R.id.rc_title);
        holder.contentView = (ProviderContainerView) this.findViewById(result, R.id.rc_content);
        holder.layout = (ViewGroup) this.findViewById(result, R.id.rc_layout);
        holder.progressBar = (ProgressBar) this.findViewById(result, R.id.rc_progress);
        holder.warning = (ImageView) this.findViewById(result, R.id.rc_warning);
        holder.readReceipt = (ImageView) this.findViewById(result, R.id.rc_read_receipt);
        holder.readReceiptRequest = (ImageView) this.findViewById(result, R.id.rc_read_receipt_request);
        holder.readReceiptStatus = (TextView) this.findViewById(result, R.id.rc_read_receipt_status);
        holder.time = (TextView) this.findViewById(result, R.id.rc_time);
        holder.sentStatus = (TextView) this.findViewById(result, R.id.rc_sent_status);
        holder.layoutItem = (RelativeLayout) this.findViewById(result, R.id.rc_layout_item_message);
        if (holder.time.getVisibility() == View.GONE) {
            this.timeGone = true;
        } else {
            this.timeGone = false;
        }

        result.setTag(holder);
        return result;
    }

    protected void bindView(View v, final int position, final UIMessage data) {
        if(data != null) {
            final MessageListAdapter.ViewHolder holder = (MessageListAdapter.ViewHolder)v.getTag();
            if(holder == null) {
                Log.e("MessageListAdapter", "view holder is null !");
            } else {
                final Object provider;
                ProviderTag tag;
                if(RongContext.getInstance() == null || data == null || data.getContent() == null) {
                        Log.e("MessageListAdapter", "Message is null !");
                        return;
                }
                provider = RongContext.getInstance().getMessageTemplate(data.getContent().getClass());
                tag = RongContext.getInstance().getMessageProviderTag(data.getContent().getClass());
                if(provider == null) {
                    Log.e("MessageListAdapter", data.getObjectName() + " message provider not found !");
                        return;
                }
                final View view = holder.contentView.inflate((IContainerItemProvider)provider);
                ((IContainerItemProvider)provider).bindView(view, position, data);
                if(view != null) {
                    view.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if(provider != null) {
                                ((IContainerItemProvider.MessageProvider)provider).onItemClick(v, position, data.getContent(), data);
                            }
                        }
                    });
                    view.setOnLongClickListener(new View.OnLongClickListener() {
                        public boolean onLongClick(View v) {
                            if(provider != null) {
                                ((IContainerItemProvider.MessageProvider)provider).onItemLongClick(v, position, data.getContent(), data);
                            }
                            return true;
                        }
                    });
                }
                if(tag == null) {
                    Log.e("MessageListAdapter", "Can not find ProviderTag for " + data.getObjectName());
                } else {
                    if (tag.hide()) {
                        holder.contentView.setVisibility(View.GONE);
                        holder.time.setVisibility(View.GONE);
                        holder.nameView.setVisibility(View.GONE);
                        holder.leftIconView.setVisibility(View.GONE);
                        holder.rightIconView.setVisibility(View.GONE);
                        holder.layoutItem.setVisibility(View.GONE);
                        holder.layoutItem.setPadding(0, 0, 0, 0);
                    } else {
                        holder.contentView.setVisibility(View.VISIBLE);
                        holder.layoutItem.setVisibility(View.VISIBLE);
                        holder.layoutItem.setPadding(RongUtils.dip2px(8.0F), RongUtils.dip2px(6.0F), RongUtils.dip2px(8.0F), RongUtils.dip2px(6.0F));
                    }

                    UserInfo var13;
                    if (data.getMessageDirection() == Message.MessageDirection.SEND) {
                        if (tag.showPortrait()) {
                            holder.rightIconView.setVisibility(View.VISIBLE);
                            holder.leftIconView.setVisibility(View.GONE);
                        } else {
                            holder.leftIconView.setVisibility(View.GONE);
                            holder.rightIconView.setVisibility(View.GONE);
                        }
                        if (!tag.centerInHorizontal()) {
                            this.setGravity(holder.layout, 5);
                            holder.contentView.containerViewRight();
                            holder.nameView.setGravity(5);
                        } else {
                            this.setGravity(holder.layout, 17);
                            holder.contentView.containerViewCenter();
                            holder.nameView.setGravity(1);
                            holder.contentView.setBackgroundColor(0);
                        }

                        boolean time = false;

                        try {
                            time = this.mContext.getResources().getBoolean(R.bool.rc_read_receipt);
                        } catch (Resources.NotFoundException var12) {
                            Log.e("MessageListAdapter", "rc_read_receipt not configure in rc_config.xml");
                            var12.printStackTrace();
                        }

                        if (data.getSentStatus() == SentStatus.SENDING) {
                            if (tag.showProgress()) {
                                holder.progressBar.setVisibility(View.VISIBLE);
                            } else {
                                holder.progressBar.setVisibility(View.GONE);
                            }

                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (data.getSentStatus() == SentStatus.FAILED) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.VISIBLE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (data.getSentStatus() == SentStatus.SENT) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        } else if (time && data.getSentStatus() == SentStatus.READ) {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            if (data.getConversationType().equals(ConversationType.PRIVATE) && tag.showReadState()) {
                                holder.readReceipt.setVisibility(View.VISIBLE);
                            } else {
                                holder.readReceipt.setVisibility(View.GONE);
                            }
                        } else {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        }
                        holder.readReceiptRequest.setVisibility(View.GONE);
                        holder.readReceiptStatus.setVisibility(View.GONE);
                        var13 = null;
                        if (data.getMessageDirection().equals(MessageDirection.SEND)) {
                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            java.util.Date date=new java.util.Date();
                            String var19=sdf.format(date);
                            holder.time.setText(var19);
                            if (data.getUserInfo() != null) {
                                var13 = data.getUserInfo();
                            } else if (data.getMessage() != null && data.getMessage().getContent() != null) {
                                var13 = data.getMessage().getContent().getUserInfo();
                            }
                            if (var13 != null) {
                                holder.nameView.setText(var13.getName());
                            } else {
                                holder.nameView.setText(data.getSenderUserId());
                            }
                        }
                    }
                    holder.leftIconView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                        }
                    });
                    Uri var15;
                    if (holder.rightIconView.getVisibility() == View.VISIBLE) {
                        if ( data.getMessageDirection().equals(MessageDirection.SEND)) {
                            var13 = data.getMessage().getContent().getUserInfo();
                            var15 = var13.getPortraitUri();
                            if (var15 != null) {
                                holder.leftIconView.setImageURI(var15);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setGravity(View view, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = gravity;
    }

    class ViewHolder {
        ImageView leftIconView;
        ImageView rightIconView;
        TextView nameView;
        ProviderContainerView contentView;
        ProgressBar progressBar;
        ImageView warning;
        ImageView readReceipt;
        ImageView readReceiptRequest;
        TextView readReceiptStatus;
        ViewGroup layout;
        TextView time;
        TextView sentStatus;
        RelativeLayout layoutItem;

        ViewHolder() {
        }
    }
}

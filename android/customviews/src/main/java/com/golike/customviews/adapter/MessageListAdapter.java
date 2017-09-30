package com.golike.customviews.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.golike.customviews.R;
import com.golike.customviews.ChatContext;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.Message.MessageDirection;
import com.golike.customviews.model.Message.SentStatus;
import com.golike.customviews.model.ProviderTag;
import com.golike.customviews.model.UIMessage;
import com.golike.customviews.model.UserInfo;
import com.golike.customviews.utilities.RongDateUtils;
import com.golike.customviews.utilities.RongUtils;
import com.golike.customviews.widget.ProviderContainerView;
import com.golike.customviews.widget.provider.IContainerItemProvider;


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
        View result = this.mInflater.inflate(R.layout.ee_item_message, null);
        MessageListAdapter.ViewHolder holder = new MessageListAdapter.ViewHolder();
        holder.leftIconView =  this.findViewById(result, R.id.ee_left);
        holder.rightIconView = this.findViewById(result, R.id.ee_right);
        holder.nameView = this.findViewById(result, R.id.ee_title);
        holder.contentView =this.findViewById(result, R.id.ee_content);
        holder.layout =this.findViewById(result, R.id.ee_layout);
        holder.progressBar =  this.findViewById(result, R.id.ee_progress);
        holder.warning = this.findViewById(result, R.id.ee_warning);
        holder.readReceipt =  this.findViewById(result, R.id.ee_read_receipt);
        holder.readReceiptRequest = this.findViewById(result, R.id.ee_read_receipt_request);
        holder.readReceiptStatus = this.findViewById(result, R.id.ee_read_receipt_status);
        holder.time = this.findViewById(result, R.id.ee_time);
        holder.sentStatus =this.findViewById(result, R.id.ee_sent_status);
        holder.layoutItem =this.findViewById(result, R.id.ee_layout_item_message);
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
                if(ChatContext.getInstance() == null || data == null || data.getContent() == null) {
                        Log.e("MessageListAdapter", "Message is null !");
                        return;
                }
                provider = ChatContext.getInstance().getMessageTemplate(data.getContent().getClass());
                tag = ChatContext.getInstance().getMessageProviderTag(data.getContent().getClass());
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
                            this.setGravity(holder.layout, View.TEXT_ALIGNMENT_VIEW_START);
                            holder.contentView.containerViewRight();
                            holder.nameView.setGravity(View.TEXT_ALIGNMENT_VIEW_START);
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
                        }else {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.warning.setVisibility(View.GONE);
                            holder.readReceipt.setVisibility(View.GONE);
                        }
                        holder.readReceiptRequest.setVisibility(View.GONE);
                        holder.readReceiptStatus.setVisibility(View.GONE);
                        if(time && (data.getConversationType().equals(ConversationType.GROUP) || data.getConversationType().equals(ConversationType.DISCUSSION))) {
                        }
                        holder.nameView.setVisibility(View.GONE);
                        holder.readReceiptRequest.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                            }
                        });
                        holder.readReceiptStatus.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                            }
                        });
                        holder.rightIconView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if(ChatContext.getInstance().getConversationBehaviorListener() != null) {
                                    UserInfo userInfo = null;
                                    if(!TextUtils.isEmpty(data.getSenderUserId())) {
                                        //userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                        userInfo = userInfo == null?new UserInfo(data.getSenderUserId(), (String)null, (Uri)null):userInfo;
                                    }
                                    ChatContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(MessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                                }
                            }
                        });
                        holder.rightIconView.setOnLongClickListener(new View.OnLongClickListener() {
                            public boolean onLongClick(View v) {
                              return  true;
                            }
                        });
                        if(!tag.showWarning()) {
                            holder.warning.setVisibility(View.GONE);
                        }
                    }
                    else {
                        if(tag.showPortrait()) {
                            holder.rightIconView.setVisibility(View.GONE);
                            holder.leftIconView.setVisibility(View.VISIBLE);
                        } else {
                            holder.leftIconView.setVisibility(View.GONE);
                            holder.rightIconView.setVisibility(View.GONE);
                        }

                        if(!tag.centerInHorizontal()) {
                            this.setGravity(holder.layout, 3);
                            holder.contentView.containerViewLeft();
                            holder.nameView.setGravity(3);
                        } else {
                            this.setGravity(holder.layout, 17);
                            holder.contentView.containerViewCenter();
                            holder.nameView.setGravity(1);
                            holder.contentView.setBackgroundColor(0);
                        }
                        holder.progressBar.setVisibility(View.GONE);
                        holder.warning.setVisibility(View.GONE);
                        holder.readReceipt.setVisibility(View.GONE);
                        holder.readReceiptRequest.setVisibility(View.GONE);
                        holder.readReceiptStatus.setVisibility(View.GONE);
                        holder.nameView.setVisibility(View.VISIBLE);
                        if(data.getConversationType() != ConversationType.PRIVATE && tag.showSummaryWithName() && data.getConversationType() != ConversationType.PUBLIC_SERVICE && data.getConversationType() != ConversationType.APP_PUBLIC_SERVICE) {
                            var13 = null;
                            if(data.getConversationType().equals(ConversationType.CUSTOMER_SERVICE) && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                                if(data.getUserInfo() != null) {
                                    var13 = data.getUserInfo();
                                } else if(data.getMessage() != null && data.getMessage().getContent() != null) {
                                    var13 = data.getMessage().getContent().getUserInfo();
                                }

                                if(var13 != null) {
                                    holder.nameView.setText(var13.getName());
                                } else {
                                    holder.nameView.setText(data.getSenderUserId());
                                }
                            } else if(data.getConversationType() == ConversationType.GROUP) {
                            } else {
                            }
                        } else {
                            holder.nameView.setVisibility(View.GONE);
                        }
                        holder.leftIconView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if(ChatContext.getInstance().getConversationBehaviorListener() != null) {
                                    UserInfo userInfo = null;
                                    if(!TextUtils.isEmpty(data.getSenderUserId())) {
                                        //userInfo = RongUserInfoManager.getInstance().getUserInfo(data.getSenderUserId());
                                        userInfo = userInfo == null?new UserInfo(data.getSenderUserId(), (String)null, (Uri)null):userInfo;
                                    }
                                    ChatContext.getInstance().getConversationBehaviorListener().onUserPortraitClick(MessageListAdapter.this.mContext, data.getConversationType(), userInfo);
                                }
                            }
                        });
                    }
                    Uri var15;
                    if(holder.rightIconView.getVisibility() == View.VISIBLE) {
                        if(data.getUserInfo() != null && data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                            var13 = data.getUserInfo();
                            var15 = var13.getPortraitUri();
                            if(var15 != null) {
                                Glide.with(mContext)
                                        .load(var15)
                                        .placeholder(R.drawable.rc_loading)
                                        .into(holder.rightIconView);
                            }
                        } else if(!TextUtils.isEmpty(data.getSenderUserId())) {
                            var13 = data.getMessage().getContent().getUserInfo();
                            if(var13 != null && var13.getPortraitUri() != null) {
                                Glide.with(mContext)
                                        .load(var13.getPortraitUri())
                                        .placeholder(R.drawable.rc_loading)
                                        .into(holder.rightIconView);
                            }
                        }
                    } else if(holder.leftIconView.getVisibility() == View.VISIBLE) {
                        var13 = null;
                        if(data.getMessageDirection().equals(MessageDirection.RECEIVE)) {
                            if(data.getUserInfo() != null) {
                                var13 = data.getUserInfo();
                            } else if(data.getMessage() != null && data.getMessage().getContent() != null) {
                                var13 = data.getMessage().getContent().getUserInfo();
                            }

                            if(var13 != null) {
                                var15 = var13.getPortraitUri();
                                if(var15 != null) {
                                    Glide.with(mContext)
                                            .load(var15)
                                            .placeholder(R.drawable.rc_loading)
                                            .into(holder.leftIconView);
                                }
                            }
                        }  else if(!TextUtils.isEmpty(data.getSenderUserId())) {
                            var13 = data.getMessage().getContent().getUserInfo();
                            if(var13 != null && var13.getPortraitUri() != null) {
                                Glide.with(mContext)
                                        .load(var13.getPortraitUri())
                                        .placeholder(R.drawable.rc_loading)
                                        .into(holder.leftIconView);
                            }
                        }
                    }

                    if(tag.hide()) {
                        holder.time.setVisibility(View.GONE);
                    } else {
                        if(!this.timeGone) {
                            String var19 = RongDateUtils.getConversationFormatDate(data.getSentTime(), view.getContext());
                            holder.time.setText(var19);
                            if(position == 0) {
                                holder.time.setVisibility(View.VISIBLE);
                            } else {
                                UIMessage var18 = this.getItem(position - 1);
                                if(RongDateUtils.isShowChatTime(data.getSentTime(), var18.getSentTime(), 180)) {
                                    holder.time.setVisibility(View.VISIBLE);
                                } else {
                                    holder.time.setVisibility(View.GONE);
                                }
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

package com.golike.customviews;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.golike.customviews.AutoRefreshListView.Mode;
import com.golike.customviews.adapter.MessageListAdapter;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.UIMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by admin on 2017/8/8.
 */

public class ChatView extends FrameLayout implements AbsListView.OnScrollListener{

    private Context mContext;
    private View mChatUIView;
    private AutoRefreshListView mChatList;
    private View mMsgListView;
    private MessageListAdapter mListAdapter;
    private EditExtension mEditExtension;

    public ChatView(Context context) {
        super(context);
        this.mContext = context;
        initEvent();
        initViews();
    }


    public ChatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initEvent();
        initViews();
    }

    public ChatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initEvent();
        initViews();
    }

    private void initEvent() {
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void initViews() {
        this.mChatUIView = LayoutInflater.from(this.getContext()).inflate(R.layout.rc_fr_conversation, (ViewGroup) null);
        this.mEditExtension = this.findViewById(mChatUIView,R.id.rc_extension);
        this.mMsgListView = this.findViewById(mChatUIView, R.id.rc_layout_msg_list);
        this.mChatList = this.findViewById(mMsgListView, R.id.rc_list);
        this.mChatList.requestDisallowInterceptTouchEvent(true);
        this.mChatList.setMode(Mode.START);
        this.mChatList.setTranscriptMode(2);
        this.mListAdapter = this.onResolveAdapter(mContext);
        this.mChatList.setAdapter(this.mListAdapter);
        this.mChatList.setOnRefreshListener(new AutoRefreshListView.OnRefreshListener() {
            public void onRefreshFromStart() {
            }
            public void onRefreshFromEnd() {
            }
        });
        mChatList.addOnScrollListener(this);
        this.addView(mChatUIView);
    }


    public EditExtension getEditExtension(){
          if(mChatUIView==null)
          {
              this.mChatUIView = LayoutInflater.from(this.getContext()).inflate(R.layout.rc_fr_conversation, (ViewGroup) null);
              this.mEditExtension = this.findViewById(mChatUIView,R.id.rc_extension);
          }
        return mEditExtension;
    }

    //订阅方法，当接收到事件的时候，会调用该方法
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Message msg){
        UIMessage uiMsg=UIMessage.obtain(msg);
        uiMsg.setSentStatus(Message.SentStatus.SENT);
        this.mListAdapter.add(uiMsg);
        this.mListAdapter.notifyDataSetChanged();
        //this.mChatList.smoothScrollToPosition(this.mChatList.getCount());
    }

    protected <T extends View> T findViewById(View view, int id) {
        return (T) view.findViewById(id);
    }

    public MessageListAdapter onResolveAdapter(Context context) {
        return new MessageListAdapter(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

}

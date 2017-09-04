package com.golike.customviews.widget.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.golike.customviews.R;
import com.golike.customviews.RongContext;
import com.golike.customviews.manager.AudioPlayManager;
import com.golike.customviews.manager.AudioRecordManager;
import com.golike.customviews.manager.IAudioPlayListener;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.ProviderTag;
import com.golike.customviews.model.Message.MessageDirection;
import com.golike.customviews.model.UIMessage;
import com.golike.customviews.model.VoiceMessage;
import com.golike.customviews.widget.provider.IContainerItemProvider.MessageProvider;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by admin on 2017/8/24.
 */

@ProviderTag(
        messageContent = VoiceMessage.class,
        showReadState = true
)
public class VoiceMessageItemProvider extends MessageProvider<VoiceMessage> {
    private static final String TAG = "VoiceMessageItemProvider";

    public VoiceMessageItemProvider(Context context) {
    }

    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_voice_message, (ViewGroup)null);
        VoiceMessageItemProvider.ViewHolder holder = new VoiceMessageItemProvider.ViewHolder();
        holder.left = (TextView)view.findViewById(R.id.rc_left);
        holder.right = (TextView)view.findViewById(R.id.rc_right);
        holder.img = (ImageView)view.findViewById(R.id.rc_img);
        holder.unread = (ImageView)view.findViewById(R.id.rc_voice_unread);
        view.setTag(holder);
        return view;
    }

    public void bindView(View v, int position, VoiceMessage content, UIMessage message) {
        VoiceMessageItemProvider.ViewHolder holder = (VoiceMessageItemProvider.ViewHolder)v.getTag();
        Uri playingUri;
        boolean listened;
        if(message.continuePlayAudio) {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if(playingUri == null || !playingUri.equals(content.getUri())) {
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().startPlay(v.getContext(), content.getUri(), new VoiceMessageItemProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            }
        } else {
            playingUri = AudioPlayManager.getInstance().getPlayingUri();
            if(playingUri != null && playingUri.equals(content.getUri())) {
                this.setLayout(v.getContext(), holder, message, true);
                listened = message.getMessage().getReceivedStatus().isListened();
                AudioPlayManager.getInstance().setPlayListener(new VoiceMessageItemProvider.VoiceMessagePlayListener(v.getContext(), message, holder, listened));
            } else {
                this.setLayout(v.getContext(), holder, message, false);
            }
        }

    }

    public void onItemClick(View view, int position, VoiceMessage content, UIMessage message) {
        //Log.d("VoiceMessageItemProvider", "Item index:" + position);
        VoiceMessageItemProvider.ViewHolder holder = (VoiceMessageItemProvider.ViewHolder)view.getTag();
        holder.unread.setVisibility(View.GONE);
        Uri playingUri = AudioPlayManager.getInstance().getPlayingUri();
        if(playingUri != null && playingUri.equals(content.getUri())) {
            AudioPlayManager.getInstance().stopPlay();
        } else {
            boolean listened = true;//message.getMessage().getReceivedStatus().isListened();
            AudioPlayManager.getInstance().startPlay(view.getContext(), content.getUri(), new VoiceMessageItemProvider.VoiceMessagePlayListener(view.getContext(), message, holder, listened));
        }

    }

    public void onItemLongClick(final View view, int position, VoiceMessage content, final UIMessage message) {

    }

    private void setLayout(Context context, VoiceMessageItemProvider.ViewHolder holder, UIMessage message, boolean playing) {
        VoiceMessage content = (VoiceMessage)message.getContent();
        byte minLength = 57;
        int duration = AudioRecordManager.getInstance().getMaxVoiceDuration();
        holder.img.getLayoutParams().width = (int)((float)(content.getDuration() * (180 / duration) + minLength) * context.getResources().getDisplayMetrics().density);
        AnimationDrawable animationDrawable;
        if(message.getMessageDirection() == MessageDirection.SEND) {
            holder.left.setText(String.format("%s\"", new Object[]{Integer.valueOf(content.getDuration())}));
            holder.left.setVisibility(View.VISIBLE);
            holder.right.setVisibility(View.GONE);
            holder.unread.setVisibility(View.GONE);
            holder.img.setScaleType(ImageView.ScaleType.FIT_END);
            holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_right);
            animationDrawable = (AnimationDrawable)context.getResources().getDrawable(R.drawable.rc_an_voice_sent);
            if(playing) {
                holder.img.setImageDrawable(animationDrawable);
                if(animationDrawable != null) {
                    animationDrawable.start();
                }
            } else {
                holder.img.setImageDrawable(holder.img.getResources().getDrawable(R.drawable.rc_ic_voice_sent));
                if(animationDrawable != null) {
                    animationDrawable.stop();
                }
            }
        } else {
            holder.right.setText(String.format("%s\"", new Object[]{Integer.valueOf(content.getDuration())}));
            holder.right.setVisibility(View.VISIBLE);
            holder.left.setVisibility(View.GONE);
            if(!message.getReceivedStatus().isListened()) {
                holder.unread.setVisibility(View.VISIBLE);
            } else {
                holder.unread.setVisibility(View.GONE);
            }

            holder.img.setBackgroundResource(R.drawable.rc_ic_bubble_left);
            animationDrawable = (AnimationDrawable)context.getResources().getDrawable(R.drawable.rc_an_voice_receive);
            if(playing) {
                holder.img.setImageDrawable(animationDrawable);
                if(animationDrawable != null) {
                    animationDrawable.start();
                }
            } else {
                holder.img.setImageDrawable(holder.img.getResources().getDrawable(R.drawable.rc_ic_voice_receive));
                if(animationDrawable != null) {
                    animationDrawable.stop();
                }
            }

            holder.img.setScaleType(ImageView.ScaleType.FIT_START);
        }

    }

    public Spannable getContentSummary(VoiceMessage data) {
        return new SpannableString(RongContext.getInstance().getString(R.string.rc_message_content_voice));
    }

    @TargetApi(8)
    private boolean muteAudioFocus(Context context, boolean bMute) {
        if(context == null) {
            //Log.d("VoiceMessageItemProvider", "muteAudioFocus context is null.");
            return false;
        } else if(Build.VERSION.SDK_INT < 8) {
            //Log.d("VoiceMessageItemProvider", "muteAudioFocus Android 2.1 and below can not stop music");
            return false;
        } else {
            boolean bool = false;
            AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            int result;
            if(bMute) {
                result = am.requestAudioFocus(null, 3, 2);
                bool = result == 1;
            } else {
                result = am.abandonAudioFocus(null);
                bool = result == 1;
            }

           //Log.d("VoiceMessageItemProvider", "muteAudioFocus pauseMusic bMute=" + bMute + " result=" + bool);
            return bool;
        }
    }

    private class VoiceMessagePlayListener implements IAudioPlayListener {
        private Context context;
        private UIMessage message;
        private VoiceMessageItemProvider.ViewHolder holder;
        private boolean listened;

        public VoiceMessagePlayListener(Context context, UIMessage message, VoiceMessageItemProvider.ViewHolder holder, boolean listened) {
            this.context = context;
            this.message = message;
            this.holder = holder;
            this.listened = listened;
        }

        public void onStart(Uri uri) {
            this.message.continuePlayAudio = false;
            this.message.setListening(true);
            this.message.setReceivedStatus(new Message.ReceivedStatus(1));
            this.message.getReceivedStatus().setListened();
            //RongIMClient.getInstance().setMessageReceivedStatus(this.message.getMessageId(), this.message.getReceivedStatus(), (ResultCallback)null);
            VoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, true);
            //EventBus.getDefault().post(this.message.getMessage());
        }

        public void onStop(Uri uri) {
            this.message.setListening(false);
            VoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, false);
        }

        public void onComplete(Uri uri) {
            this.message.setListening(false);
            VoiceMessageItemProvider.this.setLayout(this.context, this.holder, this.message, false);
        }
    }

    private static class ViewHolder {
        ImageView img;
        TextView left;
        TextView right;
        ImageView unread;

        private ViewHolder() {
        }
    }
}

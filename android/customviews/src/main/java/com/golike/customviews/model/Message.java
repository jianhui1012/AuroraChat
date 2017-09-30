package com.golike.customviews.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.utilities.ParcelUtils;

/**
 * Created by admin on 2017/8/14.
 */

public class Message implements Parcelable {
    private ConversationType conversationType;
    private String targetId;
    private int messageId;
    private Message.MessageDirection messageDirection;
    private String senderUserId;
    private Message.ReceivedStatus receivedStatus;
    private Message.SentStatus sentStatus;
    private long receivedTime;
    private long sentTime;
    private String objectName;
    private String extra;
    private String UId;
    private MessageContent content;
    public static final Creator<Message> CREATOR = new Creator() {
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public String getUId() {
        return this.UId;
    }

    public void setUId(String UId) {
        this.UId = UId;
    }

    public Message() {
    }



    public static Message obtain(String targetId, ConversationType type,MessageContent content) {
        Message obj = new Message();
        obj.setTargetId(targetId);
        obj.setConversationType(type);
        obj.setContent(content);
        return obj;
    }

    public ConversationType getConversationType() {
        return this.conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public int getMessageId() {
        return this.messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Message.MessageDirection getMessageDirection() {
        return this.messageDirection;
    }

    public void setMessageDirection(Message.MessageDirection messageDirection) {
        this.messageDirection = messageDirection;
    }

    public Message.ReceivedStatus getReceivedStatus() {
        return this.receivedStatus;
    }

    public void setReceivedStatus(Message.ReceivedStatus receivedStatus) {
        this.receivedStatus = receivedStatus;
    }

    public Message.SentStatus getSentStatus() {
        return this.sentStatus;
    }

    public void setSentStatus(Message.SentStatus sentStatus) {
        this.sentStatus = sentStatus;
    }

    public long getReceivedTime() {
        return this.receivedTime;
    }

    public void setReceivedTime(long receivedTime) {
        this.receivedTime = receivedTime;
    }

    public long getSentTime() {
        return this.sentTime;
    }

    public void setSentTime(long sentTime) {
        this.sentTime = sentTime;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getSenderUserId() {
        return this.senderUserId;
    }

    public void setSenderUserId(String senderUserId) {
        this.senderUserId = senderUserId;
    }


    public int describeContents() {
        return 0;
    }

    public Message(Parcel in) {
        String className = ParcelUtils.readFromParcel(in);
        Class loader = null;
        if(className != null) {
            try {
                loader = Class.forName(className);
            } catch (ClassNotFoundException var5) {
                var5.printStackTrace();
            }
        }

        this.setTargetId(ParcelUtils.readFromParcel(in));
        this.setMessageId(ParcelUtils.readIntFromParcel(in).intValue());
        this.setSenderUserId(ParcelUtils.readFromParcel(in));
        this.setReceivedTime(ParcelUtils.readLongFromParcel(in).longValue());
        this.setSentTime(ParcelUtils.readLongFromParcel(in).longValue());
        this.setObjectName(ParcelUtils.readFromParcel(in));
        this.setContent((MessageContent)ParcelUtils.readFromParcel(in, loader));
        this.setExtra(ParcelUtils.readFromParcel(in));
        this.setUId(ParcelUtils.readFromParcel(in));
        this.setConversationType(ConversationType.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
        this.setMessageDirection(Message.MessageDirection.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
        this.setReceivedStatus(new Message.ReceivedStatus(ParcelUtils.readIntFromParcel(in).intValue()));
        this.setSentStatus(Message.SentStatus.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
    }

    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.getContent() != null?this.getContent().getClass().getName():null);
        ParcelUtils.writeToParcel(dest, this.getTargetId());
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getMessageId()));
        ParcelUtils.writeToParcel(dest, this.getSenderUserId());
        ParcelUtils.writeToParcel(dest, Long.valueOf(this.getReceivedTime()));
        ParcelUtils.writeToParcel(dest, Long.valueOf(this.getSentTime()));
        ParcelUtils.writeToParcel(dest, this.getObjectName());
        ParcelUtils.writeToParcel(dest, this.getContent());
        ParcelUtils.writeToParcel(dest, this.getExtra());
        ParcelUtils.writeToParcel(dest, this.getUId());
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getConversationType().getValue()));
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getMessageDirection() == null?0:this.getMessageDirection().getValue()));
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getReceivedStatus() == null?0:this.getReceivedStatus().getFlag()));
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getSentStatus() == null?0:this.getSentStatus().getValue()));
    }

    public boolean equals(Object o) {
        return o == null?false:(o instanceof Message?this.messageId == ((Message)o).getMessageId():super.equals(o));
    }

    public MessageContent getContent() {
        return content;
    }

    public void setContent(MessageContent content) {
        this.content = content;
    }

    public static class ReceivedStatus {
        private static final int READ = 1;
        private static final int LISTENED = 2;
        private static final int DOWNLOADED = 4;
        private static final int RETRIEVED = 8;
        private static final int MULTIPLERECEIVE = 16;
        private int flag = 0;
        private boolean isRead = false;
        private boolean isListened = false;
        private boolean isDownload = false;
        private boolean isRetrieved = false;
        private boolean isMultipleReceive = false;

        public ReceivedStatus(int flag) {
            this.flag = flag;
            this.isRead = (flag & 1) == 1;
            this.isListened = (flag & 2) == 2;
            this.isDownload = (flag & 4) == 4;
            this.isRetrieved = (flag & 8) == 8;
            this.isMultipleReceive = (flag & 16) == 16;
        }

        public int getFlag() {
            return this.flag;
        }

        public boolean isRead() {
            return this.isRead;
        }

        public void setRead() {
            this.flag |= 1;
            this.isRead = true;
        }

        public boolean isListened() {
            return this.isListened;
        }

        public void setListened() {
            this.flag |= 2;
            this.isListened = true;
        }

        public boolean isDownload() {
            return this.isDownload;
        }

        public void setDownload() {
            this.flag |= 4;
            this.isDownload = true;
        }

        public boolean isRetrieved() {
            return this.isRetrieved;
        }

        public void setRetrieved() {
            this.flag |= 8;
            this.isRetrieved = true;
        }

        public boolean isMultipleReceive() {
            return this.isMultipleReceive;
        }

        public void setMultipleReceive() {
            this.flag |= 16;
            this.isMultipleReceive = true;
        }
    }

    public static enum SentStatus {
        SENDING(10),
        FAILED(20),
        SENT(30),
        RECEIVED(40),
        READ(50),
        DESTROYED(60),
        CANCELED(70);

        private int value = 1;

        private SentStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Message.SentStatus setValue(int code) {
            Message.SentStatus[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Message.SentStatus c = arr$[i$];
                if(code == c.getValue()) {
                    return c;
                }
            }

            return SENDING;
        }
    }

    public static enum MessageDirection {
        SEND(1),
        RECEIVE(2);

        private int value = 1;

        private MessageDirection(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Message.MessageDirection setValue(int code) {
            Message.MessageDirection[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                Message.MessageDirection c = arr$[i$];
                if(code == c.getValue()) {
                    return c;
                }
            }

            return SEND;
        }
    }
}

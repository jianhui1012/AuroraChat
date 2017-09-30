package com.golike.customviews.model;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.golike.customviews.utilities.ParcelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by admin on 2017/9/19.
 */

//@MessageTag(
//        value = "RC:ReadNtf",
//        flag = 0
//)
public class ReadReceiptMessage extends MessageContent {
    private static final String TAG = "ReadReceiptMessage";
    private long lastMessageSendTime;
    private String messageUId;
    private ReadReceiptMessage.ReadReceiptType type;
    public static final Creator<ReadReceiptMessage> CREATOR = new Creator() {
        public ReadReceiptMessage createFromParcel(Parcel source) {
            return new ReadReceiptMessage(source);
        }

        public ReadReceiptMessage[] newArray(int size) {
            return new ReadReceiptMessage[size];
        }
    };

    public long getLastMessageSendTime() {
        return this.lastMessageSendTime;
    }

    public void setLastMessageSendTime(long lastMessageSendTime) {
        this.lastMessageSendTime = lastMessageSendTime;
    }

    public String getMessageUId() {
        return this.messageUId;
    }

    public void setMessageUId(String messageUId) {
        this.messageUId = messageUId;
    }

    public ReadReceiptMessage.ReadReceiptType getType() {
        return this.type;
    }

    public void setType(ReadReceiptMessage.ReadReceiptType type) {
        this.type = type;
    }

    public ReadReceiptMessage(long sendTime) {
        this.setLastMessageSendTime(sendTime);
        this.setType(ReadReceiptMessage.ReadReceiptType.SEND_TIME);
    }

    public ReadReceiptMessage(String uId) {
        this.setMessageUId(uId);
        this.setType(ReadReceiptMessage.ReadReceiptType.UID);
    }

    public ReadReceiptMessage(long sendTime, String uId, ReadReceiptMessage.ReadReceiptType type) {
        this.setLastMessageSendTime(sendTime);
        this.setMessageUId(uId);
        this.setType(type);
    }

    public ReadReceiptMessage(Parcel in) {
        this.setLastMessageSendTime(ParcelUtils.readLongFromParcel(in).longValue());
        this.setMessageUId(ParcelUtils.readFromParcel(in));
        this.setType(ReadReceiptMessage.ReadReceiptType.setValue(ParcelUtils.readIntFromParcel(in).intValue()));
    }

    public ReadReceiptMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException var5) {
            Log.e("ReadReceiptMessage", var5.getMessage());
        }

        try {
            JSONObject e = new JSONObject(jsonStr);
            if(e.has("lastMessageSendTime")) {
                this.setLastMessageSendTime(e.getLong("lastMessageSendTime"));
            }

            if(e.has("messageUId")) {
                this.setMessageUId(e.getString("messageUId"));
            }

            if(e.has("type")) {
                this.setType(ReadReceiptMessage.ReadReceiptType.setValue(e.getInt("type")));
            }
        } catch (JSONException var4) {
            Log.e("ReadReceiptMessage", var4.getMessage());
        }

    }

    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("lastMessageSendTime", this.getLastMessageSendTime());
            if(!TextUtils.isEmpty(this.getMessageUId())) {
                jsonObj.put("messageUId", this.getMessageUId());
            }

            jsonObj.put("type", this.getType().getValue());
        } catch (JSONException var4) {
            Log.e("ReadReceiptMessage", "JSONException " + var4.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private ReadReceiptMessage() {
    }

    public static ReadReceiptMessage obtain(long sendTime) {
        ReadReceiptMessage obj = new ReadReceiptMessage();
        obj.setLastMessageSendTime(sendTime);
        obj.setType(ReadReceiptMessage.ReadReceiptType.SEND_TIME);
        return obj;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, Long.valueOf(this.getLastMessageSendTime()));
        ParcelUtils.writeToParcel(dest, this.getMessageUId());
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getType().getValue()));
    }

    public static enum ReadReceiptType {
        SEND_TIME(1),
        UID(2);

        private int value = 0;

        private ReadReceiptType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static ReadReceiptMessage.ReadReceiptType setValue(int code) {
            ReadReceiptMessage.ReadReceiptType[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                ReadReceiptMessage.ReadReceiptType c = arr$[i$];
                if(code == c.getValue()) {
                    return c;
                }
            }

            return SEND_TIME;
        }
    }
}


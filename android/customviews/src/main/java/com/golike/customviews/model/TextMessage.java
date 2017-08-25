package com.golike.customviews.model;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.golike.customviews.utilities.ParcelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by admin on 2017/8/14.
 */

public class TextMessage extends MessageContent {
    private static final String TAG = "TextMessage";
    private String content;
    protected String extra;
    public static final Creator<TextMessage> CREATOR = new Creator() {
        public TextMessage createFromParcel(Parcel source) {
            return new TextMessage(source);
        }

        public TextMessage[] newArray(int size) {
            return new TextMessage[size];
        }
    };

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", this.getEmotion(this.getContent()));
            if(!TextUtils.isEmpty(this.getExtra())) {
                jsonObj.put("extra", this.getExtra());
            }

            if(this.getJSONUserInfo() != null) {
                jsonObj.putOpt("user", this.getJSONUserInfo());
            }

            if(this.getJsonMentionInfo() != null) {
                jsonObj.putOpt("mentionedInfo", this.getJsonMentionInfo());
            }
        } catch (JSONException var4) {
            Log.e("TextMessage", "JSONException " + var4.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    private String getEmotion(String content) {
        Pattern pattern = Pattern.compile("\\[/u([0-9A-Fa-f]+)\\]");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            int inthex = Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(sb, String.valueOf(Character.toChars(inthex)));
        }

        matcher.appendTail(sb);
        Log.d("TextMessage", "getEmotion--" + sb.toString());
        return sb.toString();
    }

    protected TextMessage() {
    }

    public static TextMessage obtain(String text) {
        TextMessage model = new TextMessage();
        model.setContent(text);
        return model;
    }

    public TextMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
        }

        try {
            JSONObject e = new JSONObject(jsonStr);
            if(e.has("content")) {
                this.setContent(e.optString("content"));
            }

            if(e.has("extra")) {
                this.setExtra(e.optString("extra"));
            }

            if(e.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
            }

            if(e.has("mentionedInfo")) {
                this.setMentionedInfo(this.parseJsonToMentionInfo(e.getJSONObject("mentionedInfo")));
            }
        } catch (JSONException var4) {
            Log.e("TextMessage", "JSONException " + var4.getMessage());
        }

    }

    public void setContent(String content) {
        this.content = content;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.getExtra());
        ParcelUtils.writeToParcel(dest, this.content);
        ParcelUtils.writeToParcel(dest, this.getUserInfo());
        ParcelUtils.writeToParcel(dest, this.getMentionedInfo());
    }

    public TextMessage(Parcel in) {
        this.setExtra(ParcelUtils.readFromParcel(in));
        this.setContent(ParcelUtils.readFromParcel(in));
        this.setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
        this.setMentionedInfo((MentionedInfo)ParcelUtils.readFromParcel(in, MentionedInfo.class));
    }

    public TextMessage(String content) {
        this.setContent(content);
    }

    public String getContent() {
        return this.content;
    }

    public List<String> getSearchableWord() {
        ArrayList words = new ArrayList();
        words.add(this.content);
        return words;
    }
}

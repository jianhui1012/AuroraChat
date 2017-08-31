package com.golike.customviews.model;

import android.os.Parcel;
import android.os.Parcelable;
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
 * Created by admin on 2017/8/31.
 */

//@MessageTag(
//        value = "RC:ImgTextMsg",
//        flag = 3
//)
public class RichContentMessage extends MessageContent implements Parcelable {
    private String title;
    private String content;
    private String imgUrl;
    private String url = "";
    private String extra = "";
    public static final Creator<RichContentMessage> CREATOR = new Creator() {
        public RichContentMessage createFromParcel(Parcel source) {
            return new RichContentMessage(source);
        }

        public RichContentMessage[] newArray(int size) {
            return new RichContentMessage[size];
        }
    };

    public RichContentMessage(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imgUrl = imageUrl;
    }

    public RichContentMessage(String title, String content, String imageUrl, String url) {
        this.title = title;
        this.content = content;
        this.imgUrl = imageUrl;
        this.url = url;
    }

    public static RichContentMessage obtain(String title, String content, String imageUrl) {
        return new RichContentMessage(title, content, imageUrl);
    }

    public static RichContentMessage obtain(String title, String content, String imageUrl, String url) {
        return new RichContentMessage(title, content, imageUrl, url);
    }

    protected RichContentMessage(Parcel in) {
        this.title = ParcelUtils.readFromParcel(in);
        this.content = ParcelUtils.readFromParcel(in);
        this.imgUrl = ParcelUtils.readFromParcel(in);
        this.url = ParcelUtils.readFromParcel(in);
        this.extra = ParcelUtils.readFromParcel(in);
        this.setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
    }

    public RichContentMessage() {
    }

    public RichContentMessage(byte[] data) {
        String jsonStr = new String(data);

        try {
            JSONObject e = new JSONObject(jsonStr);
            this.title = e.optString("title");
            this.content = e.optString("content");
            this.imgUrl = e.optString("imageUri");
            this.url = e.optString("url");
            this.extra = e.optString("extra");
            if(e.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
            }
        } catch (JSONException var4) {
            Log.e("JSONException", var4.getMessage());
        }

    }

    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("title", this.getExpression(this.getTitle()));
            jsonObj.put("content", this.getExpression(this.getContent()));
            jsonObj.put("imageUri", this.getImgUrl());
            jsonObj.put("url", this.getUrl());
            jsonObj.put("extra", this.getExtra());
            if(this.getJSONUserInfo() != null) {
                jsonObj.putOpt("user", this.getJSONUserInfo());
            }
        } catch (JSONException var4) {
            Log.e("JSONException", var4.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return new byte[0];
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.title);
        ParcelUtils.writeToParcel(dest, this.content);
        ParcelUtils.writeToParcel(dest, this.imgUrl);
        ParcelUtils.writeToParcel(dest, this.url);
        ParcelUtils.writeToParcel(dest, this.extra);
        ParcelUtils.writeToParcel(dest, this.getUserInfo());
    }

    private String getExpression(String content) {
        Pattern pattern = Pattern.compile("\\[/u([0-9A-Fa-f]+)\\]");
        Matcher matcher = pattern.matcher(content);
        StringBuffer sb = new StringBuffer();

        while(matcher.find()) {
            matcher.appendReplacement(sb, this.toExpressionChar(matcher.group(1)));
        }

        matcher.appendTail(sb);
        Log.d("getExpression--", sb.toString());
        return sb.toString();
    }

    private String toExpressionChar(String expChar) {
        int inthex = Integer.parseInt(expChar, 16);
        return String.valueOf(Character.toChars(inthex));
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return this.imgUrl;
    }

    public void setImgUrl(String url) {
        this.imgUrl = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public List<String> getSearchableWord() {
        ArrayList words = new ArrayList();
        if(this.title != null) {
            words.add(this.title);
        }

        if(this.content != null) {
            words.add(this.content);
        }

        return words;
    }
}

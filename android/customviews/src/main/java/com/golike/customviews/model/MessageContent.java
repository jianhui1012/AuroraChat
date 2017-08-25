package com.golike.customviews.model;

import android.net.Uri;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.golike.customviews.model.MentionedInfo.MentionedType;

/**
 * Created by admin on 2017/8/15.
 */

public abstract class MessageContent implements Parcelable {
    private static final String TAG = "MessageContent";
    private UserInfo userInfo;
    private MentionedInfo mentionedInfo;

    protected MessageContent() {
    }

    public MessageContent(byte[] data) {
    }

    public UserInfo getUserInfo() {
        return this.userInfo;
    }

    public void setUserInfo(UserInfo info) {
        this.userInfo = info;
    }

    public MentionedInfo getMentionedInfo() {
        return this.mentionedInfo;
    }

    public void setMentionedInfo(MentionedInfo info) {
        this.mentionedInfo = info;
    }

    public JSONObject getJSONUserInfo() {
        if(this.getUserInfo() != null && this.getUserInfo().getUserId() != null) {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("id", this.getUserInfo().getUserId());
                if(!TextUtils.isEmpty(this.getUserInfo().getName())) {
                    jsonObject.put("name", this.getUserInfo().getName());
                }

                if(this.getUserInfo().getPortraitUri() != null) {
                    jsonObject.put("portrait", this.getUserInfo().getPortraitUri());
                }
            } catch (JSONException var3) {
                Log.e("MessageContent", "JSONException " + var3.getMessage());
            }

            return jsonObject;
        } else {
            return null;
        }
    }

    public UserInfo parseJsonToUserInfo(JSONObject jsonObj) {
        UserInfo info = null;
        String id = jsonObj.optString("id");
        String name = jsonObj.optString("name");
        String icon = jsonObj.optString("portrait");
        if(TextUtils.isEmpty(icon)) {
            icon = jsonObj.optString("icon");
        }

        if(!TextUtils.isEmpty(id) && !TextUtils.isEmpty(name)) {
            Uri portrait = icon != null?Uri.parse(icon):null;
            info = new UserInfo(id, name, portrait);
        }

        return info;
    }

    public JSONObject getJsonMentionInfo() {
        if(this.getMentionedInfo() == null) {
            return null;
        } else {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("type", this.getMentionedInfo().getType().getValue());
                if(this.getMentionedInfo().getMentionedUserIdList() == null) {
                    jsonObject.put("userIdList", (Object)null);
                } else {
                    JSONArray e = new JSONArray();
                    Iterator i$ = this.getMentionedInfo().getMentionedUserIdList().iterator();

                    while(i$.hasNext()) {
                        String userId = (String)i$.next();
                        e.put(userId);
                    }

                    jsonObject.put("userIdList", e);
                }

                jsonObject.put("mentionedContent", this.getMentionedInfo().getMentionedContent());
            } catch (JSONException var5) {
                Log.e("MessageContent", "JSONException " + var5.getMessage());
            }

            return jsonObject;
        }
    }

    public MentionedInfo parseJsonToMentionInfo(JSONObject jsonObject) {
        MentionedType type = MentionedType.valueOf(jsonObject.optInt("type"));
        JSONArray userList = jsonObject.optJSONArray("userIdList");
        String mentionContent = jsonObject.optString("mentionedContent");
        MentionedInfo mentionedInfo;
        if(type.equals(MentionedType.ALL)) {
            mentionedInfo = new MentionedInfo(type, (List)null, mentionContent);
        } else {
            ArrayList list = new ArrayList();

            try {
                for(int e = 0; e < userList.length(); ++e) {
                    list.add((String)userList.get(e));
                }
            } catch (JSONException var8) {
                var8.printStackTrace();
            }

            mentionedInfo = new MentionedInfo(type, list, mentionContent);
        }

        return mentionedInfo;
    }

    public List<String> getSearchableWord() {
        return null;
    }

    public abstract byte[] encode();
}

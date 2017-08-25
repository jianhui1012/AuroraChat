package com.golike.customviews.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.golike.customviews.utilities.ParcelUtils;

import java.util.List;

/**
 * Created by admin on 2017/8/15.
 */
public class MentionedInfo implements Parcelable {
    private MentionedInfo.MentionedType type;
    private List<String> userIdList;
    private String mentionedContent;
    public static final Creator<MentionedInfo> CREATOR = new Creator() {
        public MentionedInfo createFromParcel(Parcel source) {
            return new MentionedInfo(source);
        }

        public MentionedInfo[] newArray(int size) {
            return new MentionedInfo[size];
        }
    };

    public MentionedInfo() {
    }

    public MentionedInfo(Parcel in) {
        this.setType(MentionedInfo.MentionedType.valueOf(ParcelUtils.readIntFromParcel(in).intValue()));
        this.setMentionedUserIdList(ParcelUtils.readListFromParcel(in, String.class));
        this.setMentionedContent(ParcelUtils.readFromParcel(in));
    }

    public MentionedInfo(MentionedInfo.MentionedType type, List<String> userIdList, String mentionedContent) {
        if(type != null && type.equals(MentionedInfo.MentionedType.ALL)) {
            this.userIdList = null;
        } else if(type != null && type.equals(MentionedInfo.MentionedType.PART)) {
            if(userIdList == null || userIdList.size() == 0) {
                throw new IllegalArgumentException("When mentioned parts of the group memebers, userIdList can\'t be null!");
            }

            this.userIdList = userIdList;
        }

        this.type = type;
        this.mentionedContent = mentionedContent;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.getType().getValue()));
        ParcelUtils.writeToParcel(dest, this.getMentionedUserIdList());
        ParcelUtils.writeToParcel(dest, this.getMentionedContent());
    }

    public MentionedInfo.MentionedType getType() {
        return this.type;
    }

    public List<String> getMentionedUserIdList() {
        return this.userIdList;
    }

    public String getMentionedContent() {
        return this.mentionedContent;
    }

    public void setType(MentionedInfo.MentionedType type) {
        this.type = type;
    }

    public void setMentionedUserIdList(List<String> userList) {
        this.userIdList = userList;
    }

    public void setMentionedContent(String content) {
        this.mentionedContent = content;
    }

    public static enum MentionedType {
        ALL(1),
        PART(2);

        private int value;

        private MentionedType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static MentionedInfo.MentionedType valueOf(int value) {
            MentionedInfo.MentionedType[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                MentionedInfo.MentionedType type = arr$[i$];
                if(type.getValue() == value) {
                    return type;
                }
            }

            return ALL;
        }
    }
}

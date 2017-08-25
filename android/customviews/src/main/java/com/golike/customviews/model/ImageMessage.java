package com.golike.customviews.model;

import android.net.Uri;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.golike.customviews.utilities.ParcelUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 2017/8/23.
 */

//@MessageTag(
//        value = "RC:ImgMsg",
//        flag = 3,
//        messageHandler = ImageMessageHandler.class
//)
public class ImageMessage extends MessageContent {
    private Uri mThumUri;
    private Uri mLocalUri;
    private Uri mRemoteUri;
    private boolean mUpLoadExp = false;
    private String mBase64;
    boolean mIsFull;
    protected String extra;
    public static final Creator<ImageMessage> CREATOR = new Creator() {
        public ImageMessage createFromParcel(Parcel source) {
            return new ImageMessage(source);
        }

        public ImageMessage[] newArray(int size) {
            return new ImageMessage[size];
        }
    };

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public ImageMessage(byte[] data) {
        String jsonStr = new String(data);

        try {
            JSONObject e = new JSONObject(jsonStr);
            if(e.has("imageUri")) {
                String uri = e.optString("imageUri");
                if(!TextUtils.isEmpty(uri)) {
                    this.setRemoteUri(Uri.parse(uri));
                }

                if(this.getRemoteUri() != null && this.getRemoteUri().getScheme() != null && this.getRemoteUri().getScheme().equals("file")) {
                    this.setLocalUri(this.getRemoteUri());
                }
            }

            if(e.has("content")) {
                this.setBase64(e.optString("content"));
            }

            if(e.has("extra")) {
                this.setExtra(e.optString("extra"));
            }

            if(e.has("exp")) {
                this.setUpLoadExp(true);
            }

            if(e.has("isFull")) {
                this.setIsFull(e.optBoolean("isFull"));
            }

            if(e.has("user")) {
                this.setUserInfo(this.parseJsonToUserInfo(e.getJSONObject("user")));
            }
        } catch (JSONException var5) {
            Log.e("JSONException", var5.getMessage());
        }

    }

    public ImageMessage() {
    }

    private ImageMessage(Uri thumbUri, Uri localUri) {
        this.mThumUri = thumbUri;
        this.mLocalUri = localUri;
    }

    private ImageMessage(Uri thumbUri, Uri localUri, boolean original) {
        this.mThumUri = thumbUri;
        this.mLocalUri = localUri;
        this.mIsFull = original;
    }

    public static ImageMessage obtain(Uri thumUri, Uri localUri) {
        return new ImageMessage(thumUri, localUri);
    }

    public static ImageMessage obtain(Uri thumUri, Uri localUri, boolean isFull) {
        return new ImageMessage(thumUri, localUri, isFull);
    }

    public static ImageMessage obtain() {
        return new ImageMessage();
    }

    public Uri getThumUri() {
        return this.mThumUri;
    }

    public boolean isFull() {
        return this.mIsFull;
    }

    public void setIsFull(boolean isFull) {
        this.mIsFull = isFull;
    }

    public void setThumUri(Uri thumUri) {
        this.mThumUri = thumUri;
    }

    public Uri getLocalUri() {
        return this.mLocalUri;
    }

    public void setLocalUri(Uri localUri) {
        this.mLocalUri = localUri;
    }

    public Uri getRemoteUri() {
        return this.mRemoteUri;
    }

    public void setRemoteUri(Uri remoteUri) {
        this.mRemoteUri = remoteUri;
    }

    public void setBase64(String base64) {
        this.mBase64 = base64;
    }

    public String getBase64() {
        return this.mBase64;
    }

    public boolean isUpLoadExp() {
        return this.mUpLoadExp;
    }

    public void setUpLoadExp(boolean upLoadExp) {
        this.mUpLoadExp = upLoadExp;
    }

    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            if(!TextUtils.isEmpty(this.mBase64)) {
                jsonObj.put("content", this.mBase64);
            } else {
                Log.d("ImageMessage", "base64 is null");
            }

            if(this.mRemoteUri != null) {
                jsonObj.put("imageUri", this.mRemoteUri.toString());
            } else if(this.getLocalUri() != null) {
                jsonObj.put("imageUri", this.getLocalUri().toString());
            }

            if(this.mUpLoadExp) {
                jsonObj.put("exp", true);
            }

            jsonObj.put("isFull", this.mIsFull);
            if(!TextUtils.isEmpty(this.getExtra())) {
                jsonObj.put("extra", this.getExtra());
            }

            if(this.getJSONUserInfo() != null) {
                jsonObj.putOpt("user", this.getJSONUserInfo());
            }
        } catch (JSONException var3) {
            Log.e("JSONException", var3.getMessage());
        }

        this.mBase64 = null;
        return jsonObj.toString().getBytes();
    }

    public int describeContents() {
        return 0;
    }

    public ImageMessage(Parcel in) {
        this.setExtra(ParcelUtils.readFromParcel(in));
        this.mLocalUri = (Uri)ParcelUtils.readFromParcel(in, Uri.class);
        this.mRemoteUri = (Uri)ParcelUtils.readFromParcel(in, Uri.class);
        this.mThumUri = (Uri)ParcelUtils.readFromParcel(in, Uri.class);
        this.setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
        this.mIsFull = ParcelUtils.readIntFromParcel(in).intValue() == 1;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest, this.getExtra());
        ParcelUtils.writeToParcel(dest, this.mLocalUri);
        ParcelUtils.writeToParcel(dest, this.mRemoteUri);
        ParcelUtils.writeToParcel(dest, this.mThumUri);
        ParcelUtils.writeToParcel(dest, this.getUserInfo());
        ParcelUtils.writeToParcel(dest, Integer.valueOf(this.mIsFull?1:0));
    }
}


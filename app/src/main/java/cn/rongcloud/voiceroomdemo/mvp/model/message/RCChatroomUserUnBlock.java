/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.model.message;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * @author gusd
 * @Date 2021/06/17
 */
@MessageTag(value = "RC:Chatroom:User:UnBlock")
public class RCChatroomUserUnBlock extends MessageContent {
    private static final String TAG = "RCChatroomUserUnBlock";

    private String id;
    private String extra;

    public RCChatroomUserUnBlock(byte[] data){
        super(data);
        String jsonStr = null;
        jsonStr = new String(data, StandardCharsets.UTF_8);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("id")) {
                id = jsonObj.getString("id");
            }

            if (jsonObj.has("extra")) {
                extra = jsonObj.getString("extra");
            }

        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();
        try {
            if (!TextUtils.isEmpty(id)) {
                jsonObj.put("id", id);
            }
            if (!TextUtils.isEmpty(extra)) {
                jsonObj.put("extra", extra);
            }
            return jsonObj.toString().getBytes(StandardCharsets.UTF_8);
        } catch (JSONException e) {
            Log.e(TAG, "JSONException " + e.getMessage());
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.extra);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readString();
        this.extra = source.readString();
    }

    public RCChatroomUserUnBlock() {
    }



    protected RCChatroomUserUnBlock(Parcel in) {
        this.id = in.readString();
        this.extra = in.readString();
    }

    public static final Parcelable.Creator<RCChatroomUserUnBlock> CREATOR = new Parcelable.Creator<RCChatroomUserUnBlock>() {
        @Override
        public RCChatroomUserUnBlock createFromParcel(Parcel source) {
            return new RCChatroomUserUnBlock(source);
        }

        @Override
        public RCChatroomUserUnBlock[] newArray(int size) {
            return new RCChatroomUserUnBlock[size];
        }
    };
}

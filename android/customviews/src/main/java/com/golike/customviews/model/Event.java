package com.golike.customviews.model;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.model.Message.SentStatus;

/**
 * Created by admin on 2017/9/19.
 */

public class Event {
    public Event() {
    }

    public static class CSTerminateEvent {
        private String text;
        private Activity activity;

        public CSTerminateEvent(Activity activity, String content) {
            this.activity = activity;
            this.text = content;
        }

        public String getText() {
            return this.text;
        }

        public Activity getActivity() {
            return this.activity;
        }
    }

    public static class DraftEvent {
        private String content;
        private ConversationType conversationType;
        private String targetId;

        public DraftEvent(ConversationType conversationType, String targetId, String content) {
            this.conversationType = conversationType;
            this.targetId = targetId;
            this.content = content;
        }

        public ConversationType getConversationType() {
            return this.conversationType;
        }

        public String getTargetId() {
            return this.targetId;
        }

        public String getContent() {
            return this.content;
        }
    }

    public static class SyncReadStatusEvent {
        private ConversationType type;
        private String targetId;

        public String getTargetId() {
            return this.targetId;
        }

        public ConversationType getConversationType() {
            return this.type;
        }

        public SyncReadStatusEvent(ConversationType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }
    }

    public static class ReadReceiptResponseEvent {
        private ConversationType type;
        private String targetId;
        private String messageUId;
        private HashMap<String, Long> responseUserIdList;

        public ConversationType getConversationType() {
            return this.type;
        }

        public String getTargetId() {
            return this.targetId;
        }

        public String getMessageUId() {
            return this.messageUId;
        }

        public HashMap<String, Long> getResponseUserIdList() {
            return this.responseUserIdList;
        }

        public ReadReceiptResponseEvent(ConversationType type, String targetId, String messageUId, HashMap<String, Long> responseUserIdList) {
            this.type = type;
            this.targetId = targetId;
            this.messageUId = messageUId;
            this.responseUserIdList = responseUserIdList;
        }
    }

    public static class ReadReceiptRequestEvent {
        private ConversationType type;
        private String targetId;
        private String messageUId;

        public ConversationType getConversationType() {
            return this.type;
        }

        public String getTargetId() {
            return this.targetId;
        }

        public String getMessageUId() {
            return this.messageUId;
        }

        public ReadReceiptRequestEvent(ConversationType type, String targetId, String messageUId) {
            this.type = type;
            this.targetId = targetId;
            this.messageUId = messageUId;
        }
    }


    public static class ClearConversationEvent {
        private List<ConversationType> typeList = new ArrayList();

        public ClearConversationEvent() {
        }

        public static Event.ClearConversationEvent obtain(ConversationType... conversationTypes) {
            Event.ClearConversationEvent clearConversationEvent = new Event.ClearConversationEvent();
            clearConversationEvent.setTypes(conversationTypes);
            return clearConversationEvent;
        }

        public void setTypes(ConversationType[] types) {
            if(types != null && types.length != 0) {
                this.typeList.clear();
                ConversationType[] arr$ = types;
                int len$ = types.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    ConversationType type = arr$[i$];
                    this.typeList.add(type);
                }

            }
        }

        public List<ConversationType> getTypes() {
            return this.typeList;
        }
    }

    public static class ReadReceiptEvent {
        private Message readReceiptMessage;

        public ReadReceiptEvent(Message message) {
            this.readReceiptMessage = message;
        }

        public Message getMessage() {
            return this.readReceiptMessage;
        }
    }

    public static class PlayAudioEvent {
        public int messageId;
        public boolean continuously;

        public PlayAudioEvent() {
        }

        public static Event.PlayAudioEvent obtain() {
            return new Event.PlayAudioEvent();
        }
    }

    public static class ConnectEvent {
        private boolean isConnectSuccess;

        public ConnectEvent() {
        }

        public static Event.ConnectEvent obtain(boolean flag) {
            Event.ConnectEvent event = new Event.ConnectEvent();
            event.setConnectStatus(flag);
            return event;
        }

        public void setConnectStatus(boolean flag) {
            this.isConnectSuccess = flag;
        }

        public boolean getConnectStatus() {
            return this.isConnectSuccess;
        }
    }

    public static class NotificationPublicServiceInfoEvent {
        private String key;

        NotificationPublicServiceInfoEvent(String key) {
            this.setKey(key);
        }

        public static Event.NotificationPublicServiceInfoEvent obtain(String key) {
            return new Event.NotificationPublicServiceInfoEvent(key);
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class NotificationDiscussionInfoEvent {
        private String key;

        NotificationDiscussionInfoEvent(String key) {
            this.setKey(key);
        }

        public static Event.NotificationDiscussionInfoEvent obtain(String key) {
            return new Event.NotificationDiscussionInfoEvent(key);
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class NotificationGroupInfoEvent {
        private String key;

        NotificationGroupInfoEvent(String key) {
            this.setKey(key);
        }

        public static Event.NotificationGroupInfoEvent obtain(String key) {
            return new Event.NotificationGroupInfoEvent(key);
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class NotificationUserInfoEvent {
        private String key;

        NotificationUserInfoEvent(String key) {
            this.setKey(key);
        }

        public static Event.NotificationUserInfoEvent obtain(String key) {
            return new Event.NotificationUserInfoEvent(key);
        }

        public String getKey() {
            return this.key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static class AudioListenedEvent extends Event.BaseConversationEvent {
        private Message message;

        public AudioListenedEvent(Message message) {
            this.message = message;
        }

        public Message getMessage() {
            return this.message;
        }
    }

    public static class VoiceInputOperationEvent {
        public static int STATUS_DEFAULT = -1;
        public static int STATUS_INPUTING = 0;
        public static int STATUS_INPUT_COMPLETE = 1;
        private int status;

        public VoiceInputOperationEvent(int status) {
            this.setStatus(status);
        }

        public static Event.VoiceInputOperationEvent obtain(int status) {
            return new Event.VoiceInputOperationEvent(status);
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    public static class PublicServiceFollowableEvent extends Event.BaseConversationEvent {
        private boolean isFollow = false;

        public PublicServiceFollowableEvent(String targetId, ConversationType conversationType, boolean isFollow) {
            this.setTargetId(targetId);
            this.setConversationType(conversationType);
            this.setIsFollow(isFollow);
        }

        public static Event.PublicServiceFollowableEvent obtain(String targetId, ConversationType conversationType, boolean isFollow) {
            return new Event.PublicServiceFollowableEvent(targetId, conversationType, isFollow);
        }

        public boolean isFollow() {
            return this.isFollow;
        }

        public void setIsFollow(boolean isFollow) {
            this.isFollow = isFollow;
        }
    }


    protected static class BaseConversationEvent {
        protected ConversationType mConversationType;
        protected String mTargetId;

        protected BaseConversationEvent() {
        }

        public ConversationType getConversationType() {
            return this.mConversationType;
        }

        public void setConversationType(ConversationType conversationType) {
            this.mConversationType = conversationType;
        }

        public String getTargetId() {
            return this.mTargetId;
        }

        public void setTargetId(String targetId) {
            this.mTargetId = targetId;
        }
    }

    public static class RemoveFromBlacklistEvent {
        String userId;

        public RemoveFromBlacklistEvent(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return this.userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class AddToBlacklistEvent {
        String userId;

        public AddToBlacklistEvent(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return this.userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class QuitChatRoomEvent {
        String chatRoomId;

        public QuitChatRoomEvent(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public String getChatRoomId() {
            return this.chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }
    }

    public static class JoinChatRoomEvent {
        String chatRoomId;
        int defMessageCount;

        public JoinChatRoomEvent(String chatRoomId, int defMessageCount) {
            this.chatRoomId = chatRoomId;
            this.defMessageCount = defMessageCount;
        }

        public String getChatRoomId() {
            return this.chatRoomId;
        }

        public void setChatRoomId(String chatRoomId) {
            this.chatRoomId = chatRoomId;
        }

        public int getDefMessageCount() {
            return this.defMessageCount;
        }

        public void setDefMessageCount(int defMessageCount) {
            this.defMessageCount = defMessageCount;
        }
    }

    public static class QuitGroupEvent {
        String groupId;

        public QuitGroupEvent(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupId() {
            return this.groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
    }

    public static class JoinGroupEvent {
        String groupId;
        String groupName;

        public JoinGroupEvent(String groupId, String groupName) {
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public String getGroupId() {
            return this.groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return this.groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }
    }

    public static class RemoveMemberFromDiscussionEvent {
        String discussionId;
        String userId;

        public RemoveMemberFromDiscussionEvent(String discussionId, String userId) {
            this.discussionId = discussionId;
            this.userId = userId;
        }

        public String getDiscussionId() {
            return this.discussionId;
        }

        public void setDiscussionId(String discussionId) {
            this.discussionId = discussionId;
        }

        public String getUserId() {
            return this.userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    public static class AddMemberToDiscussionEvent {
        String discussionId;
        List<String> userIdList;

        public AddMemberToDiscussionEvent(String discussionId, List<String> userIdList) {
            this.discussionId = discussionId;
            this.userIdList = userIdList;
        }

        public String getDiscussionId() {
            return this.discussionId;
        }

        public void setDiscussionId(String discussionId) {
            this.discussionId = discussionId;
        }

        public List<String> getUserIdList() {
            return this.userIdList;
        }

        public void setUserIdList(List<String> userIdList) {
            this.userIdList = userIdList;
        }
    }

    public static class CreateDiscussionEvent {
        String discussionId;
        String discussionName;
        List<String> userIdList;

        public CreateDiscussionEvent(String discussionId, String discussionName, List<String> userIdList) {
            this.discussionId = discussionId;
            this.discussionName = discussionName;
            this.userIdList = userIdList;
        }

        public String getDiscussionId() {
            return this.discussionId;
        }

        public void setDiscussionId(String discussionId) {
            this.discussionId = discussionId;
        }

        public String getDiscussionName() {
            return this.discussionName;
        }

        public void setDiscussionName(String discussionName) {
            this.discussionName = discussionName;
        }

        public List<String> getUserIdList() {
            return this.userIdList;
        }

        public void setUserIdList(List<String> userIdList) {
            this.userIdList = userIdList;
        }
    }

    public static class MessagesClearEvent {
        ConversationType type;
        String targetId;

        public MessagesClearEvent(ConversationType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }

        public ConversationType getType() {
            return this.type;
        }

        public void setType(ConversationType type) {
            this.type = type;
        }

        public String getTargetId() {
            return this.targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    public static class MessageDeleteEvent {
        List<Integer> messageIds;

        public MessageDeleteEvent(int... ids) {
            if(ids != null && ids.length != 0) {
                this.messageIds = new ArrayList();
                int[] arr$ = ids;
                int len$ = ids.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    int id = arr$[i$];
                    this.messageIds.add(Integer.valueOf(id));
                }

            }
        }

        public List<Integer> getMessageIds() {
            return this.messageIds;
        }

        public void setMessageIds(List<Integer> messageIds) {
            this.messageIds = messageIds;
        }
    }

    public static class MessageSentStatusEvent {
        int messageId;
        SentStatus sentStatus;

        public MessageSentStatusEvent(int messageId, SentStatus sentStatus) {
            this.messageId = messageId;
            this.sentStatus = sentStatus;
        }

        public int getMessageId() {
            return this.messageId;
        }

        public void setMessageId(int messageId) {
            this.messageId = messageId;
        }

        public SentStatus getSentStatus() {
            return this.sentStatus;
        }

        public void setSentStatus(SentStatus sentStatus) {
            this.sentStatus = sentStatus;
        }
    }

    public static class ConversationRemoveEvent {
        ConversationType type;
        String targetId;

        public ConversationRemoveEvent(ConversationType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }

        public ConversationType getType() {
            return this.type;
        }

        public void setType(ConversationType type) {
            this.type = type;
        }

        public String getTargetId() {
            return this.targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    public static class ConversationTopEvent extends Event.BaseConversationEvent {
        boolean isTop;

        public ConversationTopEvent(ConversationType type, String targetId, boolean isTop) {
            this.setConversationType(type);
            this.setTargetId(targetId);
            this.isTop = isTop;
        }

        public boolean isTop() {
            return this.isTop;
        }

        public void setTop(boolean isTop) {
            this.isTop = isTop;
        }
    }

    public static class ConversationUnreadEvent {
        ConversationType type;
        String targetId;

        public ConversationUnreadEvent(ConversationType type, String targetId) {
            this.type = type;
            this.targetId = targetId;
        }

        public ConversationType getType() {
            return this.type;
        }

        public void setType(ConversationType type) {
            this.type = type;
        }

        public String getTargetId() {
            return this.targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }
    }

    public static class OnMessageSendErrorEvent {
        Message message;
        ErrorCode errorCode;

        public OnMessageSendErrorEvent(Message message, ErrorCode errorCode) {
            this.message = message;
            this.errorCode = errorCode;
        }

        public Message getMessage() {
            return this.message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public ErrorCode getErrorCode() {
            return this.errorCode;
        }

        public void setErrorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
        }
    }

    public static class OnReceiveMessageProgressEvent {
        Message message;
        int progress;

        public OnReceiveMessageProgressEvent() {
        }

        public int getProgress() {
            return this.progress;
        }

        public Message getMessage() {
            return this.message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }
    }

    public static class MessageLeftEvent {
        public int left;

        public MessageLeftEvent(int left) {
            this.left = left;
        }
    }

    public static class OnReceiveMessageEvent {
        Message message;
        int left;

        public OnReceiveMessageEvent(Message message, int left) {
            this.message = message;
            this.left = left;
        }

        public Message getMessage() {
            return this.message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public int getLeft() {
            return this.left;
        }

        public void setLeft(int left) {
            this.left = left;
        }
    }

    public static class FileMessageEvent {
        Message message;
        int progress;
        int callBackType;
        ErrorCode errorCode;

        public FileMessageEvent(Message message, int progress, int callBackType, ErrorCode errorCode) {
            this.message = message;
            this.progress = progress;
            this.callBackType = callBackType;
            this.errorCode = errorCode;
        }

        public Message getMessage() {
            return this.message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public int getProgress() {
            return this.progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getCallBackType() {
            return this.callBackType;
        }

        public void setCallBackType(int callBackType) {
            this.callBackType = callBackType;
        }

        public ErrorCode getErrorCode() {
            return this.errorCode;
        }

        public void setErrorCode(ErrorCode errorCode) {
            this.errorCode = errorCode;
        }
    }

    public static enum ErrorCode {
        PARAMETER_ERROR(-3, "the parameter is error."),
        IPC_DISCONNECT(-2, "IPC is not connected"),
        UNKNOWN(-1, "unknown"),
        CONNECTED(0, "connected"),
        MSG_ROAMING_SERVICE_UNAVAILABLE('胯', "Message roaming service unavailable"),
        NOT_IN_DISCUSSION(21406, ""),
        NOT_IN_GROUP(22406, ""),
        FORBIDDEN_IN_GROUP(22408, ""),
        NOT_IN_CHATROOM(23406, ""),
        FORBIDDEN_IN_CHATROOM(23408, ""),
        KICKED_FROM_CHATROOM(23409, ""),
        RC_CHATROOM_NOT_EXIST(23410, "Chat room does not exist"),
        RC_CHATROOM_IS_FULL(23411, "Chat room is full"),
        RC_CHATROOM_ILLEGAL_ARGUMENT(23412, "illegal argument."),
        REJECTED_BY_BLACKLIST(405, "rejected by blacklist"),
        RC_NET_CHANNEL_INVALID(30001, "Socket does not exist"),
        RC_NET_UNAVAILABLE(30002, ""),
        RC_MSG_RESP_TIMEOUT(30003, ""),
        RC_HTTP_SEND_FAIL(30004, ""),
        RC_HTTP_REQ_TIMEOUT(30005, ""),
        RC_HTTP_RECV_FAIL(30006, ""),
        RC_NAVI_RESOURCE_ERROR(30007, ""),
        RC_NODE_NOT_FOUND(30008, ""),
        RC_DOMAIN_NOT_RESOLVE(30009, ""),
        RC_SOCKET_NOT_CREATED(30010, ""),
        RC_SOCKET_DISCONNECTED(30011, ""),
        RC_PING_SEND_FAIL(30012, ""),
        RC_PONG_RECV_FAIL(30013, ""),
        RC_MSG_SEND_FAIL(30014, ""),
        RC_CONN_OVERFREQUENCY(30015, "Connect over frequency."),
        RC_CONN_ACK_TIMEOUT(31000, ""),
        RC_CONN_PROTO_VERSION_ERROR(31001, ""),
        RC_CONN_ID_REJECT(31002, ""),
        RC_CONN_SERVER_UNAVAILABLE(31003, ""),
        RC_CONN_USER_OR_PASSWD_ERROR(31004, ""),
        RC_CONN_NOT_AUTHRORIZED(31005, ""),
        RC_CONN_REDIRECTED(31006, ""),
        RC_CONN_PACKAGE_NAME_INVALID(31007, ""),
        RC_CONN_APP_BLOCKED_OR_DELETED(31008, ""),
        RC_CONN_USER_BLOCKED(31009, ""),
        RC_DISCONN_KICK(31010, ""),
        RC_DISCONN_EXCEPTION(31011, ""),
        RC_QUERY_ACK_NO_DATA(32001, ""),
        RC_MSG_DATA_INCOMPLETE(32002, ""),
        RC_CONN_REFUSED(32061, "connection is refused"),
        BIZ_ERROR_CLIENT_NOT_INIT('胩', ""),
        BIZ_ERROR_DATABASE_ERROR('胪', ""),
        BIZ_ERROR_INVALID_PARAMETER('胫', ""),
        BIZ_ERROR_NO_CHANNEL('胬', ""),
        BIZ_ERROR_RECONNECT_SUCCESS('胭', ""),
        BIZ_ERROR_CONNECTING('胮', ""),
        NOT_FOLLOWED(29106, ""),
        PARAMETER_INVALID_CHATROOM(23412, "invalid parameter"),
        ROAMING_SERVICE_UNAVAILABLE_CHATROOM(23414, "");

        private int code;
        private String msg;

        private ErrorCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getValue() {
            return this.code;
        }

        public String getMessage() {
            return this.msg;
        }

        public static  ErrorCode valueOf(int code) {
            ErrorCode[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                ErrorCode c = arr$[i$];
                if(code == c.getValue()) {
                    return c;
                }
            }

            Log.d("ErrorCode", "valueOf,ErrorCode:" + code);
            return UNKNOWN;
        }
    }
}

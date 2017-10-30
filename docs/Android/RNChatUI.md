## RNChatUI for React Native

## 配置

- ## 用法

```
import {
  NativeModules,
} from 'react-native';
```

## 数据格式

加载消息列表UI,需要传递一定格式的消息对象

- `message` 对象格式:

```
message = {  // text message
     "type": "text",
     "own": false,//是否为当前用户
     "content": "发送文本内容",
     "ts": "发送时间",
     "uid": "当前用户id",
     "msgid": "消息uid",
     "senduserinfo": "发送者信息"
}    

message = {  // image message
     "type": "image",
     "own": false,//是否为当前用户
     "content": "图片URL",
     "ts": "发送时间",
     "uid": "当前用户id",
     "msgid": "消息uid",
     "senduserinfo": "发送者信息"
}

message = {  // voice message
     "type": "voice",
     "own": false,//是否为当前用户
     "duration": "50",//时长 单位：秒
     "content": "语音URL",
     "ts": "发送时间",
     "uid": "当前用户id",
     "msgid": "消息uid",
     "senduserinfo": "发送者信息"
}

message = {  // chatInfo message
     "userid": "用户id",
     "name": "昵称",
     "portraitUri": "头像URL",
     "chattype": "聊天类型",//群组 私聊
     "targetid": "目标id"
}

```

## 事件处理

### RNChatUI 事件

### RNChatUI update/insert 消息方法:
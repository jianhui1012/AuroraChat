## ChatView for React Native

## 配置

- ## 用法

```
import {DeviceEventEmitter} from "react-native";
import ChatView from "../rychatview";
```

- [ChatView](#ChatView)

  - [Props 属性]()
    - [chatInfo](#chatInfo)
    - [style](#style)
    - [isOnRefresh](#isOnRefresh)
    - [OnRefresh](#OnRefresh)
  - [DeviceEventEmitter监听 uploadMsg事件]()
  - [组件内的方法]()
    - [getHistoryMessage](#getHistoryMessage)
    - [sendTextMsg](#sendTextMsg)
    - [sendRichTextMsg](#sendRichTextMsg)
    - [sendPicMsg](#sendPicMsg)
    - [sendVoiceMsg](#sendVoiceMsg)


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

## ChatView介绍

### Props 属性

#### chatInfo

**PropTypes.object:登录聊天服务器后设置初始化**

```
curChatInfo:  {
                "userid": "1001",
                
                "name": "golike",
                "portraitUri":"http://img0.imgtn.bdimg.com/it/u=651843754,4204488972&fm=213&gp=0.jpg",

                "chattype": "priv",

                "targetid": "1002"
            }
```

#### isOnRefresh

**PropTypes.bool:下拉刷新的标识符**

#### OnRefresh

**PropTypes.function:** ```() => {//重新网络请求 } ```

### DeviceEventEmitter监听 uploadMsg事件

**监听来自原生的不同消息** 

```
  componentDidMount() {
        //设置来自原生的消息的监听
        this.subscription = DeviceEventEmitter.addListener('uploadMsg', this.onUpdateMessage);
    }

  componentWillUnmount() {
        //移除监听器
        this.subscription.remove();
    }
```

### 组件内的方法

#### getHistoryMessage

**PropTypes.function:加载历史消息**

```
  historyMsgs = [ 前面消息类型中的消息,*,...]
 (historyMsgs) => {//连接消息服务器成功后获取的历史消息列表,发送给原生};
```

#### sendTextMsg

**PropTypes.function:**

```
 (msg) => {//从js构造文本数据发送给原生};
```

#### sendRichTextMsg

**PropTypes.function:未实现**

```
 (msg) => {//从js构造富文本数据发送给原生};
```

#### sendPicMsg

**PropTypes.function:**

```
 (msg) => {//从js构造图片数据发送给原生};
```

#### sendVoiceMsg

**PropTypes.function:**

```
 (msg) => {//从js构造语音数据发送给原生};
```
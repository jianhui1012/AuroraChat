import React, {Component} from "react";
import {View, ListView, StyleSheet, DeviceEventEmitter} from "react-native";
import ChatView from "../modules/rychatview";
var Dimensions = require('Dimensions');
var DataSource = new ListView.DataSource({rowHasChanged: (r1, r2) => r1 !== r2});

class ChatUI extends Component {
    constructor(props) {
        super(props);
        this.userid = "1001";
        this.chattype = "priv";
        this.offset = 0;
        this.count = 10;
        this.historyMsgs = [];
        this.state = {
            dataSource: DataSource.cloneWithRows([]),
            noMoreData: false,
            curChatInfo: {}
        };
    }

    componentDidMount() {
        //初始化聊天信息
        let result = this.initChatInfo();
        setTimeout(()=>{
            this.refreshList(this.offset, this.count);
        },1000);
        //设置来自原生的消息的监听
        this.subscription = DeviceEventEmitter.addListener('uploadMsg', this.onUpdateMessage);
        result.then((code) => {
            //console.log(code)
        });
    }

    componentWillUnmount() {
        this.subscription.remove();
    }

    //init chat envoirment
    async initChatInfo() {
        this.setState({
            curChatInfo:  {
                "userid": "1001",
                "name": "golike",
                "portraitUri": "http://img0.imgtn.bdimg.com/it/u=651843754,4204488972&fm=213&gp=0.jpg",
                "chattype": "priv",
                "targetid": "1002"
            }
        });
    }

    async getNewHistoryMsg(msgs) {
        this.historyMsgs=[];
        //let newMsg;
        for (let i = msgs.length - 1; i >= 0; i--) {
            let msg = msgs[i];
            let sendUserInfo = {
                "senduserid": "" + msg.senduserid,
                "sendname": "djh10112",
                "portraitUri": "http://img3.imgtn.bdimg.com/it/u=3449010647,3468950612&fm=213&gp=0.jpg"
            };
            let newMsg = this.getTypeMsg(msg, sendUserInfo, msg.senduserid == this.userid);
            console.warn("newMsg:"+JSON.stringify(newMsg));
            this.historyMsgs.push(newMsg);
        }

        this.liveview.getHistoryMessage({"historyMessage": this.historyMsgs});
    }

    //加载历史消息列表
    refreshList(offset, count) {
        if (offset <= 40) {
            let msgs = [];
            for (let i = offset+ count; i >= offset; i--) {
                msgs.push({
                    "senduserid": "1002", "type": "text",
                    "content": "test" + i,
                    "senttime": new Date().getTime(),
                    "uid": "uid" + i,
                    "msgid": 1000+i
                });
            }
            this.getNewHistoryMsg(msgs).then(function (code) {
                //console.warn(code);
            });
        }
        else {
            this.setState({noMoreData: true});
        }
        this.offset = offset + count;
    }

    //根据消息类型构造新的消息数据体
    getTypeMsg(msg, senduserinfo, own) {
        var data = {};
        if ("text" == msg.type) {
            data = {
                "type": msg.type,
                "own": own,
                "content": msg.content,
                "ts": this.utcToLocalTime(msg.senttime),
                "uid": "" + msg.uid,
                "msgid": msg.msgid,
                "senduserinfo": senduserinfo
            };
        } else if ("image" == msg.type) {
            data = {
                "type": msg.type,
                "own": own,
                "content": msg.content,
                "ts": this.utcToLocalTime(msg.senttime),
                "uid": "" + msg.uid,
                "msgid": msg.msgid,
                "senduserinfo": senduserinfo
            };
        } else if ("voice" == msg.type) {
            //console.warn("duration:"+msg.duration);
            data = {
                "type": msg.type,
                "own": own,
                "duration": msg.extra.duration,
                "content": msg.content,
                "ts": this.utcToLocalTime(msg.senttime),
                "uid": "" + msg.uid,
                "msgid": msg.msgid,
                "senduserinfo": senduserinfo
            };
        }
        return data;
    }

    //utc时间转化成本地时间
    utcToLocalTime(utctime) {
        let localtime = utctime;
        //localtime -= new Date().getTimezoneOffset() * 60;
        return localtime * 1000 + "";
    }

    //下拉刷新
    OnRefresh() {
        if (this.state.noMoreData) {
            console.warn("数据已加载完毕");
            return;
        }
        console.warn(this.offset);
        this.refreshList(this.offset, this.count);
    }

    //向消息服务器发送所有类型消息
    async onSendTextPrivMsg(userid, contentMsg, msguid) {
    }

    async onSendImagePrivMsg(userid, imageObject, msguid) {
        console.warn("uri:" + imageObject['uri'] + ",md5:" + imageObject['md5']);
        var result = await DataStore.uploadFile(imageObject['uri'], imageObject['md5']);
        if (result != 0) {
            console.warn('上传图片失败');
            return "";
        }
        var args = {
            'userid': userid,
            'content': imageObject['md5'],
            'msguid': msguid
        };
        var url = DataStore.imstoreQuery("sendimage", args);
        var response = await fetch(url);
        var responseJson = await response.json();
        if (responseJson.response.error) {
            console.warn(responseJson.response.message);
        }
    }

    async onSendVoicePrivMsg(userid, voiceObject, msguid, duration) {
    }

    async onSendTextGroupMsg(groupid, contentMsg, msguid) {
    }

    async onSendImageGroupMsg(groupid, imageObject, msguid) {
    }

    async onSendVoiceGroupMsg(groupid, voiceObject, msguid, duration) {
    }

    //从原生向JS发送消息
    onUpdateMessage = (msg) => {
        //console.warn("msg:" + JSON.stringify(msg));
        let result = null;
        if ("priv" == msg.chattype) {
            if ("text" == msg.type) {
                result = this.onSendTextPrivMsg(this.props.ctarget, msg.content, msg.msguid);
            } else if ("image" == msg.type) {
                result = this.onSendImagePrivMsg(this.props.ctarget, msg.content, msg.msguid);
            } else if ("voice" == msg.type) {
                result = this.onSendVoicePrivMsg(this.props.ctarget, msg.content, msg.msguid, msg.duration);
            }
        }
        if ("group" == msg.chattype) {
            if ("text" == msg.type) {
                result = this.onSendTextGroupMsg(this.props.ctarget, msg.content, msg.msguid);
            } else if ("image" == msg.type) {
                result = this.onSendImageGroupMsg(this.props.ctarget, msg.content, msg.msguid);
            } else if ("voice" == msg.type) {
                result = this.onSendVoiceGroupMsg(this.props.ctarget, msg.content, msg.msguid, msg.duration);
            }
        }
        if (result != null)
            result.then(function (code) {
                //console.log("code:" + JSON.stringify(code));
            });
    };

    render() {
        return (
            <View style={styles.container}>
                <ChatView ref={(obj) => this.liveview = obj} style={styles.chats} isOnRefresh={this.state.noMoreData}
                          chatInfo={this.state.curChatInfo} OnRefresh={this.OnRefresh.bind(this)}/>
            </View>
        );
    }


}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F5FCFF',
    },
    chats: {
        flex: 1,
        //width: Dimensions.get('window').width,
    }
});

module.exports = ChatUI;
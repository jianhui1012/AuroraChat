/**
 * Created by admin on 2017/8/28.
 */
'use strict';
import React  from 'react';
var UIManager = require('UIManager');
import { requireNativeComponent, View ,findNodeHandle} from 'react-native';

const CHATVIEW_REF = 'RCTChatUI';

class ChatView extends React.Component {

    constructor(props) {
        super(props);
        this.onChatUIEvent=this.onChatUIEvent.bind(this);
    }

    getChatViewHandle() {
        return findNodeHandle(this.refs[CHATVIEW_REF]);
    }

    componentDidMount() {
        this._mounted = true;
    }

    componentWillUnmount() {
        this._mounted = false;
    }

    onChatUIEvent(event) {
        if (!this._mounted)
            return;
        var cmd = event.nativeEvent.cmd;
        //console.warn("_mounted:"+this._mounted+",event:"+cmd);
        if (cmd == 'OnRefresh') {
            if (this.props.OnRefresh) {
                this.props.OnRefresh();
            }
        }
    }

    sendTextMsg(msg) {
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.sendTextMsg,
            [msg]
        );
    }

    sendVoiceMsg(msg) {
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.sendVoiceMsg,
            [msg]
        );
    }

    sendPicMsg(msg) {
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.sendPicMsg,
            [msg]
        );
    }

    sendRichTextMsg(msg) {
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.sendRichTextMsg,
            [msg]
        );
    }

    getHistoryMessage(msg) {
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.getHistoryMessage,
            [msg]
        );
    }

    render() {
        return (<RCTChatUI ref={CHATVIEW_REF} isOnRefresh={this.props.isOnRefresh} chatInfo={this.props.chatInfo}
                           style={this.props.style} onChatUIEvent={ this.onChatUIEvent}>
        </RCTChatUI>);
    }
}

ChatView.propTypes = {
    isOnRefresh: React.PropTypes.bool,
    chatInfo: React.PropTypes.object,
    ...View.propTypes // 包含默认的View的属性
};

var RCTChatUI = requireNativeComponent(CHATVIEW_REF, ChatView, {
    nativeOnly: {
        onChatUIEvent: true,
    },
});

module.exports = ChatView;

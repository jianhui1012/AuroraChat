/**
 * Created by admin on 2017/8/28.
 */
'use strict';
import React  from 'react';
import { requireNativeComponent, View ,findNodeHandle} from 'react-native';
var UIManager = require('UIManager');

const CHATVIEW_REF = 'RCTChatUI';
class ChatView extends React.Component {
    getChatViewHandle(){
        return  findNodeHandle(this.refs[CHATVIEW_REF]);
    }
    sendTextMsg(msg){
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.sendTextMsg,
            [msg]
        );
    }
    sendVoiceMsg(msg){
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.sendVoiceMsg,
            [msg]
        );
    }
    sendPicMsg(msg){
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.sendPicMsg,
            [msg]
        );
    }
    sendRichTextMsg(msg){
        UIManager.dispatchViewManagerCommand(
            this.getChatViewHandle(),
            UIManager.RCTChatUI.Commands.sendRichTextMsg,
            [msg]
        );
    }
    render(){
        return (<RCTChatUI ref={CHATVIEW_REF}   style={this.props.style} >
        </RCTChatUI>);
    }
}

ChatView.propTypes = {
    menuContainerHeight: React.PropTypes.number,
    ...View.propTypes // 包含默认的View的属性
};

var RCTChatUI = requireNativeComponent('RCTChatUI', ChatView);

module.exports = ChatView;

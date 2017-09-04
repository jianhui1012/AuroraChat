/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, {Component} from 'react';
import {
    StyleSheet,
    Text,
    View, Button
} from 'react-native';
import ChatView from '../modules/ChatUI'

export default class ChatUI extends Component {
    constructor(props) {
        super(props)
        this.state = {
            chatview:null
        };
    }

    componentDidMount() {

    }

    render() {
        return (
            <View style={styles.container}>
                <ChatView ref={(obj) => this.state.chatview = obj} style={{height:'70%',width:'100%'}}/>
                <View  style={{width:'100%'}}>
                <Button style={styles.btn} onPress={()=>{
             this.state.chatview.sendTextMsg({
            "type": "text",
            "from": "test",
            "content": "Hello!",
            "ts": "500"});}} title="发送文本"/>
                    <View style={styles.line}/>
                <Button  style={styles.btn}  onPress={()=>{
             this.state.chatview.sendVoiceMsg({
            "type": "text",
            "from": "test",
            "content": "Hello!",
            "ts": "500"});}} title="发送语音"/>
                    <View style={styles.line}/>
                <Button  style={styles.btn}  onPress={()=>{
             this.state.chatview.sendPicMsg({
            "type": "text",
            "from": "test",
            "content": "sendPicMsg!",
            "ts": "500"});}} title="发送图片"/>
                    <View style={styles.line}/>
                <Button  style={styles.btn}  onPress={()=>{
             this.state.chatview.sendRichTextMsg({
            "type": "text",
            "from": "test",
            "content": "sendRichTextMsg!",
            "ts": "500"});}} title="发送富文本"/>
                </View>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F5FCFF',
    },
    btn: {

    },
    line: {
        height: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});



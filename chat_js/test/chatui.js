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
                <ChatView ref={(obj) => this.state.chatview = obj} style={{height:'90%',width:'100%'}}/>
                <Button onPress={()=>{
             this.state.chatview.sendTextMsg({
            "type": "text",
            "from": "test",
            "content": "Hello!",
            "ts": "500"});}} title="Press Me"/>
            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});



/**
 * Created by admin on 2017/10/11.
 */
import React, {Component} from "react";
import {View, KeyboardAvoidingView,
TextInput, StyleSheet, DeviceEventEmitter
}
from
"react-native";
import EmotionView from "../modules/emotionview";
var Dimensions = require('Dimensions');

class EmotionUI extends Component {

    _textInput: any;

    constructor(props) {
        super(props);
        this.state = {
            selection: {start: 0, end: 0},
            value: "Hello World"
        };
    }

    componentDidMount() {
        //设置来自原生的消息的监听
        this.subscription = DeviceEventEmitter.addListener('RNEMCMD', (msg) => {
            if (msg == null)
                return;
            if (msg.cmd == "insert") {
                console.warn("msg.emoji:" + msg.emoji);
                let value = this.state.value + msg.emoji;
                this.setState({value});
            } else if (msg.cmd == "delete") {

            }
        });
    }

    componentWillUnmount() {
        this.subscription.remove();
    }

    onSelectionChange({nativeEvent: {selection}}) {
        this.setState({selection});
    }


    render() {
        //var length = this.state.value.length;
        return (
            <View style={styles.container}>

                    <TextInput
                        multiline={true}
                        onChangeText={(value) => this.setState({value})}
                        onSelectionChange={this.onSelectionChange.bind(this)}
                        ref={textInput => (this._textInput = textInput)}
                        selection={this.state.selection}
                        style={styles.default}
                        value={this.state.value}
                    />

                    <EmotionView style={styles.emView}/>

            </View>
        );
    }
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: '#F5FCFF',
    },
    default: {
        height: 26,
        borderWidth: 0.5,
        borderColor: '#0f0f0f',
        flex: 1,
        fontSize: 13,
        padding: 4,
    },
    chats: {
        flex: 1,
        //width: Dimensions.get('window').width,
    },
    emView: {
        position: 'relative',
        //top: Dimensions.get('window').height - 60,
        flex: 1
    }
});

module.exports = EmotionUI;
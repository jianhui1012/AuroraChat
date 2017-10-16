/**
 * Created by admin on 2017/8/28.
 */
'use strict';
import React  from 'react';
var UIManager = require('UIManager');
import {requireNativeComponent, View, findNodeHandle} from 'react-native';

const CHATVIEW_REF = 'RCTEmotionView';

class EmotionView extends React.Component {

    constructor(props) {
        super(props);
        this.onSetEmotionEvent = this.onSetEmotionEvent.bind(this);
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

    onSetEmotionEvent(event) {
        if (!this._mounted)
            return;
        var cmd = event.nativeEvent.cmd;
        //console.warn("_mounted:"+this._mounted+",event:"+cmd);
        if (cmd == 'setEmotionBorad') {
        }
    }

    render() {
        return (<RCTEmotionView ref={CHATVIEW_REF}
                                style={this.props.style}  isShow={this.props.isShow}   onSetEmotionEvent={ this.onSetEmotionEvent}>
        </RCTEmotionView>);
    }
}

EmotionView.propTypes = {
    isShow: React.PropTypes.bool,
    ...View.propTypes // 包含默认的View的属性
};


var RCTEmotionView = requireNativeComponent(CHATVIEW_REF, EmotionView, {
    nativeOnly: {
        onSetEmotionEvent: true,
    }
});

module.exports = EmotionView;

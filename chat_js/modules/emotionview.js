/**
 * Created by admin on 2017/8/28.
 */
'use strict';
import React  from 'react';
var UIManager = require('UIManager');
import { requireNativeComponent, View ,findNodeHandle} from 'react-native';

const CHATVIEW_REF = 'RCTEmotionView';

class EmotionView extends React.Component {

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

    render() {
        return (<RCTEmotionView ref={CHATVIEW_REF}
                           style={this.props.style} >
        </RCTEmotionView>);
    }
}

EmotionView.propTypes = {
    isOnRefresh: React.PropTypes.bool,
    chatInfo: React.PropTypes.object,
    ...View.propTypes // 包含默认的View的属性
};

var RCTEmotionView = requireNativeComponent(CHATVIEW_REF, EmotionView);

module.exports = EmotionView;

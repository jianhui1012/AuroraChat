/**
 * Created by admin on 2017/8/28.
 */
'use strict';

import { PropTypes } from 'react';
import { requireNativeComponent, View } from 'react-native';

var ChatView = {
    name: 'RCTChatView',
    propTypes: {
        menuContainerHeight: PropTypes.number,
        ...View.propTypes // 包含默认的View的属性
    },
};

module.exports = requireNativeComponent('RCTChatView', ChatView);

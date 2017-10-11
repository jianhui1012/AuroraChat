/**
 * Created by admin on 2017/8/28.
 */
import {AppRegistry} from 'react-native';
import AuroraChat from './aurorachat'
import ChatUI from './chatui'
import EmotionUI from './emotionui'

AppRegistry.registerComponent('ChatUI', () => ChatUI);
AppRegistry.registerComponent('AuroraChat', () => AuroraChat);
AppRegistry.registerComponent('Emotion', () => EmotionUI);
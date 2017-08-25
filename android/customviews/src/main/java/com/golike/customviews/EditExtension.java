package com.golike.customviews;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.golike.customviews.emoticon.AndroidEmoji;
import com.golike.customviews.emoticon.EmoticonTabAdapter;
import com.golike.customviews.emoticon.IEmoticonClickListener;
import com.golike.customviews.emoticon.IEmoticonTab;
import com.golike.customviews.menu.ISubMenuItemClickListener;
import com.golike.customviews.menu.InputSubMenu;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.model.CustomServiceMode;
import com.golike.customviews.plugin.IPluginClickListener;
import com.golike.customviews.plugin.IPluginModule;
import com.golike.customviews.plugin.ImagePlugin;
import com.golike.customviews.plugin.PluginAdapter;
import com.golike.customviews.InputBar.Style;
import com.golike.customviews.utilities.ExtensionHistoryUtil;
import com.golike.customviews.utilities.ExtensionHistoryUtil.ExtensionBarState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2017/8/8.
 */

public class EditExtension  extends LinearLayout {
    private static final String TAG = "EditExtension";
    private ImageView mPSMenu;
    private View mPSDivider;
    private List<InputMenu> mInputMenuList;
    private LinearLayout mMainBar;
    private ViewGroup mExtensionBar;
    private ViewGroup mSwitchLayout;
    private ViewGroup mContainerLayout;
    private ViewGroup mPluginLayout;
    private ViewGroup mMenuContainer;
    private View mEditTextLayout;
    private EditText mEditText;
    private View mVoiceInputToggle;
    private PluginAdapter mPluginAdapter;
    private EmoticonTabAdapter mEmotionTabAdapter;
    private FrameLayout mSendToggle;
    private ImageView mEmoticonToggle;
    private ImageView mPluginToggle;
    private ImageView mVoiceToggle;
    private OnClickListener mVoiceToggleClickListener;
    private Fragment mFragment;
    private IExtensionClickListener mExtensionClickListener;
    private ConversationType mConversationType;
    private String mTargetId;
    private List<IExtensionModule> mExtensionModuleList;
    private Style mStyle;
    private String mUserId;
    boolean isKeyBoardActive = false;
    boolean collapsed = true;
    int originalTop = 0;
    int originalBottom = 0;

    public EditExtension(Context context) {
        super(context);
        this.initView();
        this.initData();
    }

    public EditExtension(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RongExtension);
        int attr = a.getInt(R.styleable.RongExtension_RCStyle, 291);
        a.recycle();
        this.initView();
        this.initData();
        this.mStyle = Style.getStyle(attr);
        if(this.mStyle != null) {
            this.setInputBarStyle(this.mStyle);
        }

    }

    public void onDestroy() {
        //RLog.d("EditExtension", "onDestroy");
        Iterator i$ = this.mExtensionModuleList.iterator();

        while(i$.hasNext()) {
            IExtensionModule module = (IExtensionModule)i$.next();
            module.onDetachedFromExtension();
        }

        this.mExtensionClickListener = null;
    }

    public void collapseExtension() {
        this.hidePluginBoard();
        this.hideEmoticonBoard();
        this.hideInputKeyBoard();
    }

    public boolean isExtensionExpanded() {
        return this.mPluginAdapter != null && this.mPluginAdapter.getVisibility() == 0 || this.mEmotionTabAdapter != null && this.mEmotionTabAdapter.getVisibility() == 0;
    }

    public void setInputBarStyle(InputBar.Style style) {
        switch(Style.values()[style.ordinal()].ordinal()) {
            case 1:
                this.setSCE();
                break;
            case 2:
                this.setC();
                break;
            case 3:
                this.setCE();
                break;
            case 4:
                this.setEC();
                break;
            case 5:
                this.setSC();
        }

    }

    public void setConversation(ConversationType conversationType, String targetId) {
        if(this.mConversationType == null && this.mTargetId == null) {
            this.mConversationType = conversationType;
            this.mTargetId = targetId;
            Iterator i$ = this.mExtensionModuleList.iterator();

            while(i$.hasNext()) {
                IExtensionModule module = (IExtensionModule)i$.next();
                module.onAttachedToExtension(this);
            }

            this.initPlugins();
            this.initEmoticonTabs();
            this.initPanelStyle();
        }

        this.mConversationType = conversationType;
        this.mTargetId = targetId;
    }

    private void initPlugins() {
        Iterator i$ = this.mExtensionModuleList.iterator();

        while(i$.hasNext()) {
            IExtensionModule module = (IExtensionModule)i$.next();
            List pluginModules = module.getPluginModules(this.mConversationType);
            if(pluginModules != null && this.mPluginAdapter != null) {
                this.mPluginAdapter.addPlugins(pluginModules);
            }
        }

    }

    private void initEmoticonTabs() {
        Iterator i$ = this.mExtensionModuleList.iterator();

        while(i$.hasNext()) {
            IExtensionModule module = (IExtensionModule)i$.next();
            List tabs = module.getEmoticonTabs();
            this.mEmotionTabAdapter.initTabs(tabs, module.getClass().getCanonicalName());
        }

    }

    public void setInputMenu(List<InputMenu> inputMenuList, boolean showFirst) {
        if(inputMenuList != null && inputMenuList.size() > 0) {
            this.mPSMenu.setVisibility(VISIBLE);
            this.mPSDivider.setVisibility(VISIBLE);
            this.mInputMenuList = inputMenuList;
            if(showFirst) {
                this.setExtensionBarVisibility(GONE);
                this.setMenuVisibility(0, inputMenuList);
            }

        } else {
            //RLog.e("EditExtension", "setInputMenu no item");
        }
    }

    private void setExtensionBarVisibility(int visibility) {
        if(visibility == 8) {
            this.hideEmoticonBoard();
            this.hidePluginBoard();
            this.hideInputKeyBoard();
        }

        this.mExtensionBar.setVisibility(visibility);
    }

    private void setMenuVisibility(int visibility, List<InputMenu> inputMenuList) {
        if(this.mMenuContainer == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            this.mMenuContainer = (ViewGroup)inflater.inflate(R.layout.rc_ext_menu_container, (ViewGroup)null);
            this.mMenuContainer.findViewById(R.id.rc_switch_to_keyboard).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    EditExtension.this.setExtensionBarVisibility(VISIBLE);
                    EditExtension.this.mMenuContainer.setVisibility(GONE);
                }
            });

            for(int i = 0; i < inputMenuList.size(); ++i) {
                final int j=i;
                final InputMenu menu = (InputMenu)inputMenuList.get(i);
                LinearLayout rootMenu = (LinearLayout)inflater.inflate(R.layout.rc_ext_root_menu_item, (ViewGroup)null);
                LayoutParams lp = new LayoutParams(-1, -1, 1.0F);
                rootMenu.setLayoutParams(lp);
                TextView title = (TextView)rootMenu.findViewById(R.id.rc_menu_title);
                title.setText(menu.title);
                ImageView iv = (ImageView)rootMenu.findViewById(R.id.rc_menu_icon);
                if(menu.subMenuList != null && menu.subMenuList.size() > 0) {
                    iv.setVisibility(VISIBLE);
                    iv.setImageResource(R.drawable.rc_menu_trangle);
                }

                rootMenu.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        List subMenuList = menu.subMenuList;
                        if(subMenuList != null && subMenuList.size() > 0) {
                            InputSubMenu subMenu = new InputSubMenu(EditExtension.this.getContext(), subMenuList);
                            subMenu.setOnItemClickListener(new ISubMenuItemClickListener() {
                                public void onClick(int index) {
                                    if(EditExtension.this.mExtensionClickListener != null) {
                                        EditExtension.this.mExtensionClickListener.onMenuClick(j, index);
                                    }

                                }
                            });
                            subMenu.showAtLocation(v);
                        } else if(EditExtension.this.mExtensionClickListener != null) {
                            EditExtension.this.mExtensionClickListener.onMenuClick(j, -1);
                        }

                    }
                });
                ViewGroup menuBar = (ViewGroup)this.mMenuContainer.findViewById(R.id.rc_menu_bar);
                menuBar.addView(rootMenu);
            }

            this.addView(this.mMenuContainer);
        }

        if(visibility == 8) {
            this.mMenuContainer.setVisibility(GONE);
        } else {
            this.mMenuContainer.setVisibility(VISIBLE);
        }

    }

    public void setExtensionBarMode(CustomServiceMode mode) {
        switch(CustomServiceMode.values()[mode.ordinal()].ordinal()) {
            case 1:
                this.setC();
                break;
            case 2:
            case 3:
                if(this.mStyle != null) {
                    this.setInputBarStyle(this.mStyle);
                }

                this.mVoiceToggle.setImageResource(R.drawable.rc_voice_toggle_selector);
                this.mVoiceToggle.setOnClickListener(this.mVoiceToggleClickListener);
                break;
            case 4:
                this.setC();
                break;
            case 5:
                this.mVoiceToggle.setImageResource(R.drawable.rc_cs_admin_selector);
                this.mVoiceToggle.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        if(EditExtension.this.mExtensionClickListener != null) {
                            EditExtension.this.mExtensionClickListener.onSwitchToggleClick(v, EditExtension.this.mContainerLayout);
                        }

                    }
                });
                this.setSC();
        }

    }

    public EditText getInputEditText() {
        return this.mEditText;
    }

    public void refreshEmoticonTabIcon(IEmoticonTab tab, Drawable icon) {
        if(icon != null && this.mEmotionTabAdapter != null && tab != null) {
            this.mEmotionTabAdapter.refreshTabIcon(tab, icon);
        }

    }

    public void addPlugin(IPluginModule pluginModule) {
        if(pluginModule != null) {
            this.mPluginAdapter.addPlugin(pluginModule);
        }

    }

    public void removePlugin(IPluginModule pluginModule) {
        if(pluginModule != null) {
            this.mPluginAdapter.removePlugin(pluginModule);
        }

    }

    public List<IPluginModule> getPluginModules() {
        return this.mPluginAdapter.getPluginModules();
    }

    public void addPluginPager(View v) {
        if(null != this.mPluginAdapter) {
            this.mPluginAdapter.addPager(v);
        }

    }

    public void removePluginPager(View v) {
        if(this.mPluginAdapter != null && v != null) {
            this.mPluginAdapter.removePager(v);
        }

    }

    public boolean addEmoticonTab(int index, IEmoticonTab tab, String tag) {
        if(this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            return this.mEmotionTabAdapter.addTab(index, tab, tag);
        } else {
            Log.e("EditExtension", "addEmoticonTab Failure");
            return false;
        }
    }

    public void addEmoticonTab(IEmoticonTab tab, String tag) {
        if(this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            this.mEmotionTabAdapter.addTab(tab, tag);
        }

    }

    public List<IEmoticonTab> getEmoticonTabs(String tag) {
        return this.mEmotionTabAdapter != null && !TextUtils.isEmpty(tag)?this.mEmotionTabAdapter.getTagTabs(tag):null;
    }

    public int getEmoticonTabIndex(String tag) {
        return this.mEmotionTabAdapter != null && !TextUtils.isEmpty(tag)?this.mEmotionTabAdapter.getTagTabIndex(tag):-1;
    }

    public boolean removeEmoticonTab(IEmoticonTab tab, String tag) {
        boolean result = false;
        if(this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            result = this.mEmotionTabAdapter.removeTab(tab, tag);
        }

        return result;
    }

    public void setCurrentEmoticonTab(IEmoticonTab tab, String tag) {
        if(this.mEmotionTabAdapter != null && tab != null && !TextUtils.isEmpty(tag)) {
            this.mEmotionTabAdapter.setCurrentTab(tab, tag);
        }

    }

    public void setEmoticonTabBarEnable(boolean enable) {
        if(this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setTabViewEnable(enable);
        }

    }

    public void setEmoticonTabBarAddEnable(boolean enable) {
        if(this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setAddEnable(enable);
        }

    }

    public void setEmoticonTabBarAddClickListener(IEmoticonClickListener listener) {
        if(this.mEmotionTabAdapter != null) {
            this.mEmotionTabAdapter.setOnEmoticonClickListener(listener);
        }

    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    public Fragment getFragment() {
        return this.mFragment;
    }

    public ConversationType getConversationType() {
        return this.mConversationType;
    }

    public String getTargetId() {
        return this.mTargetId;
    }

    public void setExtensionClickListener(IExtensionClickListener clickListener) {
        this.mExtensionClickListener = clickListener;
    }

    public void onActivityPluginResult(int requestCode, int resultCode, Intent data) {
        int position = (requestCode >> 8) - 1;
        int reqCode = requestCode & 255;
        IPluginModule pluginModule = this.mPluginAdapter.getPluginModule(position);
        if(pluginModule != null) {
            if(this.mExtensionClickListener != null && resultCode == -1) {
                if(pluginModule instanceof ImagePlugin) {
                    boolean lat1 = data.getBooleanExtra("sendOrigin", false);
                    ArrayList list = data.getParcelableArrayListExtra("android.intent.extra.RETURN_RESULT");
                    this.mExtensionClickListener.onImageResult(list, lat1);
              }
//                else  if(pluginModule instanceof DefaultLocationPlugin || pluginModule instanceof CombineLocationPlugin) {
//                    double lat = data.getDoubleExtra("lat", 0.0D);
//                    double lng = data.getDoubleExtra("lng", 0.0D);
//                    String poi = data.getStringExtra("poi");
//                    String thumb = data.getStringExtra("thumb");
//                    this.mExtensionClickListener.onLocationResult(lat, lng, poi, Uri.parse(thumb));
//                }
            }

            pluginModule.onActivityResult(reqCode, resultCode, data);
        }

    }

    public void startActivityForPluginResult(Intent intent, int requestCode, IPluginModule pluginModule) {
        if((requestCode & -256) != 0) {
            throw new IllegalArgumentException("requestCode does not over 255.");
        } else {
            int position = this.mPluginAdapter.getPluginPosition(pluginModule);
            this.mFragment.startActivityForResult(intent, (position + 1 << 8) + (requestCode & 255));
        }
    }

    private void initData() {
        this.mExtensionModuleList = EditExtensionManager.getInstance().getExtensionModules();
        this.mPluginAdapter = new PluginAdapter();
        this.mPluginAdapter.setOnPluginClickListener(new IPluginClickListener() {
            public void onClick(IPluginModule pluginModule, int position) {
                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.onPluginClicked(pluginModule, position);
                }

                pluginModule.onClick(EditExtension.this.mFragment, EditExtension.this);
            }
        });
        this.mEmotionTabAdapter = new EmoticonTabAdapter();
        this.mUserId ="1001";// RongIMClient.getInstance().getCurrentUserId();

        try {
            boolean e = this.getResources().getBoolean(this.getResources().getIdentifier("rc_extension_history", "bool", this.getContext().getPackageName()));
            ExtensionHistoryUtil.setEnableHistory(e);
            ExtensionHistoryUtil.addExceptConversationType(ConversationType.CUSTOMER_SERVICE);
        } catch (Resources.NotFoundException var2) {
            Log.e("EditExtension", "rc_extension_history not configure in rc_configuration.xml");
            var2.printStackTrace();
        }

    }

    private void initView() {
        this.setOrientation(VERTICAL);
        this.setBackgroundColor(this.getContext().getResources().getColor(R.color.rc_extension_normal));
        this.mExtensionBar = (ViewGroup)LayoutInflater.from(this.getContext()).inflate(R.layout.rc_ext_extension_bar, (ViewGroup)null);
        this.mMainBar = (LinearLayout)this.mExtensionBar.findViewById(R.id.ext_main_bar);
        this.mSwitchLayout = (ViewGroup)this.mExtensionBar.findViewById(R.id.rc_switch_layout);
        this.mContainerLayout = (ViewGroup)this.mExtensionBar.findViewById(R.id.rc_container_layout);
        this.mPluginLayout = (ViewGroup)this.mExtensionBar.findViewById(R.id.rc_plugin_layout);
        this.mEditTextLayout = LayoutInflater.from(this.getContext()).inflate(R.layout.rc_ext_input_edit_text, (ViewGroup)null);
        this.mEditTextLayout.setVisibility(VISIBLE);
        this.mContainerLayout.addView(this.mEditTextLayout);
        LayoutInflater.from(this.getContext()).inflate(R.layout.rc_ext_voice_input, this.mContainerLayout, true);
        this.mVoiceInputToggle = this.mContainerLayout.findViewById(R.id.rc_audio_input_toggle);
        this.mVoiceInputToggle.setVisibility(GONE);
        this.mEditText = (EditText)this.mExtensionBar.findViewById(R.id.rc_edit_text);
        this.mSendToggle = (FrameLayout)this.mExtensionBar.findViewById(R.id.rc_send_toggle);
        this.mPluginToggle = (ImageView)this.mExtensionBar.findViewById(R.id.rc_plugin_toggle);
        this.mEditText.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(0 == event.getAction()) {
                    if(EditExtension.this.mExtensionClickListener != null) {
                        EditExtension.this.mExtensionClickListener.onEditTextClick(EditExtension.this.mEditText);
                    }

                    EditExtension.this.showInputKeyBoard();
                    EditExtension.this.mContainerLayout.setSelected(true);
                    EditExtension.this.hidePluginBoard();
                    EditExtension.this.hideEmoticonBoard();
                }

                return false;
            }
        });
        this.mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !TextUtils.isEmpty(EditExtension.this.mEditText.getText())) {
                    EditExtension.this.mSendToggle.setVisibility(VISIBLE);
                    EditExtension.this.mPluginLayout.setVisibility(GONE);
                }

            }
        });
        this.mEditText.addTextChangedListener(new TextWatcher() {
            private int start;
            private int count;

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.beforeTextChanged(s, start, count, after);
                }

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                this.start = start;
                this.count = count;
                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.onTextChanged(s, start, before, count);
                }

                if(EditExtension.this.mVoiceInputToggle.getVisibility() == VISIBLE) {
                    EditExtension.this.mSendToggle.setVisibility(GONE);
                    EditExtension.this.mPluginLayout.setVisibility(VISIBLE);
                } else if(s != null && s.length() != 0) {
                    EditExtension.this.mSendToggle.setVisibility(VISIBLE);
                    EditExtension.this.mPluginLayout.setVisibility(GONE);
                } else {
                    EditExtension.this.mSendToggle.setVisibility(GONE);
                    EditExtension.this.mPluginLayout.setVisibility(VISIBLE);
                }

            }

            public void afterTextChanged(Editable s) {
                if(AndroidEmoji.isEmoji(s.subSequence(this.start, this.start + this.count).toString())) {
                    EditExtension.this.mEditText.removeTextChangedListener(this);
                    EditExtension.this.mEditText.setText(AndroidEmoji.ensure(s.toString()), TextView.BufferType.SPANNABLE);
                    EditExtension.this.mEditText.setSelection(this.start + this.count);
                    EditExtension.this.mEditText.addTextChangedListener(this);
                }

                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.afterTextChanged(s);
                }

            }
        });
        this.mEditText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return EditExtension.this.mExtensionClickListener != null && EditExtension.this.mExtensionClickListener.onKey(EditExtension.this.mEditText, keyCode, event);
            }
        });
        this.mVoiceToggle = (ImageView)this.mExtensionBar.findViewById(R.id.rc_voice_toggle);
        this.mVoiceToggleClickListener = new OnClickListener() {
            public void onClick(View v) {
                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.onSwitchToggleClick(v, EditExtension.this.mContainerLayout);
                }

                if(EditExtension.this.mVoiceInputToggle.getVisibility() == GONE) {
                    EditExtension.this.mEditTextLayout.setVisibility(GONE);
                    EditExtension.this.mSendToggle.setVisibility(GONE);
                    EditExtension.this.mPluginLayout.setVisibility(VISIBLE);
                    EditExtension.this.hideInputKeyBoard();
                    EditExtension.this.showVoiceInputToggle();
                    EditExtension.this.mContainerLayout.setClickable(true);
                    EditExtension.this.mContainerLayout.setSelected(false);
                } else {
                    EditExtension.this.mEditTextLayout.setVisibility(VISIBLE);
                    EditExtension.this.hideVoiceInputToggle();
                    EditExtension.this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
                    if(EditExtension.this.mEditText.getText().length() > 0) {
                        EditExtension.this.mSendToggle.setVisibility(VISIBLE);
                        EditExtension.this.mPluginLayout.setVisibility(GONE);
                    } else {
                        EditExtension.this.mSendToggle.setVisibility(GONE);
                        EditExtension.this.mPluginLayout.setVisibility(VISIBLE);
                    }

                    EditExtension.this.showInputKeyBoard();
                    EditExtension.this.mContainerLayout.setSelected(true);
                }

                EditExtension.this.hidePluginBoard();
                EditExtension.this.hideEmoticonBoard();
            }
        };
        this.mVoiceToggle.setOnClickListener(this.mVoiceToggleClickListener);
        this.mVoiceInputToggle.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.onVoiceInputToggleTouch(v, event);
                }

                return false;
            }
        });
        this.mSendToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String text = EditExtension.this.mEditText.getText().toString();
                EditExtension.this.mEditText.getText().clear();
                EditExtension.this.mEditText.setText("");
                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.onSendToggleClick(v, text);
                }

            }
        });
        this.mPluginToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.onPluginToggleClick(v, EditExtension.this);
                }

                EditExtension.this.setPluginBoard();
            }
        });
        this.mEmoticonToggle = (ImageView)this.mExtensionBar.findViewById(R.id.rc_emoticon_toggle);
        this.mEmoticonToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if(EditExtension.this.mExtensionClickListener != null) {
                    EditExtension.this.mExtensionClickListener.onEmoticonToggleClick(v, EditExtension.this);
                }

                if(EditExtension.this.isKeyBoardActive()) {
                    EditExtension.this.hideInputKeyBoard();
                    EditExtension.this.getHandler().postDelayed(new Runnable() {
                        public void run() {
                            EditExtension.this.setEmoticonBoard();
                        }
                    }, 200L);
                } else {
                    EditExtension.this.setEmoticonBoard();
                }

                EditExtension.this.hidePluginBoard();
            }
        });
        this.mPSMenu = (ImageView)this.mExtensionBar.findViewById(R.id.rc_switch_to_menu);
        this.mPSMenu.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EditExtension.this.setExtensionBarVisibility(8);
                EditExtension.this.setMenuVisibility(0, EditExtension.this.mInputMenuList);
            }
        });
        this.mPSDivider = this.mExtensionBar.findViewById(R.id.rc_switch_divider);
        this.addView(this.mExtensionBar);
    }

    private void hideVoiceInputToggle() {
        this.mVoiceToggle.setImageResource(R.drawable.rc_voice_toggle_selector);
        this.mVoiceInputToggle.setVisibility(GONE);
        ExtensionHistoryUtil.setExtensionBarState(this.getContext(), this.mUserId, this.mConversationType, ExtensionBarState.NORMAL);
    }

    private void showVoiceInputToggle() {
        this.mVoiceInputToggle.setVisibility(VISIBLE);
        this.mVoiceToggle.setImageResource(R.drawable.rc_keyboard_selector);
        ExtensionHistoryUtil.setExtensionBarState(this.getContext(), this.mUserId, this.mConversationType, ExtensionBarState.VOICE);
    }

    private void hideEmoticonBoard() {
        this.mEmotionTabAdapter.setVisibility(GONE);
        this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
    }

    private void setEmoticonBoard() {
        if(this.mEmotionTabAdapter.isInitialized()) {
            if(this.mEmotionTabAdapter.getVisibility() == 0) {
                this.mEmotionTabAdapter.setVisibility(8);
                this.mEmoticonToggle.setSelected(false);
                this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
                this.showInputKeyBoard();
            } else {
                this.mEmotionTabAdapter.setVisibility(0);
                this.mContainerLayout.setSelected(true);
                this.mEmoticonToggle.setSelected(true);
                this.mEmoticonToggle.setImageResource(R.drawable.rc_keyboard_selector);
            }
        } else {
            this.mEmotionTabAdapter.bindView(this);
            this.mEmotionTabAdapter.setVisibility(0);
            this.mContainerLayout.setSelected(true);
            this.mEmoticonToggle.setSelected(true);
            this.mEmoticonToggle.setImageResource(R.drawable.rc_keyboard_selector);
        }

        if(!TextUtils.isEmpty(this.mEditText.getText())) {
            this.mSendToggle.setVisibility(VISIBLE);
            this.mPluginLayout.setVisibility(GONE);
        }

    }

    private void hidePluginBoard() {
        if(this.mPluginAdapter != null) {
            this.mPluginAdapter.setVisibility(8);
            View pager = this.mPluginAdapter.getPager();
            this.mPluginAdapter.removePager(pager);
        }

    }

    private void setPluginBoard() {
        if(this.mPluginAdapter.isInitialized()) {
            if(this.mPluginAdapter.getVisibility() == 0) {
                View pager = this.mPluginAdapter.getPager();
                if(pager != null) {
                    pager.setVisibility(pager.getVisibility() == GONE?VISIBLE:GONE);
                } else {
                    this.mPluginAdapter.setVisibility(8);
                    this.mContainerLayout.setSelected(true);
                    this.showInputKeyBoard();
                }
            } else {
                this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
                if(this.isKeyBoardActive()) {
                    this.getHandler().postDelayed(new Runnable() {
                        public void run() {
                            EditExtension.this.mPluginAdapter.setVisibility(0);
                        }
                    }, 200L);
                } else {
                    this.mPluginAdapter.setVisibility(0);
                }

                this.hideInputKeyBoard();
                this.hideEmoticonBoard();
                this.mContainerLayout.setSelected(false);
            }
        } else {
            this.mEmoticonToggle.setImageResource(R.drawable.rc_emotion_toggle_selector);
            this.mPluginAdapter.bindView(this);
            this.mPluginAdapter.setVisibility(0);
            this.mContainerLayout.setSelected(false);
            this.hideInputKeyBoard();
            this.hideEmoticonBoard();
        }

        this.hideVoiceInputToggle();
        this.mEditTextLayout.setVisibility(VISIBLE);
    }

    private boolean isKeyBoardActive() {
        return this.isKeyBoardActive;
    }

    private void hideInputKeyBoard() {
        InputMethodManager imm = (InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.mEditText.getWindowToken(), 0);
        this.mEditText.clearFocus();
        this.isKeyBoardActive = false;
    }

    private void showInputKeyBoard() {
        this.mEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager)this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this.mEditText, 0);
        this.mEmoticonToggle.setSelected(false);
        this.isKeyBoardActive = true;
    }

    private void setSCE() {
        this.mSwitchLayout.setVisibility(VISIBLE);
        if(this.mSendToggle.getVisibility() == VISIBLE) {
            this.mPluginLayout.setVisibility(GONE);
        } else {
            this.mPluginLayout.setVisibility(VISIBLE);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mSwitchLayout);
        this.mMainBar.addView(this.mContainerLayout);
        this.mMainBar.addView(this.mPluginLayout);
    }

    private void setSC() {
        this.mSwitchLayout.setVisibility(VISIBLE);
        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mSwitchLayout);
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void setCE() {
        if(this.mSendToggle.getVisibility() == VISIBLE) {
            this.mPluginLayout.setVisibility(GONE);
        } else {
            this.mPluginLayout.setVisibility(VISIBLE);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mContainerLayout);
        this.mMainBar.addView(this.mPluginLayout);
    }

    private void setEC() {
        if(this.mSendToggle.getVisibility() == VISIBLE) {
            this.mPluginLayout.setVisibility(GONE);
        } else {
            this.mPluginLayout.setVisibility(VISIBLE);
        }

        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mPluginLayout);
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void setC() {
        this.mMainBar.removeAllViews();
        this.mMainBar.addView(this.mContainerLayout);
    }

    private void initPanelStyle() {
        ExtensionBarState state = ExtensionHistoryUtil.getExtensionBarState(this.getContext(), this.mUserId, this.mConversationType);
        if(state == ExtensionBarState.NORMAL) {
            this.mVoiceToggle.setImageResource(R.drawable.rc_voice_toggle_selector);
            this.mEditTextLayout.setVisibility(VISIBLE);
            this.mVoiceInputToggle.setVisibility(GONE);
        } else {
            this.mVoiceToggle.setImageResource(R.drawable.rc_keyboard_selector);
            this.mEditTextLayout.setVisibility(GONE);
            this.mVoiceInputToggle.setVisibility(VISIBLE);
            this.mSendToggle.setVisibility(GONE);
            this.mPluginLayout.setVisibility(VISIBLE);
        }

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(this.originalTop != 0) {
            if(this.originalTop > t) {
                if(this.originalBottom > b && this.mExtensionClickListener != null && this.collapsed) {
                    this.collapsed = false;
                    this.mExtensionClickListener.onExtensionExpanded(this.originalBottom - t);
                } else if(this.collapsed && this.mExtensionClickListener != null) {
                    this.collapsed = false;
                    this.mExtensionClickListener.onExtensionExpanded(b - t);
                }
            } else if(!this.collapsed && this.mExtensionClickListener != null) {
                this.collapsed = true;
                this.mExtensionClickListener.onExtensionCollapsed();
            }
        }

        if(this.originalTop == 0) {
            this.originalTop = t;
            this.originalBottom = b;
        }

    }
}

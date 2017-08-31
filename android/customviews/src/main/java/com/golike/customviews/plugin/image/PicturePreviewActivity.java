package com.golike.customviews.plugin.image;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import com.golike.customviews.R;
import com.golike.customviews.photoview.PhotoView;
import com.golike.customviews.photoview.PhotoViewAttacher;
import com.golike.customviews.plugin.image.AlbumBitmapCacheHelper.ILoadImageCallback;
import com.golike.customviews.plugin.image.PictureSelectorActivity.PicItemHolder;
import com.golike.customviews.plugin.image.PictureSelectorActivity.PicItem;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Created by admin on 2017/8/10.
 */

public class PicturePreviewActivity extends Activity {
    public static final int RESULT_SEND = 1;
    private TextView mIndexTotal;
    private View mWholeView;
    private View mToolbarTop;
    private View mToolbarBottom;
    private ImageButton mBtnBack;
    private Button mBtnSend;
    private PicturePreviewActivity.CheckButton mUseOrigin;
    private PicturePreviewActivity.CheckButton mSelectBox;
    private HackyViewPager mViewPager;
    private ArrayList<PictureSelectorActivity.PicItem> mItemList;
    private int mCurrentIndex;
    private boolean mFullScreen;

    public PicturePreviewActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.setContentView(R.layout.rc_picprev_activity);
        if(savedInstanceState != null) {
            this.mItemList = savedInstanceState.getParcelableArrayList("ItemList");
        }

        this.initView();
        this.mUseOrigin.setChecked(this.getIntent().getBooleanExtra("sendOrigin", false));
        this.mCurrentIndex = this.getIntent().getIntExtra("index", 0);
        this.mItemList = PicItemHolder.itemList;
        this.mIndexTotal.setText(String.format("%d/%d", new Object[]{Integer.valueOf(this.mCurrentIndex + 1), Integer.valueOf(this.mItemList.size())}));
        int result;
        if(Build.VERSION.SDK_INT >= 11) {
            this.mWholeView.setSystemUiVisibility(1024);
            result = getSmartBarHeight(this);
            if(result > 0) {
                RelativeLayout.LayoutParams resourceId = (RelativeLayout.LayoutParams)this.mToolbarBottom.getLayoutParams();
                resourceId.setMargins(0, 0, 0, result);
                this.mToolbarBottom.setLayoutParams(resourceId);
            }
        }

        result = 0;
        int resourceId1 = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId1 > 0) {
            result = this.getResources().getDimensionPixelSize(resourceId1);
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(this.mToolbarTop.getLayoutParams());
        lp.setMargins(0, result, 0, 0);
        this.mToolbarTop.setLayoutParams(lp);
        this.mBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("sendOrigin", PicturePreviewActivity.this.mUseOrigin.getChecked());
                PicturePreviewActivity.this.setResult(-1, intent);
                PicturePreviewActivity.this.finish();
            }
        });
        this.mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent data = new Intent();
                ArrayList list = new ArrayList();
                Iterator i$ = PicturePreviewActivity.this.mItemList.iterator();

                while(i$.hasNext()) {
                    PicItem item = (PicItem)i$.next();
                    if(item.selected) {
                        list.add(Uri.parse("file://" + item.uri));
                    }
                }

                if(list.size() == 0) {
                    PicturePreviewActivity.this.mSelectBox.setChecked(true);
                    list.add(Uri.parse("file://" + ((PicItem)PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex)).uri));
                }

                data.putExtra("sendOrigin", PicturePreviewActivity.this.mUseOrigin.getChecked());
                data.putExtra("android.intent.extra.RETURN_RESULT", list);
                PicturePreviewActivity.this.setResult(1, data);
                PicturePreviewActivity.this.finish();
            }
        });
        this.mUseOrigin.setText(R.string.rc_picprev_origin);
        this.mUseOrigin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PicturePreviewActivity.this.mUseOrigin.setChecked(!PicturePreviewActivity.this.mUseOrigin.getChecked());
                if(PicturePreviewActivity.this.mUseOrigin.getChecked() && PicturePreviewActivity.this.getTotalSelectedNum() == 0) {
                    PicturePreviewActivity.this.mSelectBox.setChecked(!PicturePreviewActivity.this.mSelectBox.getChecked());
                    ((PicItem)PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex)).selected = PicturePreviewActivity.this.mSelectBox.getChecked();
                    PicturePreviewActivity.this.updateToolbar();
                }

            }
        });
        this.mSelectBox.setText(R.string.rc_picprev_select);
        this.mSelectBox.setChecked(((PicItem)this.mItemList.get(this.mCurrentIndex)).selected);
        this.mSelectBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!PicturePreviewActivity.this.mSelectBox.getChecked() && PicturePreviewActivity.this.getTotalSelectedNum() == 9) {
                    Toast.makeText(PicturePreviewActivity.this, R.string.rc_picsel_selected_max, Toast.LENGTH_SHORT).show();
                } else {
                    PicturePreviewActivity.this.mSelectBox.setChecked(!PicturePreviewActivity.this.mSelectBox.getChecked());
                    ((PicItem)PicturePreviewActivity.this.mItemList.get(PicturePreviewActivity.this.mCurrentIndex)).selected = PicturePreviewActivity.this.mSelectBox.getChecked();
                    PicturePreviewActivity.this.updateToolbar();
                }
            }
        });
        this.mViewPager.setAdapter(new PicturePreviewActivity.PreviewAdapter());
        this.mViewPager.setCurrentItem(this.mCurrentIndex);
        this.mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                PicturePreviewActivity.this.mCurrentIndex = position;
                PicturePreviewActivity.this.mIndexTotal.setText(String.format("%d/%d", new Object[]{Integer.valueOf(position + 1), Integer.valueOf(PicturePreviewActivity.this.mItemList.size())}));
                PicturePreviewActivity.this.mSelectBox.setChecked(((PicItem)PicturePreviewActivity.this.mItemList.get(position)).selected);
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
        this.updateToolbar();
    }

    private void initView() {
        this.mToolbarTop = this.findViewById(R.id.toolbar_top);
        this.mIndexTotal = (TextView)this.findViewById(R.id.index_total);
        this.mBtnBack = (ImageButton)this.findViewById(R.id.back);
        this.mBtnSend = (Button)this.findViewById(R.id.send);
        this.mWholeView = this.findViewById(R.id.whole_layout);
        this.mViewPager = (HackyViewPager)this.findViewById(R.id.viewpager);
        this.mToolbarBottom = this.findViewById(R.id.toolbar_bottom);
        this.mUseOrigin = new PicturePreviewActivity.CheckButton(this.findViewById(R.id.origin_check), R.drawable.rc_origin_check_nor, R.drawable.rc_origin_check_sel);
        this.mSelectBox = new PicturePreviewActivity.CheckButton(this.findViewById(R.id.select_check), R.drawable.rc_select_check_nor, R.drawable.rc_select_check_sel);
    }

    protected void onResume() {
        super.onResume();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 4) {
            Intent intent = new Intent();
            intent.putExtra("sendOrigin", this.mUseOrigin.getChecked());
            this.setResult(-1, intent);
        }

        return super.onKeyDown(keyCode, event);
    }

    private int getTotalSelectedNum() {
        int sum = 0;

        for(int i = 0; i < this.mItemList.size(); ++i) {
            if(((PicItem)this.mItemList.get(i)).selected) {
                ++sum;
            }
        }

        return sum;
    }

    private String getTotalSelectedSize() {
        float size = 0.0F;

        for(int totalSize = 0; totalSize < this.mItemList.size(); ++totalSize) {
            if(((PicItem)this.mItemList.get(totalSize)).selected) {
                File file = new File(((PicItem)this.mItemList.get(totalSize)).uri);
                size += (float)(file.length() / 1024L);
            }
        }

        String var4;
        if(size < 1024.0F) {
            var4 = String.format("%.0fK", new Object[]{Float.valueOf(size)});
        } else {
            var4 = String.format("%.1fM", new Object[]{Float.valueOf(size / 1024.0F)});
        }

        return var4;
    }

    private void updateToolbar() {
        int selNum = this.getTotalSelectedNum();
        if(this.mItemList.size() == 1 && selNum == 0) {
            this.mBtnSend.setText(R.string.rc_picsel_toolbar_send);
            this.mUseOrigin.setText(R.string.rc_picprev_origin);
        } else {
            if(selNum == 0) {
                this.mBtnSend.setText(R.string.rc_picsel_toolbar_send);
                this.mUseOrigin.setText(R.string.rc_picprev_origin);
            } else if(selNum <= 9) {
                this.mBtnSend.setText(String.format(this.getResources().getString(R.string.rc_picsel_toolbar_send_num), new Object[]{Integer.valueOf(selNum)}));
                this.mUseOrigin.setText(String.format(this.getResources().getString(R.string.rc_picprev_origin_size), new Object[]{this.getTotalSelectedSize()}));
            }

        }
    }

    @TargetApi(11)
    public static int getSmartBarHeight(Context context) {
        try {
            Class e = Class.forName("com.android.internal.R$dimen");
            Object obj = e.newInstance();
            Field field = e.getField("mz_action_button_min_height");
            int height = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(height);
        } catch (Exception var5) {
            var5.printStackTrace();
            return 0;
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        if(this.mItemList != null && this.mItemList.size() > 0) {
            outState.putParcelableArrayList("ItemList", this.mItemList);
        }

        super.onSaveInstanceState(outState);
    }

    private class CheckButton {
        private View rootView;
        private ImageView image;
        private TextView text;
        private boolean checked = false;
        private int nor_resId;
        private int sel_resId;

        public CheckButton(View root, @DrawableRes int norId, @DrawableRes int selId) {
            this.rootView = root;
            this.image = (ImageView)root.findViewById(R.id.image);
            this.text = (TextView)root.findViewById(R.id.text);
            this.nor_resId = norId;
            this.sel_resId = selId;
            this.image.setImageResource(this.nor_resId);
        }

        public void setChecked(boolean check) {
            this.checked = check;
            this.image.setImageResource(this.checked?this.sel_resId:this.nor_resId);
        }

        public boolean getChecked() {
            return this.checked;
        }

        public void setText(int resId) {
            this.text.setText(resId);
        }

        public void setText(CharSequence chars) {
            this.text.setText(chars);
        }

        public void setOnClickListener(@Nullable View.OnClickListener l) {
            this.rootView.setOnClickListener(l);
        }
    }

    private class PreviewAdapter extends PagerAdapter {
        private PreviewAdapter() {
        }

        public int getCount() {
            return PicturePreviewActivity.this.mItemList.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            final PhotoView photoView = new PhotoView(container.getContext());
            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                public void onViewTap(View view, float x, float y) {
                    PicturePreviewActivity.this.mFullScreen = !PicturePreviewActivity.this.mFullScreen;
                    View decorView;
                    byte uiOptions;
                    if(PicturePreviewActivity.this.mFullScreen) {
                        if(Build.VERSION.SDK_INT < 16) {
                            PicturePreviewActivity.this.getWindow().setFlags(1024, 1024);
                        } else {
                            decorView = PicturePreviewActivity.this.getWindow().getDecorView();
                            uiOptions = 4;
                            decorView.setSystemUiVisibility(uiOptions);
                        }

                        PicturePreviewActivity.this.mToolbarTop.setVisibility(INVISIBLE);
                        PicturePreviewActivity.this.mToolbarBottom.setVisibility(INVISIBLE);
                    } else {
                        if(Build.VERSION.SDK_INT < 16) {
                            PicturePreviewActivity.this.getWindow().setFlags(1024, 1024);
                        } else {
                            decorView = PicturePreviewActivity.this.getWindow().getDecorView();
                            uiOptions = 0;
                            decorView.setSystemUiVisibility(uiOptions);
                        }

                        PicturePreviewActivity.this.mToolbarTop.setVisibility(VISIBLE);
                        PicturePreviewActivity.this.mToolbarBottom.setVisibility(VISIBLE);
                    }

                }
            });
            container.addView(photoView, -1, -1);
            String path = ((PicItem)PicturePreviewActivity.this.mItemList.get(position)).uri;
            AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(path);
            AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
            Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(path, 0, 0, new ILoadImageCallback() {
                public void onLoadImageCallBack(Bitmap bitmap, String p, Object... objects) {
                    if(bitmap != null) {
                        photoView.setImageBitmap(bitmap);
                    }
                }
            }, new Object[]{Integer.valueOf(position)});
            if(bitmap != null) {
                photoView.setImageBitmap(bitmap);
            } else {
                photoView.setImageResource(R.drawable.rc_grid_image_default);
            }

          return null;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }
}


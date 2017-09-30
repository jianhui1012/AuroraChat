package com.golike.customviews.activity;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.golike.customviews.R;
import com.golike.customviews.model.Conversation.ConversationType;
import com.golike.customviews.model.ImageMessage;
import com.golike.customviews.model.Message;
import com.golike.customviews.model.RongCommonDefine.GetMessageDirection;
import com.golike.customviews.photoview.PhotoView;
import com.golike.customviews.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.golike.customviews.plugin.image.HackyViewPager;
import com.golike.customviews.utilities.FileUtils;
import com.golike.customviews.utilities.OptionsPopupDialog;
import com.golike.customviews.utilities.OptionsPopupDialog.OnOptionsItemClickedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by admin on 2017/9/6.
 */

public class PicturePagerActivity extends Activity implements View.OnLongClickListener {
    private static final String TAG = "PicturePagerActivity";
    private static final int IMAGE_MESSAGE_COUNT = 10;
    private HackyViewPager mViewPager;
    private ImageMessage mCurrentImageMessage;
    private ConversationType mConversationType;
    private int mCurrentMessageId;
    private String mTargetId = null;
    private int mCurrentIndex = 0;
    private PicturePagerActivity.ImageAdapter mImageAdapter;
    private boolean isFirstTime = false;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            Log.i("PicturePagerActivity", "onPageSelected. position:" + position);
            PicturePagerActivity.this.mCurrentIndex = position;
            View view = PicturePagerActivity.this.mViewPager.findViewById(position);
            if(view != null) {
                PicturePagerActivity.this.mImageAdapter.updatePhotoView(position, view);
            }
        }

        public void onPageScrollStateChanged(int state) {
        }
    };

    public PicturePagerActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.ee_fr_photo);
        Message currentMessage = this.getIntent().getParcelableExtra("message");
        this.mCurrentImageMessage = (ImageMessage)currentMessage.getContent();
        this.mConversationType = currentMessage.getConversationType();
        this.mCurrentMessageId = currentMessage.getMessageId();
        this.mTargetId = currentMessage.getTargetId();
        this.mViewPager = (HackyViewPager)this.findViewById(R.id.viewpager);
        this.mViewPager.setOnPageChangeListener(this.mPageChangeListener);
        this.mImageAdapter = new PicturePagerActivity.ImageAdapter();
        this.isFirstTime = true;
        this.getConversationImageUris(this.mCurrentMessageId, GetMessageDirection.FRONT);
        this.getConversationImageUris(this.mCurrentMessageId, GetMessageDirection.BEHIND);
    }

    private void getConversationImageUris(int mesageId, final GetMessageDirection direction) {
        ArrayList lists = new ArrayList();
        if(direction.equals(GetMessageDirection.FRONT) && PicturePagerActivity.this.isFirstTime) {
            lists.add(PicturePagerActivity.this.new ImageInfo(PicturePagerActivity.this.mCurrentMessageId, PicturePagerActivity.this.mCurrentImageMessage.getThumUri(), PicturePagerActivity.this.mCurrentImageMessage.getLocalUri() == null?PicturePagerActivity.this.mCurrentImageMessage.getRemoteUri():PicturePagerActivity.this.mCurrentImageMessage.getLocalUri()));
            PicturePagerActivity.this.mImageAdapter.addData(lists, direction.equals(GetMessageDirection.FRONT));
            PicturePagerActivity.this.mViewPager.setAdapter(PicturePagerActivity.this.mImageAdapter);
            PicturePagerActivity.this.isFirstTime = false;
            PicturePagerActivity.this.mViewPager.setCurrentItem(lists.size() - 1);
            PicturePagerActivity.this.mCurrentIndex = lists.size() - 1;
        } else if(lists.size() > 0) {
            PicturePagerActivity.this.mImageAdapter.addData(lists, direction.equals(GetMessageDirection.FRONT));
            PicturePagerActivity.this.mImageAdapter.notifyDataSetChanged();
            if(direction.equals(GetMessageDirection.FRONT)) {
                PicturePagerActivity.this.mViewPager.setCurrentItem(lists.size());
                PicturePagerActivity.this.mCurrentIndex = lists.size();
            }
        }
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public boolean onPictureLongClick(View v, Uri thumbUri, Uri largeImageUri) {
        return false;
    }

    public boolean onLongClick(View v) {
        PicturePagerActivity.ImageInfo imageInfo = this.mImageAdapter.getImageInfo(this.mCurrentIndex);
        if(imageInfo != null) {
            Uri thumbUri = imageInfo.getThumbUri();
            Uri largeImageUri = imageInfo.getLargeImageUri();
            if(this.onPictureLongClick(v, thumbUri, largeImageUri)) {
                return true;
            }

            if(largeImageUri == null) {
                return false;
            }

            final File file;
            if(!largeImageUri.getScheme().startsWith("http") && !largeImageUri.getScheme().startsWith("https")) {
                file = new File(largeImageUri.getPath());
            }else{
                file=null;//Glide.with(PicturePagerActivity.this).load(largeImageUri).downloadOnly(100,100);
            }

            String[] items = new String[]{this.getString(R.string.rc_save_picture)};
            OptionsPopupDialog.newInstance(this, items).setOptionsPopupDialogListener(new OnOptionsItemClickedListener() {
                public void onOptionsItemClicked(int which) {
                    if(which == 0) {
                        File path = Environment.getExternalStorageDirectory();
                        String defaultPath = PicturePagerActivity.this.getString(R.string.rc_image_default_saved_path);
                        File dir = new File(path, defaultPath);
                        if(!dir.exists()) {
                            dir.mkdirs();
                        }

                        if(file != null && file.exists()) {
                            String name = System.currentTimeMillis() + ".jpg";
                            FileUtils.copyFile(file, dir.getPath() + File.separator, name);
                            MediaScannerConnection.scanFile(PicturePagerActivity.this, new String[]{dir.getPath() + File.separator + name}, null, null);
                            Toast.makeText(PicturePagerActivity.this, String.format(PicturePagerActivity.this.getString(R.string.rc_save_picture_at), new Object[]{dir.getPath() + File.separator + name}),  Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PicturePagerActivity.this, PicturePagerActivity.this.getString(R.string.rc_src_file_not_found), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }).show();
        }

        return true;
    }

    private class ImageInfo {
        private int messageId;
        private Uri thumbUri;
        private Uri largeImageUri;

        ImageInfo(int messageId, Uri thumbnail, Uri largeImageUri) {
            this.messageId = messageId;
            this.thumbUri = thumbnail;
            this.largeImageUri = largeImageUri;
        }

        public int getMessageId() {
            return this.messageId;
        }

        public Uri getLargeImageUri() {
            return this.largeImageUri;
        }

        public Uri getThumbUri() {
            return this.thumbUri;
        }
    }

    private class ImageAdapter extends PagerAdapter {
        private ArrayList<PicturePagerActivity.ImageInfo> mImageList;
        private Context mContext;


        private ImageAdapter() {
            this.mImageList = new ArrayList();
        }

        private View newView(Context context, PicturePagerActivity.ImageInfo imageInfo) {
            this.mContext=context;
            View result = LayoutInflater.from(context).inflate(R.layout.ee_fr_image,  null);
            PicturePagerActivity.ImageAdapter.ViewHolder holder = new PicturePagerActivity.ImageAdapter.ViewHolder();
            holder.progressBar = (ProgressBar)result.findViewById(R.id.ee_progress);
            holder.progressText = (TextView)result.findViewById(R.id.ee_txt);
            holder.photoView = (PhotoView)result.findViewById(R.id.ee_photoView);
            holder.photoView.setOnLongClickListener(PicturePagerActivity.this);
            holder.photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                public void onPhotoTap(View view, float x, float y) {
                    PicturePagerActivity.this.finish();
                }

                public void onOutsidePhotoTap() {
                }
            });
            result.setTag(holder);
            return result;
        }

        public void addData(ArrayList<PicturePagerActivity.ImageInfo> newImages, boolean direction) {
            if(newImages != null && newImages.size() != 0) {
                if(this.mImageList.size() == 0) {
                    this.mImageList.addAll(newImages);
                } else if(direction && !PicturePagerActivity.this.isFirstTime && !this.isDuplicate((newImages.get(0)).getMessageId())) {
                    ArrayList temp = new ArrayList();
                    temp.addAll(this.mImageList);
                    this.mImageList.clear();
                    this.mImageList.addAll(newImages);
                    this.mImageList.addAll(this.mImageList.size(), temp);
                } else if(!PicturePagerActivity.this.isFirstTime && !this.isDuplicate((newImages.get(0)).getMessageId())) {
                    this.mImageList.addAll(this.mImageList.size(), newImages);
                }

            }
        }

        private boolean isDuplicate(int messageId) {
            Iterator i$ = this.mImageList.iterator();

            PicturePagerActivity.ImageInfo info;
            do {
                if(!i$.hasNext()) {
                    return false;
                }

                info = (PicturePagerActivity.ImageInfo)i$.next();
            } while(info.getMessageId() != messageId);

            return true;
        }

        public PicturePagerActivity.ImageInfo getItem(int index) {
            return  this.mImageList.get(index);
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public int getCount() {
            return this.mImageList.size();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            Log.i("PicturePagerActivity", "instantiateItem.position:" + position);
            View imageView = this.newView(container.getContext(), this.mImageList.get(position));
            this.updatePhotoView(position, imageView);
            imageView.setId(position);
            container.addView(imageView);
            return imageView;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.i("PicturePagerActivity", "destroyItem.position:" + position);
            PicturePagerActivity.ImageAdapter.ViewHolder holder = (PicturePagerActivity.ImageAdapter.ViewHolder)container.findViewById(position).getTag();
            holder.photoView.setImageURI(null);
            container.removeView((View)object);
        }

        private void updatePhotoView(int position, View view) {
            final PicturePagerActivity.ImageAdapter.ViewHolder holder = (PicturePagerActivity.ImageAdapter.ViewHolder)view.getTag();
            Uri originalUri = (this.mImageList.get(position)).getLargeImageUri();
            Uri thumbUri = (this.mImageList.get(position)).getThumbUri();
            if(originalUri != null && thumbUri != null) {
                    Glide.with(mContext)
                            .load(originalUri.toString())
                            .thumbnail(0.1f).fitCenter()
                            .into(holder.photoView);
            } else {
                Log.e("PicturePagerActivity", "large uri and thumbnail uri of the image should not be null.");
            }
        }

        public PicturePagerActivity.ImageInfo getImageInfo(int position) {
            return  this.mImageList.get(position);
        }

        public class ViewHolder {
            ProgressBar progressBar;
            TextView progressText;
            PhotoView photoView;

            public ViewHolder() {
            }
        }
    }
}


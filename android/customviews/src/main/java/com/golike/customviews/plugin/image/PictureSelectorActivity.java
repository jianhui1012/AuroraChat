package com.golike.customviews.plugin.image;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.golike.customviews.R;
import com.golike.customviews.utilities.ParcelUtils;
import com.golike.customviews.utilities.PermissionCheckUtil;
import com.golike.customviews.utilities.RongUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

/**
 * Created by admin on 2017/8/10.
 */

public class PictureSelectorActivity extends Activity {
    public static final int REQUEST_PREVIEW = 0;
    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    private GridView mGridView;
    private ImageButton mBtnBack;
    private Button mBtnSend;
    private PictureSelectorActivity.PicTypeBtn mPicType;
    private PictureSelectorActivity.PreviewBtn mPreviewBtn;
    private View mCatalogView;
    private ListView mCatalogListView;
    private List<PicItem> mAllItemList;
    private Map<String, List<PicItem>> mItemMap;
    private List<String> mCatalogList;
    private String mCurrentCatalog = "";
    private Uri mTakePictureUri;
    private boolean mSendOrigin = false;
    private int perWidth;

    public PictureSelectorActivity() {
    }

    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.rc_picsel_activity);
        if(savedInstanceState != null) {
            PictureSelectorActivity.PicItemHolder.itemList = savedInstanceState.getParcelableArrayList("ItemList");
        }

        this.mGridView = (GridView)this.findViewById(R.id.gridlist);
        this.mBtnBack = (ImageButton)this.findViewById(R.id.back);
        this.mBtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PictureSelectorActivity.this.finish();
            }
        });
        this.mBtnSend = (Button)this.findViewById(R.id.send);
        this.mPicType = (PictureSelectorActivity.PicTypeBtn)this.findViewById(R.id.pic_type);
        this.mPicType.init(this);
        this.mPicType.setEnabled(false);
        this.mPreviewBtn = (PictureSelectorActivity.PreviewBtn)this.findViewById(R.id.preview);
        this.mPreviewBtn.init(this);
        this.mPreviewBtn.setEnabled(false);
        this.mCatalogView = this.findViewById(R.id.catalog_window);
        this.mCatalogListView = (ListView)this.findViewById(R.id.catalog_listview);
        String[] permissions = new String[]{"android.permission.READ_EXTERNAL_STORAGE"};
        if(!PermissionCheckUtil.checkPermissions(this, permissions)) {
            PermissionCheckUtil.requestPermissions(this, permissions, 100);
        } else {
            this.initView();
        }
    }

    private void initView() {
        this.updatePictureItems();
        this.mGridView.setAdapter(new PictureSelectorActivity.GridViewAdapter());
        this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    PictureSelectorActivity.PicItemHolder.itemList = new ArrayList();
                    if(PictureSelectorActivity.this.mCurrentCatalog.isEmpty()) {
                        PictureSelectorActivity.PicItemHolder.itemList.addAll(PictureSelectorActivity.this.mAllItemList);
                    } else {
                        PictureSelectorActivity.PicItemHolder.itemList.addAll((Collection)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCurrentCatalog));
                    }

                    Intent intent = new Intent(PictureSelectorActivity.this, PicturePreviewActivity.class);
                    intent.putExtra("index", position - 1);
                    intent.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
                    PictureSelectorActivity.this.startActivityForResult(intent, 0);
                }
            }
        });
        this.mBtnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent data = new Intent();
                ArrayList list = new ArrayList();
                Iterator i$ = PictureSelectorActivity.this.mItemMap.keySet().iterator();

                while(i$.hasNext()) {
                    String key = (String)i$.next();
                    Iterator i$1 = ((List)PictureSelectorActivity.this.mItemMap.get(key)).iterator();

                    while(i$1.hasNext()) {
                        PictureSelectorActivity.PicItem item = (PictureSelectorActivity.PicItem)i$1.next();
                        if(item.selected) {
                            list.add(Uri.parse("file://" + item.uri));
                        }
                    }
                }

                data.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
                data.putExtra("android.intent.extra.RETURN_RESULT", list);
                PictureSelectorActivity.this.setResult(-1, data);
                PictureSelectorActivity.this.finish();
            }
        });
        this.mPicType.setEnabled(true);
        this.mPicType.setTextColor(this.getResources().getColor(R.color.rc_picsel_toolbar_send_text_normal));
        this.mPicType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PictureSelectorActivity.this.mCatalogView.setVisibility(View.VISIBLE);
            }
        });
        this.mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PictureSelectorActivity.PicItemHolder.itemList = new ArrayList();
                Iterator intent = PictureSelectorActivity.this.mItemMap.keySet().iterator();

                while(intent.hasNext()) {
                    String key = (String)intent.next();
                    Iterator i$ = ((List)PictureSelectorActivity.this.mItemMap.get(key)).iterator();

                    while(i$.hasNext()) {
                        PictureSelectorActivity.PicItem item = (PictureSelectorActivity.PicItem)i$.next();
                        if(item.selected) {
                            PictureSelectorActivity.PicItemHolder.itemList.add(item);
                        }
                    }
                }

                Intent intent1 = new Intent(PictureSelectorActivity.this, PicturePreviewActivity.class);
                intent1.putExtra("sendOrigin", PictureSelectorActivity.this.mSendOrigin);
                PictureSelectorActivity.this.startActivityForResult(intent1, 0);
            }
        });
        this.mCatalogView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == 1 && PictureSelectorActivity.this.mCatalogView.getVisibility() == View.VISIBLE) {
                    PictureSelectorActivity.this.mCatalogView.setVisibility(View.GONE);
                }

                return true;
            }
        });
        this.mCatalogListView.setAdapter(new PictureSelectorActivity.CatalogAdapter());
        this.mCatalogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String catalog;
                if(position == 0) {
                    catalog = "";
                } else {
                    catalog = (String)PictureSelectorActivity.this.mCatalogList.get(position - 1);
                }

                if(catalog.equals(PictureSelectorActivity.this.mCurrentCatalog)) {
                    PictureSelectorActivity.this.mCatalogView.setVisibility(View.GONE);
                } else {
                    PictureSelectorActivity.this.mCurrentCatalog = catalog;
                    TextView textView = (TextView)view.findViewById(R.id.name);
                    PictureSelectorActivity.this.mPicType.setText(textView.getText().toString());
                    PictureSelectorActivity.this.mCatalogView.setVisibility(View.GONE);
                    ((PictureSelectorActivity.CatalogAdapter)PictureSelectorActivity.this.mCatalogListView.getAdapter()).notifyDataSetChanged();
                    ((PictureSelectorActivity.GridViewAdapter)PictureSelectorActivity.this.mGridView.getAdapter()).notifyDataSetChanged();
                }
            }
        });
        this.perWidth = (((WindowManager)((WindowManager)this.getSystemService(WINDOW_SERVICE))).getDefaultDisplay().getWidth() - RongUtils.dip2px(4.0F)) / 3;
    }

    @TargetApi(23)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            if(Build.VERSION.SDK_INT >= 23 && this.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                this.initView();
            } else {
                Toast.makeText(this.getApplicationContext(), this.getString(R.string.rc_permission_grant_needed), Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }

        if(resultCode != 0) {
            if(resultCode == 1) {
                this.setResult(-1, data);
                this.finish();
            } else {
                switch(requestCode) {
                    case 0:
                        this.mSendOrigin = data.getBooleanExtra("sendOrigin", false);
                        ArrayList item2 = PictureSelectorActivity.PicItemHolder.itemList;
                        Iterator intent1 = item2.iterator();

                        while(intent1.hasNext()) {
                            PictureSelectorActivity.PicItem it = (PictureSelectorActivity.PicItem)intent1.next();
                            PictureSelectorActivity.PicItem item1 = this.findByUri(it.uri);
                            if(item1 != null) {
                                item1.selected = it.selected;
                            }
                        }

                        ((PictureSelectorActivity.GridViewAdapter)this.mGridView.getAdapter()).notifyDataSetChanged();
                        ((PictureSelectorActivity.CatalogAdapter)this.mCatalogListView.getAdapter()).notifyDataSetChanged();
                        this.updateToolbar();
                        break;
                    case 1:
                        if(this.mTakePictureUri != null) {
                            PictureSelectorActivity.PicItemHolder.itemList = new ArrayList();
                            PictureSelectorActivity.PicItem item = new PictureSelectorActivity.PicItem();
                            item.uri = this.mTakePictureUri.getPath();
                            PictureSelectorActivity.PicItemHolder.itemList.add(item);
                            Intent intent = new Intent(this, PicturePreviewActivity.class);
                            this.startActivityForResult(intent, 0);
                            MediaScannerConnection.scanFile(this, new String[]{this.mTakePictureUri.getPath()}, (String[])null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    PictureSelectorActivity.this.updatePictureItems();
                                }
                            });
                        }
                }

            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == 4 && this.mCatalogView != null && this.mCatalogView.getVisibility() == View.VISIBLE) {
            this.mCatalogView.setVisibility(View.GONE);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    protected void requestCamera() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if(!path.exists()) {
            path.mkdirs();
        }

        String name = System.currentTimeMillis() + ".jpg";
        File file = new File(path, name);
        this.mTakePictureUri = Uri.fromFile(file);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        List resInfoList = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Uri uri = null;

        try {
            uri = FileProvider.getUriForFile(this, this.getPackageName() + ".FileProvider", file);
        } catch (Exception var10) {
            var10.printStackTrace();
            throw new RuntimeException("Please check IMKit Manifest FileProvider config.");
        }

        Iterator i$ = resInfoList.iterator();

        while(i$.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo)i$.next();
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            this.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.putExtra("output", uri);
        this.startActivityForResult(intent, 1);
    }

    private void updatePictureItems() {
        String[] projection = new String[]{"_data", "date_added"};
        String orderBy = "datetaken DESC";
        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, (String)null, (String[])null, orderBy);
        this.mAllItemList = new ArrayList();
        this.mCatalogList = new ArrayList();
        this.mItemMap = new ArrayMap();
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    PictureSelectorActivity.PicItem item = new PictureSelectorActivity.PicItem();
                    item.uri = cursor.getString(0);
                    if(item.uri != null) {
                        this.mAllItemList.add(item);
                        int last = item.uri.lastIndexOf("/");
                        if(last != -1) {
                            String catalog;
                            if(last == 0) {
                                catalog = "/";
                            } else {
                                int itemList = item.uri.lastIndexOf("/", last - 1);
                                catalog = item.uri.substring(itemList + 1, last);
                            }

                            if(this.mItemMap.containsKey(catalog)) {
                                ((List)this.mItemMap.get(catalog)).add(item);
                            } else {
                                ArrayList itemList1 = new ArrayList();
                                itemList1.add(item);
                                this.mItemMap.put(catalog, itemList1);
                                this.mCatalogList.add(catalog);
                            }
                        }
                    }
                } while(cursor.moveToNext());
            }

            cursor.close();
        }

    }

    private int getTotalSelectedNum() {
        int sum = 0;
        Iterator i$ = this.mItemMap.keySet().iterator();

        while(i$.hasNext()) {
            String key = (String)i$.next();
            Iterator i$1 = ((List)this.mItemMap.get(key)).iterator();

            while(i$1.hasNext()) {
                PictureSelectorActivity.PicItem item = (PictureSelectorActivity.PicItem)i$1.next();
                if(item.selected) {
                    ++sum;
                }
            }
        }

        return sum;
    }

    private void updateToolbar() {
        int sum = this.getTotalSelectedNum();
        if(sum == 0) {
            this.mBtnSend.setEnabled(false);
            this.mBtnSend.setTextColor(this.getResources().getColor(R.color.rc_picsel_toolbar_send_text_disable));
            this.mBtnSend.setText(R.string.rc_picsel_toolbar_send);
            this.mPreviewBtn.setEnabled(false);
            this.mPreviewBtn.setText(R.string.rc_picsel_toolbar_preview);
        } else if(sum <= 9) {
            this.mBtnSend.setEnabled(true);
            this.mBtnSend.setTextColor(this.getResources().getColor(R.color.rc_picsel_toolbar_send_text_normal));
            this.mBtnSend.setText(String.format(this.getResources().getString(R.string.rc_picsel_toolbar_send_num), new Object[]{Integer.valueOf(sum)}));
            this.mPreviewBtn.setEnabled(true);
            this.mPreviewBtn.setText(String.format(this.getResources().getString(R.string.rc_picsel_toolbar_preview_num), new Object[]{Integer.valueOf(sum)}));
        }

    }

    private PictureSelectorActivity.PicItem getItemAt(int index) {
        int sum = 0;
        Iterator i$ = this.mItemMap.keySet().iterator();

        while(i$.hasNext()) {
            String key = (String)i$.next();

            for(Iterator i$1 = ((List)this.mItemMap.get(key)).iterator(); i$1.hasNext(); ++sum) {
                PictureSelectorActivity.PicItem item = (PictureSelectorActivity.PicItem)i$1.next();
                if(sum == index) {
                    return item;
                }
            }
        }

        return null;
    }

    private PictureSelectorActivity.PicItem getItemAt(String catalog, int index) {
        if(!this.mItemMap.containsKey(catalog)) {
            return null;
        } else {
            int sum = 0;

            for(Iterator i$ = ((List)this.mItemMap.get(catalog)).iterator(); i$.hasNext(); ++sum) {
                PictureSelectorActivity.PicItem item = (PictureSelectorActivity.PicItem)i$.next();
                if(sum == index) {
                    return item;
                }
            }

            return null;
        }
    }

    private PictureSelectorActivity.PicItem findByUri(String uri) {
        Iterator i$ = this.mItemMap.keySet().iterator();

        while(i$.hasNext()) {
            String key = (String)i$.next();
            Iterator i$1 = ((List)this.mItemMap.get(key)).iterator();

            while(i$1.hasNext()) {
                PictureSelectorActivity.PicItem item = (PictureSelectorActivity.PicItem)i$1.next();
                if(item.uri.equals(uri)) {
                    return item;
                }
            }
        }

        return null;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case 100:
                if(grantResults[0] == 0) {
                    if(permissions[0].equals("android.permission.READ_EXTERNAL_STORAGE")) {
                        this.initView();
                    } else if(permissions[0].equals("android.permission.CAMERA")) {
                        this.requestCamera();
                    }
                } else if(permissions[0].equals("android.permission.CAMERA")) {
                    Toast.makeText(this.getApplicationContext(), this.getString(R.string.rc_permission_grant_needed), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this.getApplicationContext(), this.getString(R.string.rc_permission_grant_needed), Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    protected void onSaveInstanceState(Bundle outState) {
        if(PictureSelectorActivity.PicItemHolder.itemList != null && PictureSelectorActivity.PicItemHolder.itemList.size() > 0) {
            outState.putParcelableArrayList("ItemList", PictureSelectorActivity.PicItemHolder.itemList);
        }

        super.onSaveInstanceState(outState);
    }

    protected void onDestroy() {
        PictureSelectorActivity.PicItemHolder.itemList = null;
        super.onDestroy();
    }

    public static class PicItemHolder {
        public static ArrayList<PictureSelectorActivity.PicItem> itemList;

        public PicItemHolder() {
        }
    }

    public static class SelectBox extends ImageView {
        private boolean mIsChecked;

        public SelectBox(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.setImageResource(R.drawable.rc_select_check_nor);
        }

        public void setChecked(boolean check) {
            this.mIsChecked = check;
            this.setImageResource(this.mIsChecked?R.drawable.rc_select_check_sel:R.drawable.rc_select_check_nor);
        }

        public boolean getChecked() {
            return this.mIsChecked;
        }
    }

    public static class PreviewBtn extends LinearLayout {
        private TextView mText;

        public PreviewBtn(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void init(Activity root) {
            this.mText = (TextView)root.findViewById(R.id.preview_text);
        }

        public void setText(int id) {
            this.mText.setText(id);
        }

        public void setText(String text) {
            this.mText.setText(text);
        }

        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            int color = enabled?R.color.rc_picsel_toolbar_send_text_normal:R.color.rc_picsel_toolbar_send_text_disable;
            this.mText.setTextColor(this.getResources().getColor(color));
        }

        public boolean onTouchEvent(MotionEvent event) {
            if(this.isEnabled()) {
                switch(event.getAction()) {
                    case 0:
                        this.mText.setVisibility(INVISIBLE);
                        break;
                    case 1:
                        this.mText.setVisibility(VISIBLE);
                }
            }

            return super.onTouchEvent(event);
        }
    }

    public static class PicTypeBtn extends LinearLayout {
        TextView mText;

        public PicTypeBtn(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void init(Activity root) {
            this.mText = (TextView)root.findViewById(R.id.type_text);
        }

        public void setText(String text) {
            this.mText.setText(text);
        }

        public void setTextColor(int color) {
            this.mText.setTextColor(color);
        }

        public boolean onTouchEvent(MotionEvent event) {
            if(this.isEnabled()) {
                switch(event.getAction()) {
                    case 0:
                        this.mText.setVisibility(INVISIBLE);
                        break;
                    case 1:
                        this.mText.setVisibility(VISIBLE);
                }
            }

            return super.onTouchEvent(event);
        }
    }

    public static class PicItem implements Parcelable {
        String uri;
        boolean selected;
        public static final Creator<PictureSelectorActivity.PicItem> CREATOR = new Creator() {
            public PictureSelectorActivity.PicItem createFromParcel(Parcel source) {
                return new PictureSelectorActivity.PicItem(source);
            }

            public PictureSelectorActivity.PicItem[] newArray(int size) {
                return new PictureSelectorActivity.PicItem[size];
            }
        };

        public int describeContents() {
            return 0;
        }

        public PicItem() {
        }

        public PicItem(Parcel in) {
            this.uri = ParcelUtils.readFromParcel(in);
            this.selected = ParcelUtils.readIntFromParcel(in).intValue() == 1;
        }

        public void writeToParcel(Parcel dest, int flags) {
            ParcelUtils.writeToParcel(dest, this.uri);
            ParcelUtils.writeToParcel(dest, Integer.valueOf(this.selected?1:0));
        }
    }

    private class CatalogAdapter extends BaseAdapter {
        private LayoutInflater mInflater = PictureSelectorActivity.this.getLayoutInflater();

        public CatalogAdapter() {
        }

        public int getCount() {
            return PictureSelectorActivity.this.mItemMap.size() + 1;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return (long)position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            PictureSelectorActivity.CatalogAdapter.ViewHolder holder;
            if(convertView == null) {
                view = this.mInflater.inflate(R.layout.rc_picsel_catalog_listview, parent, false);
                holder = new PictureSelectorActivity.CatalogAdapter.ViewHolder();
                holder.image = (ImageView)view.findViewById(R.id.image);
                holder.name = (TextView)view.findViewById(R.id.name);
                holder.number = (TextView)view.findViewById(R.id.number);
                holder.selected = (ImageView)view.findViewById(R.id.selected);
                view.setTag(holder);
            } else {
                holder = (PictureSelectorActivity.CatalogAdapter.ViewHolder)convertView.getTag();
            }

            String path;
            if(holder.image.getTag() != null) {
                path = (String)holder.image.getTag();
                AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(path);
            }

            int num = 0;
            boolean showSelected = false;
            String name;
            Bitmap bitmap;
            BitmapDrawable bd;
            if(position == 0) {
                if(PictureSelectorActivity.this.mItemMap.size() == 0) {
                    holder.image.setImageResource(R.drawable.rc_picsel_empty_pic);
                } else {
                    path = ((PictureSelectorActivity.PicItem)((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(0))).get(0)).uri;
                    AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
                    holder.image.setTag(path);
                    bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(path, PictureSelectorActivity.this.perWidth, PictureSelectorActivity.this.perWidth, new AlbumBitmapCacheHelper.ILoadImageCallback() {
                        public void onLoadImageCallBack(Bitmap bitmap, String path1, Object... objects) {
                            if(bitmap != null) {
                                BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
                                View v = PictureSelectorActivity.this.mGridView.findViewWithTag(path1);
                                if(v != null) {
                                    v.setBackgroundDrawable(bd);
                                    CatalogAdapter.this.notifyDataSetChanged();
                                }

                            }
                        }
                    }, new Object[]{Integer.valueOf(position)});
                    if(bitmap != null) {
                        bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
                        holder.image.setBackgroundDrawable(bd);
                    } else {
                        holder.image.setBackgroundResource(R.drawable.rc_grid_image_default);
                    }
                }

                name = PictureSelectorActivity.this.getResources().getString(R.string.rc_picsel_catalog_allpic);
                holder.number.setVisibility(View.GONE);
                showSelected = PictureSelectorActivity.this.mCurrentCatalog.isEmpty();
            } else {
                path = ((PictureSelectorActivity.PicItem)((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(position - 1))).get(0)).uri;
                name = (String)PictureSelectorActivity.this.mCatalogList.get(position - 1);
                num = ((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCatalogList.get(position - 1))).size();
                holder.number.setVisibility(View.VISIBLE);
                showSelected = name.equals(PictureSelectorActivity.this.mCurrentCatalog);
                AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
                holder.image.setTag(path);
                bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(path, PictureSelectorActivity.this.perWidth, PictureSelectorActivity.this.perWidth, new AlbumBitmapCacheHelper.ILoadImageCallback() {
                    public void onLoadImageCallBack(Bitmap bitmap, String path1, Object... objects) {
                        if(bitmap != null) {
                            BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
                            View v = PictureSelectorActivity.this.mGridView.findViewWithTag(path1);
                            if(v != null) {
                                v.setBackgroundDrawable(bd);
                                CatalogAdapter.this.notifyDataSetChanged();
                            }

                        }
                    }
                }, new Object[]{Integer.valueOf(position)});
                if(bitmap != null) {
                    bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
                    holder.image.setBackgroundDrawable(bd);
                } else {
                    holder.image.setBackgroundResource(R.drawable.rc_grid_image_default);
                }
            }

            holder.name.setText(name);
            holder.number.setText(String.format(PictureSelectorActivity.this.getResources().getString(R.string.rc_picsel_catalog_number), new Object[]{Integer.valueOf(num)}));
            holder.selected.setVisibility(showSelected?View.VISIBLE:View.INVISIBLE);
            return view;
        }

        private class ViewHolder {
            ImageView image;
            TextView name;
            TextView number;
            ImageView selected;

            private ViewHolder() {
            }
        }
    }

    private class GridViewAdapter extends BaseAdapter {
        private LayoutInflater mInflater = PictureSelectorActivity.this.getLayoutInflater();

        public GridViewAdapter() {
        }

        public int getCount() {
            int sum = 1;
            String key;
            if(PictureSelectorActivity.this.mCurrentCatalog.isEmpty()) {
                for(Iterator i$ = PictureSelectorActivity.this.mItemMap.keySet().iterator(); i$.hasNext(); sum += ((List)PictureSelectorActivity.this.mItemMap.get(key)).size()) {
                    key = (String)i$.next();
                }
            } else {
                sum += ((List)PictureSelectorActivity.this.mItemMap.get(PictureSelectorActivity.this.mCurrentCatalog)).size();
            }

            return sum;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return (long)position;
        }

        @TargetApi(23)
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position == 0) {
                View item1 = this.mInflater.inflate(R.layout.rc_picsel_grid_camera, parent, false);
                ImageButton view1 = (ImageButton)item1.findViewById(R.id.camera_mask);
                view1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        String[] permissions = new String[]{"android.permission.CAMERA"};
                        if(PermissionCheckUtil.requestPermissions(PictureSelectorActivity.this, permissions, 100)) {
                            PictureSelectorActivity.this.requestCamera();
                        }
                    }
                });
                return item1;
            } else {
                final PictureSelectorActivity.PicItem item;
                if(PictureSelectorActivity.this.mCurrentCatalog.isEmpty()) {
                    item = (PictureSelectorActivity.PicItem)PictureSelectorActivity.this.mAllItemList.get(position - 1);
                } else {
                    item = PictureSelectorActivity.this.getItemAt(PictureSelectorActivity.this.mCurrentCatalog, position - 1);
                }

                View view = convertView;
                final PictureSelectorActivity.GridViewAdapter.ViewHolder holder;
                if(convertView != null && convertView.getTag() != null) {
                    holder = (PictureSelectorActivity.GridViewAdapter.ViewHolder)convertView.getTag();
                } else {
                    view = this.mInflater.inflate(R.layout.rc_picsel_grid_item, parent, false);
                    holder = new PictureSelectorActivity.GridViewAdapter.ViewHolder();
                    holder.image = (ImageView)view.findViewById(R.id.image);
                    holder.mask = view.findViewById(R.id.mask);
                    holder.checkBox = (PictureSelectorActivity.SelectBox)view.findViewById(R.id.checkbox);
                    view.setTag(holder);
                }

                String path;
                if(holder.image.getTag() != null) {
                    path = (String)holder.image.getTag();
                    AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(path);
                }

                path = item.uri;
                AlbumBitmapCacheHelper.getInstance().addPathToShowlist(path);
                holder.image.setTag(path);
                Bitmap bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(path, PictureSelectorActivity.this.perWidth, PictureSelectorActivity.this.perWidth, new AlbumBitmapCacheHelper.ILoadImageCallback() {
                    public void onLoadImageCallBack(Bitmap bitmap, String path1, Object... objects) {
                        if(bitmap != null) {
                            BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
                            View v = PictureSelectorActivity.this.mGridView.findViewWithTag(path1);
                            if(v != null) {
                                v.setBackgroundDrawable(bd);
                            }

                        }
                    }
                }, new Object[]{Integer.valueOf(position)});
                if(bitmap != null) {
                    BitmapDrawable bd = new BitmapDrawable(PictureSelectorActivity.this.getResources(), bitmap);
                    holder.image.setBackgroundDrawable(bd);
                } else {
                    holder.image.setBackgroundResource(R.drawable.rc_grid_image_default);
                }

                holder.checkBox.setChecked(item.selected);
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(!holder.checkBox.getChecked() && PictureSelectorActivity.this.getTotalSelectedNum() == 9) {
                            Toast.makeText(PictureSelectorActivity.this.getApplicationContext(), R.string.rc_picsel_selected_max, Toast.LENGTH_SHORT).show();
                        } else {
                            holder.checkBox.setChecked(!holder.checkBox.getChecked());
                            item.selected = holder.checkBox.getChecked();
                            if(item.selected) {
                                holder.mask.setBackgroundColor(PictureSelectorActivity.this.getResources().getColor(R.color.rc_picsel_grid_mask_pressed));
                            } else {
                                holder.mask.setBackgroundDrawable(PictureSelectorActivity.this.getResources().getDrawable(R.drawable.rc_sp_grid_mask));
                            }

                            PictureSelectorActivity.this.updateToolbar();
                        }
                    }
                });
                if(item.selected) {
                    holder.mask.setBackgroundColor(PictureSelectorActivity.this.getResources().getColor(R.color.rc_picsel_grid_mask_pressed));
                } else {
                    holder.mask.setBackgroundDrawable(PictureSelectorActivity.this.getResources().getDrawable(R.drawable.rc_sp_grid_mask));
                }

                return view;
            }
        }

        private class ViewHolder {
            ImageView image;
            View mask;
            PictureSelectorActivity.SelectBox checkBox;

            private ViewHolder() {
            }
        }
    }
}

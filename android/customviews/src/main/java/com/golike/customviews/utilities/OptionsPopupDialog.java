package com.golike.customviews.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.golike.customviews.R;

/**
 * Created by admin on 2017/9/6.
 */

public class OptionsPopupDialog extends AlertDialog {
    private Context mContext;
    private ListView mListView;
    private String[] arrays;
    private OptionsPopupDialog.OnOptionsItemClickedListener mItemClickedListener;

    public static OptionsPopupDialog newInstance(Context context, String[] arrays) {
        OptionsPopupDialog optionsPopupDialog = new OptionsPopupDialog(context, arrays);
        return optionsPopupDialog;
    }

    public OptionsPopupDialog(Context context, String[] arrays) {
        super(context);
        this.mContext = context;
        this.arrays = arrays;
    }

    protected void onStart() {
        super.onStart();
        LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ee_dialog_popup_options,  null);
        this.mListView = (ListView)view.findViewById(R.id.rc_list_dialog_popup_options);
        ArrayAdapter adapter = new ArrayAdapter(this.mContext, R.layout.ee_dialog_popup_options_item, R.id.rc_dialog_popup_item_name, this.arrays);
        this.mListView.setAdapter(adapter);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(OptionsPopupDialog.this.mItemClickedListener != null) {
                    OptionsPopupDialog.this.mItemClickedListener.onOptionsItemClicked(position);
                    OptionsPopupDialog.this.dismiss();
                }

            }
        });
        this.setContentView(view);
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.width = this.getPopupWidth();
        layoutParams.height = -2;
        this.getWindow().setAttributes(layoutParams);
    }

    public OptionsPopupDialog setOptionsPopupDialogListener(OptionsPopupDialog.OnOptionsItemClickedListener itemListener) {
        this.mItemClickedListener = itemListener;
        return this;
    }

    private int getPopupWidth() {
        int distanceToBorder = (int)this.mContext.getResources().getDimension(R.dimen.rc_popup_dialog_distance_to_edge);
        return this.getScreenWidth() - 2 * distanceToBorder;
    }

    private int getScreenWidth() {
        return (((WindowManager)this.mContext.getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getWidth();
    }

    public interface OnOptionsItemClickedListener {
        void onOptionsItemClicked(int var1);
    }
}


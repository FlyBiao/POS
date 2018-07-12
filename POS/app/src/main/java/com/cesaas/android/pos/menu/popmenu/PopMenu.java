package com.cesaas.android.pos.menu.popmenu;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.cesaas.android.pos.R;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：
 * 创建日期：2016/11/3 10:48
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class PopMenu {
    private ArrayList<String> itemList;
    private Context context;
    private PopupWindow popupWindow;
    private ListView listView;

    public PopMenu(Context context) {
        this.context = context;
        itemList = new ArrayList<String>();

        View view = LayoutInflater.from(context)
                .inflate(R.layout.poplist, null);

        // 设置 listview
        listView = (ListView) view.findViewById(R.id.menu_listview);
        listView.setAdapter(new PopMenuAdapter(context,itemList));
        listView.setFocusableInTouchMode(true);
        listView.setFocusable(true);

        popupWindow = new PopupWindow(view, 100, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow = new PopupWindow(view, context.getResources()
                .getDimensionPixelSize(R.dimen.popmenu_width),
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    // 设置菜单项点击监听器
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        listView.setOnItemClickListener(listener);
    }

    // 批量添加菜单项
    public void addItems(String[] items) {
        for (String s : items) {
            itemList.add(s);
        }
    }

    // 单个添加菜单项
    public void addItem(String item) {
        itemList.add(item);
    }

    // 下拉式 弹出 pop菜单 parent 右下角
    public void showAsDropDown(View parent) {
        popupWindow.showAsDropDown(parent,
                10,
                // 保证尺寸是根据屏幕像素密度来的
                context.getResources().getDimensionPixelSize(
                        R.dimen.popmenu_yoff));

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 刷新状态
        popupWindow.update();
    }

    // 隐藏菜单
    public void dismiss() {
        popupWindow.dismiss();
    }
}

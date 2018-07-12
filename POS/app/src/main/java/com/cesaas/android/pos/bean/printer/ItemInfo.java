package com.cesaas.android.pos.bean.printer;

import android.os.Parcel;
import android.os.Parcelable;

import com.wangpos.poscore.util.ParamMap;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：普通打印Bean
 * 创建日期：2016/11/3 22:14
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ItemInfo extends BaseBean{

    public String name;//
    public String count;//
    public String price;//

    public ItemInfo() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ItemInfo> CREATOR = new Parcelable.Creator<ItemInfo>() {
        public ItemInfo createFromParcel(Parcel in) {
            return new ItemInfo(in);
        }

        public ItemInfo[] newArray(int size) {
            return new ItemInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(count);
        dest.writeString(price);
    }

    private ItemInfo(Parcel in) {
        name = in.readString();
        count = in.readString();
        price = in.readString();
    }

    /**
     * 加载来自服务器的数据
     *
     */

    @Override
    public void loadFromServerData(ParamMap param) {
    }
}

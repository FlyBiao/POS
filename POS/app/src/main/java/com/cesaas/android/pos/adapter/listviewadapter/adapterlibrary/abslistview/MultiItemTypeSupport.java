package com.cesaas.android.pos.adapter.listviewadapter.adapterlibrary.abslistview;

/**
 * 多Item类型支持 接口
 * @author FGB
 *
 * @param <T>
 */
public interface MultiItemTypeSupport<T>
{
	int getLayoutId(int position, T t);

	int getViewTypeCount();

	int getItemViewType(int position, T t);
}
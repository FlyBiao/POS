package com.cesaas.android.pos.net;

import android.content.Context;

import com.cesaas.android.pos.global.Constant;
import com.cesaas.android.pos.net.nohttp.CallServer;
import com.cesaas.android.pos.net.nohttp.HttpListener;
import com.cesaas.android.pos.utils.AbPrefsUtil;
import com.cesaas.android.pos.utils.MD5;
import com.yolanda.nohttp.rest.Request;


/**
 * ================================================
 * 作    者：FGB
 * 描    述：公共网络
 * 创建日期：2016/5/6 15:35
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class CommonNet {
    private Context ct;
    public CommonNet(Context ct)
    {
        this.ct=ct;
    }

    /**
     *请求网络任务【一般数据显示使用】
     * @param request 请求网络
     * @param httpListener 网络监听
     */
    public void requestNetTask(Request<String> request , HttpListener<String> httpListener)
    {
        request.setHeader("Authorization", this.getAuthorization());
        request.add("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        CallServer.getRequestInstance().add(ct, 0, request, httpListener, true, true);
    }

    /**
     *请求网络任务【分页使用】
     * @param request 请求网络
     * @param httpListener 网络监听
     * @param pageIndex 当前页
     */
    public void requestNetTask(Request<String> request , HttpListener<String> httpListener,int pageIndex)
    {
        request.setHeader("Authorization", this.getAuthorization());
        request.add("UserTicket", AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN));
        request.add("PageIndex",pageIndex);
        request.add("PageSize",50);
        CallServer.getRequestInstance().add(ct, 0, request, httpListener, true, true);
    }


    /**
     * 获取Authorization
     */
    public String getAuthorization(){
        String token = this.getToken();
        String time = AbPrefsUtil.getInstance().getString(Constant.SPF_TIME, "");
        StringBuffer auth = new StringBuffer();
        if (!"".equals(token) && !"".equals(time)) {
            auth.append("SW-Authorization ").append(token).append(";").append(time);
        }
        return auth.toString();
    }

    /**
     * 获取令牌授权
     * @return
     */
    public String getToken() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append(AbPrefsUtil.getInstance().getString(Constant.SPF_TOKEN))
                .append("SW-AppAuthorizationToken")
                .append(AbPrefsUtil.getInstance().getString(Constant.SPF_TIME, ""));
        return new MD5().toMD5(sbuf.toString());
    }
}
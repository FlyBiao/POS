package com.cesaas.android.pos.bean;

/**
 * Author FGB
 * Description
 * Created at 2017/9/6 17:40
 * Version 1.0
 */

public class ResultGetTokenBean extends BaseBean{

    public GetTokenBean TModel;

    public class GetTokenBean extends BaseBean{
        public String appkey;
        public String code;//
        public String token;//融云ToKen
        public int userId;//
    }
}

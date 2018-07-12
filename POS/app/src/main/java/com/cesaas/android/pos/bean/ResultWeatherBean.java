package com.cesaas.android.pos.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：天气结果Bean
 * 创建日期：2017/2/6 17:15
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ResultWeatherBean {

    public ArrayList<WeatherBean> TModel;

    public class WeatherBean implements Serializable{
        private String WeatherId;
        private String WeatherType;

        public String getWeatherId() {
            return WeatherId;
        }

        public void setWeatherId(String weatherId) {
            WeatherId = weatherId;
        }

        public String getWeatherType() {
            return WeatherType;
        }
        public void setWeatherType(String weatherType) {
            WeatherType = weatherType;
        }
    }

}

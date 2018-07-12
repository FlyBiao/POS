package com.cesaas.android.pos.bean;

import java.util.ArrayList;

/**
 * ================================================
 * 作    者：FGB
 * 描    述：班次结果Bean
 * 创建日期：2017/2/6 17:14
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class ResultWorkShiftBean extends BaseBean{

    public ArrayList<WorkShiftBean> TModel;

    public class WorkShiftBean{
        private String WorkShiftId;
        private String WorkShiftName;

        public String getWorkShiftId() {
            return WorkShiftId;
        }

        public void setWorkShiftId(String workShiftId) {
            WorkShiftId = workShiftId;
        }

        public String getWorkShiftName() {
            return WorkShiftName;
        }

        public void setWorkShiftName(String workShiftName) {
            WorkShiftName = workShiftName;
        }
    }
}

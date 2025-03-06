package com.excited.common.core.domain.entity;

import com.excited.common.core.enums.ResultCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TableDataInfo {

    private long total;        // 总记录数

    private List<?> data;      // 当前页的数据

    private int code;

    private String msg;

    /**
     * 当未查询到数据时所需要调用的方法
     * @return 列表响应数据
     */
    public static TableDataInfo empty() {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        tableDataInfo.setMsg(ResultCode.SUCCESS.getMsg());
        tableDataInfo.setTotal(0);
        tableDataInfo.setData(new ArrayList<>());

        return tableDataInfo;
    }

    /**
     * 当正常查询到数据时所需要调用的方法
     * @return 列表响应数据
     */
    public static TableDataInfo success(List<?> data, long total) {
        TableDataInfo tableDataInfo = new TableDataInfo();
        tableDataInfo.setCode(ResultCode.SUCCESS.getCode());
        tableDataInfo.setMsg(ResultCode.SUCCESS.getMsg());
        tableDataInfo.setTotal(total);
        tableDataInfo.setData(data);

        return tableDataInfo;
    }
}

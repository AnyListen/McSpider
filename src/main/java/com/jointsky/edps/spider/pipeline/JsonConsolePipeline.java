package com.jointsky.edps.spider.pipeline;

import cn.hutool.json.JSONUtil;
import com.jointsky.edps.spider.common.SysConstant;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-25.
 */
public class JsonConsolePipeline extends ConsolePipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> itemsAll = resultItems.getAll();
        System.out.println("**************************");
        System.out.println(JSONUtil.toJsonStr(itemsAll.getOrDefault(SysConstant.SINGLE_ITEM, itemsAll)));
        System.out.println("**************************");
    }
}

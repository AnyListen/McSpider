package com.jointsky.edps.spider.utils;

import cn.hutool.log.StaticLog;
import com.jointsky.edps.spider.common.SelectType;
import com.jointsky.edps.spider.config.FieldSelectConfig;
import us.codecraft.webmagic.selector.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-18.
 */
public class ProcessorUtils {

    public static Selectable getSelectVal(AbstractSelectable selectable, FieldSelectConfig fConfig){
        return getSelectVal(null, selectable, fConfig.getConfigText(), fConfig.getSelectType(), fConfig.getGroup());
    }

    public static Selectable getSelectVal(List<FieldSelectConfig> staticFields, AbstractSelectable selectable, FieldSelectConfig fConfig){
        return getSelectVal(staticFields, selectable, fConfig.getConfigText(), fConfig.getSelectType(), fConfig.getGroup());
    }

    public static Selectable getSelectVal(List<FieldSelectConfig> staticFields, AbstractSelectable selectable, String selConfig, SelectType selectType){
        return getSelectVal(staticFields, selectable, selConfig, selectType, 0);
    }

    public static Selectable getSelectVal(List<FieldSelectConfig> staticFields, AbstractSelectable selectable, String selConfig, SelectType selectType, int groupNum){
        Selectable value = null;
        try{
            switch (selectType){
                case CSS:
                    value = selectable.$(selConfig);
                    break;
                case REGEX:
                    value = selectable.regex(selConfig, groupNum);
                    break;
                case XPATH:
                    value = selectable.xpath(selConfig);
                    break;
                case JPATH:
                    value = selectable.jsonPath(selConfig);
                    break;
                case FIELD:
                    if (staticFields != null && staticFields.size() > 0) {
                        Stream<FieldSelectConfig> stream = staticFields.stream();
                        Optional<FieldSelectConfig> first = stream
                                .filter(f -> f.getFiledName().equals(selConfig) && f.getSelectType() == SelectType.NONE)
                                .findFirst();
                        stream.close();
                        if (first.isPresent()){
                            FieldSelectConfig config = first.get();
                            value = new PlainText(config.getConfigText());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        catch (Exception ex){
            StaticLog.error(ex);
        }
        return value;
    }
}

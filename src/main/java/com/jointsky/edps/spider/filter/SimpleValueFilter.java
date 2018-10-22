package com.jointsky.edps.spider.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jointsky.edps.spider.common.SysConstant;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * edps-spider
 * edps-spider
 * Created by hezl on 2018-10-22.
 */
public class SimpleValueFilter extends ValueFilter {
    @Override
    public boolean shouldRemove() {
        Map<String, Object> settings = this.getSettings();
        Object value = this.getValue();
        if (settings == null || settings.size() <= 0){
            return false;
        }
        String method = settings.get(SysConstant.SIMPLE_FILTER_METHOD).toString().toUpperCase();
        if (SysConstant.NULL_FILTER.equals(method)) {
            return ObjectUtil.isNull(this.getValue());
        }
        if (SysConstant.EMPTY_FILTER.equals(method)) {
            if (value instanceof CharSequence) return ((CharSequence) value).length() == 0;
            else if (value instanceof Collection) return ((Collection) value).isEmpty();
            else if (value instanceof Map) return ((Map) value).isEmpty();
            else if (value.getClass().isArray()) return Array.getLength(value) == 0;
            return StrUtil.isBlank(value.toString());
        }
        if (SysConstant.TYPE_FILTER.equals(method)) {
            String type = settings.get(SysConstant.SIMPLE_FILTER_TYPE).toString().toUpperCase();
            if (!SysConstant.TYPE_MAP.containsKey(type)){
                return false;
            }
            return SysConstant.TYPE_MAP.get(type).isInstance(value);
        }
        return false;
    }
}

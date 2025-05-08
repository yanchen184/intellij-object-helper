package com.yanchen.plugin.objecthelper.config;

import com.yanchen.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import org.jetbrains.annotations.NotNull;

/**
 * 插件配置狀態
 */
public class PluginConfigState {

    /**
     * 對象拷貝功能開關，默認開啟
     */
    private FunctionSwitchEnum objectCopySwitch = FunctionSwitchEnum.OPEN;

    /**
     * Java 類轉 JSON 功能開關，默認開啟
     */
    private FunctionSwitchEnum classToJsonSwitch = FunctionSwitchEnum.OPEN;

    /**
     * Java 類轉 XML 功能開關，默認開啟
     */
    private FunctionSwitchEnum classToXmlSwitch = FunctionSwitchEnum.OPEN;

    /**
     * Java 類轉 Thrift IDL 功能開關，默認開啟
     */
    private FunctionSwitchEnum classToThriftSwitch = FunctionSwitchEnum.OPEN;

    /**
     * 對象拷貝時，針對目標類中不存在的字段是否生成註釋，默認生成
     */
    private boolean nonExistentFieldGenerateAnnotation = true;

    /**
     * 獲取配置狀態實例
     *
     * @return 配置狀態實例
     */
    @NotNull
    public static PluginConfigState getInstance() {
        PluginConfigModel configModel = PluginConfigModel.getInstance();
        PluginConfigState state = configModel.getState();
        return state != null ? state : new PluginConfigState();
    }

    /**
     * 獲取對象拷貝功能開關
     *
     * @return 功能開關
     */
    public FunctionSwitchEnum getObjectCopySwitch() {
        return objectCopySwitch;
    }

    /**
     * 設置對象拷貝功能開關
     *
     * @param objectCopySwitch 功能開關
     */
    public void setObjectCopySwitch(FunctionSwitchEnum objectCopySwitch) {
        this.objectCopySwitch = objectCopySwitch;
    }

    /**
     * 獲取 Java 類轉 JSON 功能開關
     *
     * @return 功能開關
     */
    public FunctionSwitchEnum getClassToJsonSwitch() {
        return classToJsonSwitch;
    }

    /**
     * 設置 Java 類轉 JSON 功能開關
     *
     * @param classToJsonSwitch 功能開關
     */
    public void setClassToJsonSwitch(FunctionSwitchEnum classToJsonSwitch) {
        this.classToJsonSwitch = classToJsonSwitch;
    }

    /**
     * 獲取 Java 類轉 XML 功能開關
     *
     * @return 功能開關
     */
    public FunctionSwitchEnum getClassToXmlSwitch() {
        return classToXmlSwitch;
    }

    /**
     * 設置 Java 類轉 XML 功能開關
     *
     * @param classToXmlSwitch 功能開關
     */
    public void setClassToXmlSwitch(FunctionSwitchEnum classToXmlSwitch) {
        this.classToXmlSwitch = classToXmlSwitch;
    }

    /**
     * 獲取 Java 類轉 Thrift IDL 功能開關
     *
     * @return 功能開關
     */
    public FunctionSwitchEnum getClassToThriftSwitch() {
        return classToThriftSwitch;
    }

    /**
     * 設置 Java 類轉 Thrift IDL 功能開關
     *
     * @param classToThriftSwitch 功能開關
     */
    public void setClassToThriftSwitch(FunctionSwitchEnum classToThriftSwitch) {
        this.classToThriftSwitch = classToThriftSwitch;
    }

    /**
     * 獲取對象拷貝時是否為不存在的字段生成註釋
     *
     * @return 是否生成註釋
     */
    public boolean isNonExistentFieldGenerateAnnotation() {
        return nonExistentFieldGenerateAnnotation;
    }

    /**
     * 設置對象拷貝時是否為不存在的字段生成註釋
     *
     * @param nonExistentFieldGenerateAnnotation 是否生成註釋
     */
    public void setNonExistentFieldGenerateAnnotation(boolean nonExistentFieldGenerateAnnotation) {
        this.nonExistentFieldGenerateAnnotation = nonExistentFieldGenerateAnnotation;
    }
}
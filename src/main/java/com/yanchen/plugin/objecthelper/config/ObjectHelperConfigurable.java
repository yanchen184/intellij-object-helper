package com.yanchen.plugin.objecthelper.config;

import com.intellij.openapi.options.Configurable;
import com.yanchen.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 插件配置界面
 */
public class ObjectHelperConfigurable implements Configurable {

    /**
     * 配置面板
     */
    private JPanel rootPanel;
    
    /**
     * 對象複製功能開關
     */
    private JCheckBox objectCopyCheckBox;
    
    /**
     * Java 類轉 JSON 功能開關
     */
    private JCheckBox classToJsonCheckBox;
    
    /**
     * Java 類轉 XML 功能開關
     */
    private JCheckBox classToXmlCheckBox;
    
    /**
     * Java 類轉 Thrift IDL 功能開關
     */
    private JCheckBox classToThriftCheckBox;
    
    /**
     * 對象拷貝時，針對目標類中不存在的字段是否生成註釋
     */
    private JCheckBox nonExistentFieldGenerateAnnotationCheckBox;

    /**
     * 獲取配置界面顯示名稱
     *
     * @return 顯示名稱
     */
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Object Helper";
    }

    /**
     * 創建配置界面組件
     *
     * @return 配置界面組件
     */
    @Nullable
    @Override
    public JComponent createComponent() {
        if (rootPanel == null) {
            rootPanel = new JPanel();
            rootPanel.setLayout(new GridLayout(6, 1));

            objectCopyCheckBox = new JCheckBox("開啟對象複製功能");
            classToJsonCheckBox = new JCheckBox("開啟 Java 類轉 JSON 功能");
            classToXmlCheckBox = new JCheckBox("開啟 Java 類轉 XML 功能");
            classToThriftCheckBox = new JCheckBox("開啟 Java 類轉 Thrift IDL 功能");
            nonExistentFieldGenerateAnnotationCheckBox = new JCheckBox("對象複製時為不存在的字段生成註釋");

            rootPanel.add(objectCopyCheckBox);
            rootPanel.add(classToJsonCheckBox);
            rootPanel.add(classToXmlCheckBox);
            rootPanel.add(classToThriftCheckBox);
            rootPanel.add(nonExistentFieldGenerateAnnotationCheckBox);
        }

        reset();
        return rootPanel;
    }

    /**
     * 檢查配置是否已修改
     *
     * @return 是否已修改
     */
    @Override
    public boolean isModified() {
        PluginConfigState state = PluginConfigState.getInstance();
        
        boolean objectCopyModified = state.getObjectCopySwitch() != 
                (objectCopyCheckBox.isSelected() ? FunctionSwitchEnum.OPEN : FunctionSwitchEnum.CLOSE);
        
        boolean classToJsonModified = state.getClassToJsonSwitch() != 
                (classToJsonCheckBox.isSelected() ? FunctionSwitchEnum.OPEN : FunctionSwitchEnum.CLOSE);
        
        boolean classToXmlModified = state.getClassToXmlSwitch() != 
                (classToXmlCheckBox.isSelected() ? FunctionSwitchEnum.OPEN : FunctionSwitchEnum.CLOSE);
        
        boolean classToThriftModified = state.getClassToThriftSwitch() != 
                (classToThriftCheckBox.isSelected() ? FunctionSwitchEnum.OPEN : FunctionSwitchEnum.CLOSE);
        
        boolean nonExistentFieldGenerateAnnotationModified = 
                state.isNonExistentFieldGenerateAnnotation() != nonExistentFieldGenerateAnnotationCheckBox.isSelected();
        
        return objectCopyModified || classToJsonModified || classToXmlModified || 
                classToThriftModified || nonExistentFieldGenerateAnnotationModified;
    }

    /**
     * 應用配置修改
     */
    @Override
    public void apply() {
        PluginConfigState state = PluginConfigState.getInstance();
        
        state.setObjectCopySwitch(objectCopyCheckBox.isSelected() ? 
                FunctionSwitchEnum.OPEN : FunctionSwitchEnum.CLOSE);
        
        state.setClassToJsonSwitch(classToJsonCheckBox.isSelected() ? 
                FunctionSwitchEnum.OPEN : FunctionSwitchEnum.CLOSE);
        
        state.setClassToXmlSwitch(classToXmlCheckBox.isSelected() ? 
                FunctionSwitchEnum.OPEN : FunctionSwitchEnum.CLOSE);
        
        state.setClassToThriftSwitch(classToThriftCheckBox.isSelected() ? 
                FunctionSwitchEnum.OPEN : FunctionSwitchEnum.CLOSE);
        
        state.setNonExistentFieldGenerateAnnotation(nonExistentFieldGenerateAnnotationCheckBox.isSelected());
    }

    /**
     * 重置配置
     */
    @Override
    public void reset() {
        PluginConfigState state = PluginConfigState.getInstance();
        
        objectCopyCheckBox.setSelected(state.getObjectCopySwitch() == FunctionSwitchEnum.OPEN);
        classToJsonCheckBox.setSelected(state.getClassToJsonSwitch() == FunctionSwitchEnum.OPEN);
        classToXmlCheckBox.setSelected(state.getClassToXmlSwitch() == FunctionSwitchEnum.OPEN);
        classToThriftCheckBox.setSelected(state.getClassToThriftSwitch() == FunctionSwitchEnum.OPEN);
        nonExistentFieldGenerateAnnotationCheckBox.setSelected(state.isNonExistentFieldGenerateAnnotation());
    }
}
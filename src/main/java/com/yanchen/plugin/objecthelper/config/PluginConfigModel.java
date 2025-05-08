package com.yanchen.plugin.objecthelper.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.yanchen.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 插件配置模型
 */
@State(
        name = "ObjectHelperConfig",
        storages = {@Storage("objecthelper.xml")}
)
public class PluginConfigModel implements PersistentStateComponent<PluginConfigState> {

    /**
     * 插件配置狀態
     */
    private PluginConfigState state = new PluginConfigState();

    /**
     * 獲取配置模型實例
     *
     * @return 配置模型實例
     */
    public static PluginConfigModel getInstance() {
        return ServiceManager.getService(PluginConfigModel.class);
    }

    /**
     * 獲取配置狀態
     *
     * @return 配置狀態
     */
    @Nullable
    @Override
    public PluginConfigState getState() {
        return state;
    }

    /**
     * 加載配置狀態
     *
     * @param state 配置狀態
     */
    @Override
    public void loadState(@NotNull PluginConfigState state) {
        this.state = state;
    }
}
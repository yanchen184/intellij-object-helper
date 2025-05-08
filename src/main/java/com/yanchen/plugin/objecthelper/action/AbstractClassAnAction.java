package com.yanchen.plugin.objecthelper.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * 抽象 Action 基類
 */
public abstract class AbstractClassAnAction extends AnAction {

    /**
     * 更新操作狀態
     *
     * @param anActionEvent 事件
     */
    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        Presentation presentation = anActionEvent.getPresentation();
        
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        
        // 檢查編輯器和文件是否可用
        if (editor == null || psiFile == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        
        // 檢查是否為 Java 文件
        if (!psiFile.getName().endsWith(".java")) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        
        // 調用子類的檢查方法
        presentation.setEnabledAndVisible(actionShow(anActionEvent));
    }
    
    /**
     * 檢查操作是否應該顯示
     * 由子類實現
     *
     * @param anActionEvent 事件
     * @return 是否顯示
     */
    public abstract boolean actionShow(AnActionEvent anActionEvent);
}
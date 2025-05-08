package com.yanchen.plugin.objecthelper.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.yanchen.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import com.yanchen.plugin.objecthelper.common.util.PsiUtils;
import com.yanchen.plugin.objecthelper.config.PluginConfigState;
import com.yanchen.plugin.objecthelper.generator.Generator;
import com.yanchen.plugin.objecthelper.generator.format.ClassToJsonGenerator;

/**
 * Java 類轉 JSON 操作
 */
public class ClassToFormatJsonAction extends AbstractClassAnAction {

    /**
     * 執行操作
     *
     * @param anActionEvent 事件
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }
        
        // 獲取當前類
        PsiClass psiClass = PsiUtils.getCursorPsiClass(anActionEvent);
        if (psiClass == null) {
            Messages.showErrorDialog("請將光標放在類定義內", "錯誤");
            return;
        }
        
        // 生成 JSON
        Generator generator = ClassToJsonGenerator.getInstance(psiClass);
        String jsonContent = generator.generate();
        
        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            Messages.showErrorDialog("無法生成 JSON", "錯誤");
            return;
        }
        
        // 創建新的編輯器顯示生成的 JSON
        WriteCommandAction.runWriteCommandAction(project, () -> {
            // 創建編輯器
            Editor editor = EditorFactory.getInstance().createEditor(
                    EditorFactory.getInstance().createDocument(jsonContent),
                    project,
                    "JSON",
                    false // 只讀模式
            );
            
            // 顯示編輯器
            FileEditorManager.getInstance(project).openTextEditor(
                    editor.getDocument(),
                    true
            );
        });
    }

    /**
     * 檢查操作是否應該顯示
     *
     * @param anActionEvent 事件
     * @return 是否顯示
     */
    @Override
    public boolean actionShow(AnActionEvent anActionEvent) {
        return PluginConfigState.getInstance().getClassToJsonSwitch() == FunctionSwitchEnum.OPEN;
    }
}
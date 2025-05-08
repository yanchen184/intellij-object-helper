package com.yanchen.plugin.objecthelper.action;

import com.yanchen.plugin.objecthelper.common.enums.FunctionSwitchEnum;
import com.yanchen.plugin.objecthelper.common.util.PsiUtils;
import com.yanchen.plugin.objecthelper.common.util.StringUtils;
import com.yanchen.plugin.objecthelper.config.PluginConfigState;
import com.yanchen.plugin.objecthelper.generator.Generator;
import com.yanchen.plugin.objecthelper.generator.method.ObjectCopyMethodGenerator;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;

import static com.yanchen.plugin.objecthelper.common.constant.JavaKeyWord.VOID;

/**
 * 對象複製方法生成操作
 */
public class ObjectCopyMethodAction extends AbstractClassAnAction {

    /**
     * 執行操作
     *
     * @param anActionEvent 事件
     */
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        WriteCommandAction.runWriteCommandAction(anActionEvent.getProject(), () -> {
            generateO2O(PsiUtils.getCursorPsiMethod(anActionEvent));
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
        return PluginConfigState.getInstance().getObjectCopySwitch() == FunctionSwitchEnum.OPEN
                && check(PsiUtils.getCursorPsiMethod(anActionEvent));
    }

    /**
     * 生成對象複製方法
     *
     * @param psiMethod 目標方法
     */
    private void generateO2O(PsiMethod psiMethod) {
        if (psiMethod == null) {
            return;
        }
        
        // 初始化生成器
        Generator generator = ObjectCopyMethodGenerator.getInstance(psiMethod);
        String methodCode = generator.generate();
        
        if (StringUtils.isEmpty(methodCode)) {
            return;
        }
        
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiMethod.getProject());
        
        // 生成新的 PsiMethod
        PsiMethod newMethod = elementFactory.createMethodFromText(methodCode, psiMethod);
        
        // 替換方法
        psiMethod.replace(newMethod);
    }

    /**
     * 檢查方法是否滿足條件
     * 1. 是否有參數
     * 2. 是否有返回值（非 void）
     *
     * @param psiMethod 方法
     * @return 是否滿足條件
     */
    private boolean check(PsiMethod psiMethod) {
        if (psiMethod == null
                || PsiUtils.getPsiParameters(psiMethod).isEmpty()
                || VOID.equals(PsiUtils.getMethodReturnClassName(psiMethod))) {
            return false;
        }
        return true;
    }
}
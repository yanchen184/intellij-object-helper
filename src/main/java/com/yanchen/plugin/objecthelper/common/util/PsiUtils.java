package com.yanchen.plugin.objecthelper.common.util;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PSI 相關工具類
 */
public class PsiUtils {

    /**
     * 獲取當前光標所在的方法
     *
     * @param anActionEvent 事件
     * @return PsiMethod 實例或 null
     */
    @Nullable
    public static PsiMethod getCursorPsiMethod(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(element, PsiMethod.class);
    }

    /**
     * 獲取當前光標所在的類
     *
     * @param anActionEvent 事件
     * @return PsiClass 實例或 null
     */
    @Nullable
    public static PsiClass getCursorPsiClass(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(element, PsiClass.class);
    }

    /**
     * 獲取方法的參數列表
     *
     * @param psiMethod 方法
     * @return 參數列表
     */
    @NotNull
    public static List<PsiParameter> getPsiParameters(@Nullable PsiMethod psiMethod) {
        if (psiMethod == null) {
            return Collections.emptyList();
        }
        PsiParameterList parameterList = psiMethod.getParameterList();
        if (parameterList.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<PsiParameter> parameters = new ArrayList<>();
        for (PsiParameter parameter : parameterList.getParameters()) {
            parameters.add(parameter);
        }
        return parameters;
    }

    /**
     * 獲取方法返回值類型名稱
     *
     * @param psiMethod 方法
     * @return 返回值類型名稱
     */
    @NotNull
    public static String getMethodReturnClassName(@Nullable PsiMethod psiMethod) {
        if (psiMethod == null) {
            return "";
        }
        PsiType returnType = psiMethod.getReturnType();
        return returnType == null ? "" : returnType.getPresentableText();
    }

    /**
     * 獲取類的所有字段
     *
     * @param psiClass 類
     * @return 字段列表
     */
    @NotNull
    public static List<PsiField> getAllFields(@Nullable PsiClass psiClass) {
        if (psiClass == null) {
            return Collections.emptyList();
        }

        List<PsiField> fields = new ArrayList<>();
        for (PsiField field : psiClass.getAllFields()) {
            fields.add(field);
        }
        return fields;
    }

    /**
     * 尋找指定類型的引用
     *
     * @param project 項目
     * @param className 類名
     * @return PsiClass 實例或 null
     */
    @Nullable
    public static PsiClass findPsiClass(@NotNull Project project, @NotNull String className) {
        return JavaPsiFacade.getInstance(project).findClass(className, 
                GlobalSearchScope.allScope(project));
    }
    
    /**
     * 判斷字段是否有 Getter 方法
     *
     * @param psiClass 類
     * @param fieldName 字段名
     * @return 是否有 Getter 方法
     */
    public static boolean hasGetterMethod(@NotNull PsiClass psiClass, @NotNull String fieldName) {
        String capitalizedName = StringUtils.capitalize(fieldName);
        String getterName = "get" + capitalizedName;
        String booleanGetterName = "is" + capitalizedName;
        
        // 檢查 getter 方法
        for (PsiMethod method : psiClass.getAllMethods()) {
            String methodName = method.getName();
            if ((methodName.equals(getterName) || methodName.equals(booleanGetterName)) 
                    && method.getParameterList().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判斷字段是否有 Setter 方法
     *
     * @param psiClass 類
     * @param fieldName 字段名
     * @return 是否有 Setter 方法
     */
    public static boolean hasSetterMethod(@NotNull PsiClass psiClass, @NotNull String fieldName) {
        String setterName = "set" + StringUtils.capitalize(fieldName);
        
        // 檢查 setter 方法
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (method.getName().equals(setterName) && method.getParameterList().getParametersCount() == 1) {
                return true;
            }
        }
        return false;
    }
}
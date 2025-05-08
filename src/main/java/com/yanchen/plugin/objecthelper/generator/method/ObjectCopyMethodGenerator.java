package com.yanchen.plugin.objecthelper.generator.method;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import com.yanchen.plugin.objecthelper.common.util.PsiUtils;
import com.yanchen.plugin.objecthelper.common.util.StringUtils;
import com.yanchen.plugin.objecthelper.config.PluginConfigState;
import com.yanchen.plugin.objecthelper.generator.Generator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 對象複製方法生成器
 */
public class ObjectCopyMethodGenerator implements Generator {

    /**
     * 目標方法
     */
    private final PsiMethod psiMethod;
    
    /**
     * 方法參數（源對象）
     */
    private final PsiParameter sourceParameter;
    
    /**
     * 源類型
     */
    private final PsiClass sourceClass;
    
    /**
     * 目標類型
     */
    private final PsiClass targetClass;
    
    /**
     * 對象複製方法生成器實例
     *
     * @param psiMethod 目標方法
     * @return 生成器實例
     */
    public static Generator getInstance(PsiMethod psiMethod) {
        return new ObjectCopyMethodGenerator(psiMethod);
    }
    
    /**
     * 構造方法
     *
     * @param psiMethod 目標方法
     */
    private ObjectCopyMethodGenerator(PsiMethod psiMethod) {
        this.psiMethod = psiMethod;
        
        List<PsiParameter> parameters = PsiUtils.getPsiParameters(psiMethod);
        this.sourceParameter = parameters.isEmpty() ? null : parameters.get(0);
        
        this.sourceClass = sourceParameter == null ? null : 
                PsiTypesUtil.getPsiClass(sourceParameter.getType());
        
        this.targetClass = PsiTypesUtil.getPsiClass(psiMethod.getReturnType());
    }

    /**
     * 生成對象複製方法
     *
     * @return 生成的方法代碼
     */
    @Override
    public String generate() {
        if (sourceClass == null || targetClass == null) {
            return null;
        }
        
        // 檢查目標類是否有 Builder 模式
        PsiMethod builderMethod = findBuilderMethod(targetClass);
        if (builderMethod != null) {
            return generateWithBuilderMode();
        } else {
            return generateWithSetterMode();
        }
    }
    
    /**
     * 使用 Builder 模式生成代碼
     *
     * @return 生成的方法代碼
     */
    private String generateWithBuilderMode() {
        // 獲取 Builder 類型
        PsiMethod builderMethod = findBuilderMethod(targetClass);
        if (builderMethod == null) {
            return null;
        }
        
        PsiClass builderClass = PsiTypesUtil.getPsiClass(builderMethod.getReturnType());
        if (builderClass == null) {
            return null;
        }
        
        // 方法簽名
        StringBuilder methodBuilder = new StringBuilder();
        methodBuilder.append(psiMethod.getText().substring(0, psiMethod.getText().indexOf('{')))
                .append(" {\n");
        
        // 獲取源對象名稱
        String sourceName = sourceParameter.getName();
        
        // 創建 Builder 實例
        methodBuilder.append("    // 使用 Builder 模式構建目標對象\n");
        methodBuilder.append("    ").append(builderClass.getName()).append(" builder = ")
                .append(targetClass.getName()).append(".")
                .append(builderMethod.getName()).append("();\n\n");
        
        // 遍歷目標類字段
        List<PsiField> targetFields = PsiUtils.getAllFields(targetClass);
        
        // 源類字段
        List<PsiField> sourceFields = PsiUtils.getAllFields(sourceClass);
        
        // 添加字段賦值
        for (PsiField targetField : targetFields) {
            // 排除靜態字段和常量
            if (targetField.hasModifierProperty(PsiModifier.STATIC) || 
                    targetField.hasModifierProperty(PsiModifier.FINAL)) {
                continue;
            }
            
            // 獲取目標字段的 Builder 方法名稱
            String targetFieldName = targetField.getName();
            String builderMethodName = targetFieldName;
            
            // 檢查源類是否有對應字段
            boolean fieldFound = false;
            for (PsiField sourceField : sourceFields) {
                if (sourceField.getName().equals(targetFieldName)) {
                    fieldFound = true;
                    
                    // 生成 Builder 方法調用
                    methodBuilder.append("    builder.")
                            .append(builderMethodName)
                            .append("(")
                            .append(sourceName)
                            .append(".")
                            .append(getGetterMethodName(sourceField))
                            .append("());\n");
                    break;
                }
            }
            
            // 如果源類中不存在該字段，添加註釋
            if (!fieldFound && PluginConfigState.getInstance().isNonExistentFieldGenerateAnnotation()) {
                methodBuilder.append("    // ").append(targetFieldName)
                        .append(" 在源類中不存在，需要手動設置\n");
            }
        }
        
        // 構建目標對象並返回
        methodBuilder.append("\n    // 構建並返回目標對象\n");
        methodBuilder.append("    return builder.build();\n");
        methodBuilder.append("}");
        
        return methodBuilder.toString();
    }
    
    /**
     * 使用 Setter 模式生成代碼
     *
     * @return 生成的方法代碼
     */
    private String generateWithSetterMode() {
        // 方法簽名
        StringBuilder methodBuilder = new StringBuilder();
        methodBuilder.append(psiMethod.getText().substring(0, psiMethod.getText().indexOf('{')))
                .append(" {\n");
        
        // 獲取源對象名稱
        String sourceName = sourceParameter.getName();
        
        // 創建目標對象
        methodBuilder.append("    // 創建目標對象\n");
        methodBuilder.append("    ").append(targetClass.getName()).append(" target = new ")
                .append(targetClass.getName()).append("();\n\n");
        
        // 遍歷目標類字段
        List<PsiField> targetFields = PsiUtils.getAllFields(targetClass);
        
        // 源類字段
        List<PsiField> sourceFields = PsiUtils.getAllFields(sourceClass);
        
        // 添加字段賦值
        for (PsiField targetField : targetFields) {
            // 排除靜態字段和常量
            if (targetField.hasModifierProperty(PsiModifier.STATIC) || 
                    targetField.hasModifierProperty(PsiModifier.FINAL)) {
                continue;
            }
            
            // 獲取目標字段的 Setter 方法名稱
            String targetFieldName = targetField.getName();
            String setterMethodName = "set" + StringUtils.capitalize(targetFieldName);
            
            // 檢查目標字段是否有 Setter 方法
            if (!PsiUtils.hasSetterMethod(targetClass, targetFieldName)) {
                methodBuilder.append("    // ").append(targetFieldName)
                        .append(" 缺少 Setter 方法，需要手動設置\n");
                continue;
            }
            
            // 檢查源類是否有對應字段
            boolean fieldFound = false;
            for (PsiField sourceField : sourceFields) {
                if (sourceField.getName().equals(targetFieldName)) {
                    fieldFound = true;
                    
                    // 檢查源字段是否有 Getter 方法
                    if (!PsiUtils.hasGetterMethod(sourceClass, sourceField.getName())) {
                        methodBuilder.append("    // ").append(sourceField.getName())
                                .append(" 缺少 Getter 方法，需要手動設置\n");
                        continue;
                    }
                    
                    // 生成字段賦值
                    methodBuilder.append("    target.")
                            .append(setterMethodName)
                            .append("(")
                            .append(sourceName)
                            .append(".")
                            .append(getGetterMethodName(sourceField))
                            .append("());\n");
                    break;
                }
            }
            
            // 如果源類中不存在該字段，添加註釋
            if (!fieldFound && PluginConfigState.getInstance().isNonExistentFieldGenerateAnnotation()) {
                methodBuilder.append("    // ").append(targetFieldName)
                        .append(" 在源類中不存在，需要手動設置\n");
            }
        }
        
        // 返回目標對象
        methodBuilder.append("\n    // 返回目標對象\n");
        methodBuilder.append("    return target;\n");
        methodBuilder.append("}");
        
        return methodBuilder.toString();
    }
    
    /**
     * 獲取字段的 Getter 方法名
     *
     * @param field 字段
     * @return Getter 方法名
     */
    @NotNull
    private String getGetterMethodName(PsiField field) {
        String fieldName = field.getName();
        String capitalizedFieldName = StringUtils.capitalize(fieldName);
        
        // 布爾類型使用 is 前綴
        PsiType fieldType = field.getType();
        if (PsiTypes.booleanType().equals(fieldType) || 
                "java.lang.Boolean".equals(fieldType.getCanonicalText())) {
            // 檢查是否已有 is 前綴
            if (fieldName.startsWith("is") && fieldName.length() > 2 && 
                    Character.isUpperCase(fieldName.charAt(2))) {
                return fieldName;
            } else {
                String isMethodName = "is" + capitalizedFieldName;
                if (PsiUtils.hasGetterMethod(sourceClass, fieldName)) {
                    return isMethodName;
                }
            }
        }
        
        return "get" + capitalizedFieldName;
    }
    
    /**
     * 查找目標類的 Builder 方法
     *
     * @param psiClass 目標類
     * @return Builder 方法或 null
     */
    @Nullable
    private PsiMethod findBuilderMethod(PsiClass psiClass) {
        for (PsiMethod method : psiClass.getAllMethods()) {
            // 檢查是否為 static 方法
            if (!method.hasModifierProperty(PsiModifier.STATIC)) {
                continue;
            }
            
            // 檢查方法名
            String methodName = method.getName();
            if (!"builder".equals(methodName) && !"newBuilder".equals(methodName)) {
                continue;
            }
            
            // 檢查返回類型是否為 Builder 類型
            PsiType returnType = method.getReturnType();
            if (returnType == null) {
                continue;
            }
            
            // 獲取返回類型的類
            PsiClass returnClass = PsiTypesUtil.getPsiClass(returnType);
            if (returnClass == null) {
                continue;
            }
            
            // 檢查是否有 build 方法
            boolean hasBuildMethod = false;
            for (PsiMethod returnClassMethod : returnClass.getAllMethods()) {
                if ("build".equals(returnClassMethod.getName())) {
                    hasBuildMethod = true;
                    break;
                }
            }
            
            if (hasBuildMethod) {
                return method;
            }
        }
        
        return null;
    }
}
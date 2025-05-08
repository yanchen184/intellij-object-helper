package com.yanchen.plugin.objecthelper.generator.format;

import com.intellij.psi.*;
import com.yanchen.plugin.objecthelper.common.util.PsiUtils;
import com.yanchen.plugin.objecthelper.generator.Generator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Java 類轉 JSON 生成器
 */
public class ClassToJsonGenerator implements Generator {

    /**
     * 目標類
     */
    private final PsiClass psiClass;
    
    /**
     * 縮進空格數
     */
    private static final int INDENT_SPACES = 4;
    
    /**
     * 獲取 ClassToJsonGenerator 實例
     *
     * @param psiClass 目標類
     * @return 生成器實例
     */
    public static Generator getInstance(PsiClass psiClass) {
        return new ClassToJsonGenerator(psiClass);
    }
    
    /**
     * 構造方法
     *
     * @param psiClass 目標類
     */
    private ClassToJsonGenerator(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    /**
     * 生成 JSON
     *
     * @return JSON 字符串
     */
    @Override
    public String generate() {
        if (psiClass == null) {
            return null;
        }
        
        return generateClassJson(psiClass, 0);
    }
    
    /**
     * 生成類的 JSON 表示
     *
     * @param psiClass 目標類
     * @param indent 縮進級別
     * @return JSON 字符串
     */
    @NotNull
    private String generateClassJson(PsiClass psiClass, int indent) {
        StringBuilder json = new StringBuilder();
        String indentStr = getIndentString(indent);
        String innerIndentStr = getIndentString(indent + 1);
        
        json.append(indentStr).append("{\n");
        
        List<PsiField> fields = PsiUtils.getAllFields(psiClass);
        for (int i = 0; i < fields.size(); i++) {
            PsiField field = fields.get(i);
            
            // 排除靜態字段和常量
            if (field.hasModifierProperty(PsiModifier.STATIC) || 
                    field.hasModifierProperty(PsiModifier.FINAL) && field.hasInitializer()) {
                continue;
            }
            
            String fieldName = field.getName();
            PsiType fieldType = field.getType();
            
            json.append(innerIndentStr).append("\"").append(fieldName).append("\": ");
            
            // 根據字段類型生成適當的 JSON 值
            json.append(generateValueForType(fieldType, indent + 1));
            
            // 如果不是最後一個字段，添加逗號
            if (i < fields.size() - 1) {
                json.append(",");
            }
            json.append("\n");
        }
        
        json.append(indentStr).append("}");
        return json.toString();
    }
    
    /**
     * 根據類型生成 JSON 值
     *
     * @param type 字段類型
     * @param indent 縮進級別
     * @return JSON 值
     */
    @NotNull
    private String generateValueForType(PsiType type, int indent) {
        // 處理原始類型
        if (type.equals(PsiTypes.intType()) || 
                type.equals(PsiTypes.longType()) ||
                type.equals(PsiTypes.floatType()) ||
                type.equals(PsiTypes.doubleType()) ||
                type.equals(PsiTypes.byteType()) ||
                type.equals(PsiTypes.shortType())) {
            return "0";
        }
        
        if (type.equals(PsiTypes.booleanType())) {
            return "false";
        }
        
        // 處理包裝類型
        String typeText = type.getCanonicalText();
        if ("java.lang.Integer".equals(typeText) || 
                "java.lang.Long".equals(typeText) ||
                "java.lang.Float".equals(typeText) ||
                "java.lang.Double".equals(typeText) ||
                "java.lang.Byte".equals(typeText) ||
                "java.lang.Short".equals(typeText)) {
            return "0";
        }
        
        if ("java.lang.Boolean".equals(typeText)) {
            return "false";
        }
        
        // 處理字符串類型
        if (type.equals(PsiTypes.charType()) || "java.lang.Character".equals(typeText) || 
                "java.lang.String".equals(typeText)) {
            return "\"value\"";
        }
        
        // 處理集合類型
        if (typeText.startsWith("java.util.List") || 
                typeText.startsWith("java.util.ArrayList") ||
                typeText.startsWith("java.util.Set") ||
                typeText.startsWith("java.util.HashSet") ||
                typeText.startsWith("java.util.Collection")) {
            return "[]";
        }
        
        // 處理映射類型
        if (typeText.startsWith("java.util.Map") || 
                typeText.startsWith("java.util.HashMap") ||
                typeText.startsWith("java.util.TreeMap")) {
            return "{}";
        }
        
        // 處理數組類型
        if (type instanceof PsiArrayType) {
            return "[]";
        }
        
        // 嘗試獲取自定義類型
        if (type instanceof PsiClassType) {
            PsiClass resolvedClass = ((PsiClassType) type).resolve();
            if (resolvedClass != null && !resolvedClass.equals(psiClass)) {
                // 避免循環引用
                if (isJavaOrKotlinCoreClass(resolvedClass.getQualifiedName())) {
                    return "{}";
                } else {
                    return generateClassJson(resolvedClass, indent);
                }
            }
        }
        
        // 默認情況
        return "null";
    }
    
    /**
     * 獲取指定縮進級別的空格字符串
     *
     * @param indent 縮進級別
     * @return 縮進字符串
     */
    @NotNull
    private String getIndentString(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent * INDENT_SPACES; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
    
    /**
     * 檢查是否為 Java 或 Kotlin 核心類
     *
     * @param qualifiedName 完全限定名
     * @return 是否為核心類
     */
    private boolean isJavaOrKotlinCoreClass(String qualifiedName) {
        if (qualifiedName == null) {
            return false;
        }
        
        return qualifiedName.startsWith("java.") 
                || qualifiedName.startsWith("javax.") 
                || qualifiedName.startsWith("kotlin.");
    }
}
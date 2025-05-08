package com.yanchen.plugin.objecthelper.generator.format;

import com.intellij.psi.*;
import com.yanchen.plugin.objecthelper.common.util.PsiUtils;
import com.yanchen.plugin.objecthelper.common.util.StringUtils;
import com.yanchen.plugin.objecthelper.generator.Generator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Java 類轉 XML 生成器
 */
public class ClassToXMLGenerator implements Generator {

    /**
     * 目標類
     */
    private final PsiClass psiClass;
    
    /**
     * 縮進空格數
     */
    private static final int INDENT_SPACES = 4;
    
    /**
     * 獲取 ClassToXMLGenerator 實例
     *
     * @param psiClass 目標類
     * @return 生成器實例
     */
    public static Generator getInstance(PsiClass psiClass) {
        return new ClassToXMLGenerator(psiClass);
    }
    
    /**
     * 構造方法
     *
     * @param psiClass 目標類
     */
    private ClassToXMLGenerator(PsiClass psiClass) {
        this.psiClass = psiClass;
    }

    /**
     * 生成 XML
     *
     * @return XML 字符串
     */
    @Override
    public String generate() {
        if (psiClass == null) {
            return null;
        }
        
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        
        String className = psiClass.getName();
        if (className == null) {
            className = "root";
        }
        
        // 將類名轉為小寫，作為 XML 根節點
        String rootNodeName = StringUtils.uncapitalize(className);
        
        xml.append(generateClassXml(psiClass, rootNodeName, 0));
        
        return xml.toString();
    }
    
    /**
     * 生成類的 XML 表示
     *
     * @param psiClass 目標類
     * @param nodeName 節點名稱
     * @param indent 縮進級別
     * @return XML 字符串
     */
    @NotNull
    private String generateClassXml(PsiClass psiClass, String nodeName, int indent) {
        StringBuilder xml = new StringBuilder();
        String indentStr = getIndentString(indent);
        String innerIndentStr = getIndentString(indent + 1);
        
        // 開始標籤
        xml.append(indentStr).append("<").append(nodeName).append(">\n");
        
        List<PsiField> fields = PsiUtils.getAllFields(psiClass);
        for (PsiField field : fields) {
            // 排除靜態字段和常量
            if (field.hasModifierProperty(PsiModifier.STATIC) || 
                    field.hasModifierProperty(PsiModifier.FINAL) && field.hasInitializer()) {
                continue;
            }
            
            String fieldName = field.getName();
            PsiType fieldType = field.getType();
            
            xml.append(generateNodeForField(field, fieldName, fieldType, indent + 1));
        }
        
        // 結束標籤
        xml.append(indentStr).append("</").append(nodeName).append(">\n");
        
        return xml.toString();
    }
    
    /**
     * 為字段生成 XML 節點
     *
     * @param field 字段
     * @param fieldName 字段名
     * @param fieldType 字段類型
     * @param indent 縮進級別
     * @return XML 節點
     */
    @NotNull
    private String generateNodeForField(PsiField field, String fieldName, PsiType fieldType, int indent) {
        StringBuilder xml = new StringBuilder();
        String indentStr = getIndentString(indent);
        
        // 處理集合類型
        String typeText = fieldType.getCanonicalText();
        if (typeText.startsWith("java.util.List") || 
                typeText.startsWith("java.util.ArrayList") ||
                typeText.startsWith("java.util.Set") ||
                typeText.startsWith("java.util.HashSet") ||
                typeText.startsWith("java.util.Collection")) {
            // 集合類型，使用複數形式的標籤名
            String containerName = fieldName;
            String itemName = getSingularName(fieldName);
            
            xml.append(indentStr).append("<").append(containerName).append(">\n");
            xml.append(indentStr).append(getIndentString(1)).append("<").append(itemName).append("></").append(itemName).append(">\n");
            xml.append(indentStr).append("</").append(containerName).append(">\n");
            
            return xml.toString();
        }
        
        // 處理數組類型
        if (fieldType instanceof PsiArrayType) {
            String containerName = fieldName;
            String itemName = getSingularName(fieldName);
            
            xml.append(indentStr).append("<").append(containerName).append(">\n");
            xml.append(indentStr).append(getIndentString(1)).append("<").append(itemName).append("></").append(itemName).append(">\n");
            xml.append(indentStr).append("</").append(containerName).append(">\n");
            
            return xml.toString();
        }
        
        // 處理映射類型
        if (typeText.startsWith("java.util.Map") || 
                typeText.startsWith("java.util.HashMap") ||
                typeText.startsWith("java.util.TreeMap")) {
            xml.append(indentStr).append("<").append(fieldName).append(">\n");
            xml.append(indentStr).append(getIndentString(1)).append("<entry>\n");
            xml.append(indentStr).append(getIndentString(2)).append("<key></key>\n");
            xml.append(indentStr).append(getIndentString(2)).append("<value></value>\n");
            xml.append(indentStr).append(getIndentString(1)).append("</entry>\n");
            xml.append(indentStr).append("</").append(fieldName).append(">\n");
            
            return xml.toString();
        }
        
        // 處理自定義類型
        if (fieldType instanceof PsiClassType) {
            PsiClass resolvedClass = ((PsiClassType) fieldType).resolve();
            if (resolvedClass != null && !resolvedClass.equals(psiClass)) {
                // 避免循環引用
                if (!isJavaOrKotlinCoreClass(resolvedClass.getQualifiedName())) {
                    return generateClassXml(resolvedClass, fieldName, indent);
                }
            }
        }
        
        // 簡單類型
        xml.append(indentStr).append("<").append(fieldName).append(">")
                .append(generateValueForType(fieldType))
                .append("</").append(fieldName).append(">\n");
        
        return xml.toString();
    }
    
    /**
     * 根據類型生成 XML 值
     *
     * @param type 字段類型
     * @return XML 值
     */
    @NotNull
    private String generateValueForType(PsiType type) {
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
            return "value";
        }
        
        // 默認情況
        return "";
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
    
    /**
     * 獲取單數形式的名稱
     * 簡單處理，去掉結尾的 's'
     *
     * @param name 名稱
     * @return 單數形式
     */
    @NotNull
    private String getSingularName(String name) {
        if (name == null || name.isEmpty()) {
            return "item";
        }
        
        // 簡單處理，去掉結尾的 's'
        if (name.endsWith("s") && name.length() > 1) {
            return name.substring(0, name.length() - 1);
        }
        
        return name + "Item";
    }
}
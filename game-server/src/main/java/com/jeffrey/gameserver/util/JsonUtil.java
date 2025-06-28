package com.jeffrey.gameserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON工具类
 * 提供JSON序列化和反序列化功能
 * 
 * @author jeffrey
 */
public class JsonUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    static {
        // 配置ObjectMapper
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
    
    /**
     * 将对象转换为JSON字符串
     * 
     * @param object 要转换的对象
     * @return JSON字符串
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert object to JSON: {}", object, e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
    
    /**
     * 将JSON字符串转换为对象
     * 
     * @param json JSON字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 转换后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert JSON to object: json={}, class={}", json, clazz, e);
            throw new RuntimeException("JSON deserialization failed", e);
        }
    }
    
    /**
     * 将对象转换为格式化的JSON字符串
     * 
     * @param object 要转换的对象
     * @return 格式化的JSON字符串
     */
    public static String toPrettyJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to convert object to pretty JSON: {}", object, e);
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
    
    /**
     * 检查字符串是否为有效的JSON
     * 
     * @param json 要检查的字符串
     * @return 是否为有效JSON
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
    
    /**
     * 获取ObjectMapper实例
     * 
     * @return ObjectMapper实例
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}

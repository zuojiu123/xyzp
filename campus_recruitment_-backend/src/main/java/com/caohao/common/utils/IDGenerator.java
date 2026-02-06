package com.caohao.common.utils;

import java.util.Random;

/**
 * ID生成器封装
 */
public class IDGenerator {

    private static final IdWorker idWorker = new IdWorker(0, 0);

    /**
     * 字符串类型ID
     *
     * @return
     */
    public static String StringID() {
        return generateSequentialId("G");
    }
    
    /**
     * 生成用户ID
     */
    public static String generateUserId() {
        return generateSequentialId("U");
    }
    
    /**
     * 生成公司ID
     */
    public static String generateCompanyId() {
        return generateSequentialId("C");
    }
    
    /**
     * 生成职位ID
     */
    public static String generateEmploymentId() {
        return generateSequentialId("E");
    }
    
    /**
     * 生成文章ID
     */
    public static String generateArticleId() {
        return generateSequentialId("A");
    }
    
    /**
     * 生成反馈ID
     */
    public static String generateFeedbackId() {
        return generateSequentialId("F");
    }
    
    /**
     * 生成序列ID
     */
    private static String generateSequentialId(String prefix) {
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(999) + 1;
        return prefix + String.format("%03d", random % 1000);
    }

    /**
     * long类型ID
     *
     * @return
     */
    public static long LongID() {
        long longID = idWorker.nextId();
        return longID;
    }

    /**
     * 六位验证码
     *
     * @return
     */
    public static String authCode() {
        //生成6位随机数字
        int anInt = new Random().nextInt(999999);
        String authCode = String.valueOf(anInt);
        return authCode;
    }

}

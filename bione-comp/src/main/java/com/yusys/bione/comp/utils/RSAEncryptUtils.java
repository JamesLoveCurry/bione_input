package com.yusys.bione.comp.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class RSAEncryptUtils {
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
    private static Logger log = LoggerFactory.getLogger(RSAEncryptUtils.class);
    
    // 公钥与私钥先保存在常量中,后续根据实际使用的反馈来优化
    /**
     * 公钥
     **/
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLFVCZEkcznSlb4ZSI6ra2uXk8HHMXG3e2WZ/6i8Q51rq7uJ5PwbuHVhK4KS3G/dhrzNdFzsuejA30eRNExzuabX0ErJeqVVocLbHb5YpAwR7QT/j/4atE/8Z5ScTxP9ky3I+xwjjC8uI7I31EUeWTfY8W1qo1JlY/8tZupvOaowIDAQAB";

    /**
     * 私钥
     **/
    private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIsVUJkSRzOdKVvhlIjqtra5eTwccxcbd7ZZn/qLxDnWuru4nk/Bu4dWErgpLcb92GvM10XOy56MDfR5E0THO5ptfQSsl6pVWhwtsdvlikDBHtBP+P/hq0T/xnlJxPE/2TLcj7HCOMLy4jsjfURR5ZN9jxbWqjUmVj/y1m6m85qjAgMBAAECgYBaHhV7GTzJVKvXvPJhRLPe8Y58RHLOJh4LKUdfL/CVCW009Qqe4wh/SGYYrZXbud7g+nkB4lxM/rKMFWdGyVD0ZTTU3lv4toE/KqNaVqhzHJ7ZK980lDi9FOMrq3ECEC2Anadv4OpTwGEAniu3gW+/oIfsPrZop2iBCkNYVS3WAQJBAObseJA6WHdQTArqOlQzZAZ0/2k4J16U8lXhjv2UTluWsJsgsLO8xYUo4p4OK2kQ0VoMSBOfYBA3pAeHh61kKwkCQQCaL70wU4juFjjRWpp3R165Zccw4Q9gSC8Vu6C1WXQBfT8/Ee/WMAyavcbf1l6qebIHTLbM3XPepi2mmoKCr8dLAkEAwxbOOFMCgDJXO/VsN8x+2bGoQfVN1WsSXJnFmtfbZyckprn0brSxTnEfGxaveADcTS4JCoLfvnOzYSUP692vOQJAA1Cdb6mMGxpNNGsPQRs+tRu0EZ/1FavXn07+YIfAz7tinbeEAYXc74K/ANb6CA4/vppun67vK81E0/0kayzeSwJBAIwKZz4BdiiRigppy7RsrTZUJYXLTYlXhw+aU+KZuatSNbmCLnLlZhWnTt2wbkkUEv0sMG9GcHFZ9BCTjrOw5FY=";


    public static void main(String[] args) throws Exception {
        dataSourceDecrypt("Mgx1nAHxaBLOmD7or9hF6nz3HzqzdHlt1btQLnionX38nVH+l6a0V3tyZYry1f+R2eTy12EyTZXS/Z45qGDdFyVKq4amx/PVJp3TiseAIrveXLyx7+65CMWrNAGqHXM3N1YqGHJo9g8Q1zrrlpQV7yFLJ2qYC/DFsDzzVDX/eDM=");
        //生成公钥和私钥
        genKeyPair();
        //加密字符串
        String message = "yusys123";
        // System.out.println("随机生成的公钥为:" + keyMap.get(0));
        // System.out.println("随机生成的私钥为:" + keyMap.get(1));
        Properties properties = new Properties();

        BufferedReader bufferedReader = new BufferedReader(new FileReader("h://RSA_DATA.properties"));
        properties.load(bufferedReader);
        // 获取key对应的value值
        String publicKey = properties.getProperty("PUBLICK_KEY");
        String privateKey = properties.getProperty("PRIVATE_KEY");
        System.out.println("公钥:" + publicKey);
        System.out.println("私钥:" + privateKey);
        String messageEn = encrypt(message,publicKey);
        System.out.println(message + "\t加密后的字符串为:" + messageEn);
        String messageDe = decrypt(messageEn,privateKey);
        System.out.println("还原后的字符串为:" + messageDe);
    }

    /**
     * 随机生成密钥对
     * @throws NoSuchAlgorithmException
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024,new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        // 将公钥和私钥保存到Map
        keyMap.put(0,publicKeyString);  //0表示公钥
        keyMap.put(1,privateKeyString);  //1表示私钥
    }
    /**
     * RSA公钥加密
     *
     * @param str
     *            加密字符串
     * @param publicKey
     *            公钥
     * @return 密文
     * @throws Exception
     *             加密过程中的异常信息
     */
    public static String encrypt( String str, String publicKey ) throws Exception{
        //base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str
     *            加密字符串
     * @param privateKey
     *            私钥
     * @return 铭文
     * @throws Exception
     *             解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception{
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
        //base64编码的私钥
        byte[] decoded = Base64.decodeBase64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        String outStr = new String(cipher.doFinal(inputByte));
        return outStr;
    }

    /**
     * 使用私钥分段解密
     * 
     * @param text
     * @return java.lang.String
     * @Date 2021/10/14 10:52       
     * @author baifk
     **/
    public static String decryptSegmentedByPrivateKey(final String text) throws Exception {
        byte[] data = null;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.decodeBase64(PRIVATE_KEY));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            JSONArray list = JSONObject.parseObject(text).getJSONArray("value");
            list.forEach(value -> {
                try {
                    byte[] cache = cipher.doFinal(Base64.decodeBase64(value.toString()));
                    out.write(cache, 0, cache.length);
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    log.error("使用私钥解密出错 {}", e);
                }
            });
            data = out.toByteArray();
        }
        return new String(data);
    }
    
    /**
     * 数据库启动加载数据源时调用此方法进行解密
     * 一般先生成公钥和私钥，使用公钥手动加密数据库密码，私钥存放于服务器指定目录下，不存在于程序中，相对比较安全。
     * @param encryptCode
     * @return
     */
    public static String dataSourceDecrypt (String encryptCode) {
        String decryptCode = "";
        try {
            // 获取存放私钥的配置文件地址
            PropertiesUtils pUtils = PropertiesUtils.get("database.properties");
            String keyPath = pUtils.getProperty("privatekey.path");
            Properties properties = new Properties();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(keyPath));
            properties.load(bufferedReader);
            // String publicKey = properties.getProperty("PUBLICK_KEY");
            // 获取私钥
            String privateKey = properties.getProperty("PRIVATE_KEY");
            System.out.println("私钥为：" + privateKey);
            // 解密
            decryptCode = decrypt(encryptCode,privateKey);
            System.out.println("解密后的字符串为:" + decryptCode);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return decryptCode;
    }

}

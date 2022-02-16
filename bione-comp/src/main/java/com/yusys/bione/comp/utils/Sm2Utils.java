package com.yusys.bione.comp.utils;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.*;

/**
 * @项目名称：sm2加密工具类
 * @类名称： Sm2Utils
 * @类描述:
 * @功能描述:
 * @创建人: miaokx@yusys.com.cn
 * @创建时间: 2021年04月27日 16:48
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
public class Sm2Utils {
    private final static Digest DIGEST = new SM3Digest();

    /**
     * 私钥转换为 {@link ECPrivateKeyParameters}
     * @param key key
     * @return
     * @throws InvalidKeyException
     */
    public static ECPrivateKeyParameters privateKeyToParams(String algorithm, byte[] key) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
        Preconditions.checkNotNull(key, "key must be not null !");
        PrivateKey privateKey = generatePrivateKey(algorithm, key);
        return (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter(privateKey);
    }

    /**
     * 生成私钥
     * @param algorithm 算法
     * @param key       key
     * @return
     */
    public static PrivateKey generatePrivateKey(String algorithm, byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Preconditions.checkNotNull(algorithm, "algorithm must be not null !");
        Preconditions.checkNotNull(key, "key must be not null !");
        KeySpec keySpec = new PKCS8EncodedKeySpec(key);
        algorithm = getAlgorithmAfterWith(algorithm);
        return getKeyFactory(algorithm).generatePrivate(keySpec);
    }

    /**
     * 公钥转换为 {@link ECPublicKeyParameters}
     * @param key key
     * @return
     * @throws InvalidKeyException
     */
    public static ECPublicKeyParameters publicKeyToParams(String algorithm, byte[] key) throws InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
        Preconditions.checkNotNull(key, "key must be not null !");
        PublicKey publicKey = generatePublicKey(algorithm, key);
        return (ECPublicKeyParameters) ECUtil.generatePublicKeyParameter(publicKey);
    }

    /**
     * 生成公钥
     * @param algorithm 算法
     * @param key       key
     * @return
     */
    public static PublicKey generatePublicKey(String algorithm, byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Preconditions.checkNotNull(algorithm, "algorithm must be not null !");
        Preconditions.checkNotNull(key, "key must be not null !");
        KeySpec keySpec = new X509EncodedKeySpec(key);
        algorithm = getAlgorithmAfterWith(algorithm);
        return getKeyFactory(algorithm).generatePublic(keySpec);
    }

    /**
     * 获取用于密钥生成的算法<br>
     * 获取XXXwithXXX算法的后半部分算法，如果为ECDSA或SM2，返回算法为EC
     * @param algorithm XXXwithXXX算法
     * @return 算法
     */
    private static String getAlgorithmAfterWith(String algorithm) {
        Preconditions.checkNotNull(algorithm, "algorithm must be not null !");
        int indexOfWith = StringUtils.lastIndexOfIgnoreCase(algorithm, "with");
        if (indexOfWith > 0) {
            algorithm = StringUtils.substring(algorithm, indexOfWith + "with".length());
        }
        if ("ECDSA".equalsIgnoreCase(algorithm) || "SM2".equalsIgnoreCase(algorithm)) {
            algorithm = "EC";
        }
        return algorithm;
    }

    /**
     * 获取{@link KeyFactory}
     * @param algorithm 非对称加密算法
     * @return {@link KeyFactory}
     */
    private static KeyFactory getKeyFactory(String algorithm) throws NoSuchAlgorithmException {
        final Provider provider = new BouncyCastleProvider();
        return KeyFactory.getInstance(algorithm, provider);
    }

    /**
     * 加密
     * @param data      数据
     * @param publicKey 公钥
     * @return 加密之后的数据
     */
    public static byte[] encrypt(byte[] data, byte[] publicKey) throws Exception {
        CipherParameters pubKeyParameters = new ParametersWithRandom(publicKeyToParams("SM2", publicKey));
        SM2Engine engine = new SM2Engine(DIGEST);
        engine.init(true, pubKeyParameters);
        return engine.processBlock(data, 0, data.length);
    }

    /**
     * 解密
     * @param data       数据
     * @param privateKey 私钥
     * @return 解密之后的数据
     */
    public static byte[] decrypt(byte[] data, byte[] privateKey) throws Exception {
        CipherParameters privateKeyParameters = privateKeyToParams("SM2", privateKey);
        SM2Engine engine = new SM2Engine(DIGEST);
        engine.init(false, privateKeyParameters);
        byte[] byteDate = engine.processBlock(data, 0, data.length);
        return byteDate;
    }

    /**
     * 签名
     * @param data 数据
     * @return 签名
     */
    public static byte[] sign(byte[] data, byte[] privateKey) throws Exception {
        SM2Signer signer = new SM2Signer();
        CipherParameters param = new ParametersWithRandom(privateKeyToParams("SM2", privateKey));
        signer.init(true, param);
        signer.update(data, 0, data.length);
        return signer.generateSignature();
    }

    /**
     * 用公钥检验数字签名的合法性
     * @param data      数据
     * @param sign      签名
     * @param publicKey 公钥
     * @return 是否验证通过
     */
    public static boolean verify(byte[] data, byte[] sign, byte[] publicKey) throws Exception {
        SM2Signer signer = new SM2Signer();
        CipherParameters param = publicKeyToParams("SM2", publicKey);
        signer.init(false, param);
        signer.update(data, 0, data.length);
        return signer.verifySignature(sign);
    }


    /**
     * SM2算法生成密钥对
     * @return 密钥对信息
     */
    public static KeyPair generateSm2KeyPair() {
        try {
            final ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
            // 获取一个椭圆曲线类型的密钥对生成器
            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
            SecureRandom random = new SecureRandom();
            // 使用SM2的算法区域初始化密钥生成器
            kpg.initialize(sm2Spec, random);
            // 获取密钥对
            KeyPair keyPair = kpg.generateKeyPair();
            return keyPair;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

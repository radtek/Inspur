package base.util;

import java.io.FileInputStream;  
import java.io.FileNotFoundException;  
import java.io.IOException;  
import java.security.InvalidAlgorithmParameterException;  
import java.security.InvalidKeyException;  
import java.security.Key;  
import java.security.KeyFactory;  
import java.security.KeyPair;  
import java.security.KeyPairGenerator;  
import java.security.KeyStore;  
import java.security.KeyStoreException;  
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
import java.security.PrivateKey;  
import java.security.PublicKey;  
import java.security.SecureRandom;  
import java.security.Signature;  
import java.security.SignatureException;  
import java.security.UnrecoverableKeyException;  
import java.security.cert.Certificate;  
import java.security.cert.CertificateException;  
import java.security.cert.CertificateFactory;  
import java.security.cert.X509Certificate;  
import java.security.interfaces.RSAPrivateKey;  
import java.security.interfaces.RSAPublicKey;  
import java.security.spec.AlgorithmParameterSpec;  
import java.security.spec.InvalidKeySpecException;  
import java.security.spec.PKCS8EncodedKeySpec;  
import java.security.spec.X509EncodedKeySpec;  
import java.util.Date;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.Random;  
  
import javax.crypto.BadPaddingException;  
import javax.crypto.Cipher;  
import javax.crypto.IllegalBlockSizeException;  
import javax.crypto.KeyAgreement;  
import javax.crypto.KeyGenerator;  
import javax.crypto.Mac;  
import javax.crypto.NoSuchPaddingException;  
import javax.crypto.SecretKey;  
import javax.crypto.SecretKeyFactory;  
import javax.crypto.interfaces.DHPrivateKey;  
import javax.crypto.interfaces.DHPublicKey;  
import javax.crypto.spec.DHParameterSpec;  
import javax.crypto.spec.IvParameterSpec;  
import javax.crypto.spec.PBEKeySpec;  
import javax.crypto.spec.PBEParameterSpec;  
import javax.crypto.spec.SecretKeySpec;  
  
public class CipherUtil {  
  
    /** 
     * MD5?????? 
     */  
    private static final String ALGORITHM_MD5 = "MD5";  
    /** 
     * SHA?????? 
     */  
    private static final String ALGORITHM_SHA = "SHA";  
    /** 
     * HMAC?????? 
     */  
    private static final String ALGORITHM_MAC = "HmacMD5";  
    /** 
     * DES?????? 
     */  
    private static final String ALGORITHM_DES = "DES";  
    /** 
     * PBE?????? 
     */  
    private static final String ALGORITHM_PBE = "PBEWITHMD5andDES";  
  
    /** 
     * AESkey 
     */  
    private static final String KEY_AES = "AES";  
  
    /** 
     * AES?????? 
     */  
    private static final String ALGORITHM_AES = "AES/CBC/PKCS5Padding";  
  
    /** 
     * RSA?????? 
     */  
    private static final String KEY_ALGORITHM = "RSA";  
  
    /** 
     * ???????????? 
     */  
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";  
  
    /** 
     * ?????? 
     */  
    private static final String RSAPUBLIC_KEY = "RSAPublicKey";  
  
    /** 
     * ?????? 
     */  
    private static final String RSAPRIVATE_KEY = "RSAPrivateKey";  
  
    /** 
     * D-H?????? 
     */  
    private static final String ALGORITHM_DH = "DH";  
  
    /** 
     * ????????????????????? 
     * 
     * <pre> 
     * DH 
     * Default Keysize 1024 
     * Keysize must be a multiple of 64, ranging from 512 to 1024 (inclusive). 
     * </pre> 
     */  
    private static final int DH_KEY_SIZE = 1024;  
  
    /** 
     * DH???????????????????????????????????????????????????????????????????????????DES????????????????????????????????????????????? 
     */  
    private static final String SECRET_ALGORITHM = "DES";  
  
    /** 
     * DH?????? 
     */  
    private static final String DHPUBLIC_KEY = "DHPublicKey";  
  
    /** 
     * DH?????? 
     */  
    private static final String DHPRIVATE_KEY = "DHPrivateKey";  
  
    /** 
     * Java?????????(Java Key Store???JKS)KEY_STORE 
     */  
    private static final String KEY_STORE = "JKS";  
  
    private static final String X509 = "X.509";  
  
    /** 
     * ?????????????????? 
     * @param algorithm ???????????? 
     * @param data ????????????????????? 
     * @return ?????????????????????????????? 
     */  
    private static String encryptEncode(String algorithm, String data) {  
        try {  
            MessageDigest md = MessageDigest.getInstance(algorithm);  
            return TranscodeUtil.byteArrayToHexStr(md.digest(data.getBytes()));  
        } catch(NoSuchAlgorithmException ex) {  
            ex.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ??????MD5?????? 
     * @param data ????????????????????? 
     * @return ???????????????????????? 
     */  
    public static String MD5Encode(String data) {  
        return encryptEncode(ALGORITHM_MD5, data);  
    }  
  
    /** 
     * ??????SHA?????? 
     * @param data ????????????????????? 
     * @return ???????????????????????? 
     */  
    public static String SHAEncode(String data) {  
        return encryptEncode(ALGORITHM_SHA, data);  
    }  
  
    /** 
     * ??????HMAC?????? 
     * @return ?????????????????? 
     */  
    public static String generateMACKey() {  
        try {  
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_MAC);  
            SecretKey secretKey = keyGenerator.generateKey();  
            return TranscodeUtil.byteArrayToBase64Str(secretKey.getEncoded());  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ??????HMAC?????? 
     * @param data ????????????????????? 
     * @param key ?????? 
     * @return ???????????????????????? 
     */  
    public static String HMACEncode(String data, String key) {  
        Key k = toKey(key,ALGORITHM_MAC);  
        try {  
            Mac mac = Mac.getInstance(k.getAlgorithm());  
            mac.init(k);  
            return TranscodeUtil.byteArrayToBase64Str(mac.doFinal(data.getBytes()));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???base64???????????????????????????????????????????????? 
     * @param key ??????????????? 
     * @param algorithm ???????????? 
     * @return ?????????????????? 
     */  
    private static Key toKey(String key,String algorithm) {  
        SecretKey secretKey = new SecretKeySpec(TranscodeUtil.base64StrToByteArray(key), algorithm);  
        return secretKey;  
    }  
  
    /** 
     * ??????DES?????? 
     * @param seed ???????????? 
     * @return ??????base64???????????????????????? 
     */  
    public static String generateDESKey(String seed) {  
        try {  
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_DES);  
            kg.init(new SecureRandom(seed.getBytes()));  
            SecretKey secretKey = kg.generateKey();  
            return TranscodeUtil.byteArrayToBase64Str(secretKey.getEncoded());  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * DES?????? 
     * @param data ?????????????????? 
     * @param key ?????? 
     * @return ????????????????????????(??????base64??????) 
     */  
    public static String DESEncrypt(String data,String key) {  
        return DESCipher(data,key,Cipher.ENCRYPT_MODE);  
    }  
  
    /** 
     * DES?????? 
     * @param data ?????????????????? 
     * @param key ?????? 
     * @return ???????????????????????? 
     */  
    public static String DESDecrypt(String data, String key) {  
        return DESCipher(data,key,Cipher.DECRYPT_MODE);  
    }  
  
    /** 
     * DES??????????????? 
     * @param data ??????????????????????????? 
     * @param key ?????? 
     * @param mode ????????????????????? 
     * @return ?????????????????????????????? 
     */  
    private static String DESCipher(String data, String key, int mode) {  
        try {  
            Key k = toKey(key,ALGORITHM_DES);  
            Cipher cipher = Cipher.getInstance(ALGORITHM_DES);  
            cipher.init(mode, k);  
            return mode == Cipher.DECRYPT_MODE?new String(cipher.doFinal(TranscodeUtil.base64StrToByteArray(data))):TranscodeUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ????????? 
     * @return ??????base64????????????????????? 
     */  
    public static String generatePBESalt() {  
        byte[] salt = new byte[8];  
        Random random = new Random();  
        random.nextBytes(salt);  
        return TranscodeUtil.byteArrayToBase64Str(salt);  
    }  
  
    /** 
     * PBE(Password-based encryption??????????????????)?????? 
     * @param data ?????????????????? 
     * @param password ?????? 
     * @param salt ??? 
     * @return ????????????????????????(??????base64??????) 
     */  
    public static String PBEEncrypt(String data,String password,String salt) {  
        return PBECipher( data, password, salt, Cipher.ENCRYPT_MODE);  
    }  
  
    /** 
     * PBE(Password-based encryption??????????????????)?????? 
     * @param data ?????????????????? 
     * @param password ?????? 
     * @param salt ??? 
     * @return ???????????????????????? 
     */  
    public static String PBEDecrypt(String data,String password,String salt) {  
        return PBECipher( data, password, salt, Cipher.DECRYPT_MODE);  
    }  
  
    /** 
     * PBE???????????? 
     * @param data ???????????????????????? 
     * @param password ?????? 
     * @param salt ??? 
     * @param mode ????????????????????? 
     * @return ?????????????????????????????? 
     */  
    private static String PBECipher(String data,String password,String salt,int mode) {  
        try {  
            Key secretKey = toPBEKey(password);  
            PBEParameterSpec paramSpec = new PBEParameterSpec(TranscodeUtil.base64StrToByteArray(salt), 100);  
            Cipher cipher = Cipher.getInstance(ALGORITHM_PBE);  
            cipher.init(mode, secretKey, paramSpec);  
            return mode == Cipher.DECRYPT_MODE?new String(cipher.doFinal(TranscodeUtil.base64StrToByteArray(data))):TranscodeUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (InvalidAlgorithmParameterException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ??????PBEkey 
     * @param password ??????????????? 
     * @return ???????????????PBEkey 
     */  
    private static Key toPBEKey(String password) {  
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());  
        try {  
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_PBE);  
            SecretKey secretKey = keyFactory.generateSecret(keySpec);  
            return secretKey;  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ??????AESkey 
     * @param keySize key????????? 
     * @param seed ???????????? 
     * @return ??????base64????????????key?????? 
     */  
    public static String generateAESKey(int keySize,String seed) {  
        try {  
            KeyGenerator kgen = KeyGenerator.getInstance(KEY_AES);  
            kgen.init(keySize,new SecureRandom(seed.getBytes()));  
            SecretKey key = kgen.generateKey();  
            return TranscodeUtil.byteArrayToBase64Str(key.getEncoded());  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * AES?????? 
     * @param data ?????????????????? 
     * @param key ?????? 
     * @param algorithmParameter ???????????? 
     * @return ?????????????????? 
     */  
    public static String AESEncrypt(String data,String key,String algorithmParameter) {  
        return AESCipher(data, key, algorithmParameter,Cipher.ENCRYPT_MODE);  
    }  
  
    /** 
     * AES?????? 
     * @param data ?????????????????? 
     * @param key ?????? 
     * @param algorithmParameter ???????????? 
     * @return ?????????????????? 
     */  
    public static String AESDecrypt(String data,String key,String algorithmParameter) {  
        return AESCipher(data, key, algorithmParameter,Cipher.DECRYPT_MODE);  
    }  
  
    /** 
     * ??????AES???????????? 
     * @param data ??????????????????????????? 
     * @param key ?????? 
     * @param algorithmParameter ???????????? 
     * @param mode ??????????????? 
     * @return ?????????????????????????????? 
     */  
    private static String AESCipher(String data, String key, String algorithmParameter,int mode) {  
        try {  
            Key k = toKey(key,KEY_AES);  
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(algorithmParameter.getBytes());  
            Cipher ecipher = Cipher.getInstance(ALGORITHM_AES);  
            ecipher.init(mode, k, paramSpec);  
            return mode==Cipher.DECRYPT_MODE?new String(ecipher.doFinal(TranscodeUtil.base64StrToByteArray(data))):TranscodeUtil.byteArrayToBase64Str(ecipher.doFinal(data.getBytes()));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (InvalidAlgorithmParameterException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param privateKey ?????? 
     * @return ?????????????????? 
     */  
    public static String RSASign(String data, String privateKey) {  
        try {  
            // ?????????base64???????????????  
            byte[] keyBytes = TranscodeUtil.base64StrToByteArray(privateKey);  
            // ??????PKCS8EncodedKeySpec??????  
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
            // KEY_ALGORITHM ?????????????????????  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            // ??????????????????  
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);  
            // ????????????????????????????????????  
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
            signature.initSign(priKey);  
            signature.update(TranscodeUtil.base64StrToByteArray(data));  
            return TranscodeUtil.byteArrayToBase64Str(signature.sign());  
        } catch(NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (SignatureException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param publicKey ?????? 
     * @param sign ???????????? 
     * @return ???????????????????????? 
     */  
    public static boolean RSAVerify(String data, String publicKey, String sign) {  
        try {  
            // ?????????base64???????????????  
            byte[] keyBytes = TranscodeUtil.base64StrToByteArray(publicKey);  
            // ??????X509EncodedKeySpec??????  
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);  
            // KEY_ALGORITHM ?????????????????????  
            Signature signature;  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            // ??????????????????  
            PublicKey pubKey = keyFactory.generatePublic(keySpec);  
            signature = Signature.getInstance(SIGNATURE_ALGORITHM);  
            signature.initVerify(pubKey);  
            signature.update(TranscodeUtil.base64StrToByteArray(data));  
            // ????????????????????????  
            return signature.verify(TranscodeUtil.base64StrToByteArray(sign));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (SignatureException e) {  
            e.printStackTrace();  
        }  
        return false;  
    }  
  
    /** 
     * ???????????? 
     * @param data ????????????????????? 
     * @param key ?????? 
     * @return ??????????????????????????? 
     */  
    public static String RSADecryptByPrivateKey(String data, String key) {  
        try {  
            // ???????????????  
            byte[] keyBytes = TranscodeUtil.base64StrToByteArray(key);  
            // ????????????  
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);  
            // ???????????????  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
            return new String(cipher.doFinal(TranscodeUtil.base64StrToByteArray(data)));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param key ?????? 
     * @return ???????????????????????? 
     */  
    public static String RSADecryptByPublicKey(String data, String key) {  
        try {  
            // ???????????????  
            byte[] keyBytes = TranscodeUtil.base64StrToByteArray(key);  
            // ????????????  
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            Key publicKey = keyFactory.generatePublic(x509KeySpec);  
            // ???????????????  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            return new String(cipher.doFinal(TranscodeUtil.base64StrToByteArray(data)));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param key ?????? 
     * @return ????????????????????? 
     */  
    public static String RSAEncryptByPublicKey(String data, String key) {  
        try {  
            // ???????????????  
            byte[] keyBytes = TranscodeUtil.base64StrToByteArray(key);  
            // ????????????  
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            Key publicKey = keyFactory.generatePublic(x509KeySpec);  
            // ???????????????  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            return TranscodeUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param key ?????? 
     * @return ???????????????????????? 
     */  
    public static String RSAEncryptByPrivateKey(String data, String key) {  
        try {  
            // ???????????????  
            byte[] keyBytes = TranscodeUtil.base64StrToByteArray(key);  
            // ????????????  
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);  
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);  
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);  
            // ???????????????  
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
            return TranscodeUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param keyMap ????????? 
     * @return ????????????base64??????????????? 
     */  
    public static String getRSAPrivateKey(Map<String, Object> keyMap) {  
        Key key = (Key) keyMap.get(RSAPRIVATE_KEY);  
        return TranscodeUtil.byteArrayToBase64Str(key.getEncoded());  
    }  
  
    /** 
     * ????????????(base64??????) 
     * @param keyMap ????????? 
     * @return ????????????base64??????????????? 
     */  
    public static String getRSAPublicKey(Map<String, Object> keyMap) {  
        Key key = (Key) keyMap.get(RSAPUBLIC_KEY);  
        return TranscodeUtil.byteArrayToBase64Str(key.getEncoded());  
    }  
  
    /** 
     * ?????????????????? 
     * @return ??????????????? 
     */  
    public static Map<String, Object> initRSAKey() {  
        Map<String, Object> keyMap = new HashMap<String, Object>(2);  
        try {  
            KeyPairGenerator keyPairGen = KeyPairGenerator  
                                          .getInstance(KEY_ALGORITHM);  
            keyPairGen.initialize(1024);  
            KeyPair keyPair = keyPairGen.generateKeyPair();  
            // ??????  
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
            // ??????  
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
            keyMap.put(RSAPUBLIC_KEY, publicKey);  
            keyMap.put(RSAPRIVATE_KEY, privateKey);  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return keyMap;  
    }  
  
    /** 
     * ???????????????????????? 
     * @return ????????????????????? 
     */  
    public static Map<String, Object> initDHKey() {  
        try {  
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_DH);  
            keyPairGenerator.initialize(DH_KEY_SIZE);  
            KeyPair keyPair = keyPairGenerator.generateKeyPair();  
            // ????????????  
            DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();  
            // ????????????  
            DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();  
            Map<String, Object> keyMap = new HashMap<String, Object>(2);  
            keyMap.put(DHPUBLIC_KEY, publicKey);  
            keyMap.put(DHPRIVATE_KEY, privateKey);  
            return keyMap;  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ?????????????????????????????????????????? 
     * @param key ???????????? 
     * @return ????????????????????? 
     */  
    public static Map<String, Object> initDHKey(String key) {  
        try {  
            // ??????????????????  
            byte[] keyBytes = TranscodeUtil.base64StrToByteArray(key);  
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);  
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DH);  
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);  
            // ?????????????????????????????????  
            DHParameterSpec dhParamSpec = ((DHPublicKey) pubKey).getParams();  
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyFactory.getAlgorithm());  
            keyPairGenerator.initialize(dhParamSpec);  
            KeyPair keyPair = keyPairGenerator.generateKeyPair();  
            // ????????????  
            DHPublicKey publicKey = (DHPublicKey) keyPair.getPublic();  
            // ????????????  
            DHPrivateKey privateKey = (DHPrivateKey) keyPair.getPrivate();  
            Map<String, Object> keyMap = new HashMap<String, Object>(2);  
            keyMap.put(DHPUBLIC_KEY, publicKey);  
            keyMap.put(DHPRIVATE_KEY, privateKey);  
            return keyMap;  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        } catch (InvalidAlgorithmParameterException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * DH?????? 
     * @param data ?????????????????? 
     * @param publicKey ????????????????????? 
     * @param privateKey ????????????????????? 
     * @return ???????????? 
     */  
    public static String DHEncrypt(String data, String publicKey,String privateKey) {  
        try {  
            // ??????????????????  
            SecretKey secretKey = getDHSecretKey(publicKey, privateKey);  
            // ????????????  
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());  
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);  
            return TranscodeUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * DH?????? 
     * @param data ?????????????????? 
     * @param publicKey ?????? 
     * @param privateKey ?????? 
     * @return ?????????????????? 
     */  
    public static String DHDecrypt(String data, String publicKey,String privateKey) {  
        try {  
            // ??????????????????  
            SecretKey secretKey = getDHSecretKey(publicKey, privateKey);  
            // ????????????  
            Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());  
            cipher.init(Cipher.DECRYPT_MODE, secretKey);  
            return new String(cipher.doFinal(TranscodeUtil.base64StrToByteArray(data)));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ?????????????????? 
     * @param publicKey ?????? 
     * @param privateKey ?????? 
     * @return ?????????????????? 
     */  
    private static SecretKey getDHSecretKey(String publicKey, String privateKey) {  
        try {  
            // ???????????????  
            byte[] pubKeyBytes = TranscodeUtil.base64StrToByteArray(publicKey);  
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_DH);  
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKeyBytes);  
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);  
            // ???????????????  
            byte[] priKeyBytes = TranscodeUtil.base64StrToByteArray(privateKey);  
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKeyBytes);  
            Key priKey = keyFactory.generatePrivate(pkcs8KeySpec);  
            KeyAgreement keyAgree = KeyAgreement.getInstance(keyFactory.getAlgorithm());  
            keyAgree.init(priKey);  
            keyAgree.doPhase(pubKey, true);  
            // ??????????????????  
            SecretKey secretKey = keyAgree.generateSecret(SECRET_ALGORITHM);  
            return secretKey;  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeySpecException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param keyMap ????????? 
     * @return ??????base64??????????????? 
     */  
    public static String getDHPrivateKey(Map<String, Object> keyMap) {  
        Key key = (Key) keyMap.get(DHPRIVATE_KEY);  
        return TranscodeUtil.byteArrayToBase64Str(key.getEncoded());  
    }  
  
    /** 
     * ???????????? 
     * @param keyMap ????????? 
     * @return ??????base64??????????????? 
     */  
    public static String getDHPublicKey(Map<String, Object> keyMap) {  
        Key key = (Key) keyMap.get(DHPUBLIC_KEY);  
        return TranscodeUtil.byteArrayToBase64Str(key.getEncoded());  
    }  
  
    /** 
     * ???????????? 
     * @param keyStorePath keystore???????????? 
     * @param alias ?????? 
     * @param password ?????? 
     * @return ???????????? 
     */  
    private static PrivateKey getKeyStorePrivateKey(String keyStorePath, String alias,String password) {  
        try {  
            KeyStore ks = getKeyStore(keyStorePath, password);  
            PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());  
            return key;  
        } catch (UnrecoverableKeyException e) {  
            e.printStackTrace();  
        } catch (KeyStoreException e) {  
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param certificatePath ?????????????????? 
     * @return ???????????? 
     */  
    private static PublicKey getCertificatePublicKey(String certificatePath) {  
        try {  
            Certificate certificate = getCertificate(certificatePath);  
            PublicKey key = certificate.getPublicKey();  
            return key;  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ?????????????????? 
     * @param certificatePath ?????????????????? 
     * @return ???????????? 
     */  
    private static Certificate getCertificate(String certificatePath) {  
        try {  
            CertificateFactory certificateFactory = CertificateFactory.getInstance(X509);  
            FileInputStream in = new FileInputStream(certificatePath);  
            Certificate certificate = certificateFactory.generateCertificate(in);  
            in.close();  
            return certificate;  
        } catch (CertificateException e) {  
            e.printStackTrace();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param keyStorePath keystore???????????? 
     * @param alias ?????? 
     * @param password ?????? 
     * @return ???????????? 
     */  
    private static Certificate getCertificate(String keyStorePath,String alias, String password) {  
        try {  
            KeyStore ks = getKeyStore(keyStorePath, password);  
            Certificate certificate = ks.getCertificate(alias);  
            return certificate;  
        } catch (KeyStoreException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ??????KeyStore?????? 
     * @param keyStorePath keystore???????????? 
     * @param password keystore?????? 
     * @return ??????KeyStore 
     */  
    private static KeyStore getKeyStore(String keyStorePath, String password) {  
        try {  
            FileInputStream is = new FileInputStream(keyStorePath);  
            KeyStore ks = KeyStore.getInstance(KEY_STORE);  
            ks.load(is, password.toCharArray());  
            is.close();  
            return ks;  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (KeyStoreException e) {  
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (CertificateException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param keyStorePath keystore?????? 
     * @param alias ?????? 
     * @param password ?????? 
     * @return ???????????????????????? 
     */  
    public static String encryptByPrivateKey(String data, String keyStorePath,  
            String alias, String password) {  
        try {  
            // ????????????  
            PrivateKey privateKey = getKeyStorePrivateKey(keyStorePath, alias, password);  
            // ???????????????  
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());  
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
            return TranscodeUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param keyStorePath keystore?????? 
     * @param alias ?????? 
     * @param password ?????? 
     * @return ???????????????????????? 
     */  
    public static String decryptByPrivateKey(String data, String keyStorePath,String alias, String password) {  
        try {  
            // ????????????  
            PrivateKey privateKey = getKeyStorePrivateKey(keyStorePath, alias, password);  
            // ???????????????  
            Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());  
            cipher.init(Cipher.DECRYPT_MODE, privateKey);  
            return new String(cipher.doFinal(TranscodeUtil.base64StrToByteArray(data)));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param certificatePath ???????????? 
     * @return ???????????????????????? 
     */  
    public static String encryptByPublicKey(String data, String certificatePath) {  
        try {  
            // ????????????  
            PublicKey publicKey = getCertificatePublicKey(certificatePath);  
            // ???????????????  
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());  
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            return TranscodeUtil.byteArrayToBase64Str(cipher.doFinal(data.getBytes()));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????? 
     * @param data ?????????????????? 
     * @param certificatePath ???????????? 
     * @return ?????????????????? 
     */  
    public static String decryptByPublicKey(String data, String certificatePath) {  
        try {  
            // ????????????  
            PublicKey publicKey = getCertificatePublicKey(certificatePath);  
            // ???????????????  
            Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            return new String(cipher.doFinal(TranscodeUtil.base64StrToByteArray(data)));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (IllegalBlockSizeException e) {  
            e.printStackTrace();  
        } catch (BadPaddingException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ???????????????????????? 
     * @param certificatePath ???????????? 
     * @return ?????????????????? 
     */  
    public static boolean verifyCertificate(String certificatePath) {  
        return verifyCertificate(new Date(), certificatePath);  
    }  
  
    /** 
     * ???????????????????????? 
     * @param date ?????? 
     * @param certificatePath ???????????? 
     * @return ?????????????????? 
     */  
    public static boolean verifyCertificate(Date date, String certificatePath) {  
        boolean status = true;  
        try {  
            // ????????????  
            Certificate certificate = getCertificate(certificatePath);  
            // ?????????????????????????????????  
            status = verifyCertificate(date, certificate);  
        } catch (Exception e) {  
            status = false;  
        }  
        return status;  
    }  
  
    /** 
     * ???????????????????????? 
     * @param date ?????? 
     * @param certificate ?????? 
     * @return ?????????????????? 
     */  
    private static boolean verifyCertificate(Date date, Certificate certificate) {  
        boolean status = true;  
        try {  
            X509Certificate x509Certificate = (X509Certificate) certificate;  
            x509Certificate.checkValidity(date);  
        } catch (Exception e) {  
            status = false;  
        }  
        return status;  
    }  
  
    /** 
     * ???????????????????????? 
     * @param sign ?????????????????? 
     * @param keyStorePath keystore???????????? 
     * @param alias ?????? 
     * @param password ?????? 
     * @return ?????????????????? 
     */  
    public static String sign(String sign, String keyStorePath, String alias,String password) {  
        try {  
            // ????????????  
            X509Certificate x509Certificate = (X509Certificate) getCertificate(  
                                                  keyStorePath, alias, password);  
            // ????????????  
            KeyStore ks = getKeyStore(keyStorePath, password);  
            // ????????????  
            PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password  
                                    .toCharArray());  
            // ????????????  
            Signature signature = Signature.getInstance(x509Certificate  
                                  .getSigAlgName());  
            signature.initSign(privateKey);  
            signature.update(TranscodeUtil.base64StrToByteArray(sign));  
            return TranscodeUtil.byteArrayToBase64Str(signature.sign());  
        } catch (UnrecoverableKeyException e) {  
            e.printStackTrace();  
        } catch (KeyStoreException e) {  
            e.printStackTrace();  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (SignatureException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  
  
    /** 
     * ?????????????????? 
     * @param data ?????????????????? 
     * @param sign ???????????? 
     * @param certificatePath ???????????? 
     * @return ?????????????????? 
     */  
    public static boolean verify(String data, String sign,String certificatePath) {  
        try {  
            // ????????????  
            X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);  
            // ????????????  
            PublicKey publicKey = x509Certificate.getPublicKey();  
            // ????????????  
            Signature signature = Signature.getInstance(x509Certificate  
                                  .getSigAlgName());  
            signature.initVerify(publicKey);  
            signature.update(TranscodeUtil.base64StrToByteArray(data));  
            return signature.verify(TranscodeUtil.base64StrToByteArray(sign));  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (InvalidKeyException e) {  
            e.printStackTrace();  
        } catch (SignatureException e) {  
            e.printStackTrace();  
        }  
        return false;  
    }  
  
    /** 
     * ???????????? 
     * @param date ?????? 
     * @param keyStorePath keystore???????????? 
     * @param alias ?????? 
     * @param password ?????? 
     * @return ?????????????????? 
     */  
    public static boolean verifyCertificate(Date date, String keyStorePath,  
                                            String alias, String password) {  
        boolean status = true;  
        try {  
            Certificate certificate = getCertificate(keyStorePath, alias,  
                                      password);  
            status = verifyCertificate(date, certificate);  
        } catch (Exception e) {  
            status = false;  
        }  
        return status;  
    }  
  
    /** 
     * ???????????? 
     * @param keyStorePath keystore???????????? 
     * @param alias ?????? 
     * @param password ?????? 
     * @return ?????????????????? 
     */  
    public static boolean verifyCertificate(String keyStorePath, String alias,  
                                            String password) {  
        return verifyCertificate(new Date(), keyStorePath, alias, password);  
    }  
  
}
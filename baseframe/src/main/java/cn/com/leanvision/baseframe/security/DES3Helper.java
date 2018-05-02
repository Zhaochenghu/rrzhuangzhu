package cn.com.leanvision.baseframe.security;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/********************************
 * Created by lvshicheng on 2016/12/7.
 * <p>
 * 3DES加密工具类
 ********************************/
public class DES3Helper {

  // 加解密统一使用的编码方式
  private final static String encoding = "UTF-8";

  /**
   * 3DES加密
   *
   * @param plainText 普通文本
   * @param secretKey 秘钥，长度28位的字符串
   * @param iv        向量，长度8位的字符串
   * @throws Exception
   */
  public static String encode(String plainText, String secretKey, String iv) throws Exception {
    DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
    SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
    Key deskey = keyfactory.generateSecret(spec);

    Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
    IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
    cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
    byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
    return Base64Helper.encode(encryptData);
  }

  /**
   * 3DES解密
   *
   * @param encryptText 加密文本
   * @throws Exception
   */
  public static String decode(String encryptText, String secretKey, String iv) throws Exception {
    DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
    SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
    Key deskey = keyfactory.generateSecret(spec);
    Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
    IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
    cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

    byte[] decryptData = cipher.doFinal(Base64Helper.decode(encryptText));

    return new String(decryptData, encoding);
  }
}

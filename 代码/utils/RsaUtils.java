package chang.utils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaUtils {

    private static final int DEFAULT_KEY_SIZE = 2048;

    /**
     * 从文件中读取公钥
     *
     * @throws Exception
     */
    public static PublicKey getPublicKey(String path) throws Exception {
        byte[] bytes = readFile(path);
        return getPublicKey(bytes);
    }

    /**
     * 从文件中读取私钥
     *
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String path) throws Exception {
        byte[] bytes = readFile(path);
        return getPrivateKey(bytes);
    }

    /**
     * 获取公钥
     *
     * @param bytes 公钥的字节形式
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey(byte[] bytes) throws Exception {
        bytes = Base64.getDecoder().decode(bytes);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    /**
     * 获取密钥
     *
     * @param bytes 私钥的字节形式
     * @return
     * @throws Exception
     */
    private static PrivateKey getPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        bytes = Base64.getDecoder().decode(bytes);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }


    // ==============================================================================================================

    /**
     * 秘钥，SECRET不为空自动生成公钥和私钥
     */
    private String SECRET = "chang";
    /**
     * 自动生成公钥和私钥时的路径
     */
    private String PATH_NAME = "F:\\jwt";

    public static void main(String[] args) {
        // 生成文件
        try {
            new RsaUtils().generateKey();
            System.out.println("创建成功！");
        } catch (Exception e) {
            System.out.println("创建失败！！！");
            e.printStackTrace();
        }
    }

    /**
     * 根据密文，生存rsa公钥和私钥,并写入指定文件
     *
     *  publicKeyFilename  公钥文件路径
     *  privateKeyFilename 私钥文件路径
     *  secret             生成密钥的密文
     */
    public void generateKey() throws Exception {
        String publicKeyFilename = this.PATH_NAME + File.separator +"private.key";
        String privateKeyFilename = this.PATH_NAME + File.separator + "rsa_public.pub";
        String secret = this.SECRET;
        int keySize = secret.length();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(secret.getBytes());
        keyPairGenerator.initialize(Math.max(keySize, DEFAULT_KEY_SIZE), secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        // 获取公钥并写出
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        publicKeyBytes = Base64.getEncoder().encode(publicKeyBytes);
        writeFile(publicKeyFilename, publicKeyBytes);
        // 获取私钥并写出
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        privateKeyBytes = Base64.getEncoder().encode(privateKeyBytes);
        writeFile(privateKeyFilename, privateKeyBytes);
    }

    private static byte[] readFile(String fileName) throws Exception {
        return Files.readAllBytes(new File(fileName).toPath());
    }

    private static void writeFile(String destPath, byte[] bytes) throws IOException {
        File dest = new File(destPath);
        if (!dest.exists()) {
            dest.createNewFile();
        }
        Files.write(dest.toPath(), bytes);
    }
}
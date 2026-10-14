package br.com.estoque.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Guarda senhas de forma segura (PBKDF2 com salt aleatório).
 * No banco fica "salt:hash" em Base64; a senha original nunca é gravada.
 */
public final class SenhaUtil {

    private static final int ITERACOES = 120_000;
    private static final int TAMANHO_CHAVE_BITS = 256;
    private static final int TAMANHO_SALT_BYTES = 16;

    private SenhaUtil() {}

    public static String gerarHash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(senha.toCharArray(), salt);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(String senha, String armazenado) {
        if (senha == null || armazenado == null) return false;
        String[] partes = armazenado.split(":");
        if (partes.length != 2) return false;
        try {
            byte[] salt = Base64.getDecoder().decode(partes[0]);
            byte[] esperado = Base64.getDecoder().decode(partes[1]);
            byte[] calculado = pbkdf2(senha.toCharArray(), salt);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false; // valor gravado não está no formato esperado
        }
    }

    private static byte[] pbkdf2(char[] senha, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha, salt, ITERACOES, TAMANHO_CHAVE_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Não foi possível processar a senha.", e);
        }
    }
}

package org.dilithium.crypto;

import org.bouncycastle.jcajce.provider.digest.Blake2b;
import org.bouncycastle.jcajce.provider.digest.Skein;
import org.dilithium.crypto.groestl.Groestl256;
import org.dilithium.crypto.groestl.Groestl512;
import org.dilithium.crypto.keccak.Keccak256;
import org.dilithium.crypto.keccak.Keccak512;

import java.security.MessageDigest;

import static java.util.Arrays.copyOfRange;
import static org.dilithium.util.ByteUtil.concat;

public class Hash {
    private static MessageDigest md;

    public static byte[] keccak256(byte[] in) {
        md = new Keccak256();
        return md.digest(in);
    }

    public static byte[] keccak512(byte[] in) {
        md = new Keccak512();
        return md.digest(in);
    }

    public static byte[] keccak256Omit12(byte[] in) {
        byte[] hash = keccak256(in);
        return copyOfRange(hash, 12, hash.length);
    }

    public static byte[] skein256(byte[] in) {
        md = new Skein.Digest_256_256();
        return md.digest(in);
    }

    public static byte[] skein512(byte[] in) {
        md = new Skein.Digest_512_512();
        return md.digest(in);
    }

    public static byte[] blake256(byte[] in) {
        md = new Blake2b.Blake2b256();
        return md.digest(in);
    }

    public static byte[] blake512(byte[] in) {
        md = new Blake2b.Blake2b512();
        return md.digest(in);
    }

    public static byte[] groestl256(byte[] in) {
        return new Groestl256().digest(in);
    }

    public static byte[] groestl512(byte[] in) {
        return new Groestl512().digest(in);
    }


    public static byte[] longHash(byte[] in) {
        return concat(keccak256(in), skein256(in), blake256(in), groestl256(in));
    }
}

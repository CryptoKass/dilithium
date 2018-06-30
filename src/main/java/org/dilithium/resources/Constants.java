package org.dilithium.resources;

import org.dilithium.util.Hex;

import java.math.BigInteger;

import static org.dilithium.util.ByteUtil.xor;

public class Constants {
    /* Magic Bytes */
    public final static byte[] MagicBytesOne = Hex.fromHex("243F6A8885A308D313198A2E03707344A4093822299F31D008");
    public final static byte[] MagicBytesTwo = Hex.fromHex("2EFA98EC4E6C89452821E638D01377BE5466CF34E90C6CC0AC");

    public final static byte[] NetworkMagicID = xor(MagicBytesOne, MagicBytesTwo);

    /* Crypto Stuffs */
    private static final BigInteger SECP256K1N = new BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16);

    /**
     * Introduced in the Homestead release
     */
    public static BigInteger getSECP256K1N() {
        return SECP256K1N;
    }
}

/* 
 * Copyright (C) 2018 Dilithium Team .
 *
 * The Dilithium library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package org.dilithium.util;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldFp;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.util.encoders.Hex;


/**
 * This class Handles Keys.
 */
public class KeyUtil {
    
    private static KeyFactory kf;
    private static KeyPairGenerator kg;
    private static ECGenParameterSpec ecSpecGen;
    private static ECParameterSpec ecSpec;
    private static BigInteger ecN;
    private static BigInteger ecH;
    private static BigInteger ecA;
    private static BigInteger ecB;
    private static byte[] ecSeed;
    private static boolean isSetup = false;
    
    
    public static void SetupEC(){
        if(isSetup) return;
        try {       
            ecSpecGen = new ECGenParameterSpec("secp256r1");
            kf = KeyFactory.getInstance("ECDSA", "BC");
            kg = KeyPairGenerator.getInstance("ECDSA","BC");
            kg.initialize(ecSpecGen);
            KeyPair testPair = kg.generateKeyPair();
            //Get curve A and B
            ecA = ((ECPrivateKey) testPair.getPrivate()).getParams().getCurve().getA();
            ecB = ((ECPrivateKey) testPair.getPrivate()).getParams().getCurve().getA();
            ecN = ((ECPrivateKey) testPair.getPrivate()).getParams().getOrder();

            ECPublicKey setupKey = (ECPublicKey) testPair.getPublic();
            ecSpec = setupKey.getParams();
           
            isSetup = true;      
        } catch (Exception e) {
            //TODO notify...
            Log.log(Level.WARNING, e.getMessage());
        }
    }
    
    public static KeyPair GenerateKeyPair(){
        SetupEC();
        try {
            kg = KeyPairGenerator.getInstance("ECDSA","BC");
            kg.initialize(ecSpec);
            KeyPair newKeyPair = kg.generateKeyPair();
            return newKeyPair;
        } catch (Exception e) {
            //TODO notify...
            Log.log(Level.WARNING, e.getMessage());
            return null;
        }
    }
    
    public static ECPublicKey privateKeyToPublicKey(ECPrivateKey privateKey){
        SetupEC();
        try {
            return getPublicKey(privateKey);
        } catch (Exception e) {
            Log.log(Level.WARNING, "privatekey decode failed");
            Log.log(Level.WARNING, e.getMessage());
            return null;
        }
    }
    
    public static byte[] encodeECPublicKey(ECPublicKey publicKey){
        //System.out.println("W-X:" + publicKey.getW().getAffineX());
        return ByteUtil.ecPointToBytes(publicKey.getW());
    }
    
    public static byte[] encodeECPrivateKey(ECPrivateKey privateKey){
        return ByteUtil.bigIntegerToBytes(privateKey.getS());
    }
    
    public static ECPublicKey decodeECPublicKey(byte[] q){
        SetupEC();
        try {
            ECPoint point = ByteUtil.bytesToECPoint(q);
            ECPublicKeySpec pubSpec = new ECPublicKeySpec (point, ecSpec);
            return (ECPublicKey) kf.generatePublic(pubSpec);
        } catch (InvalidKeySpecException e) {
            Log.log(Level.WARNING, "publickey decode failed");
            Log.log(Level.WARNING, e.getMessage());
            return null;
        }
    }
    
    public static ECPrivateKey decodeECPrivateKey(byte[] input){
        SetupEC();
        try {
            BigInteger s = ByteUtil.bytesToBigInteger(input);
            ECPrivateKeySpec privSpec = new ECPrivateKeySpec(s,ecSpec);
            return (ECPrivateKey) kf.generatePrivate(privSpec);
        } catch (InvalidKeySpecException e) {
            Log.log(Level.WARNING, "privatekey decode failed");
            Log.log(Level.WARNING, e.getMessage());
            return null;
        }
    }
    
    public static String publicKeyToString(ECPublicKey publicKey){
        SetupEC();
        byte[] pubKeyBytes = encodeECPublicKey(publicKey);
        return Hex.toHexString(pubKeyBytes);
    }
    
    public static String privateKeyToString(ECPrivateKey privateKey){
        SetupEC();
        byte[] privKeyBytes = encodeECPrivateKey(privateKey);
        return Hex.toHexString(privKeyBytes);
    }
    
    public static ECPublicKey stringToPublicKey(String input){
        byte[] pubKeyBytes = Hex.decode(input);
        return decodeECPublicKey(pubKeyBytes);
    }
    
    public static ECPrivateKey stringToPrivateKey(String input){
        byte[] privKeyBytes = Hex.decode(input);
        return decodeECPrivateKey(privKeyBytes);
    }
    
    public static String publicKeyToAddressString(byte[] publicKeyBytes){
        ECPublicKey publicKey = decodeECPublicKey(publicKeyBytes);
        return publicKeyToAddressString(publicKey);
    }
    
    public static String publicKeyToAddressString(ECPublicKey publicKey){
        byte[] pubKeyBytes = encodeECPublicKey(publicKey);
        byte[] pubKeyHash = HashUtil.applyBlake2b(pubKeyBytes);
        return Encoding.bytesToAddress(pubKeyHash);
    }
    
    public static byte[] publicKeyToAddress(ECPublicKey publicKey){
        byte[] pubKeyBytes = encodeECPublicKey(publicKey);
        byte[] pubKeyHash = HashUtil.applyBlake2b(pubKeyBytes);
        return Encoding.bytesToCompressedAddress(pubKeyHash);
    }
    
    public static byte[] applyECDSASig(PrivateKey privateKey, byte[] input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			dsa.update(input);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}
   
    
   /* public static ECPublicKey signatureToPublicKey( byte[] sig, byte vByte ){
            SetupEC();
            //ASN1Primitive decodedSig = ByteUtil.toAsn1Object(sig).toASN1Primitive();
            //v = 27 + (y % 2)
            //int v = 27 + (ecpubkey.getW().getAffineY().mod(BigInteger.valueOf(2))).intValue() ;

            int b1 = sig[3];
            int rstart = 4;
            int rend = b1 + rstart;
            int compression  = 27;
            
            int b2 = sig[rend+1];
            int sstart = rend + 2;
            int send = b2 + sstart;
            
            BigInteger r = ByteUtil.bytesToBigInteger(Arrays.copyOfRange(sig, rstart, rend));
            BigInteger s = ByteUtil.bytesToBigInteger(Arrays.copyOfRange(sig, sstart, send));
            BigInteger v = BigInteger.valueOf((int) vByte - 27);
            
            BigInteger x = r;//.add(v.multiply(ecN));
            BigInteger y = x.pow(3).add(BigInteger.valueOf(7));
            
            //not working yet.
            return null;
            
    }*/
    
    
    /* Refactor below */
    // from https://stackoverflow.com/questions/42639620/generate-ecpublickey-from-ecprivatekey
    
    private static BigInteger BIG2 = BigInteger.valueOf(2);
    private static BigInteger BIG3 = BigInteger.valueOf(3);
    
    private static ECPoint doublePoint(final BigInteger p, final BigInteger a, final ECPoint R) {
    if (R.equals(ECPoint.POINT_INFINITY)) return R;
    BigInteger slope = (R.getAffineX().pow(2)).multiply(BIG3 );
    slope = slope.add(a);
    slope = slope.multiply((R.getAffineY().multiply(BIG2)).modInverse(p));
    final BigInteger Xout = slope.pow(2).subtract(R.getAffineX().multiply(BIG2)).mod(p);
    final BigInteger Yout = (R.getAffineY().negate()).add(slope.multiply(R.getAffineX().subtract(Xout))).mod(p);
    return new ECPoint(Xout, Yout);
}

    private static ECPoint addPoint   (final BigInteger p, final BigInteger a, final ECPoint r, final ECPoint g) {
    if (r.equals(ECPoint.POINT_INFINITY)) return g;
    if (g.equals(ECPoint.POINT_INFINITY)) return r;
    if (r==g || r.equals(g)) return doublePoint(p, a, r);
    final BigInteger gX    = g.getAffineX();
    final BigInteger sY    = g.getAffineY();
    final BigInteger rX    = r.getAffineX();
    final BigInteger rY    = r.getAffineY();
    final BigInteger slope = (rY.subtract(sY)).multiply(rX.subtract(gX).modInverse(p)).mod(p);
    final BigInteger Xout  = (slope.modPow(BIG2, p).subtract(rX)).subtract(gX).mod(p);
    BigInteger Yout =   sY.negate().mod(p);
    Yout = Yout.add(slope.multiply(gX.subtract(Xout))).mod(p);
    return new ECPoint(Xout, Yout);
}

    public static ECPoint scalmult   (final EllipticCurve curve, final ECPoint g, final BigInteger kin) {
    final ECField         field    = curve.getField();
    if(!(field instanceof ECFieldFp)) throw new UnsupportedOperationException(field.getClass().getCanonicalName());
    final BigInteger p = ((ECFieldFp)field).getP();
    final BigInteger a = curve.getA();
    ECPoint R = ECPoint.POINT_INFINITY;
    BigInteger k = kin.mod(p);
    final int length = k.bitLength();
    final byte[] binarray = new byte[length];
    for(int i=0;i<=length-1;i++){
        binarray[i] = k.mod(BIG2).byteValue();
        k = k.shiftRight(1);
    }
    for(int i = length-1;i >= 0;i--){
        R = doublePoint(p, a, R);
        if(binarray[i]== 1) R = addPoint(p, a, R, g);
    }
    return R;
}

    private static ECPublicKey getPublicKey(final ECPrivateKey pk) throws GeneralSecurityException {
        final ECParameterSpec params = pk.getParams();
        final ECPoint w = scalmult(params.getCurve(), pk.getParams().getGenerator(), pk.getS());
        final KeyFactory kg = KeyFactory.getInstance("EC");
        return (ECPublicKey)kg.generatePublic (new ECPublicKeySpec (w, params));
    }
    
    /*public static ECPublicKey privateKeyToPublicKey( ECPrivateKey privateKey){
        final ECParameterSpec params = privateKey.getParams();
        final ECPoint w = scalmult(params.getCurve(), privateKey.getParams().getGenerator(), privateKey.getS());
        try {
            return (ECPublicKey)kf.generatePublic (new ECPublicKeySpec (w, params));
        } catch (InvalidKeySpecException ex) {
            return null;
        }
    }*/
    
}

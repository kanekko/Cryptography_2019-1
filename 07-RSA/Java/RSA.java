import java.math.BigInteger;
import java.util.Random;
import java.io.*;
 
public class RSA {
	private BigInteger p;
	private BigInteger q;
	private BigInteger N;
	private BigInteger phi;
	private BigInteger e;
	private BigInteger d;
	private int bitlength = 16;
	// private int bitlength = 1024;
	//private int blocksize = 256;
 
	//blocksize in byte
	private Random r;

	public RSA() {
		// 1. Dos números primos aleatorios.
		r = new Random();
		p = BigInteger.probablePrime(bitlength, r);
		System.out.println("p: " + p);
		q = BigInteger.probablePrime(bitlength, r);
		System.out.println("q: " + q);

		// 2. Se calcula n=p*q
		N = p.multiply(q);
		System.out.println("N: " + N);
		
		// 3. Se calcula la función de Euler
		// Phi(n) = (p-1)(q-1)
		phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
		System.out.println("phi: " + phi);

		// 4. Entero positivo 'e' menor que phi y que sea coprimo con phi
		e = BigInteger.probablePrime(bitlength/2, r);
		System.out.println("e: " + e);
		
		while ( phi.gcd(e).compareTo(BigInteger.ONE)>0 && e.compareTo(phi)<0 ) {
			e.add(BigInteger.ONE);
		}

		// 5. Se determina 'd' que satisfaga la congruencia e*d=1(mod Phi(n))
		d = e.modInverse(phi);
		System.out.println("d: " + d);
	}
 
	public RSA(BigInteger e, BigInteger d, BigInteger N) {
		this.e = e;
		this.d = d;
		this.N = N;
	}
 
	private static String bytesToString(byte[] encrypted) {
		String test = "";
		for (byte b : encrypted) {
			test += Byte.toString(b);
		}
 
		return test;
	}
 
	/**
	 * c = c.pow(d) mod n
	 * @param message
	 * @return
	 */
	public byte[] encrypt(byte[] message) {
		return (new BigInteger(message)).modPow(e, N).toByteArray();
	}
 
	/**
	 * m = c.pow(d) mod n
	 * @param message
	 * @return
	 */
	public byte[] decrypt(byte[] message) {
		return (new BigInteger(message)).modPow(d, N).toByteArray();
	}
 
	public static void main (String[] args) throws IOException {
 
		RSA rsa = new RSA();

		String teststring = "hola";
		System.out.println("Encrypting String: " + teststring);
		System.out.println("String in Bytes: " + bytesToString( teststring.getBytes() )  );

		// encrypt
		byte[] encrypted = rsa.encrypt(teststring.getBytes());
		System.out.println("Encrypted String in Bytes: " + bytesToString(encrypted));
		
		// decrypt
		byte[] decrypted = rsa.decrypt(encrypted);
		System.out.println("Decrypted String in Bytes: " +  bytesToString(decrypted));
		System.out.println("Decrypted String: " + new String(decrypted));
		
	}

}
package com.dl;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

public class HamcTest {

	public static void main(String[] args) throws Exception {
		String secret = "c912349c";
		String message = "Message";
		boolean hex = true;
		

		byte[] kSecret = null;
		if (hex) {
			kSecret = Hex.decodeHex(secret.toCharArray());
		} else {
			kSecret = secret.getBytes();
		}

		System.out.println(Arrays.toString(kSecret));

		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(kSecret, "HmacSHA256"));
		byte[] result = mac.doFinal(message.getBytes());

		System.out.println(Hex.encodeHexString(result));
	}

}

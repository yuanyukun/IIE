package com.app.utils;

import android.util.Base64;

public class MyEncrypt {
	public String key;
	public String str;
	public String result;
	
	public MyEncrypt(String key, String str) {
		super();
		this.key = key;
		this.str = str;
	}

	public String encrypt() {
		JNCryptor cryptor = new AES256JNCryptor();
		byte[] passwordBytes = str.getBytes();
		try {
		  byte[] ciphertextBytes = cryptor.encryptData(passwordBytes, key.toCharArray());
		  // 把返回的密文变为字符串
		  result = Base64.encodeToString(ciphertextBytes, Base64.DEFAULT);
		} catch (CryptorException e) {
		  e.printStackTrace();
		}
		return result;
	}
	public String decrypt(){

		JNCryptor cryptor = new AES256JNCryptor();
		byte[] decodeStr = Base64.decode(str,Base64.DEFAULT);

		try {
			byte[] value = cryptor.decryptData(decodeStr,key.toCharArray());
			result=new String(value,"UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}

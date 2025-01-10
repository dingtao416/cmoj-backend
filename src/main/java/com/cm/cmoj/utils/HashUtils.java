package com.cm.cmoj.utils;

import cn.hutool.core.lang.hash.MurmurHash;
import org.springframework.util.DigestUtils;

/**
 * @Description: Hash工具类
 * @Author: Naccl
 * @Date: 2020-11-17
 */
public class HashUtils {

	public static String getMd5(CharSequence str) {
		return DigestUtils.md5DigestAsHex(str.toString().getBytes());
	}

	public static long getMurmurHash32(String str) {
		int i = MurmurHash.hash32(str);
		long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;
		return num;
	}

	public static String getBC(CharSequence rawPassword) {
		return (String) rawPassword;
	}


	public static void main(String[] args) {
		System.out.println(getBC("123456"));
	}
}

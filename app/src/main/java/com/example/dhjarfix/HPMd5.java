package com.example.dhjarfix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 描述：MD5加密.
 *
 * @author yangzhi
 */
public class HPMd5 {
	
	/**
	 * 描述：MD5加密.
	 * @param str 要加密的字符串
	 * @return String 加密的字符串
	 */
	private static MessageDigest sMd5MessageDigest;
	private static StringBuilder sStringBuilder;

	static {
		try {
			sMd5MessageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
		sStringBuilder = new StringBuilder();
	}

	public static String md5(String s) {

		sMd5MessageDigest.reset();
		sMd5MessageDigest.update(s.getBytes());

		byte digest[] = sMd5MessageDigest.digest();

		sStringBuilder.setLength(0);
		for (int i = 0; i < digest.length; i++) {
			final int b = digest[i] & 255;
			if (b < 16) {
				sStringBuilder.append('0');
			}
			sStringBuilder.append(Integer.toHexString(b));
		}

		return sStringBuilder.toString();
	}

	public static String getFileMD5(File file) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			FileChannel ch = in.getChannel();
			return MD5(ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
		} catch (FileNotFoundException e) {
			return "";
		} catch (IOException e) {
			return "";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// 关闭流产生的错误一般都可以忽略
				}
			}
		}

	}
	public static final char[] HEXCHAR = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	};
	/**
	 * 计算MD5校验
	 * @param buffer
	 * @return 空串，如果无法获得 MessageDigest实例
	 */
	private static String MD5(ByteBuffer buffer) {
		String s = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(buffer);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = HEXCHAR[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换, >>>,
				// 逻辑右移，将符号位一起右移
				str[k++] = HEXCHAR[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return s;
	}
}
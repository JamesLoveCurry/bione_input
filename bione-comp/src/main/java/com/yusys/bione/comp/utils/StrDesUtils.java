package com.yusys.bione.comp.utils;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * Title: 字符串DES加密、解密，暂不支持中文
 * Description: 字符DES加密、解密，暂不支持中文
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人:     修改日期:     修改内容:
 * </pre>
 */

public class StrDesUtils {
	private static final int SEGLEN = 8;
	private static final int UNTCNT = 2;
	private static final int DATLEN = SEGLEN / UNTCNT;
	private static final int TXTLEN = SEGLEN * 2;
	private static final int UNTLEN = DATLEN - 1;
	private static final int ROTATE = 3;
	private static byte[] KEYINIT = {'A','p','p','G','e','n','6','0'};
	private static int[] KEYSTEP = { 32, 19, 1, 8, 21, 89, 73, 83 };

	/**
	 * 该函数把key的前三个字母和后五个字母换位，key是一个长度为8的字符数组
	 * 
	 * @param Key
	 * @return
	 */
	private static byte[] RotateKey(byte[] Key) {
//		System.out.println();
//		for(int i=0;i<Key.length();i++){
//			System.out.print((int)Key.toCharArray()[i] + " ");
//		}
//		String Tmp = Key.substring(0, ROTATE);
//		Key = Key.substring(ROTATE, SEGLEN);
//		Key = Key + Tmp;
//		System.out.println();
//		for(int i=0;i<Key.length();i++){
//			System.out.print((int)Key.toCharArray()[i] + " ");
//		}
		
		byte Tmp[] = new byte[SEGLEN];
		Tmp = DesPublicUtils.byteCopy(Tmp, 0, Key, 0, ROTATE);
		Key = DesPublicUtils.byteCopy(Key, 0, Key, ROTATE, ( SEGLEN - ROTATE ));
		Key = DesPublicUtils.byteCopy(Key, SEGLEN - ROTATE, Tmp, 0, ROTATE);
		return Key;
	}

	/**
	 * 把key初始化为“AppGen60”，后把key的前三个字母和后五个字母换位，key是一个长度为8的字符数组
	 * 
	 * @return
	 */
	private static byte[] InitKey() {
		byte[] Key = new byte[KEYINIT.length];
		for(int i=0; i<KEYINIT.length; i++){
			Key[i] = KEYINIT[i];
		}
		Key = RotateKey(Key);
		return Key;
	}

	/**
	 * 把数据key中的每个字符都加上keystep中相应位置的值，后把key的前三个字母和后五个字母换位 key是一个长度为8的字符数组
	 * 
	 * @param Key
	 * @return
	 */
	private static byte[] StepKey(byte[] Key) {
		
		for (int i = 0; i < SEGLEN; i ++ )
			Key[i] += KEYSTEP[i];
		return RotateKey( Key );
	}

	/**
	 * 把In中每隔三个字符放入一个随机字符
	 * 
	 * @param In
	 * @param Len
	 * @param Data
	 * @return
	 */
	private static byte[] PutData(final byte[] In, int Len, byte[] Data) {
		int l;
		SecureRandom random = new SecureRandom();
		Data = DesPublicUtils.changByte(Data, 0, (byte)(random.nextInt() % 128));
//		System.out.println(Data.toString());
		if (Len > 0) {
			l = (Len < UNTLEN ? Len : UNTLEN);// 如果Len比2小，则l为Len，，否则l为2
			Data = DesPublicUtils.byteCopy(Data, 1, In, 0, l);
			if (l > In.length) {
				for (int i = 0; i < l - In.length; i++) {
					Data = DesPublicUtils.changByte(Data,
							i + In.length + 1, (byte)'\0');
				}
			}
		} else
			l = 0;
		for (; l < UNTLEN; l++)
			Data = DesPublicUtils.byteAddChar(Data, l + 1,
					(byte)(random.nextInt() % 128));
		return Data;
	}

	/**
	 * 得到Data里的值
	 * 
	 * @param Data
	 * @param Out
	 * @param Len
	 * @return
	 */
	private static Map<String, Object> GetData(final byte[] Data, byte[] Out, int Len) {
		if (Data.equals("") || Data == null) {
			return null;
		}
		int l;
		Map<String, Object> map = new HashMap<String, Object>();
		for (l = 0; l < UNTLEN; l++) {// 如果Data[1] Data[2]中含有 \0
			if (Data[l + 1] == 0)
				break;
		}
		if (l >= Len)// 如果\0的位置 >= Len 返回-1
			map.put("int", -1);
		if (l > 0) { // 如果\0的位置>0 且<Len
			Out = DesPublicUtils.byteCopy(Out, 0, Data, 1, l);
			map.put("int", l);
		}
		if (l < UNTLEN) {
			map.put("int", l);
		}
		map.put("String", Out);
		return map;
	}

	/**
	 * 对输入的字符串进行DES加密，size >= (length+1+6)/6
	 * 
	 * @param InStr
	 * @param OutBuf
	 * @param Size
	 * @return
	 */
	private static byte[] appStrEncrypt(byte[] InStr, int Size) {
		// 把输入的字符串转成GBK编码的
		int l, i, o, c, s;
		byte k[] = new byte[SEGLEN], v[] = new byte[SEGLEN];
		
//		for(i = 0; i < SEGLEN; i++){
//			k[i] = 0;
//			v[i] = 0;
//		}
		byte OutBuf[] = new byte[0];
		
		if (InStr == null)
			return null;
		
		k = InitKey();
		
		for (i = 0, o = 0, l = InStr.length + 1; l > 0;) {
			if (o + TXTLEN >= Size)
				return OutBuf;
			for (s = 0, c = 0; c < UNTCNT; c++, s += DATLEN) {
				if (i >= InStr.length) {
					i = InStr.length;
				}
				byte[] v_s = DesPublicUtils.byteArrayCopy(v, s, v.length);
				byte[] Instr_i = DesPublicUtils.byteArrayCopy(InStr, i, InStr.length);
				v =  DesPublicUtils.byteCopy(v, s, PutData(Instr_i
						, l, v_s), 0, PutData(Instr_i
								, l, v_s).length);
				i += UNTLEN;
				l -= UNTLEN;
			}
			v = DessubUtils.appDesEncrypt(k, v, v);
			k = StepKey(k);
			byte[] result = HexsubUtils.appHexUnpack(v,
					o < OutBuf.length ? DesPublicUtils.byteArrayCopy(OutBuf, o, OutBuf.length) : "".getBytes(),
					TXTLEN);
			OutBuf = DesPublicUtils.byteCopy(OutBuf, OutBuf.length, result, 0, result.length);
			o += TXTLEN;
//			v = "";
		}
		return OutBuf;
	}

	/**
	 * 对输入的字符串进行DES解密
	 * 
	 * @param InStr
	 * @param OutBuf
	 * @param Size
	 * @return
	 */
	private static byte[] appStrDecrypt(final byte[] InStr, 
			int Size) {
		int l, i, o, c, s, r;
//		byte inStrByte[] = InStr.getBytes();
		byte k[] = new byte[SEGLEN], v[] = new byte[SEGLEN];
		byte OutBuf[] = new byte[0];

		if (InStr == null)
			return null;

		k = InitKey();
		for (i = 0, o = 0, l = InStr.length + 1; l > 0;) {
			if (l < SEGLEN)
				return OutBuf;
			if (i < InStr.length) {
				v = HexsubUtils.appHexPack(DesPublicUtils.byteArrayCopy(InStr, i, InStr.length), TXTLEN, v);// 结果正确
				i += TXTLEN;
				l -= TXTLEN;
				v = DessubUtils.appDesDecrypt(k, v, v);
			} else {
				v[0] = 0;
			}
			k = StepKey(k);
			for (s = 0, c = 0; c < UNTCNT; c++, s += DATLEN) {
				Map<String, Object> map = GetData(DesPublicUtils.byteArrayCopy(v, s, v.length),  DesPublicUtils.byteArrayCopy(OutBuf, o, OutBuf.length), Size - o);
				if (map != null) {
					if ((r = new Integer(map.get("int").toString())) < 0)
						return DesPublicUtils.byteCopy(OutBuf, OutBuf.length, (byte[])map.get("String"), 0, ((byte[]) map.get("String")).length);
					else if (r < UNTLEN) {
						OutBuf = DesPublicUtils.byteCopy(OutBuf, OutBuf.length, (byte[])map.get("String"), 0, ((byte[])map.get("String")).length);
						return OutBuf;
					}
				} else {
					return OutBuf;
				}

				OutBuf = DesPublicUtils.byteCopy(OutBuf, OutBuf.length, (byte[])map.get("String"), 0, ((byte[])map.get("String")).length);
				o += UNTLEN;
			}
		}
		if (o == 0)
			return null;

		return OutBuf;
	}

//	private static byte[] getBytes(char[] chars) {
//
//		Charset cs = Charset.defaultCharset();
//		CharBuffer cb = CharBuffer.allocate(chars.length);
//		cb.put(chars);
//		cb.flip();
//		ByteBuffer bb = cs.encode(cb);
//
//		return bb.array();
//	}

	private static char[] getChars(byte[] bytes) {
		Charset cs = Charset.defaultCharset();
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);

		return cb.array();
	}

//	private static byte[] getBytesByEncode(char[] chars, String encode) {
//
//		Charset cs = Charset.forName(encode);
//		CharBuffer cb = CharBuffer.allocate(chars.length);
//		cb.put(chars);
//		cb.flip();
//		ByteBuffer bb = cs.encode(cb);
//
//		return bb.array();
//	}

//	private static char[] getCharsByEncode(byte[] bytes, String encode) {
//		Charset cs = Charset.forName(encode);
//		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
//		bb.put(bytes);
//		bb.flip();
//		CharBuffer cb = cs.decode(bb);
//
//		return cb.array();
//	}

	/**
	 * 根据默认中文编码对输入的中文字符进行DES加密
	 * 
	 * @param In
	 * @param OutData
	 * @param index
	 * @return
	 */
	public static String appStrEncryptByDefaultEncoding(String In, int index) {

//		byte[] B = getBytes(In.toCharArray());
//		int n = 0, i = 0;
//		for (i = 0; i < In.length; i++) {
//			if (In[i] == 0) {
//				n = i;
//				break;
//			}
//		}
//		if (i == In.length) {
//			n = i;
//		}
//		char[] c = new char[n];
//		for (i = 0; i < n; i++) {
//			if ((int) In[i] < 0) {
//				c[i] = (char) (In[i] + 256);
//			} else {
//				c[i] = (char) In[i];
//			}
//		}

//		System.out.println();
//		for(int i=0; i<In.getBytes().length; i++){
//			System.out.print(In.getBytes()[i] + " ");
//		}
		byte[] result = StrDesUtils.appStrEncrypt(In.getBytes(), index);
		char[] c = getChars(result);
		return String.valueOf(c);

	}

	/**
	 * 对输入的字符串进行解密，并用默认编码进行转换
	 * 
	 * @param OutData
	 * @param In
	 * @param index
	 * @return
	 */
	public static String appStrDecryptByDefaultEncoding(String OutData, int index) {
		byte[] temp = StrDesUtils.appStrDecrypt(OutData.getBytes(), index);//
		char[] c = getChars(temp);
		return String.valueOf(c);
	}

	/**
	 * 用指定编码进行加密，仅支持UTF8和GBK
	 * 
	 * @param In
	 * @param OutData
	 * @param index
	 * @param encode
	 * @return
	 */
	public static String appStrEncryptByEncode(String In,
			int index, String encode) {
		encode = encode.toUpperCase();
		if (encode.equals("GBK") || encode.equals("GB2312")
				|| encode.equals("UTF8") || encode.equals("UTF-8")) {
			
			try {
				byte[] B = In.getBytes(encode);
				char[] c = getChars(StrDesUtils.appStrEncrypt(B, index));

				return String.valueOf(c);//
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			
		} else {
			System.out.println("改编码不支持，请修改");
			return null;
		}

	}

	/**
	 * 用指定编码进行解密，仅支持UTF8和GBK
	 * 
	 * @param OutData
	 * @param In
	 * @param index
	 * @param encode
	 * @return
	 */
	public static String appStrDecryptByEncode(final String In,
			int index, String encode) {
		encode = encode.toUpperCase();
		if (encode.equals("GBK") || encode.equals("GB2312")
				|| encode.equals("UTF8") || encode.equals("UTF-8")) {

			byte[] temp = StrDesUtils.appStrDecrypt(In.getBytes(), index);//
			try {
				return new String(temp, encode);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			System.out.println("改编码不支持，请修改");
			return null;
		}
	}

	private static char[] test = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
						'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
						'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
						'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
						'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
						'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

	public static void main(String[] args) {
		SecureRandom random = new SecureRandom();

//		for (int j = 0; j < 200; j++) {
			int n = random.nextInt() % 100;

			if (n == 0)
				return;
			if (n < 0) {
				n = -n;
			}

			char InData[] = new char[n];
			for (int i = 0; i < n; i++) {
				int tmp = random.nextInt() % 62;
				if (tmp < 0) {
					tmp = -tmp;
				}
				InData[i] = test[tmp];
			}
			
			String InString = String.valueOf(InData);
			InString = "12345678";
			String OutData = StrDesUtils.appStrEncryptByEncode(InString, 460, "GBK");
			System.out.println("输入字符串" + InString);
			System.out.println("输出字符串" + OutData);
			OutData = StrDesUtils.appStrDecryptByEncode(OutData, 350, "GBK");
			
			if (!OutData.equals(InString)) {
				System.out.println("有不能解密的输入字符串");
			}
//		}
		// for (int i = 16; i <= 48; i++) {
		// OutData = StrDesUtils.appStrEncryptByDefaultEncoding(In, OutData,
		// 48);// 加密
		// String OutData = StrDesUtils.appStrEncryptByDefaultEncoding(In,
		// 148);// 加密
		// System.out.println(OutData);
		// In = "";
		// System.out.println(StrDesUtils.appStrDecryptByDefaultEncoding(OutData,
		// 148));
		// OutData = "69CD2B29EF46C64DDEFDF41E29C14828";
		// OutData = "F6392052E9D68845";
		// for(int i=0; i<OutData.length; i++){
		// System.out.print((char)OutData[i]);
		// }
		// byte[] tmp = StrDesUtils.appStrDecrypt(OutData, 148);
		// char[] out = new char[tmp.length];
		//
		// for(int i=0; i<tmp.length; i++){
		// out[i] = (char)tmp[i];
		// }
		// // System.out.println();
		// System.out.println();
		// System.out.println(String.valueOf(out));
		// 48);// 解密
		// String temp = StrDesUtils.appStrDecrypt(OutData, 136);
		// System.out.println(temp);
		// }

	}
	

}

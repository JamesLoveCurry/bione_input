package com.yusys.bione.comp.utils;


/**
 * 
 * <pre>
 * Title:	8位字符的des算法
 * Description: 8位字符的des算法
 * </pre>
 * 
 * @author fangjuan  fangjuan@yuchengtech.com
 * @version 1.00.00
 * @since	2013-12-22
 *
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class DessubUtils {
	private static byte[] NumRotate = { 1, 1, 2, 2, 2, 2, 2, 2, 1,
			2, 2, 2, 2, 2, 2, 1 };
	private static byte KeyPermut[] = { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50,
			42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44,
			36, 63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6,
			61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4 };
	private static byte TmpKeyPermut[] = { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6,
			21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37,
			47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50,
			36, 29, 32 };
	private static byte InitPermut[] = { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52,
			44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48,
			40, 32, 24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35,
			27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31,
			23, 15, 7 };
	private static byte CvtInPermut[] = { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
			8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20,
			21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31,
			32, 1 };
	private static byte ConvBits[][] = {
			{ 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7,
					4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8,
					13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4,
					9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 },
			{ 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4,
					7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11,
					10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3,
					15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 },
			{ 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0,
					9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8,
					15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9,
					8, 7, 4, 15, 14, 3, 11, 5, 2, 12 },
			{ 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11,
					5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12,
					11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1,
					13, 8, 9, 4, 5, 11, 12, 7, 2, 14 },
			{ 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2,
					12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10,
					13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14,
					2, 13, 6, 15, 0, 9, 10, 4, 5, 3 },
			{ 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4,
					2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2,
					8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15,
					10, 11, 14, 1, 7, 6, 0, 8, 13 },
			{ 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11,
					7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13,
					12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4,
					10, 7, 9, 5, 0, 15, 14, 2, 3, 12 },
			{ 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13,
					8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9,
					12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10,
					8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };
	private static byte CvtOutPermut[] = { 16, 7, 20, 21, 29, 12, 28, 17, 1,
			15, 23, 26, 5, 18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30,
			6, 22, 11, 4, 25 };
	private static byte FinPermut[] = { 40, 8, 48, 16, 56, 24, 64, 32, 39, 7,
			47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45,
			13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11,
			51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49,
			17, 57, 25 };
	
	
	private static int BitGet(final byte[] Data, int Pos) {
		int n;
		if ((Pos >> 3) < Data.length) {
			n = ((Data[Pos >> 3] >> (7 - (Pos & 0x7))) & 0x01);
		} else {
			n = ((0 >> (7 - (Pos & 0x7))) & 0x01);
		}
		return n;
	}
	
	
	private static byte[] BitSet(byte[] Data, int Pos, int Bit) {
		int c = Pos >> 3;
		int p = Pos & 0x7;
		char m = (char) (1 << (7 - p));
		if(c < Data.length){
			Data[c] = (byte) (((int) Data[c] & (0xFF ^ m)) | (Bit & 0x1) * m);
		}else{
			int n = Data.length;
			byte tmp[] = Data;
			Data = new byte[c + 1];
			for(int i=0;i<n;i++){
				Data[i] = tmp[i];
			}
			Data[c] = (byte) (( 0 & (0xFF ^ m)) | (Bit & 0x1) * m);
		}
		return Data;
	}
	private static byte[] ByteToBit(final byte[] Data, byte[] Buffer) {
		for (int i = 0; i < 64; i++) {
			Buffer[i] = (byte) BitGet(Data, i);
		}
		
		return Buffer;
	}
	private static  byte[] BitToByte(final byte Data[], byte Buffer[]) {
		int i;

		for (i = 0; i < 64; i++)
			Buffer = BitSet(Buffer, i, Data[i]);
		return Buffer;
	}
	private static byte[] Permut(byte Data[], byte Tbl[], int Size) {
		int i;
		byte Tmp[] = new byte[64];
		for (i = 0; i < Size; i++){
			if(((int)Tbl[i] - 1) < Data.length ){
				Tmp[i] = Data[ Tbl[ i ] - 1 ];
			}else{
				Tmp[i] = 0;
			}
		}
		Data = DesPublicUtils.byteCopy(Data, 0, Tmp, 0, Size);
		
		return Data;
	}
	private static byte[] Rotate(byte[] Data, int Loop) {
		
		byte Tmp[] = new byte[64];
		int Step = NumRotate[Loop];

		for (int i = 0; i < 56; i += 28) {
			Tmp = DesPublicUtils.byteCopy(Tmp, i, Data, i + Step, 28 - Step);
			Tmp = DesPublicUtils.byteCopy(Tmp, i + 28 - Step, Data, i, Step);
		}
		Data = DesPublicUtils.byteCopy(Data,0, Tmp,0, 56);
		return Data;

	}
	private static byte[] Convert(final byte KeyIn[], final byte DataIn[],
			final byte KeyOut[], byte DataOut[]) {//结果正确
		int i, j, k;
		byte TmpIn[] = new byte[48];
		byte TmpOut[] = new byte[32];
		byte c;

		for (i = 0; i < 48; i++) {// 结果正确
			if (CvtInPermut[i] - 1 < DataIn.length) {
				TmpIn[i] = (byte) (((byte) KeyIn[i]) ^ (int) DataIn[(int) (CvtInPermut[i] - 1)]);
			} else {
				TmpIn[i] = (byte) new Double(Math.pow((int) KeyIn[i], 0))
						.intValue();
			}
			if ((char) TmpIn[i] == 65535) {
				TmpIn[i] = 0;
			}
		}
		
		for (i = 0; i < 8; i++) {
			k = i * 6;

			j = 32 * TmpIn[k] + 16 * TmpIn[k + 5] + 8 * TmpIn[k + 1] + 4
					* TmpIn[k + 2] + 2 * TmpIn[k + 3] + TmpIn[k + 4];
			c = ConvBits[i][j];
			k = i * 4;
			TmpOut[k] = (byte) ((c >> 3) & 1);
			TmpOut[k + 1] = (byte) ((c >> 2) & 1);
			TmpOut[k + 2] = (byte) ((c >> 1) & 1);
			TmpOut[k + 3] = (byte) (c & 1);

		}
		for (i = 0; i < 32; i++)
			DataOut[i] = (byte) ((int) (i < KeyOut.length ? KeyOut[i] : 0) ^ (int) TmpOut[CvtOutPermut[i] - 1]);
		return DataOut;
	}
	
	/**
	 * 对8为字符进行DES算法
	 * @param Key
	 * @param InData
	 * @param OutData
	 * @return
	 */
	public static byte[] appDesEncrypt(final byte[] Key, final byte[] InData,
			byte[] OutData) {
		int i, j;
		byte KeyBlock[] = new byte[64];
		byte WorkBlock[] = new byte[64];
		byte TmpBlock[] = new byte[32];
		byte TmpKey[][] = new byte[16][48];

		byte LeftBlock[] = new byte[32];
		byte RightBlock[] = new byte[32];

		KeyBlock = ByteToBit(Key, KeyBlock);// 结果对
		WorkBlock = ByteToBit(InData, WorkBlock);
		KeyBlock = Permut(KeyBlock, KeyPermut, 56);// 结果对

		for (i = 0; i < 16; i++) {
			KeyBlock = Rotate(KeyBlock, i);// keyBlock对了
			for (j = 0; j < 48; j++)
				// TmpKey 结果正确
				if (TmpKeyPermut[j] - 1 >= KeyBlock.length) {
					TmpKey[i][j] = 0;
				} else {
					TmpKey[i][j] = KeyBlock[TmpKeyPermut[j] - 1];
				}
		}

		WorkBlock = Permut(WorkBlock, InitPermut, 64);// WorkBlock 结果正确
		LeftBlock = DesPublicUtils.byteCopy(LeftBlock, 0, WorkBlock, 0, 32);// LeftBlock
																	// 结果正确
		RightBlock = DesPublicUtils.byteCopy(RightBlock, 0, WorkBlock, 32, 32);

		for (i = 0; i < 16; i++) {
			TmpBlock = Convert(TmpKey[i], RightBlock, LeftBlock, TmpBlock);
			LeftBlock = DesPublicUtils.byteCopy(LeftBlock, 0, RightBlock, 0, 32);
			RightBlock = DesPublicUtils.byteCopy(RightBlock, 0, TmpBlock, 0, 32);
		}

		WorkBlock = DesPublicUtils.byteCopy(WorkBlock, 0, RightBlock, 0, 32);
		WorkBlock = DesPublicUtils.byteCopy(WorkBlock, 32, LeftBlock, 0, 32);

		WorkBlock = Permut(WorkBlock, FinPermut, 64);

		WorkBlock = BitToByte(WorkBlock, OutData);
		return WorkBlock;
	}
	
	/**
	 * 对8为字符进行DES解密
	 * @param Key
	 * @param InData
	 * @param OutData
	 * @return
	 */
	public static byte[] appDesDecrypt(final byte[] Key, final byte[] InData,
			byte[] OutData) {
		int i, j;
		byte KeyBlock[] = new byte[64];
		byte WorkBlock[] = new byte[64];
		byte TmpBlock[] = new byte[32];
		byte TmpKey[][] = new byte[16][48];

		byte LeftBlock[] = new byte[32];
		byte RightBlock[] = new byte[32];

		KeyBlock = ByteToBit(Key, KeyBlock);// 结果正确
		WorkBlock = ByteToBit(InData, WorkBlock);// 结果正确
		KeyBlock = Permut(KeyBlock, KeyPermut, 56);// 结果正确

		for (i = 0; i < 16; i++) {
			KeyBlock = Rotate(KeyBlock, i);
			for (j = 0; j < 48; j++)
				TmpKey[i][j] = KeyBlock[TmpKeyPermut[j] - 1];// 结果正确
		}

		WorkBlock = Permut(WorkBlock, InitPermut, 64);// 结果正确
		LeftBlock = DesPublicUtils.byteCopy(LeftBlock, 0, WorkBlock, 0, 32);
		RightBlock = DesPublicUtils.byteCopy(RightBlock, 0, WorkBlock, 32, 32);// 结果正确

		for (i = 15; i >= 0; i--) {
			TmpBlock = Convert(TmpKey[i], RightBlock, LeftBlock, TmpBlock);
			LeftBlock = DesPublicUtils.byteCopy(LeftBlock, 0, RightBlock, 0, 32);
			RightBlock = DesPublicUtils.byteCopy(RightBlock, 0, TmpBlock, 0, 32);
		}

		WorkBlock = DesPublicUtils.byteCopy(WorkBlock, 0, RightBlock, 0, 32);
		WorkBlock = DesPublicUtils.byteCopy(WorkBlock, 32, LeftBlock, 0, 32);

		WorkBlock = Permut(WorkBlock, FinPermut, 64);
		WorkBlock = BitToByte(WorkBlock, OutData);

		return WorkBlock;

	}
}

package com.yusys.bione.comp.utils;


public class HexsubUtils {
	
	/**
	 * 将ASCII转化为数字
	 * @param HexDigit
	 * @return
	 */
	public static int HexToBin( int HexDigit )
	{
		if ( HexDigit >= '0' && HexDigit <= '9' )
			return ( HexDigit - '0' );
		if ( HexDigit >= 'A' && HexDigit <= 'F' )
			return ( HexDigit - 'A' + 10 );
		if ( HexDigit >= 'a' && HexDigit <= 'f' )
			return ( HexDigit - 'a' + 10 );
		return -1;
	}
	
	/**
	 * 将数字转成ASCII值
	 * @param BinValue
	 * @return
	 */
	public static int BinToHex(int BinValue) {
		if (BinValue >= 0 && BinValue <= 9)
			return BinValue + '0';
		if (BinValue >= 10 && BinValue <= 15)
			return BinValue - 10 + 'A';
		return -1;
	}
	
	/**
	 * 将输入的InData的每个位的ASCII转成对应的数字
	 * @param InData
	 * @param Size
	 * @param OutData
	 * @return
	 */
	public static byte[] appHexPack( final byte[] InData, int Size, byte[] OutData )
	{
		int	i = 0, o = 0, s = Size % 2, v[] = { 0, 0 };

		while ( Size > 0 ) {
			if(i >= InData.length){
				v[s] = 0;
			}else{
				v[s] = HexToBin( InData[i]);
			}
			i ++;
			Size --;
			if ( s == 0 )
				s ++;
			else {
				OutData = DesPublicUtils.changByte(OutData, o, (byte)(( v[0] << 4 ) + v[1]));
				o ++;
				s --;
			}
		}
		return OutData;
	}
	
	/**
	 * 将输入的InData的ASCII转成数字
	 * @param InData
	 * @param OutData
	 * @param Size
	 * @return
	 */
	public static byte[] appHexUnpack(final byte[] InData, byte[] OutData, int Size) {
		int i = 0, o = 0, v;
		while (Size > 0) {
			if(i < InData.length){
				if((int)InData[i] < 0){
					v = (int)InData[i] + 256;
				}else{
					v = (int)InData[i];
				}
			}
			else
				v = 0;
			;
			i++;
			if (Size % 2 == 0) {
				OutData = DesPublicUtils.changByte(OutData, o, (byte) BinToHex(v >> 4));
				o++;
				Size--;
			}
			OutData = DesPublicUtils.changByte(OutData, o, (byte) BinToHex(v & 0xF));
			o++;
			Size--;
		}
		return OutData;
	}

}

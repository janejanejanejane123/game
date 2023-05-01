package com.ruoyi.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import java.util.List;

public class NumberUtils {
	
	public static final long BASE = 62L;
	public static final long DEF = 36L;

	public static final int BASE62_MAX_LENGTH = 6;

	public static List<Range<Character>> DEF_RANGES = Lists.newArrayList(Range.closed('a', 'z'),
			Range.closed('0', '9'));
	
	static String[] chars = new String[] {
			"0","1","2","3","4","5","6","7",
			"8","9","a","b","c","d","e","f",
			"g","h","i","j","k","l","m","n",
			"o","p","q","r","s","t","u","v",
			"w","x","y","z","A","B","C","D",
            "E","F","G","H","I","J","K","L",
            "M","N","O","P","Q","R","S","T",
            "U","V","W","X","Y","Z"
          };

	private static char[] chars36 = new char[] {
			'0','1','2','3','4','5','6','7',
			'8','9','a','b','c','d','e','f',
			'g','h','i','j','k','l','m','n',
			'o','p','q','r','s','t','u','v',
			'w','x','y','z'
	};

	public static long defS2l(String def){
		def=def.toLowerCase();
		char[] chars = def.toCharArray();
		long result=0;
		for (int i = 0; i < chars.length; i++) {
			char aChar = chars[i];
			boolean flag=false;
			for (Range<Character> defRange : DEF_RANGES) {
				if (defRange.contains(aChar)){
					flag=true;
					break;
				};
			}
			if (!flag){
				return -1;
			}

			result+=ascii2integer(aChar)* Math.pow(DEF,i);
		}
		return result;
	}


	public static String defL2s(long number){
		if (number<0){
			return "";
		}
		StringBuilder to36 = new StringBuilder();
		do {
			to36.append(chars36[(int) (number % DEF)]);
			number /= DEF;
		} while (number != 0);

		return to36.toString();
	}

	public static void main(String[] args) {

	}

	
	public static String decimal2base62(long number) {
		if (number < 0) {
			return "";
		}
		
		StringBuilder base62 = new StringBuilder();
		do {
			base62.append(chars[(int) (number % BASE)]);
			number /= BASE;
		} while (number != 0);
		
		return base62.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static Long base62ToDecimal(String base62) {
		int base62Length = base62.length();
		if (base62Length > BASE62_MAX_LENGTH) {
			return -1L;
		}
		List<Range<Character>> ranges = Lists.newArrayList(Range.closed('a', 'z'),
				Range.closed('0', '9'), Range.closed('A', 'Z'));
		for (int i = 0; i < base62Length; i++) {
			boolean status = false;
			for (Range<Character> range : ranges) {
				if (range.contains(base62.charAt(i))) {
					status = true;
					break;
				}
			}
			
			if (!status) {
				return -1L;
			}
		}
		
		long decimal = 0L;
		for (int i = 0; i < base62Length; i++) {
			int num = ascii2integer(base62.charAt(i));
			decimal += num * Math.pow(BASE, i);
		}
		
		return decimal;
	}
	
	public static int ascii2integer (char ch) {
		int n = 0;
		if (ch >= 48 && ch <= 57) {  
            n = ch - 48;  
        } else if (ch >= 97 && ch <= 122) {  
            n = ch - 87;  
        } else if (ch >= 65 && ch <= 90) {  
            n = ch - 29;  
        }
		
		return n;
	}
}

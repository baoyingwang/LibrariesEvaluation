package baoying.eval.jdk.util;

import java.math.BigDecimal;

// This is derived from a discussing with Yanpeng, today.
// He found that when printing 6.0-5.6 , we will got 0.40000000000036
// but when printing 0.4 directly, we will got 0.4
// why such difference?
// We know that the substraction could cause some loss. But why no problem on printing 0.4 directly?
//
// This utility tells us the exact value of an IEEE754 float number.
// the result of 6.0-5.6 , is definitely anoter than number, rather than 0.4.
// Comparing the RawLongBits could tell us that. It is more clear with the exp, fraction etc.
//
//
//It took me 1hour and 30 minutes(09:00~10:30 PM 20140627) to complete this utility
//I checked reference book at same time
//I also read the reference book during the day
//
public class IEEE754Util {



	long double_bias=1023;
	
	/**
	 * 
	 * @param v
	 * @return
	 */
	public static BigDecimal getExactV(double v){
		
		System.out.println("==========reference=============");
		System.out.println("V = (-1)^s * M * 2^E");
		System.out.println("M : 1.fraction(51~0)");
		System.out.println("E : unsigned exp(62~52) - Bias(1023 for double)");
		System.out.println("Refer: P70 of Computer System - A programmer's perspective, CN 2nd");
		
		System.out.println("=========="+v+"==============");
		BigDecimal sign= new BigDecimal(1);
		
		long longAsBits = Double.doubleToRawLongBits(v);
		
		if(longAsBits < 0){
			sign =new BigDecimal(-1);
		}else{
			sign=new BigDecimal(1);
		}
		
		long exp = getDoubleExp(longAsBits);
		long E = exp - 1023;
		System.out.println("E:"+E);
		BigDecimal v2E = get2E(E);
		System.out.println("2^E:"+v2E.toString());
		
		
		BigDecimal fraction = getDoubleFraction(longAsBits);
		System.out.println("fraction:"+fraction.toString());
		BigDecimal M = fraction.add(new BigDecimal(1));
		System.out.println("M:"+M.toString());
		
		return sign.multiply(M).multiply(v2E);
	}
	
	private static BigDecimal get2E(long E) {
		
		if(E>0){
			return new BigDecimal(1<<E);
		}else if(E==0){
			
			throw new RuntimeException("Not Yet Supported. Check P70 of Computer System - A programmer's perspective, CN 2nd");
		}else{
			return new BigDecimal(1).divide(new BigDecimal(1<<(-1*E)));
		}
	}

	private static BigDecimal getDoubleFraction(long longAsBits) {

		BigDecimal result = new BigDecimal(0);
		
		int double_fraction_start = 51; // included
		int double_fraction_end = 0; // included

		for(int i=double_fraction_start; i>=double_fraction_end; i--){
			
			long bitV = getBit(longAsBits,i);
			if(bitV == 1){
				
				//TODO will it overflow?
				long x = 1L<<(double_fraction_start-i+1);
				BigDecimal thisBitV = new BigDecimal(1).divide(new BigDecimal(x));
				//System.out.println("i:"+i+" v:"+thisBitV.toString());
				result =	result.add(thisBitV);
			}
		}

		return result;
	}
	private static long getDoubleExp(long longAsBits) {
		
		long x = longAsBits<<1;
		long y = x>>>(64-11);
		
		return y;
	}
	
	//http://stackoverflow.com/questions/9354860/how-to-get-the-value-of-a-bit-at-a-certain-position-from-a-byte
	public static long getBit(long v, int position)
	{
	   return (v >> position) & 1;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println(getExactV(6.0));
		System.out.println(getExactV(5.6));
		System.out.println(getExactV(6.0-5.6));
		System.out.println(getExactV(0.4));
	}

}

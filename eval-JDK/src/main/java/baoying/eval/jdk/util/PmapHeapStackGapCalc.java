package baoying.eval.jdk.util;

public class PmapHeapStackGapCalc {

	public static void main(String[] args){
		
		String heapBottomLine ="00030000  702464K rwx--    [ heap ]";
		String stackUpLine="50356000      40K rw--R    [ stack tid=520 ]";


		String 	strHexHeapStart = heapBottomLine.split(" +")[0];	
		String strHeapSizeInKB = heapBottomLine.split(" +")[1];
		if(strHeapSizeInKB.endsWith("K")){ strHeapSizeInKB = strHeapSizeInKB.substring(0, strHeapSizeInKB.length()-1);}
		
		int hexHeapStart = Integer.parseInt(strHexHeapStart, 16)  ;
		int heapSizeInKB = Integer.parseInt( strHeapSizeInKB)  ;
		
		int heapLowerBoundary = hexHeapStart + heapSizeInKB*1024;
		
		String strHexStackUpBoundary = stackUpLine.split(" ")[0];
		int hexStackUpBoundary = Integer.parseInt(strHexStackUpBoundary, 16)  ;;
		
		String formular = "strHexStackUpBoundary:"+strHexStackUpBoundary +" - (heapLowerBoundary:"+strHexHeapStart + " + " + strHeapSizeInKB +") KB" ; 
		System.out.println("gap between heap and stack:" + formular+" = " +(hexStackUpBoundary - heapLowerBoundary )/(1024*1.0) +" KB");
	}
}

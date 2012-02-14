/**
 * 
 */
package org.oki.transmodel;

/**
 * @author arohne
 *
 */
public class NonTransitLink {
	public int A;
	public int B;
	public int Mode;
	public float Distance;
	public float PeakTime;
	public float OffPeakTime;
	public float[] PeakVolume;
	public float[] OffPeakVolume;
	public int Region;
	
	public NonTransitLink(){
		A=0;
		B=0;
		Mode=0;
		Distance=0;
		PeakTime=0;
		OffPeakTime=0;
		PeakVolume=new float[9];
		OffPeakVolume=new float[9];
		for(int i=0;i<9;i++){
			PeakVolume[i]=0;
			OffPeakVolume[i]=0;
		}
		Region=0;
		
	}
	public String getRegion(){
		if(Mode!=103){
			if(A<1608||B<1608)
				return "OKI";
			else
				return "MVRPC";
		}else{
			if(A<13000 || B<13000 || A>21000 || B>21000)
				return "OKI";
			else
				return "MVRPC";
		}
	}
}

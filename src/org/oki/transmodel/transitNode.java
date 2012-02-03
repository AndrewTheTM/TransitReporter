/**
 * 
 */
package org.oki.transmodel;

/**
 * @author arohne
 *
 */
public class transitNode {
	public int N=0;
	public int[] on=new int[8];
	public int[] off=new int[8];
	public int seq=0;
	public int[] vol=new int[8];
	
	public transitNode(){
		N=0;
		
		//AM Boardings
		on[0]=0;
		on[1]=0;
		on[2]=0;
		on[3]=0;
		//MD Boardings
		on[4]=0;
		on[5]=0;
		on[6]=0;
		on[7]=0;
		
		//AM Alightings
		off[0]=0;
		off[1]=0;
		off[2]=0;
		off[3]=0;
		//MD Alightings
		off[4]=0;
		off[5]=0;
		off[6]=0;
		off[7]=0;
		
		//AM Volume
		vol[0]=0;
		vol[1]=0;
		vol[2]=0;
		vol[3]=0;
		//MD Volume
		vol[4]=0;
		vol[5]=0;
		vol[6]=0;
		vol[7]=0;
		
		seq=0;
	}
}

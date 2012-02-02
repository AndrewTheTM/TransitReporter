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
	public int[] on=new int[4];
	public int[] off=new int[4];
	public int seq=0;
	public int[] vol=new int[4];
	
	public transitNode(){
		N=0;
		
		on[0]=0;
		on[1]=0;
		on[2]=0;
		on[3]=0;
		off[0]=0;
		off[1]=0;
		off[2]=0;
		off[3]=0;
		vol[0]=0;
		vol[1]=0;
		vol[2]=0;
		vol[3]=0;
		seq=0;
	}
}

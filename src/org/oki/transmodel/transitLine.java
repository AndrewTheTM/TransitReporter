/**
 * 
 */
package org.oki.transmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arohne
 *
 */
public class transitLine {
	public String name;
	public String longName;
	public int AMHeadway;
	public int MDHeadway;
	public int PMHeadway;
	public int mode;
	public boolean oneWay;
	public boolean circular;
	public int operator;
	public int fareSystem;
	public int reportGroup;
	public List<transitNode> nodes;
	
	public transitLine(){
		name="";
		longName="";
		AMHeadway=0;
		MDHeadway=0;
		PMHeadway=0;
		mode=0;
		oneWay=false;
		operator=0;
		fareSystem=0;
		reportGroup=0;
		nodes=new ArrayList<transitNode>();
	}
	public void addNode(transitNode e){
		nodes.add(e);
	}
	public int getTotalBoardings(){
		int ttl=0;
		for(transitNode n:nodes)
		{
			ttl+=n.on[0]+n.on[1]+n.on[2]+n.on[3]+n.on[4]+n.on[5]+n.on[6]+n.on[7];
		}
		return ttl;
	}
	public int getTotalAlightings(){
		int ttl=0;
		for(transitNode n:nodes)
		{
			ttl+=n.off[0]+n.off[1]+n.off[2]+n.off[3]+n.off[4]+n.off[5]+n.off[6]+n.off[7];
		}
		return ttl;
	}
	public int getAMBoardings(){
		int ttl=0;
		for(transitNode n:nodes)
		{
			ttl+=n.on[0]+n.on[1]+n.on[2]+n.on[3];
		}
		return ttl;
	}
	public int getAMAlightings(){
		int ttl=0;
		for(transitNode n:nodes)
		{
			ttl+=n.off[0]+n.off[1]+n.off[2]+n.off[3];
		}
		return ttl;
	}
	public int getMDBoardings(){
		int ttl=0;
		for(transitNode n:nodes)
		{
			ttl+=n.on[4]+n.on[5]+n.on[6]+n.on[7];
		}
		return ttl;
	}
	public int getMDAlightings(){
		int ttl=0;
		for(transitNode n:nodes)
		{
			ttl+=n.off[4]+n.off[5]+n.off[6]+n.off[7];
		}
		return ttl;
	}

}

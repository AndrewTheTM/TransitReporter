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

}

package org.oki.transmodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author arohne
 *
 */
public class ReportTransit {
	/*
	 * Main class for reporting transit from OKI Model
	 */
	private List<transitLine> transitLines=new ArrayList<transitLine>();
	
	public static void main(String[] args) throws FileNotFoundException{
		System.out.println(args[0]);
		ReportTransit.readLinFile(args[0]);
	}
	static void readLinFile(String filename) throws FileNotFoundException{
		StringBuilder text=new StringBuilder();
		//String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(filename));
		try{
			while(scanner.hasNextLine()){
				text.append(scanner.nextLine());
			}
		}
		finally{
			scanner.close();
			String lineBlock[];
			lineBlock=text.toString().split("LINE ");
			for(String line : lineBlock){
				System.out.println(line.trim().substring(0,4));
				transitLine tLine = new transitLine();
				
				if(line.trim().substring(0,4).equals("NAME")){
					line=line.trim().replace(","," ");
					int a=line.indexOf("NAME=")+5;
					int c=line.indexOf(" ", a);
					tLine.name=line.substring(a,c);
					
					a=line.indexOf("LONGNAME=")+9;
					c=line.indexOf("' ",a);
					tLine.longName=line.substring(a,c).replace("'", "").trim();
					
					a=line.indexOf("HEADWAY[1]=")+11;
					if(a==10)
						a=line.indexOf("HEADWAY")+8;
					c=line.indexOf(" ",a);
					tLine.AMHeadway=Integer.parseInt(line.substring(a,c));
					
					a=line.indexOf("HEADWAY[2]=")+11;
					if(a>10)
					{
						c=line.indexOf(" ",a);
						tLine.MDHeadway=Integer.parseInt(line.substring(a,c));
					}else{
						tLine.MDHeadway=0;
					}
					
					a=line.indexOf("HEADWAY[3]=")+11;
					if(a>10)
					{
						c=line.indexOf(" ",a);
						tLine.PMHeadway=Integer.parseInt(line.substring(a,c));
					}else{
						tLine.PMHeadway=0;
					}
					
					a=line.indexOf("MODE=")+5;
					c=line.indexOf(" ",a);
					tLine.mode=Integer.parseInt(line.substring(a,c));
					
					a=line.indexOf("ONEWAY=")+7;
					if (a > 6) {
						c = line.indexOf(" ", a);
						if (line.substring(a, c) == "T")
							tLine.oneWay = true;
						else
							tLine.oneWay = false;
					}
					a=line.indexOf("CIRCULAR=")+9;
					if(a>8){
						c=line.indexOf(" ", c);
						if(line.substring(a,c)=="T")
							tLine.circular=true;
						else
							tLine.circular=false;
					}
					
					a=line.indexOf("OPERATOR=")+9;
					c=line.indexOf(" ", a);
					tLine.operator=Integer.parseInt(line.substring(a,c));
					
					a=line.indexOf("FARESYSTEM=")+11;
					c=line.indexOf(" ", a);
					tLine.fareSystem=Integer.parseInt(line.substring(a, c));
					
					a=line.indexOf("USERN2=")+7;
					if(a>6)
					{
						c=line.indexOf(" ", a);
						tLine.reportGroup=Integer.parseInt(line.substring(a,c));
					}else{
						tLine.reportGroup=0;
					}
					
					//N
					a=line.indexOf("N=")+2;
					c=line.length();
					String nEle[];
					String nLine=line.substring(a, c);
					nEle=nLine.split(" N=");
					int sequ=0;
					
					for(String nElement : nEle){
						transitNode tNode = new transitNode();
						int onStart=nElement.indexOf("ON");
						int offStart=nElement.indexOf("OFF");
						int volStart=nElement.indexOf("VOL");
						
						//get N
						if(onStart>0)
							tNode.N=Integer.parseInt(nElement.substring(0,onStart).trim());
						else if(offStart>0)
							tNode.N=Integer.parseInt(nElement.substring(0,offStart).trim());
						else if(volStart>0)
							tNode.N=Integer.parseInt(nElement.substring(0,volStart).trim());
						else
							tNode.N=Integer.parseInt(nElement.trim());

						//get On
						if(onStart>0)
						{
							onStart+=2;
							System.out.println(nElement.substring(onStart+1,onStart+2));
							int startArray=0, onStartAdd=0;
							if(nElement.substring(onStart,onStart+1).equals("["))
							{
								startArray=Integer.parseInt(nElement.substring(onStart+1,onStart+2));
								onStartAdd=4;
								
							}
							String tmp;
							if(offStart>0)
								tmp=nElement.substring(onStart+onStartAdd, offStart).replace("[", "").replace("]", "");
							else if(volStart>0)
								tmp=nElement.substring(onStart+onStartAdd, volStart).replace("[", "").replace("]", "");
							else
								tmp=nElement.substring(onStart+onStartAdd,nElement.length());
							
							String ele[];
							ele=tmp.split("\\s");
							for(int i=0;i<4;i++)
							{
								if(i>=startArray){
									tNode.on[i]=Integer.parseInt(ele[i-startArray].trim());
								}
							}
								//tNode.on[i+startArray-1]=Integer.parseInt(ele[i]);
						}
						
						//get off
						/*
						if(offStart>0)
						{
							offStart+=3;
							int startArray=0, onStartAdd=0;
							if(nElement.substring(offStart, offStart+1).equals("["))
							{
								startArray=Integer.parseInt(nElement.substring(offStart+1,offStart+2));
								onStartAdd=4;
							}
							String tmp;
							if(volStart>0)
								tmp=nElement.substring(offStart+onStartAdd, volStart).replace("[","").replace("]","");
							else
								tmp=nElement.substring(offStart+onStartAdd, nElement.length());
							
							String ele[];
							ele=tmp.split("\\s");
							for(int i=startArray-1;i<4;i++)
								tNode.off[i]=Integer.parseInt(ele[startArray-1-i]);
						}
						*/
						/*
						//get vol
						if(volStart>0)
						{
							volStart+=3;
							int startArray=0, volStartAdd=0;
							if(nElement.substring(volStart, volStart+1).equals("["))
							{
								startArray=Integer.parseInt(nElement.substring(volStart+1, volStart+2));
								volStartAdd=4;
							}
							String tmp;
							tmp=nElement.substring(volStart+volStartAdd,nElement.length());
							String ele[];
							ele=tmp.split("\\s");
							for(int i=startArray-1;i<4;i++)
								tNode.vol[i]=Integer.parseInt(ele[startArray-1-i]);
						}
						*/
						tLine.nodes.add(tNode);
						
						//ON OFF VOL
						int stopme=0;
						/*
						if(nElement.indexOf("VOL")>0){
							int d=nElement.indexOf("VOL"+4);
							int e=Math.max(nElement.indexOf("ON="),nElement.length());
							String vols[];
							vols=nElement.substring(d,e).split("\\s");
							
						}
						*/
					}
					

					/*
					 * N=5939,
				       ON=0 VOL=0 N=5486 ON=0 OFF=0 VOL=1 N=5552 ON=0 OFF=0 VOL=1 N=-5553 VOL=1,
				       N=5596 VOL=1 N=5554 ON=5 OFF=0 VOL=6 N=5555 ON=3 OFF=1 VOL=8 N=5556 ON=0,
				       VOL=8 N=8736 VOL=8 N=8747 VOL=8 N=8742 VOL=8 N=5557 OFF=0 VOL=8 N=-5558,
				       VOL=8 N=-6047 VOL=8 N=-5602 VOL=8 N=-5603 VOL=8 N=5604 ON=3 OFF=1 VOL=10,
				       N=6386 VOL=10 N=6387 VOL=10 N=5605 ON=1 OFF=1 VOL=10 N=5606 ON=1 OFF=1,
				       VOL=10 N=5941 OFF=0 VOL=9 N=5484 ON=3 OFF=0 VOL=12 N=5942 ON=1 OFF=0 VOL=13,
				       N=5460 ON=0 VOL=13 N=5459 ON=6 0 OFF=1 VOL=18 0 N=5457 ON=1 0 OFF=2 VOL=17 0,
				       N=5945 ON=2 0 OFF=1 VOL=19 0 N=5456 ON=1 OFF=0 VOL=19 0 N=5455 OFF=1 VOL=18,
				       0 N=5454 ON=1 0 VOL=20 0 N=5617 ON=1 OFF=0 VOL=20 0 N=5612 VOL=20 0,
				       N=5626 VOL=20 0 N=5619 VOL=20 0 N=5618 ON=0 OFF=1 VOL=19 0 N=5974 ON=0 OFF=0,
				       VOL=19 0 N=5723 ON=1 OFF=0 VOL=19 0 N=5722 ON=2 OFF=1 VOL=21 0 N=5721 ON=2 0,
				       OFF=1 VOL=22 0 N=5725 OFF=0 VOL=22 0 N=5729 ON=0 VOL=22 0 N=5732 ON=0 OFF=1
					 */

					
					/////\/\/\/\/\
					int b=0;
					System.out.println(line.trim().substring(a,c));
					System.out.println(c);
				}
			}
		}
	}

}

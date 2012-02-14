package org.oki.transmodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.xBaseJ.*;
import org.xBaseJ.fields.NumField;


/**
 * @author arohne
 *
 */
public class ReportTransit {
	/*
	 * Main class for reporting transit from OKI Model
	 */
	protected static List<transitLine> transitLines=new ArrayList<transitLine>();
	protected static List<NonTransitLink> ntLinks=new ArrayList<NonTransitLink>();
	
	public static void main(String[] args) throws IOException{
		if(args.length<3 || args.length==4 || args.length>5){
			System.out.println("USAGE:");
			System.out.println("java -jar TransitReporter.jar peak.lin offpeak.lin [peak.dbf] [offpeak.dbf] report.rpt");
			System.exit(0);
		}
		
		ReportTransit.readAMLinFile(args[0]);
		ReportTransit.readMDLinFile(args[1]);
		if(args.length==3)
			writeReport(args[2]);
		if(args.length==5){
			readNTLinks(args[2],args[3]);
			writeReport(args[4]);
		}
		
		
	}
	static void readAMLinFile(String filename) throws FileNotFoundException{
		StringBuilder text=new StringBuilder();
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
				transitLine tLine = new transitLine();
				
				if(line.trim().substring(0,4).equals("NAME")){
					line=line.trim().replace(","," ");
					int a=line.indexOf("NAME=")+5;
					int c=line.indexOf(" ", a);
					tLine.name=line.substring(a,c);
					
					a=line.indexOf("LONGNAME=")+9;
					c=line.indexOf("' ",a);
					if(c<0)
						c=line.indexOf(" ",a);
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
						c=line.indexOf(" ", a);
						if(line.substring(a,c).equals("1"))
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
					int seq=1;
					
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
							String onString;
							if(offStart>0)
								onString=nElement.substring(onStart, offStart).trim();
							else if(volStart>0)
								onString=nElement.substring(onStart, volStart).trim();
							else
								onString=nElement.substring(onStart, nElement.length()).trim();
							
							//check if indexed... onString is either ON=n n n n or ON[x]=n n or ON=2 ON[3]=0
							String ele[];
							ele=onString.split("\\s");
							int curEle=0;
							for(String sEle:ele){
								if(sEle.length()==0)
								{
									//maybe do nothing?
								}else if(isInteger(sEle)){
									tNode.on[curEle]=Integer.parseInt(sEle);
									curEle++;
								}else if(sEle.substring(0,3).equals("ON=")){
									tNode.on[curEle]=Integer.parseInt(sEle.substring(3,sEle.length()));
									curEle++;
								}else if(sEle.substring(0,3).equals("ON[")){
									curEle=Integer.parseInt(sEle.substring(3,4))-1;
									tNode.on[curEle]=Integer.parseInt(sEle.substring(6, sEle.length()));
									curEle++;
								}
							}
						}
						
						//get off
						if(offStart>0)
						{
							String offString;
							if(volStart>0)
								offString=nElement.substring(offStart, volStart).trim();
							else
								offString=nElement.substring(offStart, nElement.length()).trim();
							
							//check if indexed... onString is either ON=n n n n or ON[x]=n n or ON=2 ON[3]=0
							String ele[];
							ele=offString.split("\\s");
							int curEle=0;
							for(String sEle:ele){
								if(sEle.length()==0)
								{
									//maybe do nothing?
								}else if(isInteger(sEle)){
									tNode.off[curEle]=Integer.parseInt(sEle);
									curEle++;
								}else if(sEle.substring(0,4).equals("OFF=")){
									tNode.off[curEle]=Integer.parseInt(sEle.substring(4,sEle.length()));
									curEle++;
								}else if(sEle.substring(0,4).equals("OFF[")){
									curEle=Integer.parseInt(sEle.substring(4,5))-1;
									tNode.off[curEle]=Integer.parseInt(sEle.substring(7, sEle.length()));
									curEle++;
								}
							}
						}
						//get vol
						if(volStart>0)
						{
							String volString;
							volString=nElement.substring(volStart, nElement.length()).trim();
							
							//check if indexed... onString is either ON=n n n n or ON[x]=n n or ON=2 ON[3]=0
							String ele[];
							ele=volString.split("\\s");
							int curEle=0;
							for(String sEle:ele){
								if(sEle.length()==0)
								{
									//maybe do nothing?
								}else if(isInteger(sEle)){
									tNode.vol[curEle]=Integer.parseInt(sEle);
									curEle++;
								}else if(sEle.substring(0,4).equals("VOL=")){
									tNode.vol[curEle]=Integer.parseInt(sEle.substring(4,sEle.length()));
									curEle++;
								}else if(sEle.substring(0,4).equals("VOL[")){
									curEle=Integer.parseInt(sEle.substring(4,5))-1;
									tNode.vol[curEle]=Integer.parseInt(sEle.substring(7, sEle.length()));
									curEle++;
								}
							}
						}
						
						tNode.seq=seq;
						seq++;
						tLine.nodes.add(tNode);	
					}
					transitLines.add(tLine);
					
				} //line name block
			} // line for loop
		} // finally
	} // static void
	
	static void readMDLinFile(String filename) throws FileNotFoundException{
		StringBuilder text = new StringBuilder();
		Scanner scanner = new Scanner(new FileInputStream(filename));
		try {
			while (scanner.hasNextLine()) {
				text.append(scanner.nextLine());
			}
		} finally {
			scanner.close();
			String lineBlock[];
			lineBlock = text.toString().split("LINE ");
			for (String line : lineBlock) {
				// transitLine tLine = new transitLine();
				String cLine;
				if (line.trim().substring(0, 4).equals("NAME")) {
					line = line.trim().replace(",", " ");
					int a = line.indexOf("NAME=") + 5;
					int c = line.indexOf(" ", a);
					cLine = line.substring(a, c);

					for (transitLine tLine : transitLines) {
						if (tLine.name.equals(cLine)) {

							// N
							a = line.indexOf("N=") + 2;
							c = line.length();
							String nEle[];
							String nLine = line.substring(a, c);
							nEle = nLine.split(" N=");

							for (String nElement : nEle) {
								int onStart = nElement.indexOf("ON");
								int offStart = nElement.indexOf("OFF");
								int volStart = nElement.indexOf("VOL");

								int theN;

								// get N
								if (onStart > 0)
									theN = Integer.parseInt(nElement.substring(0, onStart).trim());
								else if (offStart > 0)
									theN = Integer.parseInt(nElement.substring(0, offStart).trim());
								else if (volStart > 0)
									theN = Integer.parseInt(nElement.substring(0, volStart).trim());
								else
									theN = Integer.parseInt(nElement.trim());

								for (transitNode tNode : tLine.nodes) {
									if (tNode.N == theN) {

										// get On
										if (onStart > 0) {
											String onString;
											if (offStart > 0)
												onString = nElement.substring(onStart, offStart).trim();
											else if (volStart > 0)
												onString = nElement.substring(onStart, volStart).trim();
											else
												onString = nElement.substring(onStart,nElement.length()).trim();

											// check if indexed... onString is
											// either ON=n n n n or ON[x]=n n or
											// ON=2 ON[3]=0
											String ele[];
											ele = onString.split("\\s");
											int curEle = 4;
											for (String sEle : ele) {
												if (sEle.length() == 0) {
													// maybe do nothing?
												} else if (isInteger(sEle)) {
													tNode.on[curEle] = Integer.parseInt(sEle);
													curEle++;
												} else if (sEle.substring(0, 3).equals("ON=")) {
													tNode.on[curEle] = Integer.parseInt(sEle.substring(3,sEle.length()));
													curEle++;
												} else if (sEle.substring(0, 3).equals("ON[")) {
													curEle = Integer.parseInt(sEle.substring(3,	4)) +3;
													tNode.on[curEle] = Integer.parseInt(sEle.substring(6,sEle.length()));
													curEle++;
												}
											}
										}

										// get off
										if (offStart > 0) {
											String offString;
											if (volStart > 0)
												offString = nElement.substring(offStart, volStart).trim();
											else
												offString = nElement.substring(offStart,nElement.length()).trim();

											// check if indexed... onString is
											// either ON=n n n n or ON[x]=n n or
											// ON=2 ON[3]=0
											String ele[];
											ele = offString.split("\\s");
											int curEle = 4;
											for (String sEle : ele) {
												if (sEle.length() == 0) {
													// maybe do nothing?
												} else if (isInteger(sEle)) {
													tNode.off[curEle] = Integer.parseInt(sEle);
													curEle++;
												} else if (sEle.substring(0, 4).equals("OFF=")) {
													tNode.off[curEle] = Integer.parseInt(sEle.substring(4,sEle.length()));
													curEle++;
												} else if (sEle.substring(0, 4).equals("OFF[")) {
													curEle = Integer.parseInt(sEle.substring(4,5)) +3;
													tNode.off[curEle] = Integer.parseInt(sEle.substring(7,sEle.length()));
													curEle++;
												}
											}
										}
										// get vol
										if (volStart > 0) {
											String volString;
											volString = nElement.substring(volStart,nElement.length()).trim();

											// check if indexed... onString is
											// either ON=n n n n or ON[x]=n n or
											// ON=2 ON[3]=0
											String ele[];
											ele = volString.split("\\s");
											int curEle = 4;
											for (String sEle : ele) {
												if (sEle.length() == 0) {
													// maybe do nothing?
												} else if (isInteger(sEle)) {
													tNode.vol[curEle] = Integer.parseInt(sEle);
													curEle++;
												} else if (sEle.substring(0, 4)
														.equals("VOL=")) {
													tNode.vol[curEle] = Integer.parseInt(sEle.substring(4,sEle.length()));
													curEle++;
												} else if (sEle.substring(0, 4).equals("VOL[")) {
													curEle = Integer.parseInt(sEle.substring(4,5)) +3;
													tNode.vol[curEle] = Integer.parseInt(sEle.substring(7,sEle.length()));
													curEle++;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static boolean isInteger(String input)
	{
		try
		{
			Integer.parseInt(input);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public static void dumpLines(){
		for(transitLine tLine:transitLines)
		{
			System.out.println(tLine.name+", "+tLine.getAMBoardings()+", "+tLine.getMDBoardings()+", "+tLine.getTotalBoardings());
		}
	}
	
	public static void writeReport(String filename) throws IOException{
		System.out.println("Writing Report File...");
		Writer out=new OutputStreamWriter(new FileOutputStream(filename));
		try{
			try{
				out.write((char)27+"&l0O"+(char)27+"(s18H"+(char)27+"&l8D\r\n");
				out.write("OKI MODEL TRANSIT REPORT\r\n\n");
				out.write("                       TRANSIT BOARDINGS BY CORRIDOR\r\n");
				//&l1O(s18H&l8D
			
				//Write corridor level report
				int[][] corridorBoards=new int[14][3];
				int[][] companyBoards=new int[10][3];
				int[][] modeBoards=new int[10][3];
				int[][][] modeCoBoards=new int[10][10][3];
				
				
				for(transitLine tLine:transitLines)
				{
					corridorBoards[tLine.reportGroup][0]+=tLine.getAMBoardings();
					corridorBoards[tLine.reportGroup][1]+=tLine.getMDBoardings();
					corridorBoards[tLine.reportGroup][2]+=tLine.getAMBoardings()+tLine.getMDBoardings();
					companyBoards[tLine.operator][0]+=tLine.getAMBoardings();
					companyBoards[tLine.operator][1]+=tLine.getMDBoardings();
					companyBoards[tLine.operator][2]+=tLine.getAMBoardings()+tLine.getMDBoardings();
					modeBoards[tLine.mode][0]+=tLine.getAMBoardings();
					modeBoards[tLine.mode][1]+=tLine.getMDBoardings();
					modeBoards[tLine.mode][2]+=tLine.getAMBoardings()+tLine.getMDBoardings();
					modeCoBoards[tLine.mode][tLine.operator][0]+=tLine.getAMBoardings();
					modeCoBoards[tLine.mode][tLine.operator][1]+=tLine.getMDBoardings();
					modeCoBoards[tLine.mode][tLine.operator][2]+=tLine.getAMBoardings()+tLine.getMDBoardings();
				}
				
				//out.write("|"+String.format(String.format("%%0%dd", 80), 0).replace("0","-")+"|");
				out.write("+--------------------------------------------------------------------------+\r\n");
				out.write("|  CORRIDOR   |  PEAK BOARDINGS  |  OFF-PK BOARDINGS  |  TOTAL BOARDINGS   |\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n");
				int rtAMB=0, rtMDB=0, rtTTB=0;
				for(int i=1;i<14;i++)
				{
					out.write("|    "+String.format("%1$2d",i)+"       |"+String.format("%,16d",corridorBoards[i][0])+"  |"+String.format("%,18d",corridorBoards[i][1])+"  |"+String.format("%,17d",corridorBoards[i][2])+"   |\r\n");
					rtAMB+=corridorBoards[i][0];
					rtMDB+=corridorBoards[i][1];
					rtTTB+=corridorBoards[i][2];
				}
				out.write("+--------------------------------------------------------------------------+\r\n");
				out.write("|     TOTAL   |"+String.format("%,16d", rtAMB)+"  |"+String.format("%,18d", rtMDB)+"  |"+String.format("%,17d",rtTTB)+"   |\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n\n\n");
				
				out.write("                       TRANSIT BOARDINGS BY COMPANY\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n");
				out.write("|   COMPANY   |  PEAK BOARDINGS  |  OFF-PK BOARDINGS  |  TOTAL BOARDINGS   |\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n");
				rtAMB=0; rtMDB=0; rtTTB=0;
				for(int i=1;i<7;i++)
				{
					out.write("|    "+String.format("%1$2d",i)+"       |"+String.format("%,16d",companyBoards[i][0])+"  |"+String.format("%,18d",companyBoards[i][1])+"  |"+String.format("%,17d",companyBoards[i][2])+"   |\r\n");
					rtAMB+=companyBoards[i][0];
					rtMDB+=companyBoards[i][1];
					rtTTB+=companyBoards[1][2];
				}
				out.write("+--------------------------------------------------------------------------+\r\n");
				out.write("|     TOTAL   |"+String.format("%,16d", rtAMB)+"  |"+String.format("%,18d", rtMDB)+"  |"+String.format("%,17d",rtTTB)+"   |\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n\n\n");
				
				out.write("                           TRANSIT BOARDINGS BY MODE\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n");
				out.write("|      MODE   |  PEAK BOARDINGS  |  OFF-PK BOARDINGS  |  TOTAL BOARDINGS   |\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n");
				rtAMB=0; rtMDB=0; rtTTB=0;
				for(int i=1;i<6;i++)
				{
					out.write("|    "+String.format("%1$2d",i)+"       |"+String.format("%,16d",modeBoards[i][0])+"  |"+String.format("%,18d",modeBoards[i][1])+"  |"+String.format("%,17d",modeBoards[i][2])+"   |\r\n");
					rtAMB+=modeBoards[i][0];
					rtMDB+=modeBoards[i][1];
					rtTTB+=modeBoards[1][2];
				}
				out.write("+--------------------------------------------------------------------------+\r\n");
				out.write("|     TOTAL   |"+String.format("%,16d", rtAMB)+"  |"+String.format("%,18d", rtMDB)+"  |"+String.format("%,17d",rtTTB)+"   |\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n\n\n");
				
				
				out.write("                    TRANSIT BOARDINGS BY MODE AND COMPANY\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n");
				out.write("| CO |  MODE  |  PEAK BOARDINGS  |  OFF-PK BOARDINGS  |  TOTAL BOARDINGS   |\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n");
				
				rtAMB=0; rtMDB=0; rtTTB=0;
				for(int c=1;c<7;c++)
				{
					for(int m=1;m<6;m++)
					{
						if(modeCoBoards[m][c][2]>0)
						{
							out.write("| "+String.format("%2d",c)+" |  "+String.format("%1$4d",m)+"  |"+String.format("%,16d", modeCoBoards[m][c][0])+"  |"+String.format("%,18d",modeCoBoards[m][c][1])+"  |"+String.format("%,17d",modeCoBoards[m][c][2])+"   |\r\n");
							rtAMB+=modeCoBoards[m][c][0];
							rtMDB+=modeCoBoards[m][c][1];
							rtTTB+=modeCoBoards[m][c][2];
						}
					}
				}
				out.write("+--------------------------------------------------------------------------+\r\n");
				out.write("|     TOTAL   |"+String.format("%,16d", rtAMB)+"  |"+String.format("%,18d", rtMDB)+"  |"+String.format("%,17d",rtTTB)+"   |\r\n");
				out.write("+--------------------------------------------------------------------------+\r\n\n\n");
				out.write((char)27+"&l0H\r\n");  //eject page
				//out.write("This should be page 2");
			}finally{
				out.close();
				System.out.println("Completed!");
			}
		}catch (IOException e){
			System.out.println("There was an error");
			e.printStackTrace();
		}finally{
			out.close();
		}
	}

	public static void readNTLinks(String filenamePk, String filenameOp) throws IOException{
		try {
			DBF classDB=new DBF(filenamePk);

			NumField fldA=(NumField)classDB.getField("A");
			NumField fldB=(NumField)classDB.getField("B");
			NumField fldMode=(NumField)classDB.getField("Mode");
			NumField fldDist=(NumField)classDB.getField("Dist");
			NumField fldTime=(NumField)classDB.getField("Time");
			NumField fldTVol=(NumField)classDB.getField("VOL");
			
			NumField fldVol1=null;
			NumField fldVol2=null;
			NumField fldVol3=null;
			NumField fldVol4=null;
			NumField fldVol5=null;
			NumField fldVol6=null;
			NumField fldVol7=null;
			NumField fldVol8=null;
			
			for(int f=1;f<=classDB.getFieldCount();f++){
				if(classDB.getField(f).getName().equals("VOL_1"))
					fldVol1=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_2"))
					fldVol2=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_3"))
					fldVol3=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_4"))
					fldVol4=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_5"))
					fldVol5=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_6"))
					fldVol6=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_7"))
					fldVol7=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_8"))
					fldVol8=(NumField)classDB.getField(f);
			}
			
			for(int r=1; r<=classDB.getRecordCount();r++){
				classDB.read();
				if(Float.parseFloat(fldTVol.get().trim())>0){
					NonTransitLink tmpNTL=new NonTransitLink();
					
					tmpNTL.A=Integer.parseInt(fldA.get().trim());
					tmpNTL.B=Integer.parseInt(fldB.get().trim());
					tmpNTL.Distance=Float.parseFloat(fldDist.get().trim());
					tmpNTL.Mode=Integer.parseInt(fldMode.get().trim());
					tmpNTL.PeakTime=Float.parseFloat(fldTime.get().trim());
					
					if(fldVol1!=null)
						tmpNTL.PeakVolume[1]=Float.parseFloat(fldVol1.get().trim());
					if(fldVol2!=null)
						tmpNTL.PeakVolume[2]=Float.parseFloat(fldVol2.get().trim());
					if(fldVol3!=null)
						tmpNTL.PeakVolume[3]=Float.parseFloat(fldVol3.get().trim());
					if(fldVol4!=null)
						tmpNTL.PeakVolume[4]=Float.parseFloat(fldVol4.get().trim());
					if(fldVol5!=null)
						tmpNTL.PeakVolume[5]=Float.parseFloat(fldVol5.get().trim());
					if(fldVol6!=null)
						tmpNTL.PeakVolume[6]=Float.parseFloat(fldVol6.get().trim());
					if(fldVol7!=null)
						tmpNTL.PeakVolume[7]=Float.parseFloat(fldVol7.get().trim());
					if(fldVol8!=null)
						tmpNTL.PeakVolume[8]=Float.parseFloat(fldVol8.get().trim());
					ntLinks.add(tmpNTL);
				}
			}
			
			classDB=null;
			fldA=null;
			fldB=null;
			fldMode=null;
			fldDist=null;
			fldTime=null;
			fldTVol=null;
			
			classDB=new DBF(filenameOp);
			fldA=(NumField)classDB.getField("A");
			fldB=(NumField)classDB.getField("B");
			fldMode=(NumField)classDB.getField("Mode");
			fldDist=(NumField)classDB.getField("Dist");
			fldTime=(NumField)classDB.getField("Time");
			fldTVol=(NumField)classDB.getField("VOL");
			for(int f=1;f<=classDB.getFieldCount();f++){
				if(classDB.getField(f).getName().equals("VOL_1"))
					fldVol1=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_2"))
					fldVol2=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_3"))
					fldVol3=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_4"))
					fldVol4=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_5"))
					fldVol5=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_6"))
					fldVol6=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_7"))
					fldVol7=(NumField)classDB.getField(f);
				else if(classDB.getField(f).getName().equals("VOL_8"))
					fldVol8=(NumField)classDB.getField(f);
			}
			
			for(int r=1; r<=classDB.getRecordCount();r++){
				classDB.read();
				if(Float.parseFloat(fldTVol.get().trim())>0){
					int tmpA, tmpB, tmpMode;
					float tmpVol[];
					tmpVol=new float[9];
					float tmpDist, tmpTime;
					
					tmpA=Integer.parseInt(fldA.get().trim());
					tmpB=Integer.parseInt(fldB.get().trim());
					tmpDist=Float.parseFloat(fldDist.get().trim());
					tmpMode=Integer.parseInt(fldMode.get().trim());
					tmpTime=Float.parseFloat(fldTime.get().trim());
					
					if(fldVol1!=null)
						tmpVol[1]=Float.parseFloat(fldVol1.get().trim());
					if(fldVol2!=null)
						tmpVol[2]=Float.parseFloat(fldVol2.get().trim());
					if(fldVol3!=null)
						tmpVol[3]=Float.parseFloat(fldVol3.get().trim());
					if(fldVol4!=null)
						tmpVol[4]=Float.parseFloat(fldVol4.get().trim());
					if(fldVol5!=null)
						tmpVol[5]=Float.parseFloat(fldVol5.get().trim());
					if(fldVol6!=null)
						tmpVol[6]=Float.parseFloat(fldVol6.get().trim());
					if(fldVol7!=null)
						tmpVol[7]=Float.parseFloat(fldVol7.get().trim());
					if(fldVol8!=null)
						tmpVol[8]=Float.parseFloat(fldVol8.get().trim());
					
					
					checkNTLink:{
						for(NonTransitLink tmpNTL : ntLinks){
							if(tmpNTL.A==tmpA && tmpNTL.B==tmpB && tmpNTL.Mode==tmpMode){
								tmpNTL.OffPeakTime=tmpTime;
								for(int x=1;x<9;x++)
									tmpNTL.OffPeakVolume[1]=tmpVol[1];
								break checkNTLink;
							}
						}
						NonTransitLink tmpNTL=new NonTransitLink();
						tmpNTL.A=tmpA;
						tmpNTL.B=tmpB;
						tmpNTL.Mode=tmpMode;
						tmpNTL.Distance=tmpDist;
						tmpNTL.OffPeakTime=tmpTime;
						for(int x=1;x<9;x++)
							tmpNTL.OffPeakVolume[1]=tmpVol[1];
						ntLinks.add(tmpNTL);
					}
					
				}
			}
		} catch (xBaseJException e) {
			e.printStackTrace();
		}
		
	}
}

package org.oki.transmodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * @author arohne
 *
 */
public class ReportTransit {
	/*
	 * Main class for reporting transit from OKI Model
	 */
	public static void main(String[] args) throws FileNotFoundException{
		System.out.println(args[0]);
		ReportTransit.readLinFile(args[0]);
	}
	static void readLinFile(String filename) throws FileNotFoundException{
		StringBuilder text=new StringBuilder();
		String NL = System.getProperty("line.separator");
		Scanner scanner = new Scanner(new FileInputStream(filename));
		try{
			while(scanner.hasNextLine()){
				text.append(scanner.nextLine()+NL);
			}
		}
		finally{
			scanner.close();
			String lineBlock[];
			lineBlock=text.toString().split("LINE ");
			for(String line : lineBlock){
				System.out.println(line.trim().substring(0,4));
				if(line.trim().substring(0,4).equals("NAME")){
					String components[];
					//TODO: fix the f**king regex string to get it working.
					String regex="[\\s=\"']+|\"([\"]*)\"|'([']*)'";
					components=line.split(regex);
					int a=1;
				}
				
			}
		}
		
		
		
		
		/*
		 * LINE  NAME=M4L68 LONGNAME='46 AVONVIEW IB' HEADWAY=15 HEADWAY[2]=13,
	       MODE=1 ONEWAY=1 OPERATOR=1 FARESYSTEM=1 USERA1=46 USERN2=4 N=5745 ON=40 2,
	       VOL=40 2 N=5743 ON=10 1 VOL=49 2 N=5741 VOL=49 2 N=5739 ON=16 1 OFF=1 VOL=64,
	       4 N=6006 ON=28 2 OFF=2 0 VOL=91 5 N=5080 ON=2 1 ON[4]=0 OFF=7 0 VOL=86,
	       6 VOL[4]=0 N=4977 ON=4 1 OFF=3 0 VOL=87 7 VOL[4]=0 N=4978 ON=5 4 6 0,
	       OFF=26 1 VOL=66 10 6 0 N=5074 ON=4 0 OFF=0 VOL=70 10 6 0 N=5071 ON=2 1,
	       VOL=71 11 6 0 N=5070 ON=9 1 3 0 OFF=5 0 VOL=76 12 9 0 N=5025 ON=1 1 OFF=3 0,
	       VOL=74 12 9 0 N=5024 ON=14 2 OFF=0 0 VOL=87 14 9 0 N=5023 ON=7 2 1 OFF=4 0,
	       VOL=91 15 9 0 N=5022 ON=9 1 OFF=1 0 VOL=99 15 9 0 N=4709 ON=4 0 OFF=1 0,
	       VOL=102 16 9 0 N=4897 ON=36 6 1 OFF=32 2 VOL=106 19 11 0 N=4724 OFF=5 0,
	       VOL=101 19 11 0 N=3259 VOL=101 19 11 0 N=4747 ON=6 0 OFF=7 0 VOL=101 19 11 0,
	       N=4763 ON=21 3 OFF=3 0 VOL=119 22 11 0 N=3256 ON=3 0 OFF=6 0 VOL=115 22 11 0,
	       N=4786 ON=1 VOL=116 22 11 0 N=4890 ON=9 2 OFF=26 1 VOL=98 23 11 0 N=4804,
	       ON=0 VOL=99 23 11 0 N=4852 ON=1 OFF=4 1 VOL=95 22 11 0 N=-4203 VOL=95 22,
	       11 0 N=-4217 VOL=95 22 11 0 N=-11852 VOL=95 22 11 0 N=-4230 VOL=95 22 11,
	       0 N=-4247 VOL=95 22 11 0 N=-4272 VOL=95 22 11 0 N=-11840 VOL=95 22 11 0,
	       N=-4268 VOL=95 22 11 0 N=-4285 VOL=95 22 11 0 N=-4306 VOL=95 22 11 0 N=-4320,
	       VOL=95 22 11 0 N=-4333 VOL=95 22 11 0 N=4335 ON=3 2 2 OFF=58 13 1 VOL=41 11,
	       11 0 N=-4337 VOL=41 11 11 0 N=4339 OFF=41 11 11 0
		 */
	}

}

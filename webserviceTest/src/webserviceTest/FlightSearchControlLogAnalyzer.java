package webserviceTest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 分析航班并发日志
 * */
public class FlightSearchControlLogAnalyzer {
	
	//统计间隔 ,单位为分钟     1,2,3,4,5,6,10
	private static int space = 0 ;
	
	//文件基础名
	private static String file="C:/Users/pengchengzhang/Desktop/";
	
	//文件中间名
	private static String fileType=".txt." ;
	
	//日期1
	private static String day1 = "2018-05-17" ;
	
	//日期2
	//private static String day2 = "2018-03-19" ;
	
	//数据容器
	private static Map<String, HashMap<String, MinuteData>> datas =  new HashMap<String,HashMap<String,MinuteData>>();
	
	
	@SuppressWarnings("resource")
	public static void main(String[] args){
		
		
		//筛选数据
		for(int i=35;i<=44;i++){
			
			String key = "10.220.42."+ i ;
			
			HashMap<String,MinuteData> ipData = new HashMap<String,MinuteData>();
			datas.put(key,ipData);
			ipData.put(day1+"/"+"00:00:00", new MinuteData());
			//ipData.put(day2+"/"+"00:00", new MinuteData());
			
			String fileName1 = file+"/10.220.42/"+i+"/lockVerifyHandlerLogfile.txt";
			FileReader fileReader1;
			//String fileName2 = file+i+fileType+day2;
			FileReader fileReader2;
			try {
				fileReader1 = new FileReader(fileName1);
				BufferedReader bufReader1=new BufferedReader(fileReader1);
				//遍历数据行
				String line1 = "" ;
				String lineHourMinuteSecond = "00:00:00" ;
				int flightSecondCount = 0;
				int flightSecondSum = 0;
				int loginSecondCount = 0;
				int loginSecondSum = 0;
				while((line1 = bufReader1.readLine())!=null){
					//筛选出航班并发控制日志
					MinuteData data = ipData.get(day1+"/"+lineHourMinuteSecond);
					//ADD_TIMES+AllFlightSearch+nowDomesticNum======12+nowInternationalNum======0
					if(line1.contains("ADD_TIMES+AllFlightSearch+nowDomesticNum")){
						//判断时间间隔
						
						//上下两行日志的时间分钟数相同
						String[] hms = lineHourMinuteSecond.split(":");
						int minute = Integer.parseInt(hms[1]);
						int second = Integer.parseInt(hms[2]);
						//小时 分钟  秒数 是否相同
						if(hms[0].equals(line1.substring(7, 9))&&Integer.parseInt(line1.substring(10,12))==minute&&Integer.parseInt(line1.substring(13,15))==second){
							flightSecondCount++;
							String _nowInternationalNum = line1.split("======")[1];
							int nowDomesticNum = Integer.parseInt(_nowInternationalNum.split("\\+nowInternationalNum")[0]);
							int nowInternationalNum = Integer.parseInt(line1.split("======")[2]);
							int AllFlightSearchNum = nowDomesticNum + nowInternationalNum;
							flightSecondSum = flightSecondSum + AllFlightSearchNum;
							
						}else{
							data.setFlight_count(flightSecondCount);
							data.setFlight_sum(flightSecondSum);
							
							//不同秒的日志
							lineHourMinuteSecond = line1.substring(7,15);
							//不同，需要创建新的
							data = new MinuteData();
							ipData.put(day1+"/"+lineHourMinuteSecond,data);
							flightSecondCount = 1;
							flightSecondSum = 0;
							String _nowInternationalNum = line1.split("======")[1];
							int nowDomesticNum = Integer.parseInt(_nowInternationalNum.split("\\+nowInternationalNum")[0]);
							int nowInternationalNum = Integer.parseInt(line1.split("======")[2]);
							int AllFlightSearchNum = nowDomesticNum + nowInternationalNum;
							flightSecondSum = flightSecondSum + AllFlightSearchNum;
							
						}
						
						
					}else if(line1.contains("ALL_LOGIN_TIMES")){//ALL_LOGIN_TIMES+login+num======1
						//上下两行日志的时间分钟数相同
						String[] hms = lineHourMinuteSecond.split(":");
						int minute = Integer.parseInt(hms[1]);
						int second = Integer.parseInt(hms[2]);
						//小时 分钟  秒数 是否相同
						if(hms[0].equals(line1.substring(7, 9))&&Integer.parseInt(line1.substring(10,12))==minute&&Integer.parseInt(line1.substring(13,15))==second){
							loginSecondCount++;
							int nowLoginNum = Integer.parseInt(line1.split("======")[1]);
							loginSecondSum = loginSecondSum + nowLoginNum;
							
						}else{
							data.setLogin_count(loginSecondCount);
							data.setLogin_sum(loginSecondSum);
							
							//不同秒的日志
							lineHourMinuteSecond = line1.substring(7,15);
							//不同，需要创建新的
							data = new MinuteData();
							ipData.put(day1+"/"+lineHourMinuteSecond,data);
							loginSecondCount = 1;
							loginSecondSum = 0;
							int nowLoginNum = Integer.parseInt(line1.split("======")[1]);
							loginSecondSum = loginSecondSum + nowLoginNum;
							
						}
					}
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		Map<String,MinuteData> hourDatas = new HashMap<String,MinuteData>();
		
		String keyH = "00";
		String keyM = "00";
		String keyS = "00";
		String keyHHMMSS = "" ;
		//整理数据
		for(int h=0;h<24;h++){
			if(h<10){
				keyH ="0"+h;
			}else{
				keyH = ""+h;
			}
			MinuteData hourData = new MinuteData();
			hourDatas.put(keyH, hourData);
			
			int maxSearch = 0;
			
			int maxLogin = 0 ;
			
			int countSearch = 0;
			
			int sumSearch = 0;
			
			int sumLogin = 0;
			
			int countLogin = 0;
			
			//分钟
			for(int j=0;j<60;j++){
				if(j<10){
					keyM ="0"+j;
				}else{
					keyM = ""+j;
				}
				
				//秒
				for(int k=0;k<60;k++){
					if(k<10){
						keyS = "0"+k; 
					}else{
						keyS = ""+k;
					}
					
					keyHHMMSS = keyH+":"+keyM+":"+keyS;
					System.out.println("遍历"+keyHHMMSS);
					
					for(int i=35;i<=44;i++){
					
						String key = "10.220.42."+ i ;
						
						HashMap<String, MinuteData> ipdata = datas.get(key);
						
						//每秒数据
						MinuteData secondData = ipdata.get(keyHHMMSS);
						
						if(null!=secondData){
							
							countSearch++;
							countLogin++;
							//每秒并发数
							int concurrentSearch = secondData.getFlight_sum()/secondData.getFlight_count();
							if(secondData.getFlight_sum()%secondData.getFlight_count()/(float)secondData.getFlight_count()>0.4){
								concurrentSearch+=1;
							}
							int concurrentLogin = secondData.getLogin_sum()/secondData.getLogin_count();
							if(secondData.getLogin_sum()%secondData.getLogin_count()/(float)secondData.getLogin_count()>0.4){
								concurrentLogin+=1;
							}
							
							//总并发数
							sumSearch+=concurrentSearch;
							sumLogin+=concurrentLogin;
							
							//峰值
							if(maxSearch<concurrentSearch){
								maxSearch=concurrentSearch;
							}
							
							if(maxLogin<concurrentLogin){
								maxSearch=concurrentLogin;
							}
							
						}
						
						
						
					}
				}
			}
			if (countSearch !=0) {
				hourData.setFlight_AV(sumSearch/countSearch);
			} else {
				hourData.setFlight_AV(0);
			}
			if (countLogin != 0) {
				hourData.setLogin_Av(sumLogin/countLogin);
			} else {
				hourData.setLogin_Av(0);
			}
			hourData.setFlight_MAX(maxSearch);
			hourData.setLogin_MAX(maxLogin);
		}
		
		XSSFWorkbook dataBook = new XSSFWorkbook();
		//创建sheet
		XSSFSheet sheet = dataBook.createSheet();
		//创建表头
		XSSFRow firstRow = sheet.createRow(0);
		//写表头
		firstRow.createCell(0).setCellValue("时间段");
		firstRow.createCell(1).setCellValue("航班查询并发（平均）");
		firstRow.createCell(2).setCellValue("航班查询并发（峰值）");
		firstRow.createCell(3).setCellValue("登录并发(平均)");
		firstRow.createCell(4).setCellValue("登录并发(峰值)");
		
		String keyHH = "00";
		for(int h=0;h<24;h++){
			
			XSSFRow row = sheet.createRow(h+1);
			
			if(h<10){
				keyHH ="0"+h;
			}else{
				keyHH = ""+h;
			}
			String timeStart = keyHH+":"+"00" ;
			String timeEnd = keyHH+":"+"59" ;
			
			MinuteData hourData = hourDatas.get(keyHH);
			
			row.createCell(0).setCellValue(timeStart+"-"+timeEnd);
			row.createCell(1).setCellValue(hourData.getFlight_AV());
			row.createCell(2).setCellValue(hourData.getFlight_MAX());
			row.createCell(3).setCellValue(hourData.getLogin_Av());
			row.createCell(4).setCellValue(hourData.getLogin_MAX());
		}
		
		//写入硬盘
		FileOutputStream fout1 = null;
		try {
			fout1 = new FileOutputStream("C:/Users/pengchengzhang/Desktop/0517并发数统计/航班查询并发数据.xls");
			dataBook.write(fout1);  
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally{
			 try {
				fout1.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		} 
	}
		
}
/*
 * 单位时间内的数据
 * **/
class MinuteData{
	
	private int flight_AV;
	
	private int Login_Av ;
	
	private int flight_MAX ;
	
	private int login_MAX ;
	
	//间隔分钟内数据出现之和
	private int flight_sum;
	//间隔分钟内数据次数
	private int flight_count;
	//间隔分钟内数据出现之和
	private int login_sum;
	//间隔分钟内数据次数
	private int login_count;
	
	public int getFlight_count() {
		return flight_count;
	}
	public void setFlight_count(int flight_count) {
		this.flight_count = flight_count;
	}
	public int getLogin_count() {
		return login_count;
	}
	public void setLogin_count(int login_count) {
		this.login_count = login_count;
	}
	public int getFlight_sum() {
		return flight_sum;
	}
	public void setFlight_sum(int flight_sum) {
		this.flight_sum = flight_sum;
	}
	public int getLogin_sum() {
		return login_sum;
	}
	public void setLogin_sum(int login_sum) {
		this.login_sum = login_sum;
	}
	public int getFlight_AV() {
		return flight_AV;
	}
	public void setFlight_AV(int flight_AV) {
		this.flight_AV = flight_AV;
	}
	public int getLogin_Av() {
		return Login_Av;
	}
	public void setLogin_Av(int login_Av) {
		Login_Av = login_Av;
	}
	public int getFlight_MAX() {
		return flight_MAX;
	}
	public void setFlight_MAX(int flight_MAX) {
		this.flight_MAX = flight_MAX;
	}
	public int getLogin_MAX() {
		return login_MAX;
	}
	public void setLogin_MAX(int login_MAX) {
		this.login_MAX = login_MAX;
	}
	
	
	
}
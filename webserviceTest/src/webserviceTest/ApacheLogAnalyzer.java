package cn.ty.apache;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;




public class ApacheLogAnalyzer {
	
		//统计间隔 ,单位为分钟
		private static int space = 30 ;
		
		//指定的统计日期
		private static String date = "08/May/2018" ;
		
		//apache服务器尾号IP下线
		private static int apacheServerUpLimit = 1;
		
		//apache服务器尾号IP上线
		private static int apacheServerDownLimit = 1;
		
		//文件基础名
		private static String filePath="E:/apache日志/20180508_log/apache/";
		
		//文件后缀名
		private static String fileName1="/accesslog.20180508" ;
		
		private static String fileName2 = "00";
		
		private static String fileName3 = "" ;
		
		//url统计数据
		private static ArrayList<String> urls = new ArrayList<String>();
		
		//表头
		private static ArrayList<String> sheetName = new ArrayList<String>();
		
		//原始数据容器
		private static Map<String, HashMap<String, MinuteDataOfApache>> ipDatas =  new HashMap<String,HashMap<String,MinuteDataOfApache>>();
		
		
		
		//去重工具
		private static HashSet<String> urlNo2face = new HashSet<String>();
		
		//统计的日期
		private static HashSet<String> dates = new HashSet<String>();
	
		public static void main(String[] args){
			
			initStatisticUrl();
			//遍历原始数据文件
			for(int i=apacheServerDownLimit;i<=apacheServerUpLimit-apacheServerDownLimit+1;i++){
				String key = "APACHE"+i;
				//按服务器分类存放数据
				HashMap<String,MinuteDataOfApache> ipData = new HashMap<String,MinuteDataOfApache>();
				ipDatas.put(key,ipData);
				MinuteDataOfApache dat0 = new MinuteDataOfApache();
				ipData.put("00:00:00", dat0);
				for(int j=0;j<24;j++){
					String hourString = "" ;
					if(j<10){
						hourString = "0"+j;
					}else{
						hourString = j+"" ;
					}
					String fileName = filePath+i+fileName1+hourString+fileName2+fileName3;
					System.out.println("Read :"+fileName);
					FileReader fileReader;
					
					try{
						
						fileReader = new FileReader(fileName);
						BufferedReader bufReader=new BufferedReader(fileReader);
						String line = "" ;
						String lineHourMinute = "00:00:00" ;
						
						while((line = bufReader.readLine())!=null){
							//截取url
							int before = 0 ;//URL开始下标
							if(line.contains("GET")){
								before = line.indexOf("GET")+4;
							}else if(line.contains("POST")){
								before = line.indexOf("POST")+5;
							}
							
							int end = line.indexOf("HTTP");//URL结束下标
							String url = "";
							if(before > 0 && before < end && end-before>5){
								url = line.substring(before,end);
							}else{
								//System.err.println(line);
							}
							
							//剔除静态资源请求
							if(url.contains(".do")||url.contains("/3uair/ibe/ticket/")){
								//URL中符号‘.’位置下标
								int indexOfDDO = 0;
								
								if(url.indexOf(".do")!=-1){
									indexOfDDO = url.indexOf(".do")+3;
								}else if(url.indexOf("?")!=-1){
									indexOfDDO = url.indexOf("?") ;
								}
								
								url = url.substring(0,indexOfDDO);
								
								//只统计URL名录中的URL
								if(urlNo2face.contains(url)){
									
									if(line.contains(date)){
										
										int indexOfHourSecond = line.indexOf(date)+12;
										lineHourMinute = line.substring(indexOfHourSecond, indexOfHourSecond+8);
										MinuteDataOfApache minuteData = ipData.get(lineHourMinute);
										//存在当前秒的数据
										if(null!=minuteData){
											if(minuteData.containsUrlData(url)){
												UrlData urlData = minuteData.getUrlData(url);
												int indexOfSpace = line.lastIndexOf(" ");
												int indexOfMS = line.length();
												int ms = 0;
												if(indexOfSpace<indexOfMS){
													ms = Integer.parseInt(line.substring(indexOfSpace+1,indexOfMS));
												}
												
												if(ms<0){
													System.err.print("xx");
												}
												
												if(ms!=0){
													urlData.addCount();
													urlData.addSum(ms);
												}
											}else{
												UrlData urlData = new UrlData();
												int indexOfSpace = line.lastIndexOf(" ");
												int indexOfMS = line.length();
												int ms = 0;
												if(indexOfSpace<indexOfMS){
													ms = Integer.parseInt(line.substring(indexOfSpace+1,indexOfMS));
												}
												
												if(ms<0){
													System.err.print("xx");
												}
												
												if(ms!=0){
													urlData.addCount();
													urlData.addSum(ms);
												}
												minuteData.setUrlData(url, urlData);
											}
										}else{
											
											minuteData = new MinuteDataOfApache();
											ipData.put(lineHourMinute, minuteData);
											
											UrlData urlData = new UrlData();
											urlData.addCount();
											int indexOfSpace = line.lastIndexOf(" ");
											int indexOfMS = line.length();
											int ms = 0;
											if(indexOfSpace<indexOfMS){
												ms = Integer.parseInt(line.substring(indexOfSpace+1,indexOfMS));
											}
											
											if(ms<0){
												System.err.print("xx");
											}
											if(ms!=0){
												urlData.addCount();
												urlData.addSum(ms);
											}
											minuteData.setUrlData(url, urlData);
										}
										
									}
									
								}
								
							}
							
							
						}
						
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
			}
			
			Map<String,MinuteDataOfApache> hourDatas = new HashMap<String,MinuteDataOfApache>();
			
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
				MinuteDataOfApache hourData = new MinuteDataOfApache();
				hourDatas.put(keyH, hourData);
				
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
						
						for(int i=apacheServerDownLimit;i<=apacheServerUpLimit-apacheServerDownLimit+1;i++){
							
							String key = "APACHE"+ i ;
							HashMap<String, MinuteDataOfApache> ipdata = ipDatas.get(key);
							//每秒数据
							MinuteDataOfApache secondData = ipdata.get(keyHHMMSS);
							
							if(null!=secondData){
								
								for(int x=0;x<urls.size();x++){
									UrlData secondUrlData = secondData.getUrlData(urls.get(x));
									if(secondUrlData!=null){
										UrlData hourOfUrlData = hourData.getUrlData(urls.get(x));
										if(hourOfUrlData!=null){
											hourOfUrlData.addCount(secondUrlData.getCount());
											hourOfUrlData.addSum(secondUrlData.getSum());
										}else{
											hourOfUrlData = new UrlData();
											hourOfUrlData.addCount(secondUrlData.getCount());
											hourOfUrlData.addSum(secondUrlData.getSum());
											hourData.setUrlData(urls.get(x), hourOfUrlData);
										}
									}
								}
							}
						}
						
					}
				}
			}
			
			
			
			//打印数据
			XSSFWorkbook dataBook = new XSSFWorkbook();
			//创建sheet
			XSSFSheet sheet = dataBook.createSheet();
			//创建表头
			XSSFRow firstRow = sheet.createRow(0);
			//写表头
			for(int i=0;i<sheetName.size();i++){
				firstRow.createCell(i).setCellValue(sheetName.get(i));
			}
			
			//填充数据
			for(int i=0;i<24;i++){
				
				XSSFRow row = sheet.createRow(i+1);
				
				String keyHH ="" ;
				if(i<10){
					keyHH ="0"+i;
				}else{
					keyHH = ""+i;
				}
				
				String timeStart = keyHH+":"+"00" ;
				String timeEnd = keyHH+":"+"59" ;
				row.createCell(0).setCellValue(timeStart+"-"+timeEnd);
				
				
				MinuteDataOfApache hourData = hourDatas.get(keyHH);
				
				//首页数据
				String urlHome = "/3uair/ibe/common/homeRedirect.do" ; 
				UrlData homeData = hourData.getUrlData(urlHome);
				if(homeData!=null){
					row.createCell(3).setCellValue(homeData.getCount());
					row.createCell(4).setCellValue(homeData.getAv());
				}
				
				
				//航班查询数据
				String urlFlightSearch = "/3uair/ibe/common/processSearchForm.do" ;
				UrlData flightSearchData = hourData.getUrlData(urlFlightSearch);
				if(flightSearchData!=null){
					row.createCell(7).setCellValue(flightSearchData.getCount());
					row.createCell(8).setCellValue(flightSearchData.getAv());
				}
				
				//登录数据
				String urlLogin = "/3uair/ibe/profile/processLogin.do" ;
				UrlData loginData = hourData.getUrlData(urlLogin);
				if(loginData!=null){
					row.createCell(11).setCellValue(loginData.getCount());
					row.createCell(12).setCellValue(loginData.getAv());
				}
				//预定
				String urlcreateSPNR = "/3uair/ibe/checkout/processPassengerDetails.do" ;
				UrlData createData = hourData.getUrlData(urlcreateSPNR);
				if(createData!=null){
					row.createCell(15).setCellValue(createData.getCount());
					row.createCell(16).setCellValue(createData.getAv());
				}
				
				//出票
				String urlTicketing = "/3uair/ibe/checkout/redirectPaymentCallback.do" ;
				UrlData ticketData = hourData.getUrlData(urlTicketing);
				if(ticketData!=null){
					row.createCell(19).setCellValue(ticketData.getCount());
					row.createCell(20).setCellValue(ticketData.getAv());
				}
				
			}
			
			
			
			//打印数据
			for(int i=0;i<24;i++){
				String keyHH ="" ;
				if(i<10){
					keyHH ="0"+i;
				}else{
					keyHH = ""+i;
				}
				MinuteDataOfApache hourData = hourDatas.get(keyHH);
				System.out.println("HOUR:"+keyHH+" ");
				for(int j=0;j<urls.size();j++){
					
					if(hourData.getUrlData(urls.get(j))!=null){
						int count = hourData.getUrlData(urls.get(j)).getCount() ;
						long sum = hourData.getUrlData(urls.get(j)).getSum();
						System.out.println("   "+urls.get(j)+":  count="+count+" sum="+sum);
					}
					
				}
			}
			
			
			//写入硬盘
			FileOutputStream fout1 = null;
			try {
				fout1 = new FileOutputStream("C:/Users/yuab/Desktop/川航统计数据/5.17/apache并发数据.xls");
				dataBook.write(fout1);  
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					fout1.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 初始化URL名录
		 * */
		public static void initStatisticUrl(){
			//首页
			String urlHome = "/3uair/ibe/common/homeRedirect.do" ;
			//航班查询
			String urlFlightSearch = "/3uair/ibe/common/processSearchForm.do" ;
			//航班查询并发控制
			//String urlFlightSearchControl = "/3uair/ibe/common/checkFlightSearchLockNum.do" ;
			//登录
			String urlLogin = "/3uair/ibe/profile/processLogin.do" ;
			//生单/预定
			String urlcreateSPNR = "/3uair/ibe/checkout/processPassengerDetails.do" ;
			//出票
			String urlTicketing = "/3uair/ibe/checkout/redirectPaymentCallback.do" ;
			//添加购物车
			//String urlAddToCart = "/3uair/ibe/air/addToCart.do" ;
			//低价机票
			//String lowPriceFareSearch = "/3uair/ibe/common/flightSearch.do" ;
			//ibeticket
			String urlTicket = "/3uair/ibe/ticket/" ;
			urls.add(urlHome);
			urls.add(urlFlightSearch);
			//urls.add(urlFlightSearchControl);
			urls.add(urlLogin);
			urls.add(urlcreateSPNR);
			urls.add(urlTicketing);
			//urls.add(urlAddToCart);
			//urls.add(lowPriceFareSearch);
			urls.add(urlTicket);
			urlNo2face.add(urlHome);
			urlNo2face.add(urlFlightSearch);
			//urlNo2face.add(urlFlightSearchControl);
			urlNo2face.add(urlLogin);
			urlNo2face.add(urlcreateSPNR);
			urlNo2face.add(urlTicketing);
			//urlNo2face.add(urlAddToCart);
			//urlNo2face.add(lowPriceFareSearch);
			urlNo2face.add(urlTicket);
			
			sheetName.add("时间段");
			sheetName.add("浏览量");
			sheetName.add("访客数");
			
			sheetName.add("首页总请求数");
			sheetName.add("首页平均响应时间");
			sheetName.add("首页访问并发(平均)");
			sheetName.add("首页访问并发(峰值)");
			sheetName.add("航班查询总请求数");
			sheetName.add("航班查询平均响应时间");
			sheetName.add("航班查询并发（平均）");
			sheetName.add("航班查询并发（峰值）");
			sheetName.add("登录总请求数");
			sheetName.add("登录平均响应时间");
			sheetName.add("登录并发(平均)");
			sheetName.add("登录并发(峰值)");
			sheetName.add("预定总请求数");
			sheetName.add("预定平均响应时间");
			sheetName.add("预订并发（平均）");
			sheetName.add("预订并发（峰值）");
			sheetName.add("出票总请求数");
			sheetName.add("出票平均响应时间");
			sheetName.add("出票并发（平均）");
			sheetName.add("出票并发（峰值）");
			
		}
}




class MinuteDataOfApache{
	private HashMap<String,UrlData> urlDatas = new HashMap<String,UrlData>();
	
	public UrlData getUrlData(String url){
		return urlDatas.get(url);
	}
		
	public void setUrlData(String url,UrlData urlData){
		urlDatas.put(url,urlData);
		
	}
	
	public boolean containsUrlData(String url){
		return urlDatas.containsKey(url);
	}
}

class UrlData{
	
	//单位时间内URL请求出现的次数
	private int count ;
	
	//单位时间内URL请求总毫秒
	private long sum ;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}
	
	public void addCount(){
		count++;
	}
	
	public void addCount(long c){
		count+=c;
	}
	
	public void addSum(long s){
		sum+=s;
	}
	
	public String getAv(){
		if(count>0){
			return sum/count+"";
		}
		return "无数据";
	}
	
}
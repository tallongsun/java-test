package com.dl.druid.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.dl.druid.demo.process.IProcess;

public class DianShangRand {
	Map<String,List<String>> city=new HashMap<String,List<String>>();
	List<String> province;
	
	List<String> event =new ArrayList<String>();
	
	List<String> users=new ArrayList<>();
	
	List<String> os_version=new ArrayList<String>();
	List<String> model =new ArrayList<String>();
	List<String> os =new ArrayList<String>();
	List<String> screen_width =new ArrayList<String>();
	List<String> wifi =new ArrayList<String>();
	List<String> screen_height =new ArrayList<String>();
	List<String> app_version =new ArrayList<String>();
	List<String> manufacturer =new ArrayList<String>();
	
	List<String> gender =new ArrayList<String>();
	List<String> RegisterChannel =new ArrayList<String>();
	List<String> channel =new ArrayList<String>();
	List<String> productname =new ArrayList<String>();
	List<String> producttype =new ArrayList<String>();

	List<String> shopname =new ArrayList<String>();
	List<String> AllowanceType =new ArrayList<String>();
	List<String> PaymentMethod =new ArrayList<String>();
	List<String> CancelReason =new ArrayList<String>();
	List<String> CancelTiming =new ArrayList<String>();
	List<String> SupplyMethod =new ArrayList<String>();
	List<String> ServiceContent =new ArrayList<String>();
	List<String> ServiceStatus =new ArrayList<String>();

	DateTimeFormatter dtformat = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:00:00.000ZZ");
	
	public void init() throws IOException{
		initCity(city);
		province=new ArrayList<>(city.keySet());
		
		initData("event", event);
		
		initData("os_version",os_version);
		initData("model", model);
		initData("os", os);
        initData("screen_width", screen_width);
		initData("wifi", wifi);
        initData("screen_height", screen_height);
        initData("app_version", app_version);
        initData("manufacturer", manufacturer);
        

		initData("Gender", gender);
        initData("RegisterChannel", RegisterChannel);
        initData("Channel", channel);
        initData("ProductName", productname);
        initData("ProductType", producttype);
        
        initData("ShopName", shopname);
        initData("AllowanceType", AllowanceType);
        initData("PaymentMethod", PaymentMethod);
        initData("CancelReason", CancelReason);
        initData("CancelTiming", CancelTiming);
        initData("SupplyMethod", SupplyMethod);
        initData("ServiceContent", ServiceContent);
        initData("ServiceStatus", ServiceStatus);
		
	}
	
    public void initCity( Map<String,List<String>> pc) throws IOException{
        int sumcity=0;
        int sumpro=0;
        BufferedReader fr=new BufferedReader(new FileReader("col-values/city"));
        String line=null;
        String preline="";
        while((line=fr.readLine())!=null){
            String curline[]=line.trim().split("\\s+");
            if(curline.length==1){
                preline=curline[0];
                sumpro++;
            }else{

                List<String> cities=Arrays.asList(curline);
//                if(cities!=null && cities.size()>3){
//                    cities=cities.subList(0,2);
//                }
                pc.put(preline,cities);
                sumcity+=cities.size();
            }
        }
        fr.close();
        System.out.println("province:"+sumpro+" cities:"+sumcity);
    }

	public void initData(String filename,List<String> tolist) throws IOException{
		//TODO：打成jar包后运行，文件路径有没有问题，需不需要改成基于classpath的路径。
        BufferedReader fr=new BufferedReader(new FileReader("col-values/"+filename));
        String line=null;
        while((line=fr.readLine())!=null){
            if(line.trim().length()>0){
                tolist.add(line);
            }
        }
        fr.close();
        System.out.println(filename+"-- size:"+tolist.size()+" -- "+tolist);
	}

    public List<String> genUsers(long userCount){
        for(int i=0;i<userCount;i++){
            users.add(genRandomString(10));
        }
        return users;
    }
    
    public void genData(List<String> username,long maxRows,List<IProcess> processers,DateTime timestamp){
    	Random rand=new Random();
    	List<HashMap<String,Object> > objs=new ArrayList<HashMap<String,Object> >();
    	for(int i=0;i<maxRows;i++){
    		final HashMap<String, Object> obj = new HashMap<String, Object>();
            if(timestamp==null){
                obj.put("timestamp",new DateTime().toString());
            }else{
                obj.put("timestamp", timestamp.plusHours(rand.nextInt(23)).toString(dtformat));
            }
    		
    		obj.put("country","中国");
    		String prostr=province.get(rand.nextInt(province.size()));
    		obj.put("province",prostr.trim());
            obj.put("city", city.get(prostr).get(rand.nextInt(city.get(prostr).size())).trim());
    		
    		obj.put("event",event.get(rand.nextInt(event.size())));
    		
            obj.put("os_version", os_version.get(rand.nextInt(os_version.size())));
            obj.put("model",model.get(rand.nextInt(model.size())));
            obj.put("os",os.get(rand.nextInt(os.size())));
            obj.put("screen_width",screen_width.get(rand.nextInt(screen_width.size())));
            obj.put("wifi",wifi.get(rand.nextInt(wifi.size())));
            obj.put("screen_height",screen_height.get(rand.nextInt(screen_height.size())));
            obj.put("app_version",app_version.get(rand.nextInt(app_version.size())));
            obj.put("manufacturer",manufacturer.get(rand.nextInt(manufacturer.size())));
    		
    		obj.put("Gender",gender.get(rand.nextInt(gender.size()))); 
            obj.put("RegisterChannel",RegisterChannel.get(rand.nextInt(RegisterChannel.size())));
            obj.put("channel",channel.get(rand.nextInt(channel.size())));
            obj.put("productname",productname.get(rand.nextInt(productname.size())));
            obj.put("producttype",producttype.get(rand.nextInt(producttype.size())));
            
            obj.put("shopname",shopname.get(rand.nextInt(shopname.size())));
            obj.put("AllowanceType",AllowanceType.get(rand.nextInt(AllowanceType.size())));
            obj.put("PaymentMethod",PaymentMethod.get(rand.nextInt(PaymentMethod.size())));
            obj.put("CancelReason",CancelReason.get(rand.nextInt(CancelReason.size())));
            obj.put("CancelTiming",CancelTiming.get(rand.nextInt(CancelTiming.size())));
            obj.put("SupplyMethod",SupplyMethod.get(rand.nextInt(SupplyMethod.size())));
            obj.put("ServiceContent",ServiceContent.get(rand.nextInt(ServiceContent.size())));
            obj.put("ServiceStatus",ServiceStatus.get(rand.nextInt(ServiceStatus.size())));
    		
    		obj.put("UserName",username.get(rand.nextInt(username.size())));
    		obj.put("Birthday",new DateTime(1980,1,1,0,0,0).plusDays(rand.nextInt(10000)).toString("yyyy-MM-dd"));
    		
    		//"shipprice_level","ProductTotalPrice_level","OrderTotalPrice_level","PaymentAmount_level"
    		int shipPrice = rand.nextInt(10);
            obj.put("shipprice",shipPrice);
            int unitPrice = rand.nextInt(5);
            obj.put("ProductUnitPrice",unitPrice);
            int amount = rand.nextInt(100);
            obj.put("ProductAmount",amount);
            int totalPrice = unitPrice*amount;
            obj.put("ProductTotalPrice",totalPrice);
            int orderPrice = totalPrice+shipPrice;
            obj.put("OrderTotalPrice",orderPrice);
            int allowancePrice = rand.nextInt(100);
            obj.put("AllowanceAmount",allowancePrice);
            int payPrice = orderPrice - allowancePrice;
            obj.put("PaymentAmount",payPrice);

    		objs.add(obj);
    		
    		if(objs.size()%10000 == 0){
    			for(IProcess p : processers){
    				p.apply(objs);
    			}
    			objs.clear();
    		}
    	}
    	
		if(objs.size()>0){
			for(IProcess p : processers){
				p.apply(objs);
			}
			objs.clear();
		}
    }
    
    public String genRandomString(int length) { 
        String base = "abcdeuvwxy0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}

package com.dl.vertica;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlTest {
	public static void main(String[] args) {
		System.out.println(tmpTable_Funnel_New("basc"));
//		System.out.println(buildAnything());
	}
	public static final String yyyyMMdd = "yyyy-MM-dd";
	public static Date parse(String strDate, String format) {
		try {
			DateFormat formater = getDateFormater(format);
			synchronized (formater) {
				return formater.parse(strDate);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	private static Map<String, DateFormat> formaters = null;
	public static DateFormat getDateFormater(String format) {
		if (formaters == null) {
			formaters = new HashMap<String, DateFormat>();
		}
		DateFormat formater = formaters.get(format);
		if (formater == null) {
			formater = new SimpleDateFormat(format);
			formaters.put(format, formater);
		}
		return formater;
	}
	
	public static String format(Calendar calendar, String format) {
		return format(calendar.getTime(), format);
	}
	
	public static String format(Date date, String format) {
		if (date == null) {
			return "";
		}
		DateFormat formater = getDateFormater(format);
		synchronized (formater) {
			return formater.format(date);
		}
	}
	
	private static String tmpTable_Funnel_New(String templateSql){
		StringBuffer tmpTable = new StringBuffer("( SELECT ");
		String gby = "eve_b_dollar_os";
		//计算窗口期
		String endDate = "2017-10-02";
		String unit = "day";
		if(unit.equals("day")){
			int range = 7;
			Date date = parse(endDate, yyyyMMdd);
			Calendar calendar = Calendar.getInstance(); // 得到日历
			calendar.setTime(date);// 把当前时间赋给日历
			calendar.add(Calendar.DAY_OF_MONTH, range); // 设置为前n天
			
			endDate = format(calendar, yyyyMMdd);
		} 
		
		List<String> funnels =  Arrays.asList("view_page","btn_click");
		if(gby != null && !"".equals(gby)){
			//edit by aod
			//gby = gby.substring(gby.indexOf(".") + 1, gby.length());
			tmpTable.append(gby + ",");
		}
		tmpTable.append("user_id,");
		for(int i = 0;i < funnels.size();i++){
			tmpTable.append("time"+ i +",");
		}
		for(int i = 1;i < funnels.size();i++){
			tmpTable.append("median(mtime"+ i +") over() mmtime"+ i +",");
		}
		tmpTable.deleteCharAt(tmpTable.length() - 1);
		
		tmpTable.append(" FROM (");
		tmpTable.append(" SELECT ");
		if(gby != null && !"".equals(gby)){
			tmpTable.append(gby + ",");
		}
		tmpTable.append("user_id,");
		for(int i = 0;i < funnels.size();i++){
			if(i == 0){
				tmpTable.append("min(time0)");
			} else {
				tmpTable.append("min(time"+ i +"-t_time)");
			}
			tmpTable.append(" time"+ i +",");
		}
		
		//转化中位数
		for(int i = 1;i < funnels.size();i++){
			tmpTable.append(" min(case when time"+ i +" > t_time then datediff('minute', t_time, time"+ i +") end) AS mtime"+i+",");
		}
		tmpTable.deleteCharAt(tmpTable.length() - 1);
		tmpTable.append(" FROM (");
		tmpTable.append(" SELECT ");
		if(gby != null && !"".equals(gby)){
			tmpTable.append(gby + ",");
		}
		tmpTable.append("user_id,event_name,t_time,");
		for(int i = 0; i < funnels.size();i++){
			if( i == 0){
				tmpTable.append("case when event_name='f1' then t_time else NULL end time0,");
			} else {
				tmpTable.append("case when event_name='f"+ i +"' then min(case when event_name='f"+(i + 1)+"' then t_time end) over(partition by user_id order by t_time,event_name asc rows between 0 preceding and unbounded following) else NULL end time"+ i +",");
			}
		}
		tmpTable.deleteCharAt(tmpTable.length() - 1);
		tmpTable.append(" FROM ( ");
		tmpTable.append(" SELECT ");
		if(gby != null && !"".equals(gby)){
			tmpTable.append(gby + ",");
		}
		tmpTable.append("user_id,event_name(),t_time");
		tmpTable.append(" FROM (");
		//edit by aod --替换成从配置中获取基础SQL
		//tmpTable.append("  SELECT * from events e left join  users u ON e.user_id = u.id where e.date between  '"+ req.getStartDate() +"' and  '"+ endDate +"' ");
		tmpTable.append("SELECT  * FROM (");
		tmpTable.append(templateSql);
		tmpTable.append(" ) template");
		tmpTable.append(" where t_time between '"+ "2017-10-01" +"' and  '"+ endDate +"' ");
		//筛选条件
		String filter = "filters";
		if(filter != null && !"".equals(filter)){
			tmpTable.append(" AND ");
			tmpTable.append(filter);
		}
		tmpTable.append(" ) base");
		
		tmpTable.append(" MATCH ");
		tmpTable.append(" (PARTITION BY user_id,event order by user_id,event,t_time ");
		tmpTable.append(" DEFINE ");
		for(int i = 0; i < funnels.size();i++){
			String funnel = funnels.get(i);
			tmpTable.append(" f"+(i + 1)+" AS ");
			String events = "";
			//事件处理
			for(String action : funnels){
				events += "'"+ action +"',";
			}
			if(!"".equals(events)){
				tmpTable.append(" event IN (");
				tmpTable.append(events.substring(0,events.length() -1));
				tmpTable.append(") AND");
			}
			//事件日期区间
			if(i == 0){
				//漏斗中第一个事件发生的时间范围
				tmpTable.append(" t_time between  '"+ "2017-10-01" +"' and  '"+ "2017-10-02"+"',");
			} else {
//				String tmpDate = DateUtil.getPerDayByCurDate(req.getStart_date(), endDate, funnels.size() -(i + 1));
				tmpTable.append(" t_time between  '"+  "2017-10-01" +"' and  '"+ endDate +"',");
			}
		}
		tmpTable.deleteCharAt(tmpTable.length() - 1);
		tmpTable.append(" PATTERN p AS(");
		for(int i = 1;i <= funnels.size();i++){
			tmpTable.append(" f"+ i +"*");
		}
		tmpTable.append(")");
		tmpTable.append(" ROWS MATCH FIRST EVENT)");
		tmpTable.append("  ) allData");
		tmpTable.append(" ) tmp0 ");
		if(gby != null && !"".equals(gby)){
			tmpTable.append(" WHERE "+ gby +" is not null");
		}
		tmpTable.append(" GROUP BY user_id ");
		if(gby != null && !"".equals(gby)){
			tmpTable.append("," + gby );
		}
		tmpTable.append(" ) tmp1 ");
		
		tmpTable.append(" ) tmp");
		
		return tmpTable.toString();
	}
	
	public static String buildAnything() {
		StringBuffer sqlBuffer = new StringBuffer("SELECT ");
		String gf = "eve_b_dollar_os";
		if(gf!=null){
			sqlBuffer.append(gf + " as " + gf+ ",");
		}
		
		String unit = "day";
		int duration = 7;
		String unit_time = duration + " " + unit;
		List<String> funnels = Arrays.asList("view_page","btn_click");
		for(int i = 1;i <= funnels.size();i++){//统计各漏斗层用户数
			if(i == 1)
				sqlBuffer.append("count(case when time0 is not NULL");
			else {
				sqlBuffer.append("count(case when");
				for(int j = 1;j<i;j++){
					String tmp = "";
					for(int k = 1 ; k <= j ;k++){
						tmp += "time" + k +"+";
					}
					tmp = tmp.substring(0, tmp.length() -1);
					sqlBuffer.append(" time"+j+" is not NULL and ("+tmp+") < (INTERVAL '"+ unit_time +"')");
					if(j < (i-1)){
						sqlBuffer.append(" AND");
					}
				}
			}
			sqlBuffer.append(" then user_id end) count"+i+",");
		}
		
		for(int i = 1;i < funnels.size();i++){//统计中转数
			sqlBuffer.append("min(mmtime"+ i +") m"+ i +",");
		}
		sqlBuffer.deleteCharAt(sqlBuffer.length() - 1);
		sqlBuffer.append(" FROM ");
		sqlBuffer.append(tmpTable_Funnel_New("basic"));
		if(gf!=null){
			sqlBuffer.append(" GROUP BY ").append(gf);
			sqlBuffer.append(" ORDER BY ").append(gf);
		}
		return sqlBuffer.toString();
	}
}

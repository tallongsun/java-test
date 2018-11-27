package com.dl.spel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

@SpringBootApplication
/**
 * spring表达式语言
 *
 */
public class SpElTest {
	
	@SuppressWarnings({"unchecked","unused"})
	public static void main(String[] args) throws Exception {
		ApplicationContext ctx = SpringApplication.run(SpElTest.class, args);

		//1.文字表达式
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression("'Hello World'.concat('!').bytes.length");
		System.out.println(exp.getValue());
		
	    String greetingExp = "'Hello,' + #user";                     
	    parser = new SpelExpressionParser();          
	    StandardEvaluationContext context = new StandardEvaluationContext();        
	    context.setVariable("user", "Gangyou");      
	    exp = parser.parseExpression(greetingExp);    
	    System.out.println(exp.getValue(context, String.class));
		
		GregorianCalendar c = new GregorianCalendar();
		c.set(1856, 7, 9);
		Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");
		exp = parser.parseExpression("name");
		String name = exp.getValue(tesla,String.class);
		System.out.println(name);
		
		exp = parser.parseExpression("name == 'Nikola Tesla'");
		boolean result = exp.getValue(tesla, Boolean.class); // evaluates to true
		System.out.println(result);
		
		//2.访问属性，集合元素
		Simple simple = new Simple();
		simple.booleanList.add(true);
		exp = parser.parseExpression("booleanList[0]");
		exp.setValue(simple, "false");
		Boolean b = simple.booleanList.get(0);
		System.out.println(b);
		
		SpelParserConfiguration config = new SpelParserConfiguration(true,true);
		parser = new SpelExpressionParser(config);
		exp= parser.parseExpression("list[3]");
		Demo demo = new Demo();
		Object o = exp.getValue(demo);
		System.out.println(o);
		
		config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE,SpElTest.class.getClassLoader());
		parser = new SpelExpressionParser(config);
		exp = parser.parseExpression("payload");
		MyMessage msg = new MyMessage();
		Object payload = exp.getValue(msg);
		System.out.println(payload);
		
		FieldValueTestBean fieldValueTestBean = ctx.getBean(FieldValueTestBean.class);
		System.out.println(fieldValueTestBean.getDefaultLocale());
		System.out.println(fieldValueTestBean.getRandomNum());
		
		SimpleMovieLister simpleMovieLister = ctx.getBean(SimpleMovieLister.class);
		System.out.println(simpleMovieLister.defaultLocale);
		
		//3.内嵌lists
		List<Integer> numbers = (List<Integer>) parser.parseExpression("{1,2,3,4}").getValue();
		System.out.println(numbers);
		List<List<Character>> listOfLists = (List<List<Character>>) parser.parseExpression("{{'a','b'},{'x','y'}}").getValue();
		System.out.println(listOfLists);
		
		//4.内嵌maps
		Map<String,String> inventorInfo = (Map<String,String>) parser.parseExpression(
				"{name:'Nikola',dob:'10-July-1856'}").getValue();
		System.out.println(inventorInfo);
		Map<String,Map<String,String>> mapOfMaps = (Map<String,Map<String,String>>) parser.parseExpression(
				"{name:{first:'Nikola',last:'Tesla'},dob:{day:10,month:'July',year:1856}}").getValue();
		System.out.println(mapOfMaps);
		
		//5.构造Array
		int[] numbers1 = (int[]) parser.parseExpression("new int[4]").getValue();
		System.out.println(Arrays.toString(numbers1));
		int[] numbers2 = (int[]) parser.parseExpression("new int[]{1,2,3}").getValue();
		System.out.println(Arrays.toString(numbers2));
		int[][] numbers3 = (int[][]) parser.parseExpression("new int[4][5]").getValue();
		System.out.println(Arrays.toString(numbers3));
		
		//6.方法
		exp = parser.parseExpression("new String('hello world').toUpperCase()");
		String message = exp.getValue(String.class);
		System.out.println(message);
		
		//7.运算符
		boolean trueValue = parser.parseExpression("2 == 2").getValue(Boolean.class);
		boolean falseValue = parser.parseExpression("2 < -5.0").getValue(Boolean.class);
		System.out.println(trueValue+","+falseValue);
		
		falseValue = parser.parseExpression(
		        "'xyz' instanceof T(int)").getValue(Boolean.class);
		trueValue = parser.parseExpression(
		        "'-5' matches '^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);
		System.out.println(trueValue+","+falseValue);
		
		trueValue = parser.parseExpression("true or false").getValue(Boolean.class);
		falseValue = parser.parseExpression("!true").getValue(Boolean.class);
		System.out.println(trueValue+","+falseValue);

		int two = parser.parseExpression("1 + 1").getValue(Integer.class); // 2
		String testString = parser.parseExpression(
		        "'test' + ' ' + 'string'").getValue(String.class); // test string
		int four = parser.parseExpression("1 - -3").getValue(Integer.class); // 4
		double d = parser.parseExpression("1000.00 - 1e4").getValue(Double.class); // -9000
		int six = parser.parseExpression("-2 * -3").getValue(Integer.class); // 6
		double twentyFour = parser.parseExpression("2.0 * 3e0 * 4").getValue(Double.class); // 24.0
		int minusTwo = parser.parseExpression("6 / -3").getValue(Integer.class); // -2
		double one = parser.parseExpression("8.0 / 4e0 / 2").getValue(Double.class); // 1.0
		int three = parser.parseExpression("7 % 4").getValue(Integer.class); // 3
		int minusTwentyOne = parser.parseExpression("1+2-3*8").getValue(Integer.class); // -21
		
		//8.赋值
		Inventor inventor = new Inventor();
		parser.parseExpression("name").setValue(inventor, "Alexander Seovic2");
		String aleks = parser.parseExpression(
		        "name = 'Alexandar Seovic'").getValue(inventor, String.class);
		System.out.println(aleks);
		
		//9.类型
		Class<Date> dateClass = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
		Class<String> stringClass = parser.parseExpression("T(String)").getValue(Class.class);
		
		//10.调用构造函数
		Inventor einstein = parser.parseExpression(
		        "new com.dl.spel.SpElTest.Inventor('Albert Einstein', null, 'German')")
		        .getValue(Inventor.class);
		System.out.println(einstein);
		
		//11.变量
		context = new StandardEvaluationContext(tesla);
		context.setVariable("newName", "Mike Tesla");
		parser.parseExpression("name = #newName").getValue(context);
		System.out.println(tesla.name);
		
		//12.函数
		parser = new SpelExpressionParser();
		context = new StandardEvaluationContext();
		context.registerFunction("reverseString",
		    StringUtils.class.getDeclaredMethod("reverseString", new Class[] { String.class }));
		String helloWorldReversed = parser.parseExpression(
		    "#reverseString('hello')").getValue(context, String.class);
		System.out.println(helloWorldReversed);
		
		//13.bean引用
		
		//14.三元运算符
		String falseString = parser.parseExpression(
		        "false ? 'trueExp' : 'falseExp'").getValue(String.class);
		System.out.println(falseString);
		
		//15.Elvis运算符
		name = parser.parseExpression("name?:'Unknown'").getValue(tesla,String.class);
		System.out.println(name); 
		
		//16.安全导航运算符
		parser = new SpelExpressionParser();
		tesla = new Inventor("Nikola Tesla", null,"Serbian");
		tesla.placeOfBirth = new PlaceOfBirth("Smiljan");
		context = new StandardEvaluationContext(tesla);
		String city = parser.parseExpression("placeOfBirth?.city").getValue(context, String.class);
		System.out.println(city); // Smiljan
		tesla.placeOfBirth = null;
		city = parser.parseExpression("placeOfBirth?.city").getValue(context, String.class);
		System.out.println(city); 
		
		//17.集合选择
		List<Integer> primes = new ArrayList<Integer>();
		primes.addAll(Arrays.asList(2,3,5,7,11,13,17));
		parser = new SpelExpressionParser();
		context = new StandardEvaluationContext();
		context.setVariable("primes",primes);
		List<Integer> primesGreaterThanTen = (List<Integer>) parser.parseExpression(
		        "#primes.?[#this>10]").getValue(context);
		System.out.println(primesGreaterThanTen);
		
		//18.集合投影
		List<Inventor> members = new ArrayList<Inventor>();
		members.add(tesla);
		members.add(inventor);
		parser = new SpelExpressionParser();
		context = new StandardEvaluationContext();
		context.setVariable("members",members);
		List<String> citys = (List<String>) parser.parseExpression(
		        "#members.![placeOfBirth?.city]").getValue(context);
		System.out.println(citys);
		
		//19.表达模板
		String randomPhrase = parser.parseExpression(
		        "random number is #{{T(java.lang.Math).random()}}",
		        new CustomTemplateParserContext()).getValue(String.class);
		System.out.println(randomPhrase);

	}
	
	public static class CustomTemplateParserContext implements ParserContext {

		@Override
		public boolean isTemplate() {
			return true;
		}

		@Override
		public String getExpressionPrefix() {
			return "#{{";
		}

		@Override
		public String getExpressionSuffix() {
			return "}}";
		}
		
	}
	
	public static class Inventor {
		public String name;
		public Date birthday;
		public String nationality;
		public PlaceOfBirth placeOfBirth;
		
		
		public Inventor(){
			
		}
		
		public Inventor(String name, Date birthday, String nationality) {
			super();
			this.name = name;
			this.birthday = birthday;
			this.nationality = nationality;
		}

		@Override
		public String toString() {
			return "Inventor [name=" + name + ", birthday=" + birthday + ", nationality=" + nationality + "]";
		}
		
	}
	
	public static class PlaceOfBirth{
		public String city;

		public PlaceOfBirth(String city) {
			super();
			this.city = city;
		}
		
	}
	
	public static class Simple {
	    public List<Boolean> booleanList = new ArrayList<Boolean>();
	}
	
	public static class Demo {
	    public List<String> list;
	}
	
	public static class MyMessage{
		public Object payload;
	}
	
	@Configuration("numberGuess")
	public static class NumberGuess{
	    @Value("#{ T(java.lang.Math).random() * 100.0 }")
	    private double randomNum;
	    
		public double getRandomNum() {
			return randomNum;
		}

		public void setRandomNum(double randomNum) {
			this.randomNum = randomNum;
		}
	}
	
	@Configuration
	public static class FieldValueTestBean{

	    @Value("#{ systemProperties['user.region'] }")
	    private String defaultLocale;
	    
	    @Value("#{ numberGuess.randomNum }")
	    private double randomNum;
	
	    public void setDefaultLocale(String defaultLocale) {
	        this.defaultLocale = defaultLocale;
	    }
	
	    public String getDefaultLocale() {
	        return this.defaultLocale;
	    }

		public double getRandomNum() {
			return randomNum;
		}

		public void setRandomNum(double randomNum) {
			this.randomNum = randomNum;
		}
	    
	}
	
	@Service
	public static class SimpleMovieLister {
	    public String defaultLocale;
	    @Autowired
	    public void configure(
	            @Value("#{ systemProperties['user.region'] }") String defaultLocale) {
	        this.defaultLocale = defaultLocale;
	    }

	}
	
	public static class StringUtils {

	    public static String reverseString(String input) {
	        StringBuilder backwards = new StringBuilder();
	        for (int i = 0; i < input.length(); i++){
	            backwards.append(input.charAt(input.length() - 1 - i));
	        }
	        return backwards.toString();
	    }
	}
}

package com.dl.spel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class SpElDemo {

	public static void main(String[] args) {
		
		//构建表达式
	    ExpressionParser parser = new SpelExpressionParser();     
	    Expression expr = parser.parseExpression("hasAuthority('supervisor')");    
	    
	    //构建上下文
	    EvaluationContext ctx = new StandardEvaluationContext(new ExpressionRoot(
	    		new MockAuthentication()));        

	    //执行表达式
	    boolean result = expr.getValue(ctx, Boolean.class).booleanValue();
	    System.out.println(result);
	    
	    //测试一下自定义函数
	    expr = parser.parseExpression("checkAuth('ad'.concat('min'))");
	    result = expr.getValue(ctx,Boolean.class).booleanValue();
	    System.out.println(result);
	}
	
	public static class ExpressionRoot extends SecurityExpressionRoot{
		public ExpressionRoot(Authentication authentication) {
			super(authentication);
		}
		
		public boolean checkAuth(String resource){
			return true;
		}

	}
	
	public static class MockAuthentication implements Authentication{
		private static final long serialVersionUID = 1L;

		@Override
		public String getName() {
			return null;
		}
		
		@Override
		public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		}
		
		@Override
		public boolean isAuthenticated() {
			return false;
		}
		
		@Override
		public Object getPrincipal() {
			return null;
		}
		
		@Override
		public Object getDetails() {
			return null;
		}
		
		@Override
		public Object getCredentials() {
			return null;
		}
		
		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			List<GrantedAuthority> list =  new ArrayList<>();
			list.add(new GrantedAuthority() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getAuthority() {
					return "supervisor";
				}
			});
			return list;
		}
	}

}

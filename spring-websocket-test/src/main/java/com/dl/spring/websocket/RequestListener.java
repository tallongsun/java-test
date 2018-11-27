package com.dl.spring.websocket;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

@Service
public class RequestListener implements ServletRequestListener {

	@Override
	public void requestDestroyed(ServletRequestEvent sre) {

	}

	@Override
	public void requestInitialized(ServletRequestEvent sre) {
    	HttpSession session = ((HttpServletRequest) sre.getServletRequest()).getSession();
    	String userId = sre.getServletRequest().getParameter("uid");
    	
    	session.setAttribute("uid", userId);
	}

}

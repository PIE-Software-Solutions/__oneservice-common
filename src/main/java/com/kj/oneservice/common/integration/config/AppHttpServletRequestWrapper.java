package com.kj.oneservice.common.integration.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

public class AppHttpServletRequestWrapper extends HttpServletRequestWrapper{
	
	private byte[] body;
	
	
	public AppHttpServletRequestWrapper(HttpServletRequest request) throws IOException{
		super(request);
		
		if(null != request && null != request.getInputStream()) {
			body = IOUtils.toByteArray(request.getInputStream());
		}
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException{
		
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
		ServletInputStream servletInputStream = new ServletInputStream() {
			
			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				return byteArrayInputStream.read();
			}
			
			@Override
			public boolean isReady() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean isFinished() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setReadListener(ReadListener listener) {
				// TODO Auto-generated method stub
				
			}
		};
		return servletInputStream;
	}
	
	public byte[] getBody() {
		return this.body;
	}

}

package com.kj.oneservice.common.integration.config;

import static com.kj.oneservice.common.integration.util.CommonConstants.APPLICATION_JSON;
import static com.kj.oneservice.common.integration.util.CommonConstants.CONTENT_TYPE;
import static com.kj.oneservice.common.integration.util.CommonConstants.ERROR_CODE;
import static com.kj.oneservice.common.integration.util.CommonConstants.JSON_VAL_REQ;
import static com.kj.oneservice.common.integration.util.CommonConstants.REQUEST_ID;
import static com.kj.oneservice.common.integration.util.CommonConstants.REQUEST_PATTERN;
import static com.kj.oneservice.common.integration.util.CommonConstants.REQUEST_URL;
import static com.kj.oneservice.common.integration.util.CommonConstants.RESPONSE_CODE;
import static com.kj.oneservice.common.integration.util.CommonConstants.RESPONSE_MESSAGE;
import static com.kj.oneservice.common.integration.util.CommonConstants.SERVICE_NAME;
import static com.kj.oneservice.common.integration.util.CommonConstants.TAG_INSTANCE;
import static com.kj.oneservice.common.integration.util.CommonConstants.VALIDATION_ERRORS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.removeStart;
import static org.apache.commons.lang3.StringUtils.replace;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.kj.oneservice.common.integration.util.AppLogger;
import com.kj.oneservice.common.integration.util.JSONSchemaValidator;

@SuppressWarnings("deprecation")
public class AppFilter implements Filter{
	
	private static final AppLogger LOGGER = new AppLogger(AppFilter.class.getName());
	
	private static final List<String> JSON_VALIDATION_TAGS_NOT_ALLOWED = new ArrayList<String>();
	private static final List<String> ALLOWED_FILTER_REQUEST_METHODS = new ArrayList<String>();
	
	static {
		JSON_VALIDATION_TAGS_NOT_ALLOWED.add("schema");
		
		ALLOWED_FILTER_REQUEST_METHODS.add(RequestMethod.PUT.toString());
		ALLOWED_FILTER_REQUEST_METHODS.add(RequestMethod.PATCH.toString());
		ALLOWED_FILTER_REQUEST_METHODS.add(RequestMethod.POST.toString());
	}
	
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) 
					throws IOException, ServletException{
		final String methodName = "doFilter";
		LOGGER.startMethod(methodName);
		
		final AppHttpServletResponseWrapper responseWrapper = new AppHttpServletResponseWrapper((HttpServletResponse)servletResponse);
		
		final AppHttpServletRequestWrapper requestWrapper = new AppHttpServletRequestWrapper((HttpServletRequest)servletRequest);
		String requestURI = requestWrapper.getRequestURI();
		final String requestMethod = requestWrapper.getMethod();
		
		if(isNotBlank(requestURI)) {
			MDC.put(REQUEST_URL, requestURI);
			LOGGER.debug("Request Initiated for :: "+ MDC.get(REQUEST_URL));
		}
		
		LOGGER.info("Incommin Reqyest URI :: "+ requestURI + ". Request Method :: " + requestMethod);
		
		if("Y".equals(JSON_VAL_REQ) && isNotBlank(requestURI) && isNotBlank(requestMethod)
						&& ALLOWED_FILTER_REQUEST_METHODS.contains(requestMethod) && requestURI.contains(SERVICE_NAME)) {
			ProcessingReport report = null;
			InputStream schemaStream = null;
			
			try {
				final byte[] requestBody = requestWrapper.getBody();
				final String requestBodyStr = new String(requestBody);
				
				LOGGER.info("Incomming Request Body :: " + requestBodyStr.replaceAll("\n", ""));
				
				requestURI = requestURI.substring(requestURI.lastIndexOf("/")+1, requestURI.length());
				
				if(isNotBlank(requestURI)) {
					schemaStream = JSONSchemaValidator.class.getResourceAsStream("/schema/" + requestURI + ".json");
					
					report = JSONSchemaValidator.validate(requestBody, schemaStream);
				}
			}catch (Exception exception) {
				LOGGER.error(methodName, "Exception: " + exception.getMessage());
				JSONObject responseObj = new JSONObject();
				
				setResponseDetails(responseWrapper, responseObj, HttpStatus.INTERNAL_SERVER_ERROR, ERROR_CODE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), null);
			}finally {
				try {
					if(null != schemaStream) {
						schemaStream.close();
					}
				}catch (IOException ioException) {
					LOGGER.error(methodName, "Exception while closing schema input stream - " + requestURI + ".json"+ ioException.getMessage());
				}
			}
			if(null != report) {
				if(report.isSuccess()) {
					chain.doFilter(requestWrapper, responseWrapper);
				} else {
					JSONObject responseObj = new JSONObject();
					
					final List<JSONObject> jsonValidationErrorList = extractJSONValidationError(report);
					setResponseDetails(responseWrapper, responseObj, HttpStatus.BAD_REQUEST, ERROR_CODE, null, jsonValidationErrorList);
				}
			}
		} else {
			chain.doFilter(requestWrapper, responseWrapper);
		}
		
		responseWrapper.flushBuffer();
		LOGGER.info("Outgoing Response Body :: " + new String(responseWrapper.getCopy()));
		LOGGER.endMethod(methodName);
		
	}	
	
	public static void setResponseDetails(HttpServletResponse httpServletResponse, JSONObject responseObj,
			final HttpStatus httpStatus, final Integer responseCode, final String responseMessage, final List<JSONObject> jsonValidationErrorList) throws IOException{
		
		final String methodName = "setResponseDetails";
		
		if(null != httpServletResponse && null != responseObj) {
			try {
				responseObj.put(REQUEST_ID, MDC.get(REQUEST_PATTERN));
				responseObj.put(RESPONSE_CODE, responseCode);
				if(null != responseMessage)
					responseObj.put(RESPONSE_MESSAGE, responseMessage);
				
				if(null != jsonValidationErrorList)
					responseObj.put(VALIDATION_ERRORS, (Object)jsonValidationErrorList);
				
				
				LOGGER.info(methodName, " REQUEST_ID :: " + responseObj.get(REQUEST_ID) + " RESPONSE_CODE :: "
						+ responseObj.get(RESPONSE_CODE) + " RESPONSE_MESSAGE :: "
								+ responseObj.get(RESPONSE_MESSAGE));
			}catch (JSONException jsonException) {
				LOGGER.error(methodName, "Exception: " + jsonException.getMessage());
			}
			httpServletResponse.setHeader(CONTENT_TYPE, APPLICATION_JSON);
			httpServletResponse.setStatus(httpStatus.value());
			httpServletResponse.getWriter().write(responseObj.toString());
			httpServletResponse.getWriter().flush();
			httpServletResponse.getWriter().close();
		}
		LOGGER.endMethod(methodName);

	}
	
	private static List<JSONObject> extractJSONValidationError(final ProcessingReport report){
		final String methodName = "extractJSONValidationError";
		LOGGER.startMethod(methodName);
		
		List<JSONObject> jsonValidationErrorList  = null;
		
		if(null != report && null != report.iterator()) {
			jsonValidationErrorList = new ArrayList<JSONObject>();
			final Iterator<ProcessingMessage> iterator = report.iterator();
			while(iterator.hasNext()) {
				final ProcessingMessage message = (ProcessingMessage) iterator.next();
				
				if(null != message && message.asJson() instanceof ObjectNode) {
					final ObjectNode objectNode = (ObjectNode) message.asJson();
					
					final Iterator<String> keySetIterator = objectNode.fieldNames();
					
					if(null != objectNode && null != keySetIterator) {
						final JSONObject jsonObject = new JSONObject();
						
						while(keySetIterator.hasNext()) {
							final String key = keySetIterator.next();
							
							if(isNotBlank(key) && !JSON_VALIDATION_TAGS_NOT_ALLOWED.contains(key)) {
								try {
									String value = objectNode.get(key).toString();
									value = removeStart(value, "\"");
									value = removeEnd(value, "\"");
									value = replace(value, "\\\"", "\"");
									if(TAG_INSTANCE.equalsIgnoreCase(key)) {
										value = removeStart(value, "{\"pointer\":\"");
										value = removeEnd(value, "\"}");
									}
									jsonObject.append(key, value);
								}catch(JSONException jsonException) {
									LOGGER.error(methodName, "Exception: " + jsonException.getMessage());
								}
							}
						}
					}
				}
			}
		}
		
		LOGGER.endMethod(methodName);
		return jsonValidationErrorList;
	}
	
	@Override
	public void init(FilterConfig filterConfig) {
		
	}
	
	@Override
	public void destroy() {
		
	}
}

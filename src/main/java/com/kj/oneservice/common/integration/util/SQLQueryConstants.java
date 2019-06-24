package com.kj.oneservice.common.integration.util;

import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_SERVICE_ENABLED;
import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_SERVICE_PWD;
import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_SERVICE_ROLE;
import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_SERVICE_SERVICE;
import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_SERVICE_TABLE;
import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_SERVICE_USER;
import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_USER_ROLE_TABLE;
import static com.kj.oneservice.common.integration.util.CommonConstants.COMMON_USER_TABLE;
import static com.kj.oneservice.common.integration.util.CommonConstants.SERVICE_NAME;

public class SQLQueryConstants {
	public static final String GET_USERS = "SELECT " + COMMON_SERVICE_USER  + ", " + COMMON_SERVICE_PWD + ", CASE"
					+ COMMON_SERVICE_ENABLED + " WHEN 1 THEN 'true' ELSE 'false' END enabled FROM " + COMMON_USER_TABLE
					+ " WHERE " + COMMON_SERVICE_USER + " = ?";
	
	public static final String GET_USER_ROLES = "SELECT r." + COMMON_SERVICE_USER + ", r." + COMMON_SERVICE_ROLE
					+ " FROM " + COMMON_USER_ROLE_TABLE + " r, " + COMMON_SERVICE_TABLE + " s WHERE r." + COMMON_SERVICE_USER
					+ " = s." + COMMON_SERVICE_USER + " AND s." + COMMON_SERVICE_SERVICE + "='" + SERVICE_NAME + "' AND r."
					+ COMMON_SERVICE_USER + " = ?";
}

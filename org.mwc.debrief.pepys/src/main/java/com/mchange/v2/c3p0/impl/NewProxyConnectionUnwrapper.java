package com.mchange.v2.c3p0.impl;

import java.sql.Connection;

public class NewProxyConnectionUnwrapper {
	public static Connection unWrapperInnerConnection(NewProxyConnection connection) {
		return connection.inner;
	}
}

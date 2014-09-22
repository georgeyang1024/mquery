package com.minephone.volley;

import com.android.volley.VolleyError;

/**
 * VolleyErro处理
 * 
 * @author ping 2014-4-11 下午2:04:42
 */
public class VolleyErrorMsg {
	public static String getMessage(VolleyError error) {
		String result = null;
		if (error != null) {
			if (error.networkResponse != null) {
				switch (error.networkResponse.statusCode) {
				case 400:
					result = "Bad Request";
					break;
				case 403:
					result = "Request Forbidden";
					break;
				case 404:
					result = "HTTP Not Found";
					break;
				case 500:
					result = "Internal Server Error";
					break;
				case 502:
					result = "Bad Gateway";
					break;
				default:
					result = "Request error code:"
							+ error.networkResponse.statusCode;
					break;
				}
			} else {
				String str = error.getMessage();
				if (str == null) {
					result = "Request time out";
				} else {
					if (str.startsWith("java.net.ConnectException:")) {
						result = "Connect time out";
					} else if (str.startsWith("java.lang.RuntimeException:")) {
						result = "Bad URL";
					} else if (str.startsWith("java.net.UnknownHostException:")) {
						result = "UnknownHost";
					} else if (str
							.startsWith("java.lang.IllegalArgumentException:")) {
						result = "Incorrect param";
					} else if (str.startsWith("java.net.SocketException:")) {
						result = "Connect failed";
					} else {
						result = str;
					}
				}
			}
		} else {
			result = "OK!";
		}
		return result;
	}
}

// 超时
// error:null
//
// 超时(能连接IP)
// error:java.net.ConnectException: failed to connect to /172.16.40.156 (port
// 8080) after 2500ms: connect failed: ENETUNREACH (Network is unreachable)
//
// 域名访问失败
// error:java.lang.RuntimeException: Bad URL 123.com?page=1
//
// 未知域名
// error:java.net.UnknownHostException: Unable to resolve host
// "www.aaaaaa.baidu.com": No address associated with hostname
//
// 参数不合法
// error:java.lang.IllegalArgumentException: port=576577
//
// 无法连接服务器
// error:java.net.SocketException: failed to connect to /172.16.40.155 (port
// 8080) after 2500ms: isConnected failed: EHOSTUNREACH (No route to host)

// "100" : Continue
// "101" : witching Protocols
// "200" : OK
// "201" : Created
// "202" : Accepted
// "203" : Non-Authoritative Information
// "204" : No Content
// "205" : Reset Content
// "206" : Partial Content
// "300" : Multiple Choices
// "301" : Moved Permanently
// "302" : Found
// "303" : See Other
// "304" : Not Modified
// "305" : Use Proxy
// "307" : Temporary Redirect
// "400" : Bad Request
// "401" : Unauthorized
// "402" : Payment Required
// "403" : Forbidden
// "404" : Not Found
// "405" : Method Not Allowed
// "406" : Not Acceptable
// "407" : Proxy Authentication Required
// "408" : Request Time-out
// "409" : Conflict
// "410" : Gone
// "411" : Length Required
// "412" : Precondition Failed
// "413" : Request Entity Too Large
// "414" : Request-URI Too Large
// "415" : Unsupported Media Type
// "416" : Requested range not satisfiable
// "417" : Expectation Failed
// "500" : Internal Server Error
// "501" : Not Implemented
// "502" : Bad Gateway
// "503" : Service Unavailable
// "504" : Gateway Time-out
// "505" : HTTP Version not supported
// HTTP 400 - 请求无效
// HTTP 401.1 - 未授权：登录失败
// HTTP 401.2 - 未授权：服务器配置问题导致登录失败
// HTTP 401.3 - ACL 禁止访问资源
// HTTP 401.4 - 未授权：授权被筛选器拒绝
// HTTP 401.5 - 未授权：ISAPI 或 CGI 授权失败
// HTTP 403 - 禁止访问
// HTTP 403 - 对 Internet 服务管理器 (HTML) 的访问仅限于 Localhost
// HTTP 403.1 禁止访问：禁止可执行访问
// HTTP 403.2 - 禁止访问：禁止读访问
// HTTP 403.3 - 禁止访问：禁止写访问
// HTTP 403.4 - 禁止访问：要求 SSL
// HTTP 403.5 - 禁止访问：要求 SSL 128
// HTTP 403.6 - 禁止访问：IP 地址被拒绝
// HTTP 403.7 - 禁止访问：要求客户证书
// HTTP 403.8 - 禁止访问：禁止站点访问
// HTTP 403.9 - 禁止访问：连接的用户过多
// HTTP 403.10 - 禁止访问：配置无效
// HTTP 403.11 - 禁止访问：密码更改
// HTTP 403.12 - 禁止访问：映射器拒绝访问
// HTTP 403.13 - 禁止访问：客户证书已被吊销
// HTTP 403.15 - 禁止访问：客户访问许可过多
// HTTP 403.16 - 禁止访问：客户证书不可信或者无效
// HTTP 403.17 - 禁止访问：客户证书已经到期或者尚未生效
// HTTP 404.1 - 无法找到 Web 站点
// HTTP 404 - 无法找到文件
// HTTP 405 - 资源被禁止
// HTTP 406 - 无法接受
// HTTP 407 - 要求代理身份验证
// HTTP 410 - 永远不可用
// HTTP 412 - 先决条件失败
// HTTP 414 - 请求 - URI 太长
// HTTP 500 - 内部服务器错误
// HTTP 500.100 - 内部服务器错误 - ASP 错误
// HTTP 500-11 服务器关闭
// HTTP 500-12 应用程序重新启动
// HTTP 500-13 - 服务器太忙
// HTTP 500-14 - 应用程序无效
// HTTP 500-15 - 不允许请求 global.asa
// Error 501 - 未实现
// HTTP 502 - 网关错误

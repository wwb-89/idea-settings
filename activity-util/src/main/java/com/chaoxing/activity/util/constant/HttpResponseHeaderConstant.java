package com.chaoxing.activity.util.constant;

/**http响应头常量
 * @className HttpResponseHeaderConstant
 * @description  * @author wwb
 * @date 2017-10-30 14:58:29
 * @version ver 1.0
 */
public class HttpResponseHeaderConstant {

    private HttpResponseHeaderConstant() {

    }

    /** 表明服务器是否支持指定范围请求及哪种类型的分段请求(示例:Accept-Ranges: bytes) */
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    /** 从原始服务器到代理缓存形成的估算时间(以秒计，非负)(示例:Age: 12) */
    public static final String AGE = "Age";
    /** 对某网络资源的有效的请求行为，不允许则返回405(示例:Allow: GET, HEAD) */
    public static final String ALLOW = "Allow";
    /** 告诉所有的缓存机制是否可以缓存及哪种类型(示例:Cache-Control: no-cache) */
    public static final String CACHE_CONTROL = "Cache-Control";
    /** web服务器支持的返回内容压缩编码类型(示例:Content-Encoding: gzip) */
    public static final String CONTENT_ENCODING = "Content-Encoding";
    /** 响应体的语言(示例:Content-Language: en,zh) */
    public static final String CONTENT_LANGUAGE = "Content-Language";
    /** 响应体的长度(示例:Content-Length: 348) */
    public static final String CONTENT_LENGTH = "Content-Length";
    /** 请求资源可替代的备用的另一地址(示例:Content-Location: /index.htm) */
    public static final String CONTENT_LOCATION = "Content-Location";
    /** 返回资源的MD5校验值(示例:Content-MD5: Q2hlY2sgSW50ZWdyaXR5IQ==) */
    public static final String CONTENT_MD5 = "Content-MD5";
    /** 在整个返回体中本部分的字节位置(示例:Content-Range: bytes 21010-47021/47022) */
    public static final String CONTENT_RANGE = "Content-Range";
    /** 返回内容的MIME类型(示例:Content-Type: text/html; charset=utf-8) */
    public static final String CONTENT_TYPE = "Content-Type";
    /** 原始服务器消息发出的时间(示例:Date: Tue, 15 Nov 2010 08:12:31 GMT) */
    public static final String DATE = "Date";
    /** 请求变量的实体标签的当前值(示例:ETag: "737060cd8c284d8af7ad3082f209582d") */
    public static final String ETAG = "ETag";
    /** 响应过期的日期和时间(示例:Expires: Thu, 01 Dec 2010 16:00:00 GMT) */
    public static final String EXPIRES = "Accept";
    /** 请求资源的最后修改时间(示例:Last-Modified: Tue, 15 Nov 2010 12:45:26 GMT) */
    public static final String LAST_MODIFIED = "Last-Modified";
    /** 用来重定向接收方到非请求URL的位置来完成请求或标识新的资源(示例:Location: http://www.zcmhi.com/archives/94.html) */
    public static final String LOCATION = "Location";
    /** 包括实现特定的指令，它可应用到响应链上的任何接收方(示例:Pragma: no-cache) */
    public static final String PRAGMA = "Pragma";
    /** 它指出认证方案和可应用到代理的该URL上的参数(示例:Proxy-Authenticate: Basic) */
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    /** 应用于重定向或一个新的资源被创造，在5秒之后重定向(由网景提出，被大部分浏览器支持)(示例:Refresh: 5; url=
     http://www.zcmhi.com/archives/94.html) */
    public static final String REFRESH = "refresh";
    /** 如果实体暂时不可取，通知客户端在指定时间之后再次尝试(示例:Retry-After: 120) */
    public static final String RETRY_AFTER = "Retry-After";
    /** web服务器软件名称(示例:Server: Apache/1.3.27 (Unix) (Red-Hat/Linux)) */
    public static final String SERVER = "Server";
    /** 设置Http Cookie(示例:Set-Cookie: UserID=JohnDoe; Max-Age=3600; Version=1) */
    public static final String SET_COOKIE = "Set-Cookie";
    /** 指出头域在分块传输编码的尾部存在(示例:Trailer: Max-Forwards) */
    public static final String TRAILER = "Trailer";
    /** 文件传输编码(示例:Transfer-Encoding:chunked) */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    /** 告诉下游代理是使用缓存响应还是从原始服务器请求(示例:Vary: *) */
    public static final String VARY = "Vary";
    /** 告知代理客户端响应是通过哪里发送的(示例:Via: 1.0 fred, 1.1 nowhere.com (Apache/1.1)) */
    public static final String VIA = "Via";
    /** 警告实体可能存在的问题(示例:Warning: 199 Miscellaneous warning) */
    public static final String WARNING = "Warning";
    /** 表明客户端请求实体应该使用的授权方案(示例:WWW-Authenticate: Basic) */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

}

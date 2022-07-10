package com.chaoxing.activity.util.constant;

/**http请求头常量
 * @className HttpRequestHeaderConstant
 * @description  * @author wwb
 * @date 2017-10-30 14:56:42
 * @version ver 1.0
 */
public class HttpRequestHeaderConstant {

    private HttpRequestHeaderConstant() {

    }

    /** 指定客户端能够接收的内容类型(示例:Accept: text/plain, text/html) */
    public static final String ACCEPT = "Accept";
    /** 浏览器可以接受的字符编码集(示例:Accept-Charset: iso-8859-5) */
    public static final String ACCEPT_CHARSET = "Accept-Charset	";
    /** 指定浏览器可以支持的web服务器返回内容压缩编码类型(示例:Accept-Encoding: compress, gzip) */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    /** 浏览器可接受的语言(示例:Accept-Language: en,zh) */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    /** 可以请求网页实体的一个或者多个子范围字段(示例:Accept-Ranges: bytes) */
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    /** HTTP授权的授权证书(示例:Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==) */
    public static final String AUTHORIZATION = "Authorization";
    /** 指定请求和响应遵循的缓存机制(示例:Cache-Control: no-cache) */
    public static final String CACHE_CONTROL = "Cache-Control";
    /** 表示是否需要持久连接(HTTP 1.1默认进行持久连接)(示例:Connection: close) */
    public static final String CONNECTION = "Connection";
    /** HTTP请求发送时，会把保存在该请求域名下的所有cookie值一起发送给web服务器(示例:Cookie: $Version=1; Skin=new;) */
    public static final String COOKIE = "Cookie";
    /** 请求的内容长度(示例:Content-Length: 348) */
    public static final String CONTENT_LENGTH = "Content-Length";
    /** 请求的与实体对应的MIME信息(示例:Content-Type: application/x-www-form-urlencoded) */
    public static final String CONTENT_TYPE = "Content-Type";
    /** 请求发送的日期和时间(示例:Date: Tue, 15 Nov 2010 08:12:31 GMT) */
    public static final String DATE = "Date";
    /** 请求的特定的服务器行为(示例:Expect: 100-continue) */
    public static final String EXPECT = "Expect";
    /** 发出请求的用户的Email(示例:From: user@email.com) */
    public static final String FROM = "From";
    /** 指定请求的服务器的域名和端口号(示例:Host: www.zcmhi.com) */
    public static final String HOST = "Host";
    /** 只有请求内容与实体相匹配才有效(示例:If-Match: "737060cd8c284d8af7ad3082f209582d") */
    public static final String IF_MATCH = "If-Match";
    /** 如果请求的部分在指定时间之后被修改则请求成功，未被修改则返回304代码(示例:If-Modified-Since: Sat, 29 Oct 2010 19:43:31 GMT) */
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    /** 如果内容未改变返回304代码，参数为服务器先前发送的Etag，与服务器回应的Etag比较判断是否改变(示例:If-None-Match: "737060cd8c284d8af7ad3082f209582d") */
    public static final String IF_NONE_MATCH = "If-None-Match";
    /** 如果实体未改变，服务器发送客户端丢失的部分，否则发送整个实体。参数也为Etag(示例:If-Range: "737060cd8c284d8af7ad3082f209582d") */
    public static final String IF_RANGE = "If-Range";
    /** 只在实体在指定时间之后未被修改才请求成功(示例:If-Unmodified-Since: Sat, 29 Oct 2010 19:43:31 GMT) */
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    /** 限制信息通过代理和网关传送的时间(示例:Max-Forwards: 10) */
    public static final String MAX_FORWARDS = "Max-Forwards";
    /** 用来包含实现特定的指令(示例:Pragma: no-cache) */
    public static final String PRAGMA = "Pragma";
    /** 连接到代理的授权证书(示例:Proxy-Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==) */
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
    /** 只请求实体的一部分，指定范围(示例:Range: bytes=500-999) */
    public static final String RANGE = "Range";
    /** 先前网页的地址，当前请求网页紧随其后,即来路(示例:Referer: http://www.zcmhi.com/archives/71.html) */
    public static final String REFERER = "Referer";
    /** 客户端愿意接受的传输编码，并通知服务器接受接受尾加头信息(示例:TE: trailers,deflate;q=0.5) */
    public static final String TE = "TE";
    /** 向服务器指定某种传输协议以便服务器进行转换(如果支持)(示例:Upgrade: HTTP/2.0, SHTTP/1.3, IRC/6.9, RTA/x11) */
    public static final String UPGRADE = "Upgrade";
    /** User-Agent的内容包含发出请求的用户信息(示例:User-Agent: Mozilla/5.0 (Linux; X11)) */
    public static final String USER_AGENT = "User-Agent";
    /** 通知中间网关或代理服务器地址，通信协议(示例:Via: 1.0 fred, 1.1 nowhere.com (Apache/1.1)) */
    public static final String VIA = "Via";
    /** 关于消息实体的警告信息(示例:Warn: 199 Miscellaneous warning) */
    public static final String WARNING = "Warning";

}

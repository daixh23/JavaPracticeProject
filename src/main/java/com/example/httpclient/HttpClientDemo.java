package com.example.httpclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.nio.file.Paths;
import java.time.Duration;

/**
Java 11 特性 - 响应式流实现的HttpClient - https://baijiahao.baidu.com/s?id=1724127939982159211

背景是 JDK1.1中的HttpURLConnection是基于socket来通信，且每一个实例只能发送一个请求，之后就只能通过close()来释放请求的网络资源，或者是在持久化连接时用disconnect()来关闭底层socket。
对于各种鉴权信息，cookiee信息得访问请求支持不够好，仅支持阻塞模式。
开源选择的 Apache的 HttpClient / Okhttp Client / Spring Cloud Feign 提供了更丰富的资源与更便捷的封装，也支持高级功能如 http/2协议，异步请求等。


JDK11的新的http连接器 则支持以下新的特性：
 - Http2.0
 - Https/TLS
 - 简单的阻塞使用方式
 - 异步发送，异步时间通知 - CompletableFuture
 - WebSocket
 - 响应式流

主要类：
 - java.net.http.HttpClient
 - java.net.http.HttpRequest
 - java.net.http.HttpResponse
 - java.net.http.WebSocket

*/

public class HttpClientDemo {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Run the HttpClient Demo!");


        /**
        HttpClient主要参数
            - Http协议版本 1.1 or 2.0，默认2.0
            - 是否遵从服务器发出的重定向
            - 连接超时时间
            - 代理
            - 认证
        */
        // 可以用参数调整
        // HttpClient client = HttpClient.newBuilder()
        //                     .version(Version.HTTP_1_1)
        //                     .followRedirects(Redirect.NORMAL)
        //                     .connectTimeout(Duration.ofSeconds(20))
        //                     .proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 8080)))
        //                     .authenticator(Authenticator.getDefault())
        //                     .build();

        // 全部默认的便捷创建
        HttpClient clientSimple = HttpClient.newHttpClient();

        // 创建了HttpClient实例后，可以通过其发送多条请求，不用重复创建。

         /**
        HttpRequest主要参数
            - 请求地址
            - 请求方法 GET / PUT / POST / DELETE 默认GET
            - 请求体 （GET不用Body，POST需要设置）
            - 请求超时时间(默认)
            - 请求头
        */
        // 使用参数组合进行对象构建，读取文件做为请求
        // try {
		// 	HttpRequest request = HttpRequest.newBuilder()
		// 	                        .uri(URI.create("http://www.baidu.com"))
		// 	                        .timeout(Duration.ofSeconds(20))
		// 	                        .header("Content-type", "application/json")
		// 	                        .POST(HttpRequest.BodyPublishers.ofFile(Paths.get("data.json")))
		// 	                        .build();
		// } catch (FileNotFoundException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }

        // 直接GET访问
        HttpRequest requestSimple = HttpRequest.newBuilder(URI.create("http://www.baidu.com")).build();
        
        
        // HttpRequest 是一个不可变类，可以被多次发送。


        /**
        HttpResponse是一个接口，从client的返回值中创建获得。接口中的主要方法为：
            public interface HttpResponse<T> {
                public int statusCode();
                public HttpRequest request();
                public Optional<HttpResponse<T>> previousResponse();
                public HttpHeaders headers();
                public T body();
                public URI uri();
                public Optional<SSLSession> sslSession();
                public HttpClient.Version version();
            }

        */

        /**
        HttpClient 中可以使用同步发送或异步发送
        同步发送使用 send()，请求会一直阻塞到收到response为止。
        其中send的第二个参数是通过HttpResponse.BodyHandlers的静态工厂来返回一个可以将response转换为目标类型T的处理器（handler），本例中是String。
        其中 HttpResponse.BodyHandlers.ofString()的实现为：
            public static BodySubscriber<String> ofString(Charset charset) {
                Objects.requireNotNull(charset);
                return new ResponseSubscribers.ByteArraySubscriber<>{
                    bytes -> new String(bytes, charset)
                };
            }

        可以看到最终是返回了一个ResponseSubscribers ，而Subscribers则是我们之前《JDK9响应式编程》中讨论过的订阅者。
        这个构造方法的入参Function<byte[],T>定义了订阅者中的finisher属性，而这个属性将在响应式流完成订阅的时在onComplete()方法中调用。
        */
        // final HttpResponse<String> send = clientSimple.send(requestSimple, HttpResponse.BodyHandlers.ofString());
        // System.out.println(send.body());
        
        /**
        异步 sendAsync()
        异步请求发送之后，会立刻返回 CompletableFuture，然后可以使用CompletableFuture中的方法来设置异步处理器。

        而就如同JDK中响应式流中发布者的submit()方法与offer()方法一样，HttpClient中的send()方法通知sendAsync方法的特例，
        在send()方法中是先调用sendAsync()方法，然后直接阻塞等待响应结束再返回，部分核心代码为：

        @Override
        public <T> HttpResponse<T> send(HttpRequest req, BodyHandler<T> responseHandler) throws IOException, InterruptedException {
            CompletableFuture<HttpResponse<T>> cf = null;

            // if the thread is already interrupted no need to go further.
            // cf.get() would throw anyway.
            if (Thread.interrupted()) throw new InterruptedException();
            try {
                cf = sendAsync(req, responseHandler, null, null);
                return cf.get();
            } catch (InterruptedException ie) {
                if (cf != null) {
                    cf.cancel(true);
                }
                throw ie;
            }


        }

        */
        clientSimple.sendAsync(requestSimple, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenAccept(System.out::println).join();


        
    }
}

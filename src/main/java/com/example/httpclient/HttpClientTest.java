package com.example.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;

/**
 响应式流
 HttpClient 作为 Request 的发布者 (publisher),将 Request 发布到服务器，作为 Response 的订阅者 (subscriber),从服务器接收 Response。
 而上文中我们在send()的部分发现，调用链的最底端返回的是一个ResponseSubscribers订阅者。

 当然,就如同HttpResponse.BodyHandlers.ofString(),HttpClient默认提供了一系列的默认订阅者，用语处理数据的转换：
 - HttpRequest.BodyPublishers::ofByteArray(byte[])
 - HttpRequest.BodyPublishers::ofByteArrays(Iterable)
 - HttpRequest.BodyPublishers::ofFile(Path)
 - HttpRequest.BodyPublishers::ofString(String)
 - HttpRequest.BodyPublishers::ofInputStream(Supplier<InputStream>)
 - HttpResponse.BodyHandlers::ofByteArray()
 - HttpResponse.BodyHandlers::ofString()
 - HttpResponse.BodyHandlers::ofFile(Path)
 - HttpResponse.BodyHandlers::discarding()

 所以在HttpClient的时候我们也可以创建一个实现了Flow.Subscriber<List<ByteBuffer>>接口的订阅者
 */
public class HttpClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("http://wwww.baidu.com")).build();

        HttpResponse.BodySubscriber<String> subscriber = HttpResponse.BodySubscribers.fromSubscriber(new StringSubscriber(), StringSubscriber::getBody);
        client.sendAsync(httpRequest, responseInfo -> subscriber)
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();

    }

    static class StringSubscriber implements Flow.Subscriber<List<ByteBuffer>> {
        Flow.Subscription subscription;
        List<ByteBuffer> response = new ArrayList<>();
        String body;

        public String getBody() {
            return body;
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(List<ByteBuffer> item) {
            response.addAll(item);
            subscription.request(1);
        }
        
        @Override
        public void onError(Throwable throwable) {
            System.err.println(throwable);
        }

        @Override
        public void onComplete() {
            byte[] data = new byte[response.stream().mapToInt(ByteBuffer::remaining).sum()];
            int offset = 0;
            for (ByteBuffer buffer:response) {
                int remain = buffer.remaining();
                buffer.get(data, offset, remain);
                offset += remain;
            }
            body = new String(data);
        }
    }

}

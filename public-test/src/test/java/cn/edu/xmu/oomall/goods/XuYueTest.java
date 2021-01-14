package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 8个全部通过 By 宋润涵
 * @Author: Xu Yue 24320182203307
 *
 * 将与YangMing 重复的测试已删除
 * @Date: 2020/12/16 17:53
 */

@SpringBootTest(classes = PublicTestApp.class)
public class XuYueTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;
    private WebTestClient mallClient;


    @BeforeEach
    public void setUp() {

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://" + managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://" + mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }


    String getPath(String a) {
        return a;
    }
    @Test
    /**
     * 加入一个预售活动  无名称
     */
    public void addPresaleActivity1() throws Exception{
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime beginTime = time.plusHours(1);
        LocalDateTime payTime = time.plusHours(2);
        LocalDateTime endTime = time.plusHours(3);
        String token = login("13088admin", "123456");
        byte[] responseBuffer = null;
        String requireJson = "{ \"name\": \"\", \"advancePayPrice\": 20, \"restPayPrice\": 3000, \"quantity\": 10,\"beginTime\": \"2022-01-09T15:55:18\", \"payTime\": \"2022-01-09T16:55:18\",\"endTime\": \"2022-01-09T17:55:18\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/1/skus/2/presales")
                .header("authorization", token)
                .bodyValue(requireJson)                 ;
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

    }


    @Test
    /**
     * 修改一个预售活动  无名称
     */
    public void modifyPresaleActivity1() throws Exception{
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime beginTime = time.plusHours(1);
        LocalDateTime payTime = time.plusHours(2);
        LocalDateTime endTime = time.plusHours(3);
        String token = login("13088admin", "123456");
        byte[] responseBuffer = null;
        String requireJson = "{ \"name\": \"\", \"advancePayPrice\": 20, \"restPayPrice\": 3000, \"quantity\": 10, \"beginTime\": \"2022-01-09T15:55:18\",    \"payTime\": \"2022-01-09T16:55:18\",\"endTime\": \"2022-01-09T17:55:18\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/1/presales/1")
                .header("authorization", token)
                .bodyValue(requireJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

    }
    @Test
    /**
     * 修改一个预售活动  尾款是负数
     */
    public void modifyPresaleActivity2() throws Exception{
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime beginTime = time.plusHours(1);
        LocalDateTime payTime = time.plusHours(2);
        LocalDateTime endTime = time.plusHours(3);
        String token = login("13088admin", "123456");
        byte[] responseBuffer = null;
        String requireJson = "{ \"name\": \"预售活动改\", \"advancePayPrice\": 20, \"restPayPrice\": -3000, \"quantity\": 10, \"beginTime\": \"2022-01-09T15:55:18\",    \"payTime\": \"2022-01-09T16:55:18\",\"endTime\": \"2022-01-09T17:55:18\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/1/presales/1")
                .header("authorization", token)
                .bodyValue(requireJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

    }
    @Test
    /**
     * 修改一个预售活动  开始时间小于当前
     */
    public void modifyPresaleActivity3() throws Exception{
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime beginTime = time.minusHours(1);
        LocalDateTime payTime = time.plusHours(2);
        LocalDateTime endTime = time.plusHours(3);
        String token = login("13088admin", "123456");
        byte[] responseBuffer = null;
        String requireJson = "{ \"name\": \"预售活动改\", \"advancePayPrice\": 20, \"restPayPrice\": 3000, \"quantity\": 10, \"beginTime\": \"2020-01-09T15:55:18\",    \"payTime\": \"2022-01-09T16:55:18\",\"endTime\": \"2022-01-09T17:55:18\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/1/presales/1")
                .header("authorization", token)
                .bodyValue(requireJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

    }
    @Test
    /**
     * 修改一个预售活动  支付时间小于当前
     */
    public void modifyPresaleActivity4() throws Exception{
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime beginTime = time.plusHours(1);
        LocalDateTime payTime = time.minusHours(2);
        LocalDateTime endTime = time.plusHours(3);
        String token = login("13088admin", "123456");
        byte[] responseBuffer = null;
        String requireJson = "{ \"name\": \"预售活动改\", \"advancePayPrice\": 20, \"restPayPrice\": 3000, \"quantity\": 10, \"beginTime\": \"2022-01-09T15:55:18\",    \"payTime\": \"2020-01-09T16:55:18\",\"endTime\": \"2022-01-09T17:55:18\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/1/presales/1")
                .header("authorization", token)
                .bodyValue(requireJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

    }

    private String login(String userName, String password) throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

}


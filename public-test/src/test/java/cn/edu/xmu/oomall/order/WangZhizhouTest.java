package cn.edu.xmu.oomall.order;    //伪造token无法通过网关

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author Wang Zhizhou  24320182203289
 * @date 2020-12-16 14:15
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@Slf4j
public class WangZhizhouTest {
    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp(){
        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     * 获取支付的所有可能状态
     * @throws Exception
     */
    @Test
    public void getPaymentStatusTest() throws Exception {
        byte[] responseString = mallClient.get().uri("/payments/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"code\":0,\"name\":\"未支付\"},{\"code\":1,\"name\":\"已支付\"},{\"code\":2,\"name\":\"支付失败\"}]}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 获取所有可行的支付渠道
     * @throws Exception
     */
    @Test
    public void getPaymentPatternTest() throws Exception {
        byte[] responseString = mallClient.get().uri("/payments/patterns")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"payPattern\":\"001\",\"name\":\"返点支付\"},{\"payPattern\":\"002\",\"name\":\"模拟支付渠道\"}]}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 查找订单(id=1)的支付信息, 成功
     * @throws Exception
     */
    @Test
    public void getOrderPaymentTest1() throws Exception {
        String token = this.login();
        byte[] responseString = mallClient.get().uri("/orders/{id}/payments",1L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBody();
    }

    /**
     * 查找订单(id=12343210)的支付信息, 不存在该资源id
     * @throws Exception
     */
    @Test
    public void getOrderPaymentTest2() throws Exception {
        String token = this.login();
        byte[] responseString = mallClient.get().uri("/orders/{id}/payments",12343210L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 查找订单(id=16405)的支付信息, 操作资源id不属于操作者
     * @throws Exception
     */
    @Test
    public void getOrderPaymentTest3() throws Exception {
        String token = this.login();
        byte[] responseString = mallClient.get().uri("/orders/{id}/payments",16405L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 查找售后(id=1)的支付信息, 成功
     * @throws Exception
     */
    @Test
    public void getAfterSalePaymentTest1() throws Exception {
        String token = this.login();
        byte[] responseString = mallClient.get().uri("/aftersales/{id}/payments",1L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBody();
    }

    /**
     * 查找售后(id=12343210)的支付信息, 不存在该资源id
     * @throws Exception
     */
    @Test
    public void getAfterSalePaymentTest2() throws Exception {
        String token = this.login();
        byte[] responseString = mallClient.get().uri("/aftersales/{id}/payments",12343210L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 查找售后(id=54)的支付信息, 操作资源id不属于操作者
     * @throws Exception
     */
    @Test
    public void getAfterSalePaymentTest3() throws Exception {
        String token = this.login();
        byte[] responseString = mallClient.get().uri("/aftersales/{id}/payments",54L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();
    }

    // 完成 id=1 的顾客登录, 并返回其 token
    private String login() throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName("8606245097");
        vo.setPassword("123456");
        String requireJson = JacksonUtil.toJson(vo);
        byte[] ret = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

}

package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 订单支付部分的公开测试
 * @author 张湘君 24320182203327
 * @date 2020/12/15 20:15
 */
@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ZhangXiangJunPaymentTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private String token;

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


    /**
     * 1
     * 获得支付单的所有状态，获取成功
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月15日20:32:07
     */
    @Test
    @Order(1)
    public void getAllPaymentsStatesTest() throws Exception{
        byte[] responseString=mallClient.get().uri("/payments/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"未支付\",\"code\":0},{\"name\":\"已支付\",\"code\":1},{\"name\":\"支付失败\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }


    /**
     * 2
     * 获得支付渠道，目前只返回002 模拟支付渠道，001返点支付
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月15日20:32:07
     */
    @Test
    @Order(2)
    public void getPaymentPatternsTset1() throws Exception{
        byte[] responseString=mallClient.get().uri("/payments/patterns")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"返点支付\",\"payPattern\":\"001\"},{\"name\":\"模拟支付渠道\",\"payPattern\":\"002\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }
}

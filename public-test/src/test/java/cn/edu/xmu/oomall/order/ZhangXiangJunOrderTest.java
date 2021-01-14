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
 * 订单模块公开测试
 *
 * @author 张湘君 24320182203327
 * @date 2020/12/13 20:15
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ZhangXiangJunOrderTest {
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

    /**
     * 1
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(1)
    public void shopUpdateOrderTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{ \"message\": \"test\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/orders/{id}", 123, 40000)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBody();

//        //查询
//        byte[] responseString1 = manageClient.get().uri("/shops/{shopId}/orders/{id}", 123, 40000)
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .returnResult()
//                .getResponseBody();
//        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":40000,\"message\":\"test\"},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString1, StandardCharsets.UTF_8), false);
    }


    /**
     * 2
     * 不是自己店铺的资源
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(2)
    public void shopUpdateOrderTest2() throws Exception {
        String token = this.login("537300010", "123456");
        String json = "{ \"message\": \"test\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/orders/{id}", 1, 40000)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 3
     * 不存在这个资源
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(3)
    public void shopUpdateOrderTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{ \"message\": \"test\"}";
        byte[] responseString = manageClient.put().uri("/shops/{shopId}/orders/{id}", 123, 100000)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 4
     * 商铺将支付完成的订单改为发货状态(状态码为21的订单才能修改为发货状态)
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(4)
    public void shopDeliverOrderTest1() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/{shopId}/orders/{id}/deliver", 123, 40000)
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"123456\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBody();

//        //查询
//        byte[] responseString1 = manageClient.get().uri("/shops/{shopId}/orders/{id}", 123, 40000)
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .returnResult()
//                .getResponseBody();
//        System.out.println(new String(responseString1, StandardCharsets.UTF_8));
//        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":40000,\"shipmentSn\":\"123456\",\"subState\":24},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString1, StandardCharsets.UTF_8), false);
    }

    /**
     * 5
     * 商铺将支付完成的订单改为发货状态(状态码为21的订单才能修改为发货状态),但订单状态不为21无法完成转化
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(5)
    public void shopDeliverOrderTest2() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/{shopId}/orders/{id}/deliver", 123, 40001)
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"123456\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBody();

    }

    private String login(String userName, String password) throws Exception {
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }
}

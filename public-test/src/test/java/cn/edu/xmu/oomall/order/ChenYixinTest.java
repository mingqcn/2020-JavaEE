package cn.edu.xmu.oomall.order;
import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.nio.charset.StandardCharsets;


/**
 * 订单模块店家及管理员部分公开测试
 * @author ChenYixin 24320182203180
 * @date 2020/12/12 19:28
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChenYixinTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp(){

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

    }


    //卖家登录
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

    /**
     * 店家查询商户所有订单 (概要) 仅输入shopId
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(1)
    public void shopsShopIdOrdersGet0() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString=manageClient.get().uri("/shops/406/orders").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 8,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48050,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 11,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48052,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 21,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48053,\n" +
                "        \"customerId\": 4,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 22,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48054,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 23,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48055,\n" +
                "        \"customerId\": 7,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 24,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48056,\n" +
                "        \"customerId\": 7,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 3,\n" +
                "        \"subState\": 11,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48057,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 4,\n" +
                "        \"subState\": 11,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家查询商户所有订单 (概要) 输入shopId及page=1 pageSize=3
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(2)
    public void shopsShopIdOrdersGet1() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString=manageClient.get().uri("/shops/406/orders?page=1&pageSize=3").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 8,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48050,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 11,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48052,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 21,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家查询商户所有订单 (概要) 查找指定买家且page=1,pageSize=3
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(3)
    public void shopsShopIdOrdersGet2() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString=manageClient.get().uri("/shops/406/orders?customerId=2&page=1&pageSize=3").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家查询商户所有订单 (概要) 查找指定订单编号
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(4)
    public void shopsShopIdOrdersGet3() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString=manageClient.get().uri("/shops/406/orders?orderSn=2016102363333").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":48052,\"customerId\":1,\"shopId\":406,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）订单id不存在
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(5)
    public void shopsShopIdOrdersIdGet1() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString=manageClient.get().uri("/shops/406/orders/4000000").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）查找非本店铺订单
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(6)
    public void shopsShopIdOrdersIdGet2() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString=manageClient.get().uri("/shops/406/orders/1").header("authorization",token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单留言 成功
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(7)
    public void shopsShopIdOrdersIdPut0() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48050")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/shops/406/orders/48050")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48050,message:test}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家修改订单留言 订单id不存在
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(8)
    public void shopsShopIdOrdersIdPut1() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/4000000")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单留言 订单非本店铺订单
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(9)
    public void shopsShopIdOrdersIdPut2() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/1")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 成功
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postFreights0() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48052/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/shops/406/orders/48052")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48052,shipmentSn:test}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);

    }

    /**
     * 店家对订单标记发货 操作的订单id不存在
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(11)
    public void postFreights1() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/4000000/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 操作的订单不是本店铺的
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(12)
    public void postFreights2() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/1/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 订单状态为新订单不满足待发货
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(13)
    public void postFreights3() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48050/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 订单状态为待支付尾款
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(14)
    public void postFreights4() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48051/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 订单状态为待成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(15)
    public void postFreights5() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48053/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 订单状态为未成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(16)
    public void postFreights6() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48054/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 订单状态为已发货
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(17)
    public void postFreights7() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48055/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 订单状态为已完成
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(18)
    public void postFreights8() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48056/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 订单状态为已取消
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(19)
    public void postFreights9() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48057/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 获得订单所有状态
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(20)
    public void getorderState() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString=manageClient.get().uri("/orders/states").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"待付款\",\n" +
                "      \"code\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"待收货\",\n" +
                "      \"code\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"已完成\",\n" +
                "      \"code\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"已取消\",\n" +
                "      \"code\": 4\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"新订单\",\n" +
                "      \"code\": 11\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"待支付尾款\",\n" +
                "      \"code\": 12\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"付款完成\",\n" +
                "      \"code\": 21\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"待成团\",\n" +
                "      \"code\": 22\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"未成团\",\n" +
                "      \"code\": 23\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"已发货\",\n" +
                "      \"code\": 24\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 订单状态为新订单 成功
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(21)
    public void shopsShopIdOrdersIdDelete0() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48050")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/shops/406/orders/48050")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48050,state:4}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 订单状态为待支付尾款
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(22)
    public void shopsShopIdOrdersIdDelete1() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48051")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/shops/406/orders/48051")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48051,state:4}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 取消的订单id不存在
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(23)
    public void shopsShopIdOrdersIdDelete2() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/4000000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 取消的订单不是本店铺的
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(24)
    public void shopsShopIdOrdersIdDelete3() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单
     * 由于postFreight0修改了订单48052的状态 所以此时该订单的状态已发货，不能取消
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(25)
    public void shopsShopIdOrdersIdDelete4() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48052")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 订单状态为待成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(26)
    public void shopsShopIdOrdersIdDelete5() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48053")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/shops/406/orders/48053")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48053,state:4}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 订单状态为未成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(27)
    public void shopsShopIdOrdersIdDelete6() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48054")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\": 0,\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        byte[] confirmString = manageClient.get().uri("/shops/406/orders/48054")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48054,state:4}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 订单状态为已发货
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(28)
    public void shopsShopIdOrdersIdDelete7() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48055")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 订单状态为已完成
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(29)
    public void shopsShopIdOrdersIdDelete8() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48056")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 订单状态为已取消
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(30)
    public void shopsShopIdOrdersIdDelete9() throws Exception {

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48057")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家查询商户所有订单 (概要) JWT不合法
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(31)
    public void shopsShopIdOrdersGetNotLogin() throws Exception {
        String token="test";

        byte[] responseString=manageClient.get().uri("/shops/406/orders").header("authorization",token).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）JWT不合法
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(32)
    public void shopsShopIdOrdersIdGetNotLogin() throws Exception {
        String token="test";

        byte[] responseString=manageClient.get().uri("/shops/406/orders/48050").header("authorization",token).exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单留言 JWT不合法
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(33)
    public void shopsShopIdOrdersIdPutNotLogin() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token="test";

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48050")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家对订单标记发货 JWT不合法
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(34)
    public void postFreightsNotLogin() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token="test";

        byte[] responseString = manageClient.put().uri("/shops/406/orders/48052/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 JWT不合法
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(35)
    public void shopsShopIdOrdersIdDeleteNotLogin() throws Exception {

        String token="test";
        byte[] responseString = manageClient.delete().uri("/shops/406/orders/48050")
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_INVALID_JWT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
}

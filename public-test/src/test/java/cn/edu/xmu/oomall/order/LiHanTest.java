package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

/**
 * 测试用例：运费模板模块
 *
 * @author 李涵 2203919
 * Created at 14/12/2020 8:21 上午
 * Modified by Han Li at 14/12/2020 8:21 上午
 */
@SpringBootTest(classes = PublicTestApp.class)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LiHanTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    /**
     * 初始化 Client
     */
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
     * 生成 Token
     * @param userId
     * @param departId
     * @param expireTime
     * @return
     */
    String genToken(Long userId, Long departId, int expireTime) {
        JwtHelper jwtHelper = new JwtHelper();
        return jwtHelper.createToken(userId, departId, expireTime);
    }

    /**
     * 管理员登入
     * @param username 用户名
     * @param password 密码
     * @throws Exception parse error
     */
    private String adminLogin(String username, String password) {
        LoginVo vo = new LoginVo();
        vo.setUserName(username);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        assert requireJson != null;
        byte[] ret = manageClient.post()
                .uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated() // 邱老师圣旨：登入成功为 201
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        assert ret != null;
        return JacksonUtil.parseString(new String(ret, StandardCharsets.UTF_8), "data");
    }

    /**
     * 用户登入
     * @param username 用户名
     * @param password 密码
     * @throws Exception parse error
     */
    private String customerLogin(String username, String password) {
        LoginVo vo = new LoginVo();
        vo.setUserName(username);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        assert requireJson != null;
        byte[] ret = mallClient.post()
                .uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated() // 邱老师圣旨：登入成功为 201
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        assert ret != null;
        return JacksonUtil.parseString(new String(ret, StandardCharsets.UTF_8), "data");
    }

    /*
    运费模板部分
     */

    /**
     * 定义运费模板 (件数)
     * @throws Exception
     */
    @Test
    @Order(1)
    public void defFreightModel() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        String json = "{\"name\":\"LH运费模板1\",\"type\":0,\"unit\":500}";
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        // 新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/shops/7/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expectedResponse, defString, false);
    }

    /**
     * 定义运费模板 (模板名重复)
     * @throws Exception
     */
    @Test
    @Order(2)
    public void defDuplicateFreightModel() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        String json = "{\"name\":\"LH重复定义测试模板\",\"type\":0,\"unit\":500}";
        Integer expectErrNo = 802;

        // 尝试新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/shops/7/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONObject responseObj = JSONObject.parseObject(defString);
        Integer errNo = responseObj.getInteger("errno");
        String errMsg = responseObj.getString("errmsg");

        // errno
        Assert.isTrue(expectErrNo.equals(errNo), "errno 不是 " + expectErrNo + " 而是 " + errNo + " errMsg 是 " + errMsg);
    }

    /**
     * 定义 (重量) 运费模板 (即使 type 不同也存在模板名重复)
     * @throws Exception
     */
    @Test
    @Order(3)
    public void defDuplicateDifferentTypeFreightModel() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        String json = "{\"name\":\"LH重复定义测试模板\",\"type\":1,\"unit\":500}";
        Integer expectErrNo = 802;

        // 尝试新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/shops/7/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONObject responseObj = JSONObject.parseObject(defString);
        Integer errNo = responseObj.getInteger("errno");
        String errMsg = responseObj.getString("errmsg");

        // errno
        Assert.isTrue(expectErrNo.equals(errNo), "errno 不是 " + expectErrNo + " 而是 " + errNo + " errMsg 是 " + errMsg);
    }

    /**
     * 定义件数运费模板明细 (地区码为 200)
     * @throws Exception
     */
    @Test
    @Order(4)
    public void defPieceFreightModelRule() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        String json = "{\n" +
                "  \"regionId\": 200,\n" +
                "  \"firstItem\": 2,\n" +
                "  \"firstItemPrice\": 10000,\n" +
                "  \"additionalItems\": 2,\n" +
                "  \"additionalItemsPrice\": 5000\n" +
                "}";

        // 新建运费模板明细
        manageClient
                .post()
                .uri("/shops/7/freightmodels/284811/pieceItems")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 克隆运费模板 (件数)
     * @throws Exception
     */
    @Test
    @Order(5)
    public void cloneFreightModel() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        byte[] responseBytes = manageClient
                .post()
                .uri("/shops/7/freightmodels/284811/clone")
                .header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

        // 获取定义的运费模板
        JSONObject queryResponse = JSONObject.parseObject(responseString);
        JSONObject clonedModel = queryResponse
                .getJSONObject("data");
        Long clonedFmId = clonedModel.getLong("id");

        // 查询定义的运费模板能否查出来

        byte[] queryResponseString = manageClient
                .get()
                .uri("/shops/7/freightmodels/" + clonedFmId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 克隆运费模板 (找不到源)
     * @throws Exception
     */
    @Test
    @Order(6)
    public void cloneFreightModelNotFound() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        manageClient
                .post()
                .uri("/shops/7/freightmodels/2643126963/clone")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 查询商家的运费模板 (无分页)
     * @throws Exception
     */
    @Test
    @Order(8)
    public void findFreightModels() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");

        // 新建运费模板
        byte[] responseString = manageClient
                .get()
                .uri("/shops/7/freightmodels")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);

        JSONObject response = JSONObject.parseObject(defString);

        Assert.isTrue(response.getString("errmsg").equals("成功"), "查询不成功");
        JSONArray modelArr = response
                .getJSONObject("data")
                .getJSONArray("list");

        boolean firstChecked = false;
        boolean secondChecked = false;
        for (int i = 0; i < modelArr.size(); i++) {
            JSONObject model = modelArr.getJSONObject(i);
            if (model.getLong("id") == 284810L) {
                firstChecked = true;
            } else if (model.getLong("id").equals(284811L)) {
                secondChecked = true;
            }
        }
        Assert.isTrue(firstChecked, "第一个运费模板没有找到，id=" + 284810L);
        Assert.isTrue(secondChecked, "克隆的运费模板没有找到，id=" + 284811L);
    }

    /**
     * 删除运费模板 (克隆源)
     * @throws Exception
     */
    @Test
    @Order(9)
    public void delFreightModel() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        manageClient
                .delete()
                .uri("/shops/7/freightmodels/284811")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 删除运费模板 (找不到)
     * @throws Exception
     */
    @Test
    @Order(10)
    public void delFreightModelNotFound() throws Exception {
        String token = adminLogin("shopadmin_No2", "123456");
        manageClient
                .delete()
                .uri("/shops/7/freightmodels/5646241156151")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /*
    订单部分
     */

    /**
     * 店家修改订单
     */
    @Test
    @Order(11)
    public void shopEditOrder() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/shops/7/orders/2203919")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/shops/7/orders/2203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 2203919,\n" +
                "    \"message\": \"我愛你\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家修改订单 (无权限)
     */
    @Test
    @Order(12)
    public void shopEditOrderNoRights() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"有內鬼终止交易\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/shops/7/orders/2203923") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isForbidden() // 不被批准
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 店家修改订单 (不存在订单)
     * @throws Exception
     */
    @Test
    @Order(13)
    public void shopEditOrderNotExist() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        manageClient
                .put()
                .uri("/shops/7/orders/2203928") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单 (字段不合法)
     * @throws Exception
     */
    @Test
    @Order(14)
    public void shopEditOrderFieldIllegal() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{}";
        manageClient
                .put()
                .uri("/shops/7/orders/2203919") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单 (伪造 JWT)
     * @throws Exception
     */
    @Test
    @Order(15)
    public void shopEditOrderTokenIllegal() throws Exception {
        // depart = 7L
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        manageClient
                .put()
                .uri("/shops/7/orders/2203928") // depart=2
                .header("authorization", "12u8789781379127312ui3y1i3")
                .bodyValue(body)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货 (字段不合法)
     * @throws Exception
     */
    @Test
    @Order(16)
    public void shopDeliverFieldIllegal() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{}";
        manageClient
                .put()
                .uri("/shops/7/orders/2203919/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货
     * @throws Exception
     */
    @Test
    @Order(17)
    public void shopDeliver() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/shops/7/orders/2203919/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/shops/7/orders/2203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 2203919,\n" +
                "    \"state\": 2,\n" +
                "    \"subState\": 24,\n" +
                "    \"shipmentSn\": \"1212121212123\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家发货 (发货单号为空)
     * @throws Exception
     */
    @Test
    @Order(18)
    public void shopDeliverAlreadyDelivered() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"\"\n" +
                "}";
        manageClient
                .put()
                .uri("/shops/7/orders/2203919/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (状态不允许)
     * @throws Exception
     */
    @Test
    @Order(19)
    public void shopDeliverStateNotAllow() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        manageClient
                .put()
                .uri("/shops/7/orders/2203921/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (无权限)
     */
    @Test
    @Order(20)
    public void shopDeliverNoRights() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/shops/7/orders/2203923/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (不存在订单)
     * @throws Exception
     */
    @Test
    @Order(21)
    public void shopDeliverNotExist() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        manageClient
                .put()
                .uri("/shops/7/orders/2203928/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /*
     * 支付部分
     */

    /**
     * 店家获取店内订单的支付单
     * @throws Exception
     */
    @Test
    @Order(22)
    // todo 修改 json 字段, 标准api 中为 aftersaleId 而非 afterSaleId
    public void shopGetOrderPayment() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");;
        byte[] responseBytes = manageClient
                .get()
                .uri("/shops/7/orders/2203919/payments")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 1398040,\n" +
                "            \"orderId\": 2203919,\n" +
                "            \"amount\": 25535,\n" +
                "            \"actualAmount\": 25535,\n" +
                "            \"paymentPattern\": \"002\",\n" +
                "            \"aftersaleId\": null\n" +
                "        }\n" +
                "    ],\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString, responseString, false);
    }

    /**
     * 店家获取店内订单的支付单 (无权限)
     * @throws Exception
     */
    @Test
    @Order(23)
    public void shopGetOrderPaymentNoAuth() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");;
        byte[] responseBytes = manageClient
                .get()
                .uri("/shops/7/orders/2203923/payments")
                .header("authorization", token)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家获取店内订单的支付单 (无此订单号)
     * @throws Exception
     */
    @Test
    @Order(24)
    public void shopGetOrderPaymentNoOrder() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");;
        byte[] responseBytes = manageClient
                .get()
                .uri("/shops/7/orders/2203928/payments")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家获取本人名下的支付单
     * @throws Exception
     */
    @Test
    @Order(25)
    // todo 修改 json 字段, 标准api 中为 aftersaleId 而非 afterSaleId
    public void customerGetOrderPayment() throws Exception {
        // userId = 2668
        String token = this.customerLogin("48613511536", "123456");
        byte[] responseBytes = mallClient
                .get()
                .uri("/orders/2203920/payments")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 1398041,\n" +
                "            \"orderId\": 2203920,\n" +
                "            \"amount\": 66800,\n" +
                "            \"actualAmount\": 66800,\n" +
                "            \"paymentPattern\": \"002\",\n" +
                "            \"aftersaleId\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 1398042,\n" +
                "            \"orderId\": 2203920,\n" +
                "            \"amount\": 200,\n" +
                "            \"actualAmount\": 200,\n" +
                "            \"paymentPattern\": \"001\",\n" +
                "            \"aftersaleId\": null\n" +
                "        }\n" +
                "    ],\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString, responseString, false);
    }

    /**
     * 买家获订单的支付单 (无权限)
     * @throws Exception
     */
    @Test
    @Order(26)
    public void customerGetOrderPaymentNoAuth() throws Exception {
        // userId = 2668
        String token = this.customerLogin("48613511536", "123456");
        byte[] responseBytes = mallClient
                .get()
                .uri("/orders/2203924/payments")
                .header("authorization", token)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家获取订单的支付单 (无此订单号)
     * @throws Exception
     */
    @Test
    @Order(27)
    public void customerGetOrderPaymentNoOrder() throws Exception {
        // userId = 2668
        String token = this.customerLogin("48613511536", "123456");
        byte[] responseBytes = mallClient
                .get()
                .uri("/orders/2203928/payments")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家为订单支付 (订单状态禁止)
     * @throws Exception
     */
    @Test
    @Order(28)
    public void createPaymentNotAllow() throws Exception {
        // userId = 2668
        String token = this.customerLogin("48613511536", "123456");
        String body = "{\n" +
                "  \"price\": 9,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .post()
                .uri("/orders/2203919/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家为订单支付
     * @throws Exception
     */
    @Test
    @Order(29)
    public void createPayment() throws Exception {
        // userId = 2668
        String token = this.customerLogin("48613511536", "123456");
        String body = "{\n" +
                "  \"price\": 63800,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .post()
                .uri("/orders/2203922/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 买家为订单支付 (无此订单)
     * @throws Exception
     */
    @Test
    @Order(30)
    public void createPaymentNoOrder() throws Exception {
        // userId = 2668
        String token = this.customerLogin("48613511536", "123456");
        String body = "{\n" +
                "  \"price\": 63800,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .post()
                .uri("/orders/2203928/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家为订单支付 (非本人订单)
     * @throws Exception
     */
    @Test
    @Order(31)
    public void createPaymentNotPersonal() throws Exception {
        // userId = 2668
        String token = this.customerLogin("48613511536", "123456");
        String body = "{\n" +
                "  \"price\": 63800,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        mallClient
                .post()
                .uri("/orders/2203925/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家为订单支付 (超额支付)
     * @throws Exception
     */
    @Test
    @Order(32)
    public void createPaymentOverflow() throws Exception {
        // userId = 7768
        String token = this.customerLogin("76613529842", "123456");
        String body = "{\n" +
                "  \"price\": 99999999,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] response = mallClient
                .post()
                .uri("/orders/2203925/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectBody().jsonPath("$.errno").exists()
                .returnResult().getResponseBody();

        assert response != null;
        String responseStr = new String(response, StandardCharsets.UTF_8);
        String notExpectedErrNo = "\"errno\": 0";
        JSONAssert.assertNotEquals(notExpectedErrNo, responseStr, false);
    }
}

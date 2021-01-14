package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * @author Cai Xinlu  24320182203165
 * @date 2020-12-14 17:28
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@Slf4j
public class CaiXinluTest {
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

    private String adminLogin(String userName, String password) throws Exception{
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
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }

    private String userLogin(String userName, String password) throws Exception{
        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
    }
    /**
     * 通过aftersaleId查找refund 成功
     */
    @Test
    public void getRefundTest1() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/aftersales/{id}/refunds",1)
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
     * 通过aftersaleId查找refund  找不到路径上的aftersaleId
     */
    @Test
    public void getRefundTest2() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/aftersales/{id}/refunds",666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过aftersaleId查找refund  orderId不属于Token解析出来的userId
     */
    @Test
    public void getRefundTest3() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/aftersales/{id}/refunds",295)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId查找refund  成功
     */
    @Test
    public void getRefundTest4() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",1)
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
     * 通过orderId查找refund  找不到路径上的orderId
     */
    @Test
    public void getRefundTest5() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过orderId查找refund  orderId不属于Token解析出来的userId
     */
    @Test
    public void getRefundTest6() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/{id}/refunds",2)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  通过aftersaleId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest7() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString =
                manageClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",666666,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  成功
     */
    @Test
    public void getRefundTest8() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =
                manageClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",1,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  找不到aftersaleId
     */
    @Test
    public void getRefundTest9() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =
                manageClient.get().uri("/shops/{shopId}/aftersales/{id}/refunds",1,666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId和shopId查找refund  通过orderId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest10() throws Exception{
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString =
                manageClient.get().uri("/shops/{shopId}/orders/{id}/refunds",666666,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过orderId和shopId查找refund  成功
     */
    @Test
    public void getRefundTest11() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =
                manageClient.get().uri("/shops/{shopId}/orders/{id}/refunds",1,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId和shopId查找refund  找不到orderId
     */
    @Test
    public void getRefundTest12() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =
                manageClient.get().uri("/shops/{shopId}/orders/{id}/refunds",1,666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 修改运费模板 成功
     * @throws Exception
     */
    @Test
    public void changeFreightModel1() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\"name\": \"freightModeTest\",\"unit\": 90}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/freightmodels/{id}",295,88888)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 修改运费模板  路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void changeFreightModel2() throws Exception
    {
        String token = this.adminLogin("537300010", "123456");
        String freightJson = "{\"name\": \"freightModel\",\"unit\": 100}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/freightmodels/{id}",295,88888)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改运费模板  运费模板名重复
     * @throws Exception
     */
    @Test
    public void changeFreightModel3() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\"name\": \"freightModel2\",\"unit\": 100}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/freightmodels/{id}",295,99999)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.FREIGHTNAME_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }


    /**
     * 修改件数运费模板 运费模板中该地区已经定义  region已存在
     * @throws Exception
     */
    @Test
    public void changePieceFreightModel1() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstItem\": 60,\n" +
                "    \"firstItemPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33,\n" +
                "    \"regionId\": 111\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/pieceItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改件数运费模板 路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void changePieceFreightModel2() throws Exception
    {
        String token = this.adminLogin("537300010", "123456");
        String freightJson = "{\n" +
                "    \"firstItem\": 60,\n" +
                "    \"firstItemPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/pieceItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 件数运费模板修改成功
     * @throws Exception
     */
    @Test
    public void changePieceFreightModel3() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstItem\": 60,\n" +
                "    \"firstItemPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/pieceItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改重量运费模板 运费模板中该地区已经定义  region已存在
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel1() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391,\n" +
                "    \"regionId\": 111\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/weightItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 重量运费模板修改成功
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel2() throws Exception
    {
        String token = this.adminLogin("13088admin", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/weightItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 修改重量运费模板 路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel3() throws Exception
    {
        String token = this.adminLogin("537300010", "123456");
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/shops/{shopId}/weightItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 查找订单的所有状态
     */
    @Test
    public void getOrderState() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders/states")
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"name\": \"已取消\",\n" +
                "            \"code\": 4\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"待付款\",\n" +
                "            \"code\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"待成团\",\n" +
                "            \"code\": 22\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"待支付尾款\",\n" +
                "            \"code\": 12\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"新订单\",\n" +
                "            \"code\": 11\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"未成团\",\n" +
                "            \"code\": 23\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"付款完成\",\n" +
                "            \"code\": 21\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"已完成\",\n" +
                "            \"code\": 3\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"待收货\",\n" +
                "            \"code\": 2\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"已发货\",\n" +
                "            \"code\": 24\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }

    /**
     * 查找订单  查询条件：通过orderSn查找
     */
    @Test
    public void getOrders1() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders?orderSn=2016102322523&page=1&pageSize=5")
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
     * 查找订单  查询条件：通过beginTime查找  即查找创建时间在beginTime之后的订单
     */
    @Test
    public void getOrders2() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders?page=1&pageSize=5&beginTime=2020-11-24 18:40:20")
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
     * 查找订单  查询条件：通过endTime查找  即查找创建时间在endTime之前的订单
     */
    @Test
    public void getOrders3() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders?page=1&endTime=2021-11-23 18:40:20&pageSize=5")
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
     * 查找订单  查询条件：通过beginTime和endTime联合查找  即查找创建时间在beginTime和endTime之间的订单
     */
    @Test
    public void getOrders4() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders?page=1&endTime=2021-11-23 18:40:20&pageSize=5&beginTime=2020-11-24 18:40:20")
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
     * ”beginTime在endTime之后“，不合法的查询条件应该返回空列表
     */
//    /**
//     * 查找订单  查询条件：beginTime在endTime之后，返回503错误码
//     */
//    @Test
//    public void getOrders5() throws Exception {
//        String token = this.userLogin("8606245097", "123456");
//        byte[] responseString =
//                mallClient.get().uri("/orders?page=1&endTime=2019-11-23 18:40:20&pageSize=5&beginTime=2020-11-24 18:40:20")
//                        .header("authorization", token)
//                        .exchange()
//                        .expectStatus().isBadRequest()
//                        .expectBody()
//                        .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
//                        .returnResult()
//                        .getResponseBody();
//
//    }

    /**
     * 查找订单  查询条件：beginTime或endTime格式错误,返回错误码503
     */
    @Test
    public void getOrders6() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/orders?page=1&pageSize=5&beginTime=2020-11-2418:40:20")
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isBadRequest()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 查找支付单的所有状态
     */
    @Test
    public void getPaymentState() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("/payments/states")
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"name\": \"未支付\",\n" +
                "            \"code\": 0\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"已支付\",\n" +
                "            \"code\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"支付失败\",\n" +
                "            \"code\": 2\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }


    /**
     * 计算运费1
     *
     * @throws Exception
     */
    @Test
    public void calculateFreight1() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String json = "[{\"count\":6,\"skuId\":10000},{\"count\":2,\"skuId\":10001},{\"count\":1,\"skuId\":10002}]";
        byte[] responseString = mallClient.post().uri("/region/2/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"data\":1794}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

//     /**
//      * 计算运费不可达
//      *
//      * @throws Exception
//      */
//     @Test
//     public void calculateFreight2() throws Exception {
//         String token = this.userLogin("8606245097", "123456");
//         String json = "[{\"count\":6,\"skuId\":10000},{\"count\":2,\"skuId\":10001},{\"count\":1,\"skuId\":10002}]";
//         byte[] responseString = mallClient.post().uri("/region/2001/price").header("authorization", token)
//                 .bodyValue(json)
//                 .exchange()
//                 .expectStatus().isOk()
//                 .expectBody()
//                 .returnResult()
//                 .getResponseBodyContent();

//         String expectedResponse = "{\"errno\":805}";
//         JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//     }
    /**
     * 计算运费不可达
     *
     * @throws Exception
     */
    @Test
    public void calculateFreight3() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String json = "[{\"count\":1,\"skuId\":10000},{\"count\":1,\"skuId\":10001},{\"count\":1,\"skuId\":10002}]";
        byte[] responseString = mallClient.post().uri("/region/20001/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":805}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 计算运费2
     *
     * @throws Exception
     */
    @Test
    public void calculateFreight4() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        String json = "[{\"count\":1,\"skuId\":10000},{\"count\":1,\"skuId\":10001},{\"count\":1,\"skuId\":10002}]";
        byte[] responseString = mallClient.post().uri("/region/2/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":522}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
}

package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * 订单模块-支付服务、运费模板服务
 *
 * @author  24320182203196 洪晓杰
 * @date 2020/12/15 09:20
 */
@SpringBootTest(classes = PublicTestApp.class)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HongXiaojieTest {

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




    /**
     * 管理员查询售后单的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId3() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/shops/{shopId}/aftersales/{id}/payments",57101,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }


    /**
     * 管理员查询售后单的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId4() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/shops/{shopId}/aftersales/{id}/payments",57101,47001)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBody();
    }



    /**
     * 管理员查询售后单的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId5() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/shops/{shopId}/aftersales/{id}/payments",57010,47004)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }




    /**
     * 买家查询自己的支付信息，success
     * @author 洪晓杰
     */
    @Test
    public void userQueryPaymentTest() throws Exception{
        String token = userLogin("44357456028", "123456");
        byte[] responseString=mallClient.get().uri("/orders/{id}/payments",47123)
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
     * 买家查询自己的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void userQueryPaymentTest2() throws Exception{
        String token = userLogin("44357456028", "123456");
        byte[] responseString=mallClient.get().uri("/orders/{id}/payments",48230)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }




    /**
     * 管理员查询订单的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void queryPaymentTest3() throws Exception{
        String token = this.login("13088admin", "123456");
        byte[] responseString=manageClient.get().uri("/shops/{shopId}/orders/{id}/payments",77777,47123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();

    }





    /**
     * 店家或管理员为商铺定义默认运费模板，失败
     * @author 洪晓杰
     */
    @Test
    public void setupDefaultModelTest1() throws Exception{
        String token = null;

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/default",47012,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

//     /**
//      * 店家或管理员为商铺定义默认运费模板，失败
//      * @author 洪晓杰
//      */
//     @Test
//     @Order(14)
//     public void setupDefaultModelTest2() throws Exception{
//         String token = this.login("13088admin", "123456");

//         byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/default",47012,47011)
//                 .header("authorization", token)
//                 .exchange()
//                 .expectStatus().isOk()
//                 .expectBody()
//                 .jsonPath("$.errno").isEqualTo(ResponseCode.DEFAULTMODEL_EXISTED.getCode())
//                 .returnResult()
//                 .getResponseBodyContent();
//     }

    /**
     * ERROR: FIRST GROUP ERROR
     * 管理员定义管理员定义重量模板明细，success
     * @author 洪晓杰
     */
    @Test
    @Order(15)
    public void insertWeightFreightModelTest4() throws Exception{
        String token = this.login("13088admin", "123456");
        String weightFreightModelJson = "{\n" +
                "  \"abovePrice\": 0,\n" +
                "  \"fiftyPrice\": 0,\n" +
                "  \"firstWeight\": 0,\n" +
                "  \"firstWeightFreight\": 0,\n" +
                "  \"hundredPrice\": 0,\n" +
                "  \"regionId\": 0,\n" +
                "  \"tenPrice\": 0,\n" +
                "  \"trihunPrice\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/weightItems",47002,47012)
                .header("authorization", token)
                .bodyValue(weightFreightModelJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.MODEL_TYPE_DISMATCH.getCode())
                .returnResult()
                .getResponseBodyContent();
    }




    /**
     * 店家或管理员为商铺定义默认运费模板，失败
     * @author 洪晓杰
     */
    @Test
    public void insertPieceFreightModelTest6() throws Exception{
        String token = null;
        String pieceFreightModelJson = "{\n" +
                "  \"additionalItem\": 0,\n" +
                "  \"additionalItemsPrice\": 0,\n" +
                "  \"firstItem\": 0,\n" +
                "  \"firstItemsPrice\": 0,\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/default",47012,47011)
                .header("authorization", token)
                .bodyValue(pieceFreightModelJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家或管理员为商铺定义默认运费模板，失败
     * @author 洪晓杰
     */
    @Test
    public void insertPieceFreightModelTest7() throws Exception{
        String token = null;
        String pieceFreightModelJson = "{\n" +
                "  \"additionalItem\": 0,\n" +
                "  \"additionalItemsPrice\": 0,\n" +
                "  \"firstItem\": 0,\n" +
                "  \"firstItemsPrice\": 0,\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/default",47012,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 管理员定义件数模板明细，失败
     * @author 洪晓杰
     */
    @Test
    public void insertPieceFreightModelTest8()  throws Exception{
        String token = null;
        String pieceFreightModelJson = "{\n" +
                "  \"additionalItem\": 0,\n" +
                "  \"additionalItemsPrice\": 0,\n" +
                "  \"firstItem\": 0,\n" +
                "  \"firstItemsPrice\": 0,\n" +
                "  \"regionId\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/pieceItems",47012,47011)
                .header("authorization", token)
                .bodyValue(pieceFreightModelJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员定义管理员定义重量模板明细，失败
     * @author 洪晓杰
     */
    @Test
    public void insertWeightFreightModelTest9() throws Exception{
        String token = null;
        String weightFreightModelJson = "{\n" +
                "  \"abovePrice\": 0,\n" +
                "  \"fiftyPrice\": 0,\n" +
                "  \"firstWeight\": 0,\n" +
                "  \"firstWeightFreight\": 0,\n" +
                "  \"hundredPrice\": 0,\n" +
                "  \"regionId\": 0,\n" +
                "  \"tenPrice\": 0,\n" +
                "  \"trihunPrice\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/shops/{shopId}/freightmodels/{id}/weightItems",47002,47012)
                .header("authorization", token)
                .bodyValue(weightFreightModelJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家修改本人名下订单，success
     * @author 洪晓杰
     */
    @Test
    @Order(4)
    public void updateOrderForCustomer2()throws Exception{
        String token = userLogin("44357456028", "123456");

        String orderVoJson = "{\n" +
                "  \"address\": \"string\",\n" +
                "  \"consignee\": \"string\",\n" +
                "  \"mobile\": \"string\",\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/orders/{id}/",47008)
                .header("authorization", token)
                .bodyValue(orderVoJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 买家修改本人名下订单，失败：用户未登录
     * @author 洪晓杰
     */
    @Test
    @Order(2)
    public void updateOrderForCustomer3()throws Exception{
        String token = null;

        String orderVoJson = "{\n" +
                "  \"address\": \"string\",\n" +
                "  \"consignee\": \"string\",\n" +
                "  \"mobile\": \"string\",\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/orders/{id}/",47008)
                .header("authorization", token)
                .bodyValue(orderVoJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":704}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 买家修改本人名下订单，失败：操作的资源id不存在
     * @author 洪晓杰
     */
    @Test
    @Order(3)
    public void updateOrderForCustomer4()throws Exception{
        String token = userLogin("44357456028", "123456");

        String orderVoJson = "{\n" +
                "  \"address\": \"string\",\n" +
                "  \"consignee\": \"string\",\n" +
                "  \"mobile\": \"string\",\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/orders/{id}/",47127)
                .header("authorization", token)
                .bodyValue(orderVoJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }



    /**
     * 买家标记确认收货，失败：ordersId所属customerId不一致，则无法修改
     * @author 洪晓杰
     */
    @Test
    @Order(5)
    public void updateOrderStateToConfirm()throws Exception{
        String token = userLogin("2728932539", "123456");

        byte[] responseString = mallClient.put().uri("orders/{id}/confirm",47123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }



    /**
     * 买家标记确认收货，失败：用户未登录
     * @author 洪晓杰
     */
    @Test
    @Order(6)
    public void updateOrderStateToConfirm2()throws Exception{
        String token = null;

        byte[] responseString = mallClient.put().uri("orders/{id}/confirm",47007)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":704}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 买家标记确认收货，success
     * @author 洪晓杰
     */
    @Test
    @Order(8)
    public void updateOrderStateToConfirm3()throws Exception{
        String token = userLogin("44357456028", "123456");

        byte[] responseString = mallClient.put().uri("orders/{id}/confirm",47010)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }



//     /**
//      * 买家标记确认收货，失败：操作的资源id不存在
//      * @author 洪晓杰
//      */
//     @Test
//     @Order(7)
//     public void updateOrderStateToConfirm4()throws Exception{
//         String token = userLogin("44357456028", "123456");

//         byte[] responseString = mallClient.put().uri("orders/{id}/confirm",47007)
//                 .header("authorization", token)
//                 .exchange()
//                 .expectStatus().isNotFound()
//                 .expectBody()
//                 .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
//                 .returnResult()
//                 .getResponseBodyContent();

//         String expectedResponse = "{\"errno\":504}";
//         JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
//     }



    /**
     * 买家取消，逻辑删除本人名下订单，失败：ordersId与所属customerId不一致，则无法修改
     * @author 洪晓杰
     */
    @Test
    @Order(9)
    public void updateOdersForLogicDelete2()throws Exception{
        //注意再改成登录的时候要修改userId，让其不一致
        String token = userLogin("2728932539", "123456");

        byte[] responseString = mallClient.delete().uri("/orders/{id}/",47007)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 买家取消，逻辑删除本人名下订单，失败：用户未登录
     * @author 洪晓杰
     */
    @Test
    @Order(10)
    public void updateOdersForLogicDelete3()throws Exception{
        String token = null;

        byte[] responseString = mallClient.delete().uri("/orders/{id}/",47007)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":704}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 买家取消，逻辑删除本人名下订单，失败：操作的资源id不存在
     * @author 洪晓杰
     */
    @Test
    @Order(11)
    public void updateOdersForLogicDelete4()throws Exception{
        String token = userLogin("44357456028", "123456");

        byte[] responseString = mallClient.delete().uri("/orders/{id}/",47367)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 买家取消，逻辑删除本人名下订单，成功
     * @author 洪晓杰
     */
    @Test
    @Order(12)
    public void updateOdersForLogicDelete()throws Exception{
        String token = userLogin("44357456028", "123456");

        byte[] responseString = mallClient.delete().uri("/orders/{id}/",47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
    }


    /**
     * 买家登录，获取token
     *
     * @author 洪晓杰
     * @param userName
     * @param password
     * @return token
     */
    private String userLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }



    private String login(String userName, String password) throws Exception {
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
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
        //endregion
    }

}

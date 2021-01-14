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
 * 订单公开测试
 * 测试需还原数据库数据
 * @author 余嘉炜 21620182203533
 * @date 2020/12/09 19:28
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class YujiaweiTest {

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

    private String login(String userName, String password) throws Exception{
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] ret = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    /**
     * 获取订单概要,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(1)
    public void customerGetAllSimpleOrders1() throws Exception {
        byte[] responseString = mallClient.get().uri("/orders?page=1&pageSize=5")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 获取订单概要，userid为59的全部
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(2)
    public void customerGetAllSimpleOrders2() throws Exception {
        //userid=59
        String token=this.login("39634362551", "123456");
        byte[] responseString = mallClient.get().uri("/orders?page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":13,\"pages\":3,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":456,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1174,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1175,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1423,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1424,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]}}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 获取订单概要，根据orderSn,但OrderSn不是自己的,故查不到
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(3)
    public void customerGetAllSimpleOrders3() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/orders?orderSn=2016102333120&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，根据状态查询
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(4)
    public void customerGetAllSimpleOrders5() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/orders?state=4&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":38057,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":4,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]}}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，2022年,时间超限故查不到
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(5)
    public void customerGetAllSimpleOrders6() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/orders?beginTime=2022-12-10T19:29:33&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，根据orderSn和时间，订单编号存在，但时间不对，返回空
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(6)
    public void customerGetAllSimpleOrders7() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/orders?orderSn=2016102364965&beginTime=2021-12-10T19:29:33&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，根据特定终止时间查询
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(7)
    public void customerGetAllSimpleOrders8() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/orders?endTime=2020-11-28T17:48:47&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":7,\"pages\":2,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":38055,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":24,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":38056,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":38057,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":4,\"subState\":null,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":38058,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":24,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":38059,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，根据特定起止时间查询
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(8)
    public void customerGetAllSimpleOrders9() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/orders?beginTime=2020-11-29T17:48:47&endTime=2020-11-30T17:48:47&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":38064,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":24,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 获取订单概要，查询一个被逻辑删除的订单,返回空
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(9)
    public void customerGetAllSimpleOrders10() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/orders?orderSn=2016102398984&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家修改本人名下订单,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(10)
    public void UsermodifyOrder1() throws Exception {
        byte[] responseString = mallClient.put().uri("/orders/{id}", 38058)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家修改本人名下订单,修改成功
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(11)
    public void UsermodifyOrder2() throws Exception {
        String orderJson="{\"consignee\": \"adad\",\n" +
                "  \"regionId\": 1,\n" +
                "  \"address\": \"曾厝垵\",\n" +
                "  \"mobile\": \"111\"}";
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/orders/438")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/438")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{data:{id:438,regionId:1,address:曾厝垵}}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家修改本人名下订单,订单已发货，无法修改
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(12)
    public void UsermodifyOrder3() throws Exception {
        String orderJson="{\"consignee\": \"adad\",\n" +
                "  \"regionId\": 1,\n" +
                "  \"address\": \"曾厝垵\",\n" +
                "  \"mobile\": \"111\"}";
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/orders/38058")
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
     * 买家修改本人名下订单,访问的订单id不是自己的
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(13)
    public void UsermodifyOrder4() throws Exception {
        String orderJson="{\"consignee\": \"adad\",\n" +
                "  \"regionId\": 1,\n" +
                "  \"address\": \"曾厝垵\",\n" +
                "  \"mobile\": \"111\"}";
        String token=this.login("39634362551", "123456");
        byte[] responseString = mallClient.put().uri("/orders/38058")
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
     * 买家修改本人名下订单,订单id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(14)
    public void UsermodifyOrder5() throws Exception {
        String orderJson="{\"consignee\": \"adad\",\n" +
                "  \"regionId\": 1,\n" +
                "  \"address\": \"曾厝垵\",\n" +
                "  \"mobile\": \"111\"}";
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/orders/99999")
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
     * 买家取消，逻辑删除本人名下订单,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(15)
    public void UserdeleteOrder1() throws Exception {
        byte[] responseString = mallClient.delete().uri("/orders/{id}", 38055)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家取消，逻辑删除本人名下订单,取消成功
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(16)
    public void UserdeleteOrder2() throws Exception {

        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.delete().uri("/orders/38059")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/38059")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{data:{id:38059,state:4}}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家取消，逻辑删除本人名下订单,逻辑删除成功
     * 逻辑删除之后通过买家的订单查询api就无法查到这个订单，故返回id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(17)
    public void UserdeleteOrder3() throws Exception {

        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.delete().uri("/orders/38059")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/38059")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家取消，逻辑删除本人名下订单,订单已发货，无法取消
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(18)
    public void UserdeleteOrder4() throws Exception {
        String token = this.login("79734441805", "123456");
        //String token = createTestToken(7L, 2L, 100);
        byte[] responseString = mallClient.delete().uri("/orders/{id}", 38055)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家取消，逻辑删除本人名下订单,标记的订单不是他自己的
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(19)
    public void UserdeleteOrder5() throws Exception {
        //String token = createTestToken(8L, 2L, 100);
        String token = this.login("39634362551", "123456");
        byte[] responseString = mallClient.delete().uri("/orders/{id}", 38055)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家取消，逻辑删除本人名下订单,订单id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(20)
    public void UserdeleteOrder6() throws Exception {
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.delete().uri("/orders/{id}", 99999)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家标记确认收货,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(21)
    public void UserconfirmOrder1() throws Exception {
        String token = this.login("39634362551", "123456");
        byte[] responseString = mallClient.put().uri("/orders/{id}/confirm", 38056)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家标记确认收货,收货成功
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(22)
    public void UserconfirmOrder2() throws Exception {
        String token = this.login("79734441805", "123456");
        //String token = createTestToken(7L, 2L, 100);
        byte[] responseString = mallClient.put().uri("/orders/{id}/confirm",38061 )
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/{id}",38061 )
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{data:{id:38061,state:3}}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家标记确认收货,状态不合法
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(23)
    public void UserconfirmOrder3() throws Exception {
        //String token = createTestToken(7L, 2L, 100);
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/orders/{id}/confirm", 38056)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家标记确认收货,标记的订单不是他自己的
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(24)
    public void UserconfirmOrder4() throws Exception {
        String token = this.login("39634362551", "123456");
        //String token = createTestToken(8L, 2L, 100);
        byte[] responseString = mallClient.put().uri("/orders/{id}/confirm", 38056)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家标记确认收货,标记的订单id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(25)
    public void UserconfirmOrder5() throws Exception {
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/orders/{id}/confirm", 99999)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家将团购订单转为普通订单,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(26)
    public void UserchangeOrderToNormal1() throws Exception {
        byte[] responseString = mallClient.post().uri("/orders/{id}/groupon-normal", 12)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家将团购订单转为普通订单,转换成功
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(27)
    public void UserchangeOrderToNormal2() throws Exception {
        String token = this.login("79734441805", "123456");
        //String token = createTestToken(7L, 2L, 100);
        byte[] responseString = mallClient.post().uri("/orders/{id}/groupon-normal",38060 )
                .header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/orders/{id}", 38060)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{data:{id:38060,state:2,subState:21,orderType:0}}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家将团购订单转为普通订单,状态不合法
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(28)
    public void UserchangeOrderToNormal3() throws Exception {
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.post().uri("/orders/{id}/groupon-normal", 38057)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家将团购订单转为普通订单,转换订单不是他自己的
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(29)
    public void UserchangeOrderToNormal4() throws Exception {
        String token = this.login("39634362551", "123456");
        byte[] responseString = mallClient.post().uri("/orders/{id}/groupon-normal", 38057)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家将团购订单转为普通订单,订单id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(30)
    public void UserchangeOrderToNormal5() throws Exception {
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.post().uri("/orders/{id}/groupon-normal", 99999)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

}

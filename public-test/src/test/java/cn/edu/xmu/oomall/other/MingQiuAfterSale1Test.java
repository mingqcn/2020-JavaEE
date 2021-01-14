package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/20 2:42
 **/
@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MingQiuAfterSale1Test {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private static Integer afterSaleId_9039;

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
    private String userLogin(String userName, String password){
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);

        byte[] responseString = mallClient.post().uri("/users/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(responseString), "data");
    }

    private String adminLogin(String userName, String password){
        LoginVo vo = new LoginVo();
        vo.setUserName(userName);
        vo.setPassword(password);
        String requireJson = JacksonUtil.toJson(vo);
        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret), "data");
        //endregion
    }


    @Test
    @Order(1)
    public void aftersaleTest1(){
        String token = userLogin("23636548988","123456"); // id = 6594

        Map<String, Object> aftersale = new HashMap<>();
        aftersale.put("type",0);
        aftersale.put("quantity",1);
        aftersale.put("regionId",1599);
        aftersale.put("detail","xiamen univ");
        aftersale.put("consignee","Qiu");
        aftersale.put("mobile","13959288888");
        String json = JacksonUtil.toJson(aftersale);
        byte[] response = mallClient.post().uri("/orderitems/9039/aftersales").header("authorization",token)
                .bodyValue(json).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isNumber()
                .returnResult()
                .getResponseBodyContent();

        String data = JacksonUtil.parseSubnodeToString(new String(response), "/data");
        afterSaleId_9039 = JacksonUtil.parseInteger(data, "id");

        mallClient.get().uri("/aftersales/"+afterSaleId_9039).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(afterSaleId_9039)
                .jsonPath("$.data.orderItemId").isEqualTo(9039)
                .jsonPath("$.data.skuId").isEqualTo(279)
                .jsonPath("$.data.customerId").isEqualTo(6594)
                .jsonPath("$.data.shopId").isEqualTo(1)
                .jsonPath("$.data.type").isEqualTo(0)
                .jsonPath("$.data.quantity").isEqualTo(1)
                .jsonPath("$.data.regionId").isEqualTo(1599)
                .jsonPath("$.data.detail").isEqualTo("xiamen univ")
                .jsonPath("$.data.consignee").isEqualTo("Qiu")
                .jsonPath("$.data.mobile").isEqualTo("13959288888")
                .jsonPath("$.data.state").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    public void aftersaleTest2(){
        String token = userLogin("23636548988","123456"); // id = 6594

        Map<String, Object> aftersale = new HashMap<>();
        aftersale.put("type",0);
        aftersale.put("quantity",1);
        aftersale.put("regionId",1599);
        aftersale.put("consignee","Qiu");
        aftersale.put("mobile","13959288888");
        String json = JacksonUtil.toJson(aftersale);
        mallClient.post().uri("/orderitems/9040/aftersales").header("authorization",token)
                .bodyValue(json).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(2)
    public void aftersaleTest3(){
        String token = adminLogin("13088admin","123456");

        Map<String, Object> confirm = new HashMap<>();
        confirm.put("confirm",true);
        confirm.put("price",0);
        confirm.put("type",0);
        String json = JacksonUtil.toJson(confirm);
        manageClient.put().uri("/shops/1/aftersales/"+afterSaleId_9039+"/confirm")
                .header("authorization",token).bodyValue(json).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        manageClient.get().uri("/shops/1/aftersales/"+afterSaleId_9039).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(afterSaleId_9039)
                .jsonPath("$.data.orderItemId").isEqualTo(9039)
                .jsonPath("$.data.skuId").isEqualTo(279)
                .jsonPath("$.data.customerId").isEqualTo(6594)
                .jsonPath("$.data.shopId").isEqualTo(1)
                .jsonPath("$.data.type").isEqualTo(0)
                .jsonPath("$.data.quantity").isEqualTo(1)
                .jsonPath("$.data.regionId").isEqualTo(1599)
                .jsonPath("$.data.detail").isEqualTo("xiamen univ")
                .jsonPath("$.data.consignee").isEqualTo("Qiu")
                .jsonPath("$.data.mobile").isEqualTo("13959288888")
                .jsonPath("$.data.state").isEqualTo(1)
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(3)
    public void aftersaleTest4(){
        String token = userLogin("23636548988","123456"); // id = 6594

        Map<String, Object> aftersale = new HashMap<>();
        aftersale.put("logSn","0000000");
        String json = JacksonUtil.toJson(aftersale);
        mallClient.put().uri("/aftersales/"+afterSaleId_9039+"/sendback")
                .header("authorization",token).bodyValue(json).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        mallClient.get().uri("/aftersales/"+afterSaleId_9039).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(afterSaleId_9039)
                .jsonPath("$.data.orderItemId").isEqualTo(9039)
                .jsonPath("$.data.skuId").isEqualTo(279)
                .jsonPath("$.data.customerId").isEqualTo(6594)
                .jsonPath("$.data.shopId").isEqualTo(1)
                .jsonPath("$.data.type").isEqualTo(0)
                .jsonPath("$.data.quantity").isEqualTo(1)
                .jsonPath("$.data.regionId").isEqualTo(1599)
                .jsonPath("$.data.detail").isEqualTo("xiamen univ")
                .jsonPath("$.data.consignee").isEqualTo("Qiu")
                .jsonPath("$.data.mobile").isEqualTo("13959288888")
                .jsonPath("$.data.customerLogSn").isEqualTo("0000000")
                .jsonPath("$.data.state").isEqualTo(2)
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(4)
    public void aftersaleTest5(){
        String token = adminLogin("13088admin","123456");

        Map<String, Object> aftersale = new HashMap<>();
        aftersale.put("confirm",true);
        String json = JacksonUtil.toJson(aftersale);
        manageClient.put().uri("/shops/1/aftersales/"+afterSaleId_9039+"/receive")
                .header("authorization",token).bodyValue(json).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        manageClient.get().uri("/shops/1/aftersales/"+afterSaleId_9039).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(afterSaleId_9039)
                .jsonPath("$.data.orderItemId").isEqualTo(9039)
                .jsonPath("$.data.skuId").isEqualTo(279)
                .jsonPath("$.data.customerId").isEqualTo(6594)
                .jsonPath("$.data.shopId").isEqualTo(1)
                .jsonPath("$.data.type").isEqualTo(0)
                .jsonPath("$.data.quantity").isEqualTo(1)
                .jsonPath("$.data.regionId").isEqualTo(1599)
                .jsonPath("$.data.detail").isEqualTo("xiamen univ")
                .jsonPath("$.data.consignee").isEqualTo("Qiu")
                .jsonPath("$.data.mobile").isEqualTo("13959288888")
                .jsonPath("$.data.customerLogSn").isEqualTo("0000000")
                .jsonPath("$.data.state").isEqualTo(4)
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(5)
    public void aftersaleTest6(){
        String token = adminLogin("13088admin","123456");

        manageClient.put().uri("/shops/1/aftersales/"+afterSaleId_9039+"/deliver")
                .header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] response = manageClient.get().uri("/shops/1/aftersales/"+afterSaleId_9039).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(afterSaleId_9039)
                .jsonPath("$.data.orderItemId").isEqualTo(9039)
                .jsonPath("$.data.skuId").isEqualTo(279)
                .jsonPath("$.data.customerId").isEqualTo(6594)
                .jsonPath("$.data.shopId").isEqualTo(1)
                .jsonPath("$.data.type").isEqualTo(0)
                .jsonPath("$.data.quantity").isEqualTo(1)
                .jsonPath("$.data.regionId").isEqualTo(1599)
                .jsonPath("$.data.detail").isEqualTo("xiamen univ")
                .jsonPath("$.data.consignee").isEqualTo("Qiu")
                .jsonPath("$.data.mobile").isEqualTo("13959288888")
                .jsonPath("$.data.customerLogSn").isEqualTo("0000000")
                .jsonPath("$.data.orderId").isNumber()
                .jsonPath("$.data.state").isEqualTo(5)
                .returnResult()
                .getResponseBodyContent();

        String data = JacksonUtil.parseSubnodeToString(new String(response),"/data");
        Integer orderId = JacksonUtil.parseInteger(data, "orderId");

        manageClient.get().uri("/shops/1/orders/"+orderId).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(orderId)
                .jsonPath("$.data.customer.id").isEqualTo(6594)
                .jsonPath("$.data.orderType").isEqualTo(0)
                .jsonPath("$.data.state").isEqualTo(2)
                .jsonPath("$.data.subState").isEqualTo(21)
                .jsonPath("$.data.discountPrice").isEqualTo(0)
                .jsonPath("$.data.freightPrice").isEqualTo(0)
                .jsonPath("$.data.regionId").isEqualTo(1599)
                .jsonPath("$.data.address").isEqualTo("xiamen univ")
                .jsonPath("$.data.consignee").isEqualTo("Qiu")
                .jsonPath("$.data.mobile").isEqualTo("13959288888")
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(6)
    public void aftersaleTest7(){
        String token = userLogin("23636548988","123456"); // id = 6594

        Map<String, Object> aftersale = new HashMap<>();
        String json = JacksonUtil.toJson(aftersale);
        byte[] response = mallClient.put().uri("/aftersales/"+afterSaleId_9039+"/confirm")
                .header("authorization",token).bodyValue(json).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        mallClient.get().uri("/aftersales/"+afterSaleId_9039).header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(afterSaleId_9039)
                .jsonPath("$.data.orderItemId").isEqualTo(9039)
                .jsonPath("$.data.skuId").isEqualTo(279)
                .jsonPath("$.data.customerId").isEqualTo(6594)
                .jsonPath("$.data.shopId").isEqualTo(1)
                .jsonPath("$.data.type").isEqualTo(0)
                .jsonPath("$.data.quantity").isEqualTo(1)
                .jsonPath("$.data.regionId").isEqualTo(1599)
                .jsonPath("$.data.detail").isEqualTo("xiamen univ")
                .jsonPath("$.data.consignee").isEqualTo("Qiu")
                .jsonPath("$.data.mobile").isEqualTo("13959288888")
                .jsonPath("$.data.customerLogSn").isEqualTo("0000000")
                .jsonPath("$.data.state").isEqualTo(8)
                .returnResult()
                .getResponseBodyContent();
    }
}

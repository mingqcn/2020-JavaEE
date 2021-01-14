package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

/**
 * @Author: Yifei Wang 24320182203286
 * @Date: 2020/12/15 17:53
 */

// 全部通过但怀疑数据库冲突 By 宋润涵

@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient(timeout = "10000")
public class WangYiFeiTest {

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



    String getPath(String a){
        return a;
    }

    @Test
    @Order(1)
    public void getSkuStates() throws Exception{
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.get().uri(getPath("/skus/states"));
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").isArray()
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(2)
    public void getAllSkus(){
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.get().uri(getPath("/skus"));
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult()
                .getResponseBodyContent();
    }

    //通过id获取sku
    @Test
    @Order(3)
    public void getSkuById(){
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.get().uri(getPath("/skus/300"));
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data.name").isEqualTo("+")
                .returnResult()
                .getResponseBodyContent();
    }


    //添加sku到spu 未登录
    @Test
    @Order(5)
    public void addSkuToSpu2() throws Exception{
        byte[] responseBuffer = null;
        String bodyValue = "{\n" +
                "  \"sn\": \"string\",\n" +
                "  \"name\": \"测试商品\",\n" +
                "  \"originalPrice\": 100,\n" +
                "  \"configuration\": \"ddddd\",\n" +
                "  \"weight\": 10,\n" +
                "  \"imageUrl\": null,\n" +
                "  \"inventory\": 100,\n" +
                "  \"detail\": \"aaaaa\"\n" +
                "}";
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri(getPath("/shops/1/spus/300/skus")).bodyValue(bodyValue);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
    }

    //添加sku到spu 登录 但是操作的spu不存在
    @Test
    @Order(6)
    public void addSkuToSpu3() throws Exception{
        byte[] responseBuffer = null;
        String token = login("13088admin", "123456");
        String bodyValue = "{\n" +
                "  \"sn\": \"string\",\n" +
                "  \"name\": \"测试商品\",\n" +
                "  \"originalPrice\": 100,\n" +
                "  \"configuration\": \"ddddd\",\n" +
                "  \"weight\": 10,\n" +
                "  \"imageUrl\": null,\n" +
                "  \"inventory\": 100,\n" +
                "  \"detail\": \"aaaaa\"\n" +
                "}";
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri(getPath("/shops/1/spus/500000/skus")).bodyValue(bodyValue).header("authorization",token);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
    }


    //正常删除 快乐路径
    @Test
    @Order(888)
    public void deleteSku() throws  Exception{
        byte[] responseBuffer = null;
        String token = login("13088admin", "123456");
        WebTestClient.RequestHeadersSpec res = manageClient.delete().uri(getPath("/shops/1/skus/8989")).header("authorization", token);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();

        WebTestClient.RequestHeadersSpec res2 = mallClient.get().uri(getPath("/skus/8989"));
        responseBuffer = res2.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    //删除的sku不属于自己的商铺
    @Test
    @Order(7)
    public void deleteSku2() throws Exception{
        byte[] responseBuffer = null;
        String token = login("13088admin", "123456");
        WebTestClient.RequestHeadersSpec res = manageClient.delete().uri(getPath("/shops/2/skus/300")).header("authorization", token);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    //新建浮动价格
    @Test
    @Order(10)
    public void newFloatPrice() throws Exception{
        byte[] responseBuffer = null;
        String token = login("13088admin", "123456");
        String bodyValue = "{\n" +
                "  \"activityPrice\": 12,\n" +
                "  \"beginTime\": \"2022-12-15T22:55:00\",\n" +
                "  \"endTime\": \"2022-12-20T22:55:00\",\n" +
                "  \"quantity\": 10\n" +
                "}";
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri(getPath("/shops/1/skus/8989/floatPrices")).bodyValue(bodyValue).header("authorization", token);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.activityPrice").isEqualTo(12)
                .jsonPath("$.data.quantity").isEqualTo(10)
                .returnResult()
                .getResponseBodyContent();

        WebTestClient.RequestHeadersSpec res2 = manageClient.post().uri(getPath("/shops/1/skus/8989/floatPrices")).bodyValue(bodyValue).header("authorization", token);
        responseBuffer = res2.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SKUPRICE_CONFLICT.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    //新建浮动价格 操作的sku不是自己的店铺的
    @Test
    @Order(9)
    public void newFloatPrice2() throws Exception{
        byte[] responseBuffer = null;
        String token = login("13088admin", "123456");
        String bodyValue = "{\n" +
                "  \"activityPrice\": 12,\n" +
                "  \"beginTime\": \"2022-12-15T22:55:00\",\n" +
                "  \"endTime\": \"2022-12-20T22:55:00\",\n" +
                "  \"quantity\": 10\n" +
                "}";
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri(getPath("/shops/2/skus/8989/floatPrices")).bodyValue(bodyValue).header("authorization", token);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    //正常删除
    @Test
    @Order(12)
    public void deleteFloatPrice1() throws Exception{
        byte[] responseBuffer = null;
        String token = login("13088admin", "123456");
        WebTestClient.RequestHeadersSpec res = manageClient.delete().uri(getPath("/shops/1/floatPrices/9000")).header("authorization", token);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        WebTestClient.RequestHeadersSpec res2 = manageClient.delete().uri(getPath("/shops/1/floatPrices/9000")).header("authorization", token);
        responseBuffer = res2.exchange().expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    //删除的floatprice 不属于自己的商店
    @Test
    @Order(11)
    public void deleteFloatPrice2() throws Exception{
        byte[] responseBuffer = null;
        String token = login("13088admin", "123456");
        WebTestClient.RequestHeadersSpec res = manageClient.delete().uri(getPath("/shops/2/floatPrices/9001")).header("authorization", token);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
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
    }
}

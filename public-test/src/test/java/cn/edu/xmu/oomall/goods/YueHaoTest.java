package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;


/**
 * 测试用例非常认真，虽然和张悦冲突，建议按30个算
 * @author 岳皓
 * 学号：24320182203319
 * created at 2020/12/3
 * modified at 2020/12/14
 *
 */
@SpringBootTest(classes = PublicTestApp.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class YueHaoTest {
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

    /** 4
     * 无需登录
     * 查询sku状态
     **/
    @Test
    @Order(4)
    public void getSkuStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus/states")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未上架\",\"code\":0},{\"name\":\"上架\",\"code\":4},{\"name\":\"已删除\",\"code\":6}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 5
     * 无需登录
     * 查询所有sku-不加任何条件
     **/
    @Test
    @Order(5)
    public void getSkus1() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?page=1&pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();

        String expectedResponse ="{\"errno\":0,\"data\":{\"pageSize\":2,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /** 7
     * 无需登录
     * 查询所有sku-shopId不存在
     **/
    @Test
    @Order(7)
    public void getSkus5() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?shopId=100&page=1&pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"list\":[]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 8
     * 无需登录
     * 查询所有sku-pageSize=1
     **/
    @Test
    @Order(8)
    public void getSkus6() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?pageSize=1&page=1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":1,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 9
     * 无需登录
     * 查询所有sku-pageSize=5
     **/
    @Test
    @Order(9)
    public void getSkus7() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":5,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disable\":false,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disable\":false,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disable\":false,\"price\":16200}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }
    /** 10
     * 无需登录
     * 查询所有sku-page=2
     **/
    @Test
    @Order(10)
    public void getSkus8() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?page=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse =  "{\"errno\":0,\"data\":{\"pageSize\":10,\"page\":2,\"list\":[{\"id\":283,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":780000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57faed5b5da32.jpg\",\"inventory\":1,\"disable\":false,\"price\":780000},{\"id\":284,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620c598a78f.jpg\",\"inventory\":1,\"disable\":false,\"price\":880000},{\"id\":285,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1880000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620cca773f3.jpg\",\"inventory\":1,\"disable\":false,\"price\":1880000},{\"id\":286,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1950000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58620dd2a854e.jpg\",\"inventory\":1,\"disable\":false,\"price\":1950000},{\"id\":287,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2600000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621005f229a.jpg\",\"inventory\":1,\"disable\":false,\"price\":2600000},{\"id\":288,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":550000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586211ad843f6.jpg\",\"inventory\":1,\"disable\":false,\"price\":550000},{\"id\":289,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":480000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586213438566e.jpg\",\"inventory\":1,\"disable\":false,\"price\":480000},{\"id\":290,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":180000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214e020ab2.jpg\",\"inventory\":1,\"disable\":false,\"price\":180000},{\"id\":291,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":130000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\"inventory\":1,\"disable\":false,\"price\":130000},{\"id\":292,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":200000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_58621bd1768b4.jpg\",\"inventory\":1,\"disable\":false,\"price\":200000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 11
     * 无需登录
     * 查询所有sku-page=9
     **/
    @Test
    @Order(11)
    public void getSkus9() throws Exception {
        byte[]responseString=mallClient.get().uri("/skus?pageSize=9")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.pages").isNumber()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"pageSize\":9,\"page\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disable\":false,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disable\":false,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disable\":false,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disable\":false,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disable\":false,\"price\":16200},{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disable\":false,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disable\":false,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disable\":false,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disable\":false,\"price\":1380000}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 12
     * 无需登录
     * 查询评论的所有状态
     **/
    @Test
    @Order(12)
    public void getCommentStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/comments/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未审核\",\"code\":0},{\"name\":\"评论成功\",\"code\":1},{\"name\":\"未通过\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 13
     * 无需登录
     * 查询优惠券的所有状态
     **/
    @Test
    @Order(13)
    public void getCouponStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/coupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未领取\",\"code\":0},{\"name\":\"已领取\",\"code\":1},{\"name\":\"已使用\",\"code\":2},{\"name\":\"已失效\",\"code\":3}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 14
     * 无需登录
     * 查询预售活动的所有状态
     **/
    @Test
    @Order(14)
    public void getPreSaleStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/presales/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"已下线\",\"code\":0},{\"name\":\"已上线\",\"code\":1},{\"name\":\"已删除\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 15
     * 无需登录
     * 查询团购活动的所有状态
     **/
    @Test
    @Order(15)
    public void getGrouponStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/groupons/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"已下线\",\"code\":0},{\"name\":\"已上线\",\"code\":1},{\"name\":\"已删除\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 16
     * 无需登录
     * 查询店铺的所有状态
     **/
    @Test
    @Order(16)
    public void getShopStates() throws Exception {
        byte[]responseString=mallClient.get().uri("/shops/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"未审核\",\n" +
                "      \"code\": 0\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"未上线\",\n" +
                "      \"code\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"上线\",\n" +
                "      \"code\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"关闭\",\n" +
                "      \"code\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"审核未通过\",\n" +
                "      \"code\": 4\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        System.out.println(new String(responseString, "UTF-8"));
        // JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /** 30
     * 需管理员登录
     * 删除商品sku-skuid存在
     **/
    @Test
    @Order(30)
    public void deleteSkuTest() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/skus/12936")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
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

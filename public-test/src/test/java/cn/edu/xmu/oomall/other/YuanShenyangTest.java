package cn.edu.xmu.oomall.other;

import cn.edu.xmu.ooad.PublicTestApp;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import cn.edu.xmu.ooad.util.ResponseCode;

import java.nio.charset.StandardCharsets;

/**
 * 其他模块-足迹服务、商品收藏服务、购物车服务 公开测试用例
 *
 * @author  24320182203318 yang8miao
 * @date 2020/12/09 15:15
 */
@SpringBootTest(classes = PublicTestApp.class)
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class YuanShenyangTest {

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
     * 买家登录，获取token
     *
     * @author yang8miao
     * @param userName
     * @param password
     * @return token
     * createdBy yang8miao 2020/11/26 21:34
     * modifiedBy yang8miao 2020/11/26 21:34
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
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    /**
     * 管理员登录，获取token
     *
     * @author yang8miao
     * @param userName
     * @param password
     * @return token
     * createdBy yang8miao 2020/12/12 19:48
     * modifiedBy yang8miao 2020/12/12 19:48
     */
    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    /**
     * 足迹服务-管理员查看浏览记录  普通测试1，查询成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(0)
    public void getFootprints1() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=220&page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1212599,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 291,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 130000,\n" +
                "          \"price\": 130000,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 足迹服务-管理员查看浏览记录  普通测试2，查询成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(0)
    public void getFootprints2() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=134&page=1&pageSize=1").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1212513,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 537,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201711/file_5a10e5d95d038.jpg\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 699,\n" +
                "          \"price\": 699,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 足迹服务-管理员查看浏览记录 普通测试，查询成功，但未查到任何足迹
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(0)
    public void getFootprints3() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=17320&endTime=2019-11-11 12:00:00&page=10&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 足迹服务-管理员查看浏览记录 普通测试，查询成功，但未查到任何足迹
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(0)
    public void getFootprints4() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=17320&beginTime=2022-11-24 12:00:00&page=10&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 足迹服务-管理员查看浏览记录 普通测试，开始时间大于结束时间,返回错误码
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(0)
    public void getFootprints5() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?beginTime=2022-11-24 12:00:00&endTime=2020-11-11 12:00:00")
                .header("authorization",token).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .returnResult()
                .getResponseBodyContent();
        
    }

    /**
     * 足迹服务-管理员查看浏览记录 普通测试，开始时间大于结束时间,返回错误码
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 15:28
     */
    @Test
    @Order(0)
    public void getFootprints6() throws Exception {

        String token = this.adminLogin("13088admin", "123456");

        byte[] responseString = manageClient.get().uri("/shops/0/footprints?userId=233&beginTime=2022-11-23 12:00:00&endTime=2020-11-11 12:00:00")
                .header("authorization",token).exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .returnResult()
                .getResponseBodyContent();
        
    }

    /**
     * 商品收藏服务-买家查看所有收藏的商品  普通测试1，查询成功 page=1&pageSize=1
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getFavorites1() throws Exception {

        // userId = 20
        String token = this.userLogin("10101113105", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=1").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3735458,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 428,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_5967a3fed2cf4.jpg\",\n" +
                "          \"inventory\": 9972,\n" +
                "          \"originalPrice\": 299,\n" +
                "          \"price\": 299,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家查看所有收藏的商品  普通测试2，查询成功 page=1&pageSize=5
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getFavorites2() throws Exception {

        // userId = 20
        String token = this.userLogin("10101113105", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3735458,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 428,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_5967a3fed2cf4.jpg\",\n" +
                "          \"inventory\": 9972,\n" +
                "          \"originalPrice\": 299,\n" +
                "          \"price\": 299,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3768225,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 420,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_5967792535e80.jpg\",\n" +
                "          \"inventory\": 150,\n" +
                "          \"originalPrice\": 118000,\n" +
                "          \"price\": 118000,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3800992,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 347,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58405c09ea4f3.jpg\",\n" +
                "          \"inventory\": 100,\n" +
                "          \"originalPrice\": 4028,\n" +
                "          \"price\": 4028,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家查看所有收藏的商品  普通测试3，查询成功 page=1&pageSize=2
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getFavorites3() throws Exception {

        // userId = 11026
        String token = this.userLogin("30674268147", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=2").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746375,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 573,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3acd8f6d384.png\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 480,\n" +
                "          \"price\": 480,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3779142,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 460,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59707b5f08a0e.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 15000,\n" +
                "          \"price\": 15000,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 商品收藏服务-买家查看所有收藏的商品  普通测试4，查询成功 page=1&pageSize=10
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getFavorites4() throws Exception {

        // userId = 11026
        String token = this.userLogin("30674268147", "123456");

        byte[] responseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746375,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 573,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b3acd8f6d384.png\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 480,\n" +
                "          \"price\": 480,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3779142,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 460,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59707b5f08a0e.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 15000,\n" +
                "          \"price\": 15000,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3811909,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 322,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586652b49d1a7.jpg\",\n" +
                "          \"inventory\": 100,\n" +
                "          \"originalPrice\": 2070,\n" +
                "          \"price\": 2070,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 商品收藏服务-买家收藏商品  普通测试1，收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void postFavoritesGoodsSpuId1() throws Exception {

        // userId = 11027
        String token = this.userLogin("73559977368", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/371").header("authorization",token).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "      \"goodsSku\": {\n" +
                "        \"id\": 371,\n" +
                "        \"name\": \"+\",\n" +
                "        \"skuSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58406071f1fc7.jpg\",\n" +
                "        \"inventory\": 1,\n" +
                "        \"originalPrice\": 650000,\n" +
                "        \"price\": 650000,\n" +
                "        \"disable\":  false\n" +
                "        }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3779143,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 546,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201711/file_5a11852d4524d.jpg\",\n" +
                "          \"inventory\": 1000,\n" +
                "          \"originalPrice\": 999,\n" +
                "          \"price\": 999,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3746376,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 476,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_59708e3d5e3b3.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 18000,\n" +
                "          \"price\": 18000,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3811910,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 351,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58405caec994c.jpg\",\n" +
                "          \"inventory\": 100,\n" +
                "          \"originalPrice\": 2688,\n" +
                "          \"price\": 2688,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "      \"goodsSku\": {\n" +
                "        \"id\": 371,\n" +
                "        \"name\": \"+\",\n" +
                "        \"skuSn\": null,\n" +
                "        \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58406071f1fc7.jpg\",\n" +
                "        \"inventory\": 1,\n" +
                "        \"originalPrice\": 650000,\n" +
                "        \"price\": 650000,\n" +
                "        \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家收藏商品  普通测试2，收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void postFavoritesGoodsSpuId2() throws Exception {

        // userId = 11028
        String token = this.userLogin("21571184342", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/277").header("authorization",token).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 277,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\n" +
                "          \"inventory\": 10,\n" +
                "          \"originalPrice\": 16200,\n" +
                "          \"price\": 16200,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);


        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3811911,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 519,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201711/file_5a00545f9a1e5.png\",\n" +
                "          \"inventory\": 10000,\n" +
                "          \"originalPrice\": 299,\n" +
                "          \"price\": 299,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"id\": 3779144,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 668,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201903/file_5c789a75c34c7.jpg\",\n" +
                "          \"inventory\": 30,\n" +
                "          \"originalPrice\": 1380,\n" +
                "          \"price\": 1380,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"id\": 3746377,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 385,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586664347dae0.jpg\",\n" +
                "          \"inventory\": 996,\n" +
                "          \"originalPrice\": 299,\n" +
                "          \"price\": 299,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 277,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\n" +
                "          \"inventory\": 10,\n" +
                "          \"originalPrice\": 16200,\n" +
                "          \"price\": 16200,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家收藏商品  普通测试3，收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void postFavoritesGoodsSpuId3() throws Exception {

        // userId = 11029
        String token = this.userLogin("47812733843", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/447").header("authorization",token).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 447,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201808/file_5b74f0e3a396d.jpg\",\n" +
                "          \"inventory\": 93,\n" +
                "          \"originalPrice\": 669,\n" +
                "          \"price\": 669,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746378,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 631,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b56d79642c94.jpg\",\n" +
                "          \"inventory\": 998,\n" +
                "          \"originalPrice\": 2280,\n" +
                "          \"price\": 2280,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"id\": 3811912,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 458,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_597028d78c0a3.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 30000,\n" +
                "          \"price\": 30000,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"id\": 3779145,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 620,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b5925a20bbb7.jpg\",\n" +
                "          \"inventory\": 997,\n" +
                "          \"originalPrice\": 1980,\n" +
                "          \"price\": 1980,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 447,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201808/file_5b74f0e3a396d.jpg\",\n" +
                "          \"inventory\": 93,\n" +
                "          \"originalPrice\": 669,\n" +
                "          \"price\": 669,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家收藏商品  普通测试5，收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void postFavoritesGoodsSpuId5() throws Exception {

        // userId = 11031
        String token = this.userLogin("41410343666", "123456");

        byte[] responseString = mallClient.post().uri("/favorites/goods/447").header("authorization",token).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 447,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201808/file_5b74f0e3a396d.jpg\",\n" +
                "          \"inventory\": 93,\n" +
                "          \"originalPrice\": 669,\n" +
                "          \"price\": 669,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3811914,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 646,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201807/file_5b57defeb136d.png\",\n" +
                "          \"inventory\": 971,\n" +
                "          \"originalPrice\": 498,\n" +
                "          \"price\": 498,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"id\": 3779147,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 366,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58405f8517095.jpg\",\n" +
                "          \"inventory\": 10,\n" +
                "          \"originalPrice\": 3600,\n" +
                "          \"price\": 3600,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"id\": 3746380,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 383,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_5840622c2337f.jpg\",\n" +
                "          \"inventory\": 1,\n" +
                "          \"originalPrice\": 42000,\n" +
                "          \"price\": 42000,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 447,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201808/file_5b74f0e3a396d.jpg\",\n" +
                "          \"inventory\": 93,\n" +
                "          \"originalPrice\": 669,\n" +
                "          \"price\": 669,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试1，连续删除3件商品，删除收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteFavoritesId1() throws Exception {

        // userId = 11032
        String token = this.userLogin("58084752837", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3746381").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        responseString = mallClient.delete().uri("/favorites/3811915").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        responseString = mallClient.delete().uri("/favorites/3779148").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);

    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试2，删除收藏成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteFavoritesId2() throws Exception {

        // userId = 11033
        String token = this.userLogin("30917238566", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3779149").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自身收藏商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/favorites?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 3746382,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 362,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_58405eb2c2268.jpg\",\n" +
                "          \"inventory\": 10,\n" +
                "          \"originalPrice\": 17800,\n" +
                "          \"price\": 17800,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      },\n" +
                "      {\n" +
                "        \"id\": 3811916,\n" +
                "        \"goodsSku\": {\n" +
                "          \"id\": 431,\n" +
                "          \"name\": \"+\",\n" +
                "          \"skuSn\": null,\n" +
                "          \"imageUrl\": \"http://47.52.88.176/file/images/201707/file_5967a77908428.jpg\",\n" +
                "          \"inventory\": 9831,\n" +
                "          \"originalPrice\": 299,\n" +
                "          \"price\": 299,\n" +
                "          \"disable\":  false\n" +
                "        }\n" +
                        "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);

    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试3，删除收藏失败，该用户未收藏该商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteFavoritesId3() throws Exception {

        // userId = 14712
        String token = this.userLogin("45209106845", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3782810").header("authorization",token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试4，删除收藏失败，该收藏id不存在
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteFavoritesId4() throws Exception {

        // userId = 14712
        String token = this.userLogin("45209106845", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/37").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试5，删除收藏失败，该收藏id不存在
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteFavoritesId5() throws Exception {

        // userId = 14712
        String token = this.userLogin("45209106845", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3227").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试6，删除收藏失败，该收藏id不存在
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteFavoritesId6() throws Exception {

        // userId = 14712
        String token = this.userLogin("45209106845", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/233").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 商品收藏服务-买家删除某个收藏的商品  普通测试7，删除收藏失败，该用户未收藏该商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteFavoritesId7() throws Exception {

        // userId = 14712
        String token = this.userLogin("45209106845", "123456");

        byte[] responseString = mallClient.delete().uri("/favorites/3782808").header("authorization",token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家获得购物车列表  普通测试1，查询成功 page=1&pageSize=5
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getCarts1() throws Exception {

        // userId = 20
        String token = this.userLogin("10101113105", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=5").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1001,\n" +
                "        \"goodsSkuId\": 393,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1002,\n" +
                "        \"goodsSkuId\": 658,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1003,\n" +
                "        \"goodsSkuId\": 377,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家获得购物车列表  普通测试2，查询成功 page=1&pageSize=3
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getCarts2() throws Exception {

        // userId = 400
        String token = this.userLogin("35642539836", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=3").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1004,\n" +
                "        \"goodsSkuId\": 446,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家获得购物车列表  普通测试3，查询成功，购物车中无商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getCarts3() throws Exception {

        // userId = 4000
        String token = this.userLogin("28883882732", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=20").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家获得购物车列表  普通测试4，查询成功，购物车中无商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getCarts4() throws Exception {

        // userId = 9782
        String token = this.userLogin("7912044979", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=2").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家获得购物车列表  普通测试5，查询成功，购物车中无商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getCarts5() throws Exception {

        // userId = 9781
        String token = this.userLogin("29970839554", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=4&pageSize=222").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家获得购物车列表  普通测试6，查询成功，购物车中无商品
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void getCarts6() throws Exception {

        // userId = 9782
        String token = this.userLogin("7912044979", "123456");

        byte[] responseString = mallClient.get().uri("/carts?page=1&pageSize=2223").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家将商品加入购物车  普通测试1，加入成功并查询
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void postCarts1() throws Exception {

        // userId = 99
        String token = this.userLogin("16436076738", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 300);
        body.put("quantity", 1111);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.post().uri("/carts").header("authorization",token).bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"goodsSkuId\": 300,\n" +
                "    \"skuName\": \"+\",\n" +
                "    \"quantity\": 1111,\n" +
                "    \"price\": 68000,\n" +
                "    \"couponActivity\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"goodsSkuId\": 300,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 1111,\n" +
                "        \"price\": 68000,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家将商品加入购物车  普通测试2，加入成功并查询
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void postCarts2() throws Exception {

        // userId = 999
        String token = this.userLogin("59506839941", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 300);
        body.put("quantity", 111);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.post().uri("/carts").header("authorization",token).bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"goodsSkuId\": 300,\n" +
                "    \"skuName\": \"+\",\n" +
                "    \"quantity\": 111,\n" +
                "    \"price\": 68000,\n" +
                "    \"couponActivity\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"goodsSkuId\": 300,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 111,\n" +
                "        \"price\": 68000,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家清空购物车  普通测试1，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCarts1() throws Exception {

        // userId = 1
        String token = this.userLogin("8606245097", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家清空购物车  普通测试2，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCarts2() throws Exception {

        // userId = 2
        String token = this.userLogin("36040122840", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家清空购物车  普通测试3，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCarts3() throws Exception {

        // userId = 3
        String token = this.userLogin("7306155755", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家清空购物车  普通测试4，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCarts4() throws Exception {

        // userId = 4
        String token = this.userLogin("14455881448", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家清空购物车  普通测试5，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCarts5() throws Exception {

        // userId = 5
        String token = this.userLogin("8906373389", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家清空购物车  普通测试6，清空购物车成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCarts6() throws Exception {

        // userId = 6
        String token = this.userLogin("39118189028", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=2&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家清空购物车  普通测试6，清空购物车成功,查询时使用默认的page和pageSize
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCarts7() throws Exception {

        // userId = 7
        String token = this.userLogin("63088258694", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家清空购物车  普通测试6，清空购物车成功,查询时使用默认的page和pageSize
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCarts8() throws Exception {

        // userId = 8
        String token = this.userLogin("46613241589", "123456");

        byte[] responseString = mallClient.delete().uri("/carts").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试1，修改成功并查询，此时修改数量
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void putCartsId1() throws Exception {

        // userId = 10000
        String token = this.userLogin("39288437216", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 367);
        body.put("quantity", 111);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1041").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1041,\n" +
                "        \"goodsSkuId\": 367,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 111,\n" +
                "        \"price\": 24120,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1042,\n" +
                "        \"goodsSkuId\": 658,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1043,\n" +
                "        \"goodsSkuId\": 377,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试2，修改成功并查询，此时修改数量
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void putCartsId2() throws Exception {

        // userId = 10001
        String token = this.userLogin("41372695510", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 446);
        body.put("quantity", 101);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1044").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"total\": 2,\n" +
                "    \"pages\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1044,\n" +
                "        \"goodsSkuId\": 446,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 101,\n" +
                "        \"price\": 1799,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 1045,\n" +
                "        \"goodsSkuId\": 643,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }



    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试3,要修改的skuId与原先不是同一个spuId，字段不合法
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void putCartsId3() throws Exception {

        // userId = 10002
        String token = this.userLogin("32485307410", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 522);
        body.put("quantity", 100);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1046").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试4,要修改的skuId与原先不是同一个spuId，字段不合法
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void putCartsId4() throws Exception {

        // userId = 10002
        String token = this.userLogin("32485307410", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 322);
        body.put("quantity", 101);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1047").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家修改购物车单个商品的数量或规格  普通测试5，修改成功并查询，此时修改规格，要修改的skuId未加入购物车
     *
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void putCartsId5() throws Exception {

        // userId = 10003
        String token = this.userLogin("44866608870", "123456");

        JSONObject body = new JSONObject();
        body.put("goodsSkuId", 6810);
        body.put("quantity", 150);
        String requireJson = body.toJSONString();

        byte[] responseString = mallClient.put().uri("/carts/1048").header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        

        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"goodsSkuId\": 6810,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 150,\n" +
                "        \"price\": 6697,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 购物车服务-买家删除购物车中商品  普通测试1，删除成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCartsId1() throws Exception {

        // userId = 1000
        String token = this.userLogin("97142877706", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/1061").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家删除购物车中商品  普通测试2，删除成功
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCartsId2() throws Exception {

        // userId = 1001
        String token = this.userLogin("10153144607", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/1062").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        // 买家查询自己购物车中商品，进行验证
        byte[] queryResponseString = mallClient.get().uri("/carts?page=1&pageSize=10").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        
        String queryExpectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 1063,\n" +
                "        \"goodsSkuId\": 377,\n" +
                "        \"skuName\": \"+\",\n" +
                "        \"quantity\": 100,\n" +
                "        \"price\": 2,\n" +
                "        \"couponActivity\": [\n" +
                "        ]\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(queryExpectedResponse, new String(queryResponseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 购物车服务-买家删除购物车中商品  普通测试,该购物车id不存在,删除失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCartsId3() throws Exception {

        // userId = 10
        String token = this.userLogin("19769355952", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/45").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家删除购物车中商品  普通测试,该购物车id所属买家与操作用户不一致,删除失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCartsId4() throws Exception {

        // userId = 11
        String token = this.userLogin("14902184265", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/1080").header("authorization",token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 购物车服务-买家删除购物车中商品  普通测试,该购物车id所属买家与操作用户不一致,删除失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCartsId5() throws Exception {

        // userId = 12
        String token = this.userLogin("5217325133", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/1080").header("authorization",token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家删除购物车中商品  普通测试,该购物车id不存在,删除失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCartsId6() throws Exception {

        // userId = 10
        String token = this.userLogin("19769355952", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/523").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 购物车服务-买家删除购物车中商品  普通测试,该购物车id不存在,删除失败
     * @throws Exception
     * @author yang8miao
     * @date Created in 2020/12/9 16:18
     */
    @Test
    @Order(0)
    public void deleteCartsId7() throws Exception {

        // userId = 10
        String token = this.userLogin("19769355952", "123456");

        byte[] responseString = mallClient.delete().uri("/carts/544").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
}

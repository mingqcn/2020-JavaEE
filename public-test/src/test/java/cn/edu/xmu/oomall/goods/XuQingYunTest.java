package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * description: XuQingYunTest_SKU
 * date: 2020/12/15 20:47
 * author: 24320182203306 徐清韵
 * version: 1.0
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class XuQingYunTest {
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

    private String adminLogin(String userName, String password) throws Exception{

        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();
        byte[] responseString = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        return JSONObject.parseObject(new String(responseString, StandardCharsets.UTF_8)).getString("data");
    }

    private final String creatTestToken(Long userId, Long departId, int expireTime) {
        String token = new JwtHelper().createToken(userId, departId, expireTime);
        return token;
    }

    @Test
    @Order(00)
    void getSkuList1() throws Exception{
        byte[] response =mallClient.get().uri("/skus?spuId=273&spuSn=drh-d0001&page=1&pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.list[?(@.id==273)].name").isEqualTo("+")
                .jsonPath("$.data.page").isEqualTo(1)
                .jsonPath("$.data.pageSize").isEqualTo(5)
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disabled\":4,\"price\":980000}],\"pageNum\":1,\"pageSize\":1,\"size\":1,\"startRow\":0,\"endRow\":0,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";
    }

    @Test
    @Order(01)
    void getSkuList2() throws Exception{
        byte[] response =mallClient.get().uri("/skus?page=1&pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.list[?(@.id==273)].name").isEqualTo("+")
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":5,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disabled\":4,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disabled\":4,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disabled\":4,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disabled\":4,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disabled\":4,\"price\":16200}],\"pageNum\":1,\"pageSize\":5,\"size\":5,\"startRow\":0,\"endRow\":4,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";

    }

    @Test
    @Order(02)
    void getSkuList3() throws Exception{
        byte[] response =mallClient.get().uri("/skus?page=2&pageSize=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.list[?(@.id==278)].name").isEqualTo("+")
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":5,\"list\":[{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disabled\":4,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disabled\":4,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disabled\":4,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disabled\":4,\"price\":1380000},{\"id\":282,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"disabled\":4,\"price\":120000}],\"pageNum\":1,\"pageSize\":5,\"size\":5,\"startRow\":0,\"endRow\":4,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}";

    }

    @Test
    @Order(03)
    void getSkuList4() throws Exception{
        byte[] response =mallClient.get().uri("/skus")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.list").isArray()
                .jsonPath("$.data.total").isNumber()
                .jsonPath("$.data.list[?(@.id==273)].name").isEqualTo("+")
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":10,\"list\":[{\"id\":273,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":980000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"disabled\":4,\"price\":980000},{\"id\":274,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":850,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861cd259e57a.jpg\",\"inventory\":99,\"disabled\":4,\"price\":850},{\"id\":275,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":4028,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861d65fa056a.jpg\",\"inventory\":10,\"disabled\":4,\"price\":4028},{\"id\":276,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":6225,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861da5e7ec6a.jpg\",\"inventory\":10,\"disabled\":4,\"price\":6225},{\"id\":277,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":16200,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_5861c5848ffc4.jpg\",\"inventory\":10,\"disabled\":4,\"price\":16200},{\"id\":278,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfb485e1df.jpg\",\"inventory\":46100,\"disabled\":4,\"price\":1199},{\"id\":279,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1199,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_580cfc4323959.jpg\",\"inventory\":500,\"disabled\":4,\"price\":1199},{\"id\":280,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":2399,\"imageUrl\":\"http://47.52.88.176/file/images/201611/file_583af4aec812c.jpg\",\"inventory\":1834,\"disabled\":4,\"price\":2399},{\"id\":281,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":1380000,\"imageUrl\":\"http://47.52.88.176/file/images/201610/file_57fae8f7240c6.jpg\",\"inventory\":1,\"disabled\":4,\"price\":1380000},{\"id\":282,\"skuSn\":null,\"name\":\"+\",\"originalPrice\":120000,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586214158db43.jpg\",\"inventory\":1,\"disabled\":4,\"price\":120000}],\"pageNum\":1,\"pageSize\":10,\"size\":10,\"startRow\":0,\"endRow\":9,\"pages\":1,\"prePage\":0,\"nextPage\":0,\"isFirstPage\":true,\"isLastPage\":true,\"hasPreviousPage\":false,\"hasNextPage\":false,\"navigatePages\":8,\"navigatepageNums\":[1],\"navigateFirstPage\":1,\"navigateLastPage\":1},\"errmsg\":\"成功\"}}";
    }

    @Test
    @Order(04)
    void getSku1() throws Exception{
        String token = this.userLogin("8606245097", "123456");

        byte[] response =mallClient.get().uri("/skus/273")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.id").isEqualTo(273)
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(05)
    void getSku2() throws Exception{
        String token = this.userLogin("8606245097", "123456");

        byte[] response =mallClient.get().uri("/skus/1")
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(06)
    void getgoodskustate() throws Exception
    {
        byte[] response  = mallClient.get().uri("/skus/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(3)
                .jsonPath("$.data[0].code").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"name\":\"未上架\",\"code\":0},{\"name\":\"上架\",\"code\":4},{\"name\":\"已删除\",\"code\":6}],\"errmsg\":\"成功\"}";

    }

    @Test
    @Order(07)
    void getShareSku1() throws Exception
    {
        String token =this.userLogin("17857289610", "123456");
        byte[] response  = mallClient.get().uri("/share/442315/skus/300")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"code\":\"OK\",\"errmsg\":\"成功\",\"data\":{\"id\":273,\"name\":\"+\",\"skuSn\":null,\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586206d4c7d2f.jpg\",\"inventory\":1,\"originalPrice\":980000,\"price\":null,\"disabled\":4}}";

    }

    @Test
    @Order(8)
    void getShareSku2() throws Exception
    {
//        String token =this.userLogin("17857289610", "123456");
//        byte[] response  = mallClient.get().uri("/share/442316/skus/300")
//                .header("authorization",token)
//                .exchange()
//                .expectStatus().is4xxClientError()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
//                .returnResult()
//                .getResponseBodyContent();

    }

    @Test
    @Order(9)
    void getShareSku3() throws Exception
    {
//        String token =this.userLogin("17857289610", "123456");
//        byte[] response  = mallClient.get().uri("/share/442315/skus/274")
//                .header("authorization",token)
//                .exchange()
//                .expectStatus().isForbidden()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(504)
//                .returnResult()
//                .getResponseBodyContent();
//        String expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
    }

    @Test
    @Order(10)
    void createSKU1() throws Exception
    {
        String requireJson="{\n" +
                "  \"sn\": \"newSkuSn\",\n" +
                "  \"name\": \"name\",\n" +
                "  \"originalPrice\": 100,\n" +
                "  \"configuration\": \"configuration\",\n" +
                "  \"weight\": 100,\n" +
                "  \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\n" +
                "  \"inventory\": 100,\n" +
                "  \"detail\": \"detail\"\n" +
                "}";
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.post().uri("/shops/1/spus/273/skus")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isMap()
                .jsonPath("$.data.skuSn").isEqualTo("newSkuSn")
                .jsonPath("$.data.inventory").isEqualTo(100)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"id\":696,\"name\":\"name\",\"skuSn\":\"newSkuSn\",\"imageUrl\":\"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\"inventory\":100,\"originalPrice\":100,\"price\":100,\"disabled\":0},\"errmsg\":\"成功\"}";
    }

    @Test
    @Order(12)
    void createSKU3() throws Exception
    {
        String requireJson="{\n" +
                "  \"sn\": \"newSkuSn\",\n" +
                "  \"name\": \"name\",\n" +
                "  \"originalPrice\": 100,\n" +
                "  \"configuration\": \"configuration\",\n" +
                "  \"weight\": 100,\n" +
                "  \"imageUrl\": \"http://47.52.88.176/file/images/201612/file_586227f3cd5c9.jpg\",\n" +
                "  \"inventory\": 100,\n" +
                "  \"detail\": \"detail\"\n" +
                "}";
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.post().uri("/shops/2/spus/273/skus")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";

    }

    @Test
    @Order(13)
    void modifySKU1() throws Exception{
        String requireJson="{\n    \"name\": \"name\",\n    \"originalPrice\": 100,\n    \"configuration\": \"configuration\",\n    \"weight\": 100,\n    \"inventory\": 9999,\n    \"detail\": \"detail\"\n}";
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.put().uri("/shops/1/skus/20682")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
    }

    @Test
    @Order(15)
    void modifySKU3() throws Exception{
        String requireJson="{\n    \"name\": \"name\",\n    \"originalPrice\": 100,\n    \"configuration\": \"configuration\",\n    \"weight\": 100,\n    \"inventory\": 9999,\n    \"detail\": \"detail\"\n}";
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.put().uri("/shops/2/skus/273")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(505)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
    }

    @Test
    @Order(16)
    void modifySKU4() throws Exception{
        String requireJson="{\n    \"name\": \"name\",\n    \"originalPrice\": 100,\n    \"configuration\": \"configuration\",\n    \"weight\": 100,\n    \"inventory\": 9999,\n    \"detail\": \"detail\"\n}";
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.put().uri("/shops/1/skus/1")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .jsonPath("$.errno").isEqualTo(504)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

    }

    @Test
    @Order(17)
    void putOffGoodsOnSale1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/1/skus/400/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(18)
    void putOffGoodsOnSale2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/1/skus/400/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.STATE_NOCHANGE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(19)
    void putOffGoodsOnSale3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/2/skus/400/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(20)
    void putOffGoodsOnSale4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/1/skus/1/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(21)
    void putOffGoodsOnSale5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/2/skus/400/offshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(22)
    void putGoodsOnSale1() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/1/skus/400/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    @Test
    @Order(23)
    void putGoodsOnSale2() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/1/skus/400/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.STATE_NOCHANGE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(24)
    void putGoodsOnSale3() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/2/skus/400/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(25)
    void putGoodsOnSale4() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/1/skus/1/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    @Test
    @Order(26)
    void putGoodsOnSale5() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] response  = manageClient.put().uri("/shops/2/skus/400/onshelves")
                .header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    @Test
    @Order(28)
    void add_floating_price2() throws Exception
    {
        LocalDateTime beginTime= LocalDateTime.of(2020,12,29,20,0,0);
        LocalDateTime endTime=LocalDateTime.of(2020,12,30,10,0,0);
        String requireJson="{\n    \"activityPrice\": 100,\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": 99999\n}";
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.post().uri("/shops/1/skus/273/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(900)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":900,\"errmsg\":\"库存不足：273\"}";
    }

    @Test
    @Order(29)
    void add_floating_price3() throws Exception
    {
        LocalDateTime beginTime= LocalDateTime.of(2020,12,29,20,0,0);
        LocalDateTime endTime=LocalDateTime.of(2020,12,30,10,0,0);
        String requireJson="{\n    \"activityPrice\": 100,\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": 100\n}";
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.post().uri("/shops/2/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"code\":\"RESOURCE_ID_OUTSCOPE\",\"errmsg\":\"操作的资源id不是自己的对象\",\"data\":null}";
    }

    @Test
    @Order(30)
    void add_floating_price4() throws Exception
    {
        LocalDateTime beginTime= LocalDateTime.of(2020,12,29,20,0,0);
        LocalDateTime endTime=LocalDateTime.of(2020,12,30,10,0,0);
        String requireJson="{\n    \"activityPrice\": \"100\",\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": \"100\"\n}";
        String token = this.adminLogin("13088admin", "123456");
        LocalDateTime beginTime1=LocalDateTime.of(2019,12,12,10,0,0);
        requireJson="{\n    \"activityPrice\": 100,\n    \"beginTime\": \""+beginTime1.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": 100\n}";
        byte[] response =manageClient.post().uri("/shops/1/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"must be a future date;\"}";
    }

    @Test
    @Order(31)
    void add_floating_price5() throws Exception
    {
        LocalDateTime beginTime= LocalDateTime.of(2020,12,29,20,0,0);
        LocalDateTime endTime=LocalDateTime.of(2020,12,30,10,0,0);
        String requireJson="{\n    \"activityPrice\": \"100\",\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": \"100\"\n}";
        String token = this.adminLogin("13088admin", "123456");
        LocalDateTime endTime1=LocalDateTime.of(2020,12,11,20,0,0);
        requireJson="{\n    \"activityPrice\": 100,\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime1.toString()+"\",\n    \"quantity\": 100\n}";
        byte[] response =manageClient.post().uri("/shops/1/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(610)
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(32)
    void add_floating_price6() throws Exception
    {
        LocalDateTime beginTime= LocalDateTime.of(2020,12,29,20,0,0);
        LocalDateTime endTime=LocalDateTime.of(2020,12,30,10,0,0);
        String requireJson="{\n    \"activityPrice\": \"100\",\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": \"100\"\n}";
        String token = this.adminLogin("13088admin", "123456");

        requireJson="{\n    \"activityPrice\": 100,\n    \"beginTime\": \""+beginTime.toString()+"\",\n    \"endTime\": \""+endTime.toString()+"\",\n    \"quantity\": -100\n}";
        byte[] response =manageClient.post().uri("/shops/1/skus/278/floatPrices")
                .header("authorization",token)
                .bodyValue(requireJson)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":503,\"errmsg\":\"must be greater than or equal to 0;\"}";

    }

    @Test
    @Order(33)
    void deleteSku1() throws Exception{
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.delete().uri("/shops/2/skus/20682").header("authorization",token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(505)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
    }

    @Test
    @Order(34)
    void deleteSku2() throws Exception{
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.delete().uri("/shops/1/skus/20682").header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"errmsg\":\"成功\"}";
    }

    @Test
    @Order(35)
    void deleteSku3() throws Exception{
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.delete().uri("/shops/1/skus/20682").header("authorization",token)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .jsonPath("$.errno").isEqualTo(504)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
    }

    @Test
    @Order(36)
    void deleteSku4() throws Exception{
        String token = this.adminLogin("13088admin", "123456");

        byte[] response =manageClient.delete().uri("/shops/1/skus/1").header("authorization",token)
                .exchange()
                .expectStatus().is4xxClientError().expectBody()
                .jsonPath("$.errno").isEqualTo(504)
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";

    }
}

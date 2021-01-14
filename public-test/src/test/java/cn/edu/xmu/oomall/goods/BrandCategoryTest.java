package cn.edu.xmu.oomall.goods;

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
 * description: BrandCategoryTest
 * date: 2020/12/12 22:54
 * author1: 张悦 10120182203143
 * author2: 岳皓 24320182203319
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BrandCategoryTest {


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

    /** 1
     * 不需要登录-查询商品分类关系1-存在该分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(1)
    public void getCategorySubs() throws Exception {
        byte[] responseString=mallClient.get().uri("/categories/122/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[{\"id\":123,\"pid\":122,\"name\":\"大师原作\",\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":124,\"pid\":122,\"name\":\"艺术衍生品\",\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 2
     * 不需要登录-查询商品分类关系2-不存在该分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(2)
    public void getCategorySubs2() throws Exception {//检测如果没有此id
        byte[]responseString=mallClient.get().uri("/categories/1000/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /** 3
     * 不需要登录-查询商品分类关系3-该分类下无子分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(3)
    public void getCategorySubs3() throws Exception {
        byte[]responseString=mallClient.get().uri("/categories/123/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":[],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    /** 17
     * 需管理员登录
     * 新增类目
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(4)
    public void insertCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"test\"}";
        byte[] responseString = manageClient.post().uri("/shops/0/categories/139/subcategories")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        byte[] responseString2=mallClient.get().uri("/categories/139/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":20142,\"pid\":139,\"name\":\"test\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 18
     * 需管理员登录
     * 修改分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(5)
    public void modifyCategoryTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testCategory\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/categories/126")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/125/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":126,\"pid\":125,\"name\":\"testCategory\"},{\"id\":133,\"pid\":125,\"name\":\"腕表\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 19
     * 需管理员登录
     * 删除分类-删除子分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(6)
    public void deleteCategoryTest11() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/123")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/122/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":[{\"id\":124,\"pid\":122,\"name\":\"艺术衍生品\",\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 20
     * 需管理员登录
     * 删除分类-删除分类
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(7)
    public void deleteCategoryTest12() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/137")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);

        byte[] responseString2=mallClient.get().uri("/categories/137/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 21
     * 无需登录
     * 获取品牌
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(8)
    public void getBrands() throws Exception {
        byte[]responseString=mallClient.get().uri("/brands")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":53,\"pages\":6,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":71,\"name\":\"戴荣华\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":73,\"name\":\"黄卖九\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":74,\"name\":\"李进\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":75,\"name\":\"李菊生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":76,\"name\":\"李小聪\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":77,\"name\":\"刘伟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":78,\"name\":\"陆如\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":79,\"name\":\"秦锡麟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":80,\"name\":\"舒慧娟\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 22
     * 无需登录
     * 获取品牌-page=5
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(9)
    public void getBrands2() throws Exception {
        byte[]responseString=mallClient.get().uri("/brands?page=5")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":{\"total\":53,\"pages\":6,\"pageSize\":10,\"page\":5,\"list\":[{\"id\":111,\"name\":\"孙星池\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":112,\"name\":\"林祺龙\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":113,\"name\":\"故宫\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":114,\"name\":\"方毅\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":115,\"name\":\"李晓辉\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":116,\"name\":\"揭金平\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":117,\"name\":\"杨曙华\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":118,\"name\":\"方冬生\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":119,\"name\":\"皇家窑火\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"},{\"id\":20120,\"name\":\"cpz1\",\"detail\":null,\"imageUrl\":null,\"gmtCreate\":\"2020-12-10T22:36:01\",\"gmtModified\":\"2020-12-10T22:36:01\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }
    /** 23
     * 管理员登录
     * 修改品牌-品牌ID不存在
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(10)
    public void modifyBrandTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/126")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    /** 24
     * 需管理员登录
     * 修改品牌信息
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(11)
    public void modifyBrandTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/71")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[]responseString2=mallClient.get().uri("/brands?pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":{\"total\":53,\"pages\":27,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":71,\"name\":\"testBrand\",\"detail\":null,\"imageUrl\":null},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }

    /** 25
     * 需管理员登录
     * 修改品牌信息
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(12)
    public void modifyBrandTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testBrand-56\",\"detail\": \"testBrandDetail\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/brands/71")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[]responseString2=mallClient.get().uri("/brands?pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":{\"total\":53,\"pages\":27,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":71,\"name\":\"testBrand-56\",\"detail\":\"testBrandDetail\",\"imageUrl\":null},{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 26
     * 需管理员登录
     * 删除品牌信息-品牌id不存在
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(13)
    public void deleteBrandTest() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/10000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    /** 27
     * 需管理员登录
     * 删除商品分类-分类id存在
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(14)
    public void deleteCategoryTest13() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/10000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }
    /** 28
     * 需管理员登录
     * 删除品牌
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(15)
    public void deleteBrandTest12() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/71")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
        byte[] responseString2=mallClient.get().uri("/brands?pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .returnResult().getResponseBodyContent();
        String expectedResponse2 = "{\"errno\":0,\"data\":{\"total\":52,\"pages\":26,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":72,\"name\":\"范敏祺\",\"detail\":null,\"imageUrl\":null},{\"id\":73,\"name\":\"黄卖九\",\"detail\":null,\"imageUrl\":null}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, "UTF-8"), false);
    }
    /** 29
     * 需管理员登录
     * 修改分类-分类id不存在
     * 姓名：岳皓
     * 学号：24320182203319
     **/
    @Test
    @Order(16)
    public void modifyCategoryTest() throws Exception {
        String token = this.login("13088admin", "123456");
        String roleJson = "{\"name\": \"testCategory\"}";
        byte[] responseString = manageClient.put().uri("/shops/0/categories/10000")
                .header("authorization", token)
                .bodyValue(roleJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 获取所有品牌（第一页）
     * 姓名：张悦
     * 学号：10120182203143
     * @throws Exception
     */
    @Test
    @Order(17)
    public void findAllBrands1() throws Exception{

        byte[] responseString = mallClient.get().uri("/brands?page=1&pageSize=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 2,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 72,\n" +
                "        \"name\": \"范敏祺\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 73,\n" +
                "        \"name\": \"黄卖九\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取所有品牌（第二页）
     * 姓名：张悦
     * 学号：10120182203143
     * @throws Exception
     */
    @Test
    @Order(17)
    public  void findAllBrands2() throws Exception {
        byte[] responseString = mallClient.get().uri("/brands?page=2&pageSize=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 2,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 73,\n" +
                "        \"name\": \"黄卖九\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * description: 新增品牌 品牌名称已存在
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(18)
    public void insertBrandTest2() throws Exception {

        String token = this.login("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("name", "黄卖九");
        body.put("detail", null);
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/brands",0)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.BRAND_NAME_SAME.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 新增品牌 品牌名称为空
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(18)
    public void insertBrandTest3() throws Exception{

        JSONObject body = new JSONObject();
        body.put("name", "");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();

        String token = this.login("13088admin", "123456");
        byte[] responseString = manageClient.post().uri("/shops/{shopId}/brands",0)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 未登录新增品牌
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(18)
    public void insertBrandTest4() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test4");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/brands",0)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token新增品牌
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(18)
    public void insertBrandTest5() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test4");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/brands",0)
                .header("authorization", "test4")
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 新增品牌 （成功）
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(18)
    public void insertBrandTest1()throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test4");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/{shopId}/brands",0)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"name\": \"test4\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": test4\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,  new String(responseString, StandardCharsets.UTF_8), false);

        //查询验证品牌新增成功
        byte[] responseString2 = mallClient.get().uri("/brands?page=53&pageSize=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 53,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"name\": \"test4\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\": \"test4\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);
    }

    /**
     * description: 修改品牌 (查无此品牌)
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(19)
    public void modifyBrand2() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test678");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();
        String token = this.login("13088admin", "123456");
        //String token = this.login("13088admin","123456");

        byte[] responseString = manageClient.put().uri("/shops/{shopId}/brands/{id}", 0, 11111)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 修改品牌(品牌名称已存在)
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(19)
    public void modifyBrand3() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "黄卖九");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/{shopId}/brands/{id}", 0, 119)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.BRAND_NAME_SAME.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * description: 未登录修改品牌
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(19)
    public void modifyBrand4() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test4");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/shops/{shopId}/brands/{id}", 0, 119)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token修改品牌
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(19)
    public void modifyBrand5() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test4");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/shops/{shopId}/brands/{id}", 0, 119)
                .header("authorization", "test4")
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * description: 修改品牌(成功)
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(19)
    public void modifyBrand1() throws Exception {
        String token = this.login("13088admin", "123456");

        JSONObject body = new JSONObject();
        body.put("name", "test41");
        body.put("detail", "test4");
        String brandJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/shops/{shopId}/brands/{id}", 0, 72)
                .header("authorization", token)
                .bodyValue(brandJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证品牌修改成功
        byte[] responseString2 = mallClient.get().uri("/brands?page=1&pageSize=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 1,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"total\": 53,\n" +
                "    \"pages\": 53,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 72,\n" +
                "        \"name\": \"test41\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\":\"test4\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);
    }


    /**
     * description: 删除品牌
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(20)
    public void deleteBrandTest5() throws Exception{
        String token = this.login("13088admin", "123456");

        byte[] responseString1 = manageClient.delete().uri("/shops/{shopId}/brands/{id}", 0, 73)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证品牌删除成功
        byte[] responseString2 = mallClient.get().uri("/brands?page=2&pageSize=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"page\": 2,\n" +
                "    \"pageSize\": 1,\n" +
                "    \"total\": 52,\n" +
                "    \"pages\": 52,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 74,\n" +
                "        \"name\": \"李进\",\n" +
                "        \"imageUrl\": null,\n" +
                "        \"detail\":null\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

        //查询验证品牌下的spu是否变成没有品牌的spu
        byte[] responseString3 = mallClient.get().uri("/spus/11274")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse3 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 11274,\n" +
                "    \"brand\": {\n" +
                "      \"id\": 0\n" +
                "    }\n" +
                "    }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse3, new String(responseString3, StandardCharsets.UTF_8), false);
    }


    /**
     * description: 删除品牌(id不存在)
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(20)
    public void deleteBrandTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        //String token = this.login("13088admin","123456");
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * description: 伪造token删除品牌
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(20)
    public void deleteBrandTest3() {
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/72")
                .header("authorization", "test4")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 未登录删除品牌
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(20)
    public void deleteBrandTest4() {
        byte[] responseString = manageClient.delete().uri("/shops/0/brands/72", 0, 0)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * description: 根据种类ID获取商品下一级分类信息(成功)
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(20)
    public void listSubcategories1() throws Exception{

        byte[] responseString = mallClient.get().uri("/categories/125/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 126,\n" +
                "      \"pid\": 125\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 133,\n" +
                "      \"pid\": 125\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * description: 根据分类ID获取商品下一级分类信息(分类ID不存在)
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(20)
    public void listSubcategories2() throws Exception{
        byte[] responseString = mallClient.get().uri("/categories/199/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 新增商品一级类目,pid=0 （成功）
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void insertGoodsCategoryTest1()throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test42");
        String goodsCategoryJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/0/categories/0/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"pid\":0,\n" +
                "        \"name\": \"test42\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,  new String(responseString, StandardCharsets.UTF_8), false);

        //查询验证分类新增成功
        byte[] responseString2 = mallClient.get().uri("/categories/0/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2 ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 122,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"艺术品\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 125,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"收藏品\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 127,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"高端日用品\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 131,\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"建行专享\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"cpz1\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"pid\": 0,\n" +
                "      \"name\": \"test42\"\n" +
                "    }\n" +

                "  ]\n" +
                "}";;

        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }

    /**
     * description: 新增商品二级类目,pid=131 （成功）
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void insertSubGoodsCategoryTest1()throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test4");
        String goodsCategoryJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/0/categories/131/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "        \"pid\":131,\n" +
                "        \"name\": \"test4\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,  new String(responseString, StandardCharsets.UTF_8), false);


        //查询验证分类新增成功
        byte[] responseString2 = mallClient.get().uri("/categories/131/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"pid\": 131,\n" +
                "      \"name\": \"深圳行\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"pid\": 131,\n" +
                "      \"name\": \"test4\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }

    /**
     * description: 新增商品二级类目, pid不存在
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void insertSubGoodsCategoryTest2()throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test4");
        String goodsCategoryJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/0/categories/9/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 新增商品类目，类目名称已存在
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void insertGoodsCategoryTest2() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "艺术品");
        String goodsCategoryJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/0/categories/122/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.CATEGORY_NAME_SAME.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 新增商品类目 类目名称为空
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void insertGoodsCategoryTest3() throws Exception{
        JSONObject body = new JSONObject();
        body.put("name", "");
        String goodsCategoryJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.post().uri("/shops/0/categories/122/subcategories")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * description: 未登录新增类目
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void insertGoodsCategoryTest4() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test4");
        String goodsCategoryJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/shops/0/categories/122/subcategories",0)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token新增类目
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void insertGoodsCategoryTest5() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test4");
        String goodsCategoryJson = body.toJSONString();

        byte[] responseString = manageClient.post().uri("/shops/0/categories/122/subcategories")
                .header("authorization", "test4")
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 修改类目信息(类目ID不存在)
     * 姓名：张悦
     * 学号：10120182203143
     */
    @Test
    @Order(21)
    public void modifyCategory1() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name", "test4");
        String goodsCategoryJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/0/categories/99")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 修改类目信息 (类目名称重复)
     * 姓名：张悦
     * 学号：10120182203143
     */
    @Test
    @Order(21)
    public void modifyCategory2() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "收藏品");
        String goodsCategoryJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/0/categories/135")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.CATEGORY_NAME_SAME.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 未登录修改类目
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void modifyCategory3() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test4");
        String goodsCategoryJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/shops/0/categories/123")
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token修改类目
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void modifyCategory4() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test4");
        String goodsCategoryJson = body.toJSONString();

        byte[] responseString = manageClient.put().uri("/shops/0/categories/123")
                .header("authorization", "test4")
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * description: 修改类目信息(成功)
     * 姓名：张悦
     * 学号：10120182203143
     * @param
     * @return void
     */
    @Test
    @Order(21)
    public void modifyCategory5() throws Exception {

        JSONObject body = new JSONObject();
        body.put("name", "test3");
        String goodsCategoryJson = body.toJSONString();

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/shops/0/categories/126")
                .header("authorization", token)
                .bodyValue(goodsCategoryJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证是否修改成功
        byte[] responseString2 = mallClient.get().uri("/categories/125/subcategories")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse2="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": 126,\n" +
                "      \"pid\": 125,\n" +
                "      \"name\": \"test3\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 133,\n" +
                "      \"pid\": 125,\n" +
                "      \"name\": \"腕表\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse2, new String(responseString2, StandardCharsets.UTF_8), false);

    }

    /**
     * description: 删除一级类目 (成功)
     * 姓名：张悦
     * 学号：10120182203143
     */
    @Test
    @Order(22)
    public void deleteCategoryTest1() throws Exception {

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/categories/122")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证一级类目删除
        byte[] responseString2 = mallClient.get().uri("/categories/122/subcategories")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证二级类目下的商品变成没有分类的商品
        byte[] responseString5 = mallClient.get().uri("/spus/11274")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse5 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 11274,\n" +
                "    \"category\": {\n" +
                "      \"id\": 0\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse5, new String(responseString5, StandardCharsets.UTF_8), false);
    }

    /**
     * description: 删除二级类目 (成功)
     * 姓名：张悦
     * 学号：10120182203143
     */
    @Test
    @Order(22)
    public void deleteCategoryTest5() throws Exception {

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/categories/128")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        //查询验证二级类目下的商品变成没有分类的商品
        byte[] responseString5 = mallClient.get().uri("/spus/11273")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse5 = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 11273,\n" +
                "    \"category\": {\n" +
                "      \"id\": 0\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse5, new String(responseString5, StandardCharsets.UTF_8), false);
    }

    /**
     * description: 删除类目（ id不存在)
     * 姓名：张悦
     * 学号：10120182203143
     *
     * @param
     * @return void
     */
    @Test
    @Order(22)
    public void deleteCategoryTest2() throws Exception {

        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.delete().uri("/shops/0/categories/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 伪造token删除类目
     * 姓名：张悦
     * 学号：10120182203143
     *
     * @param
     * @return void
     */
    @Test
    @Order(22)
    public void deleteCategoryTest3() {

        byte[] responseString = manageClient.delete().uri("/shops/0/categories/127")
                .header("authorization", "test4")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * description: 未登录删除类目
     * 姓名：张悦
     * 学号：10120182203143
     *
     * @param
     * @return void
     */
    @Test
    @Order(22)
    public void deleteCategoryTest4() {
        byte[] responseString = manageClient.delete().uri("/shops/0/categories/127")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
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
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");
        //endregion
    }


}

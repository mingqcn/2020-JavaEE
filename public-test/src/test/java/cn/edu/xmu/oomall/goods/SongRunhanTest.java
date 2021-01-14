package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * 24320182203266
 * 宋润涵
 * 请注意！！！
 * 由于时间段的更改本测试可能会发生错误，当时间段确定后会更新测试
 * 如遇到冲突，请自行调整端口设置
 * 本测试运行前务必清除Redis缓存，重置数据库
 */
@SpringBootTest(classes = PublicTestApp.class)   //标识本类是一个SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SongRunhanTest {
    @Autowired
    private ObjectMapper mObjectMapper;

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
     * 在时段"0"下新建秒杀活动，然后删除活动
     */
    @Test
    public void addFlashSaleActivity1() throws Exception{
        String token = this.login("13088admin", "123456");
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);

        String requestJson = "{\"flashDate\":\"" + dateTime + "\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",token).bodyValue(requestJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").isMap()
                .returnResult()
                .getResponseBodyContent();

        String response = new String(responseBuffer, "utf-8");

        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        responseBuffer= manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",token)
                .exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

        responseBuffer= manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",token)
                .exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新建今天的活动，应当阻止；
     */
    @Test
    public void addFlashSaleActivity2() throws Exception{
        String token = this.login("13088admin", "123456");
        LocalDateTime dateTime = LocalDateTime.now();

        String requestJson = "{\"flashDate\":\"" + dateTime + "\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",token).bodyValue(requestJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(503)
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 获取秒杀活动
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getFlashSaleActivity1()throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.get().uri("/flashsales/current?page=1&pageSize=10");
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8").expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[?(@.id == 8)].goodsSku.id").isEqualTo(275)
                .jsonPath("$[?(@.id == 7)].goodsSku.id").isEqualTo(290)
                .returnResult()
                .getResponseBodyContent();

        String response = new String(responseBuffer, "utf-8");
    }

    /**
     * 向秒杀活动中加入SKU、删除SKU
     * @throws Exception
     */
    @Test
    public void addSKUToActivity()throws Exception {
        String token = this.login("13088admin", "123456");
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);

        String requestJson = "{\"flashDate\":\"" + dateTime + "\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",token).bodyValue(requestJson);
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").isMap()
                .returnResult()
                .getResponseBodyContent();

        String response = new String(responseBuffer, "utf-8");

        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertActivityId = jsonNode.asInt();

        requestJson = "{\"skuId\": 280,\"price\": 2365,\"quantity\": 36}";
        responseBuffer = manageClient.post().uri("/shops/0/flashsales/" + insertActivityId + "/flashitems").header("authorization",token).bodyValue(requestJson).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").isMap()
                .returnResult()
                .getResponseBodyContent();

        response = new String(responseBuffer, "utf-8");
        jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertItemId = jsonNode.asInt();

        /* 重复加入 */
//        requestJson = "{\"skuId\": 280,\"price\": 2365,\"quantity\": 36}";
//        responseBuffer =webClient.post().uri("flashsales/" + insertId + "/flashitems").bodyValue(requestJson).exchange().expectHeader().contentType("application/json;charset=UTF-8")
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .jsonPath("$.errmsg").isEqualTo("成功")
//                .jsonPath("$.data").isMap()
//                .returnResult()
//                .getResponseBodyContent();

        manageClient.delete().uri("/shops/0/flashsales/"+ insertActivityId +"/flashitems/"+ insertItemId).header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

        //删除活动
        responseBuffer= manageClient.delete().uri("/shops/0/flashsales/"+insertActivityId).header("authorization",token)
                .exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(Integer.MAX_VALUE - 2)
    public void delFlashItem() throws Exception {
        String token = this.login("13088admin", "123456");
        // region 删除今天的秒杀商品
        manageClient.delete().uri("/shops/0/flashsales/3/flashitems/5").header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
        manageClient.delete().uri("/shops/0/flashsales/3/flashitems/6").header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
        manageClient.delete().uri("/shops/0/flashsales/4/flashitems/7").header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
        manageClient.delete().uri("/shops/0/flashsales/4/flashitems/8").header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
        // endregion

        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.get().uri("/flashsales/current?page=1&pageSize=10");
        responseBuffer = res.exchange().expectHeader().contentType("application/json;charset=UTF-8").expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(0)
                .returnResult()
                .getResponseBodyContent();

        String response = new String(responseBuffer, "utf-8");

    }

    /**
     * 上/下线秒杀活动
     * @throws Exception
     */
    @Test
    @Order(Integer.MAX_VALUE - 1)
    public void offLine() throws Exception {
        String token = this.login("13088admin", "123456");
        manageClient.put().uri("/shops/0/flashsales/1/offshelves").header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        manageClient.put().uri("/shops/0/flashsales/1/onshelves").header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    @Test
    @Order(Integer.MAX_VALUE)
    public void delFlashSale() throws Exception {
        String token = this.login("13088admin", "123456");
        for(int i=1;i<=4;++i){
            manageClient.put().uri("/shops/0/flashsales/"+ i +"/offshelves").header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();

        }

        for(int i=1;i<=4;++i){
            manageClient.delete().uri("/shops/0/flashsales/" + i).header("authorization",token).exchange().expectHeader().contentType("application/json;charset=UTF-8")
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .jsonPath("$.errmsg").isEqualTo("成功")
                    .jsonPath("$.data").doesNotExist()
                    .returnResult()
                    .getResponseBodyContent();
        }

        /*
          继续删，应当报错
         */
        manageClient.delete().uri("/shops/0/flashsales/0").header("authorization",token).exchange()
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 这个不是测试用例，不要算数！！！
     * 这个不是测试用例，不要算数！！！
     * 这个不是测试用例，不要算数！！！
     * @param userName 登录的用户名
     * @param password 登录的密码
     * @return token
     * @throws Exception
     */
    private String login(String userName, String password) throws Exception{
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
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }
}

package cn.edu.xmu.oomall.goods;

import cn.edu.xmu.ooad.PublicTestApp;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 需要其他模块进行集成测试 By 宋润涵

/**
 * @author: 24320182203259 邵良颖
 * Created at: 2020-12-15 17:36
 */
@SpringBootTest(classes = PublicTestApp.class)
public class ShaoLiangYingTest {

    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    private static String adminToken;
    private static String userToken;

    @Autowired
    private ObjectMapper mObjectMapper;



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

    public String userLogin(String userName, String password) throws Exception {
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
        return JSONObject.parseObject(new String(responseString)).getString("data");
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
                .returnResult()
                .getResponseBodyContent();
        return  JacksonUtil.parseString(new String(ret, "UTF-8"), "data");

    }

    /**
     * Flashsale002
     * 正确增加三天后的秒杀
     * @throws Exception
     */
    @Test
    public void createflash1() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
//        userToken = userLogin("8606245097", "123456");

        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 不允许增加过去和今天的秒杀
     * @throws Exception
     */
    @Test
    public void createflash2() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        //增加昨天的秒杀
        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 增加不存在时段id的秒杀
     * @throws Exception
     */
    @Test
    public void createflash3() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/-1/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //不许加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale002
     * 增加flashDate+segment_id已经存在且未删除的秒杀
     * @throws Exception
     */
    @Test
    public void createflash4() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        for (int i=0;i<6;i++) {//无论多少次执行都应该是对的
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse("2021-12-21 00:00:01",df);
            //增加秒杀
            String requestJson = "{\"flashDate\":\""+ ldt.toString() +"\"}";
            byte[] responseBuffer = null;
            WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

            responseBuffer = res.exchange()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .returnResult()
                    .getResponseBodyContent();
            String response = new String(responseBuffer, "utf-8");
            JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
            int insertId = jsonNode.asInt();

            responseBuffer = res.exchange()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.TIMESEG_CONFLICT.getCode())
                    .returnResult()
                    .getResponseBodyContent();

            responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange()
                    .expectBody()
                    .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                    .jsonPath("$.data").doesNotExist()
                    .returnResult()
                    .getResponseBodyContent();
        }
    }

    /**
     * Flashsale004
     * 删除不存在的秒杀
     * @throw Exception
     */
    @Test
    public void deleteflashsale1() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        byte[] responseBuffer = null;
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/-1").header("authorization",adminToken).exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * Flashsale005
     * 不允许将秒杀时间修改为过去时间
     */
    @Test
    public void updateflashsale1() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString() +"\",\"id\":8}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse("2019-12-17 00:00:00",df);
        //修改秒杀
        requestJson = "{\"flashDate\":\""+ ldt.toString()+"\"}";
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).bodyValue(requestJson);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();

        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 修改了状态
     * Flashsale005
     * 不允许修改过去的秒杀活动
     */
    @Test
    public void updateflashsale2() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        //请运行sql增加一条过去的记录

        int pastId = 10828;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse("2021-12-17 00:00:00",df);
        String requestJson = "{\"flashDate\":\""+ ldt.toString()+"}";
        byte[] responseBuffer = null;

        //修改秒杀
        requestJson = "{\"flashDate\":\""+ ldt.toString()+"\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.put().uri("/shops/0/flashsales/"+pastId).header("authorization",adminToken).bodyValue(requestJson);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ACTIVITYALTER_INVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 正常上线秒杀活动
     */
    @Test
    public void flashsaleState1() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        //创建一个秒杀活动，默认状态是下线
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        //将创建的秒杀活动的状态设置为上线
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/onshelves").header("authorization",adminToken);
        res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        //将创建的秒杀活动的状态设置为下线
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/offshelves").header("authorization",adminToken);
        res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 不允许修改被删除的秒杀的状态
     */
    @Test
    public void flashsaleState2() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        //创建一个秒杀活动，默认状态是下线
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        //删除该下线状态的秒杀
        res = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken);
        res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        //修改被删除的秒杀的状态
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/onshelves").header("authorization",adminToken);
        res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 不允许删除已上线的活动,只能删除下线状态的活动
     */
    @Test
    public void flashsaleState3() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        //创建一个秒杀活动，默认状态是下线
        //增加三天后的秒杀
        LocalDateTime dateTime = LocalDateTime.now().plusDays(3);
        String requestJson = "{\"flashDate\":\""+ dateTime.toString()+"\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/0/timesegments/8/flashsales").header("authorization",adminToken).bodyValue(requestJson);

        //增加秒杀
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        //将创建的秒杀活动的状态设置为已上线
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/onshelves").header("authorization",adminToken);
        res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        //删除已上线的秒杀活动
        res = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken);
        res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ACTIVITYALTER_INVALID.getCode())
                .returnResult()
                .getResponseBodyContent();

        //恢复数据库
        //将创建的秒杀活动的状态设置为下线
        res = manageClient.put().uri("/shops/0/flashsales/"+insertId+"/offshelves").header("authorization",adminToken);
        res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        //删除该活动
        responseBuffer = manageClient.delete().uri("/shops/0/flashsales/"+insertId).header("authorization",adminToken).exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 成功新增商品评论
     * @throws Exception
     */
    @Test
    public void addSkuComment1() throws Exception{
        userToken = userLogin("8606245097", "123456");
        String requestJson = "{\"type\":0,\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/orderitems/143/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增商品评论，该订单条目已评论
     * @throws Exception
     */
    @Test
    public void addSkuComment2() throws Exception {
        userToken = userLogin("8606245097", "123456");
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/orderitems/161/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();

        res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.COMMENT_EXISTED.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增商品评论，订单条目不存在
     * @throws Exception
     */
    @Test
    public void addSkuComment3() throws Exception {
        userToken = userLogin("8606245097", "123456");
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/orderitems/8888/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.USER_NOTBUY.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增商品评论，参数错误
     * @throws Exception
     */
    @Test
    public void addSkuComment4() throws Exception {
        userToken = userLogin("8606245097", "123456");
        String requestJson = "{\"type\":\"6\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/orderitems/4/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res
                .exchange().expectStatus().isBadRequest()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核评论，通过评论
     * @throws Exception
     */
    @Test
    public void auditComment1() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        userToken = userLogin("8606245097", "123456");
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/orderitems/151/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        requestJson = "{\"conclusion\":\"true\"}";
        res = manageClient.put().uri("/shops/0/comments/"+insertId+"/confirm").header("authorization",adminToken).bodyValue(requestJson);
        //审核
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员审核评论，不通过评论
     * @throws Exception
     */
    @Test
    public void auditComment2() throws Exception {
        adminToken = adminLogin("13088admin", "123456");
        userToken = userLogin("8606245097", "123456");
        String requestJson = "{\"type\":\"0\",\"content\":\"新增Sku评论\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = mallClient.post().uri("/orderitems/32/comments").header("authorization",userToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isNotEmpty()
                .returnResult()
                .getResponseBodyContent();
        String response = new String(responseBuffer, "utf-8");
        JsonNode jsonNode = mObjectMapper.readTree(response).findPath("data").get("id");
        int insertId = jsonNode.asInt();

        requestJson = "{\"conclusion\":\"true\"}";
        res = manageClient.put().uri("/shops/0/comments/"+insertId+"/confirm").header("authorization",adminToken).bodyValue(requestJson);
        //审核
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增价格浮动项,sku库存应大于价格浮动项库存
     * @throws Exception
     */
    @Test
    public void add_floating_price1() throws Exception{
        adminToken = adminLogin("13088admin", "123456");
//        userToken = userLogin("8606245097", "123456");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = LocalDateTime.parse("9041-12-28 17:42:20",df);
        LocalDateTime endTime = LocalDateTime.parse("9042-01-28 17:42:20",df);
        String requestJson = "{\"activityPrice\":\"120\", \"beginTime\":\""+beginTime.toString()+"\",\"endTime\":\""+endTime.toString()+"\",\"quantity\": \"10000\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/1/skus/626/floatPrices").header("authorization",adminToken).bodyValue(requestJson);
        //sku库存应大于价格浮动项库存
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SKU_NOTENOUGH.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增价格浮动项,SKU_ID不存在
     * @throws Exception
     */
    @Test
    public void add_floating_price2() throws Exception{
        adminToken = adminLogin("13088admin", "123456");
//        userToken = userLogin("8606245097", "123456");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beginTime = LocalDateTime.parse("2020-12-28 17:42:20",df);
        LocalDateTime endTime = LocalDateTime.parse("2021-01-28 17:42:20",df);
        String requestJson = "{\"activityPrice\":\"120\", \"beginTime\":\""+beginTime.toString()+"\",\"endTime\":\""+endTime.toString()+"\",\"quantity\": \"1000\"}";
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/1/skus/23333/floatPrices").header("authorization",adminToken).bodyValue(requestJson);
        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 新增价格浮动项,时间段冲突
     * 价格不是String写错了，考虑删除 By 宋润涵
     * @throws Exception
     */
    @Test
    public void add_floating_price3() throws Exception{
        adminToken = adminLogin("13088admin", "123456");
//        userToken = userLogin("8606245097", "123456");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        byte[] responseBuffer = null;

        //开始时间大于结束时间
        LocalDateTime beginTime = LocalDateTime.parse("2020-12-28 17:42:20",df);
        LocalDateTime endTime = LocalDateTime.parse("2020-01-28 17:42:20",df);
        String requestJson = "{\"activityPrice\":\"120\", \"beginTime\":\""+beginTime.toString()+"\",\"endTime\":\""+endTime.toString()+"\",\"quantity\": \"100\"}";
        WebTestClient.RequestHeadersSpec res = manageClient.post().uri("/shops/1/skus/626/floatPrices").header("authorization",adminToken).bodyValue(requestJson);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.Log_Bigger.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

        //shopId和skuId匹配
        //sku-spu-shop:626-626-0 9001-8991-22
        beginTime = LocalDateTime.parse("2020-12-28 17:42:20",df);
        endTime = LocalDateTime.parse("2021-01-28 17:42:20",df);
        requestJson = "{\"activityPrice\":\"120\", \"beginTime\":\""+beginTime.toString()+"\",\"endTime\":\""+endTime.toString()+"\",\"quantity\": \"10\"}";
        res = manageClient.post().uri("/shops/0/skus/8991/floatPrices").header("authorization",adminToken).bodyValue(requestJson);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();

        //时段冲突
        beginTime = LocalDateTime.parse("2020-12-28 17:42:20",df);
        endTime = LocalDateTime.parse("2021-01-28 17:42:20",df);
        requestJson = "{\"activityPrice\":\"120\", \"beginTime\":\""+beginTime.toString()+"\",\"endTime\":\""+endTime.toString()+"\",\"quantity\": \"100\"}";
        res = manageClient.post().uri("/shops/1/skus/626/floatPrices").header("authorization",adminToken).bodyValue(requestJson);
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.SKUPRICE_CONFLICT.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 失效不存在的价格浮动项 1217
     * @throws Exception
     */
    @Test
    public void invalidFloatPrice1() throws Exception{
        adminToken = adminLogin("13088admin", "123456");
//        userToken = userLogin("8606245097", "123456");
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.delete().uri("/shops/0/floatPrices/1217").header("authorization",adminToken);
        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 失效价格浮动项
     * @throws Exception
     */
    @Test
    public void invalidFloatPrice2() throws Exception{
        adminToken = adminLogin("13088admin", "123456");
//        userToken = userLogin("8606245097", "123456");
        byte[] responseBuffer = null;
        WebTestClient.RequestHeadersSpec res = manageClient.delete().uri("/shops/1/floatPrices/828").header("authorization",adminToken);
        //增加
        responseBuffer = res.exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").doesNotExist()
                .returnResult()
                .getResponseBodyContent();
    }
}

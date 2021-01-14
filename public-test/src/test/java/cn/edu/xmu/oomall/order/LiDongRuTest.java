package cn.edu.xmu.oomall.order;

import cn.edu.xmu.ooad.Application;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.oomall.LoginVo;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import org.junit.jupiter.api.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * @author 李东儒 24320182203222
 * create 2020/12/15 22:12
 * 测试用户：id为356，用户名3835711724，密码123456
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LiDongRuTest {

	@Value("${public-test.managementgate}")
	private String managementGate;

	@Value("${public-test.mallgate}")
	private String mallGate;

	private String consigneeAddressVoString;
	private static JSONObject normalUnpaidNewOrder;
	private static JSONObject grouponUnpaidNewOrder;
	private static JSONObject presaleUnpaidNewOrder;
	private static JSONObject presaleFinalPaymentUnpaidOrder;
	private static JSONObject normalPaidFinishedOrder;
	private static JSONObject grouponPaidFinishedOrder;
	private static JSONObject presalePaidFinishedOrder;
	private static JSONObject grouponGroupingOrder;
	private static JSONObject grouponGroupFailedOrder;
	private static JSONObject normalDeliveredOrder;
	private static JSONObject grouponDeliveredOrder;
	private static JSONObject presaleDeliveredOrder;
	private static JSONObject normalFinishedOrder;
	private static JSONObject grouponFinishedOrder;
	private static JSONObject presaleFinishedOrder;
	private static JSONObject normalCanceledOrder;
	private static JSONObject grouponCanceledOrder;
	private static JSONObject presaleCanceledOrder;
	private static JSONObject normalDeliveredOrder2;

	private WebTestClient manageClient;

	private WebTestClient mallClient;
	@BeforeEach
	public void setUp() throws JSONException {

		this.manageClient = WebTestClient.bindToServer()
				.baseUrl("http://"+managementGate)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
				.build();

		this.mallClient = WebTestClient.bindToServer()
				.baseUrl("http://"+mallGate)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
				.build();

		JSONObject testVo = new JSONObject();
		testVo.put("consignee", "陈");
		testVo.put("mobile", "18500000000");
		testVo.put("regionId", 1L);
		testVo.put("address", "福建省厦门市思明区厦大学生公寓");
		this.consigneeAddressVoString = JacksonUtil.toJson(testVo);
		initialize();
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
	 * 买家查询订单完整信息（普通，团购，预售） 成功
	 * @author snow
	 * create 2020/12/03 15:09
	 * modified 2020/12/03 17:25
	 * modified 2020/12/15 11:26
	 */
	@Test
	@Order(1)
	public void getOrderCompleteInfoSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68050L;
		String getURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject customerInfo = new JSONObject();
			customerInfo.put("id", 356);
			customerInfo.put("userName", null);
			customerInfo.put("realName", null);
			JSONObject orderItemInfo1 = new JSONObject();
			orderItemInfo1.put("skuId", 680);
			orderItemInfo1.put("orderId", 68050);
			orderItemInfo1.put("name", null);
			orderItemInfo1.put("quantity", 2);
			orderItemInfo1.put("price", 87800);
			JSONObject orderItemInfo2 = new JSONObject();
			orderItemInfo2.put("skuId", 681);
			orderItemInfo2.put("orderId", 68050);
			orderItemInfo2.put("name", null);
			orderItemInfo2.put("quantity", 3);
			orderItemInfo2.put("price", 99900);
			JSONObject orderItemInfo3 = new JSONObject();
			orderItemInfo3.put("skuId", 682);
			orderItemInfo3.put("orderId", 68050);
			orderItemInfo3.put("name", null);
			orderItemInfo3.put("quantity", 4);
			orderItemInfo3.put("price", 12340);
			JSONArray orderItemsInfo = new JSONArray();
			orderItemsInfo.add(orderItemInfo1);
			orderItemsInfo.add(orderItemInfo2);
			orderItemsInfo.add(orderItemInfo3);
			JSONObject responseData = new JSONObject();
			responseData.put("id", 68050);
			responseData.put("shop", null);
			responseData.put("pid", null);
			responseData.put("orderType", 0);
			responseData.put("state", 1);
			responseData.put("subState", 11);
			responseData.put("originPrice", null);
			responseData.put("discountPrice", null);
			responseData.put("freightPrice", null);
			responseData.put("regionId", null);
			responseData.put("couponId", null);
			responseData.put("grouponDiscount", null);
			responseData.put("message", null);
			responseData.put("consignee", "李");
			responseData.put("address", null);
			responseData.put("shop", null);
			responseData.put("customer", customerInfo);
			responseData.put("orderItems", orderItemsInfo);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（不属于自己的）订单完整信息（普通，团购，预售） 操作的资源id不是自己的对象
	 * @author snow
	 * create 2020/12/15 11:30
	 */
	@Test
	@Order(2)
	public void getOrderCompleteInfoIdOutScope() throws Exception {
		String token = this.login("98970287664", "123456");
		Long orderId = 68050L;
		String getURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isForbidden()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 505);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（不存在的）订单完整信息（普通，团购，预售） 操作的资源id不存在
	 * @author snow
	 * create 2020/12/03 17:20
	 * modified 2020/12/15 11:28
	 */
	@Test
	@Order(3)
	public void getOrderCompleteInfoIdNotExist() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 500000L;
		String getURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（已被逻辑删除的）订单完整信息（普通，团购，预售） 操作的资源id不存在
	 * created By snow 2020/12/03 17:21
	 */
	@Test
	@Order(4)
	public void getOrderCompleteInfoOrderBeenDeleted() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68051L;
		String getURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（查询条件：页码为1，页号为20页）名下订单（概要） 成功
	 * @author snow
	 * create 2020/12/15 15:30
	 */
	@Test
	@Order(5)
	public void getOrderBriefInfoSucceedPageEqual1PageSizEqual20() throws Exception {
		String token = login("3835711724", "123456");
		String getURL = "/orders?page=1&pageSize=20";
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONArray orderList = new JSONArray();
			orderList.add(normalUnpaidNewOrder);
			orderList.add(grouponUnpaidNewOrder);
			orderList.add(presaleUnpaidNewOrder);
			orderList.add(presaleFinalPaymentUnpaidOrder);
			orderList.add(normalPaidFinishedOrder);
			orderList.add(grouponPaidFinishedOrder);
			orderList.add(presalePaidFinishedOrder);
			orderList.add(grouponGroupingOrder);
			orderList.add(grouponGroupFailedOrder);
			orderList.add(normalDeliveredOrder);
			orderList.add(grouponDeliveredOrder);
			orderList.add(presaleDeliveredOrder);
			orderList.add(normalFinishedOrder);
			orderList.add(grouponFinishedOrder);
			orderList.add(presaleFinishedOrder);
			orderList.add(normalCanceledOrder);
			orderList.add(grouponCanceledOrder);
			orderList.add(presaleCanceledOrder);
			orderList.add(normalDeliveredOrder2);
			JSONObject responseData = new JSONObject();
			responseData.put("total", 19);
			responseData.put("pages", 1);
			responseData.put("pageSize", 20);
			responseData.put("page", 1);
			responseData.put("list", orderList);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（查询条件：页码为1，页号为5页）名下订单（概要） 成功
	 * @author snow
	 * create 2020/12/15 15:40
	 */
	@Test
	@Order(6)
	public void getOrderBriefInfoSucceedPageEqual1PageSizEqual5() throws Exception {
		String token = login("3835711724", "123456");
		String getURL = "/orders?page=1&pageSize=5";
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONArray orderList = new JSONArray();
			orderList.add(normalUnpaidNewOrder);
			orderList.add(grouponUnpaidNewOrder);
			orderList.add(presaleUnpaidNewOrder);
			orderList.add(presaleFinalPaymentUnpaidOrder);
			orderList.add(normalPaidFinishedOrder);
			JSONObject responseData = new JSONObject();
			responseData.put("total", 19);
			responseData.put("pages", 4);
			responseData.put("pageSize", 5);
			responseData.put("page", 1);
			responseData.put("list", orderList);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（查询条件：页码为2，页号为5页）名下订单（概要） 成功
	 * @author snow
	 * create 2020/12/15 15:41
	 */
	@Test
	@Order(7)
	public void getOrderBriefInfoSucceedPageEqual2PageSizEqual5() throws Exception {
		String token = login("3835711724", "123456");
		String getURL = "/orders?page=2&pageSize=5";
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONArray orderList = new JSONArray();
			orderList.add(grouponPaidFinishedOrder);
			orderList.add(presalePaidFinishedOrder);
			orderList.add(grouponGroupingOrder);
			orderList.add(grouponGroupFailedOrder);
			orderList.add(normalDeliveredOrder);
			JSONObject responseData = new JSONObject();
			responseData.put("total", 19);
			responseData.put("pages", 4);
			responseData.put("pageSize", 5);
			responseData.put("page", 2);
			responseData.put("list", orderList);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（查询条件：开始时间为2020-12-09 00:00:00）名下订单（概要） 成功
	 * @author snow
	 * create 2020/12/15 15:50
	 */
	@Test
	@Order(8)
	public void getOrderBriefInfoSucceedBeginTimeEquals20201209000000() throws Exception {
		String token = login("3835711724", "123456");
		String getURL = "/orders?page=1&pageSize=5&beginTime=2020-12-09T00:00:00";
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONArray orderList = new JSONArray();
			orderList.add(grouponGroupFailedOrder);
			orderList.add(normalDeliveredOrder);
			orderList.add(grouponDeliveredOrder);
			orderList.add(presaleDeliveredOrder);
			orderList.add(normalFinishedOrder);
			JSONObject responseData = new JSONObject();
			responseData.put("total", 11);
			responseData.put("pages", 3);
			responseData.put("pageSize", 5);
			responseData.put("page", 1);
			responseData.put("list", orderList);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（查询条件：开始时间为2020-12-11 00:00:00，结束时间为2020-12-13 00:00:00）名下订单（概要） 成功
	 * @author snow
	 * create 2020/12/15 16:06
	 */
	@Test
	@Order(9)
	public void getOrderBriefInfoSucceedBeginTimeEquals20201211000000AndEndTimeEquals20201214000000() throws Exception {
		String token = login("3835711724", "123456");
		String getURL = "/orders?page=1&pageSize=5&beginTime=2020-12-11T00:00:00&endTime=2020-12-13T00:00:00";
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONArray orderList = new JSONArray();
			orderList.add(normalFinishedOrder);
			orderList.add(grouponFinishedOrder);
			orderList.add(presaleFinishedOrder);
			orderList.add(normalCanceledOrder);
			JSONObject responseData = new JSONObject();
			responseData.put("total", 4);
			responseData.put("pages", 1);
			responseData.put("pageSize", 5);
			responseData.put("page", 1);
			responseData.put("list", orderList);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（查询条件：订单编号为2020022038350）名下订单（概要） 成功
	 * @author snow
	 * create 2020/12/15 16:15
	 */
	@Test
	@Order(10)
	public void getOrderBriefInfoSucceedOrderSnEquals2020022038350() throws Exception {
		String token = login("3835711724", "123456");
		String getURL = "/orders?page=1&pageSize=5&orderSn=2020022038350";
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONArray orderList = new JSONArray();
			orderList.add(normalUnpaidNewOrder);
			JSONObject responseData = new JSONObject();
			responseData.put("total", 1);
			responseData.put("pages", 1);
			responseData.put("pageSize", 5);
			responseData.put("page", 1);
			responseData.put("list", orderList);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（查询条件：订单状态为待收货，页码为1）名下订单（概要） 成功
	 * @author snow
	 * create 2020/12/15 16:15
	 */
	@Test
	@Order(11)
	public void getOrderBriefInfoSucceedOrderStatusEqualsWaitForReceivingPageEquals1() throws Exception {
		String token = login("3835711724", "123456");
		String getURL = "/orders?page=1&pageSize=5&state=2";
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONArray orderList = new JSONArray();
			orderList.add(normalPaidFinishedOrder);
			orderList.add(grouponPaidFinishedOrder);
			orderList.add(presalePaidFinishedOrder);
			orderList.add(grouponGroupingOrder);
			orderList.add(grouponGroupFailedOrder);
			JSONObject responseData = new JSONObject();
			responseData.put("total", 9);
			responseData.put("pages", 2);
			responseData.put("pageSize", 5);
			responseData.put("page", 1);
			responseData.put("list", orderList);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家查询（查询条件：订单状态为待收货，页码为2）名下订单（概要） 成功
	 * @author snow
	 * create 2020/12/15 16:20
	 */
	@Test
	@Order(12)
	public void getOrderBriefInfoSucceedOrderStatusEqualsWaitForReceivingPageEquals2() throws Exception {
		String token = login("3835711724", "123456");
		String getURL = "/orders?page=2&pageSize=5&state=2";
		try {
			byte[] responseString = mallClient.get().uri(getURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONArray orderList = new JSONArray();
			orderList.add(normalDeliveredOrder);
			orderList.add(grouponDeliveredOrder);
			orderList.add(presaleDeliveredOrder);
			orderList.add(normalDeliveredOrder2);
			JSONObject responseData = new JSONObject();
			responseData.put("total", 9);
			responseData.put("pages", 2);
			responseData.put("pageSize", 5);
			responseData.put("page", 2);
			responseData.put("list", orderList);
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			response.put("data", responseData);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（已发货订单）确认收货 成功
	 * @author snow
	 * create 2020/12/02 09:20
	 * modified 2020/12/02 20:16
	 */
	@Test
	@Order(13)
	public void confirmOrderReceivedSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68068L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（不属于自己的订单）确认收货 操作的资源id不是自己的对象
	 * @author snow
	 * create 2020/12/14 10:39
	 */
	@Test
	@Order(14)
	public void confirmOrderReceivedIdOutScope() throws Exception {
		String token = this.login("98970287664", "123456");
		Long orderId = 68086L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isForbidden()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 505);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（处于新订单状态的订单）确认收货 订单状态禁止
	 * @author snow
	 * create 2020/12/02 09:20
	 * modified 2020/12/02 20:16
	 * modified 2020/12/14 10:44
	 */
	@Test
	@Order(15)
	public void confirmOrderReceivedStatusForbiddenNewOrder() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68050L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（处于待支付尾款状态的订单）确认收货 订单状态禁止
	 * @author snow
	 * create 2020/12/14 10:50
	 */
	@Test
	@Order(16)
	public void confirmOrderReceivedStatusForbiddenFinalPaymentUnpaid() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68056L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（处于付款完成状态的订单）确认收货 订单状态禁止
	 * @author snow
	 * create 2020/12/14 10:52
	 */
	@Test
	@Order(17)
	public void confirmOrderReceivedStatusForbiddenPaidFinished() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68058L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（处于待成团状态的订单）确认收货 订单状态禁止
	 * @author snow
	 * create 2020/12/14 10:53
	 */
	@Test
	@Order(18)
	public void confirmOrderReceivedStatusForbiddenGrouping() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68064L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（处于未成团状态的订单）确认收货 订单状态禁止
	 * @author snow
	 * create 2020/12/14 10:54
	 */
	@Test
	@Order(19)
	public void confirmOrderReceivedStatusForbiddenGroupFailed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68066L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（不存在的订单）确认收货 操作的资源id不存在
	 * @author snow
	 * create 2020/12/02 10:06
	 * modified 2020/12/02 20:16
	 * modified 2020/12/14 10:56
	 */
	@Test
	@Order(20)
	public void confirmOrderReceivedOrderIdNotExist() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 500000L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家标记（已被逻辑删除的订单）确认收货 操作的资源id不存在
	 * created By snow 2020/12/02 15:16
	 *  modified by snow 2020/12/02 20:17
	 */
	@Test
	@Order(21)
	public void confirmOrderReceivedOrderBeenDeleted() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68069L;
		String putURL = "/orders/" + orderId + "/confirm";
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（处于新订单状态的）订单 成功
	 * @author snow
	 * create 2020/12/02 13:14
	 * modified 2020/12/14 11:20
	 */
	@Test
	@Order(22)
	public void alertOrderAddressNewOrderStatusSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68050L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（处于待支付尾款状态的）订单 成功
	 * @author snow
	 * create 2020/12/02 13:21
	 * modified 2020/12/14 11:24
	 */
	@Test
	@Order(23)
	public void alertOrderAddressFinalPaymentUnpaidStatusSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68054L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（处于付款完成状态的）订单 成功
	 * @author snow
	 * create 2020/12/02 13:22
	 * modified 2020/12/14 11:25
	 */
	@Test
	@Order(24)
	public void alertOrderAddressPaidFinishedStatusSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68058L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（处于待成团状态的）订单 成功
	 * @author snow
	 * create 2020/12/14 11:26
	 */
	@Test
	@Order(25)
	public void alertOrderAddressGroupingStatusStatusSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68064L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（处于未成团状态的）订单 成功
	 * @author snow
	 * create 2020/12/14 11:27
	 */
	@Test
	@Order(26)
	public void alertOrderAddressGroupFailedStatusSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68066L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（处于已发货状态的）订单 订单状态禁止
	 * @author snow
	 * create 2020/12/02 13:22
	 * modified 2020/12/02 20:18
	 * modified 2020/12/14 11:28
	 */
	@Test
	@Order(27)
	public void alertOrderAddressStatusForbiddenDelivered() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68086L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（处于已完成状态的）订单 订单状态禁止
	 * @author snow
	 * create 2020/12/14 11:33
	 */
	@Test
	@Order(28)
	public void alertOrderAddressStatusForbiddenOrderFinished() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68074L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（处于已取消状态的）订单 订单状态禁止
	 * @author snow
	 * create 2020/12/14 11:34
	 */
	@Test
	@Order(29)
	public void alertOrderAddressStatusForbiddenOrderCanceled() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68080L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（订单id不存在的）订单 操作的资源id不存在
	 * @author snow
	 * create 2020/12/02 13:22
	 * modified 2020/12/02 20:19
	 * modified 2020/12/14 11:36
	 */
	@Test
	@Order(30)
	public void alertOrderAddressIdNotExist() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 500000L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（已被逻辑删除的）订单 操作的资源id不存在
	 * @author snow
	 * create 2020/12/02 15:21
	 * modified 2020/12/02 20:19
	 * modified 2020/12/14 11:37
	 */
	@Test
	@Order(31)
	public void alertOrderAddressOrderBeenDeleted() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68051L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(this.consigneeAddressVoString)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家修改名下（不属于自己的）订单 操作的资源id不是自己的对象
	 * @author snow
	 * create 2020/12/15 23:05
	 */
	@Test
	@Order(32)
	public void alertOrderAddressOrderIdOutScope() throws Exception {
		String token = this.login("98970287664", "123456");
		Long orderId = 68050L;
		String putURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.put().uri(putURL).header("authorization", token)
					.bodyValue(consigneeAddressVoString)
					.exchange()
					.expectStatus().isForbidden()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 505);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消（处于新订单状态的）订单 成功
	 * @author snow
	 * create 2020/12/02 15:50
	 * modified 2020/12/14 16:04
	 */
	@Test
	@Order(33)
	public void cancelOrderNewStatusSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68050L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消（处于付款完成状态的普通）订单 成功
	 * @author snow
	 * create 2020/12/02 15:53
	 * modified 2020/12/14 16:07
	 */
	@Test
	@Order(34)
	public void cancelOrderPaidFinishedStatusNormalOrderSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68058L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消（处于付款完成状态的团购）订单 成功
	 * @author snow
	 * create 2020/12/02 15:54
	 * modified 2020/12/14 16:09
	 */
	@Test
	@Order(35)
	public void cancelOrderPaidFinishedStatusGrouponOrderSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68060L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消（处于待成团状态的团购）订单 成功
	 * @author snow
	 * create 2020/12/14 16:11
	 */
	@Test
	@Order(36)
	public void cancelOrderGroupingStatusGrouponOrderSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68064L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消（处于未成团状态的团购）订单 成功
	 * @author snow
	 * create 2020/12/14 16:12
	 */
	@Test
	@Order(37)
	public void cancelOrderGroupFailedStatusGrouponOrderSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68066L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消（处于待支付尾款状态的预售）订单 成功
	 * @author snow
	 * create 2020/12/02 15:57
	 * modified 2020/12/02 20:21
	 * modified 2020/12/14 16:16
	 * modified 2020/12/17 20:57
	 */
	@Test
	@Order(38)
	public void cancelOrderFinalPaymentUnpaidStatusPresaleOrderSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68056L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消（处于已发货状态的）订单 订单状态禁止
	 * @author snow
	 * create 2020/12/14 16:19
	 */
	@Test
	@Order(39)
	public void cancelOrderStatusForbiddenDelivered() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68086L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家逻辑删除已完成订单 成功
	 * @author snow
	 * create 2020/12/02 15:55
	 * modified 2020/12/14 16:13
	 */
	@Test
	@Order(40)
	public void deleteOrderFinishedStatusSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68074L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家逻辑删除已取消订单 成功
	 * @author snow
	 * create 2020/12/14 16:24
	 */
	@Test
	@Order(41)
	public void deleteOrderCanceledStatusSucceed() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68080L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 0);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消/逻辑删除（订单id不存在的）订单 操作的资源id不存在
	 * @author snow
	 * create 2020/12/02 15:57
	 * modified 2020/12/02 20:21
	 * modified 2020/12/14 16:20
	 */
	@Test
	@Order(42)
	public void cancelOrDeleteOrderIdNotExist() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 500000L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消/逻辑删除（已被逻辑删除的）订单 操作的资源id不存在
	 * @author snow
	 * create 2020/12/02 15:57
	 * modified 2020/12/02 20:21
	 * modified 2020/12/14 16:23
	 */
	@Test
	@Order(43)
	public void cancelOrDeleteOrderBeenDeleted() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68073L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家取消/逻辑删除（已被逻辑删除的）订单 操作的资源id不是自己的对象
	 * @author snow
	 * create 2020/12/15 23:10
	 */
	@Test
	@Order(44)
	public void cancelOrDeleteOrderIdOutScope() throws Exception {
		String token = this.login("98970287664", "123456");
		Long orderId = 68050L;
		String deleteURL = "/orders/" + orderId;
		try {
			byte[] responseString = mallClient.delete().uri(deleteURL).header("authorization", token)
					.exchange()
					.expectStatus().isForbidden()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 505);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家将（处于新订单状态的）团购订单转化普通订单 订单状态禁止
	 * @author snow
	 * create 2020/12/02 20:02
	 * modified 2020/12/15 10:49
	 * modified 2020/12/18 15:55
	 */
	@Test
	@Order(45)
	public void transferGrouponOrderToNormalOrderStatusForbiddenNewOrder() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68052L;
		String postURL = "/orders/" + orderId + "/groupon-normal";
		try {
			byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家将（已成团的）团购订单转化普通订单 订单状态禁止
	 * @author snow
	 * create 2020/12/02 20:06
	 * modified 2020/12/15 10:36
	 */
	@Test
	@Order(46)
	public void transferGrouponOrderToNormalOrderStatusForbiddenPaidFinished() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68060L;
		String postURL = "/orders/" + orderId + "/groupon-normal";
		try {
			byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家将普通订单转化普通订单 订单状态禁止
	 * @author snow
	 * create 2020/12/02 20:10
	 * modified 2020/12/15 10:38
	 */
	@Test
	@Order(47)
	public void transferNormalOrderToNormalOrderStatusForbidden() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68058L;
		String postURL = "/orders/" + orderId + "/groupon-normal";
		try {
			byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家将团购订单转化普通订单 订单状态禁止
	 * @author snow
	 * create 2020/12/15 10:39
	 */
	@Test
	@Order(48)
	public void transferPresaleOrderToNormalOrderStatusForbidden() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68062L;
		String postURL = "/orders/" + orderId + "/groupon-normal";
		try {
			byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
					.exchange()
					.expectStatus().isOk()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 801);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家将（不存在的）团购订单转化普通订单 操作的资源id不存在
	 * @author snow
	 * create 2020/12/02 20:11
	 * modified 2020/12/15 10:39
	 */
	@Test
	@Order(49)
	public void transferGrouponOrderToNormalOrderIdNotExist() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 500000L;
		String postURL = "/orders/" + orderId + "/groupon-normal";
		try {
			byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家将（被逻辑删除团购的）订单转化普通订单 操作的资源id不存在
	 * @author snow
	 * create 2020/12/02 20:14
	 * modified 2020/12/15 10:40
	 */
	@Test
	@Order(50)
	public void transferGrouponOrderToNormalOrderBeenDeleted() throws Exception {
		String token = this.login("3835711724", "123456");
		Long orderId = 68065L;
		String postURL = "/orders/" + orderId + "/groupon-normal";
		try {
			byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
					.exchange()
					.expectStatus().isNotFound()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 504);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 买家将（被逻辑删除团购的）订单转化普通订单 操作的资源id不是自己的对象
	 * @author snow
	 * create 2020/12/15 23:12
	 */
	@Test
	@Order(51)
	public void transferGrouponOrderToNormalOrderIdOutScope() throws Exception {
		String token = this.login("98970287664", "123456");
		Long orderId = 68064L;
		String postURL = "/orders/" + orderId + "/groupon-normal";
		try {
			byte[] responseString = mallClient.post().uri(postURL).header("authorization", token)
					.exchange()
					.expectStatus().isForbidden()
					.expectBody()
					.jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
					.returnResult()
					.getResponseBodyContent();
			JSONObject response = new JSONObject();
			response.put("errno", 505);
			String expectedResponse = response.toString();
			JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initialize() throws JSONException {

		normalUnpaidNewOrder = new JSONObject();
		normalUnpaidNewOrder.put("id", 68050);
		normalUnpaidNewOrder.put("customerId", 356);
		normalUnpaidNewOrder.put("orderType", 0);
		normalUnpaidNewOrder.put("state", 1);
		normalUnpaidNewOrder.put("subState", 11);
		normalUnpaidNewOrder.put("shopId", 1);
		normalUnpaidNewOrder.put("pid", null);
		normalUnpaidNewOrder.put("originalPrice", null);
		normalUnpaidNewOrder.put("discountPrice", null);
		normalUnpaidNewOrder.put("freightPrice", null);
		normalUnpaidNewOrder.put("grouponId", null);
		normalUnpaidNewOrder.put("presaleId", null);

		grouponUnpaidNewOrder = new JSONObject();
		grouponUnpaidNewOrder.put("id", 68052);
		grouponUnpaidNewOrder.put("customerId", 356);
		grouponUnpaidNewOrder.put("orderType", 1);
		grouponUnpaidNewOrder.put("state", 1);
		grouponUnpaidNewOrder.put("subState", 11);
		grouponUnpaidNewOrder.put("shopId", 1);
		grouponUnpaidNewOrder.put("pid", null);
		grouponUnpaidNewOrder.put("originalPrice", null);
		grouponUnpaidNewOrder.put("discountPrice", null);
		grouponUnpaidNewOrder.put("freightPrice", null);
		grouponUnpaidNewOrder.put("grouponId", null);
		grouponUnpaidNewOrder.put("presaleId", null);

		presaleUnpaidNewOrder = new JSONObject();
		presaleUnpaidNewOrder.put("id", 68054);
		presaleUnpaidNewOrder.put("customerId", 356);
		presaleUnpaidNewOrder.put("orderType", 2);
		presaleUnpaidNewOrder.put("state", 1);
		presaleUnpaidNewOrder.put("subState", 11);
		presaleUnpaidNewOrder.put("shopId", 1);
		presaleUnpaidNewOrder.put("pid", null);
		presaleUnpaidNewOrder.put("originalPrice", null);
		presaleUnpaidNewOrder.put("discountPrice", null);
		presaleUnpaidNewOrder.put("freightPrice", null);
		presaleUnpaidNewOrder.put("grouponId", null);
		presaleUnpaidNewOrder.put("presaleId", null);

		presaleFinalPaymentUnpaidOrder = new JSONObject();
		presaleFinalPaymentUnpaidOrder.put("id", 68056);
		presaleFinalPaymentUnpaidOrder.put("customerId", 356);
		presaleFinalPaymentUnpaidOrder.put("orderType", 2);
		presaleFinalPaymentUnpaidOrder.put("state", 1);
		presaleFinalPaymentUnpaidOrder.put("subState", 12);
		presaleFinalPaymentUnpaidOrder.put("shopId", 1);
		presaleFinalPaymentUnpaidOrder.put("pid", null);
		presaleFinalPaymentUnpaidOrder.put("originalPrice", null);
		presaleFinalPaymentUnpaidOrder.put("discountPrice", null);
		presaleFinalPaymentUnpaidOrder.put("freightPrice", null);
		presaleFinalPaymentUnpaidOrder.put("grouponId", null);
		presaleFinalPaymentUnpaidOrder.put("presaleId", null);

		normalPaidFinishedOrder = new JSONObject();
		normalPaidFinishedOrder.put("id", 68058);
		normalPaidFinishedOrder.put("customerId", 356);
		normalPaidFinishedOrder.put("orderType", 0);
		normalPaidFinishedOrder.put("state", 2);
		normalPaidFinishedOrder.put("subState", 21);
		normalPaidFinishedOrder.put("shopId", 1);
		normalPaidFinishedOrder.put("pid", null);
		normalPaidFinishedOrder.put("originalPrice", null);
		normalPaidFinishedOrder.put("discountPrice", null);
		normalPaidFinishedOrder.put("freightPrice", null);
		normalPaidFinishedOrder.put("grouponId", null);
		normalPaidFinishedOrder.put("presaleId", null);

		grouponPaidFinishedOrder = new JSONObject();
		grouponPaidFinishedOrder.put("id", 68060);
		grouponPaidFinishedOrder.put("customerId", 356);
		grouponPaidFinishedOrder.put("orderType", 1);
		grouponPaidFinishedOrder.put("state", 2);
		grouponPaidFinishedOrder.put("subState", 21);
		grouponPaidFinishedOrder.put("shopId", 1);
		grouponPaidFinishedOrder.put("pid", null);
		grouponPaidFinishedOrder.put("originalPrice", null);
		grouponPaidFinishedOrder.put("discountPrice", null);
		grouponPaidFinishedOrder.put("freightPrice", null);
		grouponPaidFinishedOrder.put("grouponId", null);
		grouponPaidFinishedOrder.put("presaleId", null);

		presalePaidFinishedOrder = new JSONObject();
		presalePaidFinishedOrder.put("id", 68062);
		presalePaidFinishedOrder.put("customerId", 356);
		presalePaidFinishedOrder.put("orderType", 2);
		presalePaidFinishedOrder.put("state", 2);
		presalePaidFinishedOrder.put("subState", 21);
		presalePaidFinishedOrder.put("shopId", 1);
		presalePaidFinishedOrder.put("pid", null);
		presalePaidFinishedOrder.put("originalPrice", null);
		presalePaidFinishedOrder.put("discountPrice", null);
		presalePaidFinishedOrder.put("freightPrice", null);
		presalePaidFinishedOrder.put("grouponId", null);
		presalePaidFinishedOrder.put("presaleId", null);

		grouponGroupingOrder = new JSONObject();
		grouponGroupingOrder.put("id", 68064);
		grouponGroupingOrder.put("customerId", 356);
		grouponGroupingOrder.put("orderType", 1);
		grouponGroupingOrder.put("state", 2);
		grouponGroupingOrder.put("subState", 22);
		grouponGroupingOrder.put("shopId", 1);
		grouponGroupingOrder.put("pid", null);
		grouponGroupingOrder.put("originalPrice", null);
		grouponGroupingOrder.put("discountPrice", null);
		grouponGroupingOrder.put("freightPrice", null);
		grouponGroupingOrder.put("grouponId", null);
		grouponGroupingOrder.put("presaleId", null);

		grouponGroupFailedOrder = new JSONObject();
		grouponGroupFailedOrder.put("id", 68066);
		grouponGroupFailedOrder.put("customerId", 356);
		grouponGroupFailedOrder.put("orderType", 1);
		grouponGroupFailedOrder.put("state", 2);
		grouponGroupFailedOrder.put("subState", 23);
		grouponGroupFailedOrder.put("shopId", 1);
		grouponGroupFailedOrder.put("pid", null);
		grouponGroupFailedOrder.put("originalPrice", null);
		grouponGroupFailedOrder.put("discountPrice", null);
		grouponGroupFailedOrder.put("freightPrice", null);
		grouponGroupFailedOrder.put("grouponId", null);
		grouponGroupFailedOrder.put("presaleId", null);

		normalDeliveredOrder = new JSONObject();
		normalDeliveredOrder.put("id", 68068);
		normalDeliveredOrder.put("customerId", 356);
		normalDeliveredOrder.put("orderType", 0);
		normalDeliveredOrder.put("state", 2);
		normalDeliveredOrder.put("subState", 24);
		normalDeliveredOrder.put("shopId", 1);
		normalDeliveredOrder.put("pid", null);
		normalDeliveredOrder.put("originalPrice", null);
		normalDeliveredOrder.put("discountPrice", null);
		normalDeliveredOrder.put("freightPrice", null);
		normalDeliveredOrder.put("grouponId", null);
		normalDeliveredOrder.put("presaleId", null);

		presaleDeliveredOrder = new JSONObject();
		presaleDeliveredOrder.put("id", 68070);
		presaleDeliveredOrder.put("customerId", 356);
		presaleDeliveredOrder.put("orderType", 1);
		presaleDeliveredOrder.put("state", 2);
		presaleDeliveredOrder.put("subState", 24);
		presaleDeliveredOrder.put("shopId", 1);
		presaleDeliveredOrder.put("pid", null);
		presaleDeliveredOrder.put("originalPrice", null);
		presaleDeliveredOrder.put("discountPrice", null);
		presaleDeliveredOrder.put("freightPrice", null);
		presaleDeliveredOrder.put("grouponId", null);
		presaleDeliveredOrder.put("presaleId", null);

		grouponDeliveredOrder = new JSONObject();
		grouponDeliveredOrder.put("id", 68072);
		grouponDeliveredOrder.put("customerId", 356);
		grouponDeliveredOrder.put("orderType", 2);
		grouponDeliveredOrder.put("state", 2);
		grouponDeliveredOrder.put("subState", 24);
		grouponDeliveredOrder.put("shopId", 1);
		grouponDeliveredOrder.put("pid", null);
		grouponDeliveredOrder.put("originalPrice", null);
		grouponDeliveredOrder.put("discountPrice", null);
		grouponDeliveredOrder.put("freightPrice", null);
		grouponDeliveredOrder.put("grouponId", null);
		grouponDeliveredOrder.put("presaleId", null);

		normalFinishedOrder = new JSONObject();
		normalFinishedOrder.put("id", 68074);
		normalFinishedOrder.put("customerId", 356);
		normalFinishedOrder.put("orderType", 0);
		normalFinishedOrder.put("state", 3);
		normalFinishedOrder.put("subState", null);
		normalFinishedOrder.put("shopId", 1);
		normalFinishedOrder.put("pid", null);
		normalFinishedOrder.put("originalPrice", null);
		normalFinishedOrder.put("discountPrice", null);
		normalFinishedOrder.put("freightPrice", null);
		normalFinishedOrder.put("grouponId", null);
		normalFinishedOrder.put("presaleId", null);

		grouponFinishedOrder = new JSONObject();
		grouponFinishedOrder.put("id", 68076);
		grouponFinishedOrder.put("customerId", 356);
		grouponFinishedOrder.put("orderType", 1);
		grouponFinishedOrder.put("state", 3);
		grouponFinishedOrder.put("subState", null);
		grouponFinishedOrder.put("shopId", 1);
		grouponFinishedOrder.put("pid", null);
		grouponFinishedOrder.put("originalPrice", null);
		grouponFinishedOrder.put("discountPrice", null);
		grouponFinishedOrder.put("freightPrice", null);
		grouponFinishedOrder.put("grouponId", null);
		grouponFinishedOrder.put("presaleId", null);

		presaleFinishedOrder = new JSONObject();
		presaleFinishedOrder.put("id", 68078);
		presaleFinishedOrder.put("customerId", 356);
		presaleFinishedOrder.put("orderType", 2);
		presaleFinishedOrder.put("state", 3);
		presaleFinishedOrder.put("subState", null);
		presaleFinishedOrder.put("shopId", 1);
		presaleFinishedOrder.put("pid", null);
		presaleFinishedOrder.put("originalPrice", null);
		presaleFinishedOrder.put("discountPrice", null);
		presaleFinishedOrder.put("freightPrice", null);
		presaleFinishedOrder.put("grouponId", null);
		presaleFinishedOrder.put("presaleId", null);

		normalCanceledOrder = new JSONObject();
		normalCanceledOrder.put("id", 68080);
		normalCanceledOrder.put("customerId", 356);
		normalCanceledOrder.put("orderType", 0);
		normalCanceledOrder.put("state", 4);
		normalCanceledOrder.put("subState", null);
		normalCanceledOrder.put("shopId", 1);
		normalCanceledOrder.put("pid", null);
		normalCanceledOrder.put("originalPrice", null);
		normalCanceledOrder.put("discountPrice", null);
		normalCanceledOrder.put("freightPrice", null);
		normalCanceledOrder.put("grouponId", null);
		normalCanceledOrder.put("presaleId", null);

		grouponCanceledOrder = new JSONObject();
		grouponCanceledOrder.put("id", 68082);
		grouponCanceledOrder.put("customerId", 356);
		grouponCanceledOrder.put("orderType", 1);
		grouponCanceledOrder.put("state", 4);
		grouponCanceledOrder.put("subState", null);
		grouponCanceledOrder.put("shopId", 1);
		grouponCanceledOrder.put("pid", null);
		grouponCanceledOrder.put("originalPrice", null);
		grouponCanceledOrder.put("discountPrice", null);
		grouponCanceledOrder.put("freightPrice", null);
		grouponCanceledOrder.put("grouponId", null);
		grouponCanceledOrder.put("presaleId", null);

		presaleCanceledOrder = new JSONObject();
		presaleCanceledOrder.put("id", 68084);
		presaleCanceledOrder.put("customerId", 356);
		presaleCanceledOrder.put("orderType", 2);
		presaleCanceledOrder.put("state", 4);
		presaleCanceledOrder.put("subState", null);
		presaleCanceledOrder.put("shopId", 1);
		presaleCanceledOrder.put("pid", null);
		presaleCanceledOrder.put("originalPrice", null);
		presaleCanceledOrder.put("discountPrice", null);
		presaleCanceledOrder.put("freightPrice", null);
		presaleCanceledOrder.put("grouponId", null);
		presaleCanceledOrder.put("presaleId", null);

		normalDeliveredOrder2 = new JSONObject();
		normalDeliveredOrder2.put("id", 68086);
		normalDeliveredOrder2.put("customerId", 356);
		normalDeliveredOrder2.put("orderType", 0);
		normalDeliveredOrder2.put("state", 2);
		normalDeliveredOrder2.put("subState", 24);
		normalDeliveredOrder2.put("shopId", 1);
		normalDeliveredOrder2.put("pid", null);
		normalDeliveredOrder2.put("originalPrice", null);
		normalDeliveredOrder2.put("discountPrice", null);
		normalDeliveredOrder2.put("freightPrice", null);
		normalDeliveredOrder2.put("grouponId", null);
		normalDeliveredOrder2.put("presaleId", null);

	}

}

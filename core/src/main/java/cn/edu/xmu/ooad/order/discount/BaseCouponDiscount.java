package cn.edu.xmu.ooad.order.discount;

import cn.edu.xmu.ooad.order.bo.OrderItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseCouponDiscount implements Computable, JsonSerializable {

	public BaseCouponDiscount(BaseCouponLimitation limitation, long value) {
		this.couponLimitation = limitation;
		this.value = value;
		this.className = this.getClass().getName();
	}

	protected long value;

	protected String className;

	protected BaseCouponLimitation couponLimitation;

	@Override
	public List<OrderItem> compute(List<OrderItem> orderItems) {
		if (!couponLimitation.pass(orderItems)) {
			for (OrderItem oi : orderItems) {
				oi.setCouponActivityId(null);
			}
			return orderItems;
		}

		calcAndSetDiscount(orderItems);

		return orderItems;
	}

	public abstract void calcAndSetDiscount(List<OrderItem> orderItems);

	@Override
	public String toJsonString() throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(this);
	}

	public static BaseCouponDiscount getInstance(String jsonString) throws JsonProcessingException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(jsonString);
		String className = root.get("className").asText();
		BaseCouponDiscount bc = (BaseCouponDiscount) Class.forName(className).getConstructor().newInstance();

		String limitation = root.get("couponLimitation").toString();
		BaseCouponLimitation bl = BaseCouponLimitation.getInstance(limitation);

		bc.setCouponLimitation(bl);
		bc.setValue(root.get("value").asLong());
		bc.setClassName(className);

		return bc;
	}
}

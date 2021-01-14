package cn.edu.xmu.ooad.order.discount;

import cn.edu.xmu.ooad.order.bo.OrderItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseCouponLimitation {

	public BaseCouponLimitation(long value) {
		this.value = value;
		this.className = this.getClass().getName();
	}

	protected long value;

	protected String className;

	public abstract boolean pass(List<OrderItem> orderItems);

	public static BaseCouponLimitation getInstance(String jsonString) throws JsonProcessingException, ClassNotFoundException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(jsonString);
		String className = root.get("className").asText();
		return (BaseCouponLimitation) mapper.readValue(jsonString, Class.forName(className));
	}
}

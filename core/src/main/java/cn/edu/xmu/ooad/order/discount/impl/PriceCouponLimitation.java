package cn.edu.xmu.ooad.order.discount.impl;

import cn.edu.xmu.ooad.order.bo.OrderItem;
import cn.edu.xmu.ooad.order.discount.BaseCouponLimitation;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
public class PriceCouponLimitation extends BaseCouponLimitation {

	public PriceCouponLimitation() {

	}

	public PriceCouponLimitation(long value) {
		super(value);
	}

	@Override
	public boolean pass(List<OrderItem> orderItems) {
		long t = 0;
		for (OrderItem oi : orderItems) {
			t += oi.getQuantity() * oi.getPrice();
		}
		return t > value;
	}

}

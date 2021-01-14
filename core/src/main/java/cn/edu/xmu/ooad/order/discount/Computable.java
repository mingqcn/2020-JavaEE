package cn.edu.xmu.ooad.order.discount;

import cn.edu.xmu.ooad.order.bo.OrderItem;

import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-18
 */
public interface Computable {

	List<OrderItem> compute(List<OrderItem> orderItems);
}

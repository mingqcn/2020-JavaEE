package cn.edu.xmu.oomall.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xincong yao
 * @date 2020-12-2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

	private Long id;
	private String name;

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}
}

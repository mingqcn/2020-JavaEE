package cn.edu.xmu.privilege.bloom;

import cn.edu.xmu.ooad.util.bloom.BloomFilterHelper;
import cn.edu.xmu.ooad.util.bloom.RedisBloomFilter;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = PrivilegeServiceApplication.class)
public class RedisBloomFilterTest {

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void test() {
		BloomFilterHelper bloomFilterHelper =  new BloomFilterHelper<>(
				Funnels.longFunnel(),
				1000, 0.03);

		RedisBloomFilter redisBloomFilter = new RedisBloomFilter<>(redisTemplate, bloomFilterHelper);

		int j = 0;
		for (int i = 0; i < 900; i++) {
			redisBloomFilter.addByBloomFilter("bloom", (long) i);
		}
		for (int i = 0; i < 1000; i++) {
			boolean result = redisBloomFilter.includeByBloomFilter("bloom", (long) i);
			if (!result) {
				j++;
			}
		}
		assertEquals( 100, j);
	}
}
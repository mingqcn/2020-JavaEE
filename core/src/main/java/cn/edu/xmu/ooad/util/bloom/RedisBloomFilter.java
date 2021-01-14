package cn.edu.xmu.ooad.util.bloom;

import com.google.common.base.Preconditions;
import org.springframework.data.redis.core.RedisTemplate;


/**
 * @author xincong yao
 * @date 2020-11-4
 */
public class RedisBloomFilter<T> {

	private BloomFilterHelper<T> bloomFilterHelper;

	private RedisTemplate redisTemplate;

	public RedisBloomFilter(RedisTemplate redisTemplate, BloomFilterHelper<T> bloomFilterHelper) {
		this.redisTemplate = redisTemplate;
		this.bloomFilterHelper = bloomFilterHelper;
	}

	/**
	 * 向redis的bitmap中添加值
	 */
	public void addByBloomFilter(String key, T value) {
		validate();
		int[] offset = bloomFilterHelper.murmurHashOffset(value);
		for (int i : offset) {
			redisTemplate.opsForValue().setBit(key, i, true);
		}
	}

	/**
	 * 判断redis的bitmap中是否存在某个值
	 */
	public boolean includeByBloomFilter(String key, T value) {
		validate();
		int[] offset = bloomFilterHelper.murmurHashOffset(value);
		for (int i : offset) {
			if (!redisTemplate.opsForValue().getBit(key, i)) {
				return false;
			}
		}
		return true;
	}

	private void validate() {
		Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper不能为空");
		Preconditions.checkArgument(redisTemplate != null, "redisTemplate不能为空");
	}

}

package common;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	@SuppressWarnings("rawtypes")
	private RedisTemplate redisTemplate;
	
	@Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

	/**
	 * Write cache
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean set(final String key, Object value) {
		boolean result = false;
		try {
			ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Write Cache Setting Ageing Time
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean setEx(final String key, Object value, Long expireTime) {
		boolean result = false;
		try {
			ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
			operations.set(key, value);
			redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Determine whether there is a corresponding value in the cache
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean exists(final String key) {
		return redisTemplate.hasKey(key);
	}

	/**
	 * Read Cache
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object get(final String key) {
		Object result = null;
		ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
		result = operations.get(key);
		return result;
	}

	/**
	 * Delete the corresponding value
	 * 
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public boolean remove(final String key) {
		if (exists(key)) {
			Boolean delete = redisTemplate.delete(key);
			return delete;
		}
		return false;
	}
}
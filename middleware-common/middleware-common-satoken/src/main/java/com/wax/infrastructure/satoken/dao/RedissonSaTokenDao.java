package com.wax.infrastructure.satoken.dao;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import jakarta.annotation.Resource;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonSaTokenDao implements SaTokenDao {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 获取 value，如无返空
     *
     * @param key 键名称
     * @return value
     */
    @Override
    public String get(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    public void set(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    /**
     * 写入 value，并设定存活时间（单位: 秒）
     *
     * @param key     键名称
     * @param value   值
     * @param timeout 数据有效期（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (timeout <= 0) {
            set(key, value);
            return;
        }
        RBucket<String> bucket = redissonClient.getBucket(key);
        bucket.set(value, timeout, TimeUnit.SECONDS); // Sa-Token 超时单位为秒
    }

    /**
     * 更新 value （过期时间不变）
     *
     * @param key   键名称
     * @param value 值
     */
    @Override
    public void update(String key, String value) {
        set(key, value);
    }

    /**
     * 删除 value
     *
     * @param key 键名称
     */
    @Override
    public void delete(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 获取 value 的剩余存活时间（单位: 秒）
     *
     * @param key 指定 key
     * @return 这个 key 的剩余存活时间
     */
    @Override
    public long getTimeout(String key) {
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return bucket.getExpireTime();
        }
        return 0;
    }

    /**
     * 修改 value 的剩余存活时间（单位: 秒）
     *
     * @param key     指定 key
     * @param timeout 过期时间（单位: 秒）
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        if (timeout <= 0) {
            delete(key);
            return;
        }
        RBucket<String> bucket = redissonClient.getBucket(key);
        if (!bucket.isExists()) {
            throw new SaTokenException("更新超时时间失败，key不存在：" + key);
        }
        bucket.expire(timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取 Object，如无返空
     *
     * @param key 键名称
     * @return object
     */
    @Override
    public Object getObject(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return bucket.get();
        }
        return null;
    }

    /**
     * 获取 Object (指定反序列化类型)，如无返空
     *
     * @param key       键名称
     * @param classType
     * @return object
     */
    @Override
    public <T> T getObject(String key, Class<T> classType) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return bucket.get();
        }
        return null;
    }

    /**
     * 写入 Object，并设定存活时间 （单位: 秒）
     *
     * @param key     键名称
     * @param object  值
     * @param timeout 存活时间（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (timeout > 0){
            bucket.set(object, timeout, TimeUnit.SECONDS);
        }
        if (timeout == -1){
            bucket.set(object);
        }
    }

    /**
     * 更新 Object （过期时间不变）
     *
     * @param key    键名称
     * @param object 值
     */
    @Override
    public void updateObject(String key, Object object) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(object);
    }

    /**
     * 删除 Object
     *
     * @param key 键名称
     */
    @Override
    public void deleteObject(String key) {
        redissonClient.getBucket(key).delete();
    }

    /**
     * 获取 Object 的剩余存活时间 （单位: 秒）
     *
     * @param key 指定 key
     * @return 这个 key 的剩余存活时间
     */
    @Override
    public long getObjectTimeout(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (bucket.isExists()) {
            return bucket.getExpireTime();
        }
        return 0;
    }

    /**
     * 修改 Object 的剩余存活时间（单位: 秒）
     *
     * @param key     指定 key
     * @param timeout 剩余存活时间
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        if (!bucket.isExists()) {
            throw new SaTokenException("更新超时时间失败，key不存在：" + key);
        }
        bucket.expire(timeout, TimeUnit.SECONDS);
    }

    /**
     * 获取 SaSession，如无返空
     *
     * @param sessionId sessionId
     * @return SaSession
     */
    @Override
    public SaSession getSession(String sessionId) {
        RBucket<Object> bucket = redissonClient.getBucket(sessionId);
        if (bucket.isExists()) {
            return (SaSession) bucket.get();
        }
        return null;
    }

    /**
     * 写入 SaSession，并设定存活时间（单位: 秒）
     *
     * @param session 要保存的 SaSession 对象
     * @param timeout 过期时间（单位: 秒）
     */
    @Override
    public void setSession(SaSession session, long timeout) {
        RBucket<Object> bucket = redissonClient.getBucket(session.getId());
        if (timeout > 0){
            bucket.set(session, timeout, TimeUnit.SECONDS);
        }
    }

    /**
     * 更新 SaSession
     *
     * @param session 要更新的 SaSession 对象
     */
    @Override
    public void updateSession(SaSession session) {
        RBucket<Object> bucket = redissonClient.getBucket(session.getId());
        bucket.set(session);
    }

    /**
     * 删除 SaSession
     *
     * @param sessionId sessionId
     */
    @Override
    public void deleteSession(String sessionId) {
        redissonClient.getBucket(sessionId).delete();
    }

    /**
     * 获取 SaSession 剩余存活时间（单位: 秒）
     *
     * @param sessionId 指定 SaSession
     * @return 这个 SaSession 的剩余存活时间
     */
    @Override
    public long getSessionTimeout(String sessionId) {
        RBucket<Object> bucket = redissonClient.getBucket(sessionId);
        if (bucket.isExists()) {
            return bucket.getExpireTime();
        }
        return 0;
    }

    /**
     * 修改 SaSession 剩余存活时间（单位: 秒）
     *
     * @param sessionId 指定 SaSession
     * @param timeout   剩余存活时间
     */
    @Override
    public void updateSessionTimeout(String sessionId, long timeout) {
        RBucket<Object> bucket = redissonClient.getBucket(sessionId);
        if (!bucket.isExists()) {
            throw new SaTokenException("更新超时时间失败，key不存在：" + sessionId);
        }
        bucket.expire(timeout, TimeUnit.SECONDS);
    }

    /**
     * 搜索数据
     *
     * @param prefix   前缀
     * @param keyword  关键字
     * @param start    开始处索引
     * @param size     获取数量  (-1代表从 start 处一直取到末尾)
     * @param sortType 排序类型（true=正序，false=反序）
     * @return 查询到的数据集合
     */
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        return List.of();
    }
}
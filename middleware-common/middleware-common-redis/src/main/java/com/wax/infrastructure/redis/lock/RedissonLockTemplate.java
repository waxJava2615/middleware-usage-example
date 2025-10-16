package com.wax.infrastructure.redis.lock;

import com.wax.infrastructure.core.exception.RepeatSubmitException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * @author wax
 * @description: 分布式锁
 */
@Slf4j
public class RedissonLockTemplate {

    private static final String ERROR_MSG = "业务正在处理中,请勿重复提交";

    private final long waitTime = 0;

    private final long leaseTime = -1;

    private final TimeUnit timeUnit = TimeUnit.SECONDS;

    private RedissonClient redissonClient;

    public RedissonLockTemplate(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }


    public <T> T tryLock(String lockName, LockCallback<T> lockCallback) throws Exception {
        return tryLock(lockName, waitTime, leaseTime, timeUnit, lockCallback);
    }


    /**
     * 获取锁
     *
     * @param lockName     所名称
     * @param waitTime     等待时长
     * @param leaseTime    锁时长
     * @param timeUnit     时间单位
     * @param lockCallback 业务逻辑
     * @return
     */
    public <T> T tryLock(String lockName, long waitTime, long leaseTime, TimeUnit timeUnit,
                         LockCallback<T> lockCallback) throws Exception {
        RLock lock = redissonClient.getLock(lockName);
        boolean tryLock = false;
        try {
            tryLock = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (tryLock) {
                log.debug("获取锁,lockName:{}\t 状态: {}\t 线程ID: {}", lock.getName(), lock.isHeldByCurrentThread(), Thread.currentThread().getId());
                return lockCallback.success();
            }
            throw new RepeatSubmitException(ERROR_MSG);
        } catch (Exception e) {
            log.error("分布式锁获取,异常信息: {} ", lockName, e);
            throw e;
        } finally {
            if (tryLock && lock.isHeldByCurrentThread()) {
                log.debug("释放锁,lockName:{}\t{}\t{}", lock.getName(), lock.isHeldByCurrentThread(), Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }


}

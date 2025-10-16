package com.wax.infrastructure.redis.lock;

/**
 * @author wax
 */
public interface LockCallback<T> {
    
    /**
     * 成功获取锁时调用
     */
    public T success() throws Exception;
    
    /**
     * 获取不到锁时调用,用于tryLock的场景
     */
    public default T fail(){
        return null;
    }

}

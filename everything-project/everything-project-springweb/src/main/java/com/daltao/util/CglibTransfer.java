package com.daltao.util;


import com.daltao.exception.ImpossibleException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.sf.cglib.beans.BeanCopier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class CglibTransfer<S, D> extends AbstractTransfer<S, D> {
    private Class<S> srcClass;
    private Class<D> dstClass;
    private BeanCopier copier;

    private static LoadingCache<List<Class>, BeanCopier> cache = CacheBuilder.newBuilder()
            .softValues()
            .concurrencyLevel(1)
            .build(new CacheLoader<List<Class>, BeanCopier>() {
                @Override
                public BeanCopier load(List<Class> key) {
                    return BeanCopier.create(key.get(0), key.get(1), false);
                }
            });

    public CglibTransfer(Class<S> srcClass, Class<D> dstClass) {
        this.srcClass = srcClass;
        this.dstClass = dstClass;
        try {
            copier = cache.get(Arrays.asList(srcClass, dstClass));
        } catch (ExecutionException e) {
            throw new ImpossibleException(e);
        }
    }

    @Override
    protected D transferNotNull(S src, Supplier<D> supplier) {
        D result = supplier.get();
        copier.copy(src, result, null);
        return result;
    }
}

package com.daltao;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry, false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isInterface();
            }

            @Override
            protected void postProcessBeanDefinition(AbstractBeanDefinition definition, String beanName) {
                super.postProcessBeanDefinition(definition, beanName);

                try {
                    final Class<?> cls = Class.forName(definition.getBeanClassName());
                    definition.setInstanceSupplier(() -> {
                        return Proxy.newProxyInstance(cls.getClassLoader(),
                                new Class[]{cls}, new InvocationHandler() {
                                    @Override
                                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                        if (method.getAnnotation(Answer.class) != null) {
                                            return method.getAnnotation(Answer.class).value();
                                        } else {
                                            return cls.getAnnotation(Answer.class).value();
                                        }
                                    }
                                });
                    });
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> metadataReader.getAnnotationMetadata().hasAnnotation(Answer.class.getCanonicalName()));
        scanner.scan("com.daltao");
    }
}

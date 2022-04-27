package com.crscd.cds.spring.netcomm.core;

import com.crscd.cds.spring.netcomm.annotation.NetCommListener;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link NetCommListener} 注解处理类
 * @author zhaole
 * @date 2022-04-24
 */
public class NetCommListenerAnnotationBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private static final int MAX_CONCURRENT_CONSUMERS_LIMIT = Runtime.getRuntime().availableProcessors() * 2;

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(@Nonnull BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@Nonnull final Object bean, @Nonnull final String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        ListenerMethod methodAnnotation = getMethodAnnotation(targetClass);
        if(methodAnnotation != null) {
            processAmazonSQSListener(methodAnnotation.annotations, bean, methodAnnotation.method);
        }
        return bean;
    }

    private void processAmazonSQSListener(NetCommListener annotation, Object bean, Method method) {
        SimpleNetCommListenerContainerFactory containerFactory = beanFactory.getBean("amazonSQSListenerContainerFactory", SimpleNetCommListenerContainerFactory.class);
        Method useMethod = checkProxy(method, bean);
        short type = annotation.type();
        short func = annotation.func();
        Assert.isTrue(type != 0x00, "net comm message handler's type can not be default 0x00");
        Assert.isTrue(func != 0x00, "net comm message handler's func can not be default 0x00");
        SimpleMessageListenerContainer listenerContainer = containerFactory.createListenerContainer(queue, message -> useMethod.invoke(bean, message));
        int consumers = annotation.consumers();
        if(consumers > 1 && consumers <= MAX_CONCURRENT_CONSUMERS_LIMIT) {
            listenerContainer.setMaxConcurrentConsumers(consumers);
        }
        if (listenerContainer instanceof InitializingBean) {
            try {
                ((InitializingBean) listenerContainer).afterPropertiesSet();
            }
            catch (Exception ex) {
                throw new BeanInitializationException("Failed to initialize message listener container", ex);
            }
        }
        listenerContainer.start();

    }

    private ListenerMethod getMethodAnnotation(Class<?> targetClass) {
        final List<ListenerMethod> methodAnnotations = new ArrayList<>();
        ReflectionUtils.doWithMethods(targetClass, method -> {
            ListenerMethod listenerAnnotations = findListenerAnnotation(method);
            if(listenerAnnotations != null) {
                methodAnnotations.add(listenerAnnotations);
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);
        if (methodAnnotations.isEmpty() && methodAnnotations.isEmpty()) {
            return null;
        }
        return methodAnnotations.get(0);
    }

    private ListenerMethod findListenerAnnotation(Method method) {
        AmazonSQSListener ann = AnnotationUtils.findAnnotation(method, AmazonSQSListener.class);
        if(ann == null) {
            return null;
        }
        return new ListenerMethod(method, ann);
    }

    private Method checkProxy(Method methodArg, Object bean) {
        Method method = methodArg;
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                // Found a @RabbitListener method on the target class for this JDK proxy ->
                // is it also present on the proxy itself?
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                for (Class<?> iface : proxiedInterfaces) {
                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    }
                    catch (NoSuchMethodException noMethod) {
                    }
                }
            }
            catch (SecurityException ex) {
                ReflectionUtils.handleReflectionException(ex);
            }
            catch (NoSuchMethodException ex) {
                throw new IllegalStateException(String.format(
                        "@RabbitListener method '%s' found on bean target class '%s', " +
                                "but not found in any interface(s) for a bean JDK proxy. Either " +
                                "pull the method up to an interface or switch to subclass (CGLIB) " +
                                "proxies by setting proxy-target-class/proxyTargetClass " +
                                "attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
            }
        }
        return method;
    }

    private static class ListenerMethod {

        final Method method; // NOSONAR

        final NetCommListener annotations; // NOSONAR

        ListenerMethod(Method method, NetCommListener annotations) { // NOSONAR
            this.method = method;
            this.annotations = annotations;
        }

    }

}

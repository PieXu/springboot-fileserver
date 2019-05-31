package com.innovate.filseserver.condition;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * http的传输方式进行
 * 
 * @author Founder
 */
public class HttpTransferCondition extends SpringBootCondition {

	@Override
	public ConditionOutcome getMatchOutcome(ConditionContext context,AnnotatedTypeMetadata metadata) {
		 Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(TransConditionalAnnotation.class.getName());
	        Object key = annotationAttributes.get("key");
	        Object value = annotationAttributes.get("value");
	        if(key == null || value == null){
	            return new ConditionOutcome(false, "error");
	        }
	        //获取environment中的值
	        String parameValue = context.getEnvironment().getProperty(key.toString());
	        if(StringUtils.isNotBlank(parameValue) && parameValue.equalsIgnoreCase(value.toString())) {
	            return new ConditionOutcome(true, "ok");
	        }
	        return new ConditionOutcome(false, "error");
	}

}

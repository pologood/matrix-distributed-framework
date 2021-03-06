package com.matrix.base;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import com.alibaba.fastjson.JSONObject;


/**
 * @description: RockMq顶层消费者；通常在系统中很少用到DefaultMQPullConsumer来主动拉去消息，
 * 	如果在业务中使用到它，请新建BaseMqPullConsumer.java类。此类会封装DefaultMQPushConsumer对象。
 * 
 * 	使用示例请参考：AsyncSyncConsumer.java
 *
 * @author Yangcl
 * @date 2016年4月24日 下午4:59:02 
 * @version 1.0.0.1
 */
public abstract class BaseMqPushConsumer extends BaseClass {
	
	private DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();

	
	/**
	 * @description: 非顺序消费 - 消息处理者，子类需要实现此方法 - 此方式系统吞吐量高|此方式常用
	 * 
	 * 		msgs中只收集同一个topic，同一个tag，并且key相同的message   
	 * 		会把不同的消息分别放置到不同的队列中  
	 * 
	 * 	保留此处代码，仅做示例。
                    for(Message msg : msgs){  
                        System.out.println(new String(msg.getBody()));           
                    }     
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;  
	 * 
	 * @param msgs
	 * @param context
	 * @author Yangcl
	 * @date 2019年5月20日 下午9:57:29 
	 * @version 1.0.0.1
	 */
	public abstract ConsumeConcurrentlyStatus msgProcessor(List<MessageExt> msgs , ConsumeConcurrentlyContext context);
	
	/**
	 * @description: 顺序消费 - 消息处理者，子类需要实现此方法 - 此方式系统吞吐量相对较低
	 * 
	 * 		msgs中只收集同一个topic，同一个tag，并且key相同的message   
	 * 		会把不同的消息分别放置到不同的队列中  
	 * 
	 * 	保留此处代码，仅做示例。
                    for(Message msg : msgs){  
                        System.out.println(new String(msg.getBody()));           
                    }     
                    return ConsumeOrderlyStatus.SUCCESS;  
	 * 
	 * @param msgs
	 * @param context
	 * @author Yangcl
	 * @date 2019年5月20日 下午9:57:29 
	 * @version 1.0.0.1
	 */
	public abstract ConsumeOrderlyStatus msgProcessorOrderly(List<MessageExt> msgs , ConsumeOrderlyContext context);
	
	/**
	 * @description: 配置消费者
	 *
	 * @param type 消费者类型：Concurrently 非顺序消费 | Orderly 顺序消费
	 * @param group
	 * @param topic
	 * @param tag
	 * @author Yangcl
	 * @date 2019年5月20日 下午9:58:37 
	 * @version 1.0.0.1
	 */
	public JSONObject doExecute(ConsumerType type , GttEnum group , GttEnum topic , GttEnum tag ) {
		JSONObject result = new JSONObject();
		result.put("status", "success");
		result.put("msg", this.getInfo(109010004));  // 109010004=消息处理成功!
		
		consumer.setNamesrvAddr(this.getConfig("matrix-rocket-mq.namesrv_" + this.getConfig("matrix-core.model")) );
		consumer.setConsumerGroup(group.toString());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET); 	// 程序第一次启动从消息队列头获取数据  
		try {
			consumer.subscribe(topic.toString() , tag.toString()); 		// 开始订阅消息，tag可以为"*"
			
			if(type.toString().equals("Concurrently")) {  		// 注册消费的监听 - 非顺序消费
				consumer.registerMessageListener(new MessageListenerConcurrently(){  		  
	                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs , ConsumeConcurrentlyContext context) {  
	                	return msgProcessor(msgs, context);  
	                }  
	            }); 
			}else if(type.toString().equals("Orderly")) {		// 注册消费的监听 - 顺序消费
				consumer.registerMessageListener(new MessageListenerOrderly() {				
					public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
						return msgProcessorOrderly(msgs, context);
					}
				});
			}else {
				result.put("status", "error");
				result.put("msg", this.getInfo(109010006));  // 109010006=消息处理失败，ConsumerType.java存在非法的枚举类型
				return result;
			}
			consumer.start(); 
		} catch (MQClientException e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("msg", this.getInfo(109010005));  // 109010005=消息处理失败，系统抛出异常! BaseMqPushConsumer.java -> initConsumerConfig() Topic 或 Tag出现错误
			return result;
		}
		
		return result;
	}

	
	/**
	 * @description: 获取消费者，执行其他操作；比如挂载消费端：consumer.suspend();
	 *
	 * @author Yangcl
	 * @date 2019年5月20日 下午10:05:25 
	 * @version 1.0.0.1
	 */
	public DefaultMQPushConsumer getConsumer() {
		return consumer;
	}
	
}
























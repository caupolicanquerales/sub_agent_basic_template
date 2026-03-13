package com.capo.sub_agent_basic_template.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.capo.sub_agent_basic_template.utils.AsyncUtil;

@Service
public class ExecutingPromptExtractTemplateService {
	
	private final ChatClient chatClient;
	private final MessageToChatClientService messageToChat;
	
	@Value(value="${event.name.chat.basic.template}")
	private String eventName;
	
	public ExecutingPromptExtractTemplateService(@Qualifier("chatClientBasicTemplate") ChatClient chatClient,
			MessageToChatClientService messageToChat) {
		this.chatClient = chatClient;
		this.messageToChat=messageToChat;
	}
	
	public CompletableFuture<String> generateBasicTemplatAsync(Map<String, byte[]> fileDataMap){
		Map<String,byte[]> files= Optional.ofNullable(fileDataMap).orElse(new HashMap<String,byte[]>());
		List<Message> listMessage = messageToChat.buildMessage(files);

		Supplier<String> supplier = () -> this.chatClient.prompt()
				.messages(listMessage)
				.call()
				.content();

		return AsyncUtil.executeAsync(supplier, "Async error generating basic template");
	}
	
}

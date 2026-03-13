package com.capo.sub_agent_basic_template.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.capo.sub_agent_basic_template.service.ConverterFileService;
import com.capo.sub_agent_basic_template.service.ExecutingPromptExtractTemplateService;
import com.capo.sub_agent_basic_template.utils.SseStreamUtil;


@RestController
@RequestMapping("sub-agent-basic-template")
@CrossOrigin(origins = "${app.frontend.url}")
public class SubAgentController {
	
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final ExecutingPromptExtractTemplateService extractTemplateService;
	private final ConverterFileService converterFile;
	
	@Value(value="${event.name.chat.basic.template}")
	private String eventName;
	
	public SubAgentController(ExecutingPromptExtractTemplateService extractTemplateService,
			ConverterFileService converterFile) {
		this.extractTemplateService= extractTemplateService;
		this.converterFile = converterFile;
	}
	
	@PostMapping(path = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamImageGeneration(@RequestPart("files") List<MultipartFile> fileParts) {
		Map<String, byte[]> fileDataMap = converterFile.convertFileToMap(fileParts);
		return SseStreamUtil.stream(executor, eventName, "Basic Template generation started for prompt",
                () -> extractTemplateService.generateBasicTemplatAsync(fileDataMap));
	}
}

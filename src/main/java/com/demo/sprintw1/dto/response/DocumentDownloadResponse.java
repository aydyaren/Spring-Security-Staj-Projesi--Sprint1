package com.demo.sprintw1.dto.response;

import org.springframework.core.io.Resource;

public record DocumentDownloadResponse(Resource resource, String originalFileName) {
}

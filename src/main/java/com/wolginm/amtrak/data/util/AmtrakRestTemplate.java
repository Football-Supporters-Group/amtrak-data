package com.wolginm.amtrak.data.util;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import com.wolginm.amtrak.data.properties.AmtrakProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class AmtrakRestTemplate {
    
    private final WebClient amtrakRestTemplate;
    private final AmtrakProperties amtrakProperties;
    private final FileUtil fileUtil;

    @Autowired
    public AmtrakRestTemplate(AmtrakProperties amtrakProperties,
        FileUtil fileUtil) {
        this.amtrakProperties = amtrakProperties;
        amtrakRestTemplate = WebClient.builder()
            .baseUrl(this.amtrakProperties.getGtfsUrl())
            .build();
        this.fileUtil = fileUtil;
        log.info("Built AmtrakRestTemplate");
    }

    public void downloadGTFSFile() {
        Flux<DataBuffer> downloadedMono = amtrakRestTemplate
            .get()
            .uri(this.amtrakProperties.getGtfsUri())
            .retrieve()
            .bodyToFlux(DataBuffer.class);

        Path placeToStoreFile = this.fileUtil.resolvePath(this.amtrakProperties.getTempFile());
        this.fileUtil.prepFoldersForFile(placeToStoreFile);

        DataBufferUtils.write(downloadedMono, 
            placeToStoreFile,
            StandardOpenOption.CREATE).block();
        log.info("File {} saved", placeToStoreFile);
    }
    
}

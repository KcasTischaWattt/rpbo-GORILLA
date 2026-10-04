package com.backend.crosswords.config;

import com.backend.crosswords.admin.services.UserService;
import com.backend.crosswords.corpus.services.DocService;
import com.backend.crosswords.corpus.services.TagService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class DataLoader implements ApplicationRunner, DisposableBean {
    private final UserService userService;
    private final TagService tagService;
    private final DocService docService;
    private final ElasticsearchOperations elasticsearchOperations;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    public DataLoader(UserService userService, TagService tagService, DocService docService, ElasticsearchOperations elasticsearchOperations) {
        this.userService = userService;
        this.tagService = tagService;
        this.docService = docService;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        userService.createDefaultUsers();
        tagService.createDefaultTags();
    }

    @Override
    public void destroy() throws Exception {
        if (ddlAuto.equals("create-drop") || ddlAuto.equals("create")) {
            this.deleteESIndexes();
        }
    }
    private void deleteESIndexes() {
        List<IndexOperations> indexOpsList = List.of(
                elasticsearchOperations.indexOps(IndexCoordinates.of("document")),
                elasticsearchOperations.indexOps(IndexCoordinates.of("digest_core")),
                elasticsearchOperations.indexOps(IndexCoordinates.of("digest_subscription")),
                elasticsearchOperations.indexOps(IndexCoordinates.of("digest"))
        );
        for (var indexOps : indexOpsList) {
            if (indexOps.exists()) {
                boolean isDeleted = indexOps.delete();
                if (isDeleted) {
                } else {
                    throw new RequestRejectedException("Failed to delete index '" + "document" + "'.");
                }
            } else {
                throw new NoSuchElementException("Index '" + "document" + "' does not exist.");
            }
        }
    }
}

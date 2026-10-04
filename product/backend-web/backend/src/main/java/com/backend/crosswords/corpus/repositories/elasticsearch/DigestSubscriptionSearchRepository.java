package com.backend.crosswords.corpus.repositories.elasticsearch;

import com.backend.crosswords.corpus.models.DigestSubscriptionES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DigestSubscriptionSearchRepository extends ElasticsearchRepository<DigestSubscriptionES, Long> {
}

package com.backend.crosswords.corpus.repositories.elasticsearch;

import com.backend.crosswords.corpus.models.DigestES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DigestSearchRepository extends ElasticsearchRepository<DigestES, String> {
}

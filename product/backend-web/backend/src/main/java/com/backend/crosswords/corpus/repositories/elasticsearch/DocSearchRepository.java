package com.backend.crosswords.corpus.repositories.elasticsearch;

import com.backend.crosswords.corpus.models.DocES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocSearchRepository extends ElasticsearchRepository<DocES, Long> {
}

package com.backend.crosswords.corpus.services;

import com.backend.crosswords.config.Pair;
import com.backend.crosswords.corpus.enums.Source;
import com.backend.crosswords.corpus.models.DigestTemplate;
import com.backend.crosswords.corpus.models.Tag;
import com.backend.crosswords.corpus.repositories.jpa.DigestTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class DigestTemplateService {
    private final DigestTemplateRepository templateRepository;

    public DigestTemplateService(DigestTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public DigestTemplate createTemplateBySourcesAndTags(Set<Source> sources, Set<Tag> tags) {
        var templateUuid = DigestTemplate.generateUuid(sources, tags);
        var checkTemplate = templateRepository.findById(templateUuid);
        if (checkTemplate.isEmpty()) {
            return templateRepository.save(new DigestTemplate(sources, tags));
        }
        return checkTemplate.get();
    }

    public List<DigestTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    public DigestTemplate getTemplateFromId(String uuid) {
        return templateRepository.findFullTemplateById(uuid).orElseThrow(() -> new NoSuchElementException("There is no templates with such id!"));
    }
    public Pair<List<String>, List<String>> getSourcesNamesAndTagsNamesFromTemplate(DigestTemplate template) {
        List<String> sources = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        for (var source : template.getSources()) {
            sources.add(source.getRussianName());
        }
        for (var tag : template.getTags()) {
            tags.add(tag.getName());
        }
        return new Pair<>(sources, tags);
    }
}

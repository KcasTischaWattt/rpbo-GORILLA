package com.backend.crosswords.corpus.services;

import com.backend.crosswords.corpus.dto.CreateTagDTO;
import com.backend.crosswords.corpus.dto.TagDTO;
import com.backend.crosswords.corpus.models.DocMeta;
import com.backend.crosswords.corpus.models.Tag;
import com.backend.crosswords.corpus.repositories.jpa.TagRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    public TagService(TagRepository tagRepository, ModelMapper modelMapper) {
        this.tagRepository = tagRepository;
        this.modelMapper = modelMapper;
    }

    public void createTag(CreateTagDTO createTagDTO) {
        if (tagRepository.existsTagByName(createTagDTO.getName())) {
            throw new IllegalArgumentException("The tag with such name is already existing!");
        }
        var tag = modelMapper.map(createTagDTO, Tag.class);
        tagRepository.save(tag);
    }

    // TODO переделать с четом новых ссылок на теги (например шаблонов дайджестов)
    public void deleteTagById(Long id) {
        var tag = tagRepository.findById(id);
        if (tag.isEmpty()) {
            throw new NoSuchElementException("There is no tags with such id!");
        }
        tagRepository.delete(tag.get());
    }

    public Set<Tag> getTagsInNamesAndSaveForDoc(List<TagDTO> tagDTOs, DocMeta docMeta) throws IllegalArgumentException {
        if (tagDTOs == null || tagDTOs.isEmpty()) {
            throw new IllegalArgumentException("Tags can't be null or empty!");
        }
        List<String> names = new ArrayList<>();
        for (var tagDTO : tagDTOs) {
            names.add(tagDTO.getName());
        }
        var tags= tagRepository.getTagsByNameIsIn(names);
        if (tags.isEmpty()) {
            throw new IllegalArgumentException("There is no appropriate tags' names!");
        }
        for (var tag : tags) {
            tag.getDocs().add(docMeta);
            tagRepository.save(tag);
        }
        return tags;
    }

    public void removeTagsFromDoc(DocMeta docMeta) {
        Iterator<Tag> iterator = docMeta.getTags().iterator();
        while (iterator.hasNext()) {
            Tag tag = iterator.next();
            tag.getDocs().remove(docMeta);
            iterator.remove();
            tagRepository.save(tag);
        }
    }

    public Set<String> getSetOfTagsNames(Collection<Tag> tags) {
        Set<String> tagsNames = new HashSet<>();
        for (var tag : tags) {
            tagsNames.add(tag.getName());
        }
        return tagsNames;
    }

    public Set<Tag> getTagsInNames(List<String> tagNames) {
        return tagRepository.getTagsByNameIsIn(tagNames);
    }

    public void createDefaultTags() {
        var defaultTags = List.of("Политика", "Внутренняя политика", "Внешняя политика", "Дипломатия", "Законы", "Выборы", "Политические партии", "Геополитика", "Экономика", "Макроэкономика", "Финансы", "Банки", "Трансграничные переводы", "Кредитование", "Гранты", "Инвестиции", "Энергетика", "Инфляция", "Фондовый рынок", "Бизнес", "Стартапы", "Технологии", "IT", "Цифровизация", "Система быстрых платежей", "Искусственный интеллект", "Кибербезопасность", "Наука", "Космос", "Медицина", "Здравоохранение", "Эпидемии", "Психология", "Образование", "Школа", "Университеты", "Соцподдержка", "Труд", "Занятость", "Миграция", "Права человека", "Экология", "Климат", "Загрязнение", "Ресурсы", "Транспорт", "Инфраструктура", "Происшествия", "Криминал", "ДТП", "Катастрофы", "Расследования", "Терроризм", "Скандалы", "Коррупция", "Протесты", "Армия", "Вооруженный конфликт", "Мир", "Международные отношения", "США", "Европа", "Азия", "Россия", "СНГ", "Ближний Восток", "Африка", "Латинская Америка", "Спорт", "Футбол", "Хоккей", "Баскетбол", "Олимпийские игры", "Спортсмены", "Соревнования", "Трансферы", "Культура", "Кино", "Музыка", "Театр", "Искусство", "Мода", "Литература", "Лайфстайл", "Еда", "Рецепты", "Путешествия", "Туризм", "Виза", "Авиация", "Отношения", "Семья", "Саморазвитие", "ЗОЖ", "Фитнес", "Ментальное здоровье", "Цифровые технологии", "Соцсети", "Цензура", "СМИ", "Журналистика", "Аналитика", "Интервью", "Прогноз", "Рейтинг", "Обзор", "Утечка", "Санкции", "Блокчейн", "Криптовалюта", "NFT", "IPO", "Слияния и поглощения", "Старение", "Удаленка", "Рынок труда", "Цены", "Зарплаты", "Социальные выплаты", "Тарифы", "Налоги", "Реформа", "Инновации", "Тренд", "Инцидент", "Не определено");
        List<Tag> newTags = new ArrayList<>();
        for (var defaultTag : defaultTags) {
            if (!tagRepository.existsTagByName(defaultTag)) {
                newTags.add(new Tag(defaultTag));
            }
        }
        tagRepository.saveAll(newTags);
    }
}

# Свидетельства из исходного состояния

Baseline учебного репозитория: `640de504f232d6cd99fc1bd35ead266d4bcbb824`.

Фрагменты извлечены автоматически; номера относятся к исходным файлам. Это наблюдения кода, а не доказательство поведения развернутой системы.

## E01

Источник: [product/backend-web/backend/src/main/java/com/backend/crosswords/config/SecurityConfig.java](../../product/backend-web/backend/src/main/java/com/backend/crosswords/config/SecurityConfig.java). SHA-256: `2ea5e24b4921ea3fb0da4e88b50eb9f2d47eb8630cbad8033eac39eeaf784bc8`.

Строки 35–98:

```text
35:         this.jwtFilter = jwtFilter;
36:         this.crosswordUserDetailsService = crosswordUserDetailsService;
37:         this.unauthorizedHandler = unauthorizedHandler;
38:     }
39: 
40:     @Bean
41:     public SecurityFilterChain applicationSecurity(HttpSecurity http) throws Exception {
42:         http
43:                 .cors(withDefaults())
44:                 .csrf(AbstractHttpConfigurer::disable)
45:                 .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
46:                 .formLogin(AbstractHttpConfigurer::disable)
47:                 .exceptionHandling(h -> h.authenticationEntryPoint(unauthorizedHandler))
48:                 .securityMatcher("/**")
49:                 .authorizeHttpRequests(registry -> registry
50:                         .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
51:                         .requestMatchers(HttpMethod.PATCH, "/documents/{id}/rate").authenticated()
52:                         .requestMatchers(HttpMethod.POST, "/documents/{id}/add_to_favourites").authenticated()
53:                         .requestMatchers(HttpMethod.POST, "/documents/{id}/remove_from_favourites").authenticated()
54:                         .requestMatchers(HttpMethod.POST, "/documents/{docId}/put_into/{packageName}").authenticated()
55:                         .requestMatchers(HttpMethod.POST, "/documents/{docId}/remove_from/{packageName}").authenticated()
56:                         .requestMatchers(HttpMethod.GET, "/users/check_auth").authenticated()
57:                         .requestMatchers(HttpMethod.GET, "/users/get_email").authenticated()
58:                         .requestMatchers(HttpMethod.GET, "/users/check_verification").authenticated()
59:                         .requestMatchers("/users/verification_code/**").authenticated()
60:                         .requestMatchers(HttpMethod.PUT, "/users/subscription_settings/set").authenticated()
61:                         .requestMatchers(HttpMethod.GET, "/users/personal_info").authenticated()
62:                         .requestMatchers(HttpMethod.POST, "/users/logout/**").authenticated()
63:                         .requestMatchers(HttpMethod.PATCH, "/users/change/**").authenticated()
64:                         .requestMatchers(HttpMethod.POST, "/users/fcm_token/create").authenticated()
65:                         .requestMatchers(HttpMethod.DELETE, "/users/fcm_token/delete").authenticated()
66:                         .requestMatchers(HttpMethod.PUT, "/documents/{id}/edit").hasAuthority(AuthorityEnum.EDIT_DELETE_DOCS.name())
67:                         .requestMatchers(HttpMethod.DELETE, "/documents/{id}").hasAuthority(AuthorityEnum.EDIT_DELETE_DOCS.name())
68:                         .requestMatchers(HttpMethod.DELETE, "/digests/{id}").hasAuthority(AuthorityEnum.EDIT_DELETE_DIGESTS.name())
69:                         .requestMatchers(HttpMethod.PUT, "/digests/{id}").hasAuthority(AuthorityEnum.EDIT_DELETE_DIGESTS.name())
70:                         .requestMatchers("/packages/**").authenticated()
71:                         .requestMatchers(HttpMethod.GET,"/subscriptions/**").permitAll()
72:                         .requestMatchers("/subscriptions/**").authenticated()
73:                         .requestMatchers(HttpMethod.GET, "/digests").permitAll()
74:                         .requestMatchers(HttpMethod.GET, "/digests/{id}").permitAll()
75:                         .requestMatchers(HttpMethod.GET, "/digests/{id}/check_access").permitAll()
76:                         .requestMatchers(HttpMethod.GET, "/digests/search").permitAll()
77:                         .requestMatchers(HttpMethod.GET, "/digests/public").permitAll()
78:                         .requestMatchers(HttpMethod.GET, "/digests/{id}/pdf").permitAll()
79:                         .requestMatchers(HttpMethod.POST, "/digests/create").permitAll()
80:                         .requestMatchers("/digests/**").authenticated()
81:                         .requestMatchers("/documents/{id}/annotate/**").authenticated()
82:                         .requestMatchers("/documents/{id}/comment/**").authenticated()
83:                         .requestMatchers("/documents/{id}/packages").authenticated()
84:                         .anyRequest().permitAll()
85:                 )
86:                 .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
87:         return http.build();
88:     }
89: 
90:     @Bean
91:     public CorsConfigurationSource corsConfigurationSource() {
92:         CorsConfiguration configuration = new CorsConfiguration();
93:         configuration.setAllowedOrigins(List.of("http://localhost:8082", "http://localhost:9000", "http://localhost:53957", "https://crosswords.jujacloud.tech"));
94:         configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
95:         configuration.setAllowedHeaders(List.of("*"));
96:         configuration.setAllowCredentials(true);
97: 
98:         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
```

## E02

Источник: [product/backend-web/backend/src/main/java/com/backend/crosswords/config/JWTFilter.java](../../product/backend-web/backend/src/main/java/com/backend/crosswords/config/JWTFilter.java). SHA-256: `ec00fb1bf778e5526fefb018c0435321462e8ddff4709d6f2f7d968dd41809ad`.

Строки 40–139:

```text
40:     @Override
41:     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
42:         String accessToken = null;
43:         String oldRefreshToken = null;
44:         if (request.getCookies() != null) {
45:             for (Cookie cookie : request.getCookies()) {
46:                 if ("access_token".equals(cookie.getName())) {
47:                     accessToken = cookie.getValue();
48:                     if (oldRefreshToken != null) {
49:                         break;
50:                     }
51:                 } else if ("refresh_token".equals(cookie.getName())) {
52:                     oldRefreshToken = cookie.getValue();
53:                     if (accessToken != null) {
54:                         break;
55:                     }
56:                 }
57:             }
58:         }
59: 
60:         if (accessToken != null && !accessToken.isBlank()) {
61:             try {
62:                 this.validateJWTAndAuthenticate(accessToken, request);
63:             } catch (UsernameNotFoundException | JWTVerificationException exception) {
64:                 refreshUser(oldRefreshToken, request, response);
65:             } catch (IllegalAccessException e) {
66:                 this.setCookies(response, "", "");
67:                 AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
68:                         "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
69:                 SecurityContextHolder.getContext().setAuthentication(anonymousToken);
70:             }
71:         } else {
72:             refreshUser(oldRefreshToken, request, response);
73:         }
74:         filterChain.doFilter(request, response);
75:     }
76: 
77:     private void validateJWTAndAuthenticate(String accessToken, HttpServletRequest request) throws IllegalAccessException {
78:         String username = jwtUtil.validateTokenAndRetrieveClaim(accessToken);
79:         UserDetails userDetails = crosswordUserDetailsService.loadUserByUsername(username);
80: 
81:         String ipAddress = request.getHeader("X-Forwarded-For");
82:         if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
83:             ipAddress = request.getRemoteAddr();
84:         }
85:         String userAgent = request.getHeader("User-Agent");
86: 
87:         if (refreshTokenService.checkExistingRefreshToken(ipAddress, userAgent, ((CrosswordUserDetails)userDetails).getUser()) == null) {
88:             throw new IllegalAccessException("There is no such authorized users!");
89:         }
90: 
91:         UsernamePasswordAuthenticationToken authToken =
92:                 new UsernamePasswordAuthenticationToken(userDetails,
93:                         userDetails.getPassword(),
94:                         userDetails.getAuthorities());
95: 
96:         if (SecurityContextHolder.getContext().getAuthentication() == null) {
97:             SecurityContextHolder.getContext().setAuthentication(authToken);
98:         }
99:     }
100: 
101:     private void refreshUser(String oldToken, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
102:         try {
103:             if (oldToken != null && !oldToken.isBlank()) {
104:                 String ipAddress = request.getHeader("X-Forwarded-For");
105:                 if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
106:                     ipAddress = request.getRemoteAddr();
107:                 }
108:                 String userAgent = request.getHeader("User-Agent");
109:                 RefreshToken newRefreshToken;
110:                 newRefreshToken = refreshTokenService.refreshUser(oldToken, ipAddress, userAgent);
111:                 String newAccessToken = jwtUtil.generateAccessToken(newRefreshToken.getUser().getUsername());
112:                 this.setCookies(response, newAccessToken, newRefreshToken.getToken());
113:                 this.validateJWTAndAuthenticate(newAccessToken, request);
114:             } else {
115:                 AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
116:                         "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
117:                 SecurityContextHolder.getContext().setAuthentication(anonymousToken);
118:             }
119:         } catch (ObjectOptimisticLockingFailureException ex) {
120:             refreshUser(oldToken, request, response);
121:         } catch (TokenExpiredException | NoSuchElementException | SecurityException | IllegalAccessException e) {
122:             AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
123:                     "anonymous", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
124:             SecurityContextHolder.getContext().setAuthentication(anonymousToken);
125:             this.setCookies(response, "", "");
126:         }
127:     }
128:     private void setCookies(HttpServletResponse response, String newAccessToken, String newRefreshToken) {
129:         var accessTokenCookie = new Cookie("access_token", newAccessToken);
130:         accessTokenCookie.setHttpOnly(true);
131:         accessTokenCookie.setSecure(true);
132:         accessTokenCookie.setPath("/");
133:         var refreshTokenCookie = new Cookie("refresh_token", newRefreshToken);
134:         refreshTokenCookie.setHttpOnly(true);
135:         refreshTokenCookie.setSecure(true);
136:         refreshTokenCookie.setPath("/");
137:         response.addCookie(accessTokenCookie);
138:         response.addCookie(refreshTokenCookie);
139:     }
```

## E03

Источник: [product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/AnnotationService.java](../../product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/AnnotationService.java). SHA-256: `c6e705a6d2ddfd8f4191ee7ddd49721e51d58ef9d8b26c1e75e97cf305f49617`.

Строки 26–61:

```text
26:     @Transactional
27:     public Annotation createAnnotation(User user, DocMeta docMeta, CreateUpdateAnnotationDTO createUpdateAnnotationDTO) {
28:         Annotation annotation = modelMapper.map(createUpdateAnnotationDTO, Annotation.class);
29:         annotation.setDoc(docMeta);
30:         annotation.setOwner(user);
31:         return annotationRepository.save(annotation);
32:     }
33: 
34:     @Transactional
35:     public void deleteAnnotationByIdFromDoc(User user, DocMeta docMeta, Long annotationId) {
36:         var annotation = annotationRepository.findById(annotationId).orElseThrow(() -> new NoSuchElementException("There is no annotations with such id"));
37:         if (!Objects.equals(user.getId(), annotation.getOwner().getId())) {
38:             throw new IllegalArgumentException("You are not the owner of this annotation");
39:         }
40:         if (!Objects.equals(docMeta.getId(), annotation.getDoc().getId())) {
41:             throw new IllegalArgumentException("This documents doesn't own this annotation");
42:         }
43:         annotation.setDoc(null);
44:         annotation.setOwner(null);
45:         docMeta.getAnnotations().remove(annotation);
46:         annotationRepository.delete(annotation);
47:     }
48: 
49:     @Transactional
50:     public void updateAnnotationByIdForDoc(User user, DocMeta docMeta, Long annotationId, CreateUpdateAnnotationDTO createUpdateAnnotationDTO) {
51:         var annotation = annotationRepository.findById(annotationId).orElseThrow(() -> new NoSuchElementException("There is no comments with such id"));
52:         if (!Objects.equals(user.getId(), annotation.getOwner().getId())) {
53:             throw new IllegalArgumentException("You are not the owner of this comment");
54:         }
55:         if (!Objects.equals(docMeta.getId(), annotation.getDoc().getId())) {
56:             throw new IllegalArgumentException("This documents doesn't own this comment");
57:         }
58:         annotation.setComments(createUpdateAnnotationDTO.getComments());
59:         annotation.setStartPos(createUpdateAnnotationDTO.getStartPos());
60:         annotation.setEndPos(createUpdateAnnotationDTO.getEndPos());
61:         annotationRepository.save(annotation);
```

## E04

Источник: [product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/DocService.java](../../product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/DocService.java). SHA-256: `f98dd9de6bd9588ffe53030588b1ed5ba7a1a69e17e3465065f7192ae1ca7fdf`.

Строки 59–136:

```text
59:         this.commentService = commentService;
60:     }
61: 
62:     private DocDTO transformDocIntoDocDTO(DocMeta docMeta, Boolean includeAnnotations) {
63:         var docDTO = modelMapper.map(docMeta, DocDTO.class);
64:         var docES = docSearchRepository.findById(docDTO.getId()).orElseThrow();
65:         User user;
66:         try {
67:             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
68:             CrosswordUserDetails crosswordUserDetails = (CrosswordUserDetails) authentication.getPrincipal();
69:             user = crosswordUserDetails.getUser();
70:             docDTO.setFavourite(packageService.checkDocInFavourites(user, docMeta));
71:             var rating = ratingService.getRatingsForDocumentByUser(docMeta, user);
72:             docDTO.setRatingSummary(rating.get(0));
73:             docDTO.setRatingClassification(rating.get(1));
74:             docDTO.setAuthed(true);
75:             boolean moderator = false;
76:             if (user.getRole() != null && user.getRole().getAuthorities() != null) {
77:                 for (var authority : user.getRole().getAuthorities()) {
78:                     if (authority.equals(AuthorityEnum.EDIT_DELETE_DOCS)) {
79:                         moderator = true;
80:                         break;
81:                     }
82:                 }
83:             }
84: 
85:             docDTO.setIsModerator(moderator);
86:         } catch (ClassCastException e) {
87:             docDTO.setFavourite(null);
88:             docDTO.setRatingClassification(null);
89:             docDTO.setRatingSummary(null);
90:             docDTO.setAuthed(false);
91:             docDTO.setIsModerator(false);
92:             user = null;
93:         }
94:         docDTO.setTagNames(new ArrayList<>());
95:         for (var tag : docMeta.getTags()) {
96:             docDTO.getTagNames().add(tag.getName());
97:         }
98:         docDTO.setText(docES.getText());
99:         docDTO.setTitle(docES.getTitle());
100:         docDTO.setRusSource(docMeta.getSource().getRussianName());
101:         if (includeAnnotations != null && includeAnnotations && user != null) {
102:             docDTO.setDocsAnnotations(new ArrayList<>());
103:             for (var annotation : docMeta.getAnnotations()) {
104:                 if (user.getId().equals(annotation.getOwner().getId())) {
105:                     docDTO.getDocsAnnotations().add(modelMapper.map(annotation, AnnotationDTO.class));
106:                 }
107:             }
108:         }
109:         return docDTO;
110:     }
111: 
112:     @Transactional
113:     public void createDoc(CreateDocDTO createDocDTO) throws IllegalArgumentException {
114:         var docMeta = modelMapper.map(createDocDTO, DocMeta.class);
115:         if (docMeta.getLanguage() == null) {
116:             throw new IllegalArgumentException("Language is unappropriated or null!");
117:         }
118:         docMeta.setSource(Source.fromRussianName(createDocDTO.getRusSource()));
119:         Timestamp timeNow = new Timestamp(System.currentTimeMillis());
120:         if (createDocDTO.getDate() == null) {
121:             docMeta.setDate(timeNow);
122:         }
123:         docMeta.setLastEdit(timeNow);
124: 
125:         docMetaRepository.save(docMeta);
126:         tagService.getTagsInNamesAndSaveForDoc(createDocDTO.getTagDTOs(), docMeta);
127: 
128:         docMeta = docMetaRepository.save(docMeta);
129:         var docES = modelMapper.map(createDocDTO, DocES.class);
130:         docES.setId(docMeta.getId());
131:         docSearchRepository.save(docES);
132:     }
133: 
134:     public List<DocDTO> getAllDocs() {
135:         List<DocDTO> result = new ArrayList<>();
136:         for (var docMeta : docMetaRepository.findAll()) {
```

## E05

Источник: [product/classifier/news_service.py](../../product/classifier/news_service.py). SHA-256: `a7c3ac4603c9899a8f136bc30d13bcbf3fadc188183c4cdea52832b2263ef01d`.

Строки 48–65:

```text
48: def create_consumer():
49:     while True:
50:         try:
51:             consumer = KafkaConsumer(
52:                 KAFKA_TOPIC,
53:                 bootstrap_servers=[KAFKA_BOOTSTRAP_SERVERS],
54:                 auto_offset_reset='latest', 
55:                 group_id='news_classification_group',
56:                 value_deserializer=safe_json_deserializer,
57:                 enable_auto_commit=False,
58:                 max_poll_interval_ms=900000,
59:             )
60:             logger.info("Успешно создан KafkaConsumer")
61:             return consumer
62:         except NoBrokersAvailable as e:
63:             logger.error("Ошибка подключения к Kafka: %s. Повтор через 5 секунд...", e)
64:             time.sleep(5)
65: 
```

Строки 100–172:

```text
100: def classify_news(news):
101:     title = news.get("title", "")
102:     text = news.get("text", "")
103:     tags_str = ", ".join(f"'{tag}'" for tag in tags)
104:     prompt = (
105:         f"Скажи мне к каким тегам из списка относится эта новость? "
106:         f"В ответе укажи просто список подходящих тегов в квадратных скобках, например: ['Экономика', 'Право']. "
107:         f"Ничего больше писать нельзя. Вот список тегов: [{tags_str}]. "
108:         f"Вот сама новость - Заголовок: {title}. Текст: {text}."
109:     )
110: 
111:     models = ["qwen2.5:32b", "llama3:8b"]
112: 
113:     for model in models:
114:         payload = {
115:             "model": model,
116:             "prompt": prompt,
117:             "stream": False
118:         }
119:         try:
120:             response = requests.post(
121:                 LLM_API_URL,
122:                 json=payload,
123:                 auth=(LLM_API_AUTH_USER, LLM_API_AUTH_PASS)
124:             )
125:             response.raise_for_status()
126:             classification_result = response.json()
127:             validated_tags = validate_and_extract_tags(classification_result, tags)
128:             if validated_tags and len(validated_tags) > 0:
129:                 logger.info("Классификация получена с моделью %s: %s", model, validated_tags)
130:                 return validated_tags
131:             else:
132:                 logger.error("Неверный формат ответа от модели %s", model)
133:         except Exception as e:
134:             logger.error("Ошибка при вызове LLM API с моделью %s: %s", model, e)
135: 
136:     return ["Не определено"]
137: 
138: def generate_summary(news):
139:     """
140:     Генерирует улучшенное краткое содержание новости с использованием LLM (модель qwen).
141:     Если генерация проходит успешно, возвращает новое краткое содержание,
142:     иначе возвращает исходное содержание.
143:     """
144:     title = news.get("title", "")
145:     text = news.get("text", "")
146:     prompt = (
147:         f"Напиши краткое содержание для новости с заголовком: \"{title}\" и текстом: \"{text}\". "
148:         f"Сделай его лаконичным, информативным и не длиннее 50 слов, оставь только самую важную информацию. В ответе укажи только краткое содержание, ничего лишнего. Ответ должен быть на руском языке."
149:     )
150:     payload = {
151:         "model": "qwen2.5:32b",
152:         "prompt": prompt,
153:         "stream": False
154:     }
155:     try:
156:         response = requests.post(
157:             LLM_API_URL,
158:             json=payload,
159:             auth=(LLM_API_AUTH_USER, LLM_API_AUTH_PASS)
160:         )
161:         response.raise_for_status()
162:         summary_result = response.json()
163:         new_summary = summary_result.get("response", "").strip()
164:         if new_summary:
165:             logger.info("Новый summary сгенерирован: %s", new_summary)
166:             return new_summary
167:         else:
168:             logger.error("Пустой ответ от LLM при генерации summary: %s", summary_result)
169:             return news.get("summary", "")
170:     except Exception as e:
171:         logger.error("Ошибка при генерации summary: %s", e)
172:         return news.get("summary", "")
```

Строки 174–213:

```text
174: def send_to_backend(news):
175:     """
176:     Отправляет новость с результатами классификации на backend.
177:     """
178:     try:
179:         headers = {}
180:         if BACKEND_SECRET_KEY:
181:             headers['Authorization'] = f'Bearer {BACKEND_SECRET_KEY}'
182:         response = requests.post(ADD_DOCUMENT_BACKEND_API_URL, json=news, headers=headers)
183:         response.raise_for_status()
184:         logger.info("Данные успешно отправлены на backend.")
185:     except Exception as e:
186:         logger.error("Ошибка при отправке данных на backend: %s", e)
187: 
188: def main():
189:     logger.info("Сервис запущен. Ожидаем сообщения из Kafka...")
190:     for message in consumer:
191:         if not running:
192:             break
193:         news = message.value
194:         if news is None:
195:             consumer.commit()
196:             continue
197:         logger.info("Получена новость: %s", news.get("title", "Без заголовка"))
198:         
199:         validated_tags = classify_news(news)
200:         news['tags'] = validated_tags
201:         
202:         # Если новость прошла классификацию (теги отличны от ["Не определено"]),
203:         # генерируем новое краткое содержание с помощью LLM 
204:         if "Не определено" not in validated_tags:
205:             new_summary = generate_summary(news)
206:             news['summary'] = new_summary
207:         send_to_backend(news)
208:         logger.info("Новость: %s", news.get("title"))
209:         logger.info("Теги: %s", news.get("tags"))
210:         consumer.commit()
211: 
212: if __name__ == "__main__":
213:     main()
```

## E06

Источник: [product/digest-creator/digest_service.py](../../product/digest-creator/digest_service.py). SHA-256: `024a8dc97c71ee4221bb112a70798d79a2cc08c0201b01d6bc84a7cd916768be`.

Строки 15–65:

```text
15: app = Flask(__name__)
16: 
17: @app.route('/', methods=['POST'])
18: def generate_digest():
19:     logger.info("Пришел запрос на создание дайджеста")
20:     data = request.get_json()
21:     logger.info(data)
22:     if not data or "documents" not in data:
23:         logger.info("Неверный формат запроса, возвращаю код 400")
24:         return jsonify({"error": "Неверный формат запроса"}), 400
25: 
26:     documents = data["documents"]
27: 
28:     # Собираем тексты новостей, разделяя их несколькими переносами строки
29:     prompt = PROMPT
30:     prompt += "\n\n".join(doc.get("text", "") for doc in documents)
31:     prompt += "\n\n\n" + INSTRUCTIONS
32: 
33:     # Если итоговый промпт слишком длинный, используем summary вместо text
34:     if len(prompt) > 90000:
35:         prompt = "\n\n".join(doc.get("summary", "") for doc in documents)
36: 
37:     payload = {
38:         "model": MODEL,
39:         "prompt": prompt,
40:         "stream": False
41:     }
42:     logger.info("отправляю запрос к моделям")
43:     response = requests.post(
44:         "http://ai.nt.fyi/api/generate",
45:         json=payload,
46:         auth=(LLM_API_AUTH_USER, LLM_API_AUTH_PASS)
47:     )
48: 
49:     if response.status_code != 200:
50:         logger.info("Ошибка при вызове LLM API")
51:         logger.info(jsonify({
52:             "error": "Ошибка при вызове LLM API",
53:             "details": response.text
54:         }))
55:         return jsonify({
56:             "error": "Ошибка при вызове LLM API",
57:             "details": response.text
58:         }), response.status_code
59: 
60:     # Возвращаем результат работы модели как ответ на исходный POST запрос
61:     logger.info("Дайджест отправляется на бекенд")
62:     return jsonify(response.json())
63: 
64: if __name__ == '__main__':
65:     app.run(host='0.0.0.0', debug=False)
```

## E07

Источник: [product/mailman/app.py](../../product/mailman/app.py). SHA-256: `0a904947cf3309488eaa5223a75b76241771e402d1150360cf88b791be46757a`.

Строки 16–76:

```text
16: def create_digest_html(title, web_link, text=None, pdf=False):
17:     content = "Содержимое дайджеста во вложении." if pdf else text
18:     html = f"""
19:     <html>
20:       <body style="background-color: #ffffff; color: #333333; font-family: Arial, sans-serif; padding: 20px;">
21:         <!-- Логотип компании -->
22:         <div style="text-align: center; margin-bottom: 20px;">
23:           <img src="https://cdn.emailacademy.com/user/unregistered/crosswords_logo2025_03_31_15_39_32.png" alt="Логотип компании" style="max-width:200px;">
24:         </div>
25:         <!-- Название письма -->
26:         <h1 style="text-align: center;"><b>{title}</b></h1>
27:         <!-- Основное содержимое -->
28:         <p>{content}</p>
29:         <!-- Кнопка с ссылкой на веб-версию -->
30:         <div style="text-align: center; margin-top: 30px;">
31:           <a href="{web_link}" style="display: inline-block; padding: 10px 20px; background-color: #ffdd2d; color: #333333; text-decoration: none; border-radius: 5px;">
32:             Открыть в корпусе
33:           </a>
34:         </div>
35:       </body>
36:     </html>
37:     """
38:     return html
39: 
40: @app.route('/send-email', methods=['POST'])
41: def send_email():
42:     if request.is_json:
43:         data = request.get_json()
44:         recipients = data.get('recipients')
45:         title = data.get('title')
46:         text = data.get('text')
47:         web_link = data.get('web_link')
48:         pdf_file = None
49:     else:
50:         recipients = request.form.getlist('recipients')
51:         title = request.form.get('title')
52:         text = request.form.get('text')
53:         web_link = request.form.get('web_link')
54:         pdf_file = request.files.get('pdf')
55: 
56:     if not recipients or not title or not web_link:
57:         return jsonify(
58:             {"error": "Отсутствуют необходимые параметры: recipients, title или web_link"}), 400
59:     pdf_present = pdf_file is not None
60: 
61:     if not pdf_present and not text:
62:         return jsonify({"error": "Не передан ни текст, ни PDF файл. Должен быть передан один из параметров."}), 400
63: 
64:     subject = f"Ежедневный дайджест - {title}"
65:     html_body = create_digest_html(title, web_link, text=text, pdf=pdf_present)
66: 
67:     try:
68:         msg = Message(subject, recipients=recipients, html=html_body)
69: 
70:         if pdf_present:
71:             pdf_data = pdf_file.read()
72:             msg.attach(pdf_file.filename, 'application/pdf', pdf_data)
73:         mail.send(msg)
74:         return jsonify({"message": "Email отправлен успешно"}), 200
75:     except Exception as e:
76:         return jsonify({"error": str(e)}), 500
```

Строки 103–118:

```text
103: @app.route('/verify_email', methods=['POST'])
104: def verify_email():
105:     data = request.get_json()
106:     email = data.get('email')
107:     code = data.get('code')
108: 
109:     if not email or not code:
110:         return jsonify({"error": "Поля 'email' и 'code' обязательны"}), 400
111: 
112:     try:
113:         html_body = create_verification_html(code)
114:         msg = Message("Код подтверждения регистрации", recipients=[email], html=html_body)
115:         mail.send(msg)
116:         return jsonify({"message": "Письмо с кодом отправлено"}), 200
117:     except Exception as e:
118:         return jsonify({"error": str(e)}), 500
```

## E08

Источник: [product/mobile/lib/services/api_service.dart](../../product/mobile/lib/services/api_service.dart). SHA-256: `5bdbb5ce001d67cb31d5de7d318d483392c3a1b55c594f9f1d16c70f80041172`.

Строки 16–58:

```text
16: class ApiService {
17:   // TODO поменять на false
18:   static final bool useMock = false;
19:   static bool isAuthenticatedMock = false;
20: 
21:   /// Экземпляр Dio с предопределенными параметрами и перехватчиками
22:   static final Dio _dio = Dio(
23:     BaseOptions(
24:       baseUrl: "http://crosswords-corpus.press:8081",
25:       connectTimeout: const Duration(seconds: 10),
26:       receiveTimeout: const Duration(seconds: 10),
27:       headers: {"Content-Type": "application/json"},
28:     ),
29:   );
30:   static final PersistCookieJar cookieJar = PersistCookieJar(
31:     storage: FileStorage('${Directory.systemTemp.path}/.cookies'),
32:   );
33:   static bool _interceptorsInitialized = false;
34: 
35:   static void initializeInterceptors() {
36:     if (_interceptorsInitialized) return;
37:     debugPrint(">>> Инициализация перехватчиков Dio");
38: 
39:     if (!kIsWeb) {
40:       _dio.interceptors.add(CookieManager(cookieJar));
41:     }
42: 
43:     _dio.interceptors.add(LogInterceptor(
44:       request: true,
45:       requestBody: true,
46:       responseBody: true,
47:       responseHeader: false,
48:       error: true,
49:     ));
50: 
51:     _dio.interceptors.add(InterceptorsWrapper(
52:       onError: (DioException e, ErrorInterceptorHandler handler) {
53:         if (e.response?.statusCode == 401) {
54:           /// TODO перенаправить на страницу логина
55:           debugPrint("401 Unauthorized — пользователь неавторизован");
56:         }
57:         return handler.next(e);
58:       },
```

Строки 84–106:

```text
84:       return;
85:     }
86: 
87:     final fcmToken = await FirebaseMessaging.instance.getToken();
88: 
89:     final response = await _dio.post("/users/login", data: {
90:       "username": username,
91:       "password": password,
92:       "fcm_token": fcmToken,
93:     });
94: 
95:     if (response.statusCode != 200) {
96:       throw Exception("Login failed with status code ${response.statusCode}");
97:     }
98:   }
99: 
100:   /// Регистрация пользователя
101:   static Future<void> register(
102:       String name, String surname, String email, String password) async {
103:     if (useMock) {
104:       await Future.delayed(const Duration(seconds: 1));
105:       return;
106:     }
```

## E09

Источник: [product/backend-web/.github/workflows/docker-publish.yml](../../product/backend-web/.github/workflows/docker-publish.yml). SHA-256: `55552b6d03ec02993c5ef8160dca508898dfbc835740012da6e91b517f8b5a98`.

Строки 1–40:

```text
1: name: Docker Build and Push
2: 
3: on:
4:   push:
5:     branches:
6:       - main
7:       - development
8: 
9: jobs:
10:   build-and-push:
11:     if: github.actor == 'cehhghost' || github.actor == 'shar3nda'
12:     runs-on: ubuntu-latest
13: 
14:     steps:
15:       # Шаг 1: Клонирование репозитория
16:       - name: Checkout repository
17:         uses: actions/checkout@v3
18: 
19:       # Шаг 2: Установка Java (для сборки JAR)
20:       - name: Set up JDK 17
21:         uses: actions/setup-java@v3
22:         with:
23:           java-version: '17'
24:           distribution: 'temurin'
25: 
26:       # Шаг 3: Сборка JAR-файла
27:       - name: Build JAR file
28:         run: |
29:           mvn -f backend/pom.xml clean package -DskipTests
30: 
31:       # Шаг 4: Настройка Docker Buildx
32:       - name: Set up Docker Buildx
33:         uses: docker/setup-buildx-action@v2
34: 
35:       # Шаг 5: Авторизация в Docker Hub
36:       - name: Log in to Docker Hub
37:         uses: docker/login-action@v2
38:         with:
39:           username: ${{ secrets.DOCKER_HUB_USERNAME }}
40:           password: ${{ secrets.DOCKER_HUB_TOKEN }}
```

Строки 55–77:

```text
55:           tags: |
56:             cehhghost/crosswords_backend:latest
57:             cehhghost/crosswords_backend:${{ steps.version.outputs.VERSION }}
58:   deploy:
59:     if: (github.actor == 'cehhghost' || github.actor == 'shar3nda') && github.ref == 'refs/heads/main'
60:     needs: build-and-push
61:     env:
62:       REPO: ${{ github.event.repository.name }}
63:     runs-on: ubuntu-latest
64:     steps:
65:       - name: Redeploy compose stack
66:         uses: appleboy/ssh-action@v1
67:         with:
68:           host: ${{ secrets.SSH_HOST }}
69:           username: ${{ secrets.SSH_USERNAME }}
70:           key: ${{ secrets.SSH_PRIVATE_KEY }}
71:           port: ${{ secrets.SSH_PORT }}
72:           script: |
73:             cd ~/Crosswords
74:             git pull
75:             docker compose pull
76:             docker compose up -d --force-recreate --wait
77:             docker ps
```

## E10

Источник: [product/backend-web/backend/src/test/java/com/backend/crosswords/CrosswordsApplicationTests.java](../../product/backend-web/backend/src/test/java/com/backend/crosswords/CrosswordsApplicationTests.java). SHA-256: `5d87039db03e6f9dd631fdcddcfee028c877ac7c6c88bd8b9f804bf51a577f18`.

Строки 1–13:

```text
1: package com.backend.crosswords;
2: 
3: import org.junit.jupiter.api.Test;
4: import org.springframework.boot.test.context.SpringBootTest;
5: 
6: @SpringBootTest
7: class CrosswordsApplicationTests {
8: 
9: 	@Test
10: 	void contextLoads() {
11: 	}
12: 
13: }
```

## E11

Источник: [product/mobile/test/widget_test.dart](../../product/mobile/test/widget_test.dart). SHA-256: `46d75c783ba00e1dc1fa30cf54aa8542f7f75593f1611049edd74bc1316d9570`.

Строки 1–30:

```text
1: // This is a basic Flutter widget test.
2: //
3: // To perform an interaction with a widget in your test, use the WidgetTester
4: // utility in the flutter_test package. For example, you can send tap and scroll
5: // gestures. You can also use WidgetTester to find child widgets in the widget
6: // tree, read text, and verify that the values of widget properties are correct.
7: 
8: import 'package:flutter/material.dart';
9: import 'package:flutter_test/flutter_test.dart';
10: 
11: import 'package:crosswords/main.dart';
12: 
13: void main() {
14:   testWidgets('Counter increments smoke test', (WidgetTester tester) async {
15:     // Build our app and trigger a frame.
16:     await tester.pumpWidget(const MyApp());
17: 
18:     // Verify that our counter starts at 0.
19:     expect(find.text('0'), findsOneWidget);
20:     expect(find.text('1'), findsNothing);
21: 
22:     // Tap the '+' icons and trigger a frame.
23:     await tester.tap(find.byIcon(Icons.add));
24:     await tester.pump();
25: 
26:     // Verify that our counter has incremented.
27:     expect(find.text('0'), findsNothing);
28:     expect(find.text('1'), findsOneWidget);
29:   });
30: }
```

## E12

Источник: [product/backend-web/frontend/package.json](../../product/backend-web/frontend/package.json). SHA-256: `4e60114804a374b1d30e48717cda69494c512dbc4f0c3b844c288f59127f13f8`.

Строки 1–43:

```text
1: {
2:   "name": "media-corpus-frontend",
3:   "version": "0.0.1",
4:   "description": "frontend for media-corpus",
5:   "productName": "media-corpus-frontend",
6:   "author": "mperestoronin <maxthirdmail@gmail.com>",
7:   "type": "module",
8:   "private": true,
9:   "scripts": {
10:     "lint": "eslint -c ./eslint.config.js \"./src*/**/*.{js,cjs,mjs,vue}\"",
11:     "format": "prettier --write \"**/*.{js,vue,scss,html,md,json}\" --ignore-path .gitignore",
12:     "test": "echo \"No test specified\" && exit 0",
13:     "dev": "quasar dev",
14:     "build": "quasar build",
15:     "postinstall": "quasar prepare"
16:   },
17:   "dependencies": {
18:     "@quasar/extras": "^1.16.4",
19:     "@recogito/recogito-js": "^1.8.4",
20:     "axios": "^1.7.9",
21:     "mitt": "^3.0.1",
22:     "quasar": "^2.16.0",
23:     "vue": "^3.4.18",
24:     "vue-router": "^4.0.0"
25:   },
26:   "devDependencies": {
27:     "@eslint/js": "^9.14.0",
28:     "@quasar/app-vite": "^2.0.0",
29:     "@vue/eslint-config-prettier": "^10.1.0",
30:     "autoprefixer": "^10.4.2",
31:     "eslint": "^9.14.0",
32:     "eslint-plugin-vue": "^9.30.0",
33:     "globals": "^15.12.0",
34:     "postcss": "^8.4.14",
35:     "prettier": "^3.3.3",
36:     "vite-plugin-checker": "^0.8.0"
37:   },
38:   "engines": {
39:     "node": "^28 || ^26 || ^24 || ^22 || ^20 || ^18",
40:     "npm": ">= 6.13.4",
41:     "yarn": ">= 1.21.1"
42:   }
43: }
```

## E13

Источник: [product/webscraper/dags/web_scraper_hub_dag.py](../../product/webscraper/dags/web_scraper_hub_dag.py). SHA-256: `ec6efeddaa6c56c89cec7122cd82c7f6bb4922b95cd0138507e31fe3c1d7b526`.

Строки 77–150:

```text
77: def fetch_news_from_source(source_config, **kwargs):
78:     ti = kwargs['ti']
79:     headers_list = ti.xcom_pull(task_ids='get_headers_list', key='headers_list')
80:     header = get_random_header(headers_list)
81:     fetch_func = source_config["fetch_links_func"]
82:     base_url = source_config["base_url"]
83:     country_code = source_config["country_code"]
84:     author_name = source_config["author_name"]
85:     news_links = fetch_func(base_url, header)
86:     news_articles = []
87:     producer = KafkaProducer(
88:         bootstrap_servers='kafka:9092',
89:         value_serializer=lambda v: json.dumps(v, ensure_ascii=False).encode('utf-8')
90:     )
91:     
92:     for url in news_links:
93:         time.sleep(randint(10, 100))
94:         article = prepare_article(url, header)
95:         article.authors = author_name
96:         
97:         # если article пуст — вызываем parse_<источник>_article, если он задан
98:         parse_func = source_config.get("parse_article_func")
99:         if ((article.title == "" or article.text == "") and parse_func) or (article.authors == "Центробанк Узбекистана"):
100:             article_content = parse_func(url, header)
101:             article.title = article_content.get("title", "")
102:             article.text = article_content.get("text", "")
103:             #  может дополнительно вызвать .parse()?
104:             article.nlp()
105:             
106:         # Отправляем в Kafka, только если есть заголовок и текст
107:         if article.title != "" and article.text != "":
108:             news = prepare_for_kafka( country_code, article.authors, article.title, article.text, article.summary, article.keywords, str(target_date.isoformat()), url)
109:             try:
110:                 producer.send("unclassified_news", value=news)
111:             except Exception as e:
112:                 print(f"Ошибка при отправке в Kafka: {e}")
113:             news_articles.append(article)
114:     producer.flush()
115:     producer.close()
116:     print(f"Новости {author_name}:", *[art.title for art in news_articles])
117: 
118: default_args = {
119:     'start_date': datetime(2025, 1, 1),
120:     'retries': 1,
121:     'retry_delay': timedelta(minutes=5)
122: }
123: 
124: dag = DAG(
125:     'web_scraper_hub_dag',
126:     default_args=default_args,
127:     description='Scrape news from various news sources',
128:     schedule_interval='50 20 * * *', # т.к. в москве +3 по часам от utc
129:     catchup=False,
130: )
131: 
132: 
133: init_headers_task = PythonOperator(
134:     task_id='get_headers_list',
135:     python_callable=get_headers_list,
136:     provide_context=True,
137:     dag=dag,
138: )
139: 
140: news_tasks = []
141: for source_key, config in SOURCES_CONFIG.items():
142:     task_id = f"fetch_{source_key}_news"
143:     task = PythonOperator(
144:         task_id=task_id,
145:         python_callable=partial(fetch_news_from_source, config),
146:         dag=dag
147:     )
148:     news_tasks.append(task)
149: 
150: 
```

## E14

Источник: [product/backend-web/backend/src/main/resources/application.properties](../../product/backend-web/backend/src/main/resources/application.properties). SHA-256: `8d151417179541677e2ab877066b2507ffa5d564ccdebba548ab64de26c7139e`.

Строки 1–45:

```text
1: spring.application.name=crosswords
2: opensearch.uris=${OPENSEARCH_URIS}
3: opensearch.username=${OPENSEARCH_USERNAME}
4: opensearch.password=${OPENSEARCH_PASSWORD}
5: spring.datasource.url=${SPRING_DATASOURCE_URL}
6: spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
7: spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
8: spring.datasource.driver-class-name=org.postgresql.Driver
9: spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO}
10: spring.jpa.database=postgresql
11: spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
12: spring.jpa.properties.hibernate.show_sql=${SPRING_JPA_PROPERTIES_HIBERNATE_SHOW_SQL}
13: server.port=8081
14: jwt_secret=${JWT_SECRET}
15: jwt_accessExpirationDate=15
16: jwt_refreshExpirationDate=10080
17: scheduler.cron=0 0 6 * * *
18: spring.thymeleaf.suffix=.html
19: spring.thymeleaf.cache=false
20: spring.thymeleaf.encoding=UTF-8
21: server.servlet.encoding.force-response=true
22: mailman.url=http://mailman:5001
23: mailman.send-email-path=/send-email
24: mailman.verify-email-path=/verify_email
25: mailman.connect-timeout=5000
26: mailman.response-timeout=10000
27: digest-generator.url=http://digest-service:5000
28: digest-generator.generate-digest-path=/
29: digest-generator.connect-timeout=5000
30: digest-generator.response-timeout=30000
31: backend-secret-key=${BACKEND_SECRET_KEY}
32: default-admins-password=${DEFAULT_ADMINS_PASSWORD}
33: springdoc.api-docs.enabled=${CREATE_SWAGGER}
34: springdoc.swagger-ui.enabled=${CREATE_SWAGGER}
35: springdoc.show-actuator=${CREATE_SWAGGER}
36: crosswords-firebase-credentials = ${CROSSWORDS_FIREBASE_CREDENTIALS}
37: crosswords-firebase-projectid = ${CROSSWORDS_FIREBASE_PROJECTID}
38: spring.flyway.enabled=false
39: spring.flyway.locations=classpath:db/migration
40: spring.flyway.baseline-on-migrate=true
41: spring.flyway.validate-on-migrate=true
42: spring.flyway.encoding=UTF-8
43: # Telegram Bot
44: telegram.bot.url=https://crosswords-tgbot.jujacloud.tech
45: telegram.bot.internal-secret=${TELEGRAM_BOT_INTERNAL_SECRET}
```

## E15

Источник: [product/backend-web/frontend/src/pages/DocumentPage.vue](../../product/backend-web/frontend/src/pages/DocumentPage.vue). SHA-256: `bc50c2b035b4a5f485b4d931b296d03c1ad4c073909d44ea27e72b58dba11980`.

Строки 14–80:

```text
14:           </div>
15:         </div>
16:         <div class="text-h5 q-my-sm">
17:           {{ documentData?.title }}
18:         </div>
19: 
20:         <div class="q-my-sm">
21:           <DocumentTags :tags="documentData?.tags" />
22:         </div>
23: 
24:         <div class="q-my-sm">
25:           <div class="q-mb-xs" v-if="documentData.is_authed">
26:             Оцените подобранные теги:
27:             <q-rating
28:               v-model="documentData.rating_classification"
29:               max="5"
30:               color="primary"
31:               icon="star"
32:               size="sm"
33:               no-reset
34:               @update:model-value="onRatingChange"
35:             />
36:           </div>
37:         </div>
38: 
39:         <q-expansion-item expand-separator>
40:           <template v-slot:header>
41:             <q-item-section avatar>
42:               <q-avatar icon="receipt" color="primary" text-color="secondary" />
43:             </q-item-section>
44:             <q-item-section class="text-h6"> Краткое содержание </q-item-section>
45:             <q-item-section side>
46:               <div class="row items-center">
47:                 <q-icon name="access_time" size="xs" class="q-mr-xs" />
48:                 <span>{{ readingTime }}</span>
49:               </div>
50:             </q-item-section>
51:           </template>
52: 
53:           <div class="q-pa-sm">
54:             {{ documentData?.summary }}
55:           </div>
56:         </q-expansion-item>
57: 
58:         <div ref="textContainer" class="q-my-md">
59:           {{ documentData?.text }}
60:         </div>
61:         <div v-if="documentData?.URL" class="q-mt-sm text-body2">
62:           Оригинал:
63:           <a
64:             :href="documentData?.URL"
65:             target="_blank"
66:             rel="noopener noreferrer"
67:             :class="$q.dark.isActive ? 'text-primary' : 'text-secondary'"
68:           >
69:             {{ documentData?.URL }}
70:           </a>
71:         </div>
72:       </q-card>
73: 
74:       <div class="row items-start wrap justify-between q-gutter-sm q-mt-md">
75:         <q-btn
76:           v-if="documentData?.URL"
77:           label="Читать оригинал"
78:           color="primary"
79:           size="md"
80:           no-caps
```

## E16

Источник: [product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/MailManService.java](../../product/backend-web/backend/src/main/java/com/backend/crosswords/corpus/services/MailManService.java). SHA-256: `ad09a2570166506dd117e48400fe054895e2cc3834189182c94af71ddcbddfcf`.

Строки 20–46:

```text
20:     public MailManService(@Qualifier("mailmanWebClient")WebClient webClient, MailmanProperties properties) {
21:         this.webClient = webClient;
22:         this.properties = properties;
23:     }
24:     public Mono<String> sendEmail(SendDigestByEmailsDTO request) throws ConnectionClosedException {
25:         return webClient.post()
26:                 .uri(properties.getSendEmailPath())
27:                 .contentType(MediaType.APPLICATION_JSON)
28:                 .bodyValue(request)
29:                 .retrieve()
30:                 .onStatus(HttpStatusCode::isError, error -> Mono.error(new ConnectionClosedException("Connection with mailman error")))
31:                 .bodyToMono(String.class);
32:     }
33:     public Mono<String> sendVerificationCode(SendVerificationCodeDTO request) throws ConnectionClosedException {
34:         try {
35:             return webClient.post()
36:                     .uri(properties.getVerifyEmailPath())
37:                     .contentType(MediaType.APPLICATION_JSON)
38:                     .bodyValue(request)
39:                     .retrieve()
40:                     .onStatus(HttpStatusCode::isError, error -> Mono.error(new ConnectionClosedException("Connection with mailman error")))
41:                     .bodyToMono(String.class);
42:         } catch (Exception e) {
43:             throw new ConnectionClosedException(e.getMessage());
44:         }
45:     }
46: }
```

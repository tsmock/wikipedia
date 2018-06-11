package org.wikipedia.api.wikidata_action.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.wikipedia.api.SerializationSchema;
import org.wikipedia.tools.RegexUtil;

public final class SitematrixResult {
    public static final SerializationSchema<SitematrixResult> SCHEMA = new SerializationSchema<>(
        SitematrixResult.class,
        mapper -> {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.registerModule(new SimpleModule().addDeserializer(
                SitematrixResult.Sitematrix.class,
                new SitematrixResult.Sitematrix.Deserializer(mapper)
            ));
        }
    );

    private final Sitematrix sitematrix;

    @JsonCreator
    public SitematrixResult(@JsonProperty("sitematrix") final Sitematrix sitematrix) {
        this.sitematrix = sitematrix;
    }

    public Sitematrix getSitematrix() {
        return sitematrix;
    }

    public static final class Sitematrix {
        private final Collection<Language> languages = new ArrayList<>();
        private final Collection<Site> specialSites = new ArrayList<>();

        private Sitematrix() {
            // Instantiates empty object
        }

        public Collection<Language> getLanguages() {
            return Collections.unmodifiableCollection(languages);
        }

        public Collection<Site> getSpecialSites() {
            return Collections.unmodifiableCollection(specialSites);
        }

        public static class Deserializer extends StdDeserializer<Sitematrix> {
            private final ObjectMapper mapper;
            public Deserializer(final ObjectMapper mapper) {
                super((Class<?>) null);
                this.mapper = mapper;
            }

            @Override
            public Sitematrix deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                final Sitematrix result = new Sitematrix();
                final JsonNode node = p.getCodec().readTree(p);
                final Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
                while (fields.hasNext()) {
                    final Map.Entry<String, JsonNode> field = fields.next();
                    if (RegexUtil.INTEGER_PATTERN.matcher(field.getKey()).matches()) {
                        result.languages.add(mapper.treeToValue(field.getValue(), Language.class));
                    }
                    if ("specials".equals(field.getKey())) {
                        final Iterator<JsonNode> elements = field.getValue().elements();
                        while (elements.hasNext()) {
                            result.specialSites.add(mapper.treeToValue(elements.next(), Site.class));
                        }
                    }
                }
                return result;
            }
        }

        public static final class Language {
            private final String code;
            private final String name;
            private final Collection<Site> sites = new ArrayList<>();

            @JsonCreator
            public Language(
                @JsonProperty("code") final String code,
                @JsonProperty("name") final String name,
                @JsonProperty("site") final Collection<Site> sites
            ) {
                this.code = code;
                this.name = name;
                if (sites != null) {
                    this.sites.addAll(sites);
                }
            }

            public String getCode() {
                return code;
            }

            public String getName() {
                return name;
            }

            public Collection<Site> getSites() {
                return Collections.unmodifiableCollection(sites);
            }
        }
        public static final class Site {
            private final boolean closed;
            private final String code;
            private final String dbName;
            private final String url;

            @JsonCreator
            public Site(
                @JsonProperty("url") final String url,
                @JsonProperty("dbname") final String dbName,
                @JsonProperty("code") final String code,
                @JsonProperty("closed") final String closed
            ) {
                this.closed = closed != null;
                this.code = code;
                this.dbName = dbName;
                this.url = url;
            }

            public String getCode() {
                return code;
            }

            public String getDbName() {
                return dbName;
            }

            public String getUrl() {
                return url;
            }

            public boolean isClosed() {
                return closed;
            }
        }
    }
}

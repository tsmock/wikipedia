// License: GPL. For details, see LICENSE file.
package org.wikipedia.data;

import java.awt.Color;
import java.util.Comparator;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.Tag;
import org.openstreetmap.josm.tools.AlphanumComparator;
import org.openstreetmap.josm.tools.CheckParameterUtil;
import org.openstreetmap.josm.tools.Utils;
import org.wikipedia.tools.RegexUtil;

public class WikidataEntry extends WikipediaEntry {

    public final String label;
    public final String description;

    public WikidataEntry(String id) {
        this(id, null, null, null);
    }

    public WikidataEntry(String id, String label, LatLon coordinate, String description) {
        super("wikidata", id, coordinate);
        this.label = label;
        this.description = description;
        ensureValidWikidataId(id);
    }

    @Override
    public Tag createWikipediaTag() {
        return new Tag("wikidata", article);
    }

    @Override
    public String getLabelText() {
        final String descriptionInParen = description == null ? "" : (" (" + description + ")");
        return getLabelText(label, article + descriptionInParen);
    }

    public static String getLabelText(String bold, String gray) {
        return getLabelText(bold, gray, Optional.empty());
    }

    public static String getLabelText(final String bold, final String colored, @Nonnull final Optional<Color> color) {
        return
            Utils.escapeReservedCharactersHTML(bold) +
            " <span color='" + color.map(it -> String.format("#%02x%02x%02x", it.getRed(), it.getGreen(), it.getBlue())).orElse("gray") + "'>" +
            Utils.escapeReservedCharactersHTML(colored) +
            "</span>";
    }

    @Override
    public String getSearchText() {
        return Optional.ofNullable(label).orElse(article);
    }

    private static void ensureValidWikidataId(String id) {
        CheckParameterUtil.ensureThat(RegexUtil.isValidQId(id), "Invalid Wikidata ID given: " + id);
    }

    private static final Comparator<WikidataEntry> WIKIDATA_ENTRY_COMPARATOR = Comparator
        .<WikidataEntry, String>comparing(x -> x.label, AlphanumComparator.getInstance())
        .thenComparing(x -> x.article, AlphanumComparator.getInstance());

    @Override
    public int compareTo(WikipediaEntry o) {
        if (o instanceof WikidataEntry) {
            return WIKIDATA_ENTRY_COMPARATOR.compare(this, ((WikidataEntry) o));
        } else {
            return super.compareTo(o);
        }
    }
}

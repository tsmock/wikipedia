// License: GPL. For details, see LICENSE file.
package org.wikipedia.gui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.junit.Rule;
import org.junit.Test;
import org.openstreetmap.josm.testutils.JOSMTestRules;

public class WikidataTagCellRendererTest {

    /**
     * Setup test.
     */
    @Rule
    public JOSMTestRules rules = new JOSMTestRules().preferences().i18n("en");

    @Test
    public void testRenderLabel() throws Exception {
        final List<String> ids = Arrays.asList("Q84", "Q1741", "Q278250");
        final WikidataTagCellRenderer renderer = new WikidataTagCellRenderer();
        renderer.renderValues(ids, new JTable(), new JLabel(), Optional.empty());
        for (String id : ids) {
            // wait for labels to be fetched
            renderer.labelCache.get(id).get();
        }
        final JLabel label = renderer.renderValues(ids, new JTable(), new JLabel(), Optional.empty());
        assertNotNull(label);
        assertThat(label.getText(), is("<html>" +
                "Q84 <span color='gray'>London</span>; " +
                "Q1741 <span color='gray'>Vienna</span>; " +
                "Q278250 <span color='gray'>Völs</span>"));
        assertThat(label.getToolTipText(), is("<html><ul>" +
                "<li>Q84 <span color='gray'>London</span></li>" +
                "<li>Q1741 <span color='gray'>Vienna</span></li>" +
                "<li>Q278250 <span color='gray'>Völs</span></li></ul>"));
    }
}

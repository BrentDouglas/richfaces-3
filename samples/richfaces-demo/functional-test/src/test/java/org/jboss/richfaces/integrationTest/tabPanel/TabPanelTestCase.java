/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and others contributors as indicated
 * by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */

package org.jboss.richfaces.integrationTest.tabPanel;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case that tests tab panel. Tests all three tabs -- usage, customization,
 * and deletion.
 * 
 * @author <a href="mailto:ppitonak@redhat.com">Pavol Pitonak</a>
 * @version $Revision$
 */
public class TabPanelTestCase extends AbstractSeleniumRichfacesTestCase {

    // messages
    private final String MSG_USAGE_CONTENT_OF_PANEL_PREFORMATTED = getMsg("USAGE_CONTENT_OF_PANEL_PREFORMATTED");
    private final String MSG_USAGE_SECOND_TAB_SHOULD_BE_DISABLED = getMsg("USAGE_SECOND_TAB_SHOULD_BE_DISABLED");
    private final String MSG_CUST_TABS_RIGHT_ALIGNED = getMsg("CUST_TABS_RIGHT_ALIGNED");
    private final String MSG_DELET_NUMBER_OF_TABS = getMsg("DELET_NUMBER_OF_TABS");

    // locators
    private final String LOC_USAGE_EXAMPLE_HEADER = getLoc("USAGE_EXAMPLE_HEADER");
    private final String LOC_USAGE_PANEL_TAB_PREFORMATTED = getLoc("USAGE_PANEL_TAB_PREFORMATTED");
    private final String LOC_USAGE_PANEL_TEXT_PREFORMATTED = getLoc("USAGE_PANEL_TEXT_PREFORMATTED");
    private final String LOC_USAGE_PANEL_3_TEXT_PREFORMATTED = getLoc("USAGE_PANEL_3_TEXT_PREFORMATTED");

    private final String LOC_CUST_EXAMPLE_HEADER = getLoc("CUST_EXAMPLE_HEADER");
    private final String LOC_CUST_TABS_ALIGN = getLoc("CUST_TABS_ALIGN");
    private final String LOC_CUST_PANEL_TEXT_PREFORMATTED = getLoc("CUST_PANEL_TEXT_PREFORMATTED");
    private final String LOC_CUST_PANEL_TAB_PREFORMATTED = getLoc("CUST_PANEL_TAB_PREFORMATTED");

    private final String LOC_DELET_EXAMPLE_HEADER = getLoc("DELET_EXAMPLE_HEADER");
    private final String LOC_DELET_PANEL_TABS = getLoc("DELET_PANEL_TABS");
    private final String LOC_DELET_PANEL_TAB_CLOSE_PREFORMATTED = getLoc("DELET_PANEL_TAB_CLOSE_PREFORMATTED");
    private final String LOC_DELET_RESET_BUTTON = getLoc("DELET_RESET_BUTTON");

    /**
     * Tests three tab panels from the usage tab. It click on each tab and
     * verifies that the content of panel changed appropriately.
     */
    @Test
    public void testTabPanelExample() {
        openTab("Usage");
        String text;

        // test the first tab panel
        for (int i = 3; i > 0; i--) {
            scrollIntoView(LOC_USAGE_EXAMPLE_HEADER, true); // scrolling is
            // forgotten after
            // page reload
            selenium.click(format(LOC_USAGE_PANEL_TAB_PREFORMATTED, 1, i));
            waitFor(2000);
            text = selenium.getText(format(LOC_USAGE_PANEL_TEXT_PREFORMATTED, 1));
            assertEquals(text, "Here is tab #" + i, format(MSG_USAGE_CONTENT_OF_PANEL_PREFORMATTED, 1));
        }

        // test the second tab panel
        scrollIntoView(LOC_USAGE_EXAMPLE_HEADER, true);
        selenium.click(format(LOC_USAGE_PANEL_TAB_PREFORMATTED, 2, 3));
        waitFor(2000);
        text = selenium.getText(format(LOC_USAGE_PANEL_TEXT_PREFORMATTED, 2));
        assertEquals(text, "Here is tab #" + 3, format(MSG_USAGE_CONTENT_OF_PANEL_PREFORMATTED, 2));

        selenium.click(format(LOC_USAGE_PANEL_TAB_PREFORMATTED, 2, 1));
        waitFor(2000);
        text = selenium.getText(format(LOC_USAGE_PANEL_TEXT_PREFORMATTED, 2));
        assertEquals(text, "Here is tab #" + 1, format(MSG_USAGE_CONTENT_OF_PANEL_PREFORMATTED, 2));

        // test the disabled tab
        boolean isEnabled = true;
        try {
            isEnabled = belongsClass("rich-tab-diabled", format(LOC_USAGE_PANEL_TAB_PREFORMATTED, 2, 2));
            assertFalse(isEnabled, MSG_USAGE_SECOND_TAB_SHOULD_BE_DISABLED);
        } catch (Exception e) {
            // OK - there is no class attribute
        }
        selenium.click(format(LOC_USAGE_PANEL_TAB_PREFORMATTED, 2, 2));
        waitFor(2000);
        text = selenium.getText(format(LOC_USAGE_PANEL_TEXT_PREFORMATTED, 2));
        assertEquals(text, "Here is tab #1", format(MSG_USAGE_CONTENT_OF_PANEL_PREFORMATTED, 2));

        // test the third tab panel -- client type work different
        scrollIntoView(LOC_USAGE_EXAMPLE_HEADER, true);
        for (int i = 3; i > 0; i--) {
            selenium.click(format(LOC_USAGE_PANEL_TAB_PREFORMATTED, 3, i));
            waitFor(2000);
            for (int j = 1; j < 4; j++) {
                if (i == j) {
                    assertTrue(isDisplayed(format(LOC_USAGE_PANEL_3_TEXT_PREFORMATTED, j)));
                } else {
                    assertFalse(isDisplayed(format(LOC_USAGE_PANEL_3_TEXT_PREFORMATTED, j)));
                }
            }
        }
    }

    /**
     * Tests tab panel customization. It clicks every tab and verifies that the
     * content of panel changed. Then it checks that the tabs are aligned to the
     * right.
     */
    @Test
    public void testCustomization() {
        openTab("Look Customization");
        scrollIntoView(LOC_CUST_EXAMPLE_HEADER, true);
        String text;

        for (int i = 3; i > 0; i--) {
            selenium.click(format(LOC_CUST_PANEL_TAB_PREFORMATTED, i));
            waitFor(2000);
            for (int j = 1; j < 4; j++) {
                if (i == j) {
                    waitFor(5000);
                    assertTrue(isDisplayed(format(LOC_CUST_PANEL_TEXT_PREFORMATTED, j)), format(
                            "Tab nr. {0} should be open.", j));
                } else {
                    assertFalse(isDisplayed(format(LOC_CUST_PANEL_TEXT_PREFORMATTED, j)), format(
                            "Tab nr. {0} should not be open.", j));
                }
            }
        }

        text = selenium.getAttribute(LOC_CUST_TABS_ALIGN);
        assertEquals(text, "right", MSG_CUST_TABS_RIGHT_ALIGNED);
    }

    /**
     * Tests tabs deletion. First it check that there are 3 tabs, then deletes
     * the third, the second, and the first tab. After each deletion it verifies
     * the number of remaining tabs. In the end it clicks the reset button and
     * checks that all tabs were restored.
     */
    @Test
    public void testTabsDeletion() {
        openTab("Tabs Deletion");
        scrollIntoView(LOC_DELET_EXAMPLE_HEADER, true);

        int count = selenium.getXpathCount(LOC_DELET_PANEL_TABS).intValue();
        assertEquals(count - 4, 3, MSG_DELET_NUMBER_OF_TABS);

        for (int i = 3; i > 0; i--) {
            selenium.click(format(LOC_DELET_PANEL_TAB_CLOSE_PREFORMATTED, i));
            waitFor(2000);
            count = selenium.getXpathCount(LOC_DELET_PANEL_TABS).intValue();
            assertEquals(count / 2, i - 1, MSG_DELET_NUMBER_OF_TABS);
        }

        selenium.click(LOC_DELET_RESET_BUTTON);
        waitFor(2000);
        count = selenium.getXpathCount(LOC_DELET_PANEL_TABS).intValue();
        assertEquals(count - 4, 3, MSG_DELET_NUMBER_OF_TABS);
    }

    /**
     * Tests the "View Source". It checks that the source code is not visible,
     * clicks on the link, and checks 5 lines of source code.
     */
    @Test
    public void testUsageTabExampleSource() {
        String[] strings = new String[] { "<ui:composition xmlns=\"http://www.w3.org/1999/xhtml\"", "<rich:tabPanel>",
                "<rich:tab label=\"First\">", "Here is tab #2", "<rich:tabPanel switchType=\"ajax\">",
                "<rich:tab label=\"Second\" disabled=\"true\">", "<rich:tabPanel switchType=\"client\">", };

        abstractTestSource(1, "View Source", strings);
    }

    /**
     * Loads the page containing the calendar component.
     */
    @SuppressWarnings("unused")
    @BeforeMethod
    private void loadPage() {
        openComponent("Tab Panel");
    }

}

/**
 * License Agreement.
 *
 *  JBoss RichFaces
 *
 * Copyright (C) 2009  Red Hat, Inc.
 *
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this code; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.jboss.richfaces.integrationTest.panelBar;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case that tests panel bar.
 * 
 * @author <a href="mailto:ppitonak@redhat.com">Pavol Pitonak</a>
 * @version $Revision$
 */
public class PanelBarTestCase extends AbstractSeleniumRichfacesTestCase {

	// messages
	private final String MSG_HEADER_N = getMsg("HEADER_N");
	private final String MSG_PANEL_N_SHOULD_BE_VISIBLE = getMsg("PANEL_N_SHOULD_BE_VISIBLE");
	private final String MSG_PANEL_N_SHOULD_NOT_BE_VISIBLE = getMsg("PANEL_N_SHOULD_NOT_BE_VISIBLE");

	// locators
	private final String LOC_EXAMPLE_HEADER = getLoc("EXAMPLE_HEADER");
	private final String LOC_HEADER_1_N = getLoc("HEADER_1_N");
	private final String LOC_HEADER_2_N = getLoc("HEADER_2_N");
	private final String LOC_CONTENT_N = getLoc("CONTENT_N");

	/**
	 * Tests all components as they appear after page is loaded.
	 */
	@Test
	public void testInitialState() {
		String[] headers = new String[] {
				"Leverage the whole set of JSF benefits while working with AJAX",
				"Add AJAX capability to existing JSF applications",
				"Write your own custom rich components with built-in AJAX support",
				"Package resources with the application's Java classes",
				"Easily generate images on-the-fly",
				"Create a modern rich user interface look-and-feel with skins-based technology",
				"Test the components, actions, listeners, and pages as you are creating them" };

        String text = selenium.getText(format(LOC_HEADER_2_N, 1));
		assertEquals(text, headers[0], format(MSG_HEADER_N, 1));
		assertTrue(isDisplayed(format(LOC_CONTENT_N, 1)), format(MSG_PANEL_N_SHOULD_BE_VISIBLE, 1));

		for (int i = 2; i < 8; i++) {
			text = selenium.getText(format(LOC_HEADER_1_N, i));
			assertEquals(text, headers[i - 1], format(MSG_HEADER_N, i));
			assertFalse(isDisplayed(format(LOC_CONTENT_N, i)), format(MSG_PANEL_N_SHOULD_NOT_BE_VISIBLE, i));
		}
	}

	/**
	 * Tests expanding each panel. It verifies that the clicked panel is visible
	 * and that none of the other panels is visible.
	 */
	@Test
	public void testExpanding() {
		for (int i = 7; i > 0; i--) {
			selenium.click(format(LOC_HEADER_1_N, i));

			// check that clicked panel is visible
			assertTrue(isDisplayed(format(LOC_CONTENT_N, i)), format(MSG_PANEL_N_SHOULD_BE_VISIBLE, i));

			// check that other panel are not visible
			for (int j = 1; j < 8; j++) {
				if (j == i) {
					continue;
				}
				assertFalse(isDisplayed(format(LOC_CONTENT_N, j)), format(MSG_PANEL_N_SHOULD_NOT_BE_VISIBLE, i));
			}
		}
	}

	/**
	 * Tests the "View Source". It checks that the source code is not visible,
	 * clicks on the link, and checks 6 lines of source code.
	 */
	@Test
	public void testPageSource() {
		String[] strings = new String[] {
				"<ui:composition xmlns=\"http://www.w3.org/1999/xhtml\"",
				"<rich:panelBar height=\"400\" width=\"500\">",
				"label=\"Leverage the whole set of JSF benefits while working with AJAX\">",
				"</rich:panelBarItem>", "</rich:panelBar>",
				"</ui:composition>", };

		abstractTestSource(1, "View Source", strings);
	}

	/**
	 * Loads the page containing the component.
	 */
	@BeforeMethod
	private void loadPage() {
	    openComponent("Panel Bar");
		scrollIntoView(LOC_EXAMPLE_HEADER, true);
	}
}
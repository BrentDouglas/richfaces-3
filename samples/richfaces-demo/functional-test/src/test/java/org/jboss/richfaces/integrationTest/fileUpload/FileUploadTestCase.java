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

package org.jboss.richfaces.integrationTest.fileUpload;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.jboss.richfaces.integrationTest.AbstractSeleniumRichfacesTestCase;
import org.jboss.test.selenium.waiting.Condition;
import org.jboss.test.selenium.waiting.Wait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test case that tests the component for uploading files. Before test it
 * generates 6 images, each with size 128x128px with solid color - one of red,
 * blue, cyan, yellow, orange, or green as defined in the java.awt.Color. It
 * saves the images in temporary directory. After test it deletes all files.
 * 
 * @author <a href="mailto:ppitonak@redhat.com">Pavol Pitonak</a>
 * @version $Revision$
 */
public class FileUploadTestCase extends AbstractSeleniumRichfacesTestCase {

    private static final String FILE_RED = "/tmp/selenium-test" + Color.RED.getRGB() + ".jpg";
    private static final String FILE_BLUE = "/tmp/selenium-test" + Color.BLUE.getRGB() + ".jpg";
    private static final String FILE_CYAN = "/tmp/selenium-test" + Color.CYAN.getRGB() + ".jpg";
    private static final String FILE_YELLOW = "/tmp/selenium-test" + Color.YELLOW.getRGB() + ".jpg";
    private static final String FILE_ORANGE = "/tmp/selenium-test" + Color.ORANGE.getRGB() + ".jpg";
    private static final String FILE_BIG = "/tmp/selenium-test" + Color.GREEN.getRGB() + ".jpg";

    // messages
    private final String MSG_COULD_NOT_CREATE_IMAGE = getMsg("COULD_NOT_CREATE_IMAGE");
    private final String MSG_PROVIDED_URL_NOT_VALID = getMsg("PROVIDED_URL_NOT_VALID");
    private final String MSG_COULD_NOT_READ_IMAGE = getMsg("COULD_NOT_READ_IMAGE");

    private final String MSG_LEFT_PANEL_NUMBER_OF_ITEMS = getMsg("LEFT_PANEL_NUMBER_OF_ITEMS");
    private final String MSG_LEFT_PANEL_NAME_N = getMsg("LEFT_PANEL_NAME_N");
    private final String MSG_LEFT_PANEL_CANCEL_N = getMsg("LEFT_PANEL_CANCEL_N");
    private final String MSG_LEFT_PANEL_DONE_N = getMsg("LEFT_PANEL_DONE_N");

    private final String MSG_RIGHT_PANEL_CONTENT = getMsg("RIGHT_PANEL_CONTENT");
    private final String MSG_RIGHT_PANEL_NUMBER_OF_ITEMS = getMsg("RIGHT_PANEL_NUMBER_OF_ITEMS");
    private final String MSG_RIGHT_PANEL_NAME_N = getMsg("RIGHT_PANEL_NAME_N");
    private final String MSG_RIGHT_PANEL_SIZE_N = getMsg("RIGHT_PANEL_SIZE_N");
    private final String MSG_RIGHT_PANEL_COLOR_X_Y = getMsg("RIGHT_PANEL_COLOR_X_Y");

    private final String MSG_ADD_BUTTON_ENABLED = getMsg("ADD_BUTTON_ENABLED");
    private final String MSG_ADD_BUTTON_NOT_ENABLED = getMsg("ADD_BUTTON_NOT_ENABLED");
    private final String MSG_UPLOAD_BUTTON_VISIBLE = getMsg("UPLOAD_BUTTON_VISIBLE");
    private final String MSG_UPLOAD_BUTTON_NOT_VISIBLE = getMsg("UPLOAD_BUTTON_NOT_VISIBLE");
    private final String MSG_CLEAR_ALL_BUTTON_VISIBLE = getMsg("CLEAR_ALL_BUTTON_VISIBLE");
    private final String MSG_CLEAR_ALL_BUTTON_NOT_VISIBLE = getMsg("CLEAR_ALL_BUTTON_NOT_VISIBLE");
    private final String MSG_CLEAR_UPLOADED_DATA_BUTTON_VISIBLE = getMsg("CLEAR_UPLOADED_DATA_BUTTON_VISIBLE");
    private final String MSG_CLEAR_UPLOADED_DATA_BUTTON_NOT_VISIBLE = getMsg("CLEAR_UPLOADED_DATA_BUTTON_NOT_VISIBLE");

    // locators
    private final String LOC_EXAMPLE_HEADER = getLoc("EXAMPLE_HEADER");
    private final String LOC_ADD_BUTTON_N = getLoc("ADD_BUTTON_N");
    private final String LOC_ADD_BUTTON_CLASS = getLoc("ADD_BUTTON_CLASS");
    private final String LOC_UPLOAD_BUTTON = getLoc("UPLOAD_BUTTON");
    private final String LOC_UPLOAD_BUTTON_STYLE = getLoc("UPLOAD_BUTTON_STYLE");
    private final String LOC_CLEAR_ALL_BUTTON = getLoc("CLEAR_ALL_BUTTON");
    private final String LOC_CLEAR_ALL_BUTTON_STYLE = getLoc("CLEAR_ALL_BUTTON_STYLE");
    private final String LOC_CLEAR_UPLOADED_DATA_BUTTON = getLoc("CLEAR_UPLOADED_DATA_BUTTON");

    private final String LOC_NOT_UPLOADED_LIST_TR = getLoc("NOT_UPLOADED_LIST_TR");
    private final String LOC_NOT_UPLOADED_LIST_N_NAME = getLoc("NOT_UPLOADED_LIST_N_NAME");
    private final String LOC_NOT_UPLOADED_LIST_N_DONE = getLoc("NOT_UPLOADED_LIST_N_DONE");
    private final String LOC_NOT_UPLOADED_LIST_N_CANCEL = getLoc("NOT_UPLOADED_LIST_N_CANCEL");

    private final String LOC_UPLOADED_FILES_INFO = getLoc("UPLOADED_FILES_INFO");
    private final String LOC_UPLOADED_LIST_TR = getLoc("UPLOADED_LIST_TR");
    private final String LOC_UPLOADED_LIST_N_IMG = getLoc("UPLOADED_LIST_N_IMG");
    private final String LOC_UPLOADED_LIST_N_NAME = getLoc("UPLOADED_LIST_N_NAME");
    private final String LOC_UPLOADED_LIST_N_SIZE = getLoc("UPLOADED_LIST_N_SIZE");

    private final String LOC_AUTOMATIC_UPLOAD = getLoc("AUTOMATIC_UPLOAD");
    private final String LOC_FLASH = getLoc("FLASH");

    /**
     * Tests uploading one file. First it checks that the component is empty,
     * add button is enabled and that upload button and clear all buttons are
     * disabled. Then it checks that button for clearing uploaded data is not
     * visible. After that it attaches yellow picture and verifies that it
     * appeared in left part of component. It checks all buttons again and
     * clicks the upload button. Then it checks all button third time.
     */
    @Test
    public void testUploadOneFile() {
        String text = selenium.getText(LOC_UPLOADED_FILES_INFO);
        assertEquals(text, "No files currently uploaded", MSG_RIGHT_PANEL_CONTENT);

        int count = selenium.getXpathCount(LOC_NOT_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 0, MSG_LEFT_PANEL_NUMBER_OF_ITEMS);

        assertTrue(!belongsClass("rich-fileupload-button-dis", LOC_ADD_BUTTON_CLASS), MSG_ADD_BUTTON_ENABLED);
        assertFalse(isDisplayed(LOC_UPLOAD_BUTTON_STYLE), MSG_UPLOAD_BUTTON_NOT_VISIBLE);
        assertFalse(isDisplayed(LOC_CLEAR_ALL_BUTTON_STYLE), MSG_CLEAR_ALL_BUTTON_NOT_VISIBLE);

        boolean isPresent = selenium.isElementPresent(LOC_CLEAR_UPLOADED_DATA_BUTTON);
        assertFalse(isPresent, MSG_CLEAR_UPLOADED_DATA_BUTTON_NOT_VISIBLE);

        selenium.attachFile(format(LOC_ADD_BUTTON_N, 1), "file://" + FILE_YELLOW);

        count = selenium.getXpathCount(LOC_NOT_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 1, MSG_LEFT_PANEL_NUMBER_OF_ITEMS);

        assertTrue(!belongsClass("rich-fileupload-button-dis", LOC_ADD_BUTTON_CLASS), MSG_ADD_BUTTON_ENABLED);
        assertTrue(isDisplayed(LOC_UPLOAD_BUTTON_STYLE), MSG_UPLOAD_BUTTON_VISIBLE);
        assertFalse(isDisplayed(LOC_CLEAR_ALL_BUTTON_STYLE), MSG_CLEAR_ALL_BUTTON_NOT_VISIBLE);

        isPresent = selenium.isElementPresent(LOC_CLEAR_UPLOADED_DATA_BUTTON);
        assertFalse(isPresent, MSG_CLEAR_UPLOADED_DATA_BUTTON_NOT_VISIBLE);

        selenium.click(LOC_UPLOAD_BUTTON);
        waitFor(2000);

        assertTrue(!belongsClass("rich-fileupload-button-dis", LOC_ADD_BUTTON_CLASS), MSG_ADD_BUTTON_ENABLED);
        assertFalse(isDisplayed(LOC_UPLOAD_BUTTON_STYLE), MSG_UPLOAD_BUTTON_NOT_VISIBLE);
        assertTrue(isDisplayed(LOC_CLEAR_ALL_BUTTON_STYLE), MSG_CLEAR_ALL_BUTTON_VISIBLE);

        isPresent = selenium.isElementPresent(LOC_CLEAR_UPLOADED_DATA_BUTTON);
        assertTrue(isPresent, MSG_CLEAR_UPLOADED_DATA_BUTTON_VISIBLE);
    }

    /**
     * Tests information in the left list. First it attaches 2 files -- yellow
     * and blue. Then it verifies that these two files appeared in left list. It
     * verifies the name of the file in the list and checks that the cancel
     * button is visible. Then it clicks upload button and verifies that there
     * are two files in the right list. Then it checks the left list again.
     */
    @Test
    void testFilesToBeUploadedInfo() {
        int count = selenium.getXpathCount(LOC_NOT_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 0, MSG_LEFT_PANEL_NUMBER_OF_ITEMS);

        selenium.attachFile(format(LOC_ADD_BUTTON_N, 1), "file://" + FILE_YELLOW);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 2), "file://" + FILE_BLUE);

        count = selenium.getXpathCount(LOC_NOT_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 2, MSG_LEFT_PANEL_NUMBER_OF_ITEMS);

        String text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_NAME, 1));
        assertEquals(text, "selenium-test" + Color.YELLOW.getRGB() + ".jpg", format(MSG_LEFT_PANEL_NAME_N, 1));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_NAME, 2));
        assertEquals(text, "selenium-test" + Color.BLUE.getRGB() + ".jpg", format(MSG_LEFT_PANEL_NAME_N, 2));

        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_CANCEL, 1));
        assertEquals(text, "Cancel", format(MSG_LEFT_PANEL_CANCEL_N, 1));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_CANCEL, 2));
        assertEquals(text, "Cancel", format(MSG_LEFT_PANEL_CANCEL_N, 2));

        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_DONE, 1));
        assertEquals(text, "", format(MSG_LEFT_PANEL_DONE_N, 1));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_DONE, 2));
        assertEquals(text, "", format(MSG_LEFT_PANEL_DONE_N, 1));

        selenium.click(LOC_UPLOAD_BUTTON);
        waitFor(2000);

        count = selenium.getXpathCount(LOC_NOT_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 2, MSG_LEFT_PANEL_NUMBER_OF_ITEMS);

        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_NAME, 1));
        assertEquals(text, "selenium-test" + Color.YELLOW.getRGB() + ".jpg", format(MSG_LEFT_PANEL_NAME_N, 1));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_NAME, 2));
        assertEquals(text, "selenium-test" + Color.BLUE.getRGB() + ".jpg", format(MSG_LEFT_PANEL_NAME_N, 2));

        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_CANCEL, 1));
        assertEquals(text, "Clear", format(MSG_LEFT_PANEL_CANCEL_N, 1));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_CANCEL, 2));
        assertEquals(text, "Clear", format(MSG_LEFT_PANEL_CANCEL_N, 1));

        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_DONE, 1));
        assertEquals(text, "Done", format(MSG_LEFT_PANEL_DONE_N, 1));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_DONE, 2));
        assertEquals(text, "Done", format(MSG_LEFT_PANEL_DONE_N, 1));
    }

    /**
     * Tests information in the right part of the component. First it uploads
     * two files -- cyan and orange. Then it checks the name and size of both
     * files in the right list.
     */
    @Test
    public void testUploadedFilesInfo() {
        int count = selenium.getXpathCount(LOC_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 0, MSG_RIGHT_PANEL_NUMBER_OF_ITEMS);

        selenium.attachFile(format(LOC_ADD_BUTTON_N, 1), "file://" + FILE_CYAN);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 2), "file://" + FILE_ORANGE);

        selenium.click(LOC_UPLOAD_BUTTON);

        count = selenium.getXpathCount(LOC_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 0, MSG_RIGHT_PANEL_NUMBER_OF_ITEMS);

        waitForElement(format(LOC_UPLOADED_LIST_N_NAME, 1));

        String text = selenium.getText(format(LOC_UPLOADED_LIST_N_NAME, 1));
        assertEquals(text, "selenium-test" + Color.CYAN.getRGB() + ".jpg", format(MSG_RIGHT_PANEL_NAME_N, 1));
        text = selenium.getText(format(LOC_UPLOADED_LIST_N_NAME, 2));
        assertEquals(text, "selenium-test" + Color.ORANGE.getRGB() + ".jpg", format(MSG_RIGHT_PANEL_NAME_N, 2));

        long size1 = Long.parseLong(selenium.getText(format(LOC_UPLOADED_LIST_N_SIZE, 1)));
        long size2 = new File(FILE_CYAN).length();
        assertEquals(size1, size2, format(MSG_RIGHT_PANEL_SIZE_N, 1));

        size1 = Long.parseLong(selenium.getText(format(LOC_UPLOADED_LIST_N_SIZE, 2)));
        size2 = new File(FILE_ORANGE).length();
        assertEquals(size1, size2, format(MSG_RIGHT_PANEL_SIZE_N, 1));

        // FIXME it cannot download the image
        // String url =
        // selenium.getAttribute(format(LOC_UPLOADED_LIST_N_IMG, 1) +
        // "@src");
        // assertImageColorEquals(url, Color.CYAN);
        //		    
        // url = selenium.getAttribute(format(LOC_UPLOADED_LIST_N_IMG, 2)
        // + "@src");
        // assertImageColorEquals(url, Color.BLUE);
    }

    /**
     * Tests automatic upload. First it clicks the button activating automatic
     * upload and attaches the cyan image. Then it waits for the right list to
     * update.
     */
    @Test
    public void testAutomaticUpload() {
        selenium.click(LOC_AUTOMATIC_UPLOAD);

        int count = selenium.getXpathCount(LOC_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 0, MSG_RIGHT_PANEL_NUMBER_OF_ITEMS);

        selenium.attachFile(format(LOC_ADD_BUTTON_N, 1), "file://" + FILE_CYAN);

        waitForElement(LOC_UPLOADED_LIST_TR);
        count = selenium.getXpathCount(LOC_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 1, MSG_RIGHT_PANEL_NUMBER_OF_ITEMS);
    }

    /**
     * Tests uploading maximal allowed number of files -- 5.
     */
    @Test
    public void testUploadFiveFiles() {
        assertTrue(!belongsClass("rich-fileupload-button-dis", LOC_ADD_BUTTON_CLASS), MSG_ADD_BUTTON_ENABLED);

        selenium.attachFile(format(LOC_ADD_BUTTON_N, 1), "file://" + FILE_YELLOW);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 2), "file://" + FILE_BLUE);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 3), "file://" + FILE_CYAN);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 4), "file://" + FILE_ORANGE);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 5), "file://" + FILE_RED);

        assertFalse(!belongsClass("rich-fileupload-button-dis", LOC_ADD_BUTTON_CLASS), MSG_ADD_BUTTON_NOT_ENABLED);
    }

    /**
     * Tests uploading a file with size that is bigger than allowed maximum.
     * First it attaches yellow image, then the big image and clicks the upload
     * button. Then it checks properties of files in the left list.
     */
    @Test
    public void testUploadBigFile() {
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 1), "file://" + FILE_YELLOW);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 2), "file://" + FILE_BIG);

        selenium.click(LOC_UPLOAD_BUTTON);
        waitFor(2000);

        String text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_NAME, 1));
        assertEquals(text, "selenium-test" + Color.YELLOW.getRGB() + ".jpg", format(MSG_LEFT_PANEL_NAME_N, 1));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_NAME, 2));
        assertEquals(text, "selenium-test" + Color.GREEN.getRGB() + ".jpg", format(MSG_LEFT_PANEL_NAME_N, 2));

        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_CANCEL, 1));
        assertEquals(text, "Clear", format(MSG_LEFT_PANEL_CANCEL_N, 1));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_CANCEL, 2));
        assertEquals(text, "Cancel", format(MSG_LEFT_PANEL_CANCEL_N, 2));

        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_DONE, 1));
        assertEquals(text, "Done", format(MSG_LEFT_PANEL_DONE_N, 2));
        text = selenium.getText(format(LOC_NOT_UPLOADED_LIST_N_DONE, 2));
        assertEquals(text, "File size restricted", format(MSG_LEFT_PANEL_DONE_N, 2));

        int count = selenium.getXpathCount(LOC_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 1, MSG_RIGHT_PANEL_NUMBER_OF_ITEMS);
    }

    /**
     * Tests the "Clear All" button. It uploads two files -- yellow and cyan,
     * verifies that the clear all button is visible, and clicks it. Then it
     * checks that the button disappeared and that the left list is empty.
     */
    @Test
    public void testClearAllButton() {
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 1), "file://" + FILE_YELLOW);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 2), "file://" + FILE_CYAN);

        assertFalse(isDisplayed(LOC_CLEAR_ALL_BUTTON_STYLE), MSG_CLEAR_ALL_BUTTON_NOT_VISIBLE);

        selenium.click(LOC_UPLOAD_BUTTON);
        waitForElement(LOC_UPLOADED_LIST_TR);

        assertTrue(isDisplayed(LOC_CLEAR_ALL_BUTTON_STYLE), MSG_CLEAR_ALL_BUTTON_VISIBLE);

        selenium.click(LOC_CLEAR_ALL_BUTTON);

        assertFalse(isDisplayed(LOC_CLEAR_ALL_BUTTON_STYLE), MSG_CLEAR_ALL_BUTTON_NOT_VISIBLE);

        int count = selenium.getXpathCount(LOC_NOT_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 0, MSG_LEFT_PANEL_NUMBER_OF_ITEMS);
    }

    /**
     * Tests the "Clear Up Uploaded Data" button. First it verifies that the
     * button is hidden and then uploads two images - yellow and cyan. Then it
     * checks that the button is visible now and clicks it. After that it checks
     * that button disappeared again. At the end it verifies that both left and
     * right lists are empty.
     */
    @Test
    public void testClearUploadedDataButton() {
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 1), "file://" + FILE_YELLOW);
        selenium.attachFile(format(LOC_ADD_BUTTON_N, 2), "file://" + FILE_CYAN);

        assertFalse(selenium.isElementPresent(LOC_CLEAR_UPLOADED_DATA_BUTTON),
                MSG_CLEAR_UPLOADED_DATA_BUTTON_NOT_VISIBLE);

        waitFor(2000);

        selenium.click(LOC_UPLOAD_BUTTON);
        waitForElement(LOC_UPLOADED_LIST_TR);

        assertTrue(selenium.isElementPresent(LOC_CLEAR_UPLOADED_DATA_BUTTON), MSG_CLEAR_UPLOADED_DATA_BUTTON_VISIBLE);

        selenium.click(LOC_CLEAR_UPLOADED_DATA_BUTTON);

        // wait for JavaScript to finish its work
        Wait.until(new Condition() {
            public boolean isTrue() {
                return !selenium.isElementPresent(LOC_UPLOADED_LIST_TR);
            }
        });

        assertFalse(selenium.isElementPresent(LOC_CLEAR_UPLOADED_DATA_BUTTON),
                MSG_CLEAR_UPLOADED_DATA_BUTTON_NOT_VISIBLE);

        int count = selenium.getXpathCount(LOC_NOT_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 0, MSG_LEFT_PANEL_NUMBER_OF_ITEMS);

        count = selenium.getXpathCount(LOC_UPLOADED_LIST_TR).intValue();
        assertEquals(count, 0, MSG_RIGHT_PANEL_NUMBER_OF_ITEMS);
    }

    // /**
    // * TODO Test the Flash version of the component.
    // */
    // @Test
    // public void testFlash() {
    //
    // }

    /**
     * Tests the "View Source". It checks that the source code is not visible,
     * clicks on the link, and checks 8 lines of source code.
     */
    @Test
    public void testPageSource() {
        String[] strings = new String[] { "<ui:composition xmlns=\"http://www.w3.org/1999/xhtml\"",
                "<h:panelGrid columns=\"2\" columnClasses=\"top,top\">",
                "<rich:fileUpload fileUploadListener=\"#{fileUploadBean.listener}\"",
                "maxFilesQuantity=\"#{fileUploadBean.uploadsAvailable}\"", "id=\"upload\"",
                "immediateUpload=\"#{fileUploadBean.autoUpload}\"",
                "acceptedTypes=\"jpg, gif, png, bmp\" allowFlash=\"#{fileUploadBean.useFlash}\">",
                "<a4j:support event=\"onuploadcomplete\" reRender=\"info\" />", };

        abstractTestSource(1, "View Source", strings);
    }

    /**
     * Tests the "View FileUploadBean.java Source". It checks that the source
     * code is not visible, clicks on the link, and checks 8 lines of source
     * code.
     */
    @Test
    public void testBeanSource() {
        String[] strings = new String[] { "package org.richfaces.demo.fileUpload;", "public FileUploadBean() {",
                "public void paint(OutputStream stream, Object object) throws IOException {",
                "public void listener(UploadEvent event) throws Exception{", "public String clearUploadData() {",
                "public long getTimeStamp(){", "public ArrayList<File> getFiles() {",
                "public void setFiles(ArrayList<File> files) { ", };

        abstractTestSource(1, "View FileUploadBean.java Source", strings);
    }

    /**
     * Creates an image of given width, height, and color.
     * 
     * @param c
     *            color of the image to be created
     * @param width
     *            width of the image to be created
     * @param height
     *            height of the image to be created
     */
    private void createImage(Color c, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(c);
        graphics.fillRect(0, 0, width, height);
        try {
            ImageIO.write(image, "JPEG", new File("/tmp/selenium-test" + c.getRGB() + ".jpg"));
        } catch (IOException e) {
            fail(MSG_COULD_NOT_CREATE_IMAGE);
        }
    }

    /**
     * Asserts that the image's color equals the one from method's parameter.
     * 
     * @param url
     *            relative url of the image
     * @param expecteColor
     *            expected color of the image
     */
    private void assertImageColorEquals(String url, Color expectedColor) {
        assertEquals(getPixelColor(url, 0, 0), expectedColor, format(MSG_RIGHT_PANEL_COLOR_X_Y, 0, 0));
        assertEquals(getPixelColor(url, 10, 18), expectedColor, format(MSG_RIGHT_PANEL_COLOR_X_Y, 10, 18));
        assertEquals(getPixelColor(url, 120, 4), expectedColor, format(MSG_RIGHT_PANEL_COLOR_X_Y, 120, 4));
        assertEquals(getPixelColor(url, 5, 9), expectedColor, format(MSG_RIGHT_PANEL_COLOR_X_Y, 5, 9));
        assertEquals(getPixelColor(url, 84, 84), expectedColor, format(MSG_RIGHT_PANEL_COLOR_X_Y, 84, 84));
        assertEquals(getPixelColor(url, 71, 55), expectedColor, format(MSG_RIGHT_PANEL_COLOR_X_Y, 71, 55));
    }

    /**
     * Returns the color of the pixel with specified coordinates.
     * 
     * @param url
     *            relative URL of the image
     * @param x
     *            the horizontal coordinate of the pixel
     * @param y
     *            the vertical coordinate of the pixel
     * @return color of the pixel
     */
    private Color getPixelColor(String url, int x, int y) {
        // the index of first '/' not counting http://
        int index = selenium.getLocation().indexOf('/', 7);
        url = selenium.getLocation().substring(0, index) + url;

        BufferedImage image = null;
        try {
            image = ImageIO.read(new URL(url));
        } catch (MalformedURLException e) {
            fail(MSG_PROVIDED_URL_NOT_VALID);
        } catch (IOException e) {
            e.printStackTrace();
            fail(MSG_COULD_NOT_READ_IMAGE);
        }
        int c = image.getRGB(18, 97);
        int red = (c & 0x00ff0000) >> 16;
        int green = (c & 0x0000ff00) >> 8;
        int blue = c & 0x000000ff;

        return new Color(red, green, blue);
    }

    @SuppressWarnings("unused")
    @BeforeClass
    private void createImages() {
        createImage(Color.RED, 128, 128);
        createImage(Color.BLUE, 128, 128);
        createImage(Color.CYAN, 128, 128);
        createImage(Color.YELLOW, 128, 128);
        createImage(Color.ORANGE, 128, 128);
        createImage(Color.GREEN, 2000, 3500);
    }

    @SuppressWarnings("unused")
    @AfterClass
    private void cleanUpImages() {
        new File(FILE_RED).delete();
        new File(FILE_BLUE).delete();
        new File(FILE_CYAN).delete();
        new File(FILE_YELLOW).delete();
        new File(FILE_ORANGE).delete();
    }

    /**
     * Loads the page containing the component.
     */
    @SuppressWarnings("unused")
    @BeforeMethod
    private void loadPage() {
        openComponent("File Upload");
        openTab("Usage");

        scrollIntoView(LOC_EXAMPLE_HEADER, true);
    }
}

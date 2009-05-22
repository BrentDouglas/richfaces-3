/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.richfaces.convert.seamtext.tags;

class LineTag extends HtmlTag {
    private static final long serialVersionUID = 6972613670825989225L;

    private String startTag;
    protected boolean isHtml = false;
    
    protected LineTag(String name) {
        super(name);
    }

    public LineTag(String name, String startTag) {
        super(name);
        setStartTag(startTag);
    }

    @Override
    public String printStart() {
        return startTag;
    }

    @Override
    public String printPlain() {
        isHtml = true;
        return super.printPlain();
    }
    
    private boolean isFirstChars = true;
    
    @Override
    public void appendBody(String str) {
        if (isFirstChars) {
            char text[] = str.toCharArray();
            int i = 0;
            while (i < text.length && (text[i] == '\r' || text[i] == '\n')) {
                i++;
            }
              
            if (i < text.length) {
                super.appendBody(str.substring(i));
                isFirstChars = false;
            } else {
                return;
            }
        } else {
            super.appendBody(str);
        }
    }

    @Override
    protected void appendChildTag(StringBuilder res, HtmlTag child) {
        if (child instanceof LineTag) {
            res.append(((LineTag)child).printPlain());
        } else {
            res.append(child);
        }
    }

    @Override
    protected String printBody() {
        while (!isBodyEmpty() && isBreakLineChild(body.getLast())) {
            body.removeLast();
        }
        
        return super.printBody();
    }
    
    @Override
    public String printEnd() {
        return "\n";
    }

    public String getStartTag() {
        return startTag;
    }

    public void setStartTag(String startTag) {
        this.startTag = startTag;
    }
}


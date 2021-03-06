/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2010 Technische Universitaet Berlin
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.mumie.cdk.util.editor.types;

import net.mumie.cocoon.checkin.GDIMEntry;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.checkin.SimpleEditableGDIMEntry;

/**
 * This editable GDIM entry handles the generic document references.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public class BasicEditableGDIMEntry extends SimpleEditableGDIMEntry {

  public BasicEditableGDIMEntry() throws MasterException {
    super();
  }

  public BasicEditableGDIMEntry(GDIMEntry entry) throws MasterException {
    super(entry);
    setGenericDocPath(entry.getGenericDocPath());
    setGenericDocId(entry.getGenericDocId());
    setLang(entry.getLang());
    setLangCode(entry.getLangCode());
    setThemePath(entry.getThemePath());
  }
}

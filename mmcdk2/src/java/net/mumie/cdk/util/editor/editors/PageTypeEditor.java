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

package net.mumie.cdk.util.editor.editors;

import net.mumie.cdk.util.editor.types.BasicEditableMaster;
import net.mumie.cocoon.checkin.Master;
import net.mumie.cocoon.checkin.MasterException;
import net.mumie.cocoon.notions.MediaType;

/**
 * This class is a master type editor and handles page specific meta
 * information.
 * 
 * @author Markus Gronau <a
 *         href="mailto:gronau@math.tu-berlin.de">gronau@math.tu-berlin.de</a>
 * @version <code>$Id$</code>
 */
public class PageTypeEditor extends AbstractTypeEditor {

  public PageTypeEditor(Master master) throws MasterException {
    super(BasicEditableMaster.createEditableMaster(master));
  }

  protected String getTypeName() {
    return "page";
  }

  protected void initTypeAttributes() throws MasterException {
    addTypeAttribute(CONTENT_TYPE_INFO);
  }

  protected String getData(String name) throws MasterException {
    if (name.equals(CONTENT_TYPE_INFO))
      return MediaType.nameFor(getMaster().getContentType());
    else
      return super.getData(name);
  }

  protected void setData(String name, String data) throws MasterException {
    if (name.equals(CONTENT_TYPE_INFO))
      getMaster().setContentType(MediaType.codeFor(data));
    else
      super.setData(name, data);
  }

  protected TextInputComponent createInputComponent(String name)
      throws MasterException {
    if (CONTENT_TYPE_INFO.equals(name))
      return createComboBox(new String[] { "text/xml", "text/html" });
    else
      return super.createInputComponent(name);
  }
}
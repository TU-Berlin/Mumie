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

package net.mumie.cocoon.event;

import net.mumie.cocoon.checkin.ContentObjectToCheckin;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class CheckinEvent
  implements Event
{
  /**
   * Comprises the nature, type, and id of a checked-in (pseudo-)document.
   */

  public static final class Item
  {
    /**
     * The nature (document or pseudo-document).
     */

    protected final int nature;

    /**
     * The type.
     */

    protected final int type;

    /**
     * The id
     */

    protected final int id;

    /**
     * Creates a new instance from the specified content object.
     */

    public Item (ContentObjectToCheckin contentObject)
    {
      this.nature = contentObject.getNature();
      this.type = contentObject.getType();
      this.id = contentObject.getId();
    }

    /**
     * Returns the nature (document or pseudo-document).
     */

    public final int getNature ()
    {
      return this.nature;
    }

    /**
     * Returns the type.
     */

    public final int getType ()
    {
      return this.type;
    }

    /**
     * Returns the id.
     */

    public final int getId ()
    {
      return this.id;
    }
  }

  /**
   * The natures, types, and ids of the checked-in (pseudo-)documents.
   */

  protected final Item[] items;

  /**
   * Creates a new instance from the specified checked-in (pseudo-)documents.
   */

  public CheckinEvent (ContentObjectToCheckin[] contentObjects)
  {
    this.items = new Item[contentObjects.length];
    for (int i = 0; i < items.length; i++)
      items[i] = new Item(contentObjects[i]);
  }

  /**
   * Returns the natures, types, and ids of the checked-in (pseudo-)documents as an
   * immutable list of {@link Item Item} objects.
   */

  public List<Item> getItems ()
  {
    return Collections.unmodifiableList(Arrays.asList(this.items));
  }
}

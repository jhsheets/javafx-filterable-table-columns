/*
 * Copyright (c) 2013, jhsheets@gmail.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.google.jhsheets.filtered.tablecolumn.editor;

import java.util.Collection;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.WindowEvent;
import org.google.jhsheets.filtered.operators.IFilterOperator;

/**
 *
 * @author JHS
 */
public abstract class AbstractFilterEditor<R extends IFilterOperator<?>>
implements IFilterEditor<R>
{
    private FilterContextMenu menu;
    private SimpleBooleanProperty filtered;
    
    public AbstractFilterEditor(String title)
    {
        menu = new FilterContextMenu(title);
        filtered = new SimpleBooleanProperty(false);
        
        menu.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                AbstractFilterEditor.this.cancel();
            }
        });
    }
    
    @Override
    public FilterContextMenu getFilterMenu()
    {
        return menu;
    }
    
    /**
     * Adds an item to the filter editor
     * @param item
     */
    public void addFilterMenuItem(MenuItem item)
    {
        menu.addFilterMenuItem(item);
    }
    
    /**
     * Adds all items to the filter editor, in the supplied order
     * @param items 
     */
    public void addFilterMenuItems(Collection<? extends MenuItem> items)
    {
        menu.addFilterMenuItems(items);
    }
    
    public void clearFilterMenuItems()
    {
        menu.clearFilterMenuItems();
    }
    
    @Override
    public BooleanProperty filteredProperty()
    {
        return filtered;
    }
    
    @Override
    public boolean isFiltered()
    {
        return filtered.get();
    }
    
    /**
     * @param isFiltered If there are any non-default filters applied
     */
    protected void setFiltered(boolean isFiltered)
    {
        filtered.set(isFiltered);
    }
}

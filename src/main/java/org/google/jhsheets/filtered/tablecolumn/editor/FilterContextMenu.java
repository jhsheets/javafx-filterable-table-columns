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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Window;
import org.google.jhsheets.filtered.control.TitledSeparatorMenuItem;

/**
 *
 * @author JHS
 */
public class FilterContextMenu 
extends ContextMenu
{
    //http://www.guigarage.com/2012/11/custom-ui-controls-with-javafx-part-1/
    
    private static FilterContextMenu currentlyVisibleMenu;
    
    private final MenuItem titleItem;
    private final MenuItem saveItem;
    private final MenuItem clearItem;
    private final List<MenuItem> menuItems;
    
    
    public FilterContextMenu(String title)
    {
        super();
        
        setAutoHide(false);
        
        menuItems = new ArrayList<>(30);
        titleItem = new TitledSeparatorMenuItem(title + " Filter");
        saveItem = new MenuItem("Save");
        clearItem = new MenuItem("Clear");
    }
    
    public FilterContextMenu(String title, MenuItem... items)
    {
        this(title);
        for (MenuItem i : items) {
            menuItems.add(i);
        }
    }
    
    public void addFilterMenuItem(MenuItem item)
    {
        menuItems.add(item);
        resetMenu();
    }
    
    public void addFilterMenuItems(Collection<? extends MenuItem> items)
    {
        menuItems.addAll(items);
        resetMenu();
    }
    
    private void resetMenu()
    {
        getItems().clear();
        getItems().addAll(titleItem);
        getItems().addAll(menuItems);
        getItems().addAll(new SeparatorMenuItem(), saveItem, clearItem);
    }
    
    public void setSaveEvent(EventHandler<ActionEvent> event)
    {
        saveItem.setOnAction(event);
    }
    
    public void setClearEvent(EventHandler<ActionEvent> event)
    {
        clearItem.setOnAction(event);
    }
    
    /**
     * There can be only one... visible FilterContextMenu
     */
    private void highlander()
    {
        if (currentlyVisibleMenu != null && currentlyVisibleMenu != this)
        {
            currentlyVisibleMenu.hide();
        }
        currentlyVisibleMenu = this;
    }
    
    @Override
    public void hide() 
    {
        if (currentlyVisibleMenu == this)
            currentlyVisibleMenu = null;
        super.hide();
    }

    @Override
    protected void show() 
    {
        highlander();
        super.show();
    }

    @Override
    public void show(Window window) 
    {
        highlander();
        super.show(window);
    }

    @Override
    public void show(Window window, double d, double d1) 
    {
        highlander();
        super.show(window, d, d1);
    }
    
    @Override
    public void show(Node node, double d, double d1) 
    {
        highlander();
        super.show(node, d, d1);
    }
    
    @Override
    public void show(Node node, Side side, double dx, double dy)
    {
        highlander();
        super.show(node, side, dx, dy);
    }

}

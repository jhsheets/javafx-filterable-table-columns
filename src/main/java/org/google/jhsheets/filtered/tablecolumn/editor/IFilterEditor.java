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

import javafx.beans.property.BooleanProperty;
import org.google.jhsheets.filtered.operators.IFilterOperator;

/**
 * A graphical interface used to change filters
 * @author JHS
 */
public interface IFilterEditor<R extends IFilterOperator>
{
    /**
     * @return The user entered filters
     * @throws Exception 
     */
    abstract public R[] getFilters() throws Exception;
    
    /**
     * Cancel filter editing
     */
    abstract public void cancel();
    
    /**
     * Set the filter to the saved 
     * @return If the filter was successfully saved
     * @throws Exception 
     */
    abstract public boolean save() throws Exception;
    
    /**
     * Clears the filter back to its default state.
     * If successful, the menu should hide.
     * @return If the filter was successfully cleared
     * @throws Exception 
     */
    abstract public boolean clear() throws Exception;
    
    /**
     * @return The menu used to change the filter
     */
    abstract public FilterContextMenu getFilterMenu();
    
    /**
     * @return Property identifying if there is a filter set
     */
    abstract public BooleanProperty filteredProperty();
    
    /**
     * @return If there is currently a filter set
     */
    abstract public boolean isFiltered();
}

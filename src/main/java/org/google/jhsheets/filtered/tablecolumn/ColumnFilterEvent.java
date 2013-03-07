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
package org.google.jhsheets.filtered.tablecolumn;

import java.util.List;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.TableView;
import org.google.jhsheets.filtered.operators.IFilterOperator;
import org.google.jhsheets.filtered.tablecolumn.editor.IFilterEditor;

/**
 * An event that is fired when an {@link AbstractFilterableTableColumn} has its filter changed
 * @author JHS
 */
public class ColumnFilterEvent<S,T,R extends IFilterOperator,M extends IFilterEditor<R>>
extends Event
{
    /**
     * An event indicating that the filter has changed
     */
    public static final EventType<ColumnFilterEvent> FILTER_CHANGED_EVENT = new EventType<>(Event.ANY, "FILTER_CHANGED");
    
    private List<R> filter;
    
    private AbstractFilterableTableColumn<S,T,R,M> sourceColumn;
    
    
    public ColumnFilterEvent(TableView table, AbstractFilterableTableColumn<S,T,R,M> sourceColumn, List<R> filter) 
    {
        super(table, Event.NULL_SOURCE_TARGET, ColumnFilterEvent.FILTER_CHANGED_EVENT);

        if (table == null) {
            throw new NullPointerException("TableView can not be null");
        }

        this.filter = filter;
        this.sourceColumn = sourceColumn;
    }
    
    /**
     * @return Any and all filters applied to the column
     */
    public List<R> getFilters()
    {
        return filter;
    }
    
    /**
     * @return The {@link AbstractFilterableTableColumn} which had its filter changed
     */
    public AbstractFilterableTableColumn<S,T,R,M> sourceColumn()
    {
        return sourceColumn;
    }
}

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
package org.google.jhsheets.filtered;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.google.jhsheets.filtered.tablecolumn.AbstractFilterableTableColumn;
import org.google.jhsheets.filtered.tablecolumn.ColumnFilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link TableView} that identifies any {@link AbstractFilterableTableColumn}'s added to it, 
 * and fires a single event when any of them have their filters changed.
 * <br/><br/>
 * To listen for changes the table's filters, register a {@link ColumnFilterEvent#FILTER_CHANGED_EVENT}
 * with {@link #addEventFilter(javafx.event.EventType, javafx.event.EventHandler) }
 * or {@link #addEventHandler(javafx.event.EventType, javafx.event.EventHandler) }
 * 
 * @author JHS
 */
public class FilteredTableView<S>
extends TableView<S>
{
    private static final Logger logger = LoggerFactory.getLogger(FilteredTableView.class);
    
    /** List of filterable columns with a filter applied */
    private ObservableList<AbstractFilterableTableColumn<?,?,?,?>> filteredColumns;
    
    
    public FilteredTableView(ObservableList<S> ol)
    {
        this();
        super.setItems(ol);
    }
    
    public FilteredTableView()
    {
        super();
        
        filteredColumns = FXCollections.observableArrayList();
        
        // Execute the filteringChanged runnable
        // And, if a column has a filter on it, make sure that column is in our filteredColumns list
        final EventHandler<ColumnFilterEvent<?,?,?,?>> columnFilteredEventHandler = new EventHandler<ColumnFilterEvent<?,?,?,?>>() 
        {
            @Override
            public void handle(ColumnFilterEvent<?,?,?,?> event) 
            {
                // Keep track of which TableColumn's are currently filtered
                final AbstractFilterableTableColumn<?,?,?,?> col = event.sourceColumn();
                
                if (col.isFiltered() == true && filteredColumns.contains(col) == false)
                {
                    filteredColumns.add(col);
                    logger.debug(String.format("Filter added on column: %s", col.getText()));
                }
                else if (col.isFiltered() == false && filteredColumns.contains(col) == true)
                {
                    filteredColumns.remove(col);
                    logger.debug(String.format("Filter removed on column: %s", col.getText()));
                }
                
                // Forward event
                fireEvent(event);
            }
        };
        
        // Make sure any filterable columns on this table have the columnFilterEventHandler
        getColumns().addListener(new ListChangeListener<TableColumn<?,?>>() 
        {
            @Override
            public void onChanged(Change<? extends TableColumn<?,?>> change) 
            {
                change.next();// must advance to next change, for whatever reason...
                // Drag-n-dropping a column fires a remove and an add.
                if (change.wasRemoved())
                {
                    for (final TableColumn<?,?> col : change.getAddedSubList())
                    {
                        if (col instanceof AbstractFilterableTableColumn)
                        {
                            logger.debug(String.format("No longer listening for filter changes on column: %s", col.getText()));
                            final AbstractFilterableTableColumn<?,?,?,?> fcol = (AbstractFilterableTableColumn<?,?,?,?>)col;
                            fcol.removeEventHandler(ColumnFilterEvent.FILTER_CHANGED_EVENT, columnFilteredEventHandler);
                        }
                    }
                }
                if (change.wasAdded())
                {
                    for (final TableColumn<?,?> col : change.getAddedSubList())
                    {
                        if (col instanceof AbstractFilterableTableColumn)
                        {
                            logger.debug(String.format("Now listening for filter changes on column: %s", col.getText()));
                            final AbstractFilterableTableColumn<?,?,?,?> fcol = (AbstractFilterableTableColumn<?,?,?,?>)col;
                            fcol.addEventHandler(ColumnFilterEvent.FILTER_CHANGED_EVENT, columnFilteredEventHandler);
                        }
                    }
                }
            }
        });
    }
    
    /**
     * @return Observable list containing any {@link AbstractFilterableTableColumn}'s that have a filter applied
     */
    public ObservableList<AbstractFilterableTableColumn<?,?,?,?>> getFilteredColumns()
    {
        return filteredColumns;
    }
    
}

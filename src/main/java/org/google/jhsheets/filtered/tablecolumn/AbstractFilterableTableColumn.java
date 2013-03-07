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

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import org.google.jhsheets.filtered.operators.IFilterOperator;
import org.google.jhsheets.filtered.tablecolumn.editor.IFilterEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Note: we hijack the ContextMenu to display the filter selection dialog. 
 * Do not set the ContextMenu anywhere else.
 * 
 * @author JHS
 */
public class AbstractFilterableTableColumn<S,T,R extends IFilterOperator,M extends IFilterEditor<R>> 
extends TableColumn<S,T>
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractFilterableTableColumn.class);
    
    private final M filterEditor;
    private final ObservableList<R> filterResults;
    
    
    public AbstractFilterableTableColumn(String name, final M filterEditor)
    {
        super(name);
        
        this.filterEditor = filterEditor;
        this.filterResults = FXCollections.observableArrayList();
        
        // Display a button on the column to show the menu
        final Button filterTrigger = new Button();
        filterTrigger.getStyleClass().add("filter-button-node"); 
        filterTrigger.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (getContextMenu().isShowing()) 
                {
                    getContextMenu().hide();
                }
                else 
                {
                    getContextMenu().show(filterTrigger, Side.BOTTOM, 0, 0);
                }
            }
        });
        
        // Change the filter button icon based on filtered status
        filteredProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean oldVal, Boolean newVal) {
                if (newVal == Boolean.TRUE) {
                    filterTrigger.getStyleClass().add("active");
                } else {
                    filterTrigger.getStyleClass().remove("active");
                }
            }
        });  
        
        // I'd love to do this, but you have to set the content to GRAPHIC_ONLY, but there's
        // no way to do that as the header skin is part of the table, not the column
        //final Label lbl = new Label();
        //lbl.textProperty().bind(this.textProperty());
        //final BorderPane pane = new BorderPane();
        //pane.setLeft(filterTrigger);
        //pane.setCenter(lbl);
        //setGraphic(pane);
        
        setGraphic(filterTrigger);
        setContextMenu(filterEditor.getFilterMenu());
        
        filterEditor.getFilterMenu().setClearEvent(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent t) 
            {
                try 
                {
                    if (filterEditor.clear())
                    {
                        filterResults.setAll(filterEditor.getFilters());
                        
                        final ColumnFilterEvent e = new ColumnFilterEvent(
                                    AbstractFilterableTableColumn.this.getTableView()
                                    , AbstractFilterableTableColumn.this
                                    , getFilters());
                        
                        Event.fireEvent(AbstractFilterableTableColumn.this, e);
                    }
                    getContextMenu().hide();
                } 
                catch (Exception ex) 
                {
                    logger.error(String.format("Error clearing filter on column: %s"
                            , AbstractFilterableTableColumn.this.getText()), ex);
                }
            }
        });
        
        filterEditor.getFilterMenu().setSaveEvent(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent t) 
            {
                try
                {
                    if (filterEditor.save())
                    {
                        filterResults.setAll(filterEditor.getFilters());
                        
                        final ColumnFilterEvent e = new ColumnFilterEvent(
                                AbstractFilterableTableColumn.this.getTableView()
                                , AbstractFilterableTableColumn.this
                                , getFilters());
                        
                        Event.fireEvent(AbstractFilterableTableColumn.this, e);
                    }
                    getContextMenu().hide();
                }
                catch (Exception ex)
                {
                    logger.error(String.format("Error saving filter on column: %s"
                            , AbstractFilterableTableColumn.this.getText()), ex);
                }
            }
        });
    }
    
    protected M getFilterEditor() 
    {
        return filterEditor;
    }
    
    /**
     * Note: this method can return {@link IFilterOperator.Type.NONE}'s.  
     * Use {@link #isFiltered()} to determine if there is actually a filter
     * applied to this column
     * 
     * @return All applied filters.
     */
    public ObservableList<R> getFilters() 
    { 
        return filterResults;
    }
    
    /**
     * @return Property indicating if this column has filters applied
     */
    public final BooleanProperty filteredProperty()
    {
        return filterEditor.filteredProperty();
    }
    
    /**
     * @return If this column has filters applied
     */
    public boolean isFiltered()
    {
        return filterEditor.isFiltered();
    }
    
    //public void setFilters(R filters) {
    //    // TODO
    //}
    //public boolean isFilterable() {
    //    // TODO
    //}
    //public void setFilterable(boolean filterable) {
    //    // TODO
    //}
    //public SimpleBooleanProperty filterableProperty() {
    //    // TODO
    //}
    
}

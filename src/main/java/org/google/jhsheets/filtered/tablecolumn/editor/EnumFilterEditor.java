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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.google.jhsheets.filtered.operators.EnumOperator;

/**
 *
 * @author JHS
 */
public class EnumFilterEditor<T> 
extends AbstractFilterEditor<EnumOperator<T>> 
{
    private static final String TOGGLE_ALL = "(Toggle All)";
    
    private final CheckBox toggleAllChbx = new CheckBox(TOGGLE_ALL);
    private boolean selectedByDefault;
    private boolean showToggle;
    
    private boolean[] previousSelections;    
    private final ObservableList<CheckBox> enumCombos;    
    
    public EnumFilterEditor(String title, T[] values)
    {
        this(title, values, false);
    }
    
    public EnumFilterEditor(String title, T[] values, boolean selectedByDefault) 
    {
        this(title, values, false, false);
    }
    
    public EnumFilterEditor(String title, T[] values, boolean selectedByDefault, boolean showToggle) 
    {
        super(title);
        this.selectedByDefault = selectedByDefault;
        this.enumCombos = FXCollections.observableArrayList();
        this.showToggle = showToggle;
        populateMenuItems(values);
    }
    
    final public void populateMenuItems(T[] values) 
    {
        final int len = values == null ? 0 : values.length;
        this.previousSelections = new boolean[len + 1]; // add 1 for Toggle All chbx
        
        this.enumCombos.clear();
        
        if (values != null) 
        {
            // Create a checkbox to toggle selection of the rest
            toggleAllChbx.setSelected(selectedByDefault);
            toggleAllChbx.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent t) {
                    toggleAll(toggleAllChbx.isSelected());
                    t.consume();
                }
            });
            
            showToggleAll(showToggle);
            
            // Create a property that will update toggleAllChbx when items get (de)selected
            final SimpleIntegerProperty itemsChecked = new SimpleIntegerProperty();            
            itemsChecked.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    final int itemsSel = newValue.intValue();
                    if (itemsSel == 0) {
                        toggleAllChbx.setSelected(false);
                        toggleAllChbx.setIndeterminate(false);
                    }
                    else if (itemsSel == enumCombos.size()-1) {
                        toggleAllChbx.setSelected(true);
                        toggleAllChbx.setIndeterminate(false);
                    }
                    else {
                        toggleAllChbx.setIndeterminate(true);
                    }
                }
            });

            // Populate checkboxes with the values; set default toggle state
            for (T value : values) 
            {
                final CheckBox ecb = new CheckBox(value.toString());
                ecb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        final int currVal = itemsChecked.getValue();
                        itemsChecked.set( ecb.isSelected() ? currVal+1 : currVal-1);
                    }
                });
                ecb.setUserData(value);
                ecb.setSelected(selectedByDefault);
                enumCombos.add(ecb);
            }
        }
        
        final ListView<CheckBox> list = new ListView<>(enumCombos);
        list.setEditable(false);
        list.setMaxHeight(215);
        list.setMaxWidth(400);
        list.setPrefWidth(200);
        list.setPrefHeight(25 * Math.max(enumCombos.size(), 2)); 
        list.getItems().addListener(new ListChangeListener<Control>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Control> c) {
                final int items = c.getList().size();
                list.setPrefHeight( 25 * Math.max(items, 2));
            }       
        });
        // We don't allow edit mode, so we can let escape key events bubble to the popup
        list.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    list.getScene().getWindow().hide();
                }
            }
        });
        // Checkbox doesn't fill the entire cell. Change selection on clicks outside the checkbox
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                final CheckBox cb = list.getSelectionModel().getSelectedItem();
                if (cb != null) {
                    cb.setSelected( !cb.isSelected() );
                    cb.requestFocus();
                }
            }
        });
        setFilterMenuContent(list);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public EnumOperator<T>[] getFilters() throws Exception 
    {
        final ArrayList<EnumOperator<?>> retList = new ArrayList<>();
        
        for (CheckBox emt : enumCombos)
        {
            if (emt != toggleAllChbx && emt.isSelected())
            {
                retList.add(new EnumOperator<>(EnumOperator.Type.EQUALS, emt.getUserData()));
            }
        }
        
        if (retList.isEmpty())
        {
            retList.add(new EnumOperator<>(EnumOperator.Type.NONE, null));
        }
        
        return retList.toArray(new EnumOperator[0]);
    }
    
    @Override
    public void cancel()
    {
        int i=0;
        for (CheckBox emu : enumCombos)
        {
            emu.setSelected(previousSelections[i++]);
        }
    }

    @Override
    public boolean save() throws Exception 
    {
        boolean changed = false;
        
        // Determine if there are any changes
        boolean noSelections = true;
        selectionCheck: for (int i=0; i < enumCombos.size(); i++)
        {
            final CheckBox emu = enumCombos.get(i);
            if (emu.isSelected() != selectedByDefault)
            {
                noSelections = false;
                break selectionCheck;
            }
        }
        
        if (noSelections)
        {
            changed = clear();
        }
        else
        {
            setFiltered(true);
            
            // Determine if anything's changed
            changedCheck: for (int i=0; i < enumCombos.size(); i++)
            {
                final CheckBox emu = enumCombos.get(i);
                if (previousSelections[i] != emu.isSelected())
                {
                    changed = true;
                    break changedCheck;
                }
            }
            
            // Save to previousSelection
            for (int i=0; i < enumCombos.size(); i++)
            {
                final CheckBox emu = enumCombos.get(i);
                previousSelections[i] = emu.isSelected();
            }
        }
        
        return changed;
    }

    @Override
    public boolean clear() throws Exception 
    {
        boolean changed = false;
        
        for (int i=0; i < previousSelections.length; i++)
        {
            previousSelections[i] = selectedByDefault;
        }
        for (CheckBox emu : enumCombos)
        {
            emu.setSelected(selectedByDefault);
        }
                
        if (isFiltered())
        {
            setFiltered(false);
            changed = true;
        }
        
        return changed;
    }
    
    public void selectedByDefault(final boolean selected)
    {
        selectedByDefault = selected;
    }
    
    public void toggleAll(final boolean selected)
    {
        for(int i=0; i < enumCombos.size(); i++) 
        {
            final CheckBox ecb = enumCombos.get(i);
            previousSelections[i] = ecb.isSelected();
            ecb.setSelected(selected);
        }
    }
    
    public void showToggleAll(final boolean showToggle)
    {
        if (showToggle)
        {
            if (enumCombos.isEmpty() || enumCombos.get(0) != toggleAllChbx)
            {
                enumCombos.add(0, toggleAllChbx);
            }
        }
        else
        {
            if (!enumCombos.isEmpty() && enumCombos.get(0) == toggleAllChbx)
            {
                enumCombos.remove(0);
            }
        }
    }
}
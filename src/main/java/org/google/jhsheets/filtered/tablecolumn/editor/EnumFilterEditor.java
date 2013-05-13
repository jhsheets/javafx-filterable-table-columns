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
    private boolean[] previousSelections;
    
    private ObservableList<CheckBox> enumCombos;
    
    
    public EnumFilterEditor(String title, T[] values)
    {
        super(title);
        this.enumCombos = FXCollections.observableArrayList();
        populateMenuItems(values);
    }
    
    final public void populateMenuItems(T[] values)
    {
        final int len = values == null ? 0 : values.length;
        this.previousSelections = new boolean[len];
        
        this.enumCombos.clear();
		if (values != null) 
		{
			for (T value : values) 
			{
				final CheckBox ecb = new CheckBox(value.toString());
				ecb.setUserData(value);
				enumCombos.add(ecb);
			}
		}
        
        final ListView<CheckBox> list = new ListView<CheckBox>(enumCombos);
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
				CheckBox cb = list.getSelectionModel().getSelectedItem();
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
            if (emt.isSelected())
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
            if (emu.isSelected())
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
            previousSelections[i] = false;
        }
        for (CheckBox emu : enumCombos)
        {
            emu.setSelected(false);
        }
                
        if (isFiltered())
        {
            setFiltered(false);
            changed = true;
        }
        
        return changed;
    }
    
}
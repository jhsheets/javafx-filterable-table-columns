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
import javafx.scene.control.ComboBox;
import org.google.jhsheets.filtered.control.ComboBoxMenuItem;
import org.google.jhsheets.filtered.operators.BooleanOperator;

/**
 *
 * @author JHS
 */
public class BooleanFilterEditor
extends AbstractFilterEditor<BooleanOperator>
{

    private BooleanOperator.Type previousType;
    
    private final ComboBox<BooleanOperator.Type> typeBox;
    
    private final BooleanOperator.Type DEFAULT_TYPE;
    
    public BooleanFilterEditor(String title)
    {
        this(title, BooleanOperator.validTypes());
    }
    
    public BooleanFilterEditor(String title, BooleanOperator.Type[] types)
    {
        super(title);
        
        DEFAULT_TYPE = BooleanOperator.Type.NONE;
        
        typeBox = new ComboBox<>();
        
        final ComboBoxMenuItem typeItem = new ComboBoxMenuItem(typeBox);
        
        addFilterMenuItem(typeItem);
        
        previousType = DEFAULT_TYPE;
        
        typeBox.getSelectionModel().select(DEFAULT_TYPE);
        typeBox.getItems().addAll(types);
    }
    
    @Override
    public BooleanOperator[] getFilters() throws Exception 
    {
        final ArrayList<BooleanOperator> retList = new ArrayList<>();
        final BooleanOperator.Type selectedType = typeBox.getSelectionModel().getSelectedItem();
        if (selectedType == BooleanOperator.Type.NONE)
        {
            retList.add(new BooleanOperator(selectedType, null) );
        }
        else
        {
            retList.add(new BooleanOperator(selectedType, selectedType == BooleanOperator.Type.TRUE));
        }
        return retList.toArray(new BooleanOperator[0]);
    }
    
    @Override
    public void cancel()
    {
        typeBox.getSelectionModel().select(previousType);
    }

    @Override
    public boolean save() throws Exception 
    {
        boolean changed = false;
        
        final BooleanOperator.Type selectedType = typeBox.getSelectionModel().getSelectedItem();
        if (selectedType == DEFAULT_TYPE)
        {
            changed = clear();
        }
        else
        {
            changed = previousType != typeBox.getSelectionModel().getSelectedItem();
            previousType = typeBox.getSelectionModel().getSelectedItem();
            setFiltered(true);
        }
        
        return changed;
    }

    @Override
    public boolean clear() throws Exception 
    {
        boolean changed = false;
        
        previousType = DEFAULT_TYPE;
        typeBox.getSelectionModel().select(DEFAULT_TYPE);
        
        if (isFiltered())
        {
            setFiltered(false);
            changed = true;
        }
        
        return changed;
    }
    
}
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

import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import org.google.jhsheets.filtered.operators.BooleanOperator;

/**
 *
 * @author JHS
 */
public class BooleanFilterEditor
extends AbstractFilterEditor<BooleanOperator>
{

    private BooleanOperator.Type previousType;
    
    private final ToggleGroup typeGroup = new ToggleGroup();
    
    private final BooleanOperator.Type DEFAULT_TYPE;
    
    public BooleanFilterEditor(String title)
    {
        this(title, BooleanOperator.validTypes());
    }
    
    public BooleanFilterEditor(String title, BooleanOperator.Type[] types)
    {
        super(title);
        
        DEFAULT_TYPE = BooleanOperator.Type.NONE;
        
        final RadioButton rbNone = new RadioButton(BooleanOperator.Type.NONE.toString());
        rbNone.setUserData(BooleanOperator.Type.NONE);
        rbNone.setToggleGroup(typeGroup);
        
        final RadioButton rbTrue = new RadioButton(BooleanOperator.Type.TRUE.toString());
        rbTrue.setUserData(BooleanOperator.Type.TRUE);
        rbTrue.setToggleGroup(typeGroup);
        
        final RadioButton rbFalse = new RadioButton(BooleanOperator.Type.FALSE.toString());
        rbFalse.setUserData(BooleanOperator.Type.FALSE);
        rbFalse.setToggleGroup(typeGroup);
        
        setSelectedToggle(DEFAULT_TYPE);
        
        final VBox box = new VBox();
        box.setSpacing(4);
        box.getChildren().addAll(rbNone, rbTrue, rbFalse);
        
        setFilterMenuContent(box);
    }
    
    @Override
    public BooleanOperator[] getFilters() throws Exception 
    {
        final ArrayList<BooleanOperator> retList = new ArrayList<>();
        final BooleanOperator.Type selectedType = (BooleanOperator.Type)typeGroup.getSelectedToggle().getUserData();
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
    	setSelectedToggle(previousType);
    }

    @Override
    public boolean save() throws Exception 
    {
        boolean changed = false;
        
        final BooleanOperator.Type selectedType = (BooleanOperator.Type)typeGroup.getSelectedToggle().getUserData();
        if (selectedType == DEFAULT_TYPE)
        {
            changed = clear();
        }
        else
        {
            changed = previousType != selectedType;
            previousType = selectedType;
            setFiltered(true);
        }
        
        return changed;
    }

    @Override
    public boolean clear() throws Exception 
    {
        boolean changed = false;
        
        previousType = DEFAULT_TYPE;
        setSelectedToggle(DEFAULT_TYPE);
        
        if (isFiltered())
        {
            setFiltered(false);
            changed = true;
        }
        
        return changed;
    }
    
    private void setSelectedToggle(BooleanOperator.Type type)
    {
    	for (Toggle t : typeGroup.getToggles())
    	{
    		final BooleanOperator.Type tmp = (BooleanOperator.Type)t.getUserData();
    		if (type == tmp)
    		{
    			t.setSelected(true);
    			break;
    		}
    	}
    }
    
}
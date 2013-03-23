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
import java.util.List;
import javafx.scene.control.CheckBox;
import org.google.jhsheets.filtered.control.CheckBoxMenuItem;
import org.google.jhsheets.filtered.operators.EnumOperator;

/**
 *
 * @author JHS
 */
public class EnumFilterEditor<T>
extends AbstractFilterEditor<EnumOperator<T>>
{
    private boolean[] previousSelections;
    
    private List<CheckBox> enumCombos;
    
    
    public EnumFilterEditor(String title, T[] values)
    {
        super(title);
        populateMenuItems(values);
    }
    
    final public void populateMenuItems(T[] values)
    {
        final int len = values == null ? 0 : values.length;
        this.previousSelections = new boolean[len];
        this.enumCombos = new ArrayList<>(len);
        
        clearFilterMenuItems();
        final List<CheckBoxMenuItem> menuItems = new ArrayList<>(len);
        if (values != null)
        {
            for (T value : values)
            {
                final CheckBox ecb = new CheckBox(value.toString());
                ecb.setUserData(value);
                enumCombos.add(ecb);
                menuItems.add(new CheckBoxMenuItem(ecb));
            }
        }
        addFilterMenuItems(menuItems);
    }
    
    @Override
    public EnumOperator<T>[] getFilters() throws Exception 
    {
        final ArrayList<EnumOperator> retList = new ArrayList<>();
        
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
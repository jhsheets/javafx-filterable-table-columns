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
import java.util.EnumSet;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import org.google.jhsheets.filtered.operators.StringOperator;

/**
 *
 * @author JHS
 */
public class TextFilterEditor 
extends AbstractFilterEditor<StringOperator>
{
    private String previousText;
    private StringOperator.Type previousType;
    
    private final TextField textField;
    private final ComboBox<StringOperator.Type> typeBox;
    
    private final String DEFAULT_TEXT;
    private final StringOperator.Type DEFAULT_TYPE;
    
    public TextFilterEditor(String title)
    {
        this(title, StringOperator.VALID_TYPES);
    }
    
    public TextFilterEditor(String title, EnumSet<StringOperator.Type> types)
    {
    	this(title, types.toArray(new StringOperator.Type[0]));
    }
    
    public TextFilterEditor(String title, StringOperator.Type[] types)
    {
        super(title);
        
        DEFAULT_TEXT = "";
        DEFAULT_TYPE = StringOperator.Type.NONE;
        
        textField = new TextField();
        typeBox = new ComboBox<>();
        
        final GridPane box = new GridPane();
        GridPane.setRowIndex(typeBox, 0);
        GridPane.setColumnIndex(typeBox, 0);
        GridPane.setRowIndex(textField, 1);
        GridPane.setColumnIndex(textField, 0);
        GridPane.setMargin(typeBox, new Insets(4, 0, 0, 0));
        GridPane.setMargin(textField, new Insets(4, 0, 0, 0));
        final ColumnConstraints boxConstraint = new ColumnConstraints();
        boxConstraint.setPercentWidth(100);
        box.getColumnConstraints().addAll(boxConstraint);
        box.getChildren().addAll(typeBox, textField);
        
        setFilterMenuContent(box);
        
        previousText = DEFAULT_TEXT;
        previousType = DEFAULT_TYPE;
        
        typeBox.getSelectionModel().select(DEFAULT_TYPE);
        typeBox.setMaxWidth(Double.MAX_VALUE);
        typeBox.getItems().addAll(types);
        typeBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<StringOperator.Type>() {
            @Override
            public void changed(ObservableValue<? extends StringOperator.Type> ov, StringOperator.Type old, StringOperator.Type newVal) {
                textField.setDisable(newVal == StringOperator.Type.NONE);
            }
        });
        
        textField.setDisable(true);
    }
    
    @Override
    public StringOperator[] getFilters() throws Exception 
    {
        final ArrayList<StringOperator> retList = new ArrayList<>();
        
        final String text = textField.getText();
        final StringOperator.Type selectedType = typeBox.getSelectionModel().getSelectedItem();
        if (selectedType == StringOperator.Type.NONE)
        {
            retList.add( new StringOperator(selectedType, "") );
        }
        else
        {
            if (text.isEmpty()) {
                throw new Exception("Filter text cannot be empty");
            } else {
                retList.add(new StringOperator(selectedType, text));
            }
        }
        return retList.toArray(new StringOperator[0]);
    }
    
    @Override
    public void cancel()
    {
        textField.setText(previousText);
        typeBox.getSelectionModel().select(previousType);
    }

    @Override
    public boolean save() throws Exception 
    {
        boolean changed = false;
        
        final StringOperator.Type selectedType = typeBox.getSelectionModel().getSelectedItem();
        if (selectedType == DEFAULT_TYPE)
        {
            changed = clear();
        }
        else
        {
            changed = previousType != typeBox.getSelectionModel().getSelectedItem()
                    || (typeBox.getSelectionModel().getSelectedItem() != StringOperator.Type.NONE 
                        && previousText.equals(textField.getText()) == false);
            
            previousText = textField.getText();
            previousType = typeBox.getSelectionModel().getSelectedItem();
            setFiltered(true);
            //changed = true;
        }
        
        return changed;
    }

    @Override
    public boolean clear() throws Exception 
    {
        boolean changed = false;
        
        previousText = DEFAULT_TEXT;
        previousType = DEFAULT_TYPE;
        
        textField.setText(DEFAULT_TEXT);
        typeBox.getSelectionModel().select(DEFAULT_TYPE);
        
        if (isFiltered())
        {
            setFiltered(false);
            changed = true;
        }
        
        return changed;
    }
    
}

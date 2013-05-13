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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.google.jhsheets.filtered.operators.NumberOperator;

/**
 *
 * @author JHS
 */
public class NumberFilterEditor<T extends Number>
extends AbstractFilterEditor<NumberOperator<T>>
{
    private final Class<T> klass;
    private final NumberFilterEditor<T>.Picker picker1;
    private final NumberFilterEditor<T>.Picker picker2;
    
    public NumberFilterEditor(String title, Class<T> klass)
    {
        this(title, klass, NumberOperator.validTypes());
    }
    
    public NumberFilterEditor(String title, Class<T> klass, NumberOperator.Type[] types)
    {
        super(title);
        this.klass = klass;
        
        final List<NumberOperator.Type> set1 = new ArrayList<>(20);
        final List<NumberOperator.Type> set2 = new ArrayList<>(20);
        parseTypes(types, set1, set2);
        
        picker1 = new Picker(set1.toArray(new NumberOperator.Type[0]));
        picker2 = new Picker(set2.toArray(new NumberOperator.Type[0]));
        
        final VBox box = new VBox();
        box.getChildren().addAll(picker1.box, picker2.box);
        setFilterMenuContent(box);
        
        // Disable the 2nd picker if the 1st picker isn't the start of a range
        picker2.setEnabled(false);
        picker1.typeBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NumberOperator.Type>() {
            @Override
            public void changed(ObservableValue<? extends NumberOperator.Type> ov, NumberOperator.Type old, NumberOperator.Type newVal) {
                picker2.setEnabled(newVal.equals(NumberOperator.Type.GREATERTHAN) || newVal.equals(NumberOperator.Type.GREATERTHANEQUALS));
            }
        });
    }
    
    private void parseTypes(NumberOperator.Type[] types, List<NumberOperator.Type> set1, List<NumberOperator.Type> set2)
    {
        set1.add(NumberOperator.Type.NONE);
        set2.add(NumberOperator.Type.NONE);
        for (NumberOperator.Type type : types)
        {
            // Only these range types should show up in 2nd picker
            if (type.equals(NumberOperator.Type.LESSTHAN) || type.equals(NumberOperator.Type.LESSTHANEQUALS))
            {
                if (!set2.contains(type)) set2.add(type);
            }
            // Everything else but above types should show up in 1st picker
            else
            {
                if (!set1.contains(type)) set1.add(type);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public NumberOperator<T>[] getFilters() throws Exception 
    {
        final NumberOperator<T> val1 = picker1.getFilter();
        final NumberOperator<T> val2 = picker2.getFilter();
        
        // TODO: if the Types are ranges, we should probably check that they're within the proper bounds.  Need a separate check for each <T> though
        
        return new NumberOperator[] { val1, val2 };
    }
    
    @Override
    public void cancel()
    {
        picker1.cancel();
        picker2.cancel();
    }

    @Override
    public boolean save() throws Exception 
    {
        boolean changed = false;
        
        final NumberOperator<T> do1 = picker1.getFilter();
        final NumberOperator<T> do2 = picker2.getFilter();
        
        if (do1.getType() == picker1.DEFAULT_TYPE && do2.getType() == picker2.DEFAULT_TYPE)
        {
            changed = clear();
        }
        else
        {
            final boolean changed1 = picker1.save();
            final boolean changed2 = picker2.save();
            setFiltered(true);
            changed = changed1 || changed2;
        }
        
        return changed;
    }

    @Override
    public boolean clear() throws Exception 
    {
        boolean changed = false;
        
        picker1.clear();
        picker2.clear();
        
        if (isFiltered())
        {
            setFiltered(false);
            changed = true;
        }
        
        return changed;
    }
    
    
    
    /**
     * Separate code out so we can reuse it for multiple Date picker groups
     */
    private class Picker
    {
        private final String DEFAULT_TEXT = "";
        private final NumberOperator.Type DEFAULT_TYPE = NumberOperator.Type.NONE;
        
        private String previousText = DEFAULT_TEXT;
        private NumberOperator.Type previousType = DEFAULT_TYPE;
        
        final GridPane box = new GridPane();
        private final TextField textField;
        private final ComboBox<NumberOperator.Type> typeBox;
        
        private Picker(NumberOperator.Type[] choices)
        {    
            textField = new TextField(DEFAULT_TEXT);
            
            typeBox = new ComboBox<>();
            typeBox.setMaxWidth(Double.MAX_VALUE);
            typeBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NumberOperator.Type>() {
                @Override
                public void changed(ObservableValue<? extends NumberOperator.Type> ov, NumberOperator.Type old, NumberOperator.Type newVal) {
                    textField.setDisable(newVal == NumberOperator.Type.NONE);
                }
            });
            typeBox.getSelectionModel().select(DEFAULT_TYPE);
            typeBox.getItems().addAll(choices);
            
            GridPane.setRowIndex(typeBox, 0);
            GridPane.setColumnIndex(typeBox, 0);
            GridPane.setMargin(typeBox, new Insets(4, 0, 0, 0));
            GridPane.setRowIndex(textField, 1);
            GridPane.setColumnIndex(textField, 0);
            GridPane.setMargin(textField, new Insets(4, 0, 0, 0));
            final ColumnConstraints boxConstraint = new ColumnConstraints();
            boxConstraint.setPercentWidth(100);
            box.getColumnConstraints().addAll(boxConstraint);
            box.getChildren().addAll(typeBox, textField);
            
            setFilterMenuContent(box);
        }
        
        public void setEnabled(boolean enable)
        {
            typeBox.setDisable(!enable);
            textField.setDisable(!enable || typeBox.getSelectionModel().getSelectedItem() == NumberOperator.Type.NONE);
        }
        
        public void cancel()
        {
            textField.setText(previousText);
            typeBox.getSelectionModel().select(previousType);
        }
        
        public void clear()
        {
            previousText = DEFAULT_TEXT;
            previousType = DEFAULT_TYPE;

            textField.setText(DEFAULT_TEXT);
            typeBox.getSelectionModel().select(DEFAULT_TYPE);
        }
        
        public boolean save()
        {
            final boolean changed = previousType != typeBox.getSelectionModel().getSelectedItem()
                    || (typeBox.getSelectionModel().getSelectedItem() != NumberOperator.Type.NONE 
                        && previousText.equals(textField.getText()) == false);
            
            previousText = textField.getText();
            previousType = typeBox.getSelectionModel().getSelectedItem();
            
            return changed;
        }
        
        @SuppressWarnings({ "rawtypes", "unchecked" })
		public NumberOperator<T> getFilter() throws Exception
        {
            final String text = textField.getText();
            final NumberOperator.Type selectedType = typeBox.getSelectionModel().getSelectedItem();
            
            if (typeBox.isDisable() || selectedType == NumberOperator.Type.NONE)
            {
                return new NumberOperator(NumberOperator.Type.NONE, 0);
            }
            else
            {
                if (text.isEmpty()) 
                {
                    throw new Exception("Filter text cannot be empty");
                } 
                else
                {
                    Number number;
                    if (klass == BigInteger.class)
                    {
                        number = new BigInteger(text);
                    }
                    else if (klass == BigDecimal.class)
                    {
                        number = new BigDecimal(text);
                    }
                    else if (klass == Byte.class)
                    {
                        number = Byte.parseByte(text);
                    }
                    else if (klass == Short.class)
                    {
                        number = Short.parseShort(text);
                    }
                    else if (klass == Integer.class)
                    {
                        number = Integer.parseInt(text);
                    }
                    else if (klass == Long.class)
                    {
                        number = Long.parseLong(text);
                    }
                    else if (klass == Float.class)
                    {
                        number = Float.parseFloat(text);
                    }
                    else // Double
                    {
                        number = Double.parseDouble(text);
                    }
                    
                    return new NumberOperator(selectedType, number);
                }
            }
        }
    };
    
}

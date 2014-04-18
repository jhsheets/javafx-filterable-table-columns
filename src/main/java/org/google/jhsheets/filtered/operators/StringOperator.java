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
package org.google.jhsheets.filtered.operators;

import java.util.EnumSet;

/**
 *
 * @author JHS
 */
public class StringOperator implements IFilterOperator<String>
{
	public static final EnumSet<Type> VALID_TYPES = EnumSet.of(
			Type.NONE
			, Type.EQUALS
			, Type.NOTEQUALS
			, Type.CONTAINS
			, Type.STARTSWITH
			, Type.ENDSWITH
			);

    private final IFilterOperator.Type type;
    private final String value;
    
    public StringOperator(IFilterOperator.Type type, String value)
    {
        this.type = type;
        this.value = value;
    }
    
    @Override
    public IFilterOperator.Type getType()
    {
        return type;
    }
    
    @Override
    public String getValue()
    {
        return value;
    }
    
}

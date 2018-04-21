package org.dilithium.Exceptions;

/* 
 * Copyright (C) 2018 Dilithium Team .
 *
 * The Dilithium library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

public class AxiomUpdateException extends java.lang.RuntimeException {
    public AxiomUpdateException() {
        super("This Object 'Axiom' didn't have an update to call. Perhaps the objects did not require an axiom update? -see www.dilithium-manual.com -> axiom ");
        /* Further information for axiom developers:
         * Each axiom (org.dilithium.core.axiom) has an update block and update transaction method which is called pre mining, signing.
         * If your axiom doesnt require an Update then make sure requireBlockUpdate() and requireTransactonUpdate() return false.
         * Axioms updates are called everytime the axiomData/merkletree changes within the object.
        */
    }
}

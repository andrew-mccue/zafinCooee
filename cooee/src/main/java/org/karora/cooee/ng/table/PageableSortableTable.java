/* 
 * This file is part of the Echo2 Table Extension (hereinafter "ETE").
 * Copyright (C) 2002-2005 NextApp, Inc.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */
package org.karora.cooee.ng.table;

import org.karora.cooee.app.table.DefaultTableColumnModel;
import org.karora.cooee.app.table.TableColumnModel;
import org.karora.cooee.app.table.TableModel;

/**
 * Convenience Table class for <code>PageableTableModel</code> 
 * and <code>SortableTableModel</code>
 * 
 * @author Jason Dalton
 * 
 */
public class PageableSortableTable extends SortableTable {

    /**
     * Creates a PageableSortableTable with a 
     * <code>DefaultTableColumnModel</code> and 
     * <code>DefaultPageableSortableTableModel </code>
     */
    public PageableSortableTable() {
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        DefaultPageableSortableTableModel model = new DefaultPageableSortableTableModel(columnModel);
        setModel(model);
        setColumnModel(columnModel);
    }

    public PageableSortableTable(TableModel model) {
        super(model);
        if ( (model instanceof SortableTableModel) == false){
            throw new IllegalArgumentException("Model must be of type SortableTableModel");
        }
        if ( (model instanceof PageableTableModel) == false){
            throw new IllegalArgumentException("Model must be of type PageableSortableTableModel");
        }
    }

    public PageableSortableTable(TableModel model, TableColumnModel columnModel) {
        super(model, columnModel);
        if ( (model instanceof SortableTableModel) == false){
            throw new IllegalArgumentException("Model must be of type SortableTableModel");
        }
        if ( (model instanceof PageableTableModel) == false){
            throw new IllegalArgumentException("Model must be of type PageableSortableTableModel");
        }
    }

}

package org.karora.cooee.app.menu;

import org.karora.cooee.app.event.ChangeListener;

public interface MenuSelectionModel {
    
    void addChangeListener(ChangeListener l);
    
    void removeChangeListener(ChangeListener l);
    
    void setSelectedId(String id);
    
    String getSelectedId();
    
}

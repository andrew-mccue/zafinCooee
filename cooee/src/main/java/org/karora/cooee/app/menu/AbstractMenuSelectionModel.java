package org.karora.cooee.app.menu;

import java.util.EventListener;

import org.karora.cooee.app.event.ChangeEvent;
import org.karora.cooee.app.event.ChangeListener;
import org.karora.cooee.app.event.EventListenerList;

public abstract class AbstractMenuSelectionModel implements MenuSelectionModel {

    private EventListenerList listenerList = new EventListenerList();

    /**
     * @see org.karora.cooee.app.menu.MenuSelectionModel#addChangeListener(org.karora.cooee.app.event.ChangeListener)
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.addListener(ChangeListener.class, l);
    }

    /**
     * Notifies <code>ChangeListener</code>s of a selection state change.
     */
    protected void fireStateChanged() {
        ChangeEvent e = new ChangeEvent(this);
        EventListener[] listeners = listenerList.getListeners(ChangeListener.class);
        for (int i = 0; i < listeners.length; ++i) {
            ((ChangeListener) listeners[i]).stateChanged(e);
        }
    }

    /**
     * @see org.karora.cooee.app.menu.MenuSelectionModel#removeChangeListener(org.karora.cooee.app.event.ChangeListener)
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.removeListener(ChangeListener.class, l);
    }

}

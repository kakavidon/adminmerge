/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger.ui;

import java.util.EventListener;

/**
 *
 * @author lnedelc
 */
public interface TaskListener extends EventListener {

    public void percentDone(final TaskEvent taskEvent);
    
}

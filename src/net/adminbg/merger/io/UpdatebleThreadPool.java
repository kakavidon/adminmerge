/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger.io;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.adminbg.merger.ui.MainWindow;

/**
 *
 * @author kakavidon
 */
public class UpdatebleThreadPool extends ThreadPoolExecutor {

    private final MainWindow invoker;

    public UpdatebleThreadPool(int nThreads, final MainWindow invoker) {
        super(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        this.invoker = invoker;
    }


    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                FileTask result = ((Future<FileTask>) r).get();
                invoker.printTask(result);
                invoker.updateProgress(result.getPercentDone());
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // ignore/reset
            }
        }
        if (t != null) {
            System.out.println(t);
        }
    }

}

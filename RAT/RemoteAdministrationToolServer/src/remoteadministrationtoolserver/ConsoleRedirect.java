/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package remoteadministrationtoolserver;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 *
 * @author Cyber
 */
public class ConsoleRedirect extends PrintStream {
    private ConsoleCallback callback;

    public ConsoleCallback getCallback() {
        return callback;
    }

    public void setCallback(ConsoleCallback callback) {
        this.callback = callback;
    }

    public ConsoleRedirect(OutputStream out) {
        super(out);
    }
    
    @Override
    public void println(String message) {
        //super.println(new Date().getTime()+":"+message);
        if(this.callback!=null)
            this.callback.update(new Date().getTime(), message+"\r\n");
    }

    @Override
    public void println(Object message) {
        //super.println(new Date().getTime()+":"+message);
        if(this.callback!=null)
            this.callback.update(new Date().getTime(), message.toString()+"\r\n");
    }
    
    @Override
    public void print(String message) {
        //super.print(new Date().getTime()+":"+message);
        if(this.callback!=null)
            this.callback.update(new Date().getTime(), message);
    }

    @Override
    public void print(Object message) {
        //super.print(new Date().getTime()+":"+message);
        if(this.callback!=null)
            this.callback.update(new Date().getTime(), message.toString());
    }
    
    @Override
    public PrintStream printf(String format,Object... message) {
        //super.printf(format, message);
        if(this.callback!=null)
            this.callback.update(new Date().getTime(), String.format(format, message));
        return this;
    }
    
}

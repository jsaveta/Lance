package util;

/**
 * @author jsaveta, Foundation for Research and Technology-Hellas (FORTH)
 *
 * @date Apr 1, 2015
 */
import java.io.*;
import java.text.*;
import java.util.*;
import java.io.FileOutputStream;

public class Logger {
    private static String logFile = "msglog.txt";
    private static String tsvLogFile = "msglog.tsv";
    private final static DateFormat df = new SimpleDateFormat ("yyyy.MM.dd  hh:mm:ss ");
    
    public Logger() {
    	File file = new File(logFile);
    	File tsvFile = new File(tsvLogFile);
    	try{
    		new FileOutputStream(file, false);
    		new FileOutputStream(tsvFile, false);
    	}catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public static void setLogFilename(String filename) {
        logFile = filename;
        new File(filename).delete();

        try {
            write("LOG file : " + filename);
        }
        catch (Exception e) { 
            System.out.println(stack2string(e));
        }   
    }
    
    public static void write(String msg) {
        write(logFile, msg);
    }
    
    public static void write(Exception e) {
        write(logFile, stack2string(e));
    }
    public static void writeTsv(String msg) {
        write(tsvLogFile, msg);
    }
    
    public static void writeTsv(Exception e) {
        write(tsvLogFile, stack2string(e));
    }
    
    public static void write(String file, String msg) {
        try {
            Date now = new Date();
            String currentTime = Logger.df.format(now); 
            FileWriter aWriter = new FileWriter(file, true);
            aWriter.write(currentTime + " " + msg 
                    + System.getProperty("line.separator"));
            aWriter.flush();
            aWriter.close();
        }
        catch (Exception e) {
            System.out.println(stack2string(e));
        }
    }
    
    private static String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
        }
        catch(Exception e2) {
            return "bad stack2string";
        }
    }
}

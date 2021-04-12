package noppes.mpm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class LogWriter {
  private static final String name = "MorePlayerModels";

  private static final Logger logger = Logger.getLogger("MorePlayerModels");

  private static final SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");

  private static Handler handler;

  static {
    try {
      File dir = new File("logs");
      if (!dir.exists())
        dir.mkdir();
      File file = new File(dir, "MorePlayerModels-latest.log");
      File lock = new File(dir, "MorePlayerModels-latest.log.lck");
      File file1 = new File(dir, "MorePlayerModels-1.log");
      File file2 = new File(dir, "MorePlayerModels-2.log");
      File file3 = new File(dir, "MorePlayerModels-3.log");
      if (lock.exists())
        lock.delete();
      if (file3.exists())
        file3.delete();
      if (file2.exists())
        file2.renameTo(file3);
      if (file1.exists())
        file1.renameTo(file2);
      if (file.exists())
        file.renameTo(file1);
      handler = new StreamHandler(new FileOutputStream(file), new Formatter() {
    	    @Override
            public String format(LogRecord record) {
              StackTraceElement element = null;
              for (int i = (Thread.currentThread().getStackTrace()).length; i > 0; i--) {
                StackTraceElement el = Thread.currentThread().getStackTrace()[i - 1];
                if (el.getClassName().contains("noppes.mpm.LogWriter")) {
                  element = Thread.currentThread().getStackTrace()[i];
                  break;
                }
              }
              String line = "[" + element.getClassName() + ":" + element.getLineNumber() + "] ";
              String time = "[" + LogWriter.dateformat.format(new Date(record.getMillis())) + "][" + record.getLevel() + "/" + "MorePlayerModels" + "]" + line;
              if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                return time + sw.toString();
              }
              return time + record.getMessage() + System.getProperty("line.separator");
            }
          });
      handler.setLevel(Level.ALL);
      logger.addHandler(handler);
      logger.setUseParentHandlers(false);
      Handler consoleHandler = new ConsoleHandler();
      consoleHandler.setFormatter(handler.getFormatter());
      consoleHandler.setLevel(Level.ALL);
      logger.addHandler(consoleHandler);
      logger.setLevel(Level.ALL);
      info((new Date()).toString());
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void info(Object msg) {
    logger.log(Level.FINE, msg.toString());
    handler.flush();
  }

  public static void warn(Object msg) {
    logger.log(Level.WARNING, msg.toString());
    handler.flush();
  }

  public static void error(Object msg) {
    logger.log(Level.SEVERE, msg.toString());
    handler.flush();
  }

  public static void error(Object msg, Exception e) {
    logger.log(Level.SEVERE, msg.toString());
    logger.log(Level.SEVERE, e.getMessage(), e);
    handler.flush();
  }

  public static void except(Exception e) {
    logger.log(Level.SEVERE, e.getMessage(), e);
    handler.flush();
  }
}
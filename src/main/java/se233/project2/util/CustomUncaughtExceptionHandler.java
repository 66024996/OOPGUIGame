package se233.project2.util;

public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.err.println(e.getClass().getSimpleName() + " in thread '" + t.getName() + "': " + e.getMessage());

        for (StackTraceElement element : e.getStackTrace()) {
            System.err.println(" at " + element);
        }
    }
}

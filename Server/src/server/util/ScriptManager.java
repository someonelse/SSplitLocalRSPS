package server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import server.util.log.Logger;

public class ScriptManager {

    public static final PythonInterpreter python = new PythonInterpreter();

    public static PyObject getVariable(String variable) {
        try {
            return python.get(variable);
        } catch (PyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object callFunc(Class<?> c, String funcName, Object... binds) {
        try {
            PyObject obj = python.get(funcName);
            if (obj instanceof PyFunction func) {
                PyObject[] objects = new PyObject[binds.length];
                for (int i = 0; i < binds.length; i++) {
                    objects[i] = Py.java2py(binds[i]);
                }
                return func.__call__(objects).__tojava__(c);
            }
            return null;
        } catch (PyException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean callFunc(String funcName, Object... binds) {
        try {
            PyObject obj = python.get(funcName);
            if (obj instanceof PyFunction func) {
                PyObject[] objects = new PyObject[binds.length];
                for (int i = 0; i < binds.length; i++) {
                    objects[i] = Py.java2py(binds[i]);
                }
                func.__call__(objects);
                return true;
            }
            return false;
        } catch (PyException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void loadScripts() throws IOException {
        System.out.println("Loading scripts...");
        python.cleanup();
        int scriptsLoaded = loadDirectory(new File("./Data/scripts/"));
        System.out.println("Loaded " + scriptsLoaded + " scripts!");
    }

    private static int loadDirectory(File dir) throws IOException {
        int scriptsLoaded = 0;
        if (dir.isDirectory() && !dir.getName().startsWith(".")) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isFile() && child.getName().endsWith(".py")) {
                        System.out.println("\tLoading script: " + child.getPath());
                        try (FileInputStream in = new FileInputStream(child)) {
                            python.execfile(in);
                            scriptsLoaded++;
                        }
                    } else if (child.isDirectory()) {
                        scriptsLoaded += loadDirectory(child);
                    }
                }
            }
        }
        return scriptsLoaded;
    }

    static {
        python.setOut(new Logger(System.out));
        python.setErr(new Logger(System.err));
    }
}

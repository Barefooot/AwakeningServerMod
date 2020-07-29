package net.spirangle.awakening.util;

import javassist.*;
import javassist.expr.ExprEditor;
import org.gotti.wurmunlimited.modloader.classhooks.HookException;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Syringe {

    private static final Logger logger = Logger.getLogger(Syringe.class.getName());

    private static ClassPool classPool = null;

    private CtClass ctClass = null;
    private CtMethod ctMethod = null;


    public static Syringe getClass(String className) {
        CtClass ctClass = null;
        if(classPool==null) {
            classPool = HookManager.getInstance().getClassPool();
        }
        try {
            ctClass = classPool.get(className);
            ctClass.defrost();
        } catch(NotFoundException e) {
            logger.log(Level.SEVERE,e.getMessage(),e);
            throw new HookException(e);
        }
        return new Syringe(ctClass);
    }

    private Syringe(CtClass ctClass) {
        this.ctClass = ctClass;
    }

    public CtClass getCtClass() { return ctClass; }

    public CtMethod getCtMethod() { return ctMethod; }

    public Syringe addField(String field,String value) {
        if(ctClass!=null) {
            try {
                ctClass.addField(CtField.make(field,ctClass),value);
                logger.info(ctClass.getName()+": Added field "+field+" = "+value+"");
            } catch(CannotCompileException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe addMethod(String method) {
        if(ctClass!=null) {
            try {
                ctClass.addMethod(CtNewMethod.make(method,ctClass));
                logger.info(ctClass.getName()+": Added method "+method);
            } catch(CannotCompileException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe setBody(String method,String code,String message) {
        if(ctClass!=null) {
            try {
                ctMethod = ctClass.getDeclaredMethod(method);
                setBody(code,message);
            } catch(NotFoundException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe setBody(String method,String signature,String code,String message) {
        if(ctClass!=null) {
            try {
                ctMethod = ctClass.getMethod(method,signature);
                setBody(code,message);
            } catch(NotFoundException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe setBody(String code,String message) {
        if(ctMethod!=null) {
            try {
                ctMethod.setBody(code);
                logger.info(ctClass.getName()+": Set body in "+ctMethod.getName()+(message!=null? " ("+message+")" : ""));
            } catch(CannotCompileException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe insertBefore(String method,String code,String message) {
        if(ctClass!=null) {
            try {
                ctMethod = ctClass.getDeclaredMethod(method);
                insertBefore(code,message);
            } catch(NotFoundException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe insertBefore(String method,String signature,String code,String message) {
        if(ctClass!=null) {
            try {
                ctMethod = ctClass.getMethod(method,signature);
                insertBefore(code,message);
            } catch(NotFoundException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe insertBefore(String code,String message) {
        if(ctMethod!=null) {
            try {
                ctMethod.insertBefore(code);
                logger.info(ctClass.getName()+": Insert before "+ctMethod.getName()+(message!=null? " ("+message+")" : ""));
            } catch(CannotCompileException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe insertAfter(String method,String code,String message) {
        if(ctClass!=null) {
            try {
                ctMethod = ctClass.getDeclaredMethod(method);
                insertAfter(code,message);
            } catch(NotFoundException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    @SuppressWarnings("unused")
    public Syringe insertAfter(String method,String signature,String code,String message) {
        if(ctClass!=null) {
            try {
                ctMethod = ctClass.getMethod(method,signature);
                insertAfter(code,message);
            } catch(NotFoundException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe insertAfter(String code,String message) {
        if(ctMethod!=null) {
            try {
                ctMethod.insertAfter(code);
                logger.info(ctClass.getName()+": Insert after "+ctMethod.getName()+(message!=null? " ("+message+")" : ""));
            } catch(CannotCompileException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe instrument(String method,ExprEditor editor) {
        if(ctClass!=null) {
            try {
                ctMethod = ctClass.getDeclaredMethod(method);
                instrument(editor);
            } catch(NotFoundException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe instrument(String method,String signature,ExprEditor editor) {
        if(ctClass!=null) {
            try {
                ctMethod = ctClass.getMethod(method,signature);
                instrument(editor);
            } catch(NotFoundException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }

    public Syringe instrument(ExprEditor editor) {
        if(ctMethod!=null) {
            try {
                ctMethod.instrument(editor);
            } catch(CannotCompileException e) {
                logger.log(Level.SEVERE,e.getMessage(),e);
                throw new HookException(e);
            }
        }
        return this;
    }
}



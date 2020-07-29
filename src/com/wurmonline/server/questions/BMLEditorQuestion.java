package com.wurmonline.server.questions;

import com.wurmonline.server.creatures.Creature;

import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.spirangle.awakening.AwakeningConstants.BML_EDITOR_QUESTION_TYPE;


public class BMLEditorQuestion extends Question {

    private static final Logger logger = Logger.getLogger(BMLEditorQuestion.class.getName());

    private static final Pattern bmlPattern = Pattern.compile("(?:(\\\\n)|\\#(\\w+)\\#)");

    private String bml;
    private int width;
    private int height;
    private boolean render;

    public BMLEditorQuestion(Creature responder,String bml,int width,int height,boolean render) {
        super(responder,"BML Editor","",BML_EDITOR_QUESTION_TYPE,responder.getWurmId());
        if(bml!=null && bml.length()>0) {
            Matcher m = bmlPattern.matcher(bml);
            StringBuffer buffer = new StringBuffer(bml.length());
            String replacement, token;
            while(m.find()) {
                replacement = null;
                if(m.group(1)!=null) {
                    replacement = "\n";
                } else if(m.group(2)!=null) {
                    token = m.group(2);
                    if("id".equals(token)) replacement = String.valueOf(getId());
                }
                if(replacement!=null) {
                    m.appendReplacement(buffer,replacement);
                }
            }
            m.appendTail(buffer);
            bml = buffer.toString();
        }
        this.bml = bml;
        this.width = width;
        this.height = height;
        this.render = render;
    }

    @Override
    public void sendQuestion() {
        Creature responder = this.getResponder();
        StringBuilder bml = new StringBuilder();
        int w = this.width;
        int h = this.height;
        if(this.render) {
            bml.append(this.bml);
        } else {
            w = 500;
            h = 350;
            bml.append("border{\n"+
                       " varray{rescale='true';\n"+
                       "  text{type='bold';text=\"Edit BML to see how it will render:\"}\n"+
                       " };\n"+
                       " null;\n"+
                       " scroll{horizontal='false';vertical='true';\n"+
                       "  varray{rescale='true';\n"+
                       "   passthrough{id='id';text='"+getId()+"'};\n"+
                       "   input{id='bml';bgcolor='100,100,100';maxchars='2048';maxlines='-1';text='"+this.bml+"'}\n"+
                       "  }\n"+
                       " };\n"+
                       " null;\n"+
                       " right{\n"+
                       "  harray{\n"+
                       "   button{id='cancel';size='80,20';text='Cancel'}\n"+
                       "   button{id='submit';size='80,20';text='Submit'}\n"+
                       "  }\n"+
                       " }\n"+
                       "}\n");
        }
        responder.getCommunicator().sendBml(w,h,false,true,bml.toString(),200,200,200,this.title);
    }

    @Override
    public void answer(Properties properties) {
        Creature responder = this.getResponder();

        if("true".equals(properties.getProperty("cancel"))) return;

        String bml = properties.getProperty("bml","");
        BMLEditorQuestion question = new BMLEditorQuestion(responder,bml,this.width,this.height,bml.length()>0);
        question.sendQuestion();
    }
}

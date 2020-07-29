package net.spirangle.awakening.items;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;

import java.util.logging.Level;
import java.util.logging.Logger;


public class StockItem {

    private static final Logger logger = Logger.getLogger(StockItem.class.getName());

    public int templateId;
    public int amount;
    public int ql;
    public int price;
    public byte aux;
    public String name;
    public int counter;

    public StockItem(int templateId,int amount,int ql,int price) {
        this(templateId,amount,ql,price,(byte)-128,null);
    }

    @SuppressWarnings("unused")
    public StockItem(int templateId,int amount,int ql,int price,byte aux) {
        this(templateId,amount,ql,price,aux,null);
    }

    @SuppressWarnings("unused")
    public StockItem(int templateId,int amount,int ql,int price,String name) {
        this(templateId,amount,ql,price,(byte)-128,name);
    }

    public StockItem(int templateId,int amount,int ql,int price,byte aux,String name) {
        this.templateId = templateId;
        this.amount = amount;
        this.ql = ql;
        this.price = price;
        this.aux = aux;
        this.name = name;
        this.counter = 0;
    }

    public void restock(Item inventory) {
        if(this.amount>this.counter)
            logger.info("StockItem: Restock "+templateId+" from "+this.counter+" to "+this.amount+".");
        for(int i = 0, n = (this.amount-this.counter); i<n; ++i) {
            try {
                Item item = ItemFactory.createItem(templateId,ql,null);
                item.setPrice(this.price);
                if(this.aux!=-128) item.setAuxData(this.aux);
                if(this.name!=null) item.setName(this.name);
                inventory.insertItem(item,true);
            } catch(Exception e) {
                logger.log(Level.WARNING,"StockItem: Could not create item "+templateId+".",e);
            }
        }
        this.counter = 0;
    }
}


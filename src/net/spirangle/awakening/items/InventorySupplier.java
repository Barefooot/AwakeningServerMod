package net.spirangle.awakening.items;

import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
import com.wurmonline.server.creatures.NoSuchCreatureException;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import net.spirangle.awakening.Config;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


public class InventorySupplier {

    private static InventorySupplier instance = null;

    public static InventorySupplier getInstance() {
        if(instance==null) instance = new InventorySupplier();
        return instance;
    }

    private class Supplier {
        Creature creature;
        StockItem[] items;

        public Supplier(Creature creature,StockItem[] items) {
            this.creature = creature;
            this.items = items;
        }

        public void update() {
            int i, n, t;
            Item inventory = creature.getInventory();
            Item[] itemarr = inventory.getAllItems(false);
            Item item;
            StockItem si;
            for(n = 0; n<this.items.length; ++n)
                this.items[n].counter = 0;
            for(i = 0; i<itemarr.length; ++i) {
                item = itemarr[i];
                if(item.isCoin()) Items.destroyItem(item.getWurmId());
                else {
                    t = item.getTemplateId();
                    for(n = 0; n<this.items.length; ++n) {
                        si = this.items[n];
                        if(si.templateId==t && (si.aux==(byte)-128 || si.aux==item.getAuxData()))
                            ++si.counter;
                    }
                }
            }
            for(n = 0; n<this.items.length; ++n)
                this.items[n].restock(inventory);
        }
    }


    private Hashtable<Creature,Supplier> suppliers;

    private InventorySupplier() {
        suppliers = new Hashtable<Creature,Supplier>();
    }

    public void init() {
        if(Config.kingdomTraders==null) return;
        StockItem[] traderWares = new StockItem[]{
            new StockItem(ItemList.chestNoDecaySmall,3,40,250000),
            new StockItem(ItemList.rodTransmutation,3,80,500000),
            new StockItem(ItemList.tuningFork,3,80,10000),
            new StockItem(ItemList.merchantContract,1,80,100000),
            new StockItem(ItemList.teleportationTwig,3,80,50000),
            new StockItem(ItemList.resurrectionStone,3,80,50000),
            new StockItem(ItemList.teleportationStone,3,80,50000),
            new StockItem(ItemList.shakerOrb,3,80,50000),
            new StockItem(ItemList.chestNoDecayLarge,3,40,500000),
            new StockItem(ItemList.handMirror,1,40,50000),
            new StockItem(ItemList.fireworks,3,40,5000),
        };
        for(int i = 0; i<Config.kingdomTraders.length; ++i) {
            addInventory(Config.kingdomTraders[i],traderWares);
        }
    }

    public void addInventory(long wurmId,StockItem[] items) {
        try {
            Creature creature = Creatures.getInstance().getCreature(wurmId);
            if(creature!=null) {
                suppliers.put(creature,new Supplier(creature,items));
            }
        } catch(NoSuchCreatureException e) {}
    }

    public void update() {
        Set<Creature> keys = suppliers.keySet();
        Iterator<Creature> itr = keys.iterator();
        Creature creature;
        Supplier supplier;
        while(itr.hasNext()) {
            creature = itr.next();
            supplier = suppliers.get(creature);
            supplier.update();
        }
    }
}



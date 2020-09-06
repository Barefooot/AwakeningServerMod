package net.spirangle.awakening;

import com.wurmonline.server.behaviours.Actions;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import net.spirangle.awakening.util.Syringe;

import java.util.logging.Logger;


public class CodeInjections {

    private static final Logger logger = Logger.getLogger(CodeInjections.class.getName());


    public static class IsBulkModifier extends ExprEditor {
        final Syringe s;
        final String method;
        final String target;
        final int n;
        int i = 0;

        public IsBulkModifier(Syringe s,String method,String target,int n) {
            this.s = s;
            this.method = method;
            this.target = target;
            this.n = n;
        }

        @Override
        public void edit(final MethodCall mc) throws CannotCompileException {
            if(mc.getMethodName().equals(method)) {
                if(n<0 || i==n) {
                    mc.replace("$_ = (net.spirangle.awakening.items.ItemTemplateCreatorAwakening.isBulkContainer("+target+") || $proceed($$));");
                    logger.info(s.getCtClass().getName()+": Installed modify "+s.getCtMethod().getName()+" ("+method+"), additional bulk containers ["+mc.getLineNumber()+"].");
                }
                ++i;
            }
        }
    }

    public static void preInit() {

        if(Config.useHandleServerLag) {
            /* Server: */
            final Syringe server = Syringe.getClass("com.wurmonline.server.Server");
            server.insertAfter("run","net.spirangle.awakening.time.Scheduler.handleServerLag(com.wurmonline.server.Server.secondsLag);",null);
        }

        /* LoginHandler: */
        final Syringe lh = Syringe.getClass("com.wurmonline.server.LoginHandler");
        if(Config.useAcceptLoginDifferentIPs) {
            lh.instrument("reallyHandle",new ExprEditor() {
                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("addLoginHandler")) {
                        mc.replace("$proceed($$);$_ = true;");
                        logger.info("LoginHandler: Accept login from different IPs.");
                    }
                }
            });
        }
        if(Config.useOneCharacterPerSteamId || Config.useCharactersOnlySameKingdom) {
            lh.insertBefore("login",
                            "{\n"+
                            "   String text = net.spirangle.awakening.players.LoginHandler.playerLoginTest($1,$2,$6);\n"+
                            "   if(text!=null) {\n"+
                            "      try {\n"+
                            "         com.wurmonline.server.Server.getInstance().steamHandler.EndAuthSession(steamIDAsString);\n"+
                            "         sendLoginAnswer(false,text,0.0f,0.0f,0.0f,0.0f,0,\"model.player.broken\",(byte)0,0);\n"+
                            "      } catch(java.io.IOException ioe) {\n"+
                            "         com.wurmonline.server.LoginHandler.logger.log(java.util.logging.Level.WARNING,this.conn.getIp()+\", problem sending login denied message: \"+text,ioe);\n"+
                            "      }\n"+
                            "      return;\n"+
                            "   }\n"+
                            "}",null);
        }

        /* Player: */
        final Syringe player = Syringe.getClass("com.wurmonline.server.players.Player");
        if(Config.useCustomPetHandling) {
            player.addField("public int petOrderState;","0");
        }
        if(Config.usePlayerHoverText) {
            player.addMethod("public String getHoverText(com.wurmonline.server.creatures.Creature watcher) { return net.spirangle.awakening.players.PlayerData.getHoverText(this,super.getHoverText(watcher)); }");
        }
        if(Config.useMayorsCanTurnUnlawful) {
            player.instrument("setLegal",new ExprEditor() {
                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("getKingdomTemplateId")) {
                        mc.replace("$_ = 3;");
                        logger.info("setLegal: Permit players of all kingdoms turn unlawful, even mayors.");
                    }
                }
            });
        }

        /* Creature: */
        final Syringe creature = Syringe.getClass("com.wurmonline.server.creatures.Creature");
        if(Config.useCustomPetHandling) {
            creature.addMethod("public int getPetOrderState() { return 0; }");
            creature.addMethod("public void setPetOrderState(int state) {}");
        }
        if(Config.useOffspringNames) {
            creature.instrument("checkPregnancy",new ExprEditor() {
                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("isHorse")) {
                        mc.replace("$_ = !this.isUnicorn();");
                        logger.info("Creature: Installed change for all bred animals to get offspring names.");
                    } else if(mc.getMethodName().equals("generateGenericName")) {
                        mc.replace("$_ = net.spirangle.awakening.creatures.Offspring.generateGenericName(this);\n"+
                                   "if($_==null) $_ = $proceed($$);");
                        logger.info("Creature: Installed generate generic name.");
                    } else if(mc.getMethodName().equals("generateFemaleName")) {
                        mc.replace("$_ = net.spirangle.awakening.creatures.Offspring.generateFemaleName(this);\n"+
                                   "if($_==null) $_ = $proceed($$);");
                        logger.info("Creature: Installed generate female name.");
                    } else if(mc.getMethodName().equals("generateGenericName")) {
                        mc.replace("$_ = net.spirangle.awakening.creatures.Offspring.generateMaleName(this);\n"+
                                   "if($_==null) $_ = $proceed($$);");
                        logger.info("Creature: Installed generate male name.");
                    }
                }
            });
        }
        if(Config.useCustomPetHandling) {
            creature.insertBefore("clearOrders",
                                  "com.wurmonline.server.creatures.Creature dominator = this.getDominator();\n"+
                                  "if(dominator!=null && dominator.isPlayer()) dominator.setPetOrderState(0);",null);
        }
        if(Config.useCorpseColourCreatureFix) {
            creature.instrument("die","(ZLjava/lang/String;Z)V",new ExprEditor() {
                int i = 0;

                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("setAuxData")) {
                        if(i==1) {
                            mc.replace("$_ = $proceed($$);\n"+
                                       "$0.setColor(com.wurmonline.server.items.WurmColor.createColor(this.getColorRed(),this.getColorGreen(),this.getColorBlue()));");
                            logger.info("Creature: Make corpse keep creature color.");
                        }
                        ++i;
                    }
                }
            });
        }

        if(Config.useCustomPetHandling) {
            /* Player: */
            player.addMethod("public int getPetOrderState() { return this.petOrderState; }");
            player.addMethod("public void setPetOrderState(int state) { this.petOrderState = state;this.getStatus().sendStateString(); }");

            /* Creature: */
            creature.instrument("getAttitude",new ExprEditor() {
                int i = 0;

                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("getAttitude")) {
                        if(i==0) {
                            mc.replace("$_ = net.spirangle.awakening.creatures.Pets.getPetAttitude($1,$0,$1.getPetOrderState());");
                            logger.info("Creature: Insert changed model for pet towards target attitude.");
                        } else if(i==1 || i==2) {
                            mc.replace("$_ = net.spirangle.awakening.creatures.Pets.getTargetAttitude($1,$0,$1.getPetOrderState());");
                            logger.info("Creature: Insert changed model for target towards pet attitude.");
                        }
                        ++i;
                    }
                }
            });

            /* CreatureBehaviour: */
            final Syringe cb = Syringe.getClass("com.wurmonline.server.behaviours.CreatureBehaviour");
            cb.instrument("action","(Lcom/wurmonline/server/behaviours/Action;Lcom/wurmonline/server/creatures/Creature;Lcom/wurmonline/server/creatures/Creature;SF)Z",new ExprEditor() {
                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("attackTarget")) {
                        mc.replace("if(performer.isPlayer()) performer.setPetOrderState(1);\n"+
                                   "$_ = $proceed($$);");
                        logger.info("CreatureBehaviour: Set decisions.state = 1, to automatically attack enemies.");
                    }
                }
            });
        }

        if(Config.useOffspringNames || Config.useCustomPetHandling || Config.useAdjustSizeByTraits) {
            /* CreatureStatus: */
            final Syringe cs = Syringe.getClass("com.wurmonline.server.creatures.CreatureStatus");
            if(Config.useOffspringNames) {
                cs.instrument("pollAge",new ExprEditor() {
                    @Override
                    public void edit(MethodCall mc) throws CannotCompileException {
                        if(mc.getMethodName().equals("isHorse")) {
                            mc.replace("$_ = true;");
                            logger.info("CreatureStatus: Make all animals with offspring names keep them when turning adult.");
                        }
                    }
                });
            }
            if(Config.useCustomPetHandling) {
                cs.instrument("sendStateString",new ExprEditor() {
                    @Override
                    public void edit(MethodCall mc) throws CannotCompileException {
                        if(mc.getMethodName().equals("listIterator")) {
                            mc.replace("net.spirangle.awakening.creatures.Pets.getStateString(this.statusHolder,$0,this.statusHolder.getPetOrderState());\n"+
                                       "$_ = $proceed($$);");
                            logger.info("CreatureStatus: Show pet order state in player status.");
                        }
                    }
                });
            }
            if(Config.useOffspringNames) {
                cs.instrument("getSizeMod",new ExprEditor() {
                    @Override
                    public void edit(MethodCall mc) throws CannotCompileException {
                        if(mc.getMethodName().equals("getTemplateId")) {
                            mc.replace("$_ = 0;");
                            logger.info("CreatureStatus: Make named bison retain normal size.");
                        }
                    }
                });
            }
            if(Config.useAdjustSizeByTraits) {
                cs.instrument("getSizeMod",new ExprEditor() {
                    @Override
                    public void edit(MethodCall mc) throws CannotCompileException {
                        if(mc.getMethodName().equals("getHitched")) {
                            cs.getCtMethod().insertAt(mc.getLineNumber(),"floatToRet *= net.spirangle.awakening.creatures.Traits.getSizeMod(this.statusHolder);");
                            logger.info("CreatureStatus: Modify size with certain traits for some creatures.");
                        }
                    }
                });
            }
        }

        if(Config.antiMacroHandling) {
            /* ActionStack: */
            final Syringe actst = Syringe.getClass("com.wurmonline.server.behaviours.ActionStack");
            actst.insertBefore("addAction","net.spirangle.awakening.actions.ActionStack.addAction(action);",null);
        }

        if(Config.useKingdomsMayAlly) {
            /* KingdomStatusQuestion: */
            final Syringe ksq = Syringe.getClass("com.wurmonline.server.questions.KingdomStatusQuestion");
            ksq.setBody("mayAlly","return true;",null);
        }

        if(Config.useInitializeFarmToMidnightGMT || Config.useFarmGrowthWhenTended) {
            /* CropTilePoller: */
            final Syringe ctp = Syringe.getClass("com.wurmonline.server.zones.CropTilePoller");
            if(Config.useInitializeFarmToMidnightGMT) {
                ctp.insertBefore("initializeFields",
                                 "com.wurmonline.server.zones.CropTilePoller.lastPolledTiles = net.spirangle.awakening.zones.Tiles.getLastPolledTiles();","make farm ticks at 00:00 GMT");
            }
            if(Config.useFarmGrowthWhenTended) {
                ctp.instrument("checkForFarmGrowth",new ExprEditor() {
                    int i = 0;

                    @Override
                    public void edit(MethodCall mc) throws CannotCompileException {
                        if(mc.getMethodName().equals("setTile")) {
                            if(i==2) {
                                ctp.getCtMethod().insertAt(mc.getLineNumber(),"if(!net.spirangle.awakening.zones.Tiles.checkForFarmGrowth(tempvtile1,farmed)) return;");
                                logger.info("CropTilePoller: Install block for untended field growth on deed.");
                            }
                            ++i;
                        }
                    }
                });
            }
        }

        if(Config.useAcceptingDeityNames) {
            /* Deities: */
            final Syringe deities = Syringe.getClass("com.wurmonline.server.deities.Deities");
            deities.setBody("isNameOkay","{ return true; }","fixed accepting deity names for players");
        }

        /* Item: */
        final Syringe item = Syringe.getClass("com.wurmonline.server.items.Item");
        if(Config.useIsTurnableFix) {
            item.insertBefore("isTurnable","(Lcom/wurmonline/server/creatures/Creature;)Z","if(!this.isTurnable() && this.canDisableTurning()) { return false; }",null);
        }
        if(Config.useIsMoveableFix) {
            item.insertBefore("isMoveable","if(this.isNoMove() && this.canDisableMoveable()) { return false; }",null);
        }
        if(Config.useBulkChest) {
            item.instrument("moveToItem",new IsBulkModifier(item,"isCrate","target",4));
        }
        if(Config.useCorpseSizeCreatureFix) {
            item.insertBefore("getSizeMod",
                              "if(this.getTemplateId()==272) return (float)Math.pow((double)this.getVolume()/(double)this.template.getVolume(), 0.3333333333333333);",
                              "Make corpse get same size as creature");
        }

        if(Config.useBulkChest) {
            /* ItemBehaviour: */
            final Syringe ib = Syringe.getClass("com.wurmonline.server.behaviours.ItemBehaviour");
            ib.instrument("moveBulkItemAsAction",new IsBulkModifier(ib,"isFood","target",1));

            /* Communicator: */
            final Syringe comm = Syringe.getClass("com.wurmonline.server.creatures.Communicator");
            comm.instrument("sendAddToInventory",new IsBulkModifier(comm,"isCrate","parent",1));
            comm.instrument("sendUpdateInventoryItem","(Lcom/wurmonline/server/items/Item;JI)V",new IsBulkModifier(comm,"isCrate","parent",1));
        }

        if(Config.useOffspringNames || Config.useAdjustMilkByTraits || Config.useAdjustWoolByTraits || Config.useBrandNullPointerFix) {
            /* MethodsCreatures: */
            final Syringe mthc = Syringe.getClass("com.wurmonline.server.behaviours.MethodsCreatures");
            mthc.instrument("milk",new ExprEditor() {
                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(Config.useOffspringNames && mc.getMethodName().equals("getNameWithoutPrefixes")) {
                        mc.replace("$_ = $0.getTemplate().getName();");
                        logger.info("MethodsCreatures: Changed naming of milk to use template name of creature.");
                    } else if(Config.useAdjustMilkByTraits && mc.getMethodName().equals("fillContainer")) {
                        mc.replace("net.spirangle.awakening.creatures.Traits.milk($3,$4,target);\n"+
                                   "$_ = $proceed($$);");
                        logger.info("MethodsCreatures: Installed adjust milk by traits function.");
                    }
                }
            });
            if(Config.useAdjustWoolByTraits) {
                mthc.instrument("shear",new ExprEditor() {
                    @Override
                    public void edit(MethodCall mc) throws CannotCompileException {
                        if(mc.getMethodName().equals("insertItem")) {
                            mc.replace("net.spirangle.awakening.creatures.Traits.shear($1,performer,target);\n"+
                                       "$_ = $proceed($$);");
                            logger.info("MethodsCreatures: Installed adjust wool by traits function.");
                        }
                    }
                });
            }
            if(Config.useBrandNullPointerFix) {
                mthc.instrument("brand",new ExprEditor() {
                    int i = 0;

                    @Override
                    public void edit(MethodCall mc) throws CannotCompileException {
                        if(mc.getMethodName().equals("getCitizenVillage")) {
                            if(i==0) mc.replace("$_ = ($0.getCurrentVillage()!=null? $proceed($$) : null);");
                            logger.info("MethodsCreatures: Fix brand null pointer bug.");
                            ++i;
                        }
                    }
                });
            }
        }

        if(Config.useCanPlantMarkerBlessed || Config.useMayDropDirt) {
            /* MethodsItems: */
            final Syringe mi = Syringe.getClass("com.wurmonline.server.behaviours.MethodsItems");
            if(Config.useCanPlantMarkerBlessed) {
                mi.insertBefore("cannotPlant","if(!net.spirangle.awakening.zones.Tiles.canPlantMarker($1,$2)) return true;",null);
            }
            if(Config.useMayDropDirt) {
                mi.insertAfter("mayDropDirt","if(!net.spirangle.awakening.zones.Tiles.mayDropDirt($1)) return false;",null);
            }
        }

        if(Config.useCheckForTreeSprout) {
            /* TilePoller: */
            final Syringe tp = Syringe.getClass("com.wurmonline.server.zones.TilePoller");
            tp.insertBefore("checkForTreeSprout","if(!net.spirangle.awakening.zones.Tiles.checkForTreeSprout($1,$2,$3,$4)) return true;",null);
        }

        if(Config.useContinueInHouseWithoutManage || Config.useCanAllowEveryone) {
            /* Structure: */
            final Syringe str = Syringe.getClass("com.wurmonline.server.structures.Structure");
            if(Config.useContinueInHouseWithoutManage) {
                str.instrument("isActionAllowed",new ExprEditor() {
                    @Override
                    public void edit(MethodCall mc) throws CannotCompileException {
                        if(mc.getMethodName().equals("mayModify")) {
                            mc.replace("$_ = (action=="+Actions.CONTINUE_BUILDING+" || $proceed($$));");
                            logger.info("Structure: Permit continue building inside a house without Manage Building set.");
                        }
                    }
                });
            }
            if(Config.useCanAllowEveryone) {
                str.setBody("canAllowEveryone","return true;",null);
            }
        }

        if(Config.useCanAllowEveryone) {
            /* Door: */
            final Syringe door = Syringe.getClass("com.wurmonline.server.structures.Door");
            door.setBody("canAllowEveryone","return true;",null);
        }

        if(Config.useVillagePermissionsPvEzone) {
            /* Village: */
            final Syringe village = Syringe.getClass("com.wurmonline.server.villages.Village");
            village.instrument("isActionAllowed","(SLcom/wurmonline/server/creatures/Creature;ZII)Z",new ExprEditor() {
                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("isThisAPvpServer")) {
                        mc.replace("$_ = creature.isOnPvPServer();");
                        logger.info("isActionAllowed: Use village permissions inside PvE zones.");
                    }
                }
            });
            village.instrument("mayAttack",new ExprEditor() {
                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("isThisAPvpServer")) {
                        mc.replace("$_ = attacker.isOnPvPServer();");
                        logger.info("mayAttack: Use village permissions inside PvE zones.");
                    }
                }
            });
        }

        if(Config.useCanPlantMarkerBlessed) {
            /* MethodsHighways: */
            final Syringe mhw = Syringe.getClass("com.wurmonline.server.highways.MethodsHighways");
            mhw.insertBefore("canPlantMarker","if(!net.spirangle.awakening.zones.Tiles.canPlantMarker($1,$3)) return false;",null);
        }

        if(Config.useOldSkillMeditation) {
            /* Cults: */
            final Syringe cults = Syringe.getClass("com.wurmonline.server.players.Cults");
            cults.instrument("meditate",new ExprEditor() {
                @Override
                public void edit(MethodCall mc) throws CannotCompileException {
                    if(mc.getMethodName().equals("skillCheck")) {
                        mc.replace("$_ = $proceed($1,$2,$3,$4,$5,false,2.0);");
                        logger.info("meditate: Change meditation to use the old skill system.");
                    }
                }
            });
        }

        if(Config.useClaimByEmbarkUnlockedVehicle) {
            /* Seat: */
            final Syringe seat = Syringe.getClass("com.wurmonline.server.behaviours.Seat");
            seat.insertBefore("occupy","net.spirangle.awakening.items.Vehicle.occupySeat($0,$1,$2);",null);
        }
    }
}

package com.koala.twohanded;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("twohanded")
public class TwoHanded {
    
    private static final Logger LOGGER = LogManager.getLogger();
    
    public TwoHanded() {
    }
    
    public static boolean isItemTwoHanded(ItemStack stack) {
        return TwoHandedTags.Items.TWO_HANDED.contains(stack.getItem());
    }
}

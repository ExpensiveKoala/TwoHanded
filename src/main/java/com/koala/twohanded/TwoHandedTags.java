package com.koala.twohanded;

import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

public class TwoHandedTags {
    public static class Items {
        
        public static final ITag.INamedTag<Item> TWO_HANDED = createTag("two_handed");
        
        private static ITag.INamedTag<Item> createTag(String name) {
            return ItemTags.makeWrapperTag("twohanded:" + name);
        }
    }
}

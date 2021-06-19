package com.koala.twohanded.mixins;

import com.koala.twohanded.TwoHanded;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetHandler.class)
public class HeldItemChangeMixin {
    
    @Shadow
    public ServerPlayerEntity player;
    
    @Inject(method = "processHeldItemChange", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;currentItem:I", ordinal = 0))
    public void processHeldItemChange(CHeldItemChangePacket packetIn, CallbackInfo ci) {
        if (this.player.inventory.currentItem != packetIn.getSlotId()) {
            if (TwoHanded.isItemTwoHanded(this.player.inventory.getStackInSlot(packetIn.getSlotId()))) {
                this.player.resetActiveHand();
            }
        }
    }
}

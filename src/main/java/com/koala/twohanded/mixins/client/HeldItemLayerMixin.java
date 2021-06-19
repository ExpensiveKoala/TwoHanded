package com.koala.twohanded.mixins.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.koala.twohanded.TwoHanded.isItemTwoHanded;

@Mixin(HeldItemLayer.class)
public class HeldItemLayerMixin {
    
    @Inject(
      method = "func_229135_a_",
      at = @At("HEAD"),
      cancellable = true
    )
    public void renderHeldItem(LivingEntity entity, ItemStack stack, ItemCameraTransforms.TransformType transformType, HandSide handSide, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLightIn, CallbackInfo ci) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            Hand otherHand = player.getPrimaryHand() == handSide ? Hand.OFF_HAND : Hand.MAIN_HAND;
            ItemStack otherStack = player.getHeldItem(otherHand);
            if (!otherStack.isEmpty() && isItemTwoHanded(otherStack)) {
                ci.cancel();
            }
        }
    }
}

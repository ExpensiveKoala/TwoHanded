package com.koala.twohanded;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import static com.koala.twohanded.TwoHanded.isItemTwoHanded;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
    
    protected static final ResourceLocation WIDGETS_TEX_PATH = new ResourceLocation("textures/gui/spectator_widgets.png");
    
    @SubscribeEvent
    public static void renderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        MatrixStack matrixStack = event.getMatrixStack();
        boolean flag = event.getHand() == Hand.MAIN_HAND;
        HandSide handside = flag ? mc.player.getPrimaryHand() : mc.player.getPrimaryHand().opposite();
        ItemStack otherItem = mc.player.getHeldItem(flag ? Hand.OFF_HAND : Hand.MAIN_HAND);
        
        if (!otherItem.isEmpty() && isItemTwoHanded(otherItem)) {
            event.setCanceled(true);
        }
        
    }
    
    @SubscribeEvent
    public static void renderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        MatrixStack matrixStack = event.getMatrixStack();
        PlayerEntity player = mc.player;
        MainWindow window = event.getWindow();
        for (Hand hand : Hand.values()) {
            ItemStack heldItem = player.getHeldItem(hand);
            if (!heldItem.isEmpty() && isItemTwoHanded(heldItem)) {
                mc.getTextureManager().bindTexture(WIDGETS_TEX_PATH);
                int offset = mc.ingameGUI.getBlitOffset();
                mc.ingameGUI.setBlitOffset(offset + 300);
                //RenderSystem.enableBlend();
                enableAlpha(0.95f);
                int i = window.getScaledWidth() / 2;
                if (hand == Hand.MAIN_HAND) {
                    //Render an X on offhand slot
                    if (!player.getHeldItemOffhand().isEmpty()) {
                        HandSide side = player.getPrimaryHand().opposite();
                        if (side == HandSide.LEFT) {
                            mc.ingameGUI.blit(matrixStack, i - 91 - 29 + 4, window.getScaledHeight() - 23 + 4, 129, 0, 16, 16);
                        } else {
                            mc.ingameGUI.blit(matrixStack, i + 91 + 7 + 4, window.getScaledHeight() - 23 + 4, 129, 0, 16, 16);
                        }
                    }
                } else {
                    //Render an X on mainhand slot
                    mc.ingameGUI.blit(matrixStack, i - 91 + 4 + player.inventory.currentItem * 20, window.getScaledHeight() - 23 + 4, 129, 0, 16, 16);
                }
                mc.ingameGUI.setBlitOffset(offset);
                disableAlpha(0.95f);
                //RenderSystem.disableBlend();
            }
        }
    }
    
    @SubscribeEvent
    public static void onClick(InputEvent.ClickInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Hand otherHand = event.getHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        
        ItemStack stack = mc.player.getHeldItem(otherHand);
        if (!stack.isEmpty() && isItemTwoHanded(stack)) {
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if(event.getPlayer() != player) {
            return;
        }
        for (Hand hand : Hand.values()) {
            ItemStack heldItem = player.getHeldItem(hand);
            if (!heldItem.isEmpty() && isItemTwoHanded(heldItem)) {
                HandSide side = hand == Hand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
                switch (side) {
                    case LEFT:
                        event.getRenderer().getEntityModel().rightArmPose = BipedModel.ArmPose.EMPTY;
                        break;
                    case RIGHT:
                        event.getRenderer().getEntityModel().leftArmPose = BipedModel.ArmPose.EMPTY;
                        break;
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!isItemTwoHanded(event.getItemStack())) {
            return;
        }
        event.getToolTip().add(new TranslationTextComponent("tooltip.twohanded.twohanded"));
    }
    
    public static void enableAlpha(float alpha) {
        RenderSystem.enableBlend();
        
        if (alpha == 1f)
            return;
        
        RenderSystem.color4f(1.0F, 0.5F, 0.5F, alpha);
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }
    
    public static void disableAlpha(float alpha) {
        RenderSystem.disableBlend();
        
        if (alpha == 1f)
            return;
        
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
